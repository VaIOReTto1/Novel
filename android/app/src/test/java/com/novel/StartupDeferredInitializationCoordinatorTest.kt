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

    @Test
    fun createPlanAfterFirstFrame_requiresFirstFrameGateToOpen() {
        val coordinator = StartupDeferredInitializationCoordinator()

        val plan = coordinator.createPlanAfterFirstFrame(firstFrameDrawn = false)

        assertThat(plan.shouldInitializeNetwork).isEqualTo(false)
        assertThat(plan.shouldInitializeSettings).isEqualTo(false)
    }

    @Test
    fun createPlanAfterFirstFrame_exposesOrderedDeferredTaskCatalog() {
        val coordinator = StartupDeferredInitializationCoordinator()

        val plan = coordinator.createPlanAfterFirstFrame()

        assertThat(plan.tasks.map { it.id }).containsExactly(
            StartupDeferredInitializationTaskId.NETWORK,
            StartupDeferredInitializationTaskId.SETTINGS,
        ).inOrder()
        assertThat(plan.tasks.map { it.priority }).containsExactly(
            StartupDeferredInitializationPriority.HIGH,
            StartupDeferredInitializationPriority.MEDIUM,
        ).inOrder()
        assertThat(plan.tasks.map { it.trigger }).containsExactly(
            "after_first_frame",
            "after_first_frame",
        ).inOrder()
        assertThat(plan.tasks.map { it.expectedBenefit }).containsExactly(
            "将网络服务初始化移出首帧前关键路径",
            "将设置服务初始化移出首帧前关键路径",
        ).inOrder()
    }
}
