package com.novel.core.storage

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.util.UUID

class SettingsDataStorePilotTest {

    @Test
    fun migrateIfNeeded_usesLegacyValuesWhenStoreIsEmpty() = runBlocking {
        val pilot = createPilot("migrateIfNeeded_usesLegacyValuesWhenStoreIsEmpty")

        val result = pilot.migrateIfNeeded(
            legacySnapshot = SettingsDataStoreSnapshot(
                nightMode = "dark",
                followSystemTheme = false,
                autoNightModeEnabled = true,
                nightStartTime = "21:00",
                nightEndTime = "06:30",
                pageFlipEffect = "SLIDE",
                fontSize = 18,
                brightness = 0.8f,
                backgroundColor = "#FFF7E6",
                textColor = "#111111",
                isLoggedIn = true,
                tokenExpiresAt = 12345L,
                userId = 7,
                newsType = 2,
            ),
        )

        assertEquals("dark", result.nightMode)
        assertEquals(false, result.followSystemTheme)
        assertEquals(true, result.autoNightModeEnabled)
        assertEquals("21:00", result.nightStartTime)
        assertEquals("06:30", result.nightEndTime)
        assertEquals("SLIDE", result.pageFlipEffect)
        assertEquals(18, result.fontSize)
        assertEquals(0.8f, result.brightness)
        assertEquals("#FFF7E6", result.backgroundColor)
        assertEquals("#111111", result.textColor)
        assertEquals(true, result.isLoggedIn)
        assertEquals(12345L, result.tokenExpiresAt)
        assertEquals(7, result.userId)
        assertEquals(2, result.newsType)
    }

    @Test
    fun migrateIfNeeded_preservesExistingDataStoreValues() = runBlocking {
        val pilot = createPilot("migrateIfNeeded_preservesExistingDataStoreValues")
        pilot.mirror(
            SettingsDataStoreSnapshot(
                nightMode = "light",
                followSystemTheme = true,
                autoNightModeEnabled = false,
                fontSize = 16,
                userId = 99,
            ),
        )

        val result = pilot.migrateIfNeeded(
            legacySnapshot = SettingsDataStoreSnapshot(
                nightMode = "dark",
                followSystemTheme = false,
                autoNightModeEnabled = true,
                fontSize = 20,
                userId = 7,
            ),
        )

        assertEquals("light", result.nightMode)
        assertEquals(true, result.followSystemTheme)
        assertEquals(false, result.autoNightModeEnabled)
        assertEquals(16, result.fontSize)
        assertEquals(99, result.userId)
    }

    @Test
    fun mirror_updatesReaderAndUserFields() = runBlocking {
        val pilot = createPilot("mirror_updatesReaderAndUserFields")

        pilot.mirror(
            SettingsDataStoreSnapshot(
                pageFlipEffect = "PAGECURL",
                fontSize = 22,
                brightness = 0.6f,
                backgroundColor = "#FFF5F5DC",
                textColor = "#FF2E2E2E",
                isLoggedIn = true,
                tokenExpiresAt = 9999L,
                userId = 42,
            ),
        )

        val result = pilot.readSnapshot()

        assertEquals("PAGECURL", result.pageFlipEffect)
        assertEquals(22, result.fontSize)
        assertEquals(0.6f, result.brightness)
        assertEquals("#FFF5F5DC", result.backgroundColor)
        assertEquals("#FF2E2E2E", result.textColor)
        assertEquals(true, result.isLoggedIn)
        assertEquals(9999L, result.tokenExpiresAt)
        assertEquals(42, result.userId)
    }

    private fun createPilot(testName: String): SettingsDataStorePilot {
        val root = File(System.getProperty("java.io.tmpdir"), "novel-datastore-$testName").apply {
            mkdirs()
            deleteOnExit()
        }
        val dataFileName = "settings-${UUID.randomUUID()}.preferences_pb"
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1)),
            produceFile = { File(root, dataFileName) }
        )
        return SettingsDataStorePilot(dataStore)
    }
}
