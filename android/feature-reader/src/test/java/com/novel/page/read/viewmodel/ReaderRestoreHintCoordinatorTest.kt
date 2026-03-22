package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderRestoreHintCoordinatorTest {

    private val coordinator = ReaderRestoreHintCoordinator()

    @Test
    fun shouldShowHint_returnsTrue_onlyWhenRestoreEntryIsReady() {
        val shouldShow = coordinator.shouldShowHint(
            chapterId = null,
            isInitSuccess = true,
            hasPageData = true,
            hasShownHint = false,
        )

        assertThat(shouldShow).isTrue()
    }

    @Test
    fun shouldShowHint_returnsFalse_whenChapterIdProvided() {
        val shouldShow = coordinator.shouldShowHint(
            chapterId = "chapter-2",
            isInitSuccess = true,
            hasPageData = true,
            hasShownHint = false,
        )

        assertThat(shouldShow).isFalse()
    }

    @Test
    fun shouldShowHint_returnsFalse_whenInitNotReady() {
        val shouldShow = coordinator.shouldShowHint(
            chapterId = null,
            isInitSuccess = false,
            hasPageData = true,
            hasShownHint = false,
        )

        assertThat(shouldShow).isFalse()
    }

    @Test
    fun shouldShowHint_returnsFalse_whenPageDataMissingOrAlreadyShown() {
        assertThat(
            coordinator.shouldShowHint(
                chapterId = null,
                isInitSuccess = true,
                hasPageData = false,
                hasShownHint = false,
            ),
        ).isFalse()

        assertThat(
            coordinator.shouldShowHint(
                chapterId = null,
                isInitSuccess = true,
                hasPageData = true,
                hasShownHint = true,
            ),
        ).isFalse()
    }

    @Test
    fun hintVisibleDurationMs_usesFixedDuration() {
        assertThat(coordinator.hintVisibleDurationMs()).isEqualTo(3000L)
    }
}
