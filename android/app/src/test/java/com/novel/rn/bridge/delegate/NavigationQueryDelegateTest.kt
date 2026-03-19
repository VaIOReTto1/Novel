package com.novel.rn.bridge.delegate

import com.novel.rn.bridge.BridgeState
import com.novel.rn.settings.SettingsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationQueryDelegateTest {

    @Test
    fun getBridgeStatus_returnsSnapshotFromBridgeState() {
        val delegate = NavigationQueryDelegate()
        val snapshot = delegate.getBridgeStatus(
            BridgeState(
                isBridgeInitialized = true,
                currentRoute = "settings",
                isLoading = false
            )
        )

        assertTrue(snapshot.isInitialized)
        assertEquals("settings", snapshot.currentRoute)
        assertEquals(0, snapshot.cachedComponentsCount)
        assertFalse(snapshot.isLoading)
    }

    @Test
    fun getBridgeStatus_returnsDefaultSnapshotWhenStateMissing() {
        val delegate = NavigationQueryDelegate()

        val snapshot = delegate.getBridgeStatus(null)

        assertFalse(snapshot.isInitialized)
        assertNull(snapshot.currentRoute)
        assertEquals(0, snapshot.cachedComponentsCount)
        assertFalse(snapshot.isLoading)
    }

    @Test
    fun getCurrentActualTheme_returnsThemeFromSettingsState() {
        val delegate = NavigationQueryDelegate()

        val result = delegate.getCurrentActualTheme(
            SettingsState(actualTheme = "dark")
        )

        assertEquals("dark", result)
    }

    @Test
    fun getCurrentNightMode_returnsCurrentThemeModeFromSettingsState() {
        val delegate = NavigationQueryDelegate()

        val result = delegate.getCurrentNightMode(
            SettingsState(currentThemeMode = "auto")
        )

        assertEquals("auto", result)
    }

    @Test
    fun getCurrentActualTheme_returnsNullWhenStateMissing() {
        val delegate = NavigationQueryDelegate()

        assertNull(delegate.getCurrentActualTheme(null))
    }
}
