package com.novel

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
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

    @Test
    fun onFirstFrameRendered_waitsForFrameGatesBeforeMarkingLifecycleEvents() = runBlocking {
        val events = mutableListOf<String>()
        val coordinator = ComposeMainActivityFirstFrameCoordinator(
            awaitNextFrame = { events += "await-frame" },
        )

        coordinator.runPlan(
            hasReactContext = false,
            markFirstFrameDrawn = { events += "mark-first-frame" },
            createReactContextInBackground = { events += "prewarm-rn" },
            markAppFullyLoaded = { events += "mark-app-fully-loaded" },
        )

        assertThat(events).containsExactly(
            "await-frame",
            "mark-first-frame",
            "prewarm-rn",
            "await-frame",
            "mark-app-fully-loaded",
        ).inOrder()
    }
}
