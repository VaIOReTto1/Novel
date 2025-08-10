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
import com.novel.rn.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import java.util.concurrent.atomic.AtomicBoolean
import com.novel.rn.settings.SettingsEffect
import com.novel.rn.settings.SettingsIntent

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

    private val settingsViewModel: SettingsViewModel?
        get() = try {
            val activity = currentActivity as? ComposeMainActivity
            activity?.let {
                val vm = ViewModelProvider(it as ViewModelStoreOwner)[SettingsViewModel::class.java]
                vm.initReactContext(reactContext)
                vm
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "无法获取SettingsViewModel", e)
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
     * 导航到推书中心页面
     */
    @ReactMethod
    fun navigateToRecommendBook() {
        TimberLogger.d(TAG, "导航到推书中心页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToRecommendBook()
        }
    }

    /**
     * 导航到我的预约页面
     */
    @ReactMethod
    fun navigateToMyReservation() {
        TimberLogger.d(TAG, "导航到我的预约页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToMyReservation()
        }
    }

    /**
     * 导航到会员中心页面
     */
    @ReactMethod
    fun navigateToMemberCenter() {
        TimberLogger.d(TAG, "导航到会员中心页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToMemberCenter()
        }
    }

    /**
     * 导航到看过的人页面
     */
    @ReactMethod
    fun navigateToViewedUsers() {
        TimberLogger.d(TAG, "导航到看过的人页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToViewedUsers()
        }
    }

    /**
     * 导航到反馈与帮助页面
     */
    @ReactMethod
    fun navigateToFeedbackHelp() {
        TimberLogger.d(TAG, "导航到反馈与帮助页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToFeedbackHelp()
        }
    }

    /**
     * 导航到问题列表页面
     */
    @ReactMethod
    fun navigateToQuestionList() {
        TimberLogger.d(TAG, "导航到问题列表页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToQuestionList()
        }
    }

    /**
     * 导航到问题详情页面
     */
    @ReactMethod
    fun navigateToQuestionDetail() {
        TimberLogger.d(TAG, "导航到问题详情页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToQuestionDetail()
        }
    }

    /**
     * 导航到发表评论页面
     */
    @ReactMethod
    fun navigateToWriteReview(bookId: String?, rating: Double?) {
        TimberLogger.d(TAG, "导航到发表评论页面，bookId=$bookId, rating=$rating")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToWriteReview(bookId, rating?.toInt())
        }
    }

    /**
     * 导航到评论详情页面
     */
    @ReactMethod
    fun navigateToReviewDetail(commentData: String) {
        TimberLogger.d(TAG, "导航到评论详情页面，评论数据: $commentData")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToReviewDetail(commentData)
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

    /**
     * 获取当前实际主题
     */
    @ReactMethod
    fun getCurrentActualTheme(callback: Callback) {
        TimberLogger.d(TAG, "获取当前实际主题")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.actualTheme)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 获取当前夜间模式
     */
    @ReactMethod
    fun getCurrentNightMode(callback: Callback) {
        TimberLogger.d(TAG, "获取当前夜间模式")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.currentThemeMode)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 统一主题切换接口
     */
    @ReactMethod
    fun changeTheme(theme: String, promise: Promise) {
        TimberLogger.d(TAG, "统一主题切换: $theme")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForPromise(viewModel, promise) { effect, promiseResolved, p ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.resolve(effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.reject("THEME_CHANGE_ERROR", effect.error)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.SetNightMode(theme))
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 观察Effect并执行Promise的辅助方法
     */
    private fun observeEffectForPromise(
        viewModel: SettingsViewModel,
        promise: Promise,
        effectHandler: (SettingsEffect, AtomicBoolean, Promise) -> Boolean
    ) {
        TimberLogger.d(TAG, "设置Effect观察器(Promise)")
        
        // 使用原子布尔值确保promise只被调用一次
        val promiseResolved = AtomicBoolean(false)
        
        // 在主线程上启动协程来监听Effect
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 设置超时时间，避免无限等待
                withTimeout(10000) {
                    viewModel.effect.collect { effect ->
                        TimberLogger.d(TAG, "收到Effect: $effect")
                        val shouldStop = effectHandler(effect, promiseResolved, promise)
                        if (shouldStop) {
                            // 只有当effectHandler返回true时才停止监听
                            return@collect
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                TimberLogger.w(TAG, "Effect监听超时")
                if (promiseResolved.compareAndSet(false, true)) {
                    promise.reject("TIMEOUT_ERROR", "操作超时")
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "Effect监听异常", e)
                if (promiseResolved.compareAndSet(false, true)) {
                    promise.reject("EFFECT_ERROR", "操作失败: ${e.message}", e)
                }
            }
        }
    }
}