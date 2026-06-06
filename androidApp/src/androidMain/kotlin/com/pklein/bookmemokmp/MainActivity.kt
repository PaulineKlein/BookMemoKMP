package com.pklein.bookmemokmp

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.JikanType
import com.pklein.bookmemokmp.domain.repository.CollectionRepository
import com.pklein.bookmemokmp.scanner.BarcodeScanner
import com.pklein.bookmemokmp.widget.EXTRA_EDIT_ITEM_ID
import com.pklein.bookmemokmp.widget.FavoritesWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.File

class MainActivity : ComponentActivity() {
    // Backed by Compose state so that onNewIntent recompositions are picked up
    // (handles the case where the app is already in foreground when tapping the widget).
    private var editItemId by mutableStateOf<Long?>(null)

    private val repository: CollectionRepository by inject()

    private val importDbLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { importDatabaseFile(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.auto(
                    lightScrim = android.graphics.Color.TRANSPARENT,
                    darkScrim = android.graphics.Color.TRANSPARENT,
                ),
        )
        super.onCreate(savedInstanceState)
        editItemId = intent.getLongExtra(EXTRA_EDIT_ITEM_ID, -1L).takeIf { it != -1L }
        setContent {
            App(
                startEditItemId = editItemId,
                onExportCsv = ::shareCsvFile,
                onExportDb = ::shareDatabaseFile,
                onImportDb = ::launchImportDb,
                barcodeScanner =
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                        BarcodeScanner(this)
                    } else {
                        null
                    },
            )
        }
        observeFavoritesForWidget()
    }

    private fun observeFavoritesForWidget() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getFavorites().collect {
                    FavoritesWidget().updateAll(this@MainActivity)
                }
            }
        }
    }

    private fun shareCsvFile(content: String) {
        val file = File(cacheDir, "bookmemo_export.csv")
        file.writeText(content)
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun shareDatabaseFile() {
        val dbFile = getDatabasePath("Book.db")
        val exportFile = File(cacheDir, "bookmemo_backup.db")
        dbFile.copyTo(exportFile, overwrite = true)
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", exportFile)
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun launchImportDb() {
        importDbLauncher.launch(arrayOf("*/*"))
    }

    private fun importDatabaseFile(uri: Uri) {
        val tmpFile = File(cacheDir, "bookmemo_import_tmp.db")
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                tmpFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return
        } catch (_: Exception) {
            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val db =
                        SQLiteDatabase.openDatabase(
                            tmpFile.absolutePath,
                            null,
                            SQLiteDatabase.OPEN_READONLY,
                        )
                    db.use {
                        val cursor = it.rawQuery("SELECT * FROM Book", null)
                        cursor.use { c ->
                            fun getStrOrNull(col: String): String? {
                                val idx = c.getColumnIndex(col)
                                return if (idx == -1 || c.isNull(idx)) null else c.getString(idx)
                            }

                            fun getIntOrNull(col: String): Int? {
                                val idx = c.getColumnIndex(col)
                                return if (idx == -1 || c.isNull(idx)) null else c.getInt(idx)
                            }
                            while (c.moveToNext()) {
                                val item =
                                    CollectionItem(
                                        id = getIntOrNull("id")?.toLong() ?: 0L,
                                        type =
                                            ItemType.fromString(
                                                c.getString(c.getColumnIndexOrThrow("type")),
                                            ),
                                        title = c.getString(c.getColumnIndexOrThrow("title")),
                                        author = getStrOrNull("author"),
                                        illustrator = getStrOrNull("illustrator"),
                                        year = getIntOrNull("year"),
                                        bought = c.getInt(c.getColumnIndexOrThrow("bought")) == 1,
                                        wishlist = c.getInt(c.getColumnIndexOrThrow("wishlist")) == 1,
                                        finished = c.getInt(c.getColumnIndexOrThrow("finish")) == 1,
                                        tome = getIntOrNull("tome"),
                                        chapter = getIntOrNull("chapter"),
                                        episode = getIntOrNull("episode"),
                                        season = getIntOrNull("season"),
                                        description = getStrOrNull("desc"),
                                        favorite = c.getInt(c.getColumnIndexOrThrow("favorite")) == 1,
                                        imageUrl = getStrOrNull("image_url"),
                                        isBorrowed = (getIntOrNull("is_borrowed") ?: 0) == 1,
                                        borrowedSince =
                                            run {
                                                val idx = c.getColumnIndex("borrowed_since")
                                                if (idx == -1 || c.isNull(idx)) null else c.getLong(idx)
                                            },
                                        borrowedBy = getStrOrNull("borrowed_by"),
                                        jikanId = getIntOrNull("jikan_id")?.toLong(),
                                        jikanType = JikanType.fromString(getStrOrNull("jikan_type")),
                                        totTome = getIntOrNull("tot_tome"),
                                        totChapter = getIntOrNull("tot_chapter"),
                                        totEpisode = getIntOrNull("tot_episode"),
                                    )
                                repository.add(item)
                            }
                        }
                    }
                } catch (_: Exception) {
                    // not a valid BookMemo database — silently ignore
                } finally {
                    tmpFile.delete()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        editItemId = intent.getLongExtra(EXTRA_EDIT_ITEM_ID, -1L).takeIf { it != -1L }
    }
}
