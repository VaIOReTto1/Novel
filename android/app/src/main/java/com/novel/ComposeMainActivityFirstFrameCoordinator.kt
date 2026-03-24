package com.novel

import com.facebook.react.ReactInstanceManager
import com.novel.rn.ReactNativeHostPathTraceCoordinator
import com.novel.utils.TimberLogger
import kotlinx.coroutines.delay

internal class ComposeMainActivityFirstFrameCoordinator(
    private val hostPathTraceCoordinator: ReactNativeHostPathTraceCoordinator = ReactNativeHostPathTraceCoordinator(),
    private val reactNativePrewarmCoordinator: ReactNativePrewarmCoordinator = ReactNativePrewarmCoordinator(),
) {

    suspend fun onFirstFrameRendered(
        application: MainApplication?,
        reactInstanceManager: ReactInstanceManager?,
    ) {
        delay(100)
        application?.markFirstFrameDrawn()

        val hasReactContext = reactInstanceManager?.currentReactContext != null
        TimberLogger.d(
            "ComposeMainActivity",
            hostPathTraceCoordinator.formatContextTrace(
                trigger = "prewarm_after_first_frame",
                hasReactContext = hasReactContext,
            ),
        )

        if (reactNativePrewarmCoordinator.shouldPrewarmAfterFirstFrame() && !hasReactContext) {
            TimberLogger.d(
                "ComposeMainActivity",
                hostPathTraceCoordinator.formatContextTrace(
                    trigger = "create_react_context_in_background",
                    hasReactContext = false,
                ),
            )
            reactInstanceManager?.createReactContextInBackground()
        }

        delay(200)
        application?.markAppFullyLoaded()
    }
}
