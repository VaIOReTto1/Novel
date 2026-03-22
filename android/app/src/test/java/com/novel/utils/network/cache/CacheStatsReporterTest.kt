package com.novel.utils.network.cache

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CacheStatsReporterTest {

    @Test
    fun getCleanupStats_returnsDefaultStatsBeforeAnyCleanup() {
        val reporter = CacheStatsReporter(
            cleanupCoordinator = CacheCleanupCoordinator(currentTimeMillis = { 100L }),
        )

        assertThat(reporter.getCleanupStats()).isEqualTo(
            CleanupStats(
                totalCleaned = 0,
                cleanupRuns = 0,
                spaceCleaned = 0L,
                lastCleanupTime = 0L,
                cleanupReason = "",
                lastCleanupDurationMs = 0L,
                entryCountBefore = 0,
                entryCountAfter = 0,
            )
        )
    }

    @Test
    fun performCleanup_updatesExposedStatsWithLatestSummary() {
        var currentTime = 1_000L
        val reporter = CacheStatsReporter(
            cleanupCoordinator = CacheCleanupCoordinator(currentTimeMillis = { currentTime }),
        )

        val summary = reporter.performCleanup(
            strategy = CleanupStrategy.TIME_BASED_ONLY,
            entryCountBefore = 12,
            entryCountAfter = 9,
            performLRUCleanup = { error("LRU cleanup should not run") },
            performTimeBasedCleanup = {
                currentTime = 1_250L
                3 to 256L
            },
            performHybridCleanup = { error("Hybrid cleanup should not run") },
            performStoragePressureCleanup = { error("Storage cleanup should not run") },
        )

        val expectedStats = CleanupStats(
            totalCleaned = 3,
            cleanupRuns = 1,
            spaceCleaned = 256L,
            lastCleanupTime = 1_250L,
            cleanupReason = "TIME_BASED_ONLY",
            lastCleanupDurationMs = 250L,
            entryCountBefore = 12,
            entryCountAfter = 9,
        )
        assertThat(summary.updatedStats).isEqualTo(expectedStats)
        assertThat(reporter.getCleanupStats()).isEqualTo(expectedStats)
    }

    @Test
    fun performCleanup_accumulatesStatsAcrossMultipleRuns() {
        var currentTime = 10L
        val reporter = CacheStatsReporter(
            cleanupCoordinator = CacheCleanupCoordinator(currentTimeMillis = { currentTime }),
        )

        reporter.performCleanup(
            strategy = CleanupStrategy.LRU_ONLY,
            entryCountBefore = 20,
            entryCountAfter = 18,
            performLRUCleanup = {
                currentTime = 20L
                2 to 100L
            },
            performTimeBasedCleanup = { error("Time-based cleanup should not run") },
            performHybridCleanup = { error("Hybrid cleanup should not run") },
            performStoragePressureCleanup = { error("Storage cleanup should not run") },
        )

        currentTime = 30L
        reporter.performCleanup(
            strategy = CleanupStrategy.STORAGE_PRESSURE,
            entryCountBefore = 18,
            entryCountAfter = 14,
            performLRUCleanup = { error("LRU cleanup should not run") },
            performTimeBasedCleanup = { error("Time-based cleanup should not run") },
            performHybridCleanup = { error("Hybrid cleanup should not run") },
            performStoragePressureCleanup = {
                currentTime = 50L
                4 to 512L
            },
        )

        assertThat(reporter.getCleanupStats()).isEqualTo(
            CleanupStats(
                totalCleaned = 6,
                cleanupRuns = 2,
                spaceCleaned = 612L,
                lastCleanupTime = 50L,
                cleanupReason = "STORAGE_PRESSURE",
                lastCleanupDurationMs = 20L,
                entryCountBefore = 18,
                entryCountAfter = 14,
            )
        )
    }
}
