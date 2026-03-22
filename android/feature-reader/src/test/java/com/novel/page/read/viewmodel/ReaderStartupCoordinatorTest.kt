package com.novel.page.read.viewmodel

import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderStartupCoordinatorTest {

    private val coordinator = ReaderStartupCoordinator()

    @Test
    fun createInitialLoadPlan_skipsInitWhenBookIdIsBlank() {
        val outcome = coordinator.createInitialLoadPlan(
            bookId = "",
            chapterId = null,
        )

        assertThat(outcome.shouldInitReader).isEqualTo(false)
        assertThat(outcome.shouldShowProgressRestoredHint).isEqualTo(false)
    }

    @Test
    fun createInitialLoadPlan_showsRestoreHintWhenChapterIdMissing() {
        val outcome = coordinator.createInitialLoadPlan(
            bookId = "book-1",
            chapterId = null,
        )

        assertThat(outcome.shouldInitReader).isEqualTo(true)
        assertThat(outcome.shouldShowProgressRestoredHint).isEqualTo(true)
    }

    @Test
    fun createInitialLoadPlan_avoidsRestoreHintWhenChapterIdProvided() {
        val outcome = coordinator.createInitialLoadPlan(
            bookId = "book-1",
            chapterId = "chapter-2",
        )

        assertThat(outcome.shouldInitReader).isEqualTo(true)
        assertThat(outcome.shouldShowProgressRestoredHint).isEqualTo(false)
    }

    @Test
    fun shouldRefreshContainerAfterInit_requiresPendingRefreshAndMeasuredContainer() {
        assertThat(
            coordinator.shouldRefreshContainerAfterInit(
                isInitialized = true,
                containerSize = IntSize(1080, 1920),
                hasPendingPostInitRefresh = true,
            ),
        ).isEqualTo(true)

        assertThat(
            coordinator.shouldRefreshContainerAfterInit(
                isInitialized = false,
                containerSize = IntSize(1080, 1920),
                hasPendingPostInitRefresh = true,
            ),
        ).isEqualTo(false)

        assertThat(
            coordinator.shouldRefreshContainerAfterInit(
                isInitialized = true,
                containerSize = IntSize.Zero,
                hasPendingPostInitRefresh = true,
            ),
        ).isEqualTo(false)

        assertThat(
            coordinator.shouldRefreshContainerAfterInit(
                isInitialized = true,
                containerSize = IntSize(1080, 1920),
                hasPendingPostInitRefresh = false,
            ),
        ).isEqualTo(false)
    }
}
