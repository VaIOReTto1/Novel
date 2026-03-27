package com.novel.page.read.viewmodel

class ReaderRestoreHintCoordinator(
    private val visibleDurationMs: Long = DEFAULT_VISIBLE_DURATION_MS,
) {

    fun shouldShowHint(
        shouldRestoreFromProgress: Boolean,
        isInitSuccess: Boolean,
        hasPageData: Boolean,
        hasShownHint: Boolean,
    ): Boolean {
        return shouldRestoreFromProgress &&
            isInitSuccess &&
            hasPageData &&
            !hasShownHint
    }

    fun shouldAutoDismissHint(
        isHintVisible: Boolean,
        wasShownForRestore: Boolean,
    ): Boolean {
        return isHintVisible && wasShownForRestore
    }

    fun hintVisibleDurationMs(): Long = visibleDurationMs

    private companion object {
        const val DEFAULT_VISIBLE_DURATION_MS = 3000L
    }
}
