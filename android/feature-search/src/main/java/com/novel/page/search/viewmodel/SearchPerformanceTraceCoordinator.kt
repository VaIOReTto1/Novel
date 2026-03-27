package com.novel.page.search.viewmodel

data class SearchPerformanceTrace(
    val action: String,
    val startedAtMs: Long,
    val metadata: Map<String, String>,
)

class SearchPerformanceTraceCoordinator(
    private val nowMs: () -> Long = System::currentTimeMillis,
) {

    private companion object {
        val METADATA_PRIORITY = listOf("trigger", "query", "page", "resultCount", "hasMore")
    }

    fun start(
        action: String,
        metadata: Map<String, String> = emptyMap(),
    ): SearchPerformanceTrace {
        return SearchPerformanceTrace(
            action = action,
            startedAtMs = nowMs(),
            metadata = metadata,
        )
    }

    fun formatStartMessage(trace: SearchPerformanceTrace): String {
        return buildString {
            append("phase=start action=${trace.action}")
            appendFormattedMetadata(trace.metadata)
        }
    }

    fun formatFinishMessage(
        trace: SearchPerformanceTrace,
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
        metadata
            .entries
            .sortedWith(
                compareBy<Map.Entry<String, String>>(
                    { METADATA_PRIORITY.indexOf(it.key).let { index -> if (index == -1) Int.MAX_VALUE else index } },
                    { it.key },
                ),
            )
            .forEach { (key, value) ->
            append(" $key=$value")
            }
    }
}
