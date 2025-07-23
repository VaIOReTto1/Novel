package com.novel

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader
import com.novel.ui.theme.ThemeManager
import com.novel.utils.network.RetrofitClient
import com.novel.utils.network.TokenProvider
import com.novel.utils.network.interceptor.AuthInterceptor
import com.novel.rn.NavigationPackage
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.facebook.react.ReactRootView
import java.util.concurrent.ConcurrentHashMap
import com.facebook.react.bridge.ReactContext
import com.facebook.react.ReactInstanceManager
import com.novel.rn.settings.SettingsUtils
import timber.log.Timber

/**
 * 主应用类
 * 
 * 架构特点：
 * - Hilt依赖注入管理
 * - React Native新架构支持
 * - 全局单例组件初始化
 * 
 * 初始化流程：
 * 1. ThemeManager全局主题管理
 * 2. RetrofitClient网络服务
 * 3. SoLoader原生库加载
 * 4. React Native引擎初始化
 */
@Stable
@HiltAndroidApp
class MainApplication : Application(), ReactApplication {
    
    companion object {
        private const val TAG = "MainApplication"
        private var instance: MainApplication? = null
        
        fun getInstance(): MainApplication? = instance
    }

    @Stable
    @Inject
    lateinit var authInterceptor: AuthInterceptor

    @Stable
    @Inject
    lateinit var tokenProvider: TokenProvider
    
    @Stable
    @Inject
    lateinit var settingsUtils: SettingsUtils
    
    @Stable
    @Inject
    lateinit var gson: com.google.gson.Gson

    // 添加ReactRootView缓存管理
    @Stable
    private val reactRootViewCache = ConcurrentHashMap<String, ReactRootView>()

    @get:Stable
    override val reactNativeHost: ReactNativeHost =
        object : DefaultReactNativeHost(this) {
            override fun getPackages(): List<ReactPackage> =
                PackageList(this).packages.apply {
                    add(NavigationPackage())
                }

            override fun getJSMainModuleName(): String = "index"

            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

            override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
        }

    override val reactHost: ReactHost
        get() = getDefaultReactHost(applicationContext, reactNativeHost)

    override fun onCreate() {
        super.onCreate()
        
        // 初始化Timber日志框架
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // 生产环境可以植入自定义的Tree，比如Crashlytics或其他日志收集服务
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // 在生产环境中，可以将日志发送到远程服务器
                    // 这里只记录错误和警告级别的日志
                    if (priority >= android.util.Log.WARN) {
                        android.util.Log.println(priority, tag ?: "Novel", message)
                        t?.printStackTrace()
                    }
                }
            })
        }
        
        TimberLogger.d(TAG, "===== MainApplication 初始化开始 =====")
        
        instance = this
        
        // 初始化全局主题管理器
        TimberLogger.d(TAG, "初始化ThemeManager...")
        ThemeManager.initialize(this)
        
        // 初始化网络服务
        TimberLogger.d(TAG, "初始化RetrofitClient...")
        RetrofitClient.init(
            authInterceptor = authInterceptor,
            tokenProvider = tokenProvider,
            gson = gson
        )
        
        // 初始化SoLoader
        TimberLogger.d(TAG, "初始化SoLoader...")
        SoLoader.init(this, OpenSourceMergedSoMapping)
        
        // 启用新架构支持
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            TimberLogger.d(TAG, "启用React Native新架构...")
            load()
        }
        
        // 初始化自动主题切换功能
        TimberLogger.d(TAG, "初始化自动主题切换...")
        settingsUtils.initializeAutoThemeSwitch()
        
        TimberLogger.i(TAG, "✅ MainApplication 初始化完成")
        TimberLogger.d(TAG, "====================================")
    }

    override fun onTerminate() {
        super.onTerminate()
        TimberLogger.d(TAG, "MainApplication 终止，清理资源...")
        
        // 清理定时器资源
        settingsUtils.cleanup()
        
        // 清理ReactRootView缓存
        clearAllReactRootViewCache()
    }

    /**
     * 获取或创建ReactRootView实例
     * @param componentName React组件名称
     * @param initialProps 初始属性
     * @return 缓存的或新创建的ReactRootView
     */
    @SuppressLint("VisibleForTests")
    fun getOrCreateReactRootView(
        componentName: String, 
        initialProps: Bundle? = null
    ): ReactRootView {
        return reactRootViewCache.getOrPut(componentName) {
            TimberLogger.d(TAG, "创建新的ReactRootView: $componentName")
            ReactRootView(this).apply {
                setIsFabric(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED)
                
                val rim = reactNativeHost.reactInstanceManager
                if (rim.currentReactContext != null) {
                    TimberLogger.d(TAG, "立即启动React应用: $componentName")
                    startReactApplication(rim, componentName, initialProps)
                } else {
                    TimberLogger.d(TAG, "等待React上下文初始化: $componentName")
                    rim.addReactInstanceEventListener(
                        object : ReactInstanceManager.ReactInstanceEventListener {
                            override fun onReactContextInitialized(context: ReactContext) {
                                TimberLogger.d(TAG, "React上下文就绪，启动应用: $componentName")
                                startReactApplication(rim, componentName, initialProps)
                                rim.removeReactInstanceEventListener(this)
                            }
                        }
                    )
                }
            }
        }
    }
    
    /**
     * 清理指定的ReactRootView缓存
     */
    fun clearReactRootViewCache(componentName: String) {
        reactRootViewCache.remove(componentName)?.let {
            TimberLogger.d(TAG, "清理ReactRootView缓存: $componentName")
        }
    }
    
    /**
     * 清理所有ReactRootView缓存
     */
    fun clearAllReactRootViewCache() {
        reactRootViewCache.clear()
        TimberLogger.d(TAG, "清理所有ReactRootView缓存")
    }
}
