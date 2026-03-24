package com.novel

import android.annotation.SuppressLint
import android.os.Bundle
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.bridge.ReactContext
import com.novel.rn.ReactNativeHostPathTraceCoordinator
import com.novel.utils.TimberLogger

internal class MainApplicationReactRootViewRegistry(
    private val application: MainApplication,
    private val reactNativeHost: ReactNativeHost,
    private val traceCoordinator: ReactNativeHostPathTraceCoordinator,
    private val ensureNetworkServiceInitialized: () -> Unit,
    private val cacheStore: ReactRootViewCacheStore<ReactRootView> = ReactRootViewCacheStore(),
) {

    companion object {
        private const val TAG = "MainApplication"
    }

    @SuppressLint("VisibleForTests")
    fun getOrCreateReactRootView(
        componentName: String,
        initialProps: Bundle? = null,
    ): ReactRootView {
        ensureNetworkServiceInitialized()

        val reactInstanceManager = reactNativeHost.reactInstanceManager
        val hasReactContext = reactInstanceManager.currentReactContext != null

        cacheStore.peek(componentName)?.let { cachedRootView ->
            TimberLogger.d(
                TAG,
                traceCoordinator.formatRootViewTrace(
                    componentName = componentName,
                    reused = true,
                    hasReactContext = hasReactContext,
                ),
            )
            return cachedRootView
        }

        TimberLogger.d(
            TAG,
            traceCoordinator.formatRootViewTrace(
                componentName = componentName,
                reused = false,
                hasReactContext = hasReactContext,
            ),
        )

        return cacheStore.getOrCreate(componentName) {
            createReactRootView(
                reactInstanceManager = reactInstanceManager,
                componentName = componentName,
                initialProps = initialProps,
            )
        }
    }

    fun clearReactRootViewCache(componentName: String) {
        cacheStore.remove(componentName)?.let {
            TimberLogger.d(TAG, "Clear ReactRootView cache: $componentName")
        }
    }

    fun clearAllReactRootViewCache() {
        cacheStore.clear()
        TimberLogger.d(TAG, "Clear all ReactRootView cache")
    }

    private fun createReactRootView(
        reactInstanceManager: ReactInstanceManager,
        componentName: String,
        initialProps: Bundle?,
    ): ReactRootView {
        TimberLogger.d(TAG, "Create ReactRootView: $componentName")
        return ReactRootView(application).apply {
            setIsFabric(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED)

            if (reactInstanceManager.currentReactContext != null) {
                TimberLogger.d(TAG, "Start React application immediately: $componentName")
                startReactApplication(reactInstanceManager, componentName, initialProps)
            } else {
                TimberLogger.d(TAG, "Wait for React context before start: $componentName")
                reactInstanceManager.addReactInstanceEventListener(
                    object : ReactInstanceManager.ReactInstanceEventListener {
                        override fun onReactContextInitialized(context: ReactContext) {
                            TimberLogger.d(TAG, "React context ready, start application: $componentName")
                            startReactApplication(reactInstanceManager, componentName, initialProps)
                            reactInstanceManager.removeReactInstanceEventListener(this)
                        }
                    },
                )
            }
        }
    }
}
