package com.novel.rn

import android.annotation.SuppressLint
import com.novel.utils.TimberLogger
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import com.facebook.react.ReactInstanceManager
import com.novel.MainApplication
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.react.bridge.ReactApplicationContext
import com.novel.rn.bridge.BridgeIntent
import com.novel.rn.bridge.BridgeViewModel
import com.novel.rn.settings.SettingsViewModel
import androidx.compose.runtime.LaunchedEffect
import com.novel.ui.theme.NovelColors
import kotlinx.coroutines.launch

/**
 * MVI模块类型枚举
 *
 * 重构后强制要求使用MVI模块，移除NONE选项
 */
enum class MviModuleType {
    SETTINGS,  // 使用设置MVI模块
    BRIDGE,    // 使用桥接MVI模块
    BOTH       // 同时使用设置和桥接MVI模块
}

/**
 * React Native 页面容器组件 - MVI架构版
 *
 * 重构后的特性：
 * - 强制使用MVI架构模块，移除兼容性代码
 * - 使用StateAdapter进行细粒度状态管理
 * - 通过ViewModel进行统一的状态和业务逻辑管理
 * - 自动主题同步和组件生命周期管理
 *
 * @param componentName RN组件名称，决定加载哪个RN页面
 * @param initialProps 传递给RN组件的初始属性
 * @param destroyOnBack 是否在返回时销毁当前RN页面的缓存，默认为false
 * @param mviModuleType 指定使用的MVI模块类型，必须指定
 */
@SuppressLint("VisibleForTests")
@Composable
fun ReactNativePage(
    componentName: String = "Novel",
    initialProps: Map<String, Any> = mapOf("nativeMessage" to "ProfilePage"),
    destroyOnBack: Boolean = false,
    mviModuleType: MviModuleType
) {

    val TAG = "ReactNativePage"
    val context = LocalContext.current
    val mainApplication = context.applicationContext as MainApplication

    // 跟踪RN上下文初始化状态
    val reactInstanceManager = remember { mainApplication.reactNativeHost.reactInstanceManager }
    var isContextReady by remember { mutableStateOf(reactInstanceManager.currentReactContext != null) }

    // 根据MVI模块类型获取对应的ViewModel
    val settingsViewModel: SettingsViewModel? =
        if (mviModuleType == MviModuleType.SETTINGS || mviModuleType == MviModuleType.BOTH) {
            hiltViewModel()
        } else null

    val bridgeViewModel: BridgeViewModel? =
        if (mviModuleType == MviModuleType.BRIDGE || mviModuleType == MviModuleType.BOTH) {
            hiltViewModel()
        } else null



    TimberLogger.d(
        TAG,
        "组件渲染 - componentName: $componentName, isContextReady: $isContextReady, destroyOnBack: $destroyOnBack, mviModule: $mviModuleType"
    )

    // 注册组件到桥接系统
    DisposableEffect(componentName, bridgeViewModel) {
        bridgeViewModel?.registerComponent(componentName)
        onDispose {
            TimberLogger.d(TAG, "组件注销: $componentName")
        }
    }

    // 设置BackHandler（如果启用了返回时销毁）
    if (destroyOnBack) {
        BackHandler(enabled = true) {
            TimberLogger.d(TAG, "BackHandler触发 for $componentName, 准备销毁缓存并返回")

            // 使用Bridge MVI处理返回
            bridgeViewModel?.sendIntent(BridgeIntent.NavigateBack(componentName))
                ?: run {
                    TimberLogger.w(TAG, "BridgeViewModel未初始化，无法处理返回操作")
                }
        }
    }

    // 获取缓存的ReactRootView实例
    val rootView = remember(componentName, initialProps) {
        TimberLogger.d(TAG, "获取缓存的ReactRootView for $componentName")

        // 🎯 预获取主题状态，避免RN端闪烁
        val themeManager = com.novel.ui.theme.ThemeManager.getInstance()
        val currentThemeMode = themeManager.getCurrentThemeMode()
        val currentActualTheme = themeManager.getCurrentActualThemeMode()
        val isDarkMode = currentActualTheme == "dark"
        
        TimberLogger.d(TAG, "预传递主题状态 - mode: $currentThemeMode, actual: $currentActualTheme, isDark: $isDarkMode")

        // 将Map转换为Bundle，并添加主题信息
        val bundle = bundleOf().apply {
            // 添加原有的props
            initialProps.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    else -> putString(key, value.toString())
                }
            }
            
            // 🎯 添加主题状态到props，确保RN组件初始渲染就有正确主题
            putString("initialThemeMode", currentThemeMode)
            putString("initialActualTheme", currentActualTheme)
            putBoolean("initialIsDarkMode", isDarkMode)
        }

        mainApplication.getOrCreateReactRootView(componentName, bundle)
    }

    // 管理RN上下文监听器的生命周期
    DisposableEffect(reactInstanceManager, componentName, settingsViewModel) {
        TimberLogger.d(TAG, "DisposableEffect启动 for $componentName")

        val contextListener = if (!isContextReady) {
            TimberLogger.d(TAG, "添加RN上下文监听器 for $componentName")
            ReactInstanceManager.ReactInstanceEventListener { reactCtx ->
                TimberLogger.d(TAG, "RN上下文状态变更为就绪 for $componentName")
                isContextReady = true
                settingsViewModel?.initReactContext(reactCtx as ReactApplicationContext)
                // RN上下文就绪时，主动同步主题信息
//                syncThemeToRN(componentName, settingsViewModel)
            }.also { listener ->
                reactInstanceManager.addReactInstanceEventListener(listener)
            }
        } else {
            // 如果RN上下文已经就绪，直接同步主题
            val rc = reactInstanceManager.currentReactContext
            if (rc != null) {
                settingsViewModel?.initReactContext(rc as ReactApplicationContext)
//                syncThemeToRN(componentName, settingsViewModel)
            }
            null
        }

        onDispose {
            contextListener?.let { listener ->
                TimberLogger.d(TAG, "移除RN上下文监听器，防止内存泄漏 for $componentName")
                reactInstanceManager.removeReactInstanceEventListener(listener)
            }
        }
    }

    // 使用AndroidView嵌入缓存的ReactRootView
    AndroidView(
        factory = {
            TimberLogger.d(TAG, "AndroidView factory返回缓存的ReactRootView for $componentName")
            rootView
        },
        modifier = Modifier.fillMaxSize().background(NovelColors.NovelBackground)
    )

    // 在RN未就绪时显示加载指示器
    if (!isContextReady) {
        TimberLogger.v(TAG, "显示加载指示器 for $componentName")
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

/**
 * 同步当前主题信息到RN端 - MVI架构版
 *
 * @param componentName 组件名称，用于日志标识
 * @param settingsViewModel 设置ViewModel，使用StateAdapter获取主题状态
 */
private fun syncThemeToRN(componentName: String, settingsViewModel: SettingsViewModel?) {
    try {
        TimberLogger.d("ReactNativePage", "🎯 开始同步主题信息到RN for $componentName")

        // 使用SettingsViewModel的StateAdapter获取主题状态
        val actualTheme = settingsViewModel?.adapter?.getCurrentSnapshot()?.actualTheme ?: run {
            TimberLogger.w(
                "ReactNativePage",
                "SettingsViewModel或StateAdapter未初始化，跳过主题同步 for $componentName"
            )
            return
        }

        TimberLogger.d("ReactNativePage", "当前实际主题: $actualTheme for $componentName")

        // 通过ThemeManager发送主题变更事件到RN
        val themeManager = com.novel.ui.theme.ThemeManager.getInstance()
        themeManager.notifyThemeChangedToRN(actualTheme)
        TimberLogger.d("ReactNativePage", "✅ 主题信息已同步到RN: $actualTheme for $componentName")
    } catch (e: Exception) {
        TimberLogger.e("ReactNativePage", "❌ 同步主题信息到RN失败 for $componentName", e)
    }
}
