package com.novel.rn.bridge.delegate

import com.novel.rn.bridge.BridgeState
import com.novel.rn.settings.SettingsState

data class BridgeStatusSnapshot(
    val isInitialized: Boolean,
    val currentRoute: String?,
    val cachedComponentsCount: Int,
    val isLoading: Boolean
)

class NavigationQueryDelegate {

    fun getBridgeStatus(currentState: BridgeState?): BridgeStatusSnapshot {
        return BridgeStatusSnapshot(
            isInitialized = currentState?.isBridgeInitialized ?: false,
            currentRoute = currentState?.currentRoute,
            cachedComponentsCount = currentState?.cachedComponents?.size ?: 0,
            isLoading = currentState?.isLoading ?: false
        )
    }

    fun getCurrentActualTheme(currentState: SettingsState?): String? {
        return currentState?.actualTheme
    }

    fun getCurrentNightMode(currentState: SettingsState?): String? {
        return currentState?.currentThemeMode
    }
}
