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
import com.novel.core.logging.CoreLogger
import com.novel.page.welfare.utils.WebViewPreloadManager
import com.novel.page.welfare.utils.WelfarePerformanceMonitor
import com.novel.page.welfare.utils.WelfareWebSecurityConfig

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewComponent(
    url: String,
    modifier: Modifier = Modifier,
    performanceCoordinator: WelfareWebPerformanceCoordinator,
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
    forceContentWidthDp: Int? = null,
) {
    val context = LocalContext.current
    val performanceMonitor = remember { WelfarePerformanceMonitor.getInstance() }
    val webViewPreloadManager = remember { WebViewPreloadManager.getInstance(context) }

    val webView = remember {
        val webViewInstance = webViewPreloadManager.getWebView(context) ?: WebView(context)

        webViewInstance.onResume()
        webViewInstance.resumeTimers()
        CoreLogger.d("WebViewComponent", "WebView渲染状态已恢复")

        webViewInstance.apply {
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
                allowFileAccess = false
                allowContentAccess = false
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                setGeolocationEnabled(false)
                databaseEnabled = false
                savePassword = false
                saveFormData = false
                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
                WebSettingsCompat.setSafeBrowsingEnabled(settings, true)
            }

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    CoreLogger.d("WebViewComponent", "页面开始加载: $url")
                    performanceCoordinator.resetForNewPageLoad()
                    performanceMonitor.recordWebViewLoadStart()
                    onPageStarted()
                    url?.let(onUrlChanged)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    CoreLogger.d("WebViewComponent", "页面加载完成: $url")
                    performanceMonitor.recordWebViewLoadComplete()
                    onPageFinished()

                    view?.let { onNavigationStateChanged(it.canGoBack(), it.canGoForward()) }

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
                              }
                            })();
                        """.trimIndent()).format(forceContentWidthDp)
                        view.evaluateJavascript(js, null)
                    }
                }

                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    if (performanceCoordinator.shouldRecordFirstContentfulPaint()) {
                        performanceMonitor.recordFirstContentfulPaint()
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?,
                ) {
                    super.onReceivedError(view, request, error)
                    val errorMessage = error?.description?.toString() ?: "未知错误"
                    val errorCode = error?.errorCode ?: -1

                    CoreLogger.e("WebViewComponent", "页面加载错误: $errorMessage (代码: $errorCode)")
                    performanceMonitor.recordError("webview_error", "$errorMessage (代码: $errorCode)")

                    when (errorCode) {
                        WebViewClient.ERROR_HOST_LOOKUP,
                        WebViewClient.ERROR_CONNECT,
                        WebViewClient.ERROR_TIMEOUT -> onNetworkError("网络连接失败，请检查网络设置")
                        else -> onPageError(errorMessage)
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?,
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    val statusCode = errorResponse?.statusCode ?: 0
                    val reasonPhrase = errorResponse?.reasonPhrase ?: "HTTP错误"

                    CoreLogger.e("WebViewComponent", "HTTP错误: $statusCode $reasonPhrase")
                    performanceMonitor.recordError("http_error", "$statusCode $reasonPhrase")
                    onHttpError(statusCode, reasonPhrase)
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?,
                ) {
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

                    CoreLogger.e("WebViewComponent", "SSL错误: $errorMessage")
                    performanceMonitor.recordError("ssl_error", errorMessage)
                    onSslError(errorMessage)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val requestUri: Uri = request?.url ?: return false
                    if (!request.isForMainFrame) return false

                    if (WelfareWebSecurityConfig.isAllowedMainFrameUri(requestUri)) return false

                    if (WelfareWebSecurityConfig.shouldOpenExternally(requestUri)) {
                        val externalUrl = requestUri.toString()
                        CoreLogger.i("WebViewComponent", "外部打开非白名单链接: $externalUrl")
                        onExternalUrlRequested(externalUrl)
                        return true
                    }

                    CoreLogger.w("WebViewComponent", "拦截未通过白名单校验的URL: $requestUri")
                    return true
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (performanceCoordinator.shouldRecordTimeToInteractive(newProgress)) {
                        performanceMonitor.recordTimeToInteractive()
                    }
                    onProgressChanged(newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    title?.let(onTitleChanged)
                }
            }
        }
    }

    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

    DisposableEffect(webView, savedState) {
        CoreLogger.d("WebViewComponent", "DisposableEffect触发 - savedState: ${savedState != null}, url: $url, webView.url: ${webView.url}")

        savedState?.let { bundle ->
            try {
                CoreLogger.d("WebViewComponent", "开始恢复WebView状态")
                webView.restoreState(bundle)
                CoreLogger.d("WebViewComponent", "WebView状态已恢复，当前URL: ${webView.url}")
            } catch (e: Exception) {
                CoreLogger.e("WebViewComponent", "恢复WebView状态失败", e)
            }
        }

        if (url.isNotBlank() && url != webView.url) {
            CoreLogger.d("WebViewComponent", "传入URL与当前URL不同，强制加载: $url (当前: ${webView.url})")
            webView.loadUrl(url)
        } else if (url.isNotBlank()) {
            CoreLogger.d("WebViewComponent", "传入URL与当前URL相同，无需重新加载: $url")
        } else {
            CoreLogger.d("WebViewComponent", "传入URL为空，保持当前状态")
        }

        onDispose {
            try {
                val bundle = android.os.Bundle()
                CoreLogger.d("WebViewComponent", "开始保存WebView状态，当前URL: ${webView.url}")
                webView.saveState(bundle)
                onStateChanged(bundle)
                CoreLogger.d("WebViewComponent", "WebView状态已保存到Bundle")
            } catch (e: Exception) {
                CoreLogger.e("WebViewComponent", "保存WebView状态失败", e)
            }

            try {
                webView.apply {
                    stopLoading()
                    onPause()
                }

                if (!webViewPreloadManager.recycleWebView(webView)) {
                    webView.apply {
                        clearHistory()
                        clearCache(true)
                        loadUrl("about:blank")
                        removeAllViews()
                        destroy()
                    }
                }
                CoreLogger.d("WebViewComponent", "WebView资源已清理")
            } catch (e: Exception) {
                CoreLogger.e("WebViewComponent", "清理WebView资源时出错", e)
            }
        }
    }

    DisposableEffect(url) {
        if (url.isNotBlank() && url != webView.url) {
            CoreLogger.d("WebViewComponent", "URL参数变化，重新加载: $url (当前: ${webView.url})")
            webView.loadUrl(url)
        }
        onDispose {}
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                webView.onResume()
                webView.resumeTimers()
                CoreLogger.d("WebViewComponent", "WebView已挂载到界面并恢复活跃状态")
                onWebViewCreated(webView)
                webView
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

interface WebViewActions {
    fun goBack()
    fun goForward()
    fun refresh()
    fun loadUrl(url: String)
}

fun createWebViewActions(webView: WebView): WebViewActions {
    return object : WebViewActions {
        override fun goBack() {
            if (webView.canGoBack()) webView.goBack()
        }

        override fun goForward() {
            if (webView.canGoForward()) webView.goForward()
        }

        override fun refresh() {
            webView.reload()
        }

        override fun loadUrl(url: String) {
            webView.loadUrl(url)
        }
    }
}
