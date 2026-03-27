package com.novel.page.welfare.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WelfarePageBootstrapCoordinatorTest {

    @Test
    fun createInitialPlan_keepsPageSideWorkButSkipsDuplicateInitializeIntent() {
        val outcome = WelfarePageBootstrapCoordinator(
            viewModelOwnsInitialization = true,
        ).createInitialPlan(alreadyBootstrapped = false)

        assertThat(outcome.shouldInitializePreloadManager).isEqualTo(true)
        assertThat(outcome.shouldDispatchInitializeIntent).isEqualTo(false)
    }

    @Test
    fun createInitialPlan_returnsNoOpAfterBootstrapAlreadyRan() {
        val outcome = WelfarePageBootstrapCoordinator(
            viewModelOwnsInitialization = true,
        ).createInitialPlan(alreadyBootstrapped = true)

        assertThat(outcome.shouldInitializePreloadManager).isEqualTo(false)
        assertThat(outcome.shouldDispatchInitializeIntent).isEqualTo(false)
    }

    @Test
    fun createInitialPlan_canDelegateInitializationToPageWhenNeeded() {
        val outcome = WelfarePageBootstrapCoordinator(
            viewModelOwnsInitialization = false,
        ).createInitialPlan(alreadyBootstrapped = false)

        assertThat(outcome.shouldInitializePreloadManager).isEqualTo(true)
        assertThat(outcome.shouldDispatchInitializeIntent).isEqualTo(true)
    }
}
