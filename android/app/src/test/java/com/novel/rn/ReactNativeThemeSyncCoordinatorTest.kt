package com.novel.rn

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReactNativeThemeSyncCoordinatorTest {

    private val coordinator = ReactNativeThemeSyncCoordinator()

    @Test
    fun syncActualTheme_skipsWhenThemeMissing() {
        var emittedTheme: String? = null

        val synced = coordinator.syncActualTheme(
            actualTheme = null,
            notifyThemeChanged = { emittedTheme = it },
        )

        assertThat(synced).isEqualTo(false)
        assertThat(emittedTheme).isNull()
    }

    @Test
    fun syncActualTheme_skipsWhenThemeBlank() {
        var emittedTheme: String? = null

        val synced = coordinator.syncActualTheme(
            actualTheme = "   ",
            notifyThemeChanged = { emittedTheme = it },
        )

        assertThat(synced).isEqualTo(false)
        assertThat(emittedTheme).isNull()
    }

    @Test
    fun syncActualTheme_notifiesWhenThemeAvailable() {
        var emittedTheme: String? = null

        val synced = coordinator.syncActualTheme(
            actualTheme = "dark",
            notifyThemeChanged = { emittedTheme = it },
        )

        assertThat(synced).isEqualTo(true)
        assertThat(emittedTheme).isEqualTo("dark")
    }
}
