package com.novel.rn.bridge

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.novel.core.logging.CoreLogger
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.rn.bridge.BridgeComponentCachePolicy
import com.novel.rn.host.HostNavigationGateway
import com.novel.rn.host.ReactRootViewCacheGateway
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class BridgeViewModel @Inject constructor(
    @Stable
    private val hostNavigationGateway: HostNavigationGateway,
    @Stable
    private val reactRootViewCacheGateway: ReactRootViewCacheGateway,
) : BaseMviViewModel<BridgeIntent, BridgeState, BridgeEffect>() {

    companion object {
        private const val TAG = "BridgeViewModel"
    }

    private val bridgeReducer = BridgeReducer()

    val adapter = BridgeStateAdapter(state)

    init {
        CoreLogger.d(TAG, "BridgeViewModel initialized")
        sendIntent(BridgeIntent.InitializeBridge)
    }

    override fun createInitialState(): BridgeState = BridgeState()

    override fun getReducer(): MviReducer<BridgeIntent, BridgeState> {
        return object : MviReducer<BridgeIntent, BridgeState> {
            override fun reduce(currentState: BridgeState, intent: BridgeIntent): BridgeState {
                val result = bridgeReducer.reduce(currentState, intent)
                result.effect?.let(::sendEffect)
                return result.newState
            }
        }
    }

    override fun onIntentProcessed(intent: BridgeIntent, newState: BridgeState) {
        super.onIntentProcessed(intent, newState)

        when (intent) {
            BridgeIntent.InitializeBridge -> handleInitializeBridge()
            BridgeIntent.NavigateToLogin -> handleNavigateToLogin()
            BridgeIntent.NavigateToSettings -> handleNavigateToSettings()
            is BridgeIntent.NavigateBack -> handleNavigateBack(intent.componentName, intent.cachePolicy)
            is BridgeIntent.ClearComponentCache -> handleClearComponentCache(intent.componentName)
            BridgeIntent.ClearAllComponentCache -> handleClearAllComponentCache()
            else -> Unit
        }
    }

    private fun handleInitializeBridge() {
        viewModelScope.launch {
            try {
                delay(500)

                val result = BridgeAsyncResult.BridgeInitialized
                val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
                updateState(reduceResult.newState)
                reduceResult.effect?.let(::sendEffect)

                CoreLogger.d(TAG, "Bridge initialized")
            } catch (error: Exception) {
                CoreLogger.e(TAG, "Bridge initialization failed", error)
                handleAsyncError("桥接初始化失败: ${error.message}")
            }
        }
    }

    private fun handleNavigateToLogin() {
        performNavigation("navigateToLogin") {
            hostNavigationGateway.navigateToRoute("login")
        }
    }

    private fun handleNavigateToSettings() {
        performNavigation("navigateToSettings") {
            hostNavigationGateway.navigateToRoute("settings")
        }
    }

    private fun handleNavigateBack(
        componentName: String?,
        cachePolicy: BridgeComponentCachePolicy,
    ) {
        if (
            cachePolicy == BridgeComponentCachePolicy.CLEAR_COMPONENT_CACHE &&
            !componentName.isNullOrEmpty()
        ) {
            clearComponentCacheInternal(componentName)
        }

        performNavigation("navigateBack", hostNavigationGateway::navigateBack)
    }

    private fun handleClearComponentCache(componentName: String) {
        viewModelScope.launch {
            try {
                clearComponentCacheInternal(componentName)

                val result = BridgeAsyncResult.CacheOperationCompleted("已清理 $componentName 的缓存")
                val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
                updateState(reduceResult.newState)
                reduceResult.effect?.let(::sendEffect)
            } catch (error: Exception) {
                CoreLogger.e(TAG, "Failed to clear component cache", error)
                handleAsyncError("清理组件缓存失败: ${error.message}")
            }
        }
    }

    private fun handleClearAllComponentCache() {
        viewModelScope.launch {
            try {
                reactRootViewCacheGateway.clearAllComponentCache()

                val result = BridgeAsyncResult.CacheOperationCompleted("已清理所有组件缓存")
                val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
                updateState(reduceResult.newState)
                reduceResult.effect?.let(::sendEffect)
            } catch (error: Exception) {
                CoreLogger.e(TAG, "Failed to clear all component cache", error)
                handleAsyncError("清理所有组件缓存失败: ${error.message}")
            }
        }
    }

    fun registerComponent(componentName: String) {
        viewModelScope.launch {
            val result = BridgeAsyncResult.ComponentRegistered(componentName)
            val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
            updateState(reduceResult.newState)
            reduceResult.effect?.let(::sendEffect)

            CoreLogger.d(TAG, "Component registered: $componentName")
        }
    }

    fun notifyRouteChanged(route: String) {
        viewModelScope.launch {
            val result = BridgeAsyncResult.RouteChanged(route)
            val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
            updateState(reduceResult.newState)
            reduceResult.effect?.let(::sendEffect)

            CoreLogger.d(TAG, "Route changed: $route")
        }
    }

    private fun performNavigation(actionName: String, action: () -> Unit) {
        runCatching(action).onFailure { error ->
            CoreLogger.e(TAG, "$actionName failed", error)
            handleAsyncError("导航操作失败: ${error.message}")
        }
    }

    private fun clearComponentCacheInternal(componentName: String) {
        try {
            reactRootViewCacheGateway.clearComponentCache(componentName)
            CoreLogger.d(TAG, "Cleared component cache: $componentName")
        } catch (error: Exception) {
            CoreLogger.e(TAG, "Failed to clear component cache: $componentName", error)
            throw error
        }
    }

    private fun handleAsyncError(message: String) {
        val result = BridgeAsyncResult.Error(message)
        val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
        updateState(reduceResult.newState)
        reduceResult.effect?.let(::sendEffect)
    }

    fun getStateForBridge(): BridgeState = getCurrentState()
}
