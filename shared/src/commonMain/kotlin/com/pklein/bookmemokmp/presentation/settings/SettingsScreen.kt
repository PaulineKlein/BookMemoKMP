package com.pklein.bookmemokmp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bookmemokmp.shared.generated.resources.Res
import bookmemokmp.shared.generated.resources.export_csv
import bookmemokmp.shared.generated.resources.export_database
import bookmemokmp.shared.generated.resources.go_back_accessibility
import bookmemokmp.shared.generated.resources.import_database
import bookmemokmp.shared.generated.resources.settings_cloud_backup
import bookmemokmp.shared.generated.resources.settings_error_backup
import bookmemokmp.shared.generated.resources.settings_error_delete_cloud
import bookmemokmp.shared.generated.resources.settings_error_restore
import bookmemokmp.shared.generated.resources.settings_error_sign_in
import bookmemokmp.shared.generated.resources.settings_local_data
import bookmemokmp.shared.generated.resources.settings_preferences
import bookmemokmp.shared.generated.resources.settings_save_english_description
import bookmemokmp.shared.generated.resources.settings_success_backup
import bookmemokmp.shared.generated.resources.settings_success_delete_cloud
import bookmemokmp.shared.generated.resources.settings_success_restore
import bookmemokmp.shared.generated.resources.settings_theme
import bookmemokmp.shared.generated.resources.settings_theme_dark
import bookmemokmp.shared.generated.resources.settings_theme_light
import bookmemokmp.shared.generated.resources.settings_theme_system
import bookmemokmp.shared.generated.resources.settings_title
import com.pklein.bookmemokmp.appVersion
import com.pklein.bookmemokmp.data.ThemeMode
import com.pklein.bookmemokmp.isAndroidPlatform
import com.pklein.bookmemokmp.presentation.additem.ToggleRowItem
import com.pklein.bookmemokmp.presentation.settings.viewmodel.BackupNotification
import com.pklein.bookmemokmp.presentation.settings.viewmodel.RestoreStrategy
import com.pklein.bookmemokmp.presentation.settings.viewmodel.SettingsViewModel
import com.pklein.bookmemokmp.ui.theme.BookMemoTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit = {},
    onSignIn: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    onExportDb: () -> Unit = {},
    onImportDb: () -> Unit = {},
) {
    val email by viewModel.email.collectAsState()
    val backupDate by viewModel.lastBackupDate.collectAsState()
    val isBackupInProgress by viewModel.isLoading.collectAsState()
    val restoreConflict by viewModel.restoreConflict.collectAsState()
    val importConflict by viewModel.importConflict.collectAsState()
    val notification by viewModel.notification.collectAsState()
    val saveEnglishDescription by viewModel.saveEnglishDescription.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorSignIn = stringResource(Res.string.settings_error_sign_in)
    val errorBackup = stringResource(Res.string.settings_error_backup)
    val errorRestore = stringResource(Res.string.settings_error_restore)
    val errorDeleteCloud = stringResource(Res.string.settings_error_delete_cloud)
    val successBackup = stringResource(Res.string.settings_success_backup)
    val successRestore = stringResource(Res.string.settings_success_restore)
    val successDeleteCloud = stringResource(Res.string.settings_success_delete_cloud)

    LaunchedEffect(notification) {
        val msg =
            when (notification) {
                BackupNotification.SignInError -> errorSignIn
                BackupNotification.BackupError -> errorBackup
                BackupNotification.RestoreError -> errorRestore
                BackupNotification.DeleteError -> errorDeleteCloud
                BackupNotification.BackupSuccess -> successBackup
                BackupNotification.RestoreSuccess -> successRestore
                BackupNotification.DeleteSuccess -> successDeleteCloud
                null -> return@LaunchedEffect
            }
        snackbarHostState.showSnackbar(msg)
        viewModel.clearNotification()
    }

    SettingsContent(
        email = email,
        backupDate = backupDate,
        isBackupInProgress = isBackupInProgress,
        restoreConflict = restoreConflict,
        snackbarHostState = snackbarHostState,
        saveEnglishDescription = saveEnglishDescription,
        onBack = onBack,
        onSignIn = onSignIn,
        onSignOut = viewModel::signOut,
        onBackupNow = viewModel::backupNow,
        onRestore = viewModel::restore,
        onDeleteCloudData = viewModel::deleteCloudBackup,
        onApplyRestore = viewModel::applyRestore,
        onDismissConflict = viewModel::dismissRestoreConflict,
        importConflict = importConflict,
        onApplyImport = viewModel::applyImport,
        onDismissImportConflict = viewModel::dismissImportConflict,
        onSaveEnglishDescriptionChange = viewModel::setSaveEnglishDescription,
        themeMode = themeMode,
        onThemeModeChange = viewModel::setThemeMode,
        onExportCsv = onExportCsv,
        onExportDb = onExportDb,
        onImportDb = onImportDb,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    email: String?,
    backupDate: String?,
    isBackupInProgress: Boolean,
    restoreConflict: com.pklein.bookmemokmp.presentation.settings.viewmodel.RestoreConflict?,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    saveEnglishDescription: Boolean,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onBackupNow: () -> Unit,
    onRestore: () -> Unit,
    onDeleteCloudData: () -> Unit,
    onApplyRestore: (RestoreStrategy) -> Unit,
    onDismissConflict: () -> Unit,
    importConflict: com.pklein.bookmemokmp.presentation.settings.viewmodel.RestoreConflict?,
    onApplyImport: (RestoreStrategy) -> Unit,
    onDismissImportConflict: () -> Unit,
    onSaveEnglishDescriptionChange: (Boolean) -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onExportCsv: () -> Unit,
    onExportDb: () -> Unit,
    onImportDb: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back_accessibility),
                        )
                    }
                },
                actions = {
                    appVersion()?.let { version ->
                        Text(
                            text = "v$version",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(end = 16.dp),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (restoreConflict != null) {
            RestoreConflictDialog(
                conflict = restoreConflict,
                onApplyRestore = onApplyRestore,
                onDismiss = onDismissConflict,
            )
        }
        if (importConflict != null) {
            RestoreConflictDialog(
                conflict = importConflict,
                onApplyRestore = onApplyImport,
                onDismiss = onDismissImportConflict,
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // ── Preferences section ────────────────────────────────────────────
            Text(
                text = stringResource(Res.string.settings_preferences),
                style = MaterialTheme.typography.headlineSmall,
            )

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                val themeOptions =
                    listOf(
                        ThemeMode.SYSTEM to stringResource(Res.string.settings_theme_system),
                        ThemeMode.LIGHT to stringResource(Res.string.settings_theme_light),
                        ThemeMode.DARK to stringResource(Res.string.settings_theme_dark),
                    )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.settings_theme),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(0.25f),
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.weight(1f)) {
                        themeOptions.forEachIndexed { index, (mode, label) ->
                            SegmentedButton(
                                selected = themeMode == mode,
                                onClick = { onThemeModeChange(mode) },
                                shape =
                                    SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = themeOptions.size,
                                    ),
                                icon = {},
                                label = { Text(label) },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ToggleRowItem(
                    label = stringResource(Res.string.settings_save_english_description),
                    checked = saveEnglishDescription,
                    onCheckedChange = onSaveEnglishDescriptionChange,
                )
            }

            // ── Local and cloud data section (Android only) ─────────────────────────────
            if (isAndroidPlatform) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.settings_local_data),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                    OutlinedButton(
                        onClick = onExportCsv,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20),
                    ) {
                        Text(stringResource(Res.string.export_csv))
                    }
                    OutlinedButton(
                        onClick = onExportDb,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20),
                    ) {
                        Text(stringResource(Res.string.export_database))
                    }
                    OutlinedButton(
                        onClick = onImportDb,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20),
                    ) {
                        Text(stringResource(Res.string.import_database))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.settings_cloud_backup),
                    style = MaterialTheme.typography.headlineSmall,
                )

                CloudBackupCard(
                    email = email,
                    backupDate = backupDate,
                    isBackupInProgress = isBackupInProgress,
                    onSignIn = onSignIn,
                    onSignOut = onSignOut,
                    onBackupNow = onBackupNow,
                    onRestore = onRestore,
                    onDeleteCloudData = onDeleteCloudData,
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun PreviewSettingsNotSignedIn() {
    BookMemoTheme {
        SettingsContent(
            email = null,
            backupDate = null,
            isBackupInProgress = false,
            restoreConflict = null,
            saveEnglishDescription = true,
            onBack = {},
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
            onDeleteCloudData = {},
            onApplyRestore = {},
            onDismissConflict = {},
            importConflict = null,
            onApplyImport = {},
            onDismissImportConflict = {},
            onSaveEnglishDescriptionChange = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
        )
    }
}

@Preview(fontScale = 2.0f)
@Composable
private fun PreviewSettingsNotSignedInBigFont() {
    BookMemoTheme {
        SettingsContent(
            email = null,
            backupDate = null,
            isBackupInProgress = false,
            restoreConflict = null,
            saveEnglishDescription = true,
            onBack = {},
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
            onDeleteCloudData = {},
            onApplyRestore = {},
            onDismissConflict = {},
            importConflict = null,
            onApplyImport = {},
            onDismissImportConflict = {},
            onSaveEnglishDescriptionChange = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsSignedInNoBackup() {
    BookMemoTheme {
        SettingsContent(
            email = "test@gmail.com",
            backupDate = null,
            isBackupInProgress = false,
            restoreConflict = null,
            saveEnglishDescription = true,
            onBack = {},
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
            onDeleteCloudData = {},
            onApplyRestore = {},
            onDismissConflict = {},
            importConflict = null,
            onApplyImport = {},
            onDismissImportConflict = {},
            onSaveEnglishDescriptionChange = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsSignedInWithBackup() {
    BookMemoTheme {
        SettingsContent(
            email = "test@gmail.com",
            backupDate = "22 Jul 2026 at 14:32",
            isBackupInProgress = false,
            restoreConflict = null,
            saveEnglishDescription = true,
            onBack = {},
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
            onDeleteCloudData = {},
            onApplyRestore = {},
            onDismissConflict = {},
            importConflict = null,
            onApplyImport = {},
            onDismissImportConflict = {},
            onSaveEnglishDescriptionChange = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
        )
    }
}

@Preview(fontScale = 2.0f)
@Composable
private fun PreviewSettingsSignedInWithBackupBigFont() {
    BookMemoTheme {
        SettingsContent(
            email = "test@gmail.com",
            backupDate = "22 Jul 2026 at 14:32",
            isBackupInProgress = false,
            restoreConflict = null,
            saveEnglishDescription = false,
            onBack = {},
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
            onDeleteCloudData = {},
            onApplyRestore = {},
            onDismissConflict = {},
            importConflict = null,
            onApplyImport = {},
            onDismissImportConflict = {},
            onSaveEnglishDescriptionChange = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsBackupInProgress() {
    BookMemoTheme {
        SettingsContent(
            email = "test@gmail.com",
            backupDate = "22 Jul 2026 at 14:32",
            isBackupInProgress = true,
            restoreConflict = null,
            saveEnglishDescription = true,
            onBack = {},
            onSignIn = {},
            onSignOut = {},
            onBackupNow = {},
            onRestore = {},
            onDeleteCloudData = {},
            onApplyRestore = {},
            onDismissConflict = {},
            importConflict = null,
            onApplyImport = {},
            onDismissImportConflict = {},
            onSaveEnglishDescriptionChange = {},
            themeMode = ThemeMode.SYSTEM,
            onThemeModeChange = {},
            onExportCsv = {},
            onExportDb = {},
            onImportDb = {},
        )
    }
}
