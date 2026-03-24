package com.novel.rn

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.novel.rn.bridge.NavigationBridgeModule
import com.novel.rn.bridge.UserBridgeModule
import com.novel.rn.host.ReactNativeModuleRegistry
import com.novel.rn.settings.SettingsBridgeModule
import com.novel.utils.TimberLogger

class NavigationPackage : ReactPackage {

    companion object {
        private const val TAG = "NavigationPackage"
    }

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        TimberLogger.d(TAG, "Create RN native modules for host package")

        val modules = ReactNativeModuleRegistry(
            moduleFactories = listOf(
                { SettingsBridgeModule(reactContext) },
                { NavigationBridgeModule(reactContext) },
                { UserBridgeModule(reactContext) },
            ),
        ).createModules()

        TimberLogger.d(TAG, "RN native modules created: ${modules.size}")
        modules.forEach { module ->
            TimberLogger.d(TAG, "- ${module.name}")
        }

        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        TimberLogger.v(TAG, "No custom ViewManager in NavigationPackage")
        return emptyList()
    }
}
