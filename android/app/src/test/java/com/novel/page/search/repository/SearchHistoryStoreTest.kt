package com.novel.page.search.repository

import com.google.gson.Gson
import com.novel.core.storage.StorageFacade
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test

class SearchHistoryStoreTest {

    @Test
    fun returnsStoredHistoryAndCollapsedStateByDefault() {
        val storage = SearchHistoryStore(
            searchPreferenceStorage = SearchPreferenceStorage(FakeStorageFacade()),
            gson = Gson()
        )

        assertEquals(emptyList<String>(), storage.getSearchHistory())
        assertFalse(storage.getHistoryExpansionState())
    }

    @Test
    fun addSearchHistoryKeepsNewestFirstDeduplicatesAndCapsAtTen() {
        val storage = SearchHistoryStore(
            searchPreferenceStorage = SearchPreferenceStorage(FakeStorageFacade()),
            gson = Gson()
        )

        (1..10).forEach { index ->
            storage.addSearchHistory("keyword-$index")
        }
        storage.addSearchHistory("keyword-4")
        storage.addSearchHistory("keyword-11")

        assertEquals(
            listOf(
                "keyword-11",
                "keyword-4",
                "keyword-10",
                "keyword-9",
                "keyword-8",
                "keyword-7",
                "keyword-6",
                "keyword-5",
                "keyword-3",
                "keyword-2"
            ),
            storage.getSearchHistory()
        )
    }

    @Test
    fun returnsEmptyHistoryWhenStoredJsonIsCorrupted() {
        val fakeStorage = FakeStorageFacade().apply {
            rawValues[SearchPreferenceStorage.SEARCH_HISTORY_KEY] = "{not-valid-json"
        }
        val storage = SearchHistoryStore(
            searchPreferenceStorage = SearchPreferenceStorage(fakeStorage),
            gson = Gson()
        )

        assertEquals(emptyList<String>(), storage.getSearchHistory())
    }

    @Test
    fun persistsAndClearsHistoryExpansionState() {
        val fakeStorage = FakeStorageFacade()
        val storage = SearchHistoryStore(
            searchPreferenceStorage = SearchPreferenceStorage(fakeStorage),
            gson = Gson()
        )

        storage.addSearchHistory("alpha")
        storage.saveHistoryExpansionState(true)
        storage.clearSearchHistory()

        assertEquals(emptyList<String>(), storage.getSearchHistory())
        assertEquals("true", fakeStorage.rawValues[SearchPreferenceStorage.HISTORY_EXPANDED_KEY])
        assertEquals(true, storage.getHistoryExpansionState())
    }

    @Test
    fun swallowsStorageFailuresAndReturnsSafeDefaults() {
        val storage = SearchHistoryStore(
            searchPreferenceStorage = SearchPreferenceStorage(ThrowingStorageFacade()),
            gson = Gson()
        )

        try {
            assertEquals(emptyList<String>(), storage.getSearchHistory())
            assertFalse(storage.getHistoryExpansionState())
            storage.addSearchHistory("alpha")
            storage.saveHistoryExpansionState(true)
            storage.clearSearchHistory()
        } catch (error: Throwable) {
            fail("SearchHistoryStore should swallow storage failures, but threw: $error")
        }
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

    private class ThrowingStorageFacade : StorageFacade {
        override fun putString(key: String, value: String) {
            error("putString failed for $key")
        }

        override fun getString(key: String): String? {
            error("getString failed for $key")
        }

        override fun remove(key: String) {
            error("remove failed for $key")
        }

        override fun putInt(key: NovelUserDefaultsKey, value: Int) {
            error("unused")
        }

        override fun getInt(key: NovelUserDefaultsKey): Int? = error("unused")

        override fun putBoolean(key: NovelUserDefaultsKey, value: Boolean) {
            error("unused")
        }

        override fun getBoolean(key: NovelUserDefaultsKey): Boolean? = error("unused")

        override fun putLong(key: NovelUserDefaultsKey, value: Long) {
            error("unused")
        }

        override fun getLong(key: NovelUserDefaultsKey): Long? = error("unused")

        override fun remove(key: NovelUserDefaultsKey) {
            error("unused")
        }

        override fun contains(key: NovelUserDefaultsKey): Boolean = error("unused")
    }
}
