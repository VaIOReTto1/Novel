package com.novel.rn.settings

import androidx.lifecycle.viewModelScope
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.ui.theme.ThemeManager
import com.novel.rn.settings.SettingsUtils
import com.novel.rn.settings.usecase.ClearCacheUseCase
import com.novel.rn.settings.usecase.ExportUserDataUseCase
import com.novel.rn.settings.usecase.GetUserSettingsUseCase
import com.novel.rn.settings.usecase.ImportUserDataUseCase
import com.novel.rn.settings.usecase.UpdateSettingsUseCase
import com.novel.core.logging.CoreLogger
import com.novel.utils.Store.NovelKeyChain.NovelKeyChain
import com.novel.utils.Store.NovelKeyChain.NovelKeyChainType
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 设置页面ViewModel
 * 
 * 继承BaseMviViewModel，实现设置相关的MVI模式：
 * - 主题管理和切换
 * - 缓存计算和清理
 * - 时间设置管理
 * - 导航操作
 * - 使用UseCase处理业务逻辑
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val exportUserDataUseCase: ExportUserDataUseCase,
    private val importUserDataUseCase: ImportUserDataUseCase,
    private val novelKeyChain: NovelKeyChain,
    private val novelUserDefaults: NovelUserDefaults
) : BaseMviViewModel<SettingsIntent, SettingsState, SettingsEffect>() {
    
    companion object {
        private const val TAG = "SettingsViewModel"
    }
    
    private val settingsReducer = SettingsReducer()

    private var reactContext: ReactApplicationContext? = null
    private var themeManager: ThemeManager? = null

    // ② 暴露给桥接模块调用
    fun initReactContext(rc: ReactApplicationContext) {
        this.reactContext = rc
        initializeThemeManager()
        // 在ReactContext初始化后立即加载设置
        sendIntent(SettingsIntent.LoadCurrentTheme)
    }

    private fun initializeThemeManager() {
        reactContext?.let { context ->
            CoreLogger.d(TAG, "初始化ThemeManager")
            themeManager = ThemeManager.getInstance(context.applicationContext)
            // 设置系统主题变化回调
            themeManager?.setSystemThemeChangeCallback { actualTheme ->
                CoreLogger.d(TAG, "系统主题变化回调: $actualTheme")
                // sendThemeChangeEvent(actualTheme)
            }
            CoreLogger.d(TAG, "ThemeManager初始化完成")
        } ?: run {
            CoreLogger.w(TAG, "ReactContext为空，无法初始化ThemeManager")
        }
    }
    
    /** 状态适配器，提供便利的状态访问方法 */
    val adapter = SettingsStateAdapter(state)
    
    init {
        CoreLogger.d(TAG, "SettingsViewModel初始化")
        // 延迟加载设置，等待ReactContext初始化
    }
    
    override fun createInitialState(): SettingsState {
        return SettingsState()
    }
    
    override fun getReducer(): MviReducer<SettingsIntent, SettingsState> {
        // 返回一个适配器，将MviReducerWithEffect适配为MviReducer
        return object : MviReducer<SettingsIntent, SettingsState> {
            override fun reduce(currentState: SettingsState, intent: SettingsIntent): SettingsState {
                val result = settingsReducer.reduce(currentState, intent)
                // 在这里处理副作用
                result.effect?.let { effect ->
                    sendEffect(effect)
                }
                return result.newState
            }
        }
    }
    
    override fun onIntentProcessed(intent: SettingsIntent, newState: SettingsState) {
        super.onIntentProcessed(intent, newState)
        
        // 处理需要异步操作的Intent
        when (intent) {
            is SettingsIntent.LoadCurrentTheme -> handleLoadCurrentTheme()
            is SettingsIntent.ToggleNightMode -> handleToggleNightMode()
            is SettingsIntent.SetNightMode -> handleSetNightMode(intent.mode)
            is SettingsIntent.SetFollowSystemTheme -> handleSetFollowSystemTheme(intent.follow)
            is SettingsIntent.SetAutoNightMode -> handleSetAutoNightMode(intent.enabled)
            is SettingsIntent.SetNightModeTime -> handleSetNightModeTime(intent.startTime, intent.endTime)
            is SettingsIntent.CalculateCacheSize -> handleCalculateCacheSize()
            is SettingsIntent.ClearAllCache -> handleClearAllCache()
            is SettingsIntent.CheckCurrentTimeTheme -> handleCheckCurrentTimeTheme()
            is SettingsIntent.NavigateToTimedSwitch -> handleNavigateToTimedSwitch()
            is SettingsIntent.NavigateToHelpSupport -> handleNavigateToHelpSupport()
            is SettingsIntent.NavigateToPrivacyPolicy -> handleNavigateToPrivacyPolicy()
            is SettingsIntent.Logout -> handleConfirmLogout()
            SettingsIntent.ConfirmLogout -> handleConfirmLogout()
            else -> {
                // 导航相关的Intent已经由Reducer处理，不需要额外操作
            }
        }
    }

    /**
     * 发送主题变更事件到RN
     */
    private fun sendThemeChangeEvent(theme: String) {
        // 🎯 优化：此方法已弃用，由ThemeManager统一发送事件
        return
        try {
            CoreLogger.d(TAG, "准备发送主题变更事件到RN: $theme")

            reactContext?.let { context ->
                // 获取当前完整的主题状态，确保数据一致性
                val currentState = getCurrentState()
                val isFollowSystem = currentState.isFollowSystemTheme
                val currentThemeMode = currentState.currentThemeMode
                
                val params = Arguments.createMap().apply {
                    putString("colorScheme", theme)
                    putString("currentThemeMode", currentThemeMode)
                    putBoolean("followSystem", isFollowSystem)
                }

                CoreLogger.d(TAG, "创建事件参数: colorScheme = $theme, currentThemeMode = $currentThemeMode, followSystem = $isFollowSystem")

                context
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                    .emit("ThemeChanged", params)

                CoreLogger.d(TAG, "✅ 主题变更事件已发送到RN: $theme (mode: $currentThemeMode, follow: $isFollowSystem)")
            } ?: run {
                CoreLogger.w(TAG, "ReactContext为空，无法发送主题变更事件")
            }
        } catch (e: Exception) {
            CoreLogger.e(TAG, "❌ 发送主题变更事件失败: $theme", e)
        }
    }
    
    private fun handleLoadCurrentTheme() {
        // 使用UseCase异步加载当前设置
        viewModelScope.launch {
            try {
                val settingsData = getUserSettingsUseCase(Unit)
                
                val asyncResult = SettingsAsyncResult.ThemeLoaded(
                    mode = settingsData.currentThemeMode,
                    actualTheme = settingsData.actualTheme,
                    followSystem = settingsData.isFollowSystemTheme,
                    autoEnabled = settingsData.isAutoNightModeEnabled,
                    startTime = settingsData.nightModeStartTime,
                    endTime = settingsData.nightModeEndTime
                )
                
                val reduceResult = settingsReducer.handleAsyncResult(getCurrentState(), asyncResult)
                updateState(reduceResult.newState)
                reduceResult.effect?.let { sendEffect(it) }
                
                CoreLogger.d(TAG, "设置加载成功: $settingsData")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "加载设置失败", e)
                handleAsyncError("加载设置失败: ${e.message}")
            }
        }
    }
    
    private fun handleToggleNightMode() {
        viewModelScope.launch {
            try {
                val updateResult = updateSettingsUseCase(UpdateSettingsUseCase.UpdateParams.ToggleTheme)
                
                // 获取最新的跟随系统主题状态
                val isFollowSystem = themeManager?.followSystemTheme?.value ?: false
                
                // 更新状态，确保所有字段都同步
                val newState = getCurrentState().copy(
                    currentThemeMode = updateResult.newThemeMode ?: getCurrentState().currentThemeMode,
                    actualTheme = updateResult.newActualTheme ?: getCurrentState().actualTheme,
                    isFollowSystemTheme = isFollowSystem,
                    isLoading = false
                )
                updateState(newState)
                
                // 发送成功效果
                sendEffect(SettingsEffect.ShowToast(updateResult.message))
                
                // 通知RN主题变化
                // updateResult.newActualTheme?.let { sendThemeChangeEvent(it) }
                
                CoreLogger.d(TAG, "主题切换成功: ${updateResult.message}, followSystem: $isFollowSystem, newThemeMode: ${updateResult.newThemeMode}")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "切换主题失败", e)
                handleAsyncError("切换主题失败: ${e.message}")
            }
        }
    }
    
    private fun handleSetNightMode(mode: String) {
        viewModelScope.launch {
            try {
                val updateResult = updateSettingsUseCase(UpdateSettingsUseCase.UpdateParams.ThemeMode(mode))
                
                // 获取最新的跟随系统主题状态
                val isFollowSystem = themeManager?.followSystemTheme?.value ?: false
                
                // 更新状态，确保所有字段都同步
                val newState = getCurrentState().copy(
                    currentThemeMode = updateResult.newThemeMode ?: mode,
                    actualTheme = updateResult.newActualTheme ?: getCurrentState().actualTheme,
                    isFollowSystemTheme = isFollowSystem,
                    isLoading = false
                )
                updateState(newState)
                
                // 发送成功效果
                sendEffect(SettingsEffect.ShowToast(updateResult.message))
                
                // 通知RN主题变化
                // updateResult.newActualTheme?.let { sendThemeChangeEvent(it) }
                
                CoreLogger.d(TAG, "设置主题模式成功: $mode, followSystem: $isFollowSystem, newThemeMode: ${updateResult.newThemeMode}")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "设置主题模式失败", e)
                handleAsyncError("设置主题模式失败: ${e.message}")
            }
        }
    }
    
    private fun handleSetFollowSystemTheme(follow: Boolean) {
        viewModelScope.launch {
            try {
                val updateResult = updateSettingsUseCase(UpdateSettingsUseCase.UpdateParams.FollowSystemTheme(follow))
                
                // 更新状态
                val newState = getCurrentState().copy(
                    isFollowSystemTheme = follow,
                    currentThemeMode = updateResult.newThemeMode ?: getCurrentState().currentThemeMode,
                    actualTheme = updateResult.newActualTheme ?: getCurrentState().actualTheme
                )
                updateState(newState)
                
                // 发送成功效果
                sendEffect(SettingsEffect.ShowToast(updateResult.message))
                
                // 通知RN主题变化
                // updateResult.newActualTheme?.let { sendThemeChangeEvent(it) }
                
                CoreLogger.d(TAG, "设置跟随系统主题成功: $follow")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "设置跟随系统主题失败", e)
                handleAsyncError("设置跟随系统主题失败: ${e.message}")
            }
        }
    }
    
    private fun handleSetAutoNightMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val updateResult = updateSettingsUseCase(UpdateSettingsUseCase.UpdateParams.AutoNightMode(enabled))
                
                // 更新状态
                val newState = getCurrentState().copy(
                    isAutoNightModeEnabled = enabled
                )
                updateState(newState)
                
                // 发送成功效果
                sendEffect(SettingsEffect.ShowToast(updateResult.message))
                
                CoreLogger.d(TAG, "设置自动夜间模式成功: $enabled")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "设置自动夜间模式失败", e)
                handleAsyncError("设置自动夜间模式失败: ${e.message}")
            }
        }
    }
    
    private fun handleSetNightModeTime(startTime: String, endTime: String) {
        viewModelScope.launch {
            try {
                val updateResult = updateSettingsUseCase(UpdateSettingsUseCase.UpdateParams.NightModeTime(startTime, endTime))
                
                // 更新状态
                val newState = getCurrentState().copy(
                    nightModeStartTime = startTime,
                    nightModeEndTime = endTime
                )
                updateState(newState)
                
                // 发送成功效果
                sendEffect(SettingsEffect.ShowToast(updateResult.message))
                
                CoreLogger.d(TAG, "设置夜间模式时间成功: $startTime - $endTime")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "设置夜间模式时间失败", e)
                handleAsyncError("设置夜间模式时间失败: ${e.message}")
            }
        }
    }
    
    private fun handleCalculateCacheSize() {
        // 在IO线程执行计算
        viewModelScope.launch {
            try {
                val cacheResult = clearCacheUseCase(ClearCacheUseCase.CacheOperation.CalculateSize)
                
                val asyncResult = SettingsAsyncResult.CacheSizeCalculated(cacheResult.cacheSize ?: "计算失败")
                val reduceResult = settingsReducer.handleAsyncResult(getCurrentState(), asyncResult)
                updateState(reduceResult.newState)
                reduceResult.effect?.let { sendEffect(it) }
                
                CoreLogger.d(TAG, "缓存大小计算成功: ${cacheResult.cacheSize}")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "计算缓存大小失败", e)
                handleAsyncError("计算缓存大小失败: ${e.message}")
            }
        }
    }
    
    private fun handleClearAllCache() {
        // 在主线程执行清理（UseCase内部会切换线程）
        viewModelScope.launch {
            try {
                val cacheResult = clearCacheUseCase(ClearCacheUseCase.CacheOperation.ClearAll)
                
                val asyncResult = SettingsAsyncResult.CacheCleared(cacheResult.message)
                val reduceResult = settingsReducer.handleAsyncResult(getCurrentState(), asyncResult)
                updateState(reduceResult.newState)
                reduceResult.effect?.let { sendEffect(it) }
                
                CoreLogger.d(TAG, "缓存清理成功: ${cacheResult.message}")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "清理缓存失败", e)
                handleAsyncError("清理缓存失败: ${e.message}")
            }
        }
    }
    
    private fun handleCheckCurrentTimeTheme() {
        viewModelScope.launch {
            try {
                if (settingsUtils.isAutoNightModeEnabled() && !settingsUtils.isFollowSystemTheme()) {
                    settingsUtils.startTimeBasedThemeCheck()
                    val currentMode = themeManager?.getCurrentActualThemeMode() ?: "light"
                    
                    val result = SettingsAsyncResult.ThemeChanged(currentMode)
                    val reduceResult = settingsReducer.handleAsyncResult(getCurrentState(), result)
                    updateState(reduceResult.newState)
                    reduceResult.effect?.let { sendEffect(it) }
                    
                    sendEffect(SettingsEffect.ShowToast("主题检查完成，当前主题: $currentMode"))
                } else {
                    val reason = when {
                        !settingsUtils.isAutoNightModeEnabled() -> "自动切换未启用"
                        settingsUtils.isFollowSystemTheme() -> "正在跟随系统主题"
                        else -> "未知原因"
                    }
                    
                    val reduceResult = settingsReducer.handleAsyncResult(
                        getCurrentState(),
                        SettingsAsyncResult.Error("跳过主题检查: $reason")
                    )
                    updateState(reduceResult.newState.copy(isLoading = false))
                    sendEffect(SettingsEffect.ShowToast("跳过主题检查: $reason"))
                }
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "检查当前时间主题失败", e)
                handleAsyncError("检查当前时间主题失败: ${e.message}")
            }
        }
    }
    
    private fun handleNavigateToTimedSwitch() {
        val reduceResult = settingsReducer.reduce(getCurrentState(), SettingsIntent.NavigateToTimedSwitch)
        updateState(reduceResult.newState)
        reduceResult.effect?.let { sendEffect(it) }
    }
    
    private fun handleNavigateToHelpSupport() {
        val reduceResult = settingsReducer.reduce(getCurrentState(), SettingsIntent.NavigateToHelpSupport)
        updateState(reduceResult.newState)
        reduceResult.effect?.let { sendEffect(it) }
    }
    
    private fun handleNavigateToPrivacyPolicy() {
        val reduceResult = settingsReducer.reduce(getCurrentState(), SettingsIntent.NavigateToPrivacyPolicy)
        updateState(reduceResult.newState)
        reduceResult.effect?.let { sendEffect(it) }
    }
    
    private fun handleConfirmLogout() {
        viewModelScope.launch {
            try {
                CoreLogger.d(TAG, "开始执行退出登录流程")
                
                // 清除用户数据
                CoreLogger.d(TAG, "开始清除用户数据")
                clearUserDataDirectly()
                CoreLogger.d(TAG, "用户数据清除完成")
                
                val asyncResult = SettingsAsyncResult.LogoutSuccess("退出登录成功")
                val reduceResult = settingsReducer.handleAsyncResult(getCurrentState(), asyncResult)
                updateState(reduceResult.newState)
                
                CoreLogger.d(TAG, "准备发送LogoutSuccess效果")
                reduceResult.effect?.let { 
                    CoreLogger.d(TAG, "发送效果: $it")
                    sendEffect(it) 
                } ?: run {
                    CoreLogger.w(TAG, "没有效果需要发送")
                }
                
                CoreLogger.d(TAG, "退出登录流程完成")
                
            } catch (e: Exception) {
                CoreLogger.e(TAG, "退出登录失败", e)
                val asyncResult = SettingsAsyncResult.LogoutError("退出登录失败: ${e.message}")
                val reduceResult = settingsReducer.handleAsyncResult(getCurrentState(), asyncResult)
                updateState(reduceResult.newState)
                
                CoreLogger.d(TAG, "准备发送LogoutError效果")
                reduceResult.effect?.let { 
                    CoreLogger.d(TAG, "发送错误效果: $it")
                    sendEffect(it) 
                } ?: run {
                    CoreLogger.w(TAG, "没有错误效果需要发送")
                }
            }
        }
    }
    
    /**
     * 直接清除用户数据的辅助方法
     * 用于在ViewModel不可用时的备用清理
     */
    private fun clearUserDataDirectly() {
        try {
            // 清除加密存储的token
            novelKeyChain.delete(NovelKeyChainType.TOKEN)
            novelKeyChain.delete(NovelKeyChainType.REFRESH_TOKEN)
            
            // 清除用户偏好设置
            novelUserDefaults.clearAll()
            
            CoreLogger.d(TAG, "用户数据清理完成")
        } catch (e: Exception) {
            CoreLogger.e(TAG, "清理用户数据时发生错误", e)
            throw e
        }
    }
    
    private fun handleAsyncError(message: String) {
        val result = SettingsAsyncResult.Error(message)
        val reduceResult = settingsReducer.handleAsyncResult(getCurrentState(), result)
        updateState(reduceResult.newState)
        reduceResult.effect?.let { sendEffect(it) }
    }
    
    // 添加公共方法供桥接模块使用
    fun getStateForBridge(): SettingsState = getCurrentState()
}

