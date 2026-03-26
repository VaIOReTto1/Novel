package com.novel

import com.facebook.react.ReactInstanceManager
import com.novel.rn.ReactNativeHostPathTraceCoordinator
import com.novel.utils.TimberLogger

internal data class ComposeMainActivityFirstFramePlan(
    val shouldMarkFirstFrameDrawn: Boolean,
    val shouldCreateReactContextInBackground: Boolean,
    val shouldMarkAppFullyLoaded: Boolean,
)

internal class ComposeMainActivityFirstFrameCoordinator(
    private val hostPathTraceCoordinator: ReactNativeHostPathTraceCoordinator = ReactNativeHostPathTraceCoordinator(),
    private val reactNativePrewarmCoordinator: ReactNativePrewarmCoordinator = ReactNativePrewarmCoordinator(),
) {

    internal fun createPlan(hasReactContext: Boolean): ComposeMainActivityFirstFramePlan {
        val prewarmPlan = reactNativePrewarmCoordinator.createPlanAfterFirstFrame(
            hasReactContext = hasReactContext,
        )
        return ComposeMainActivityFirstFramePlan(
            shouldMarkFirstFrameDrawn = true,
            shouldCreateReactContextInBackground = prewarmPlan.shouldCreateReactContextInBackground,
            shouldMarkAppFullyLoaded = true,
        )
    }

    suspend fun onFirstFrameRendered(
        application: MainApplication?,
        reactInstanceManager: ReactInstanceManager?,
    ) {
        val hasReactContext = reactInstanceManager?.currentReactContext != null
        val plan = createPlan(hasReactContext = hasReactContext)
        if (plan.shouldMarkFirstFrameDrawn) {
            application?.markFirstFrameDrawn()
        }
        TimberLogger.d(
            "ComposeMainActivity",
            hostPathTraceCoordinator.formatContextTrace(
                trigger = "prewarm_after_first_frame",
                hasReactContext = hasReactContext,
            ),
        )

        if (plan.shouldCreateReactContextInBackground) {
            TimberLogger.d(
                "ComposeMainActivity",
                hostPathTraceCoordinator.formatContextTrace(
                    trigger = "create_react_context_in_background",
                    hasReactContext = false,
                ),
            )
            reactInstanceManager?.createReactContextInBackground()
        }

        if (plan.shouldMarkAppFullyLoaded) {
            application?.markAppFullyLoaded()
        }
    }
}
