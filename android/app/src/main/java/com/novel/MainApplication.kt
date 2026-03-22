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
import com.novel.core.concurrency.OnDemandInitializer
import com.novel.rn.settings.SettingsUtils
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import com.novel.utils.performance.StartupPerformanceMonitor

/**
 * 主应用类
 * 
 * 架构特点：
 * - Hilt依赖注入管理
 * - React Native新架构支持
 * - 全局单例组件初始化
 * - 冷启动优化：延迟初始化策略
 * - 启动性能监控
 * 
 * 初始化流程：
 * 1. 关键路径：Timber、ThemeManager、SoLoader（主线程）
 * 2. 非关键路径：RetrofitClient、SettingsUtils（后台线程延迟初始化）
 * 3. React Native引擎按需初始化
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

    @Stable
    @Inject
    lateinit var startupPerformanceMonitor: StartupPerformanceMonitor

    // 添加ReactRootView缓存管理
    @Stable
    private val reactRootViewCache = ConcurrentHashMap<String, ReactRootView>()

    // 冷启动优化：延迟初始化管理
    private val lazyInitializationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val networkServiceInitializer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OnDemandInitializer { initializeNetworkServiceInternal() }
    }
    private val settingsServiceInitializer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OnDemandInitializer { initializeSettingsServiceInternal() }
    }
    private val deferredInitializationCoordinator = StartupDeferredInitializationCoordinator()
    
    // 启动时间监控
    private var appStartTime: Long = 0
    private var onCreateStartTime: Long = 0

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
        // Hilt 在 super.onCreate() 中执行注入，因此必须先调用它
        super.onCreate()

        // 现在可以安全地访问注入的依赖项
        appStartTime = System.currentTimeMillis()
        onCreateStartTime = System.currentTimeMillis()
        
        // 开始启动性能监控
        startupPerformanceMonitor.startProcessMonitoring()
        startupPerformanceMonitor.onApplicationCreateStart()
        
        // 初始化Timber日志框架（关键路径）
        initializeTimber()
        
        TimberLogger.d(TAG, "===== MainApplication 初始化开始 =====")
        instance = this
        
        // 关键路径初始化（主线程，阻塞式）
        initializeCriticalComponents()
        
        // 非关键路径初始化（后台线程，延迟式）
        
        
        // 标记Application onCreate完成
        startupPerformanceMonitor.onApplicationCreateEnd()
        
        logStartupTime("onCreate完成")
        TimberLogger.i(TAG, "✅ MainApplication 初始化完成")
        TimberLogger.d(TAG, "====================================")
    }

    /**
     * 初始化Timber日志框架
     */
    private fun initializeTimber() {
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
    }

    /**
     * 初始化关键组件（主线程，启动必需）
     */
    private fun initializeCriticalComponents() {
        // 初始化全局主题管理器（影响UI，必须主线程）
        TimberLogger.d(TAG, "初始化ThemeManager...")
        val themeStartTime = System.currentTimeMillis()
        ThemeManager.initialize(this)
        val themeDuration = System.currentTimeMillis() - themeStartTime
        logComponentInitTime("ThemeManager", themeStartTime)
        startupPerformanceMonitor.recordComponentInitTime("ThemeManager", themeDuration)
        
        // 初始化SoLoader（影响React Native，必须主线程）
        TimberLogger.d(TAG, "初始化SoLoader...")
        val soLoaderStartTime = System.currentTimeMillis()
        SoLoader.init(this, OpenSourceMergedSoMapping)
        val soLoaderDuration = System.currentTimeMillis() - soLoaderStartTime
        logComponentInitTime("SoLoader", soLoaderStartTime)
        startupPerformanceMonitor.recordComponentInitTime("SoLoader", soLoaderDuration)
        
        // 启用新架构支持（必须主线程）
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            TimberLogger.d(TAG, "启用React Native新架构...")
            val newArchStartTime = System.currentTimeMillis()
            load()
            val newArchDuration = System.currentTimeMillis() - newArchStartTime
            logComponentInitTime("NewArchitecture", newArchStartTime)
            startupPerformanceMonitor.recordComponentInitTime("NewArchitecture", newArchDuration)
        }
    }

    /**
     * 异步初始化非关键组件（后台线程，延迟加载）
     */
    private fun initializeNonCriticalComponentsAfterFirstFrame() {
        val plan = deferredInitializationCoordinator.createPlanAfterFirstFrame()
        if (plan.shouldInitializeNetwork) {
            lazyInitializationScope.launch {
                ensureNetworkServiceInitialized()
            }
        }
        if (plan.shouldInitializeSettings) {
            lazyInitializationScope.launch {
                ensureSettingsServiceInitialized()
            }
        }
        /*
            // 延迟初始化网络服务（非启动必需）
            delay(100) // 给主线程一些喘息时间
            ensureNetworkServiceInitialized()
            
            // 延迟初始化设置服务（非启动必需）
            delay(50)
            ensureSettingsServiceInitialized()
        }
        */
    }

    /**
     * 初始化网络服务（后台线程）
     */
    private fun initializeNetworkServiceInternal() {
        TimberLogger.d(TAG, "后台初始化RetrofitClient...")
        val networkStartTime = System.currentTimeMillis()
        try {
            RetrofitClient.init(
                authInterceptor = authInterceptor,
                tokenProvider = tokenProvider,
                gson = gson
            )
            val networkDuration = System.currentTimeMillis() - networkStartTime
            logComponentInitTime("RetrofitClient", networkStartTime)
            startupPerformanceMonitor.recordComponentInitTime("RetrofitClient", networkDuration)
        } catch (e: Exception) {
            TimberLogger.e(TAG, "网络服务初始化失败", e)
        }
    }

    /**
     * 初始化设置服务（后台线程）
     */
    private fun initializeSettingsServiceInternal() {
        TimberLogger.d(TAG, "后台初始化自动主题切换...")
        val settingsStartTime = System.currentTimeMillis()
        try {
            settingsUtils.initializeAutoThemeSwitch()
            val settingsDuration = System.currentTimeMillis() - settingsStartTime
            logComponentInitTime("SettingsUtils", settingsStartTime)
            startupPerformanceMonitor.recordComponentInitTime("SettingsUtils", settingsDuration)
        } catch (e: Exception) {
            TimberLogger.e(TAG, "设置服务初始化失败", e)
        }
    }

    /**
     * 确保网络服务已初始化（按需初始化）
     */
    fun ensureNetworkServiceInitialized() {
        networkServiceInitializer.initializeIfNeeded()
    }

    /**
     * 确保设置服务已初始化（按需初始化）
     */
    fun ensureSettingsServiceInitialized() {
        settingsServiceInitializer.initializeIfNeeded()
    }

    override fun onTerminate() {
        super.onTerminate()
        TimberLogger.d(TAG, "MainApplication 终止，清理资源...")
        
        // 清理定时器资源
        settingsUtils.cleanup()
        
        // 清理ReactRootView缓存
        clearAllReactRootViewCache()
        
        // 清理协程作用域
        lazyInitializationScope.cancel()
    }

    /**
     * 记录启动时间
     */
    private fun logStartupTime(milestone: String) {
        val currentTime = System.currentTimeMillis()
        val fromAppStart = currentTime - appStartTime
        val fromOnCreateStart = currentTime - onCreateStartTime
        TimberLogger.i(TAG, "⏱️ $milestone - 距应用启动: ${fromAppStart}ms, 距onCreate开始: ${fromOnCreateStart}ms")
    }

    /**
     * 记录组件初始化时间
     */
    private fun logComponentInitTime(componentName: String, startTime: Long) {
        val initTime = System.currentTimeMillis() - startTime
        TimberLogger.d(TAG, "⏱️ $componentName 初始化耗时: ${initTime}ms")
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
        // 确保网络服务已初始化（React Native组件可能需要网络）
        ensureNetworkServiceInitialized()

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

    /**
     * 标记应用完全加载完成
     * 应在首个Activity完全初始化并显示后调用
     */
    fun markAppFullyLoaded() {
        startupPerformanceMonitor.onAppFullyLoaded()
    }

    /**
     * 标记首帧绘制完成
     * 应在首个Activity的首帧绘制完成后调用
     */
    fun markFirstFrameDrawn() {
        startupPerformanceMonitor.onFirstFrameDrawn()
        initializeNonCriticalComponentsAfterFirstFrame()
    }

    /**
     * 标记首个Activity创建
     * 应在MainActivity.onCreate中调用
     */
    fun markFirstActivityCreate() {
        startupPerformanceMonitor.onFirstActivityCreate()
    }
}
