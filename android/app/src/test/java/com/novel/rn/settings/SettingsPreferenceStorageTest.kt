package com.novel.rn.settings

import com.novel.core.storage.StorageFacade
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsPreferenceStorageTest {

    @Test
    fun returnsDefaultsWhenNoStoredValuesExist() {
        val storage = SettingsPreferenceStorage(FakeStorageFacade())

        assertEquals("auto", storage.getNightMode())
        assertEquals(true, storage.isFollowSystemTheme())
        assertEquals(false, storage.isAutoNightModeEnabled())
        assertEquals("22:00", storage.getNightModeStartTime())
        assertEquals("06:00", storage.getNightModeEndTime())
    }

    @Test
    fun persistsAndReadsSettingsKeysThroughStorageFacade() {
        val fakeStorage = FakeStorageFacade()
        val storage = SettingsPreferenceStorage(fakeStorage)

        storage.setNightMode("dark")
        storage.setFollowSystemTheme(false)
        storage.setAutoNightModeEnabled(true)
        storage.setNightModeTime("21:30", "05:45")

        assertEquals("dark", storage.getNightMode())
        assertEquals(false, storage.isFollowSystemTheme())
        assertEquals(true, storage.isAutoNightModeEnabled())
        assertEquals("21:30", storage.getNightModeStartTime())
        assertEquals("05:45", storage.getNightModeEndTime())
        assertEquals("dark", fakeStorage.rawValues["night_mode"])
    }

    private class FakeStorageFacade : StorageFacade {
        val rawValues = mutableMapOf<String, String>()
        private val enumValues = mutableMapOf<NovelUserDefaultsKey, Any>()

        override fun putString(key: String, value: String) {
            rawValues[key] = value
        }

        override fun getString(key: String): String? = rawValues[key]

        override fun remove(key: String) {
            rawValues.remove(key)
        }

        override fun putInt(key: NovelUserDefaultsKey, value: Int) {
            enumValues[key] = value
        }

        override fun getInt(key: NovelUserDefaultsKey): Int? = enumValues[key] as? Int

        override fun putBoolean(key: NovelUserDefaultsKey, value: Boolean) {
            enumValues[key] = value
        }

        override fun getBoolean(key: NovelUserDefaultsKey): Boolean? = enumValues[key] as? Boolean

        override fun putLong(key: NovelUserDefaultsKey, value: Long) {
            enumValues[key] = value
        }

        override fun getLong(key: NovelUserDefaultsKey): Long? = enumValues[key] as? Long

        override fun remove(key: NovelUserDefaultsKey) {
            enumValues.remove(key)
        }

        override fun contains(key: NovelUserDefaultsKey): Boolean = enumValues.containsKey(key)
    }
}
