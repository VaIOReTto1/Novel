package com.novel.page.welfare.component

import android.annotation.SuppressLint
import android.net.Uri
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.novel.page.welfare.utils.WebViewPreloadManager
import com.novel.page.welfare.utils.WelfarePerformanceMonitor
import com.novel.page.welfare.utils.WelfareWebSecurityConfig
import com.novel.utils.TimberLogger

/**
 * WebView组件
 * 
 * 封装WebView的Compose组件，提供：
 * - WebView的基本配置
 * - 加载状态回调
 * - 导航状态回调
 * - 错误处理
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewComponent(
    url: String,
    modifier: Modifier = Modifier,
    savedState: android.os.Bundle? = null,
    onStateChanged: (android.os.Bundle?) -> Unit = {},
    onWebViewCreated: (WebView) -> Unit = {},
    onPageStarted: () -> Unit = {},
    onPageFinished: () -> Unit = {},
    onPageError: (String) -> Unit = {},
    onSslError: (String) -> Unit = {},
    onHttpError: (Int, String) -> Unit = { _, _ -> },
    onNetworkError: (String) -> Unit = {},
    onExternalUrlRequested: (String) -> Unit = {},
    onProgressChanged: (Int) -> Unit = {},
    onNavigationStateChanged: (canGoBack: Boolean, canGoForward: Boolean) -> Unit = { _, _ -> },
    onTitleChanged: (String) -> Unit = {},
    onUrlChanged: (String) -> Unit = {},
    forceContentWidthDp: Int? = null
) {
    val context = LocalContext.current
    val performanceMonitor = remember { WelfarePerformanceMonitor.getInstance() }
    val webViewPreloadManager = remember { WebViewPreloadManager.getInstance(context) }
    
    val webView = remember {
        // 尝试从预加载池获取WebView实例，如果失败则创建新实例
        val webViewInstance = webViewPreloadManager.getWebView(context) ?: WebView(context)
        
        // 关键修复：无论是从池中获取还是新创建的WebView，都要恢复渲染状态
        // 解决复用WebView时白屏问题
        webViewInstance.onResume()
        webViewInstance.resumeTimers()
        TimberLogger.d("WebViewComponent", "WebView渲染状态已恢复")
        
        webViewInstance.apply {
            // 安全设置
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
                javaScriptCanOpenWindowsAutomatically = false
                setSupportMultipleWindows(false)
                
                // 安全配置
                allowFileAccess = false
                allowContentAccess = false
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                
                // 隐私设置
                setGeolocationEnabled(false)
                databaseEnabled = false
                savePassword = false
                saveFormData = false
                
                // 性能优化
                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
            }
            
            // 配置Safe Browsing（如果支持）
            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
                WebSettingsCompat.setSafeBrowsingEnabled(settings, true)
            }
            
            // 深色模式支持（如果支持）
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }
            
            // 设置WebViewClient
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    TimberLogger.d("WebViewComponent", "页面开始加载: $url")
                    performanceMonitor.recordWebViewLoadStart()
                    onPageStarted()
                    url?.let { onUrlChanged(it) }
                }
                
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    TimberLogger.d("WebViewComponent", "页面加载完成: $url")
                    performanceMonitor.recordWebViewLoadComplete()
                    onPageFinished()
                    
                    // 更新导航状态
                    view?.let {
                        onNavigationStateChanged(it.canGoBack(), it.canGoForward())
                    }

                    // 如需强制按指定宽度渲染（例如375dp），注入 viewport 与样式
                    if (forceContentWidthDp != null && view != null) {
                        val js = ("""
                            (function() {
                              try {
                                var desired = %d;
                                var meta = document.querySelector('meta[name="viewport"]');
                                var currentWidth = Math.max(document.documentElement.clientWidth, window.innerWidth || 0) || desired;
                                var scale = currentWidth / desired;
                                if (!meta) {
                                  meta = document.createElement('meta');
                                  meta.name = 'viewport';
                                  document.head && document.head.appendChild(meta);
                                }
                                if (meta) {
                                  meta.setAttribute('content', 'width=' + desired + ', initial-scale=' + scale + ', maximum-scale=' + scale + ', minimum-scale=' + scale + ', user-scalable=no');
                                }
                                if (document && document.documentElement) {
                                  document.documentElement.style.overflowX = 'hidden';
                                }
                                if (document && document.body) {
                                  document.body.style.margin = '0 auto';
                                  document.body.style.width = desired + 'px';
                                }
                              } catch (e) {
                                // swallow
                              }
                            })();
                        """.trimIndent()).format(forceContentWidthDp)
                        view.evaluateJavascript(js, null)
                    }
                }
                
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    val errorMessage = error?.description?.toString() ?: "未知错误"
                    val errorCode = error?.errorCode ?: -1
                    
                    TimberLogger.e("WebViewComponent", "页面加载错误: $errorMessage (代码: $errorCode)")
                    performanceMonitor.recordError("webview_error", "$errorMessage (代码: $errorCode)")
                    
                    // 根据错误类型分类处理
                    when (errorCode) {
                        WebViewClient.ERROR_HOST_LOOKUP,
                        WebViewClient.ERROR_CONNECT,
                        WebViewClient.ERROR_TIMEOUT -> {
                            onNetworkError("网络连接失败，请检查网络设置")
                        }
                        else -> {
                            onPageError(errorMessage)
                        }
                    }
                }
                
                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    val statusCode = errorResponse?.statusCode ?: 0
                    val reasonPhrase = errorResponse?.reasonPhrase ?: "HTTP错误"
                    
                    TimberLogger.e("WebViewComponent", "HTTP错误: $statusCode $reasonPhrase")
                    performanceMonitor.recordError("http_error", "$statusCode $reasonPhrase")
                    onHttpError(statusCode, reasonPhrase)
                }
                
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    // 默认拒绝SSL错误，提高安全性
                    handler?.cancel()
                    
                    val errorMessage = when (error?.primaryError) {
                        SslError.SSL_UNTRUSTED -> "SSL证书不受信任"
                        SslError.SSL_EXPIRED -> "SSL证书已过期"
                        SslError.SSL_IDMISMATCH -> "SSL证书域名不匹配"
                        SslError.SSL_NOTYETVALID -> "SSL证书尚未生效"
                        SslError.SSL_DATE_INVALID -> "SSL证书日期无效"
                        SslError.SSL_INVALID -> "SSL证书无效"
                        else -> "SSL连接错误"
                    }
                    
                    TimberLogger.e("WebViewComponent", "SSL错误: $errorMessage")
                    performanceMonitor.recordError("ssl_error", errorMessage)
                    onSslError(errorMessage)
                }
                
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val requestUri = request?.url ?: return false
                    if (request.isForMainFrame.not()) {
                        return false
                    }

                    if (WelfareWebSecurityConfig.isAllowedMainFrameUri(requestUri)) {
                        return false
                    }

                    if (WelfareWebSecurityConfig.shouldOpenExternally(requestUri)) {
                        val externalUrl = requestUri.toString()
                        TimberLogger.i("WebViewComponent", "外部打开非白名单链接: $externalUrl")
                        onExternalUrlRequested(externalUrl)
                        return true
                    }

                    TimberLogger.w("WebViewComponent", "拦截未通过白名单校验的URL: $requestUri")
                    return true
                }
            }
            
            // 设置WebChromeClient
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onProgressChanged(newProgress)
                }
                
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    title?.let { onTitleChanged(it) }
                }
            }
        }
        
        webViewInstance
    }

    // 第三方Cookie控制
    val cm = CookieManager.getInstance()
    cm.setAcceptThirdPartyCookies(webView, true)
    
    // 状态保存和恢复
    DisposableEffect(webView, savedState) {
        TimberLogger.d("WebViewComponent", "DisposableEffect触发 - savedState: ${savedState != null}, url: $url, webView.url: ${webView.url}")
        
        // 恢复WebView状态
        savedState?.let { bundle ->
            try {
                TimberLogger.d("WebViewComponent", "开始恢复WebView状态")
                webView.restoreState(bundle)
                TimberLogger.d("WebViewComponent", "WebView状态已恢复，当前URL: ${webView.url}")
            } catch (e: Exception) {
                TimberLogger.e("WebViewComponent", "恢复WebView状态失败", e)
            }
        }
        
        // 无论是否有savedState，都要确保传入的url参数被正确加载
        // 传入的url参数优先级高于savedState中的URL
        if (url.isNotBlank() && url != webView.url) {
            TimberLogger.d("WebViewComponent", "传入URL与当前URL不同，强制加载: $url (当前: ${webView.url})")
            webView.loadUrl(url)
        } else if (url.isNotBlank()) {
            TimberLogger.d("WebViewComponent", "传入URL与当前URL相同，无需重新加载: $url")
        } else {
            TimberLogger.d("WebViewComponent", "传入URL为空，保持当前状态")
        }
        
        onDispose {
            // 保存WebView状态 - 在清理之前保存
            try {
                val bundle = android.os.Bundle()
                TimberLogger.d("WebViewComponent", "开始保存WebView状态，当前URL: ${webView.url}")
                webView.saveState(bundle)
                onStateChanged(bundle)
                TimberLogger.d("WebViewComponent", "WebView状态已保存到Bundle")
            } catch (e: Exception) {
                TimberLogger.e("WebViewComponent", "保存WebView状态失败", e)
            }
            
            // 保存状态后再进行清理
            try {
                webView.apply {
                    stopLoading()
                    onPause()
                }
                
                // 尝试回收到预加载池，如果失败则销毁
                if (!webViewPreloadManager.recycleWebView(webView)) {
                    webView.apply {
                        // 只有在真正销毁时才彻底清理
                        clearHistory()
                        clearCache(true)
                        loadUrl("about:blank")
                        removeAllViews()
                        destroy()
                    }
                }
                TimberLogger.d("WebViewComponent", "WebView资源已清理")
            } catch (e: Exception) {
                TimberLogger.e("WebViewComponent", "清理WebView资源时出错", e)
            }
        }
    }
    
    // 当URL变化时重新加载
    DisposableEffect(url) {
        if (url.isNotBlank() && url != webView.url) {
            TimberLogger.d("WebViewComponent", "URL参数变化，重新加载: $url (当前: ${webView.url})")
            webView.loadUrl(url)
        }
        
        onDispose {
            // 清理资源
        }
    }
    
    // WebView生命周期管理已合并到状态保存的DisposableEffect中
    
    Box(modifier = modifier) {
        AndroidView(
            factory = { 
                // 确保WebView在挂载到界面时处于活跃状态
                webView.onResume()
                webView.resumeTimers()
                TimberLogger.d("WebViewComponent", "WebView已挂载到界面并恢复活跃状态")
                onWebViewCreated(webView)
                webView 
            },
            modifier = Modifier.fillMaxSize()
        ) { view ->
            // 这里可以进行额外的配置更新
        }
    }
}

/**
 * WebView操作接口
 * 提供对WebView的控制方法
 */
interface WebViewActions {
    fun goBack()
    fun goForward()
    fun refresh()
    fun loadUrl(url: String)
}

/**
 * 创建WebView操作实例
 */
fun createWebViewActions(webView: WebView): WebViewActions {
    return object : WebViewActions {
        override fun goBack() {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }
        
        override fun goForward() {
            if (webView.canGoForward()) {
                webView.goForward()
            }
        }
        
        override fun refresh() {
            webView.reload()
        }
        
        override fun loadUrl(url: String) {
            webView.loadUrl(url)
        }
    }
}
