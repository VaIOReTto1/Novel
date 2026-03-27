package com.novel.rn

import com.novel.rn.bridge.BridgeComponentCachePolicy
import com.novel.rn.bridge.BridgeIntent

class ReactRootViewBackNavigationPolicyCoordinator {

    fun resolveNavigateBackIntent(
        componentName: String,
        destroyOnBack: Boolean,
    ): BridgeIntent.NavigateBack {
        return BridgeIntent.NavigateBack(
            componentName = componentName,
            cachePolicy = if (destroyOnBack) {
                BridgeComponentCachePolicy.CLEAR_COMPONENT_CACHE
            } else {
                BridgeComponentCachePolicy.RETAIN_COMPONENT_CACHE
            },
        )
    }
}
