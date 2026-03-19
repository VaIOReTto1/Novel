package com.novel.rn.bridge.delegate

sealed class NavigationHostResult {
    data class Success(val message: String) : NavigationHostResult()
    data class Failure(val message: String) : NavigationHostResult()
}

class NavigationHostDelegate(
    private val registerComponent: (String) -> Unit,
    private val notifyRouteChanged: (String) -> Unit,
    private val clearComponentCache: (String) -> NavigationHostResult,
    private val clearAllComponentCache: () -> NavigationHostResult
) {

    fun registerComponent(componentName: String) {
        registerComponent.invoke(componentName)
    }

    fun notifyRouteChanged(route: String) {
        notifyRouteChanged.invoke(route)
    }

    fun clearComponentCache(componentName: String): NavigationHostResult {
        return clearComponentCache.invoke(componentName)
    }

    fun clearAllComponentCache(): NavigationHostResult {
        return clearAllComponentCache.invoke()
    }
}
