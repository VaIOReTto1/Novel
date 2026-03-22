package com.novel.page.read.viewmodel

import androidx.compose.ui.unit.IntSize

data class ReaderInitialLoadPlan(
    val shouldInitReader: Boolean,
    val shouldShowProgressRestoredHint: Boolean,
)

class ReaderStartupCoordinator {

    fun createInitialLoadPlan(
        bookId: String,
        chapterId: String?,
    ): ReaderInitialLoadPlan {
        val shouldInitReader = bookId.isNotBlank()
        return ReaderInitialLoadPlan(
            shouldInitReader = shouldInitReader,
            shouldShowProgressRestoredHint = shouldInitReader && chapterId == null,
        )
    }

    fun shouldRefreshContainerAfterInit(
        isInitialized: Boolean,
        containerSize: IntSize,
        hasPendingPostInitRefresh: Boolean,
    ): Boolean {
        return hasPendingPostInitRefresh &&
            isInitialized &&
            containerSize != IntSize.Zero
    }
}
