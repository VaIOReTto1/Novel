package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderDebugScenarioCoordinatorTest {

    private val coordinator = ReaderDebugScenarioCoordinator()

    @Test
    fun createAutoFlipPlan_skipsWhenScenarioDirectionMissing() {
        val plan = coordinator.createAutoFlipPlan(
            isInitialized = true,
            hasCurrentPage = true,
            direction = null,
        )

        assertThat(plan.shouldTriggerAutoFlip).isFalse()
        assertThat(plan.direction).isNull()
    }

    @Test
    fun createAutoFlipPlan_requiresInitializedReaderAndCurrentPage() {
        assertThat(
            coordinator.createAutoFlipPlan(
                isInitialized = false,
                hasCurrentPage = true,
                direction = FlipDirection.NEXT,
            ).shouldTriggerAutoFlip,
        ).isFalse()

        assertThat(
            coordinator.createAutoFlipPlan(
                isInitialized = true,
                hasCurrentPage = false,
                direction = FlipDirection.NEXT,
            ).shouldTriggerAutoFlip,
        ).isFalse()
    }

    @Test
    fun createAutoFlipPlan_triggersOnlyOnceForConfiguredDirection() {
        val firstPlan = coordinator.createAutoFlipPlan(
            isInitialized = true,
            hasCurrentPage = true,
            direction = FlipDirection.NEXT,
        )
        val secondPlan = coordinator.createAutoFlipPlan(
            isInitialized = true,
            hasCurrentPage = true,
            direction = FlipDirection.NEXT,
        )

        assertThat(firstPlan.shouldTriggerAutoFlip).isTrue()
        assertThat(firstPlan.direction).isEqualTo(FlipDirection.NEXT)
        assertThat(secondPlan.shouldTriggerAutoFlip).isFalse()
    }
}
