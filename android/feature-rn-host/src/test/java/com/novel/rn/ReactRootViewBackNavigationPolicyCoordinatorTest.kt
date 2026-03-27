package com.novel.rn

import com.google.common.truth.Truth.assertThat
import com.novel.rn.bridge.BridgeComponentCachePolicy
import org.junit.Test

class ReactRootViewBackNavigationPolicyCoordinatorTest {

    private val coordinator = ReactRootViewBackNavigationPolicyCoordinator()

    @Test
    fun resolveNavigateBackIntent_marksDestroyOnBackPagesAsClearOnBack() {
        val intent = coordinator.resolveNavigateBackIntent(
            componentName = "SettingsPageComponent",
            destroyOnBack = true,
        )

        assertThat(intent.componentName).isEqualTo("SettingsPageComponent")
        assertThat(intent.cachePolicy).isEqualTo(BridgeComponentCachePolicy.CLEAR_COMPONENT_CACHE)
    }

    @Test
    fun resolveNavigateBackIntent_retainsReusablePages() {
        val intent = coordinator.resolveNavigateBackIntent(
            componentName = "Novel",
            destroyOnBack = false,
        )

        assertThat(intent.componentName).isEqualTo("Novel")
        assertThat(intent.cachePolicy).isEqualTo(BridgeComponentCachePolicy.RETAIN_COMPONENT_CACHE)
    }
}
