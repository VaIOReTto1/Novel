package com.novel.page.read.viewmodel

internal class ReaderRestoreHintCoordinator(
    private val visibleDurationMs: Long = DEFAULT_VISIBLE_DURATION_MS,
) {

    fun shouldShowHint(
        chapterId: String?,
        isInitSuccess: Boolean,
        hasPageData: Boolean,
        hasShownHint: Boolean,
    ): Boolean {
        return chapterId == null &&
            isInitSuccess &&
            hasPageData &&
            !hasShownHint
    }

    fun hintVisibleDurationMs(): Long = visibleDurationMs

    private companion object {
        const val DEFAULT_VISIBLE_DURATION_MS = 3000L
    }
}
