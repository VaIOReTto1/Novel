package com.novel.utils.network.cache

import java.io.File

data class CacheGovernanceReport(
    val currentCacheSizeBytes: Long,
    val currentEntryCount: Int,
    val cleanupStats: CleanupStats,
)

class CacheGovernanceReportGenerator {

    fun generate(
        cacheDir: File,
        cleanupStats: CleanupStats,
    ): CacheGovernanceReport {
        val files = cacheDir.listFiles()?.filter { it.isFile && it.name != "cache_version.txt" }.orEmpty()
        return CacheGovernanceReport(
            currentCacheSizeBytes = files.sumOf { it.length() },
            currentEntryCount = files.size,
            cleanupStats = cleanupStats,
        )
    }

    fun toMarkdown(
        report: CacheGovernanceReport,
        generatedOn: String,
    ): String {
        return buildString {
            appendLine("# 缓存清理治理报告")
            appendLine()
            appendLine("## 摘要")
            appendLine("- 日期：`$generatedOn`")
            appendLine("- 当前缓存体积：`${report.currentCacheSizeBytes}` bytes")
            appendLine("- 当前缓存条目：`${report.currentEntryCount}`")
            appendLine()
            appendLine("## Cleanup 统计")
            appendLine("- cleanup 次数：`${report.cleanupStats.cleanupRuns}`")
            appendLine("- cleanup 原因：`${report.cleanupStats.cleanupReason}`")
            appendLine("- cleanup 耗时：`${report.cleanupStats.lastCleanupDurationMs}` ms")
            appendLine("- cleanup 前后条目量：`${report.cleanupStats.entryCountBefore} -> ${report.cleanupStats.entryCountAfter}`")
            appendLine("- 累计清理条目：`${report.cleanupStats.totalCleaned}`")
            appendLine("- 累计释放空间：`${report.cleanupStats.spaceCleaned}` bytes")
        }
    }
}
