package com.pklein.bookmemokmp.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pklein.bookmemokmp.data.ThemeMode
import com.pklein.bookmemokmp.data.UserPreferencesRepository
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.repository.IBackupRepository
import com.pklein.bookmemokmp.domain.repository.ICollectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

enum class RestoreStrategy { REPLACE, DUPLICATE, SKIP }

sealed interface BackupNotification {
    data object SignInError : BackupNotification

    data object BackupError : BackupNotification

    data object RestoreError : BackupNotification

    data object BackupSuccess : BackupNotification

    data object RestoreSuccess : BackupNotification
}

data class RestoreConflict(
    val duplicateCount: Int,
    val pendingItems: List<CollectionItem>,
)

class SettingsViewModel(
    private val backupService: IBackupRepository,
    private val repository: ICollectionRepository,
    private val userPrefs: UserPreferencesRepository,
) : ViewModel() {
    private val _themeMode = MutableStateFlow(userPrefs.themeMode)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(value: ThemeMode) {
        userPrefs.themeMode = value
        _themeMode.value = value
    }

    private val _saveEnglishDescription = MutableStateFlow(userPrefs.saveEnglishDescription)
    val saveEnglishDescription: StateFlow<Boolean> = _saveEnglishDescription.asStateFlow()

    fun setSaveEnglishDescription(value: Boolean) {
        userPrefs.saveEnglishDescription = value
        _saveEnglishDescription.value = value
    }

    private val _email = MutableStateFlow(userPrefs.backupEmail)
    val email: StateFlow<String?> = _email.asStateFlow()

    private val currentUserEmail: String? get() = backupService.getCurrentUserEmail()

    private val _lastBackupDate = MutableStateFlow(userPrefs.lastBackupDate)
    val lastBackupDate: StateFlow<String?> = _lastBackupDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _notification = MutableStateFlow<BackupNotification?>(null)
    val notification: StateFlow<BackupNotification?> = _notification.asStateFlow()

    fun clearNotification() {
        _notification.value = null
    }

    fun signInFailed() {
        _notification.value = BackupNotification.SignInError
    }

    private val _restoreConflict = MutableStateFlow<RestoreConflict?>(null)
    val restoreConflict: StateFlow<RestoreConflict?> = _restoreConflict.asStateFlow()

    private val _importConflict = MutableStateFlow<RestoreConflict?>(null)
    val importConflict: StateFlow<RestoreConflict?> = _importConflict.asStateFlow()

    fun signInCompleted() {
        val email = currentUserEmail ?: return
        userPrefs.backupEmail = email
        _email.value = email
        viewModelScope.launch {
            val date = backupService.fetchLastBackupDate()
            if (date != null) {
                userPrefs.lastBackupDate = date
                _lastBackupDate.value = date
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            backupService.signOut()
            userPrefs.backupEmail = null
            _email.value = null
            userPrefs.lastBackupDate = null
            _lastBackupDate.value = null
        }
    }

    fun backupNow() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = repository.getAll().first()
                backupService.backup(items)
                val date = currentFormattedDate()
                userPrefs.lastBackupDate = date
                _lastBackupDate.value = date
                _notification.value = BackupNotification.BackupSuccess
            } catch (_: Exception) {
                _notification.value = BackupNotification.BackupError
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                checkAndInsert(backupService.restore(), _restoreConflict)
            } catch (_: Exception) {
                _notification.value = BackupNotification.RestoreError
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyRestore(strategy: RestoreStrategy) {
        applyConflict(_restoreConflict, strategy)
    }

    fun dismissRestoreConflict() {
        _restoreConflict.value = null
    }

    fun importItems(incoming: List<CollectionItem>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                checkAndInsert(incoming, _importConflict)
            } catch (_: Exception) {
                _notification.value = BackupNotification.RestoreError
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyImport(strategy: RestoreStrategy) {
        applyConflict(_importConflict, strategy)
    }

    fun dismissImportConflict() {
        _importConflict.value = null
    }

    private suspend fun checkAndInsert(
        incoming: List<CollectionItem>,
        conflictState: MutableStateFlow<RestoreConflict?>,
    ) {
        val duplicates =
            incoming.filter {
                repository.existsByTitleAndType(it.title, it.type, excludeId = 0L)
            }
        if (duplicates.isEmpty()) {
            incoming.forEach { repository.add(it) }
            _notification.value = BackupNotification.RestoreSuccess
        } else {
            conflictState.value =
                RestoreConflict(
                    duplicateCount = duplicates.size,
                    pendingItems = incoming,
                )
        }
    }

    private fun applyConflict(
        conflictState: MutableStateFlow<RestoreConflict?>,
        strategy: RestoreStrategy,
    ) {
        val conflict = conflictState.value ?: return
        conflictState.value = null
        viewModelScope.launch {
            _isLoading.value = true
            try {
                conflict.pendingItems.forEach { remote ->
                    val isDuplicate =
                        repository.existsByTitleAndType(remote.title, remote.type, excludeId = 0L)
                    when {
                        !isDuplicate || strategy == RestoreStrategy.DUPLICATE -> {
                            repository.add(remote)
                        }

                        strategy == RestoreStrategy.SKIP -> {
                            Unit
                        }

                        strategy == RestoreStrategy.REPLACE -> {
                            val existing =
                                repository
                                    .getAll()
                                    .first()
                                    .find { it.title == remote.title && it.type == remote.type }
                            if (existing != null) repository.update(remote.copy(id = existing.id))
                        }
                    }
                }
                _notification.value = BackupNotification.RestoreSuccess
            } catch (_: Exception) {
                _notification.value = BackupNotification.RestoreError
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun currentFormattedDate(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val month =
            now.month.name
                .lowercase()
                .replaceFirstChar { it.uppercase() }
                .take(3)
        val h = now.hour.toString().padStart(2, '0')
        val m = now.minute.toString().padStart(2, '0')
        return "${now.day} $month ${now.year} at $h:$m"
    }
}
