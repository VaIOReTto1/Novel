package com.novel.page.read.viewmodel

import androidx.compose.ui.unit.IntSize

internal class ReaderSettingsRefreshCoordinator {

    fun shouldRefreshPagination(
        previousSettings: ReaderSettings?,
        currentSettings: ReaderSettings,
        containerSize: IntSize,
    ): Boolean {
        if (previousSettings == null || containerSize == IntSize.Zero) {
            return false
        }

        return previousSettings.fontSize != currentSettings.fontSize ||
            previousSettings.pageFlipEffect != currentSettings.pageFlipEffect
    }
}
