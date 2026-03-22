package com.novel.utils.network.cache

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CacheGovernanceReportGeneratorTest {

    @Test
    fun generate_collectsCurrentSizeFileCountAndCleanupStats() {
        val root = createTempDir(prefix = "cache-governance")
        File(root, "a.json").writeText("12345")
        File(root, "b.json").writeText("1234567890")

        val generator = CacheGovernanceReportGenerator()
        val report = generator.generate(
            cacheDir = root,
            cleanupStats = CleanupStats(
                totalCleaned = 6,
                cleanupRuns = 2,
                spaceCleaned = 612L,
                lastCleanupTime = 50L,
                cleanupReason = "STORAGE_PRESSURE",
                lastCleanupDurationMs = 20L,
                entryCountBefore = 18,
                entryCountAfter = 14,
            ),
        )

        assertEquals(2, report.currentEntryCount)
        assertEquals(15L, report.currentCacheSizeBytes)
        assertEquals("STORAGE_PRESSURE", report.cleanupStats.cleanupReason)
    }

    @Test
    fun toMarkdown_rendersChineseReportFields() {
        val generator = CacheGovernanceReportGenerator()
        val markdown = generator.toMarkdown(
            report = CacheGovernanceReport(
                currentCacheSizeBytes = 1024L,
                currentEntryCount = 7,
                cleanupStats = CleanupStats(
                    totalCleaned = 6,
                    cleanupRuns = 2,
                    spaceCleaned = 612L,
                    lastCleanupTime = 50L,
                    cleanupReason = "STORAGE_PRESSURE",
                    lastCleanupDurationMs = 20L,
                    entryCountBefore = 18,
                    entryCountAfter = 14,
                ),
            ),
            generatedOn = "2026-03-22",
        )

        assertTrue(markdown.contains("# 缓存清理治理报告"))
        assertTrue(markdown.contains("当前缓存体积"))
        assertTrue(markdown.contains("cleanup 前后条目量"))
    }
}
