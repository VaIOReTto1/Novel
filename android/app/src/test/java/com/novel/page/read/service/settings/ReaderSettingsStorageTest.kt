package com.novel.page.read.service.settings

import com.novel.core.storage.StorageFacade
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderSettingsStorageTest {

    @Test
    fun returnsNullForUnsetReaderSettingsKeys() {
        val storage = ReaderSettingsStorage(FakeStorageFacade())

        assertEquals(null, storage.getPageFlipEffect())
        assertEquals(null, storage.getFontSize())
        assertEquals(null, storage.getBrightness())
        assertEquals(null, storage.getBackgroundColor())
        assertEquals(null, storage.getTextColor())
    }

    @Test
    fun persistsAndReadsReaderSettingsThroughStorageFacade() {
        val fakeStorage = FakeStorageFacade()
        val storage = ReaderSettingsStorage(fakeStorage)

        storage.setPageFlipEffect("CURL")
        storage.setFontSize(22)
        storage.setBrightness(0.8f)
        storage.setBackgroundColor("#FFF7E6")
        storage.setTextColor("#111111")

        assertEquals("CURL", storage.getPageFlipEffect())
        assertEquals(22, storage.getFontSize())
        assertEquals(0.8f, storage.getBrightness())
        assertEquals("#FFF7E6", storage.getBackgroundColor())
        assertEquals("#111111", storage.getTextColor())
    }

    private class FakeStorageFacade : StorageFacade {
        private val rawValues = mutableMapOf<String, String>()
        private val enumValues = mutableMapOf<NovelUserDefaultsKey, Any>()

        override fun putString(key: String, value: String) {
            rawValues[key] = value
        }

        override fun getString(key: String): String? = rawValues[key]

        override fun putString(key: NovelUserDefaultsKey, value: String) {
            enumValues[key] = value
        }

        override fun getString(key: NovelUserDefaultsKey): String? = enumValues[key] as? String

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

        override fun putFloat(key: NovelUserDefaultsKey, value: Float) {
            enumValues[key] = value
        }

        override fun getFloat(key: NovelUserDefaultsKey): Float? = enumValues[key] as? Float

        override fun remove(key: NovelUserDefaultsKey) {
            enumValues.remove(key)
        }

        override fun contains(key: NovelUserDefaultsKey): Boolean = enumValues.containsKey(key)
    }
}
