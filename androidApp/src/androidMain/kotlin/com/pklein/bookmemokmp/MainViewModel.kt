package com.pklein.bookmemokmp

import android.app.Application
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.FormatType
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.MangaApiType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(
    app: Application,
) : AndroidViewModel(app) {
    var onImportItemsCallback: ((List<CollectionItem>) -> Unit)? = null

    fun shareCsvFile(content: String) {
        val app = getApplication<Application>()
        val file = File(app.cacheDir, "bookmemo_export.csv")
        file.writeText(content)
        val uri = FileProvider.getUriForFile(app, "${app.packageName}.fileprovider", file)
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        app.startActivity(
            Intent.createChooser(intent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun shareDatabaseFile() {
        val app = getApplication<Application>()
        val dbFile = app.getDatabasePath("Book.db")
        val exportFile = File(app.cacheDir, "bookmemo_backup.db")
        dbFile.copyTo(exportFile, overwrite = true)
        val uri = FileProvider.getUriForFile(app, "${app.packageName}.fileprovider", exportFile)
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        app.startActivity(
            Intent.createChooser(intent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun importDatabaseFile(uri: Uri) {
        val app = getApplication<Application>()
        val tmpFile = File(app.cacheDir, "bookmemo_import_tmp.db")
        try {
            app.contentResolver.openInputStream(uri)?.use { input ->
                tmpFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return
        } catch (_: Exception) {
            return
        }

        viewModelScope.launch {
            val items =
                withContext(Dispatchers.IO) {
                    val result = mutableListOf<CollectionItem>()
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
                                    result +=
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
                                            isDigital = (getIntOrNull("is_digital") ?: 0) == 1,
                                            isBorrowed = (getIntOrNull("is_borrowed") ?: 0) == 1,
                                            borrowedSince =
                                                run {
                                                    val idx = c.getColumnIndex("borrowed_since")
                                                    if (idx == -1 || c.isNull(idx)) {
                                                        null
                                                    } else {
                                                        c.getLong(idx)
                                                    }
                                                },
                                            borrowedBy = getStrOrNull("borrowed_by"),
                                            mangaApiId = getIntOrNull("jikan_id")?.toLong(),
                                            mangaApiAuthorId = getIntOrNull("jikan_author_id")?.toLong(),
                                            mangaApiType = MangaApiType.fromString(getStrOrNull("jikan_type")),
                                            totTome = getIntOrNull("tot_tome"),
                                            totChapter = getIntOrNull("tot_chapter"),
                                            totEpisode = getIntOrNull("tot_episode"),
                                            checkedTomes =
                                                getStrOrNull("checked_tomes")
                                                    ?.split(",")
                                                    ?.mapNotNull { n -> n.trim().toIntOrNull() }
                                                    ?: emptyList(),
                                            notes = getStrOrNull("notes"),
                                            format = FormatType.fromString(getStrOrNull("format")),
                                        )
                                }
                            }
                        }
                    } catch (_: Exception) {
                        // not a valid BookMemo database — silently ignore
                    } finally {
                        tmpFile.delete()
                    }
                    result
                }
            if (items.isNotEmpty()) onImportItemsCallback?.invoke(items)
        }
    }
}
