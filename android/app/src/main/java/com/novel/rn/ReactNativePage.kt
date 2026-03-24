package com.novel.rn

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.react.bridge.ReactApplicationContext
import com.novel.MainApplication
import com.novel.rn.bridge.BridgeIntent
import com.novel.rn.bridge.BridgeViewModel
import com.novel.rn.settings.SettingsViewModel
import com.novel.utils.TimberLogger

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

    ReactNativePageContent(
        componentName = componentName,
        destroyOnBack = destroyOnBack,
        reactInstanceManager = reactInstanceManager,
        rootView = rootView,
        isContextReady = isContextReady,
        onContextReadyChanged = { isContextReady = it },
        onReactContextReady = { reactContext ->
            settingsViewModel?.initReactContext(reactContext as ReactApplicationContext)
            syncThemeToRN(componentName, settingsViewModel, themeSyncCoordinator)
        },
        onNavigateBack = {
            bridgeViewModel?.sendIntent(BridgeIntent.NavigateBack(componentName))
                ?: TimberLogger.w(tag, "BridgeViewModel未初始化，无法处理返回操作")
        },
    )
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
