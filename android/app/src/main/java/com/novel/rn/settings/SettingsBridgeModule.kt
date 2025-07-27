package com.novel.rn.settings

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.facebook.react.bridge.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.novel.ComposeMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 设置桥接模块
 * 
 * 专门处理设置相关的RN调用，通过SettingsViewModel管理状态：
 * - 主题切换和管理
 * - 缓存计算和清理
 * - 时间设置管理
 * - 自动主题切换
 */
@Stable
class SettingsBridgeModule(
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val TAG = "SettingsBridgeModule"
    }

    override fun getName(): String = "SettingsBridge"

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
     * 切换夜间模式
     */
    @ReactMethod
    fun toggleNightMode(callback: Callback) {
        TimberLogger.d(TAG, "切换夜间模式")
        
        settingsViewModel?.let { viewModel ->
            // 监听Effect来获取结果
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            // 发送Intent
            viewModel.sendIntent(SettingsIntent.ToggleNightMode)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 设置夜间模式
     */
    @ReactMethod
    fun setNightMode(mode: String, callback: Callback) {
        TimberLogger.d(TAG, "设置夜间模式: $mode")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.SetNightMode(mode))
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
     * 设置是否跟随系统主题
     */
    @ReactMethod
    fun setFollowSystemTheme(follow: Boolean, callback: Callback) {
        TimberLogger.d(TAG, "设置跟随系统主题: $follow")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.SetFollowSystemTheme(follow))
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 获取是否跟随系统主题
     */
    @ReactMethod
    fun isFollowSystemTheme(callback: Callback) {
        TimberLogger.d(TAG, "获取跟随系统主题状态")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.isFollowSystemTheme)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 设置自动切换夜间模式
     */
    @ReactMethod
    fun setAutoNightMode(enabled: Boolean, callback: Callback) {
        TimberLogger.d(TAG, "设置自动切换夜间模式: $enabled")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.SetAutoNightMode(enabled))
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 获取是否启用自动切换夜间模式
     */
    @ReactMethod
    fun isAutoNightModeEnabled(callback: Callback) {
        TimberLogger.d(TAG, "获取自动切换夜间模式状态")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.isAutoNightModeEnabled)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 清除所有缓存
     */
    @ReactMethod
    fun clearAllCache(callback: Callback) {
        TimberLogger.d(TAG, "清除所有缓存")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.CacheCleared -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.ClearAllCache)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 计算缓存大小
     */
    @ReactMethod
    fun calculateCacheSize(callback: Callback) {
        TimberLogger.d(TAG, "计算缓存大小")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.CacheCalculated -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.size)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.CalculateCacheSize)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 设置夜间模式时间段
     */
    @ReactMethod
    fun setNightModeTime(startTime: String, endTime: String, callback: Callback) {
        TimberLogger.d(TAG, "设置夜间模式时间段: $startTime - $endTime")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.SetNightModeTime(startTime, endTime))
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 获取夜间模式开始时间
     */
    @ReactMethod
    fun getNightModeStartTime(callback: Callback) {
        TimberLogger.d(TAG, "获取夜间模式开始时间")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.nightModeStartTime)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 获取夜间模式结束时间
     */
    @ReactMethod
    fun getNightModeEndTime(callback: Callback) {
        TimberLogger.d(TAG, "获取夜间模式结束时间")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.nightModeEndTime)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 检查当前时间的主题状态
     */
    @ReactMethod
    fun checkCurrentTimeTheme(callback: Callback) {
        TimberLogger.d(TAG, "检查当前时间的主题状态")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForCallback(viewModel, callback) { effect, callbackInvoked, cb ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(null, effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (callbackInvoked.compareAndSet(false, true)) {
                            cb.invoke(effect.error, null)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.CheckCurrentTimeTheme)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 统一主题切换接口（Promise版本）
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
     * 用户退出登录
     */
    @ReactMethod
    fun logout(promise: Promise) {
        try {
            TimberLogger.d(TAG, "开始执行退出登录")
            
            // 直接发送确认退出登录意图到ViewModel
            settingsViewModel?.let { viewModel ->
                observeEffectForPromise(viewModel, promise) { effect, promiseResolved, p ->
                    when (effect) {
                        is SettingsEffect.LogoutSuccess -> {
                            if (promiseResolved.compareAndSet(false, true)) {
                                p.resolve(effect.message)
                            }
                            true
                        }
                        is SettingsEffect.LogoutError -> {
                            if (promiseResolved.compareAndSet(false, true)) {
                                p.reject("LOGOUT_ERROR", effect.error)
                            }
                            true
                        }
                        else -> false
                    }
                }

                viewModel.sendIntent(SettingsIntent.ConfirmLogout)
            } ?: run {
                promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
            }
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "退出登录失败", e)
            promise.reject("LOGOUT_ERROR", "退出登录失败: ${e.message}", e)
        }
    }
    


    /**
     * 观察Effect并执行回调的辅助方法
     */
    private fun observeEffectForCallback(
        viewModel: SettingsViewModel,
        callback: Callback,
        effectHandler: (SettingsEffect, AtomicBoolean, Callback) -> Boolean
    ) {
        TimberLogger.d(TAG, "设置Effect观察器")
        
        // 使用原子布尔值确保callback只被调用一次
        val callbackInvoked = AtomicBoolean(false)
        
        // 在主线程上启动协程来监听Effect
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 设置超时时间，避免无限等待
                withTimeout(10000) {
                    viewModel.effect.collect { effect ->
                        TimberLogger.d(TAG, "收到Effect: $effect")
                        val shouldStop = effectHandler(effect, callbackInvoked, callback)
                        if (shouldStop) {
                            // 只有当effectHandler返回true时才停止监听
                            return@collect
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                TimberLogger.w(TAG, "Effect监听超时")
                if (callbackInvoked.compareAndSet(false, true)) {
                    callback.invoke("操作超时", null)
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "Effect监听异常", e)
                if (callbackInvoked.compareAndSet(false, true)) {
                    callback.invoke("操作失败: ${e.message}", null)
                }
            }
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