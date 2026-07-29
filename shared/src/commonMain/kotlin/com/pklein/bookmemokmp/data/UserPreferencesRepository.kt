package com.pklein.bookmemokmp.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class UserPreferencesRepository(private val settings: Settings) {

    var themeMode: ThemeMode
        get() = ThemeMode.entries.getOrElse(settings.getInt(KEY_THEME_MODE, 0)) { ThemeMode.SYSTEM }
        set(value) = settings.putInt(KEY_THEME_MODE, value.ordinal)

    private val _saveEnglishDescription = MutableStateFlow(
        settings.getBoolean(KEY_SAVE_ENGLISH_DESC, defaultValue = true)
    )
    val saveEnglishDescriptionFlow: StateFlow<Boolean> = _saveEnglishDescription.asStateFlow()

    var saveEnglishDescription: Boolean
        get() = _saveEnglishDescription.value
        set(value) {
            settings.putBoolean(KEY_SAVE_ENGLISH_DESC, value)
            _saveEnglishDescription.value = value
        }

    var backupEmail: String?
        get() = settings.getStringOrNull(KEY_BACKUP_EMAIL)
        set(value) = if (value != null) settings.putString(KEY_BACKUP_EMAIL, value)
                     else settings.remove(KEY_BACKUP_EMAIL)

    var lastBackupDate: String?
        get() = settings.getStringOrNull(KEY_LAST_BACKUP)
        set(value) = if (value != null) settings.putString(KEY_LAST_BACKUP, value)
                     else settings.remove(KEY_LAST_BACKUP)

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_SAVE_ENGLISH_DESC = "save_english_description"
        const val KEY_BACKUP_EMAIL = "backup_email"
        const val KEY_LAST_BACKUP = "backup_last_date"
    }
}
