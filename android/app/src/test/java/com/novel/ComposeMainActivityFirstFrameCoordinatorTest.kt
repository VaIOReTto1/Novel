package com.novel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ComposeMainActivityFirstFrameCoordinatorTest {

    @Test
    fun createPlan_marksLifecycleButSkipsPrewarmWhenReactContextAlreadyReady() {
        val coordinator = ComposeMainActivityFirstFrameCoordinator()

        val plan = coordinator.createPlan(hasReactContext = true)

        assertThat(plan.shouldMarkFirstFrameDrawn).isTrue()
        assertThat(plan.shouldCreateReactContextInBackground).isFalse()
        assertThat(plan.shouldMarkAppFullyLoaded).isTrue()
    }

    @Test
    fun createPlan_consumesPrewarmGateAfterFirstMissingContextPass() {
        val coordinator = ComposeMainActivityFirstFrameCoordinator()

        val firstPlan = coordinator.createPlan(hasReactContext = false)
        val secondPlan = coordinator.createPlan(hasReactContext = false)

        assertThat(firstPlan.shouldCreateReactContextInBackground).isTrue()
        assertThat(secondPlan.shouldCreateReactContextInBackground).isFalse()
    }
}
