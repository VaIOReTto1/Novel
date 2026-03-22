package com.novel.page.welfare.viewmodel

internal data class WelfarePageBootstrapPlan(
    val shouldInitializePreloadManager: Boolean,
    val shouldStartPerformanceMonitor: Boolean,
    val shouldDispatchInitializeIntent: Boolean,
)

internal class WelfarePageBootstrapCoordinator(
    private val viewModelOwnsInitialization: Boolean = true,
) {

    fun createInitialPlan(alreadyBootstrapped: Boolean): WelfarePageBootstrapPlan {
        if (alreadyBootstrapped) {
            return WelfarePageBootstrapPlan(
                shouldInitializePreloadManager = false,
                shouldStartPerformanceMonitor = false,
                shouldDispatchInitializeIntent = false,
            )
        }

        return WelfarePageBootstrapPlan(
            shouldInitializePreloadManager = true,
            shouldStartPerformanceMonitor = true,
            shouldDispatchInitializeIntent = !viewModelOwnsInitialization,
        )
    }
}
