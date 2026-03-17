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
    fun writeThenReadFollowSystemTheme() = runBlocking {
        val pilot = createPilot("writeThenReadFollowSystemTheme")

        pilot.writeFollowSystemTheme(true)

        assertEquals(true, pilot.readFollowSystemTheme())
    }

    @Test
    fun migrateIfNeeded_usesLegacyValuesWhenStoreIsEmpty() = runBlocking {
        val pilot = createPilot("migrateIfNeeded_usesLegacyValuesWhenStoreIsEmpty")

        val result = pilot.migrateIfNeeded(
            legacyFollowSystemTheme = "false",
            legacyAutoNightMode = "true"
        )

        assertEquals(false, result.followSystemTheme)
        assertEquals(true, result.autoNightModeEnabled)
        assertEquals(false, pilot.readFollowSystemTheme())
        assertEquals(true, pilot.readAutoNightModeEnabled())
    }

    @Test
    fun migrateIfNeeded_preservesExistingDataStoreValues() = runBlocking {
        val pilot = createPilot("migrateIfNeeded_preservesExistingDataStoreValues")
        pilot.migrateIfNeeded(
            legacyFollowSystemTheme = "true",
            legacyAutoNightMode = "false"
        )

        val result = pilot.migrateIfNeeded(
            legacyFollowSystemTheme = "false",
            legacyAutoNightMode = "true"
        )

        assertEquals(true, result.followSystemTheme)
        assertEquals(false, result.autoNightModeEnabled)
    }

    private fun createPilot(testName: String): SettingsDataStorePilot {
        val root = File(System.getProperty("java.io.tmpdir"), "novel-datastore-$testName").apply {
            mkdirs()
            deleteOnExit()
        }
        val dataFileName = "settings-${UUID.randomUUID()}.preferences_pb"
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { File(root, dataFileName) }
        )
        return SettingsDataStorePilot(dataStore)
    }
}
