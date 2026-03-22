package com.novel.core.storage

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsDataStoreMirrorCoordinatorTest {

    private val coordinator = SettingsDataStoreMirrorCoordinator()

    @Test
    fun createSettingsSnapshot_containsSettingsFields() {
        val snapshot = coordinator.createSettingsSnapshot(
            nightMode = "dark",
            followSystemTheme = false,
            autoNightModeEnabled = true,
            nightStartTime = "21:00",
            nightEndTime = "06:00",
        )

        assertEquals("dark", snapshot.nightMode)
        assertEquals(false, snapshot.followSystemTheme)
        assertEquals(true, snapshot.autoNightModeEnabled)
        assertEquals("21:00", snapshot.nightStartTime)
        assertEquals("06:00", snapshot.nightEndTime)
    }

    @Test
    fun createReaderSnapshot_containsReaderFields() {
        val snapshot = coordinator.createReaderSnapshot(
            pageFlipEffect = "SLIDE",
            fontSize = 18,
            brightness = 0.7f,
            backgroundColor = "#FFF7E6",
            textColor = "#111111",
        )

        assertEquals("SLIDE", snapshot.pageFlipEffect)
        assertEquals(18, snapshot.fontSize)
        assertEquals(0.7f, snapshot.brightness)
        assertEquals("#FFF7E6", snapshot.backgroundColor)
        assertEquals("#111111", snapshot.textColor)
    }

    @Test
    fun mirrorIfEnabled_andClearUserStateIfEnabled_driveCallbacks() = runBlocking {
        var mirrored: SettingsDataStoreSnapshot? = null
        var cleared = false

        coordinator.mirrorIfEnabled(
            isEnabled = true,
            snapshot = coordinator.createUserSessionSnapshot(
                isLoggedIn = true,
                tokenExpiresAt = 123L,
                userId = 7,
                newsType = 2,
            ),
        ) {
            mirrored = it
        }

        coordinator.clearUserStateIfEnabled(
            isEnabled = true,
        ) {
            cleared = true
        }

        assertEquals(true, mirrored?.isLoggedIn)
        assertEquals(123L, mirrored?.tokenExpiresAt)
        assertEquals(7, mirrored?.userId)
        assertEquals(2, mirrored?.newsType)
        assertTrue(cleared)
    }
}
