package com.novel.rn.bridge

import androidx.compose.runtime.Stable
import com.novel.core.adapter.StateAdapter
import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@Stable
class BridgeStateAdapter(
    stateFlow: StateFlow<BridgeState>
) : StateAdapter<BridgeState>(stateFlow) {

    val isBridgeInitialized = mapState { it.isBridgeInitialized }
    val currentRoute = mapState { it.currentRoute }
    val isCacheOperationInProgress = mapState { it.isCacheOperationInProgress }
    val cachedComponents = mapState { it.cachedComponents }

    fun isComponentCached(componentName: String): Boolean {
        return getCurrentSnapshot().cachedComponents.contains(componentName)
    }

    fun getCachedComponentsCount(): Int {
        return getCurrentSnapshot().cachedComponents.size
    }

    fun isCurrentRoute(route: String): Boolean {
        return getCurrentSnapshot().currentRoute == route
    }

    fun isBridgeReady(): Boolean {
        val state = getCurrentSnapshot()
        return state.isBridgeInitialized && !state.isLoading
    }

    fun canNavigate(): Boolean {
        return isBridgeReady() && !getCurrentSnapshot().isCacheOperationInProgress
    }

    fun canPerformCacheOperation(): Boolean {
        return isBridgeReady() && !getCurrentSnapshot().isCacheOperationInProgress
    }

    fun getBridgeStatusSummary(): String {
        val state = getCurrentSnapshot()
        return buildString {
            append("桥接: ${if (state.isBridgeInitialized) "已初始化" else "未初始化"}")
            if (state.isLoading) append(", 加载中")
            if (state.isCacheOperationInProgress) append(", 缓存操作中")
            if (state.currentRoute != null) append(", 当前路由: ${state.currentRoute}")
            append(", 缓存组件: ${state.cachedComponents.size}个")
        }
    }

    val bridgeInitializationStatus = createConditionFlow { it.isBridgeInitialized }
    val routeChanges = mapState { it.currentRoute }.map { it ?: "unknown" }
    val cachedComponentsCountChanges = mapState { it.cachedComponents.size }
    val cacheOperationStatus = createConditionFlow { it.isCacheOperationInProgress }
}

@Stable
data class BridgeScreenState(
    val isLoading: Boolean,
    val error: String?,
    val isBridgeInitialized: Boolean,
    val currentRoute: String?,
    val canNavigate: Boolean,
    val canPerformCacheOperation: Boolean,
    val cachedComponentsCount: Int,
    val bridgeStatusSummary: String,
    val isCacheOperationInProgress: Boolean
)

class BridgeStateListener(private val adapter: BridgeStateAdapter) {

    fun onBridgeInitializationChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isBridgeInitialized.map { isInitialized ->
            action(isInitialized)
            isInitialized
        }
    }

    fun onRouteChanged(action: (String?) -> Unit): Flow<String?> {
        return adapter.currentRoute.map { route ->
            action(route)
            route
        }
    }

    fun onCacheStateChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isCacheOperationInProgress.map { inProgress ->
            action(inProgress)
            inProgress
        }
    }

    fun onCachedComponentsChanged(action: (PersistentSet<String>) -> Unit): Flow<PersistentSet<String>> {
        return adapter.cachedComponents.map { components ->
            action(components)
            components
        }
    }
}

fun BridgeStateAdapter.toScreenState(): BridgeScreenState {
    return BridgeScreenState(
        isLoading = isCurrentlyLoading(),
        error = getCurrentError(),
        isBridgeInitialized = getCurrentSnapshot().isBridgeInitialized,
        currentRoute = getCurrentSnapshot().currentRoute,
        canNavigate = canNavigate(),
        canPerformCacheOperation = canPerformCacheOperation(),
        cachedComponentsCount = getCachedComponentsCount(),
        bridgeStatusSummary = getBridgeStatusSummary(),
        isCacheOperationInProgress = getCurrentSnapshot().isCacheOperationInProgress
    )
}

fun BridgeStateAdapter.createBridgeListener(): BridgeStateListener {
    return BridgeStateListener(this)
}
