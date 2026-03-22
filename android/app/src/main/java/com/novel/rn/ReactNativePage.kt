package com.novel.rn

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.ReactApplicationContext
import com.novel.MainApplication
import com.novel.rn.bridge.BridgeIntent
import com.novel.rn.bridge.BridgeViewModel
import com.novel.rn.settings.SettingsViewModel
import com.novel.ui.theme.NovelColors
import com.novel.utils.TimberLogger

enum class MviModuleType {
    SETTINGS,
    BRIDGE,
    BOTH,
}

@SuppressLint("VisibleForTests")
@Composable
fun ReactNativePage(
    componentName: String = "Novel",
    initialProps: Map<String, Any> = mapOf("nativeMessage" to "ProfilePage"),
    destroyOnBack: Boolean = false,
    mviModuleType: MviModuleType,
) {
    val tag = "ReactNativePage"
    val context = LocalContext.current
    val mainApplication = context.applicationContext as MainApplication
    val themeSyncCoordinator = remember { ReactNativeThemeSyncCoordinator() }

    val reactInstanceManager = remember { mainApplication.reactNativeHost.reactInstanceManager }
    var isContextReady by remember {
        mutableStateOf(reactInstanceManager.currentReactContext != null)
    }

    val settingsViewModel: SettingsViewModel? =
        if (mviModuleType == MviModuleType.SETTINGS || mviModuleType == MviModuleType.BOTH) {
            hiltViewModel()
        } else {
            null
        }

    val bridgeViewModel: BridgeViewModel? =
        if (mviModuleType == MviModuleType.BRIDGE || mviModuleType == MviModuleType.BOTH) {
            hiltViewModel()
        } else {
            null
        }

    TimberLogger.d(
        tag,
        "组件渲染 - componentName: $componentName, isContextReady: $isContextReady, destroyOnBack: $destroyOnBack, mviModule: $mviModuleType",
    )

    DisposableEffect(componentName, bridgeViewModel) {
        bridgeViewModel?.registerComponent(componentName)
        onDispose {
            TimberLogger.d(tag, "组件注销: $componentName")
        }
    }

    if (destroyOnBack) {
        BackHandler(enabled = true) {
            TimberLogger.d(tag, "BackHandler触发 for $componentName, 准备销毁缓存并返回")
            bridgeViewModel?.sendIntent(BridgeIntent.NavigateBack(componentName))
                ?: TimberLogger.w(tag, "BridgeViewModel未初始化，无法处理返回操作")
        }
    }

    val rootView = remember(componentName, initialProps) {
        TimberLogger.d(tag, "获取缓存的ReactRootView for $componentName")

        val themeManager = com.novel.ui.theme.ThemeManager.getInstance()
        val currentThemeMode = themeManager.getCurrentThemeMode()
        val currentActualTheme = themeManager.getCurrentActualThemeMode()
        val isDarkMode = currentActualTheme == "dark"

        val bundle = bundleOf().apply {
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
            putString("initialThemeMode", currentThemeMode)
            putString("initialActualTheme", currentActualTheme)
            putBoolean("initialIsDarkMode", isDarkMode)
        }

        mainApplication.getOrCreateReactRootView(componentName, bundle)
    }

    DisposableEffect(reactInstanceManager, componentName, settingsViewModel, themeSyncCoordinator) {
        TimberLogger.d(tag, "DisposableEffect启动 for $componentName")

        val contextListener = if (!isContextReady) {
            TimberLogger.d(tag, "添加RN上下文监听器 for $componentName")
            ReactInstanceManager.ReactInstanceEventListener { reactCtx ->
                TimberLogger.d(tag, "RN上下文状态变更为就绪 for $componentName")
                isContextReady = true
                settingsViewModel?.initReactContext(reactCtx as ReactApplicationContext)
                syncThemeToRN(componentName, settingsViewModel, themeSyncCoordinator)
            }.also { listener ->
                reactInstanceManager.addReactInstanceEventListener(listener)
            }
        } else {
            val reactContext = reactInstanceManager.currentReactContext
            if (reactContext != null) {
                settingsViewModel?.initReactContext(reactContext as ReactApplicationContext)
                syncThemeToRN(componentName, settingsViewModel, themeSyncCoordinator)
            }
            null
        }

        onDispose {
            contextListener?.let { listener ->
                TimberLogger.d(tag, "移除RN上下文监听器，防止内存泄漏 for $componentName")
                reactInstanceManager.removeReactInstanceEventListener(listener)
            }
        }
    }

    AndroidView(
        factory = {
            TimberLogger.d(tag, "AndroidView factory返回缓存的ReactRootView for $componentName")
            rootView
        },
        modifier = Modifier
            .fillMaxSize()
            .background(NovelColors.NovelBackground),
    )

    if (!isContextReady) {
        TimberLogger.v(tag, "显示加载指示器 for $componentName")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

private fun syncThemeToRN(
    componentName: String,
    settingsViewModel: SettingsViewModel?,
    themeSyncCoordinator: ReactNativeThemeSyncCoordinator,
) {
    try {
        TimberLogger.d("ReactNativePage", "开始同步主题信息到RN for $componentName")

        val actualTheme = settingsViewModel?.adapter?.getCurrentSnapshot()?.actualTheme
        if (!themeSyncCoordinator.syncActualTheme(actualTheme) { theme ->
                val themeManager = com.novel.ui.theme.ThemeManager.getInstance()
                themeManager.notifyThemeChangedToRN(theme)
            }
        ) {
            TimberLogger.w(
                "ReactNativePage",
                "SettingsViewModel或StateAdapter未初始化，跳过主题同步 for $componentName",
            )
            return
        }

        TimberLogger.d("ReactNativePage", "当前实际主题: $actualTheme for $componentName")
        TimberLogger.d("ReactNativePage", "主题信息已同步到RN: $actualTheme for $componentName")
    } catch (e: Exception) {
        TimberLogger.e("ReactNativePage", "同步主题信息到RN失败 for $componentName", e)
    }
}
