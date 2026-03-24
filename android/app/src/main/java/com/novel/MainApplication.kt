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
    private val startupOrchestrator = MainApplicationStartupOrchestrator()
    private val startupLifecycleReporter by lazy(LazyThreadSafetyMode.NONE) {
        MainApplicationStartupLifecycleReporter(
            onFirstActivityCreate = startupPerformanceMonitor::onFirstActivityCreate,
            onFirstFrameDrawn = startupPerformanceMonitor::onFirstFrameDrawn,
            onAppFullyLoaded = startupPerformanceMonitor::onAppFullyLoaded,
            afterFirstFrame = ::initializeNonCriticalComponentsAfterFirstFrame,
        )
    }
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

        TimberLogger.d(TAG, "===== MainApplication initialization started =====")
        instance = this

        initializeCriticalComponents()

        startupPerformanceMonitor.onApplicationCreateEnd()

        logStartupTime("onCreate completed")
        TimberLogger.i(TAG, "MainApplication initialization completed")
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
        startupOrchestrator.initializeCriticalComponents(
            isNewArchitectureEnabled = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED,
            initializeThemeManager = {
                TimberLogger.d(TAG, "Initialize ThemeManager")
                ThemeManager.initialize(this)
            },
            initializeSoLoader = {
                TimberLogger.d(TAG, "Initialize SoLoader")
                SoLoader.init(this, OpenSourceMergedSoMapping)
            },
            initializeNewArchitecture = {
                TimberLogger.d(TAG, "Load React Native new architecture")
                load()
            },
            onComponentInitialized = { componentName, startTime, duration ->
                logComponentInitTime(componentName, startTime)
                startupPerformanceMonitor.recordComponentInitTime(componentName, duration)
            },
        )
    }

    private fun initializeNonCriticalComponentsAfterFirstFrame() {
        startupOrchestrator.initializeNonCriticalComponentsAfterFirstFrame(
            plan = deferredInitializationCoordinator.createPlanAfterFirstFrame(),
            launchNetworkInitialization = {
                lazyInitializationScope.launch {
                    ensureNetworkServiceInitialized()
                }
            },
            launchSettingsInitialization = {
                lazyInitializationScope.launch {
                    ensureSettingsServiceInitialized()
                }
            },
        )
    }

    private fun initializeNetworkServiceInternal() {
        TimberLogger.d(TAG, "Initialize RetrofitClient in background")
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
            TimberLogger.e(TAG, "Failed to initialize network service", error)
        }
    }

    private fun initializeSettingsServiceInternal() {
        TimberLogger.d(TAG, "Initialize SettingsUtils in background")
        val settingsStartTime = System.currentTimeMillis()
        try {
            settingsUtils.initializeAutoThemeSwitch()
            val settingsDuration = System.currentTimeMillis() - settingsStartTime
            logComponentInitTime("SettingsUtils", settingsStartTime)
            startupPerformanceMonitor.recordComponentInitTime("SettingsUtils", settingsDuration)
        } catch (error: Exception) {
            TimberLogger.e(TAG, "Failed to initialize settings service", error)
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
        TimberLogger.d(TAG, "MainApplication terminating, cleaning resources")

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
            "$milestone - from app start ${fromAppStart}ms, from onCreate start ${fromOnCreateStart}ms",
        )
    }

    private fun logComponentInitTime(componentName: String, startTime: Long) {
        val initTime = System.currentTimeMillis() - startTime
        TimberLogger.d(TAG, "$componentName initialized in ${initTime}ms")
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
        startupLifecycleReporter.markAppFullyLoaded()
    }

    fun markFirstFrameDrawn() {
        startupLifecycleReporter.markFirstFrameDrawn()
    }

    fun markFirstActivityCreate() {
        startupLifecycleReporter.markFirstActivityCreate()
    }
}
