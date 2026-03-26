package com.novel.utils.network.cache

import java.io.File

data class CacheGovernanceSummary(
    val cleanupReductionRatio: Double,
    val averageBytesCleanedPerRun: Double,
)

data class CacheGovernanceRecommendation(
    val id: String,
    val severity: String,
    val message: String,
)

data class CacheGovernanceReport(
    val currentCacheSizeBytes: Long,
    val currentEntryCount: Int,
    val cleanupStats: CleanupStats,
    val summary: CacheGovernanceSummary,
    val recommendations: List<CacheGovernanceRecommendation>,
)

class CacheGovernanceReportGenerator {

    fun generate(
        cacheDir: File,
        cleanupStats: CleanupStats,
    ): CacheGovernanceReport {
        val files = cacheDir.listFiles()?.filter { it.isFile && it.name != "cache_version.txt" }.orEmpty()
        val summary = buildSummary(cleanupStats)
        return CacheGovernanceReport(
            currentCacheSizeBytes = files.sumOf { it.length() },
            currentEntryCount = files.size,
            cleanupStats = cleanupStats,
            summary = summary,
            recommendations = buildRecommendations(cleanupStats, summary),
        )
    }

    fun toMarkdown(
        report: CacheGovernanceReport,
        generatedOn: String,
    ): String {
        return buildString {
            appendLine("# 缓存清理治理报告 / Cache Governance Report")
            appendLine()
            appendLine("## 摘要 / Summary")
            appendLine("- generated_on: `$generatedOn`")
            appendLine("- current_cache_size_bytes: `${report.currentCacheSizeBytes}`")
            appendLine("- current_entry_count: `${report.currentEntryCount}`")
            appendLine("- cleanup_reduction_ratio: `${"%.2f".format(report.summary.cleanupReductionRatio)}`")
            appendLine("- average_bytes_cleaned_per_run: `${"%.2f".format(report.summary.averageBytesCleanedPerRun)}`")
            appendLine()
            appendLine("## Cleanup 统计")
            appendLine("- cleanup_runs: `${report.cleanupStats.cleanupRuns}`")
            appendLine("- cleanup_reason: `${report.cleanupStats.cleanupReason}`")
            appendLine("- cleanup_duration_ms: `${report.cleanupStats.lastCleanupDurationMs}`")
            appendLine("- cleanup_entries: `${report.cleanupStats.entryCountBefore} -> ${report.cleanupStats.entryCountAfter}`")
            appendLine("- total_cleaned: `${report.cleanupStats.totalCleaned}`")
            appendLine("- space_cleaned_bytes: `${report.cleanupStats.spaceCleaned}`")
            appendLine()
            appendLine("## 风险提示 / Recommendations")
            if (report.recommendations.isEmpty()) {
                appendLine("- none")
            } else {
                report.recommendations.forEach { recommendation ->
                    appendLine(
                        "- ${recommendation.severity} `${recommendation.id}`: ${recommendation.message}",
                    )
                }
            }
        }
    }

    private fun buildSummary(cleanupStats: CleanupStats): CacheGovernanceSummary {
        val reductionRatio = if (cleanupStats.entryCountBefore > 0) {
            (cleanupStats.entryCountBefore - cleanupStats.entryCountAfter).toDouble() / cleanupStats.entryCountBefore.toDouble()
        } else {
            0.0
        }
        val averageBytesCleanedPerRun = if (cleanupStats.cleanupRuns > 0) {
            cleanupStats.spaceCleaned.toDouble() / cleanupStats.cleanupRuns.toDouble()
        } else {
            0.0
        }

        return CacheGovernanceSummary(
            cleanupReductionRatio = reductionRatio,
            averageBytesCleanedPerRun = averageBytesCleanedPerRun,
        )
    }

    private fun buildRecommendations(
        cleanupStats: CleanupStats,
        summary: CacheGovernanceSummary,
    ): List<CacheGovernanceRecommendation> {
        val recommendations = mutableListOf<CacheGovernanceRecommendation>()

        if (cleanupStats.cleanupRuns >= 4) {
            recommendations += CacheGovernanceRecommendation(
                id = "cleanup-frequency-high",
                severity = "risk",
                message = "Cleanup is running frequently and may indicate unstable cache pressure handling.",
            )
        }

        if (summary.cleanupReductionRatio >= 0.5) {
            recommendations += CacheGovernanceRecommendation(
                id = "cleanup-entry-drop-large",
                severity = "warning",
                message = "Cleanup removes at least half of tracked entries in the latest snapshot.",
            )
        }

        if (cleanupStats.cleanupRuns > 0 && cleanupStats.spaceCleaned <= 0L) {
            recommendations += CacheGovernanceRecommendation(
                id = "cleanup-space-release-low",
                severity = "warning",
                message = "Cleanup runs are recorded but no storage was reclaimed.",
            )
        }

        return recommendations
    }
}
