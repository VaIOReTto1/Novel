package com.novel.rn.bridge.facade

import com.novel.rn.bridge.BridgeIntent

class DefaultNavigationBridgeFacade(
    private val bridgeIntentSink: ((BridgeIntent) -> Unit)?,
    private val navigateToRoute: (String) -> Unit,
    private val navigateBack: () -> Unit,
    private val clearComponentCache: (String) -> Unit
) {

    fun goToLogin() {
        bridgeIntentSink?.invoke(BridgeIntent.NavigateToLogin)
            ?: navigateToRoute("login")
    }

    fun navigateToSettings() {
        bridgeIntentSink?.invoke(BridgeIntent.NavigateToSettings)
            ?: navigateToRoute("settings")
    }

    fun navigateBack(componentName: String?) {
        bridgeIntentSink?.invoke(BridgeIntent.NavigateBack(componentName))
            ?: run {
                if (!componentName.isNullOrEmpty()) {
                    clearComponentCache(componentName)
                }
                navigateBack()
            }
    }
}
