package com.novel.page.read.viewmodel

internal data class ReaderPerformanceTrace(
    val action: String,
    val startedAtMs: Long,
    val metadata: Map<String, String>,
)

internal class ReaderPerformanceTraceCoordinator(
    private val nowMs: () -> Long = System::currentTimeMillis,
) {

    fun start(
        action: String,
        metadata: Map<String, String> = emptyMap(),
    ): ReaderPerformanceTrace {
        return ReaderPerformanceTrace(
            action = action,
            startedAtMs = nowMs(),
            metadata = metadata,
        )
    }

    fun formatStartMessage(trace: ReaderPerformanceTrace): String {
        return buildString {
            append("phase=start action=${trace.action}")
            appendFormattedMetadata(trace.metadata)
        }
    }

    fun formatFinishMessage(
        trace: ReaderPerformanceTrace,
        status: String,
        metadata: Map<String, String> = emptyMap(),
    ): String {
        val durationMs = (nowMs() - trace.startedAtMs).coerceAtLeast(0L)
        val mergedMetadata = linkedMapOf<String, String>().apply {
            putAll(trace.metadata)
            putAll(metadata)
        }

        return buildString {
            append("phase=finish action=${trace.action} status=$status durationMs=$durationMs")
            appendFormattedMetadata(mergedMetadata)
        }
    }

    private fun StringBuilder.appendFormattedMetadata(metadata: Map<String, String>) {
        metadata.forEach { (key, value) ->
            append(" $key=$value")
        }
    }
}
