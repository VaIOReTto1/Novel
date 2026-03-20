package com.novel.rn

import com.novel.utils.TimberLogger
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.novel.rn.bridge.NavigationBridgeModule
import com.novel.rn.settings.SettingsBridgeModule
import com.novel.rn.bridge.UserBridgeModule

/**
 * React Native导航模块包 - MVI架构版
 * 
 * 提供按功能域拆分的桥接模块：
 * - SettingsBridgeModule: 设置相关功能（基于SettingsViewModel）
 * - NavigationBridgeModule: 导航相关功能（基于BridgeViewModel）
 * 
 * 特性：
 * - 按业务域分离，采用MVI架构模式
 * - 通过StateAdapter进行细粒度状态管理
 * - 移除了兼容性代码，统一使用新架构
 */
class NavigationPackage : ReactPackage {
    
    companion object {
        private const val TAG = "NavigationPackage"
    }
    
    /**
     * 创建原生模块列表
     * 
     * @param reactContext RN应用上下文
     * @return 原生模块列表，包含各个功能域的桥接模块
     */
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        TimberLogger.d(TAG, "创建MVI架构桥接原生模块")
        
        val modules = listOf(
            // 设置相关桥接模块（基于SettingsViewModel）
            SettingsBridgeModule(reactContext),
            
            // 导航相关桥接模块（基于BridgeViewModel）
            NavigationBridgeModule(reactContext),
            
            // 用户数据桥接模块
            UserBridgeModule(reactContext)
        )
        
        TimberLogger.d(TAG, "MVI桥接模块创建完成，包含${modules.size}个模块：")
        modules.forEach { module ->
            TimberLogger.d(TAG, "- ${module.name} (MVI架构)")
        }
        
        return modules
    }

    /**
     * 创建ViewManager列表
     * 
     * @param reactContext RN应用上下文
     * @return 空列表，本包不提供自定义ViewManager
     */
    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        TimberLogger.v(TAG, "MVI桥接包无自定义ViewManager")
        return emptyList()
    }
}
