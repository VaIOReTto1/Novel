package com.novel

internal enum class StartupDeferredInitializationTaskId {
    NETWORK,
    SETTINGS,
}

internal enum class StartupDeferredInitializationPriority {
    HIGH,
    MEDIUM,
}

internal data class StartupDeferredInitializationTask(
    val id: StartupDeferredInitializationTaskId,
    val priority: StartupDeferredInitializationPriority,
    val trigger: String,
    val expectedBenefit: String,
)

private fun buildDeferredTasks(
    shouldInitializeNetwork: Boolean,
    shouldInitializeSettings: Boolean,
): List<StartupDeferredInitializationTask> {
    return buildList {
        if (shouldInitializeNetwork) {
            add(
                StartupDeferredInitializationTask(
                    id = StartupDeferredInitializationTaskId.NETWORK,
                    priority = StartupDeferredInitializationPriority.HIGH,
                    trigger = "after_first_frame",
                    expectedBenefit = "将网络服务初始化移出首帧前关键路径",
                ),
            )
        }
        if (shouldInitializeSettings) {
            add(
                StartupDeferredInitializationTask(
                    id = StartupDeferredInitializationTaskId.SETTINGS,
                    priority = StartupDeferredInitializationPriority.MEDIUM,
                    trigger = "after_first_frame",
                    expectedBenefit = "将设置服务初始化移出首帧前关键路径",
                ),
            )
        }
    }
}

internal data class StartupDeferredInitializationPlan(
    val shouldInitializeNetwork: Boolean,
    val shouldInitializeSettings: Boolean,
    val tasks: List<StartupDeferredInitializationTask> = buildDeferredTasks(
        shouldInitializeNetwork = shouldInitializeNetwork,
        shouldInitializeSettings = shouldInitializeSettings,
    ),
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
