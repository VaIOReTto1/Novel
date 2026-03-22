package com.novel.utils.network.cache

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CacheCleanupCoordinatorTest {

    @Test
    fun performCleanup_routesOnlyToRequestedStrategy() {
        val coordinator = CacheCleanupCoordinator(currentTimeMillis = { 100L })

        CleanupStrategy.values().forEach { strategy ->
            val calls = mutableListOf<String>()

            val summary = coordinator.performCleanup(
                strategy = strategy,
                currentStats = CleanupStats(
                    totalCleaned = 0,
                    cleanupRuns = 0,
                    spaceCleaned = 0L,
                    lastCleanupTime = 0L,
                    cleanupReason = "",
                    lastCleanupDurationMs = 0L,
                    entryCountBefore = 0,
                    entryCountAfter = 0,
                ),
                entryCountBefore = 10,
                entryCountAfter = 9,
                performLRUCleanup = {
                    calls += "lru"
                    1 to 10L
                },
                performTimeBasedCleanup = {
                    calls += "time"
                    2 to 20L
                },
                performHybridCleanup = {
                    calls += "hybrid"
                    3 to 30L
                },
                performStoragePressureCleanup = {
                    calls += "storage"
                    4 to 40L
                },
            )

            val expectedCall = when (strategy) {
                CleanupStrategy.LRU_ONLY -> "lru"
                CleanupStrategy.TIME_BASED_ONLY -> "time"
                CleanupStrategy.SMART_HYBRID -> "hybrid"
                CleanupStrategy.STORAGE_PRESSURE -> "storage"
            }
            val expectedResult = when (strategy) {
                CleanupStrategy.LRU_ONLY -> 1 to 10L
                CleanupStrategy.TIME_BASED_ONLY -> 2 to 20L
                CleanupStrategy.SMART_HYBRID -> 3 to 30L
                CleanupStrategy.STORAGE_PRESSURE -> 4 to 40L
            }

            assertThat(calls).containsExactly(expectedCall)
            assertThat(summary.cleanedCount).isEqualTo(expectedResult.first)
            assertThat(summary.spaceCleaned).isEqualTo(expectedResult.second)
            assertThat(summary.updatedStats.cleanupReason).isEqualTo(strategy.name)
            assertThat(summary.updatedStats.cleanupRuns).isEqualTo(1)
            assertThat(summary.updatedStats.entryCountBefore).isEqualTo(10)
            assertThat(summary.updatedStats.entryCountAfter).isEqualTo(9)
        }
    }

    @Test
    fun performCleanup_accumulatesStatsAndTracksDuration() {
        var currentTime = 1_000L
        val coordinator = CacheCleanupCoordinator(currentTimeMillis = { currentTime })

        val summary = coordinator.performCleanup(
            strategy = CleanupStrategy.TIME_BASED_ONLY,
            currentStats = CleanupStats(
                totalCleaned = 7,
                cleanupRuns = 2,
                spaceCleaned = 512L,
                lastCleanupTime = 111L,
                cleanupReason = "LRU_ONLY",
                lastCleanupDurationMs = 99L,
                entryCountBefore = 12,
                entryCountAfter = 10,
            ),
            entryCountBefore = 10,
            entryCountAfter = 7,
            performLRUCleanup = { error("LRU cleanup should not run") },
            performTimeBasedCleanup = {
                currentTime = 1_250L
                3 to 256L
            },
            performHybridCleanup = { error("Hybrid cleanup should not run") },
            performStoragePressureCleanup = { error("Storage cleanup should not run") },
        )

        assertThat(summary.cleanedCount).isEqualTo(3)
        assertThat(summary.spaceCleaned).isEqualTo(256L)
        assertThat(summary.durationMs).isEqualTo(250L)
        assertThat(summary.updatedStats).isEqualTo(
            CleanupStats(
                totalCleaned = 10,
                cleanupRuns = 3,
                spaceCleaned = 768L,
                lastCleanupTime = 1_250L,
                cleanupReason = "TIME_BASED_ONLY",
                lastCleanupDurationMs = 250L,
                entryCountBefore = 10,
                entryCountAfter = 7,
            )
        )
    }
}
