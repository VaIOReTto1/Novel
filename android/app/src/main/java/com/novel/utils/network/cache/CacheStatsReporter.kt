package com.novel.utils.network.cache

internal class CacheStatsReporter(
    private val cleanupCoordinator: CacheCleanupCoordinator = CacheCleanupCoordinator(),
    initialStats: CleanupStats = CleanupStats(
        totalCleaned = 0,
        cleanupRuns = 0,
        spaceCleaned = 0L,
        lastCleanupTime = 0L,
        cleanupReason = "",
        lastCleanupDurationMs = 0L,
        entryCountBefore = 0,
        entryCountAfter = 0,
    ),
) {

    private var cleanupStats: CleanupStats = initialStats

    fun performCleanup(
        strategy: CleanupStrategy,
        entryCountBefore: Int,
        entryCountAfter: Int,
        performLRUCleanup: () -> Pair<Int, Long>,
        performTimeBasedCleanup: () -> Pair<Int, Long>,
        performHybridCleanup: () -> Pair<Int, Long>,
        performStoragePressureCleanup: () -> Pair<Int, Long>,
    ): CleanupExecutionSummary {
        val summary = cleanupCoordinator.performCleanup(
            strategy = strategy,
            currentStats = cleanupStats,
            entryCountBefore = entryCountBefore,
            entryCountAfter = entryCountAfter,
            performLRUCleanup = performLRUCleanup,
            performTimeBasedCleanup = performTimeBasedCleanup,
            performHybridCleanup = performHybridCleanup,
            performStoragePressureCleanup = performStoragePressureCleanup,
        )

        cleanupStats = summary.updatedStats
        return summary
    }

    fun getCleanupStats(): CleanupStats = cleanupStats

    fun updateCleanupStats(stats: CleanupStats) {
        cleanupStats = stats
    }
}
