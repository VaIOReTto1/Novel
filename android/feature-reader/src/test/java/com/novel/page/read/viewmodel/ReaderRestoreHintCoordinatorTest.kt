package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderRestoreHintCoordinatorTest {

    private val coordinator = ReaderRestoreHintCoordinator()

    @Test
    fun shouldShowHint_returnsTrue_onlyWhenRestoreEntryIsReady() {
        val shouldShow = coordinator.shouldShowHint(
            shouldRestoreFromProgress = true,
            isInitSuccess = true,
            hasPageData = true,
            hasShownHint = false,
        )

        assertThat(shouldShow).isTrue()
    }

    @Test
    fun shouldShowHint_returnsFalse_whenChapterIdProvided() {
        val shouldShow = coordinator.shouldShowHint(
            shouldRestoreFromProgress = false,
            isInitSuccess = true,
            hasPageData = true,
            hasShownHint = false,
        )

        assertThat(shouldShow).isFalse()
    }

    @Test
    fun shouldShowHint_returnsFalse_whenInitNotReady() {
        val shouldShow = coordinator.shouldShowHint(
            shouldRestoreFromProgress = true,
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
                shouldRestoreFromProgress = true,
                isInitSuccess = true,
                hasPageData = false,
                hasShownHint = false,
            ),
        ).isFalse()

        assertThat(
            coordinator.shouldShowHint(
                shouldRestoreFromProgress = true,
                isInitSuccess = true,
                hasPageData = true,
                hasShownHint = true,
            ),
        ).isFalse()
    }

    @Test
    fun shouldAutoDismissHint_onlyTracksRestoreManagedVisibility() {
        assertThat(
            coordinator.shouldAutoDismissHint(
                isHintVisible = true,
                wasShownForRestore = true,
            ),
        ).isTrue()

        assertThat(
            coordinator.shouldAutoDismissHint(
                isHintVisible = true,
                wasShownForRestore = false,
            ),
        ).isFalse()
    }

    @Test
    fun hintVisibleDurationMs_usesFixedDuration() {
        assertThat(coordinator.hintVisibleDurationMs()).isEqualTo(3000L)
    }
}
