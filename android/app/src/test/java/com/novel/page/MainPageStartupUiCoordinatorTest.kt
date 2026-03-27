package com.novel.page

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainPageStartupUiCoordinatorTest {

    private val coordinator = MainPageStartupUiCoordinator()

    @Test
    fun createInitialUiState_hidesDeferredOverlaysBeforeFirstFrame() {
        val state = coordinator.createInitialUiState()

        assertThat(state.showShortDramaToast).isFalse()
        assertThat(state.shouldLoadLaunchDialogAfterFirstFrame).isTrue()
    }

    @Test
    fun createAfterFirstFramePlan_revealsDeferredUiOnlyOnce() {
        val firstPlan = coordinator.createAfterFirstFramePlan(hasRevealedDeferredUi = false)
        val secondPlan = coordinator.createAfterFirstFramePlan(hasRevealedDeferredUi = true)

        assertThat(firstPlan.shouldRevealShortDramaToast).isTrue()
        assertThat(firstPlan.shouldLoadLaunchDialog).isTrue()
        assertThat(secondPlan.shouldRevealShortDramaToast).isFalse()
        assertThat(secondPlan.shouldLoadLaunchDialog).isFalse()
    }
}
