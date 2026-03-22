package com.novel.page.read.viewmodel

import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderSettingsRefreshCoordinatorTest {

    private val coordinator = ReaderSettingsRefreshCoordinator()

    @Test
    fun shouldRefreshPagination_returnsFalse_withoutPreviousSettings() {
        val shouldRefresh = coordinator.shouldRefreshPagination(
            previousSettings = null,
            currentSettings = ReaderSettings.getDefault(),
            containerSize = IntSize(1080, 1920),
        )

        assertThat(shouldRefresh).isEqualTo(false)
    }

    @Test
    fun shouldRefreshPagination_returnsFalse_whenContainerNotMeasured() {
        val base = ReaderSettings.getDefault()

        val shouldRefresh = coordinator.shouldRefreshPagination(
            previousSettings = base,
            currentSettings = base.copy(fontSize = base.fontSize + 2),
            containerSize = IntSize.Zero,
        )

        assertThat(shouldRefresh).isEqualTo(false)
    }

    @Test
    fun shouldRefreshPagination_returnsTrue_whenFontSizeChanges() {
        val base = ReaderSettings.getDefault()

        val shouldRefresh = coordinator.shouldRefreshPagination(
            previousSettings = base,
            currentSettings = base.copy(fontSize = base.fontSize + 2),
            containerSize = IntSize(1080, 1920),
        )

        assertThat(shouldRefresh).isEqualTo(true)
    }

    @Test
    fun shouldRefreshPagination_returnsTrue_whenFlipEffectChanges() {
        val base = ReaderSettings.getDefault()

        val shouldRefresh = coordinator.shouldRefreshPagination(
            previousSettings = base,
            currentSettings = base.copy(pageFlipEffect = PageFlipEffect.SLIDE),
            containerSize = IntSize(1080, 1920),
        )

        assertThat(shouldRefresh).isEqualTo(true)
    }

    @Test
    fun shouldRefreshPagination_returnsFalse_forColorOnlyChanges() {
        val base = ReaderSettings.getDefault()

        val shouldRefresh = coordinator.shouldRefreshPagination(
            previousSettings = base,
            currentSettings = base.copy(brightness = 0.8f),
            containerSize = IntSize(1080, 1920),
        )

        assertThat(shouldRefresh).isEqualTo(false)
    }
}
