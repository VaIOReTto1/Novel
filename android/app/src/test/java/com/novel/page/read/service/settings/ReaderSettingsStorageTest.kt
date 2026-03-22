package com.novel.page.read.service.settings

import com.novel.core.config.RefactorFeatureFlags
import com.novel.core.storage.SettingsDataStorePilot
import com.novel.core.storage.StorageFacade
import com.novel.page.read.viewmodel.PageFlipEffect
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.util.UUID

class ReaderSettingsStorageTest {

    @Test
    fun returnsNullForUnsetReaderSettingsKeys() {
        val storage = ReaderSettingsStorage(
            FakeStorageFacade(),
            createPilot("returnsNullForUnsetReaderSettingsKeys"),
            DisabledPilotFlags,
        )

        assertEquals(null, storage.getPageFlipEffect())
        assertEquals(null, storage.getFontSize())
        assertEquals(null, storage.getBrightness())
        assertEquals(null, storage.getBackgroundColor())
        assertEquals(null, storage.getTextColor())
    }

    @Test
    fun persistsAndReadsReaderSettingsThroughStorageFacade() {
        val fakeStorage = FakeStorageFacade()
        val storage = ReaderSettingsStorage(
            fakeStorage,
            createPilot("persistsAndReadsReaderSettingsThroughStorageFacade"),
            DisabledPilotFlags,
        )

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

    @Test
    fun mirrorsReaderSettingsIntoDataStorePilotWhenEnabled() = runBlocking {
        val pilot = createPilot("mirrorsReaderSettingsIntoDataStorePilotWhenEnabled")
        val storage = ReaderSettingsStorage(
            FakeStorageFacade(),
            pilot,
            EnabledPilotFlags,
        )

        storage.mirrorSettings(
            ReaderSettings(
                pageFlipEffect = PageFlipEffect.SLIDE,
                fontSize = 20,
                brightness = 0.75f,
                backgroundColor = Color(0xFFFFF7E6),
                textColor = Color(0xFF111111),
            ),
        )

        repeat(20) {
            val snapshot = pilot.readSnapshot()
            if (
                snapshot.pageFlipEffect == "SLIDE" &&
                snapshot.fontSize == 20 &&
                snapshot.brightness == 0.75f &&
                snapshot.backgroundColor == "#FFF7E6" &&
                snapshot.textColor == "#111111"
            ) {
                return@runBlocking
            }
            delay(20)
        }

        val snapshot = pilot.readSnapshot()
        assertEquals("SLIDE", snapshot.pageFlipEffect)
        assertEquals(20, snapshot.fontSize)
        assertEquals(0.75f, snapshot.brightness)
        assertEquals("#FFFFF7E6", snapshot.backgroundColor)
        assertEquals("#FF111111", snapshot.textColor)
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

    private fun createPilot(testName: String): SettingsDataStorePilot {
        val root = File(System.getProperty("java.io.tmpdir"), "reader-settings-pilot-$testName").apply {
            mkdirs()
            deleteOnExit()
        }
        val dataFileName = "reader-settings-${UUID.randomUUID()}.preferences_pb"
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1)),
            produceFile = { File(root, dataFileName) }
        )
        return SettingsDataStorePilot(dataStore)
    }

    private object EnabledPilotFlags : RefactorFeatureFlags {
        override fun enableBridgeErrorMapper(): Boolean = false
        override fun enableBridgeSharedScopes(): Boolean = false
        override fun enableSettingsDataStorePilot(): Boolean = true
    }

    private object DisabledPilotFlags : RefactorFeatureFlags {
        override fun enableBridgeErrorMapper(): Boolean = false
        override fun enableBridgeSharedScopes(): Boolean = false
        override fun enableSettingsDataStorePilot(): Boolean = false
    }
}
