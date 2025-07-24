package com.novel.rn.bridge

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.facebook.react.bridge.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.novel.ComposeMainActivity
import com.novel.MainApplication
import com.novel.utils.NavViewModel

/**
 * 导航桥接模块
 * 
 * 专门处理导航相关的RN调用，通过BridgeViewModel管理状态：
 * - 页面导航操作
 * - 组件缓存管理
 * - 返回操作处理
 */
@Stable
class NavigationBridgeModule(
    @Stable
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val TAG = "NavigationBridgeModule"
    }

    override fun getName(): String = "NavigationBridge"

    private val bridgeViewModel: BridgeViewModel?
        get() = try {
            val activity = currentActivity as? ComposeMainActivity
            activity?.let { 
                ViewModelProvider(it as ViewModelStoreOwner)[BridgeViewModel::class.java]
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "无法获取BridgeViewModel", e)
            null
        }

    /**
     * 导航到登录页面
     */
    @ReactMethod
    fun goToLogin() {
        TimberLogger.d(TAG, "导航到登录页面")
        
        bridgeViewModel?.sendIntent(BridgeIntent.NavigateToLogin) ?: run {
            TimberLogger.w(TAG, "BridgeViewModel未初始化，使用fallback导航")
            // Fallback到直接导航
            Handler(Looper.getMainLooper()).post {
                NavViewModel.navController.value?.navigate("login")
            }
        }
    }

    /**
     * 导航到设置页面
     */
    @ReactMethod
    fun navigateToSettings() {
        TimberLogger.d(TAG, "导航到设置页面")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.NavigateToSettings)
        } ?: run {
            TimberLogger.w(TAG, "BridgeViewModel未初始化，使用fallback导航")
            // Fallback到直接导航
            Handler(Looper.getMainLooper()).post {
                NavViewModel.navController.value?.navigate("settings")
            }
        }
    }

    /**
     * 返回上一页
     */
    @ReactMethod
    fun navigateBack(componentName: String?) {
        TimberLogger.d(TAG, "返回上一页, 组件: $componentName")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.NavigateBack(componentName))
        } ?: run {
            TimberLogger.w(TAG, "BridgeViewModel未初始化，使用fallback导航")
            // Fallback处理
            if (!componentName.isNullOrEmpty()) {
                try {
                    MainApplication.getInstance()?.clearReactRootViewCache(componentName)
                    TimberLogger.d(TAG, "已清理 $componentName 的缓存")
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "清理 $componentName 的缓存失败", e)
                }
            }
            
            Handler(Looper.getMainLooper()).post {
                NavViewModel.navController.value?.popBackStack()
            }
        }
    }

    /**
     * 导航到定时切换页面
     */
    @ReactMethod
    fun navigateToTimedSwitch() {
        TimberLogger.d(TAG, "导航到定时切换页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navController.value?.navigate("timed_switch")
        }
    }

    /**
     * 导航到帮助与支持页面
     */
    @ReactMethod
    fun navigateToHelpSupport() {
        TimberLogger.d(TAG, "导航到帮助与支持页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navController.value?.navigate("help_support")
        }
    }

    /**
     * 导航到隐私政策页面
     */
    @ReactMethod
    fun navigateToPrivacyPolicy() {
        TimberLogger.d(TAG, "导航到隐私政策页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navController.value?.navigate("privacy_policy")
        }
    }

    /**
     * 导航到历史页面
     */
    @ReactMethod
    fun navigateToHistory() {
        TimberLogger.d(TAG, "导航到历史页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToHistory()
        }
    }

    /**
     * 导航到消息页面
     */
    @ReactMethod
    fun navigateToMessage() {
        TimberLogger.d(TAG, "导航到消息页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToMessage()
        }
    }

    /**
     * 导航到成为作家页面
     */
    @ReactMethod
    fun navigateToBecomeWriter() {
        TimberLogger.d(TAG, "导航到成为作家页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToBecomeWriter()
        }
    }

    /**
     * 清理指定组件缓存
     */
    @ReactMethod
    fun clearComponentCache(componentName: String, callback: Callback) {
        TimberLogger.d(TAG, "清理组件缓存: $componentName")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.ClearComponentCache(componentName))
            // 由于RN桥接是同步的，我们暂时直接返回成功
            callback.invoke(null, "已清理 $componentName 的缓存")
        } ?: run {
            try {
                MainApplication.getInstance()?.clearReactRootViewCache(componentName)
                callback.invoke(null, "已清理 $componentName 的缓存")
            } catch (e: Exception) {
                TimberLogger.e(TAG, "清理组件缓存失败", e)
                callback.invoke(e.message, null)
            }
        }
    }

    /**
     * 清理所有组件缓存
     */
    @ReactMethod
    fun clearAllComponentCache(callback: Callback) {
        TimberLogger.d(TAG, "清理所有组件缓存")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.ClearAllComponentCache)
            // 由于RN桥接是同步的，我们暂时直接返回成功
            callback.invoke(null, "已清理所有组件缓存")
        } ?: run {
            try {
                MainApplication.getInstance()?.clearAllReactRootViewCache()
                callback.invoke(null, "已清理所有组件缓存")
            } catch (e: Exception) {
                TimberLogger.e(TAG, "清理所有组件缓存失败", e)
                callback.invoke(e.message, null)
            }
        }
    }

    /**
     * 注册组件到桥接系统
     */
    @ReactMethod
    fun registerComponent(componentName: String) {
        TimberLogger.d(TAG, "注册组件: $componentName")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.registerComponent(componentName)
        }
    }

    /**
     * 通知路由变更
     */
    @ReactMethod
    fun notifyRouteChanged(route: String) {
        TimberLogger.d(TAG, "路由变更: $route")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.notifyRouteChanged(route)
        }
    }

    /**
     * 获取桥接状态
     */
    @ReactMethod
    fun getBridgeStatus(callback: Callback) {
        TimberLogger.d(TAG, "获取桥接状态")
        
        bridgeViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            val status = mapOf(
                "isInitialized" to currentState.isBridgeInitialized,
                "currentRoute" to currentState.currentRoute,
                "cachedComponentsCount" to currentState.cachedComponents.size,
                "isLoading" to currentState.isLoading
            )
            
            val bundle = Arguments.createMap().apply {
                putBoolean("isInitialized", currentState.isBridgeInitialized)
                putString("currentRoute", currentState.currentRoute)
                putInt("cachedComponentsCount", currentState.cachedComponents.size)
                putBoolean("isLoading", currentState.isLoading)
            }
            
            callback.invoke(null, bundle)
        } ?: run {
            val bundle = Arguments.createMap().apply {
                putBoolean("isInitialized", false)
                putString("currentRoute", null)
                putInt("cachedComponentsCount", 0)
                putBoolean("isLoading", false)
            }
            callback.invoke(null, bundle)
        }
    }
} 