package com.novel.rn.bridge

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect

/**
 * 桥接模块MVI契约
 * 
 * 功能域：
 * - 跨端导航管理
 * - RN组件缓存管理
 * - 系统级操作
 */

// ==================== Intent ====================
sealed class BridgeIntent : MviIntent {
    // 导航相关
    object NavigateToLogin : BridgeIntent()
    object NavigateToSettings : BridgeIntent()
    data class NavigateBack(val componentName: String? = null) : BridgeIntent()
    
    // 组件缓存管理
    data class ClearComponentCache(val componentName: String) : BridgeIntent()
    object ClearAllComponentCache : BridgeIntent()
    
    // 初始化相关
    object InitializeBridge : BridgeIntent()
    object CheckBridgeStatus : BridgeIntent()
}

// ==================== State ====================
@Stable
data class BridgeState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    
    // 桥接状态
    val isBridgeInitialized: Boolean = false,
    val currentRoute: String? = null,
    
    // 缓存状态
    @Stable val cachedComponents: PersistentSet<String> = persistentSetOf(),
    val isCacheOperationInProgress: Boolean = false
) : MviState

// ==================== Effect ====================
sealed class BridgeEffect : MviEffect {
    // 导航操作
    object NavigateToLogin : BridgeEffect()
    object NavigateToSettings : BridgeEffect()
    object NavigateBack : BridgeEffect()
    
    // 缓存操作结果
    data class ComponentCacheCleared(val componentName: String) : BridgeEffect()
    object AllComponentCacheCleared : BridgeEffect()
    
    // 状态通知
    data class ShowToast(val message: String) : BridgeEffect()
    data class ShowError(val error: String) : BridgeEffect()
    
    // 桥接状态变更
    object BridgeInitialized : BridgeEffect()
    data class RouteChanged(val route: String) : BridgeEffect()
}
