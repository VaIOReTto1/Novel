package com.novel.page.welfare.utils

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Stable
import com.novel.core.logging.CoreLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Stable
class WebViewPreloadManager private constructor(private val context: Context) {

    companion object {
        private const val TAG = "WebViewPreloadManager"
        private const val MAX_POOL_SIZE = 3
        private const val MIN_POOL_SIZE = 1

        @Volatile
        private var INSTANCE: WebViewPreloadManager? = null

        fun getInstance(context: Context): WebViewPreloadManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WebViewPreloadManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val webViewPool = ConcurrentLinkedQueue<WebView>()
    private val poolSize = AtomicInteger(0)
    private val isInitialized = AtomicBoolean(false)
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val preloadUrls = mutableSetOf<String>()

    fun initialize() {
        if (isInitialized.compareAndSet(false, true)) {
            CoreLogger.d(TAG, "初始化WebView预加载管理器")
            managerScope.launch {
                repeat(MIN_POOL_SIZE) {
                    createWebViewInstance()
                }
                CoreLogger.i(TAG, "WebView预加载池初始化完成，当前池大小: ${poolSize.get()}")
            }
        }
    }

    fun getWebView(context: Context): WebView? {
        val webView = webViewPool.poll()
        if (webView != null) {
            poolSize.decrementAndGet()
            CoreLogger.d(TAG, "从池中获取WebView，剩余: ${poolSize.get()}")

            if (poolSize.get() < MIN_POOL_SIZE) {
                managerScope.launch {
                    createWebViewInstance()
                }
            }
            return webView
        }

        CoreLogger.d(TAG, "池为空，返回null")
        return null
    }

    fun recycleWebView(webView: WebView): Boolean {
        return if (poolSize.get() < MAX_POOL_SIZE) {
            try {
                webView.apply {
                    stopLoading()
                    clearHistory()
                    loadUrl("about:blank")
                }
                webViewPool.offer(webView)
                poolSize.incrementAndGet()
                CoreLogger.d(TAG, "WebView已回收到池，当前池大小: ${poolSize.get()}")
                true
            } catch (e: Exception) {
                CoreLogger.e(TAG, "回收WebView时出错", e)
                false
            }
        } else {
            CoreLogger.d(TAG, "池已满，无法回收")
            false
        }
    }

    fun preloadUrl(url: String) {
        if (!preloadUrls.add(url)) {
            return
        }
        managerScope.launch {
            try {
                CoreLogger.d(TAG, "开始预加载URL: $url")
                val webView = getWebView(context) ?: createWebViewInstance()
                withContext(Dispatchers.Main) {
                    webView.loadUrl(url)
                }
            } catch (e: Exception) {
                CoreLogger.e(TAG, "预加载URL失败: $url", e)
            }
        }
    }

    private suspend fun createWebViewInstance(): WebView {
        return withContext(Dispatchers.Main) {
            CoreLogger.d(TAG, "创建新WebView实例")
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = false
                    displayZoomControls = false
                    setSupportZoom(false)
                    cacheMode = WebSettings.LOAD_DEFAULT
                    setRenderPriority(WebSettings.RenderPriority.HIGH)
                    allowFileAccess = false
                    allowContentAccess = false
                    allowFileAccessFromFileURLs = false
                    allowUniversalAccessFromFileURLs = false
                    savePassword = false
                    saveFormData = false
                }
                if (poolSize.get() < MAX_POOL_SIZE) {
                    webViewPool.offer(this)
                    poolSize.incrementAndGet()
                }
            }
        }
    }

    private fun destroyWebView(webView: WebView) {
        try {
            webView.apply {
                stopLoading()
                loadUrl("about:blank")
                clearHistory()
                removeAllViews()
                destroy()
            }
            CoreLogger.d(TAG, "WebView实例已销毁")
        } catch (e: Exception) {
            CoreLogger.e(TAG, "销毁WebView时出错", e)
        }
    }

    fun cleanup() {
        CoreLogger.d(TAG, "清理WebView预加载管理器")
        managerScope.launch {
            while (webViewPool.isNotEmpty()) {
                webViewPool.poll()?.let { destroyWebView(it) }
            }
            poolSize.set(0)
            preloadUrls.clear()
            CoreLogger.i(TAG, "WebView预加载管理器清理完成")
        }
        managerScope.cancel()
        isInitialized.set(false)
    }

    fun getPoolStatus(): String {
        return "WebView池状态 - 当前大小: ${poolSize.get()}, 最大: $MAX_POOL_SIZE, 最小: $MIN_POOL_SIZE, 预加载URL数: ${preloadUrls.size}"
    }
}
