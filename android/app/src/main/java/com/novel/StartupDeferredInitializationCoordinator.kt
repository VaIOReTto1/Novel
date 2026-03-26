package com.novel

internal data class StartupDeferredInitializationPlan(
    val shouldInitializeNetwork: Boolean,
    val shouldInitializeSettings: Boolean,
)

internal class StartupDeferredInitializationCoordinator {

    private var hasScheduledDeferredInitialization = false

    fun createPlanAfterFirstFrame(firstFrameDrawn: Boolean = true): StartupDeferredInitializationPlan {
        if (!firstFrameDrawn || hasScheduledDeferredInitialization) {
            return StartupDeferredInitializationPlan(
                shouldInitializeNetwork = false,
                shouldInitializeSettings = false,
            )
        }

        hasScheduledDeferredInitialization = true
        return StartupDeferredInitializationPlan(
            shouldInitializeNetwork = true,
            shouldInitializeSettings = true,
        )
    }
}
