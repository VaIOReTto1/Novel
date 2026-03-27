package com.novel.utils.network.cache

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CacheGovernanceReportGeneratorTest {

    @Test
    fun generate_collectsCurrentSizeFileCountCleanupStatsAndSummary() {
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
        assertEquals(0.22, report.summary.cleanupReductionRatio, 0.01)
        assertEquals(0, report.recommendations.size)
    }

    @Test
    fun generate_flagsAggressiveCleanupPatterns() {
        val root = createTempDir(prefix = "cache-governance-risky")
        File(root, "a.json").writeText("12345")

        val generator = CacheGovernanceReportGenerator()
        val report = generator.generate(
            cacheDir = root,
            cleanupStats = CleanupStats(
                totalCleaned = 12,
                cleanupRuns = 4,
                spaceCleaned = 0L,
                lastCleanupTime = 50L,
                cleanupReason = "MEMORY_PRESSURE",
                lastCleanupDurationMs = 20L,
                entryCountBefore = 18,
                entryCountAfter = 6,
            ),
        )

        assertEquals(3, report.recommendations.size)
        assertTrue(report.recommendations.any { it.id == "cleanup-frequency-high" })
        assertTrue(report.recommendations.any { it.id == "cleanup-entry-drop-large" })
        assertTrue(report.recommendations.any { it.id == "cleanup-space-release-low" })
    }

    @Test
    fun toMarkdown_rendersSummaryAndRecommendationSections() {
        val generator = CacheGovernanceReportGenerator()
        val markdown = generator.toMarkdown(
            report = CacheGovernanceReport(
                currentCacheSizeBytes = 1024L,
                currentEntryCount = 7,
                cleanupStats = CleanupStats(
                    totalCleaned = 6,
                    cleanupRuns = 4,
                    spaceCleaned = 0L,
                    lastCleanupTime = 50L,
                    cleanupReason = "STORAGE_PRESSURE",
                    lastCleanupDurationMs = 20L,
                    entryCountBefore = 18,
                    entryCountAfter = 6,
                ),
                summary = CacheGovernanceSummary(
                    cleanupReductionRatio = 0.66,
                    averageBytesCleanedPerRun = 0.0,
                ),
                recommendations = listOf(
                    CacheGovernanceRecommendation(
                        id = "cleanup-frequency-high",
                        severity = "risk",
                        message = "cleanup frequency is high",
                    ),
                ),
            ),
            generatedOn = "2026-03-22",
        )

        assertTrue(markdown.contains("#"))
        assertTrue(markdown.contains("cleanup_reduction_ratio"))
        assertTrue(markdown.contains("cleanup-frequency-high"))
    }
}
