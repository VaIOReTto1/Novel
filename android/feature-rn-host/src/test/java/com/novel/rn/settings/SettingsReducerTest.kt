package com.novel.rn.settings

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SettingsReducerTest {

    private val reducer = SettingsReducer()

    @Test
    fun reduce_setNightMode_updatesStateAndToast() {
        val result = reducer.reduce(SettingsState(), SettingsIntent.SetNightMode("dark"))

        assertThat(result.newState.currentThemeMode).isEqualTo("dark")
        assertThat(result.effect).isEqualTo(SettingsEffect.ShowToast("主题已切换到: dark"))
    }

    @Test
    fun handleAsyncResult_cacheCalculated_updatesCacheState() {
        val result = reducer.handleAsyncResult(
            SettingsState(isCacheCalculating = true),
            SettingsAsyncResult.CacheSizeCalculated("12 MB"),
        )

        assertThat(result.newState.isCacheCalculating).isFalse()
        assertThat(result.newState.cacheSize).isEqualTo("12 MB")
        assertThat(result.effect).isEqualTo(SettingsEffect.CacheCalculated("12 MB"))
    }
}
