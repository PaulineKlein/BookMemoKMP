package com.pklein.bookmemokmp

import com.russhwolf.settings.Settings

internal class FakeSettings : Settings {
    private val map = mutableMapOf<String, Any>()
    override val keys: Set<String> get() = map.keys
    override val size: Int get() = map.size

    override fun clear() = map.clear()

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun hasKey(key: String) = map.containsKey(key)

    override fun putInt(
        key: String,
        value: Int,
    ) {
        map[key] = value
    }

    override fun getInt(
        key: String,
        defaultValue: Int,
    ) = map[key] as? Int ?: defaultValue

    override fun getIntOrNull(key: String) = map[key] as? Int

    override fun putLong(
        key: String,
        value: Long,
    ) {
        map[key] = value
    }

    override fun getLong(
        key: String,
        defaultValue: Long,
    ) = map[key] as? Long ?: defaultValue

    override fun getLongOrNull(key: String) = map[key] as? Long

    override fun putString(
        key: String,
        value: String,
    ) {
        map[key] = value
    }

    override fun getString(
        key: String,
        defaultValue: String,
    ) = map[key] as? String ?: defaultValue

    override fun getStringOrNull(key: String) = map[key] as? String

    override fun putFloat(
        key: String,
        value: Float,
    ) {
        map[key] = value
    }

    override fun getFloat(
        key: String,
        defaultValue: Float,
    ) = map[key] as? Float ?: defaultValue

    override fun getFloatOrNull(key: String) = map[key] as? Float

    override fun putDouble(
        key: String,
        value: Double,
    ) {
        map[key] = value
    }

    override fun getDouble(
        key: String,
        defaultValue: Double,
    ) = map[key] as? Double ?: defaultValue

    override fun getDoubleOrNull(key: String) = map[key] as? Double

    override fun putBoolean(
        key: String,
        value: Boolean,
    ) {
        map[key] = value
    }

    override fun getBoolean(
        key: String,
        defaultValue: Boolean,
    ) = map[key] as? Boolean ?: defaultValue

    override fun getBooleanOrNull(key: String) = map[key] as? Boolean
}
