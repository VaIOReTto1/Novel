package com.novel

import android.content.res.Configuration
import android.os.Bundle
import com.novel.utils.TimberLogger
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.novel.page.component.ImageLoaderService
import com.novel.page.component.LocalImageLoaderService
import com.novel.ui.theme.NovelTheme
import com.novel.ui.theme.ThemeManager
import com.novel.utils.AdaptiveScreen
import com.novel.utils.NavigationSetup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 小说应用纯Compose主Activity
 * 
 * 作为小说阅读应用的主要入口点，负责：
 * - 初始化Compose UI环境和主题系统
 * - 管理React Native实例生命周期
 * - 配置图片加载服务和导航系统
 * - 处理Activity生命周期事件
 * 
 * 采用Hilt进行依赖注入，确保组件间的松耦合
 */
@Stable
@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "ComposeMainActivity"
    }

    /** 图片加载服务 - 用于全局图片缓存和加载优化 */
    @Stable
    @Inject
    lateinit var imageLoaderService: ImageLoaderService

    /** React Native实例管理器 - 用于混合开发中的RN模块管理 */
    @Stable
    private val rim: com.facebook.react.ReactInstanceManager?
        get() = (application as MainApplication).reactNativeHost.reactInstanceManager
    private val reactNativePrewarmCoordinator = ReactNativePrewarmCoordinator()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as? MainApplication)?.markFirstActivityCreate()
        super.onCreate(savedInstanceState)
        TimberLogger.d(TAG, "Activity创建开始")

        val debugRoute = if (BuildConfig.DEBUG) {
            intent?.getStringExtra("debug_route")
        } else {
            null
        }
        
        // 在后台初始化React Native上下文，避免阻塞UI线程
        

        setContent {
            // 应用主题包装器，提供统一的视觉风格
            NovelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 提供图片加载服务给所有子组件
                    CompositionLocalProvider(
                        LocalImageLoaderService provides imageLoaderService
                    ) {
                        // 自适应屏幕组件，处理不同设备尺寸适配
                        AdaptiveScreen{
                            Box(modifier = Modifier.fillMaxSize()) {
                                // 应用导航系统初始化
                                NavigationSetup(debugRoute = debugRoute)
                            }
                        }
                    }
                }
            }
        }
        
        TimberLogger.d(TAG, "Activity创建完成")

        window.decorView.post {
            lifecycleScope.launch {
                delay(100)
                (application as? MainApplication)?.markFirstFrameDrawn()
                if (reactNativePrewarmCoordinator.shouldPrewarmAfterFirstFrame() &&
                    rim?.currentReactContext == null
                ) {
                    rim?.createReactContextInBackground()
                }
                delay(200)
                (application as? MainApplication)?.markAppFullyLoaded()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        TimberLogger.d(TAG, "Activity恢复可见")
        // 恢复React Native实例状态
        rim?.onHostResume(this)
    }

    override fun onPause() {
        super.onPause()
        TimberLogger.d(TAG, "Activity暂停")
        // 暂停React Native实例
        rim?.onHostPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        TimberLogger.d(TAG, "Activity销毁")
        // 清理React Native实例资源
        rim?.onHostDestroy(this)
        
        // 清理ReactRootView缓存
        (application as MainApplication).clearAllReactRootViewCache()
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        TimberLogger.d(TAG, "配置发生变化")
        
        // 检查是否为主题变化
        val uiMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (uiMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                TimberLogger.d(TAG, "系统切换到深色模式")
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                TimberLogger.d(TAG, "系统切换到浅色模式")
            }
        }
        
        // 通知ThemeManager系统主题发生变化
        try {
            val themeManager = ThemeManager.getInstance(this)
            themeManager.notifySystemThemeChanged()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "通知主题变化失败", e)
        }
    }
}
