package com.novel.utils.network.cache

internal data class CleanupExecutionSummary(
    val cleanedCount: Int,
    val spaceCleaned: Long,
    val updatedStats: CleanupStats,
    val durationMs: Long,
)

internal class CacheCleanupCoordinator(
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) {

    fun performCleanup(
        strategy: CleanupStrategy,
        currentStats: CleanupStats,
        entryCountBefore: Int,
        entryCountAfter: Int,
        performLRUCleanup: () -> Pair<Int, Long>,
        performTimeBasedCleanup: () -> Pair<Int, Long>,
        performHybridCleanup: () -> Pair<Int, Long>,
        performStoragePressureCleanup: () -> Pair<Int, Long>,
    ): CleanupExecutionSummary {
        val startTime = currentTimeMillis()
        val (cleanedCount, spaceCleaned) = when (strategy) {
            CleanupStrategy.LRU_ONLY -> performLRUCleanup()
            CleanupStrategy.TIME_BASED_ONLY -> performTimeBasedCleanup()
            CleanupStrategy.SMART_HYBRID -> performHybridCleanup()
            CleanupStrategy.STORAGE_PRESSURE -> performStoragePressureCleanup()
        }
        val endTime = currentTimeMillis()

        return CleanupExecutionSummary(
            cleanedCount = cleanedCount,
            spaceCleaned = spaceCleaned,
            updatedStats = CleanupStats(
                totalCleaned = currentStats.totalCleaned + cleanedCount,
                cleanupRuns = currentStats.cleanupRuns + 1,
                spaceCleaned = currentStats.spaceCleaned + spaceCleaned,
                lastCleanupTime = endTime,
                cleanupReason = strategy.name,
                lastCleanupDurationMs = endTime - startTime,
                entryCountBefore = entryCountBefore,
                entryCountAfter = entryCountAfter,
            ),
            durationMs = endTime - startTime,
        )
    }
}
