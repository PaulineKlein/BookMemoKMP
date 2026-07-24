package com.pklein.bookmemokmp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pklein.bookmemokmp.domain.repository.IBackupRepository
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository
import com.pklein.bookmemokmp.scanner.BarcodeScanner
import com.pklein.bookmemokmp.widget.EXTRA_EDIT_ITEM_ID
import com.pklein.bookmemokmp.widget.FavoritesWidget
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private var editItemId by mutableStateOf<Long?>(null)
    private val repository: ICollectionRepository by inject()
    private val backupService: IBackupRepository by inject()
    private val mainViewModel: MainViewModel by viewModels()
    private val importDbLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { mainViewModel.importDatabaseFile(it) }
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
                onLaunchSignIn = { onSuccess, onError -> launchGoogleSignIn(onSuccess, onError) },
                onExportCsv = mainViewModel::shareCsvFile,
                onExportDb = mainViewModel::shareDatabaseFile,
                onImportDb = { importDbLauncher.launch(arrayOf("*/*")) },
                onImportItems = { callback -> mainViewModel.onImportItemsCallback = callback },
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

    private fun launchGoogleSignIn(
        onSuccess: () -> Unit,
        onError: () -> Unit,
    ) {
        lifecycleScope.launch {
            try {
                backupService.signIn(getString(R.string.default_web_client_id))
                onSuccess()
            } catch (_: Exception) {
                onError()
            }
        }
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        editItemId = intent.getLongExtra(EXTRA_EDIT_ITEM_ID, -1L).takeIf { it != -1L }
    }
}
