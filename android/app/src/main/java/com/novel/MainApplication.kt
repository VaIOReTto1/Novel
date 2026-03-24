package com.novel

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.compose.runtime.Stable
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.ReactRootView
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader
import com.novel.core.concurrency.OnDemandInitializer
import com.novel.rn.NavigationPackage
import com.novel.rn.ReactNativeHostPathTraceCoordinator
import com.novel.rn.settings.SettingsUtils
import com.novel.ui.theme.ThemeManager
import com.novel.utils.TimberLogger
import com.novel.utils.network.RetrofitClient
import com.novel.utils.network.TokenProvider
import com.novel.utils.network.interceptor.AuthInterceptor
import com.novel.utils.performance.StartupPerformanceMonitor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

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

    private val lazyInitializationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val networkServiceInitializer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OnDemandInitializer { initializeNetworkServiceInternal() }
    }
    private val settingsServiceInitializer by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        OnDemandInitializer { initializeSettingsServiceInternal() }
    }
    private val deferredInitializationCoordinator = StartupDeferredInitializationCoordinator()
    private val reactNativeHostPathTraceCoordinator = ReactNativeHostPathTraceCoordinator()
    private val reactRootViewRegistry by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MainApplicationReactRootViewRegistry(
            application = this,
            reactNativeHost = reactNativeHost,
            traceCoordinator = reactNativeHostPathTraceCoordinator,
            ensureNetworkServiceInitialized = ::ensureNetworkServiceInitialized,
        )
    }

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
        super.onCreate()

        appStartTime = System.currentTimeMillis()
        onCreateStartTime = System.currentTimeMillis()

        startupPerformanceMonitor.startProcessMonitoring()
        startupPerformanceMonitor.onApplicationCreateStart()

        initializeTimber()

        TimberLogger.d(TAG, "===== MainApplication 初始化开始 =====")
        instance = this

        initializeCriticalComponents()

        startupPerformanceMonitor.onApplicationCreateEnd()

        logStartupTime("onCreate完成")
        TimberLogger.i(TAG, "✅ MainApplication 初始化完成")
        TimberLogger.d(TAG, "====================================")
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(
                object : Timber.Tree() {
                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        if (priority >= android.util.Log.WARN) {
                            android.util.Log.println(priority, tag ?: "Novel", message)
                            t?.printStackTrace()
                        }
                    }
                },
            )
        }
    }

    private fun initializeCriticalComponents() {
        TimberLogger.d(TAG, "初始化 ThemeManager...")
        val themeStartTime = System.currentTimeMillis()
        ThemeManager.initialize(this)
        val themeDuration = System.currentTimeMillis() - themeStartTime
        logComponentInitTime("ThemeManager", themeStartTime)
        startupPerformanceMonitor.recordComponentInitTime("ThemeManager", themeDuration)

        TimberLogger.d(TAG, "初始化 SoLoader...")
        val soLoaderStartTime = System.currentTimeMillis()
        SoLoader.init(this, OpenSourceMergedSoMapping)
        val soLoaderDuration = System.currentTimeMillis() - soLoaderStartTime
        logComponentInitTime("SoLoader", soLoaderStartTime)
        startupPerformanceMonitor.recordComponentInitTime("SoLoader", soLoaderDuration)

        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            TimberLogger.d(TAG, "启用 React Native 新架构...")
            val newArchStartTime = System.currentTimeMillis()
            load()
            val newArchDuration = System.currentTimeMillis() - newArchStartTime
            logComponentInitTime("NewArchitecture", newArchStartTime)
            startupPerformanceMonitor.recordComponentInitTime("NewArchitecture", newArchDuration)
        }
    }

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
    }

    private fun initializeNetworkServiceInternal() {
        TimberLogger.d(TAG, "后台初始化 RetrofitClient...")
        val networkStartTime = System.currentTimeMillis()
        try {
            RetrofitClient.init(
                authInterceptor = authInterceptor,
                tokenProvider = tokenProvider,
                gson = gson,
            )
            val networkDuration = System.currentTimeMillis() - networkStartTime
            logComponentInitTime("RetrofitClient", networkStartTime)
            startupPerformanceMonitor.recordComponentInitTime("RetrofitClient", networkDuration)
        } catch (error: Exception) {
            TimberLogger.e(TAG, "网络服务初始化失败", error)
        }
    }

    private fun initializeSettingsServiceInternal() {
        TimberLogger.d(TAG, "后台初始化自动主题切换...")
        val settingsStartTime = System.currentTimeMillis()
        try {
            settingsUtils.initializeAutoThemeSwitch()
            val settingsDuration = System.currentTimeMillis() - settingsStartTime
            logComponentInitTime("SettingsUtils", settingsStartTime)
            startupPerformanceMonitor.recordComponentInitTime("SettingsUtils", settingsDuration)
        } catch (error: Exception) {
            TimberLogger.e(TAG, "设置服务初始化失败", error)
        }
    }

    fun ensureNetworkServiceInitialized() {
        networkServiceInitializer.initializeIfNeeded()
    }

    fun ensureSettingsServiceInitialized() {
        settingsServiceInitializer.initializeIfNeeded()
    }

    override fun onTerminate() {
        super.onTerminate()
        TimberLogger.d(TAG, "MainApplication 终止，清理资源...")

        settingsUtils.cleanup()
        clearAllReactRootViewCache()
        lazyInitializationScope.cancel()
    }

    private fun logStartupTime(milestone: String) {
        val currentTime = System.currentTimeMillis()
        val fromAppStart = currentTime - appStartTime
        val fromOnCreateStart = currentTime - onCreateStartTime
        TimberLogger.i(
            TAG,
            "⏱️ $milestone - 距应用启动 ${fromAppStart}ms, 距 onCreate 开始 ${fromOnCreateStart}ms",
        )
    }

    private fun logComponentInitTime(componentName: String, startTime: Long) {
        val initTime = System.currentTimeMillis() - startTime
        TimberLogger.d(TAG, "⏱️ $componentName 初始化耗时: ${initTime}ms")
    }

    @SuppressLint("VisibleForTests")
    fun getOrCreateReactRootView(
        componentName: String,
        initialProps: Bundle? = null,
    ): ReactRootView {
        return reactRootViewRegistry.getOrCreateReactRootView(
            componentName = componentName,
            initialProps = initialProps,
        )
    }

    fun clearReactRootViewCache(componentName: String) {
        reactRootViewRegistry.clearReactRootViewCache(componentName)
    }

    fun clearAllReactRootViewCache() {
        reactRootViewRegistry.clearAllReactRootViewCache()
    }

    fun markAppFullyLoaded() {
        startupPerformanceMonitor.onAppFullyLoaded()
    }

    fun markFirstFrameDrawn() {
        startupPerformanceMonitor.onFirstFrameDrawn()
        initializeNonCriticalComponentsAfterFirstFrame()
    }

    fun markFirstActivityCreate() {
        startupPerformanceMonitor.onFirstActivityCreate()
    }
}
