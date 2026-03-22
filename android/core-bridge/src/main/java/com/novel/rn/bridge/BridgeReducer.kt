package com.novel.rn.bridge

import kotlinx.collections.immutable.persistentSetOf
import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.core.logging.CoreLogger

/**
 * 桥接模块状态处理器
 * 
 * 负责处理桥接相关的状态转换：
 * - 导航状态管理
 * - 组件缓存状态更新
 * - 桥接初始化状态
 */
class BridgeReducer : MviReducerWithEffect<BridgeIntent, BridgeState, BridgeEffect> {
    
    companion object {
        private const val TAG = "BridgeReducer"
    }
    
    override fun reduce(
        currentState: BridgeState,
        intent: BridgeIntent
    ): ReduceResult<BridgeState, BridgeEffect> {
        
        CoreLogger.d(TAG, "处理桥接意图: ${intent::class.simpleName}")
        
        return when (intent) {
            is BridgeIntent.InitializeBridge -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true
                    )
                )
            }
            
            is BridgeIntent.CheckBridgeStatus -> {
                ReduceResult(newState = currentState)
            }
            
            is BridgeIntent.NavigateToLogin -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        currentRoute = "login"
                    ),
                    effect = BridgeEffect.NavigateToLogin
                )
            }
            
            is BridgeIntent.NavigateToSettings -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        currentRoute = "settings"
                    ),
                    effect = BridgeEffect.NavigateToSettings
                )
            }
            
            is BridgeIntent.NavigateBack -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        currentRoute = null
                    ),
                    effect = BridgeEffect.NavigateBack
                )
            }
            
            is BridgeIntent.ClearComponentCache -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isCacheOperationInProgress = true,
                        cachedComponents = currentState.cachedComponents.remove(intent.componentName)
                    ),
                    effect = BridgeEffect.ComponentCacheCleared(intent.componentName)
                )
            }
            
            is BridgeIntent.ClearAllComponentCache -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isCacheOperationInProgress = true,
                        cachedComponents = persistentSetOf()
                    ),
                    effect = BridgeEffect.AllComponentCacheCleared
                )
            }
        }
    }
    
    /**
     * 处理异步操作完成后的状态更新
     */
    fun handleAsyncResult(
        currentState: BridgeState,
        result: BridgeAsyncResult
    ): ReduceResult<BridgeState, BridgeEffect> {
        
        return when (result) {
            is BridgeAsyncResult.BridgeInitialized -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        isBridgeInitialized = true
                    ),
                    effect = BridgeEffect.BridgeInitialized
                )
            }
            
            is BridgeAsyncResult.CacheOperationCompleted -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isCacheOperationInProgress = false
                    ),
                    effect = BridgeEffect.ShowToast(result.message)
                )
            }
            
            is BridgeAsyncResult.ComponentRegistered -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        cachedComponents = currentState.cachedComponents.add(result.componentName)
                    )
                )
            }
            
            is BridgeAsyncResult.RouteChanged -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        currentRoute = result.route
                    ),
                    effect = BridgeEffect.RouteChanged(result.route)
                )
            }
            
            is BridgeAsyncResult.Error -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        isCacheOperationInProgress = false,
                        error = result.message
                    ),
                    effect = BridgeEffect.ShowError(result.message)
                )
            }
        }
    }
}

/**
 * 桥接异步操作结果封装
 */
sealed class BridgeAsyncResult {
    object BridgeInitialized : BridgeAsyncResult()
    data class CacheOperationCompleted(val message: String) : BridgeAsyncResult()
    data class ComponentRegistered(val componentName: String) : BridgeAsyncResult()
    data class RouteChanged(val route: String) : BridgeAsyncResult()
    data class Error(val message: String) : BridgeAsyncResult()
}
