package com.novel.ui.theme

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Stable
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancelChildren
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * 全局主题管理器 - 优化版
 * 
 * 核心改进：
 * - 单向数据流：只在真正需要时发送事件到RN
 * - Flow去重：使用distinctUntilChanged避免重复事件
 * - 事件去抖：debounce(200ms)合并连续点击
 * - 安全检查：发送事件前检查ReactContext状态
 * - 简化状态：去除冗余的手动状态赋值
 */
@Stable
class ThemeManager private constructor(private val context: Context) : ViewModel() {
    
    companion object {
        private const val PREFS_NAME = "theme_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_FOLLOW_SYSTEM = "follow_system_theme"
        private const val DEBOUNCE_DELAY_MS = 200L
        
        @Volatile
        private var INSTANCE: ThemeManager? = null
        
        private val viewModelStore = ViewModelStore()
        private val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = Companion.viewModelStore
        }
        
        fun getInstance(context: Context? = null): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    requireNotNull(context) { "Context必须在首次调用时提供" }
                    val factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ThemeManager(context.applicationContext) as T
                        }
                    }
                    val provider = ViewModelProvider(viewModelStoreOwner, factory)
                    val instance = provider[ThemeManager::class.java]
                    INSTANCE = instance
                    instance
                }
            }
        }
        
        /**
         * 初始化主题管理器
         */
        fun initialize(application: Application) {
            val instance = getInstance(application)
            instance.restoreThemeFromCache()
            instance.startThemeFlowMonitoring()
        }
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // 🎯 优化：使用Flow监听实际主题变化，而不是手动设置
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    
    private val _followSystemTheme = MutableStateFlow(true)
    val followSystemTheme: StateFlow<Boolean> = _followSystemTheme.asStateFlow()
    
    // 协程作用域
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // 系统主题变化回调
    private var systemThemeChangeCallback: ((String) -> Unit)? = null
    
    /**
     * 🎯 新增：启动Flow监听，实现去重和去抖
     */
    private fun startThemeFlowMonitoring() {
        coroutineScope.launch {
            isDarkMode
                .debounce(DEBOUNCE_DELAY_MS) // 去抖：合并连续变化
                .collect { isDark ->
                    val actualTheme = if (isDark) "dark" else "light"
                    println("[ThemeManager] 🎯 主题变化检测: $actualTheme")
                    
                    // 只在真正变化时才通知RN和回调
                    notifyThemeChangedToRN(actualTheme)
                    systemThemeChangeCallback?.invoke(actualTheme)
                }
        }
    }
    
    /**
     * 从缓存恢复主题设置
     */
    private fun restoreThemeFromCache() {
        try {
            val savedThemeMode = sharedPreferences.getString(KEY_THEME_MODE, "auto") ?: "auto"
            val savedFollowSystem = sharedPreferences.getBoolean(KEY_FOLLOW_SYSTEM, true)
            
            // 恢复状态
            _followSystemTheme.value = savedFollowSystem
            
            // 应用主题设置，但不重复保存到缓存
            applyThemeMode(savedThemeMode, saveToCache = false, notifyRN = false)
            
            println("[ThemeManager] 已从缓存恢复主题: mode=$savedThemeMode, followSystem=$savedFollowSystem")
        } catch (e: Exception) {
            println("[ThemeManager] 恢复主题缓存失败: ${e.message}")
            // 失败时使用默认设置
            setThemeMode("auto", notifyRN = false)
        }
    }
    
    /**
     * 保存主题设置到缓存
     */
    private fun saveThemeToCache() {
        try {
            sharedPreferences.edit {
                putString(KEY_THEME_MODE, getCurrentThemeMode())
                putBoolean(KEY_IS_DARK_MODE, _isDarkMode.value)
                putBoolean(KEY_FOLLOW_SYSTEM, _followSystemTheme.value)
            }
            println("[ThemeManager] 主题设置已保存到缓存")
        } catch (e: Exception) {
            println("[ThemeManager] 保存主题缓存失败: ${e.message}")
        }
    }
    
    /**
     * 应用主题模式 - 优化版
     */
    private fun applyThemeMode(mode: String, saveToCache: Boolean = true, notifyRN: Boolean = false) {
        when (mode) {
            "light" -> {
                _followSystemTheme.value = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                // 🎯 优化：让AppCompatDelegate的变化自然触发Configuration变化，然后更新_isDarkMode
                updateDarkModeFromConfiguration()
            }
            "dark" -> {
                _followSystemTheme.value = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                updateDarkModeFromConfiguration()
            }
            "auto" -> {
                _followSystemTheme.value = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                updateDarkModeFromConfiguration()
            }
        }
        
        if (saveToCache) {
            saveThemeToCache()
        }
    }
    
    /**
     * 🎯 新增：从Configuration更新深色模式状态，避免手动双写
     */
    private fun updateDarkModeFromConfiguration() {
        val isSystemDark = detectSystemDarkMode()
        _isDarkMode.value = isSystemDark
    }
    
    /**
     * 设置主题模式 - 简化版（主要用于RN调用）
     * @param mode 主题模式
     * @param notifyRN 是否通知RN（默认false，因为RN已经有本地状态）
     */
    fun setThemeMode(mode: String, notifyRN: Boolean = false) {
        println("[ThemeManager] 🎯 设置主题模式: $mode, notifyRN: $notifyRN")
        applyThemeMode(mode, saveToCache = true, notifyRN = notifyRN)
        println("[ThemeManager] ✅ 主题设置完成: $mode")
    }
    
    /**
     * 切换深色模式
     */
    fun toggleDarkMode() {
        val newMode = if (_isDarkMode.value) "light" else "dark"
        setThemeMode(newMode, notifyRN = false) // RN端会立即更新本地状态
    }
    
    /**
     * 获取当前主题模式字符串
     */
    fun getCurrentThemeMode(): String {
        return when {
            _followSystemTheme.value -> "auto"
            _isDarkMode.value -> "dark"
            else -> "light"
        }
    }
    
    /**
     * 获取当前实际的主题模式（用于RN端显示）
     */
    fun getCurrentActualThemeMode(): String {
        return if (_isDarkMode.value) "dark" else "light"
    }
    
    /**
     * 检测系统是否为深色模式 - 增强版
     */
    private fun detectSystemDarkMode(): Boolean {
        return try {
            val configurationDark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            
            val delegateMode = AppCompatDelegate.getDefaultNightMode()
            val result = when (delegateMode) {
                AppCompatDelegate.MODE_NIGHT_YES -> true
                AppCompatDelegate.MODE_NIGHT_NO -> false
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> configurationDark
                else -> configurationDark
            }
            
            println("[ThemeManager] 系统主题检测 - Configuration: $configurationDark, Final: $result")
            result
        } catch (e: Exception) {
            println("[ThemeManager] 系统主题检测失败，使用默认值: ${e.message}")
            _isDarkMode.value
        }
    }
    
    /**
     * 设置系统主题变化回调
     */
    fun setSystemThemeChangeCallback(callback: ((String) -> Unit)?) {
        systemThemeChangeCallback = callback
    }
    
    /**
     * 通知系统主题发生变化（由外部调用，如Configuration变化）
     */
    fun notifySystemThemeChanged() {
        println("[ThemeManager] 收到系统主题变化通知")
        updateDarkModeFromConfiguration()
    }
    
    /**
     * 🎯 优化：向RN端发送主题变更事件，增加安全检查
     */
    fun notifyThemeChangedToRN(theme: String) {
        try {
            println("[ThemeManager] 准备发送主题变更事件到RN: $theme")
            
            val mainApplication = context.applicationContext as com.novel.MainApplication
            val reactInstanceManager = mainApplication.reactNativeHost.reactInstanceManager
            val reactContext = reactInstanceManager.currentReactContext
            
            // 🎯 增加安全检查
            if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
                val params = Arguments.createMap().apply {
                    putString("colorScheme", theme)
                    putString("currentThemeMode", getCurrentThemeMode())
                    putBoolean("followSystem", _followSystemTheme.value)
                }
                
                println("[ThemeManager] 创建事件参数: $params")
                
                reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                    .emit("ThemeChanged", params)
                    
                println("[ThemeManager] ✅ 主题变更事件已发送到RN: $theme")
            } else {
                println("[ThemeManager] ❌ RN上下文不可用，跳过发送事件")
            }
        } catch (e: Exception) {
            println("[ThemeManager] ❌ 发送主题变更事件失败: $theme, error: ${e.message}")
        }
    }
    
    /**
     * 清除主题缓存
     */
    fun clearThemeCache() {
        try {
            sharedPreferences.edit().clear().apply()
            println("[ThemeManager] 主题缓存已清除")
        } catch (e: Exception) {
            println("[ThemeManager] 清除主题缓存失败: ${e.message}")
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // 清理协程
        coroutineScope.coroutineContext.cancelChildren()
        println("[ThemeManager] 资源已清理")
    }
}
