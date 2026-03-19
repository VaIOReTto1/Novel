package com.novel.rn.bridge.delegate

import com.novel.rn.settings.SettingsEffect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationThemeDelegateTest {

    private val delegate = NavigationThemeDelegate()

    @Test
    fun mapEffect_returnsSuccessForShowToast() {
        val result = delegate.mapEffect(
            SettingsEffect.ShowToast("切换成功")
        )

        assertTrue(result is NavigationThemeResult.Success)
        assertEquals("切换成功", (result as NavigationThemeResult.Success).message)
    }

    @Test
    fun mapEffect_returnsFailureForShowError() {
        val result = delegate.mapEffect(
            SettingsEffect.ShowError("切换失败")
        )

        assertTrue(result is NavigationThemeResult.Failure)
        assertEquals("THEME_CHANGE_ERROR", (result as NavigationThemeResult.Failure).errorCode)
        assertEquals("切换失败", result.errorMessage)
    }

    @Test
    fun mapEffect_returnsNullForIrrelevantEffects() {
        val result = delegate.mapEffect(
            SettingsEffect.NotifyThemeChanged("dark")
        )

        assertEquals(null, result)
    }
}
