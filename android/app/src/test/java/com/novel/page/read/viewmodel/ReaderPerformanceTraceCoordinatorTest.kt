package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderPerformanceTraceCoordinatorTest {

    @Test
    fun formatStartMessage_includesActionAndMetadata() {
        val coordinator = ReaderPerformanceTraceCoordinator(nowMs = { 100L })

        val trace = coordinator.start(
            action = "init",
            metadata = mapOf("bookId" to "book-1", "entry" to "restored"),
        )

        assertThat(coordinator.formatStartMessage(trace))
            .isEqualTo("phase=start action=init bookId=book-1 entry=restored")
    }

    @Test
    fun formatFinishMessage_includesDurationStatusAndMergedMetadata() {
        var now = 100L
        val coordinator = ReaderPerformanceTraceCoordinator(nowMs = { now })
        val trace = coordinator.start(
            action = "settings_update",
            metadata = mapOf("fontSize" to "18"),
        )

        now = 460L

        assertThat(
            coordinator.formatFinishMessage(
                trace = trace,
                status = "success",
                metadata = mapOf("rebuild" to "true"),
            ),
        ).isEqualTo(
            "phase=finish action=settings_update status=success durationMs=360 fontSize=18 rebuild=true",
        )
    }
}
