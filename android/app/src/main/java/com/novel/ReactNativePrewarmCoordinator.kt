package com.novel

internal data class ReactNativePrewarmPlan(
    val shouldCreateReactContextInBackground: Boolean,
)

internal class ReactNativePrewarmCoordinator {

    private var hasPrewarmed = false

    fun createPlanAfterFirstFrame(hasReactContext: Boolean): ReactNativePrewarmPlan {
        if (hasPrewarmed || hasReactContext) {
            hasPrewarmed = true
            return ReactNativePrewarmPlan(
                shouldCreateReactContextInBackground = false,
            )
        }

        hasPrewarmed = true
        return ReactNativePrewarmPlan(
            shouldCreateReactContextInBackground = true,
        )
    }

    fun shouldPrewarmAfterFirstFrame(): Boolean {
        return createPlanAfterFirstFrame(hasReactContext = false)
            .shouldCreateReactContextInBackground
    }
}
