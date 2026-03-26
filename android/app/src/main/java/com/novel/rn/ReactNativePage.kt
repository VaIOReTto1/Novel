package com.novel.rn

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelStoreOwner
import com.facebook.react.bridge.ReactApplicationContext
import com.novel.rn.bridge.BridgeIntent
import com.novel.rn.bridge.BridgeViewModel
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
        "component render - componentName: $componentName, isContextReady: $isContextReady, destroyOnBack: $destroyOnBack, mviModule: $mviModuleType",
    )

    DisposableEffect(componentName, bridgeViewModel) {
        bridgeViewModel?.registerComponent(componentName)
        onDispose {
            TimberLogger.d(tag, "component disposed: $componentName")
        }
    }

    val rootView = remember(componentName, initialProps) {
        TimberLogger.d(tag, "fetch cached ReactRootView for $componentName")

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
                ?: TimberLogger.w(
                    tag,
                    "BridgeViewModel unavailable, skip navigate back for $componentName",
                )
        },
    )
}

private fun syncThemeToRN(
    componentName: String,
    settingsViewModel: SettingsViewModel?,
    themeSyncCoordinator: ReactNativeThemeSyncCoordinator,
) {
    try {
        TimberLogger.d("ReactNativePage", "start theme sync to RN for $componentName")

        val themeManager = com.novel.ui.theme.ThemeManager.getInstance()
        val settingsSnapshot = settingsViewModel?.adapter?.getCurrentSnapshot()
        val actualTheme = settingsSnapshot?.actualTheme
        val fallbackTheme = themeManager.getCurrentActualThemeMode()
        val preferFallbackTheme = settingsSnapshot?.isLoading == true

        when (
            val syncAction = themeSyncCoordinator.resolveSyncAction(
                actualTheme = actualTheme,
                fallbackTheme = fallbackTheme,
                preferFallbackTheme = preferFallbackTheme,
            )
        ) {
            ReactNativeThemeSyncCoordinator.ThemeSyncAction.Skip -> {
                TimberLogger.w(
                    "ReactNativePage",
                    "SettingsViewModel or actual theme unavailable, skip theme sync for $componentName",
                )
                return
            }
            is ReactNativeThemeSyncCoordinator.ThemeSyncAction.Dispatch -> {
                themeManager.notifyThemeChangedToRN(syncAction.theme)
                TimberLogger.d(
                    "ReactNativePage",
                    "theme synced to RN: ${syncAction.theme} for $componentName",
                )
            }
        }
    } catch (e: Exception) {
        TimberLogger.e("ReactNativePage", "theme sync to RN failed for $componentName", e)
    }
}
