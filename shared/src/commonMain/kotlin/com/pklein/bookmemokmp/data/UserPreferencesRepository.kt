package com.pklein.bookmemokmp.data

import com.russhwolf.settings.Settings

class UserPreferencesRepository(private val settings: Settings) {

    var saveEnglishDescription: Boolean
        get() = settings.getBoolean(KEY_SAVE_ENGLISH_DESC, defaultValue = true)
        set(value) = settings.putBoolean(KEY_SAVE_ENGLISH_DESC, value)

    private companion object {
        const val KEY_SAVE_ENGLISH_DESC = "save_english_description"
    }
}
