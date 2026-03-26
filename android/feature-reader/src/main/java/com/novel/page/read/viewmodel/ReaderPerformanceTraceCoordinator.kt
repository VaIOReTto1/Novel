package com.novel.page.read.viewmodel

data class ReaderPerformanceTrace(
    val action: String,
    val startedAtMs: Long,
    val metadata: Map<String, String>,
)

class ReaderPerformanceTraceCoordinator(
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
        val budgetMs = budgetFor(trace.action)
        return buildString {
            append("phase=start action=${trace.action}")
            if (budgetMs != null) {
                append(" budgetMs=$budgetMs")
            }
            appendFormattedMetadata(trace.metadata)
        }
    }

    fun formatFinishMessage(
        trace: ReaderPerformanceTrace,
        status: String,
        metadata: Map<String, String> = emptyMap(),
    ): String {
        val durationMs = (nowMs() - trace.startedAtMs).coerceAtLeast(0L)
        val budgetMs = budgetFor(trace.action)
        val mergedMetadata = linkedMapOf<String, String>().apply {
            putAll(trace.metadata)
            putAll(metadata)
        }

        return buildString {
            append("phase=finish action=${trace.action} status=$status durationMs=$durationMs")
            if (budgetMs != null) {
                append(" budgetMs=$budgetMs budgetStatus=${budgetStatus(durationMs, budgetMs)}")
            }
            appendFormattedMetadata(mergedMetadata)
        }
    }

    private fun budgetFor(action: String): Long? = ACTION_BUDGETS_MS[action]

    private fun budgetStatus(durationMs: Long, budgetMs: Long): String {
        return if (durationMs <= budgetMs) "within" else "over"
    }

    private fun StringBuilder.appendFormattedMetadata(metadata: Map<String, String>) {
        metadata.forEach { (key, value) ->
            append(" $key=$value")
        }
    }

    private companion object {
        val ACTION_BUDGETS_MS = mapOf(
            "init" to 1200L,
            "settings_update" to 400L,
            "flip" to 250L,
        )
    }
}
