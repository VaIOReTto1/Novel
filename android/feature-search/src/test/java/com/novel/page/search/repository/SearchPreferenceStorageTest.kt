package com.novel.page.search.repository

import com.novel.core.storage.StorageFacade
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchPreferenceStorageTest {

    @Test
    fun returnsDefaultsWhenNoStoredValuesExist() {
        val storage = SearchPreferenceStorage(FakeStorageFacade())

        assertEquals(null, storage.getSearchHistoryJson())
        assertEquals(false, storage.getHistoryExpanded())
    }

    @Test
    fun persistsSearchHistoryAndExpansionStateThroughStorageFacade() {
        val fakeStorage = FakeStorageFacade()
        val storage = SearchPreferenceStorage(fakeStorage)

        storage.setSearchHistoryJson("[\"a\",\"b\"]")
        storage.setHistoryExpanded(true)

        assertEquals("[\"a\",\"b\"]", storage.getSearchHistoryJson())
        assertEquals(true, storage.getHistoryExpanded())
        assertEquals("true", fakeStorage.rawValues["history_expanded"])
    }

    @Test
    fun clearRemovesStoredSearchKeys() {
        val fakeStorage = FakeStorageFacade()
        val storage = SearchPreferenceStorage(fakeStorage)

        storage.setSearchHistoryJson("[\"a\"]")
        storage.setHistoryExpanded(true)
        storage.clearSearchHistory()

        assertEquals(null, storage.getSearchHistoryJson())
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
