package com.novel

import android.view.Choreographer
import com.facebook.react.ReactInstanceManager
import com.novel.rn.ReactNativeHostPathTraceCoordinator
import com.novel.utils.TimberLogger
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

internal data class ComposeMainActivityFirstFramePlan(
    val shouldMarkFirstFrameDrawn: Boolean,
    val shouldCreateReactContextInBackground: Boolean,
    val shouldMarkAppFullyLoaded: Boolean,
)

internal class ComposeMainActivityFirstFrameCoordinator(
    private val hostPathTraceCoordinator: ReactNativeHostPathTraceCoordinator = ReactNativeHostPathTraceCoordinator(),
    private val reactNativePrewarmCoordinator: ReactNativePrewarmCoordinator = ReactNativePrewarmCoordinator(),
    private val awaitNextFrame: suspend () -> Unit = ::awaitNextFrameOnMainThread,
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
        runPlan(
            hasReactContext = hasReactContext,
            markFirstFrameDrawn = { application?.markFirstFrameDrawn() },
            createReactContextInBackground = { reactInstanceManager?.createReactContextInBackground() },
            markAppFullyLoaded = { application?.markAppFullyLoaded() },
        )
    }

    internal suspend fun runPlan(
        hasReactContext: Boolean,
        markFirstFrameDrawn: () -> Unit,
        createReactContextInBackground: () -> Unit,
        markAppFullyLoaded: () -> Unit,
    ) {
        val plan = createPlan(hasReactContext = hasReactContext)
        awaitNextFrame()
        if (plan.shouldMarkFirstFrameDrawn) {
            markFirstFrameDrawn()
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
            createReactContextInBackground()
        }

        awaitNextFrame()
        if (plan.shouldMarkAppFullyLoaded) {
            markAppFullyLoaded()
        }
    }
}

private suspend fun awaitNextFrameOnMainThread() {
    suspendCancellableCoroutine<Unit> { continuation ->
        Choreographer.getInstance().postFrameCallback {
            if (continuation.isActive) {
                continuation.resume(Unit)
            }
        }
    }
}
