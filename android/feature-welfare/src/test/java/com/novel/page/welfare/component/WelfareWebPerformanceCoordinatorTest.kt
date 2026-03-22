package com.novel.page.welfare.component

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WelfareWebPerformanceCoordinatorTest {

    @Test
    fun shouldRecordFirstContentfulPaint_onlyReturnsTrueOncePerLoad() {
        val coordinator = WelfareWebPerformanceCoordinator()

        assertThat(coordinator.shouldRecordFirstContentfulPaint()).isTrue()
        assertThat(coordinator.shouldRecordFirstContentfulPaint()).isFalse()

        coordinator.resetForNewPageLoad()

        assertThat(coordinator.shouldRecordFirstContentfulPaint()).isTrue()
    }

    @Test
    fun shouldRecordTimeToInteractive_requiresFullProgressAndOnlyOncePerLoad() {
        val coordinator = WelfareWebPerformanceCoordinator()

        assertThat(coordinator.shouldRecordTimeToInteractive(80)).isFalse()
        assertThat(coordinator.shouldRecordTimeToInteractive(100)).isTrue()
        assertThat(coordinator.shouldRecordTimeToInteractive(100)).isFalse()

        coordinator.resetForNewPageLoad()

        assertThat(coordinator.shouldRecordTimeToInteractive(100)).isTrue()
    }

    @Test
    fun shouldRecordPageLoadComplete_requiresReadyStateAndOnlyOncePerLoad() {
        val coordinator = WelfareWebPerformanceCoordinator()

        assertThat(
            coordinator.shouldRecordPageLoadComplete(
                isLoading = true,
                currentUrl = "https://example.com",
            ),
        ).isFalse()

        assertThat(
            coordinator.shouldRecordPageLoadComplete(
                isLoading = false,
                currentUrl = "",
            ),
        ).isFalse()

        assertThat(
            coordinator.shouldRecordPageLoadComplete(
                isLoading = false,
                currentUrl = "https://example.com",
            ),
        ).isTrue()

        assertThat(
            coordinator.shouldRecordPageLoadComplete(
                isLoading = false,
                currentUrl = "https://example.com",
            ),
        ).isFalse()

        coordinator.resetForNewPageLoad()

        assertThat(
            coordinator.shouldRecordPageLoadComplete(
                isLoading = false,
                currentUrl = "https://example.com",
            ),
        ).isTrue()
    }
}
