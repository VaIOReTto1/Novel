package com.novel.core.storage

import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import javax.inject.Inject
import javax.inject.Singleton

interface StorageFacade {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun remove(key: String)

    fun putInt(key: NovelUserDefaultsKey, value: Int)
    fun getInt(key: NovelUserDefaultsKey): Int?

    fun putBoolean(key: NovelUserDefaultsKey, value: Boolean)
    fun getBoolean(key: NovelUserDefaultsKey): Boolean?

    fun putLong(key: NovelUserDefaultsKey, value: Long)
    fun getLong(key: NovelUserDefaultsKey): Long?

    fun remove(key: NovelUserDefaultsKey)
    fun contains(key: NovelUserDefaultsKey): Boolean
}

@Singleton
class LegacyStorageFacade @Inject constructor(
    private val userDefaults: NovelUserDefaults
) : StorageFacade {

    override fun putString(key: String, value: String) {
        userDefaults.setString(key, value)
    }

    override fun getString(key: String): String? = userDefaults.getString(key)

    override fun remove(key: String) {
        userDefaults.remove(key)
    }

    override fun putInt(key: NovelUserDefaultsKey, value: Int) {
        userDefaults.set(value, key)
    }

    override fun getInt(key: NovelUserDefaultsKey): Int? = userDefaults.get(key)

    override fun putBoolean(key: NovelUserDefaultsKey, value: Boolean) {
        userDefaults.set(value, key)
    }

    override fun getBoolean(key: NovelUserDefaultsKey): Boolean? = userDefaults.get(key)

    override fun putLong(key: NovelUserDefaultsKey, value: Long) {
        userDefaults.set(value, key)
    }

    override fun getLong(key: NovelUserDefaultsKey): Long? = userDefaults.get(key)

    override fun remove(key: NovelUserDefaultsKey) {
        userDefaults.remove(key)
    }

    override fun contains(key: NovelUserDefaultsKey): Boolean = userDefaults.contains(key)
}
