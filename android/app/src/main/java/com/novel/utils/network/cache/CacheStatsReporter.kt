package com.novel.utils.network.cache

internal class CacheStatsReporter(
    private val cleanupCoordinator: CacheCleanupCoordinator = CacheCleanupCoordinator(),
    initialStats: CleanupStats = CleanupStats(0, 0L, 0L, ""),
) {

    private var cleanupStats: CleanupStats = initialStats

    fun performCleanup(
        strategy: CleanupStrategy,
        performLRUCleanup: () -> Pair<Int, Long>,
        performTimeBasedCleanup: () -> Pair<Int, Long>,
        performHybridCleanup: () -> Pair<Int, Long>,
        performStoragePressureCleanup: () -> Pair<Int, Long>,
    ): CleanupExecutionSummary {
        val summary = cleanupCoordinator.performCleanup(
            strategy = strategy,
            currentStats = cleanupStats,
            performLRUCleanup = performLRUCleanup,
            performTimeBasedCleanup = performTimeBasedCleanup,
            performHybridCleanup = performHybridCleanup,
            performStoragePressureCleanup = performStoragePressureCleanup,
        )

        cleanupStats = summary.updatedStats
        return summary
    }

    fun getCleanupStats(): CleanupStats = cleanupStats
}
