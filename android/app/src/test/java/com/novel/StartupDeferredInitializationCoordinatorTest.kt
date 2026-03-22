package com.novel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StartupDeferredInitializationCoordinatorTest {

    @Test
    fun createPlanAfterFirstFrame_initializesNonCriticalServicesOnlyOnce() {
        val coordinator = StartupDeferredInitializationCoordinator()

        val firstPlan = coordinator.createPlanAfterFirstFrame()
        val secondPlan = coordinator.createPlanAfterFirstFrame()

        assertThat(firstPlan.shouldInitializeNetwork).isEqualTo(true)
        assertThat(firstPlan.shouldInitializeSettings).isEqualTo(true)
        assertThat(secondPlan.shouldInitializeNetwork).isEqualTo(false)
        assertThat(secondPlan.shouldInitializeSettings).isEqualTo(false)
    }
}
