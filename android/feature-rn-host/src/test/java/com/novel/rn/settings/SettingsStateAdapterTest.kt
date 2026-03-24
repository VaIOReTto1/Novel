package com.novel.rn.settings

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class SettingsStateAdapterTest {

    @Test
    fun getThemeDisplayName_formatsKnownModes() {
        val adapter = SettingsStateAdapter(
            MutableStateFlow(SettingsState(currentThemeMode = "dark", actualTheme = "dark")),
        )

        assertThat(adapter.getThemeDisplayName()).isEqualTo("深色")
        assertThat(adapter.getActualThemeDisplayName()).isEqualTo("深色")
    }

    @Test
    fun canPerformCacheOperation_falseWhenBusy() {
        val adapter = SettingsStateAdapter(
            MutableStateFlow(SettingsState(isCacheCalculating = true)),
        )

        assertThat(adapter.canPerformCacheOperation()).isFalse()
        assertThat(adapter.isCacheOperationInProgress()).isTrue()
    }
}
