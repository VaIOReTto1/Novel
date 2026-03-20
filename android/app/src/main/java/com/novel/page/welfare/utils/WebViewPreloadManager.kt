package com.novel.page.welfare.utils

import android.content.Context
import android.webkit.WebView
import android.webkit.WebSettings
import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * WebView预加载管理器
 * 
 * 功能特点：
 * - WebView实例池管理，减少创建开销
 * - 预加载常用页面，提升首次访问速度
 * - 内存管理，避免WebView内存泄漏
 * - 线程安全的实例获取和回收
 * 
 * 性能优化：
 * - 预创建WebView实例，避免运行时创建延迟
 * - 智能缓存策略，平衡内存使用和性能
 * - 后台预加载，不阻塞主线程
 */
@Stable
class WebViewPreloadManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "WebViewPreloadManager"
        private const val MAX_POOL_SIZE = 3 // 最大预加载实例数
        private const val MIN_POOL_SIZE = 1 // 最小保持实例数
        
        @Volatile
        private var INSTANCE: WebViewPreloadManager? = null
        
        fun getInstance(context: Context): WebViewPreloadManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WebViewPreloadManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // WebView实例池
    private val webViewPool = ConcurrentLinkedQueue<WebView>()
    private val poolSize = AtomicInteger(0)
    private val isInitialized = AtomicBoolean(false)
    
    // 协程作用域
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // 预加载URL列表
    private val preloadUrls = mutableSetOf<String>()
    
    /**
     * 初始化预加载管理器
     */
    fun initialize() {
        if (isInitialized.compareAndSet(false, true)) {
            TimberLogger.d(TAG, "初始化WebView预加载管理器")
            
            // 异步预创建WebView实例
            managerScope.launch {
                repeat(MIN_POOL_SIZE) {
                    createWebViewInstance()
                }
                TimberLogger.i(TAG, "WebView预加载池初始化完成，当前池大小: ${poolSize.get()}")
            }
        }
    }
    
    /**
     * 获取预加载的WebView实例
     * @param context 上下文
     * @return WebView实例，如果池为空则返回null
     */
    fun getWebView(context: Context): WebView? {
        val webView = webViewPool.poll()
        if (webView != null) {
            poolSize.decrementAndGet()
            TimberLogger.d(TAG, "从池中获取WebView，剩余: ${poolSize.get()}")
            
            // 异步补充池
            if (poolSize.get() < MIN_POOL_SIZE) {
                managerScope.launch {
                    createWebViewInstance()
                }
            }
            
            return webView
        } else {
            TimberLogger.d(TAG, "池为空，返回null")
            return null
        }
    }
    
    /**
     * 回收WebView实例到池中
     * @param webView 要回收的WebView实例
     * @return 是否成功回收到池中
     */
    fun recycleWebView(webView: WebView): Boolean {
        return if (poolSize.get() < MAX_POOL_SIZE) {
            // 清理WebView状态
            try {
                webView.apply {
                    stopLoading()
                    clearHistory()
                    loadUrl("about:blank")
                }
                
                webViewPool.offer(webView)
                poolSize.incrementAndGet()
                TimberLogger.d(TAG, "WebView已回收到池，当前池大小: ${poolSize.get()}")
                true
            } catch (e: Exception) {
                TimberLogger.e(TAG, "回收WebView时出错", e)
                false
            }
        } else {
            TimberLogger.d(TAG, "池已满，无法回收")
            false
        }
    }
    
    /**
     * 预加载指定URL
     * @param url 要预加载的URL
     */
    fun preloadUrl(url: String) {
        if (url.isBlank()) return
        
        preloadUrls.add(url)
        managerScope.launch {
            try {
                val webView = createWebViewInstance()
                TimberLogger.d(TAG, "开始预加载URL: $url")
                webView.loadUrl(url)
                
                // 预加载完成后回收
                delay(5000) // 等待5秒让页面加载
                if (!recycleWebView(webView)) {
                    destroyWebView(webView)
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "预加载URL失败: $url", e)
            }
        }
    }
    
    /**
     * 创建WebView实例
     */
    private suspend fun createWebViewInstance(): WebView = withContext(Dispatchers.Main) {
        TimberLogger.d(TAG, "创建新WebView实例")
        
        WebView(context).apply {
            // 基础配置
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                
                // 缓存策略
                cacheMode = WebSettings.LOAD_DEFAULT
                
                // 性能优化
                setRenderPriority(WebSettings.RenderPriority.HIGH)
                
                // 安全配置
                allowFileAccess = false
                allowContentAccess = false
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                
                // 隐私设置
                setGeolocationEnabled(false)
                savePassword = false
                saveFormData = false
            }
        }
    }
    
    /**
     * 销毁WebView实例
     */
    private fun destroyWebView(webView: WebView) {
        try {
            webView.apply {
                stopLoading()
                clearHistory()
                clearCache(true)
                loadUrl("about:blank")
                onPause()
                removeAllViews()
                destroy()
            }
            TimberLogger.d(TAG, "WebView实例已销毁")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "销毁WebView时出错", e)
        }
    }
    
    /**
     * 清理所有资源
     */
    fun cleanup() {
        TimberLogger.d(TAG, "清理WebView预加载管理器")
        
        managerScope.launch {
            // 销毁池中所有WebView
            while (webViewPool.isNotEmpty()) {
                webViewPool.poll()?.let { destroyWebView(it) }
            }
            poolSize.set(0)
            preloadUrls.clear()
            
            TimberLogger.i(TAG, "WebView预加载管理器清理完成")
        }
        
        // 取消协程作用域
        managerScope.cancel()
        isInitialized.set(false)
    }
    
    /**
     * 获取当前池状态信息
     */
    fun getPoolStatus(): String {
        return "WebView池状态 - 当前大小: ${poolSize.get()}, 最大: $MAX_POOL_SIZE, 最小: $MIN_POOL_SIZE, 预加载URL数: ${preloadUrls.size}"
    }
}
