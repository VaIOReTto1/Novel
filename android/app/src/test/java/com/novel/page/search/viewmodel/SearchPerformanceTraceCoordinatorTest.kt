package com.novel.page.search.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SearchPerformanceTraceCoordinatorTest {

    @Test
    fun formatStartMessage_includesTriggerAndQueryMetadata() {
        val coordinator = SearchPerformanceTraceCoordinator(nowMs = { 10L })

        val trace = coordinator.start(
            action = "search",
            metadata = mapOf(
                "trigger" to SearchTriggerSource.FILTER_APPLY.name,
                "query" to "玄幻",
            ),
        )

        assertThat(coordinator.formatStartMessage(trace))
            .isEqualTo("phase=start action=search trigger=FILTER_APPLY query=玄幻")
    }

    @Test
    fun formatFinishMessage_includesDurationAndMergedMetadata() {
        var now = 50L
        val coordinator = SearchPerformanceTraceCoordinator(nowMs = { now })
        val trace = coordinator.start(
            action = "search",
            metadata = mapOf(
                "trigger" to SearchTriggerSource.LOAD_MORE.name,
                "page" to "2",
            ),
        )

        now = 270L

        assertThat(
            coordinator.formatFinishMessage(
                trace = trace,
                status = "success",
                metadata = mapOf("resultCount" to "20"),
            ),
        ).isEqualTo("phase=finish action=search status=success durationMs=220 trigger=LOAD_MORE page=2 resultCount=20")
    }
}
