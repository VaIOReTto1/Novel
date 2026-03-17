package com.novel.core.storage

import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LegacyStorageFacadeTest {

    @Test
    fun storesAndReadsEnumBackedValues() {
        val userDefaults = FakeNovelUserDefaults()
        val facade = LegacyStorageFacade(userDefaults)

        facade.putInt(NovelUserDefaultsKey.USER_ID, 7)
        facade.putBoolean(NovelUserDefaultsKey.IS_LOGGED_IN, true)

        assertEquals(7, facade.getInt(NovelUserDefaultsKey.USER_ID))
        assertEquals(true, facade.getBoolean(NovelUserDefaultsKey.IS_LOGGED_IN))
        assertTrue(facade.contains(NovelUserDefaultsKey.USER_ID))
    }

    @Test
    fun storesAndReadsRawStringValues() {
        val userDefaults = FakeNovelUserDefaults()
        val facade = LegacyStorageFacade(userDefaults)

        facade.putString("night_mode", "dark")

        assertEquals("dark", facade.getString("night_mode"))
    }

    @Test
    fun removeClearsEnumAndRawKeys() {
        val userDefaults = FakeNovelUserDefaults()
        val facade = LegacyStorageFacade(userDefaults)

        facade.putLong(NovelUserDefaultsKey.TOKEN_EXPIRES_AT, 99L)
        facade.putString("custom_key", "value")
        facade.remove(NovelUserDefaultsKey.TOKEN_EXPIRES_AT)
        facade.remove("custom_key")

        assertEquals(null, facade.getLong(NovelUserDefaultsKey.TOKEN_EXPIRES_AT))
        assertEquals(null, facade.getString("custom_key"))
        assertFalse(facade.contains(NovelUserDefaultsKey.TOKEN_EXPIRES_AT))
    }

    private class FakeNovelUserDefaults : NovelUserDefaults {
        private val enumStore = mutableMapOf<NovelUserDefaultsKey, Any>()
        private val stringStore = mutableMapOf<String, String>()

        override fun <T> set(value: T, forKey: NovelUserDefaultsKey) {
            enumStore[forKey] = value as Any
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(key: NovelUserDefaultsKey): T? = enumStore[key] as? T

        override fun remove(key: NovelUserDefaultsKey) {
            enumStore.remove(key)
        }

        override fun contains(key: NovelUserDefaultsKey): Boolean = enumStore.containsKey(key)

        override fun clearAll() {
            enumStore.clear()
            stringStore.clear()
        }

        override fun setString(key: String, value: String) {
            stringStore[key] = value
        }

        override fun getString(key: String): String? = stringStore[key]

        override fun remove(key: String) {
            stringStore.remove(key)
        }
    }
}
