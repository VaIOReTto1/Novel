package com.novel

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.novel.ui.theme.ThemeManager
import com.novel.utils.TimberLogger
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ReactActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    /**
     * Returns the name of the main component registered from JavaScript. This is used to schedule
     * rendering of the component.
     */
    override fun getMainComponentName(): String = "Novel"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // 标记首个Activity创建开始
        (application as? MainApplication)?.markFirstActivityCreate()
        
        TimberLogger.d(TAG, "MainActivity onCreate 开始")
        
        super.onCreate(savedInstanceState)
        
        TimberLogger.d(TAG, "MainActivity onCreate 完成")
        
        // 监听主题变更并应用到Activity
        lifecycleScope.launch {
            val themeManager = ThemeManager.getInstance()
            themeManager.isDarkMode.collect {
                // 这里可以添加应用级别的主题变更逻辑
                // 例如状态栏、导航栏的颜色调整
            }
        }
    }

    override fun onResume() {
        super.onResume()
        TimberLogger.d(TAG, "MainActivity onResume")
        
        // 在Activity完全可见后短暂延迟，确保首帧已绘制
        lifecycleScope.launch {
            kotlinx.coroutines.delay(100) // 等待首帧绘制完成
            (application as? MainApplication)?.markFirstFrameDrawn()
            
            // 再延迟一点时间确保应用完全可交互
            kotlinx.coroutines.delay(200)
            (application as? MainApplication)?.markAppFullyLoaded()
        }
    }

    /**
     * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
     * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
     */
    override fun createReactActivityDelegate(): ReactActivityDelegate {
        return object : DefaultReactActivityDelegate(
            this,
            mainComponentName,
            fabricEnabled
        ) {
            // ★ 在这里 override，而不是在 Activity 本身
            override fun getLaunchOptions(): Bundle {
                // 启动时就发送 nativeMessage
                return Bundle().apply {
                    putString(
                        "nativeMessage",
                        intent?.getStringExtra("nativeMessage") ?: "默认消息"
                    )
                }
            }
        }
    }
}
