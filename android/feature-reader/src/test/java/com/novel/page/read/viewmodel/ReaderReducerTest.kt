package com.novel.page.read.viewmodel

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderReducerTest {

    private val reducer = ReaderReducer()

    @Test
    fun reduce_updateSettingsBrightness_emitsBrightnessEffect() {
        val currentState = ReaderState(readerSettings = ReaderSettings.getDefault().copy(brightness = 0.5f))
        val newSettings = currentState.readerSettings.copy(brightness = 0.8f)

        val result = reducer.reduce(currentState, ReaderIntent.UpdateSettings(newSettings))

        assertThat(result.newState.readerSettings).isEqualTo(newSettings)
        assertThat(result.effect).isEqualTo(ReaderEffect.SetBrightness(0.8f))
    }

    @Test
    fun reduce_showSettingsPanel_hidesChapterList() {
        val currentState = ReaderState(isChapterListVisible = true)

        val result = reducer.reduce(currentState, ReaderIntent.ShowSettingsPanel(true))

        assertThat(result.newState.isSettingsPanelVisible).isTrue()
        assertThat(result.newState.isChapterListVisible).isFalse()
    }

    @Test
    fun reduce_updateContainerSize_updatesDensityAndSize() {
        val currentState = ReaderState()
        val size = IntSize(1080, 1920)
        val density = Density(2f)

        val result = reducer.reduce(currentState, ReaderIntent.UpdateContainerSize(size, density))

        assertThat(result.newState.containerSize).isEqualTo(size)
        assertThat(result.newState.density).isEqualTo(density)
    }
}
