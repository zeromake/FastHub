package com.fastaccess.helper

import androidx.preference.PreferenceDataStore

class SettingsDataStore : PreferenceDataStore() {
    override fun putString(key: String, value: String?) {
        PrefHelper.getInstance().putString(key, value)
    }

    override fun getString(key: String, defValue: String?): String? {
        return PrefHelper.getInstance().getString(key, defValue)
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?) {
        PrefHelper.getInstance().putStringSet(key, values)
    }

    override fun putInt(key: String?, value: Int) {
        PrefHelper.getInstance().putInt(key, value)
    }

    override fun putLong(key: String?, value: Long) {
        PrefHelper.getInstance().putLong(key, value)
    }

    override fun putFloat(key: String?, value: Float) {
        PrefHelper.getInstance().putFloat(key, value)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        PrefHelper.getInstance().putBoolean(key, value)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        return PrefHelper.getInstance().getStringSet(key, defValues)
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return PrefHelper.getInstance().getInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return PrefHelper.getInstance().getLong(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return PrefHelper.getInstance().getFloat(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return PrefHelper.getInstance().getBoolean(key, defValue)
    }
}