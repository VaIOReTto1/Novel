package com.novel.rn.bridge

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentSet
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * 桥接模块状态适配器
 * 
 * 为Bridge模块提供统一的状态适配功能：
 * - 细粒度状态订阅
 * - UI友好的便利方法
 * - 类型安全的状态访问
 */
@Stable
class BridgeStateAdapter(
    stateFlow: StateFlow<BridgeState>
) : StateAdapter<BridgeState>(stateFlow) {
    
    // region 基础状态适配
    
    /** 桥接是否已初始化 */
    val isBridgeInitialized = mapState { it.isBridgeInitialized }
    
    /** 当前路由 */
    val currentRoute = mapState { it.currentRoute }
    
    /** 缓存操作是否正在进行 */
    val isCacheOperationInProgress = mapState { it.isCacheOperationInProgress }
    
    /** 已缓存的组件列表 */
    val cachedComponents = mapState { it.cachedComponents }
    
    // endregion
    
    // region 状态查询方法
    
    /** 检查指定组件是否已缓存 */
    fun isComponentCached(componentName: String): Boolean {
        return getCurrentSnapshot().cachedComponents.contains(componentName)
    }
    
    /** 获取缓存组件数量 */
    fun getCachedComponentsCount(): Int {
        return getCurrentSnapshot().cachedComponents.size
    }
    
    /** 检查是否在指定路由 */
    fun isCurrentRoute(route: String): Boolean {
        return getCurrentSnapshot().currentRoute == route
    }
    
    /** 检查桥接是否就绪 */
    fun isBridgeReady(): Boolean {
        val state = getCurrentSnapshot()
        return state.isBridgeInitialized && !state.isLoading
    }
    
    /** 检查是否可以执行导航 */
    fun canNavigate(): Boolean {
        return isBridgeReady() && !getCurrentSnapshot().isCacheOperationInProgress
    }
    
    /** 检查是否可以执行缓存操作 */
    fun canPerformCacheOperation(): Boolean {
        return isBridgeReady() && !getCurrentSnapshot().isCacheOperationInProgress
    }
    
    /** 获取桥接状态摘要 */
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
    
    // endregion
    
    // region 条件流创建
    
    /** 监听桥接初始化状态变化 */
    val bridgeInitializationStatus = createConditionFlow { it.isBridgeInitialized }
    
    /** 监听路由变化 */
    val routeChanges = mapState { it.currentRoute }.map { it ?: "unknown" }
    
    /** 监听缓存组件数量变化 */
    val cachedComponentsCountChanges = mapState { it.cachedComponents.size }
    
    /** 监听缓存操作状态变化 */
    val cacheOperationStatus = createConditionFlow { it.isCacheOperationInProgress }
    
    // endregion
}

/**
 * 状态组合器 - 桥接页面专用
 */
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

/**
 * 状态监听器 - 桥接页面专用
 */
class BridgeStateListener(private val adapter: BridgeStateAdapter) {
    
    /** 监听桥接初始化状态变化 */
    fun onBridgeInitializationChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isBridgeInitialized.map { isInitialized ->
            action(isInitialized)
            isInitialized
        }
    }
    
    /** 监听路由变化 */
    fun onRouteChanged(action: (String?) -> Unit): Flow<String?> {
        return adapter.currentRoute.map { route ->
            action(route)
            route
        }
    }
    
    /** 监听缓存状态变化 */
    fun onCacheStateChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isCacheOperationInProgress.map { inProgress ->
            action(inProgress)
            inProgress
        }
    }
    
    /** 监听缓存组件变化 */
    fun onCachedComponentsChanged(action: (PersistentSet<String>) -> Unit): Flow<PersistentSet<String>> {
        return adapter.cachedComponents.map { components ->
            action(components)
            components
        }
    }
}

/**
 * 扩展函数：为BridgeStateAdapter创建状态组合器
 */
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

/**
 * 扩展函数：为BridgeStateAdapter创建状态监听器
 */
fun BridgeStateAdapter.createBridgeListener(): BridgeStateListener {
    return BridgeStateListener(this)
}
