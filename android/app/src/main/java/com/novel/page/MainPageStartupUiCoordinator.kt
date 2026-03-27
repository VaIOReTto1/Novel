package com.novel.page

data class MainPageStartupUiState(
    val showShortDramaToast: Boolean,
    val shouldLoadLaunchDialogAfterFirstFrame: Boolean,
)

data class MainPageDeferredUiPlan(
    val shouldRevealShortDramaToast: Boolean,
    val shouldLoadLaunchDialog: Boolean,
)

class MainPageStartupUiCoordinator {

    fun createInitialUiState(): MainPageStartupUiState {
        return MainPageStartupUiState(
            showShortDramaToast = false,
            shouldLoadLaunchDialogAfterFirstFrame = true,
        )
    }

    fun createAfterFirstFramePlan(hasRevealedDeferredUi: Boolean): MainPageDeferredUiPlan {
        if (hasRevealedDeferredUi) {
            return MainPageDeferredUiPlan(
                shouldRevealShortDramaToast = false,
                shouldLoadLaunchDialog = false,
            )
        }

        return MainPageDeferredUiPlan(
            shouldRevealShortDramaToast = true,
            shouldLoadLaunchDialog = true,
        )
    }
}
