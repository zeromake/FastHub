package com.fastaccess.helper

import androidx.preference.PreferenceDataStore
import com.tencent.mmkv.MMKV

class SettingsDataStore(key: String) : PreferenceDataStore() {
    private val instance: MMKV = PrefHelper.getInstanceWithKey(key)

    override fun putString(key: String, value: String?) {
        instance.putString(key, value)
    }

    override fun getString(key: String, defValue: String?): String? {
        return instance.getString(key, defValue)
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?) {
        instance.putStringSet(key, values)
    }

    override fun putInt(key: String?, value: Int) {
        instance.putInt(key, value)
    }

    override fun putLong(key: String?, value: Long) {
        instance.putLong(key, value)
    }

    override fun putFloat(key: String?, value: Float) {
        instance.putFloat(key, value)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        instance.putBoolean(key, value)
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        return instance.getStringSet(key, defValues)
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return instance.getInt(key, defValue)
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return instance.getLong(key, defValue)
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return instance.getFloat(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return instance.getBoolean(key, defValue)
    }
}