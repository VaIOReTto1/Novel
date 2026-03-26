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
import com.facebook.react.bridge.ReactApplicationContext
import com.novel.rn.bridge.BridgeIntent
import com.novel.rn.bridge.BridgeViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.novel.rn.host.hostGatewayEntryPoint
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
    val viewModelStoreOwner = context as? ViewModelStoreOwner
    val hostGatewayEntryPoint = remember(context.applicationContext) {
        context.applicationContext.hostGatewayEntryPoint()
    }
    val hostBridgeViewModelGateway = remember(hostGatewayEntryPoint) {
        hostGatewayEntryPoint.hostBridgeViewModelGateway()
    }
    val reactContextWarmupGateway = remember(hostGatewayEntryPoint) {
        hostGatewayEntryPoint.reactContextWarmupGateway()
    }
    val reactRootViewRegistryGateway = remember(hostGatewayEntryPoint) {
        hostGatewayEntryPoint.reactRootViewRegistryGateway()
    }
    val themeSyncCoordinator = remember { ReactNativeThemeSyncCoordinator() }

    val reactInstanceManager = remember {
        reactContextWarmupGateway.reactInstanceManagerOrNull()
    } ?: run {
        TimberLogger.w(tag, "ReactInstanceManager unavailable for $componentName")
        return
    }
    var isContextReady by remember {
        mutableStateOf(reactContextWarmupGateway.hasReactContext())
    }

    val settingsViewModel: SettingsViewModel? =
        if (mviModuleType == MviModuleType.SETTINGS || mviModuleType == MviModuleType.BOTH) {
            remember(viewModelStoreOwner, mviModuleType) {
                hostBridgeViewModelGateway.getSettingsViewModelOrNull(viewModelStoreOwner)
            }
        } else {
            null
        }

    val bridgeViewModel: BridgeViewModel? =
        if (mviModuleType == MviModuleType.BRIDGE || mviModuleType == MviModuleType.BOTH) {
            remember(viewModelStoreOwner, mviModuleType) {
                hostBridgeViewModelGateway.getBridgeViewModelOrNull(viewModelStoreOwner)
            }
        } else {
            null
        }

    TimberLogger.d(
        tag,
        "缁勪欢娓叉煋 - componentName: $componentName, isContextReady: $isContextReady, destroyOnBack: $destroyOnBack, mviModule: $mviModuleType",
    )

    DisposableEffect(componentName, bridgeViewModel) {
        bridgeViewModel?.registerComponent(componentName)
        onDispose {
            TimberLogger.d(tag, "缁勪欢娉ㄩ攢: $componentName")
        }
    }

    val rootView = remember(componentName, initialProps) {
        TimberLogger.d(tag, "鑾峰彇缂撳瓨鐨凴eactRootView for $componentName")

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

        reactRootViewRegistryGateway.getOrCreateReactRootView(componentName, bundle)
    }

    ReactNativePageContent(
        componentName = componentName,
        destroyOnBack = destroyOnBack,
        reactInstanceManager = reactInstanceManager,
        rootView = rootView,
        isContextReady = isContextReady,
        onContextReadyChanged = { isContextReady = it },
        onReactContextReady = { reactContext ->
            hostBridgeViewModelGateway.getSettingsViewModelOrNull(
                owner = viewModelStoreOwner,
                reactContext = reactContext as ReactApplicationContext,
            )
            syncThemeToRN(componentName, settingsViewModel, themeSyncCoordinator)
        },
        onNavigateBack = {
            bridgeViewModel?.sendIntent(BridgeIntent.NavigateBack(componentName))
                ?: TimberLogger.w(tag, "BridgeViewModel鏈垵濮嬪寲锛屾棤娉曞鐞嗚繑鍥炴搷浣?)
        },
    )
}

private fun syncThemeToRN(
    componentName: String,
    settingsViewModel: SettingsViewModel?,
    themeSyncCoordinator: ReactNativeThemeSyncCoordinator,
) {
    try {
        TimberLogger.d("ReactNativePage", "寮€濮嬪悓姝ヤ富棰樹俊鎭埌RN for $componentName")

        val actualTheme = settingsViewModel?.adapter?.getCurrentSnapshot()?.actualTheme
        when (val action = themeSyncCoordinator.resolveSyncAction(actualTheme)) {
            ReactNativeThemeSyncCoordinator.ThemeSyncAction.Skip -> {
                TimberLogger.w(
                    "ReactNativePage",
                    "SettingsViewModel鎴朣tateAdapter鏈垵濮嬪寲锛岃烦杩囦富棰樺悓姝?for $componentName",
                )
                return
            }

            is ReactNativeThemeSyncCoordinator.ThemeSyncAction.Dispatch -> {
                val themeManager = com.novel.ui.theme.ThemeManager.getInstance()
                themeManager.notifyThemeChangedToRN(action.theme)
            }
        }

        TimberLogger.d("ReactNativePage", "褰撳墠瀹為檯涓婚: $actualTheme for $componentName")
        TimberLogger.d("ReactNativePage", "涓婚淇℃伅宸插悓姝ュ埌RN: $actualTheme for $componentName")
    } catch (e: Exception) {
        TimberLogger.e("ReactNativePage", "鍚屾涓婚淇℃伅鍒癛N澶辫触 for $componentName", e)
    }
}
