package com.novel.rn.settings

import androidx.compose.runtime.Stable
import com.novel.core.config.RefactorFeatureFlags
import com.novel.utils.TimberLogger
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.novel.ComposeMainActivity
import com.novel.rn.bridge.BridgeCoroutineScopes
import com.novel.rn.bridge.rejectMapped
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 设置桥接模块 - 优化版
 *
 * 核心改进：
 * - 单向数据流：RN告诉原生去切，不再回读
 * - 简化接口：移除getCurrentActualTheme等读取方法
 * - 超时处理：所有Promise接口统一10s超时
 * - 错误处理：防止Promise卡死
 * 
 * 专门处理设置相关的RN调用，通过SettingsViewModel管理状态：
 * - 主题切换和管理（单向）
 * - 缓存计算和清理
 * - 时间设置管理
 * - 自动主题切换
 */
@Stable
class SettingsBridgeModule(
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SettingsBridgeEntryPoint {
        fun bridgeCoroutineScopes(): BridgeCoroutineScopes
        fun refactorFeatureFlags(): RefactorFeatureFlags
    }

    companion object {
        private const val TAG = "SettingsBridgeModule"
        private const val TIMEOUT_SECONDS = 10L // 🎯 统一超时时间：10秒
    }

    override fun getName(): String = "SettingsBridge"

    private val bridgeCoroutineScopes: BridgeCoroutineScopes by lazy {
        EntryPointAccessors.fromApplication(
            reactContext.applicationContext,
            SettingsBridgeEntryPoint::class.java
        ).bridgeCoroutineScopes()
    }

    private val refactorFeatureFlags: RefactorFeatureFlags by lazy {
        EntryPointAccessors.fromApplication(
            reactContext.applicationContext,
            SettingsBridgeEntryPoint::class.java
        ).refactorFeatureFlags()
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
     * 🎯 优化：统一主题切换接口（Promise版本，单向数据流）
     * RN调用后立即resolve，不再等待回传数据
     */
    @ReactMethod
    fun changeTheme(theme: String, promise: Promise) {
        TimberLogger.d(TAG, "🎯 统一主题切换: $theme")

        settingsViewModel?.let { viewModel ->
            bridgeCoroutineScopes.main.launch {
                try {
                    withTimeout(TIMEOUT_SECONDS * 1000) {
                        // 🎯 优化：只发送Intent，不等待Effect
                        viewModel.sendIntent(SettingsIntent.SetNightMode(theme))
                        
                        // 立即resolve，不等待原生回传
                        promise.resolve("主题切换已发起: $theme")
                        TimberLogger.d(TAG, "✅ 主题切换指令已发送: $theme")
                    }
                } catch (e: TimeoutCancellationException) {
                    TimberLogger.w(TAG, "主题切换超时: $theme")
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "THEME_CHANGE_ERROR",
                        defaultMessagePrefix = "主题切换失败",
                        timeoutMessage = "主题切换操作超时",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "主题切换失败: $theme", e)
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "THEME_CHANGE_ERROR",
                        defaultMessagePrefix = "主题切换失败",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            }
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 🎯 优化：切换夜间模式（Promise版本，单向数据流）
     */
    @ReactMethod
    fun toggleNightMode(promise: Promise) {
        TimberLogger.d(TAG, "🎯 切换夜间模式")

        settingsViewModel?.let { viewModel ->
            bridgeCoroutineScopes.main.launch {
                try {
                    withTimeout(TIMEOUT_SECONDS * 1000) {
                        // 🎯 优化：只发送Intent，不等待Effect
                        viewModel.sendIntent(SettingsIntent.ToggleNightMode)
                        
                        // 立即resolve，不等待原生回传
                        promise.resolve("夜间模式切换已发起")
                        TimberLogger.d(TAG, "✅ 夜间模式切换指令已发送")
                    }
                } catch (e: TimeoutCancellationException) {
                    TimberLogger.w(TAG, "夜间模式切换超时")
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "TOGGLE_ERROR",
                        defaultMessagePrefix = "夜间模式切换失败",
                        timeoutMessage = "夜间模式切换操作超时",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "夜间模式切换失败", e)
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "TOGGLE_ERROR",
                        defaultMessagePrefix = "夜间模式切换失败",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            }
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 设置是否跟随系统主题（Promise版本）
     */
    @ReactMethod
    fun setFollowSystemTheme(follow: Boolean, promise: Promise) {
        TimberLogger.d(TAG, "设置跟随系统主题: $follow")

        settingsViewModel?.let { viewModel ->
            bridgeCoroutineScopes.main.launch {
                try {
                    withTimeout(TIMEOUT_SECONDS * 1000) {
                        viewModel.sendIntent(SettingsIntent.SetFollowSystemTheme(follow))
                        promise.resolve("跟随系统主题设置已发起: $follow")
                        TimberLogger.d(TAG, "✅ 跟随系统主题设置已发送: $follow")
                    }
                } catch (e: TimeoutCancellationException) {
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "SETTING_ERROR",
                        defaultMessagePrefix = "设置跟随系统主题失败",
                        timeoutMessage = "设置跟随系统主题操作超时",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "设置跟随系统主题失败", e)
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "SETTING_ERROR",
                        defaultMessagePrefix = "设置跟随系统主题失败",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            }
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 设置自动切换夜间模式（Promise版本）
     */
    @ReactMethod
    fun setAutoNightMode(enabled: Boolean, promise: Promise) {
        TimberLogger.d(TAG, "设置自动切换夜间模式: $enabled")

        settingsViewModel?.let { viewModel ->
            bridgeCoroutineScopes.main.launch {
                try {
                    withTimeout(TIMEOUT_SECONDS * 1000) {
                        viewModel.sendIntent(SettingsIntent.SetAutoNightMode(enabled))
                        promise.resolve("自动切换夜间模式设置已发起: $enabled")
                        TimberLogger.d(TAG, "✅ 自动切换夜间模式设置已发送: $enabled")
                    }
                } catch (e: TimeoutCancellationException) {
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "SETTING_ERROR",
                        defaultMessagePrefix = "设置自动切换夜间模式失败",
                        timeoutMessage = "设置自动切换夜间模式操作超时",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "设置自动切换夜间模式失败", e)
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "SETTING_ERROR",
                        defaultMessagePrefix = "设置自动切换夜间模式失败",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            }
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 清除所有缓存（保留，因为需要返回清理结果）
     */
    @ReactMethod
    fun clearAllCache(promise: Promise) {
        TimberLogger.d(TAG, "清除所有缓存")

        settingsViewModel?.let { viewModel ->
            observeEffectForPromise(viewModel, promise) { effect, promiseResolved, p ->
                when (effect) {
                    is SettingsEffect.CacheCleared -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.resolve(effect.message)
                        }
                        true // 停止监听
                    }

                    is SettingsEffect.ShowError -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.reject("CACHE_ERROR", effect.error)
                        }
                        true // 停止监听
                    }

                    else -> false // 继续监听
                }
            }

            viewModel.sendIntent(SettingsIntent.ClearAllCache)
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 计算缓存大小（保留，因为需要返回计算结果）
     */
    @ReactMethod
    fun calculateCacheSize(promise: Promise) {
        TimberLogger.d(TAG, "计算缓存大小")

        settingsViewModel?.let { viewModel ->
            observeEffectForPromise(viewModel, promise) { effect, promiseResolved, p ->
                when (effect) {
                    is SettingsEffect.CacheCalculated -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.resolve(effect.size)
                        }
                        true // 停止监听
                    }

                    is SettingsEffect.ShowError -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.reject("CACHE_ERROR", effect.error)
                        }
                        true // 停止监听
                    }

                    else -> false // 继续监听
                }
            }

            viewModel.sendIntent(SettingsIntent.CalculateCacheSize)
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 设置夜间模式时间段（Promise版本）
     */
    @ReactMethod
    fun setNightModeTime(startTime: String, endTime: String, promise: Promise) {
        TimberLogger.d(TAG, "设置夜间模式时间段: $startTime - $endTime")

        settingsViewModel?.let { viewModel ->
            bridgeCoroutineScopes.main.launch {
                try {
                    withTimeout(TIMEOUT_SECONDS * 1000) {
                        viewModel.sendIntent(SettingsIntent.SetNightModeTime(startTime, endTime))
                        promise.resolve("夜间模式时间设置已发起: $startTime - $endTime")
                        TimberLogger.d(TAG, "✅ 夜间模式时间设置已发送: $startTime - $endTime")
                    }
                } catch (e: TimeoutCancellationException) {
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "SETTING_ERROR",
                        defaultMessagePrefix = "设置夜间模式时间失败",
                        timeoutMessage = "设置夜间模式时间操作超时",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "设置夜间模式时间失败", e)
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "SETTING_ERROR",
                        defaultMessagePrefix = "设置夜间模式时间失败",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            }
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 检查当前时间的主题状态（Promise版本）
     */
    @ReactMethod
    fun checkCurrentTimeTheme(promise: Promise) {
        TimberLogger.d(TAG, "检查当前时间的主题状态")

        settingsViewModel?.let { viewModel ->
            bridgeCoroutineScopes.main.launch {
                try {
                    withTimeout(TIMEOUT_SECONDS * 1000) {
                        viewModel.sendIntent(SettingsIntent.CheckCurrentTimeTheme)
                        promise.resolve("时间主题检查已发起")
                        TimberLogger.d(TAG, "✅ 时间主题检查已发送")
                    }
                } catch (e: TimeoutCancellationException) {
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "CHECK_ERROR",
                        defaultMessagePrefix = "检查时间主题失败",
                        timeoutMessage = "检查时间主题操作超时",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "检查时间主题失败", e)
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "CHECK_ERROR",
                        defaultMessagePrefix = "检查时间主题失败",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            }
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 用户退出登录（保留，因为需要清理用户数据）
     */
    @ReactMethod
    fun logout(promise: Promise) {
        try {
            TimberLogger.d(TAG, "开始执行退出登录")

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
            promise.rejectMapped(
                throwable = e,
                defaultCode = "LOGOUT_ERROR",
                defaultMessagePrefix = "退出登录失败",
                enabled = refactorFeatureFlags.enableBridgeErrorMapper()
            )
        }
    }

    /**
     * 🎯 保留部分读取接口，用于初始化（但减少使用频率）
     */
    
    /**
     * 获取夜间模式开始时间（仅用于初始化）
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
     * 获取夜间模式结束时间（仅用于初始化）
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
     * 获取是否启用自动切换夜间模式（仅用于初始化）
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
     * 获取是否跟随系统主题（仅用于初始化）
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
     * 观察Effect并执行Promise的辅助方法 - 优化版
     */
    private fun observeEffectForPromise(
        viewModel: SettingsViewModel,
        promise: Promise,
        effectHandler: (SettingsEffect, AtomicBoolean, Promise) -> Boolean
    ) {
        TimberLogger.d(TAG, "设置Effect观察器(Promise)")

        val promiseResolved = AtomicBoolean(false)

        bridgeCoroutineScopes.main.launch {
            try {
                withTimeout(TIMEOUT_SECONDS * 1000) {
                    viewModel.effect.collect { effect ->
                        TimberLogger.d(TAG, "收到Effect: $effect")
                        val shouldStop = effectHandler(effect, promiseResolved, promise)
                        if (shouldStop) {
                            return@collect
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                TimberLogger.w(TAG, "Effect监听超时")
                if (promiseResolved.compareAndSet(false, true)) {
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "EFFECT_ERROR",
                        defaultMessagePrefix = "操作失败",
                        timeoutMessage = "操作超时",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "Effect监听异常", e)
                if (promiseResolved.compareAndSet(false, true)) {
                    promise.rejectMapped(
                        throwable = e,
                        defaultCode = "EFFECT_ERROR",
                        defaultMessagePrefix = "操作失败",
                        enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                    )
                }
            }
        }
    }
}
