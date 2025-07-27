package com.novel.page.welfare

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import com.novel.page.welfare.component.SkeletonLoadingComponent
import com.novel.page.welfare.component.EnhancedErrorComponent
import com.novel.page.welfare.component.ErrorType
import com.novel.page.welfare.utils.WebViewPreloadManager
import com.novel.page.welfare.utils.WelfarePerformanceMonitor
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novel.page.welfare.component.InitialLoadingState
import com.novel.page.welfare.component.LoadingIndicator
import com.novel.page.welfare.component.WebViewComponent
import com.novel.page.welfare.component.rememberThemeState
import com.novel.page.welfare.component.updateWebViewDarkMode
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import android.os.Bundle
import com.novel.page.welfare.viewmodel.WelfareEffect
import com.novel.page.welfare.viewmodel.WelfareIntent
import com.novel.page.welfare.viewmodel.WelfareViewModel
import com.novel.ui.theme.NovelTheme
import com.novel.ui.theme.ThemeManager
import com.novel.utils.TimberLogger

/**
 * 福利页面
 *
 * 使用MVI架构实现的WebView页面，主要功能：
 * - 展示GitHub页面 (https://github.com/VaIOReTto1)
 * - 提供WebView导航控制
 * - 处理加载状态和错误状态
 * - 集成到主页面的第三页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelfarePage(
    onNavigateBack: () -> Unit = {},
    viewModel: WelfareViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // 获取主题管理器和触觉反馈
    val themeManager = remember { ThemeManager.getInstance() }
    val isDarkMode by themeManager.isDarkMode.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 保存WebView状态，防止页面切换时丢失
    var savedWebViewState by rememberSaveable { mutableStateOf<Bundle?>(null) }
    
    // 简化页面可见性逻辑 - 默认页面总是可见，避免白屏问题
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val isPageVisible = lifecycleState.isAtLeast(Lifecycle.State.STARTED)
    
    // 记录页面可见性变化
    LaunchedEffect(isPageVisible) {
        TimberLogger.d("WelfarePage", "页面可见性: $isPageVisible, 生命周期状态: $lifecycleState")
    }
    
    // 性能监控
    val performanceMonitor = remember { WelfarePerformanceMonitor.getInstance() }
    
    // WebView预加载管理器
    val webViewPreloadManager = remember { WebViewPreloadManager.getInstance(context) }

    // 使用StateAdapter优化状态访问
    val adapter = viewModel.adapter

    // 收集状态
    val isLoading by adapter.createLoadingState()
    val currentUrl by adapter.currentUrlState()
    val pageLoadingState by adapter.isPageLoadingState()
    val errorMessage by adapter.pageErrorState()
    val loadingProgress by adapter.loadingProgressState()
    val shouldShowProgress by adapter.shouldShowProgressState()
    
    // 深色模式状态
    val themeState = rememberThemeState()
    
    // WebView引用
    var currentWebView by remember { mutableStateOf<android.webkit.WebView?>(null) }
    
    // 初始化组件
    LaunchedEffect(Unit) {
        // 初始化WebView预加载管理器
        webViewPreloadManager.initialize()
        
        // 开始性能监控
        performanceMonitor.startPageLoad("welfare_page")
    }
    
    // 监控页面加载状态变化
    LaunchedEffect(isLoading, currentUrl) {
        if (!isLoading && currentUrl.isNotBlank()) {
            performanceMonitor.recordPageLoadComplete()
        }
    }
    
    // 监控错误状态
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            performanceMonitor.recordError("page_error", error)
        }
    }
    
    // 注意：下拉刷新功能需要更新的Material3版本
    // 当前版本暂时移除该功能

    // 收集副作用
    val effects by viewModel.effect.collectAsStateWithLifecycle(initialValue = null)

    // 处理副作用
    LaunchedEffect(effects) {
        effects?.let { effect ->
            when (effect) {
                is WelfareEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                    )
                }

                is WelfareEffect.NavigateBack -> {
                    onNavigateBack()
                }

                is WelfareEffect.ShowErrorDialog -> {
                    // 这里可以显示错误对话框
                    // 目前使用Snackbar代替
                    snackbarHostState.showSnackbar(
                        message = "错误: ${effect.message}"
                    )
                }

                is WelfareEffect.ShowSslErrorDialog -> {
                    // TODO: 显示SSL错误对话框
                }

                is WelfareEffect.ShowHttpErrorPage -> {
                    // TODO: 显示HTTP错误页面
                }

                is WelfareEffect.ShowNetworkErrorSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "重试"
                    )
                }

                is WelfareEffect.OpenInBrowser -> {
                    // TODO: 在外部浏览器中打开
                }

                is WelfareEffect.TriggerHapticFeedback -> {
                    // 触发触觉反馈
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                // WebView相关的副作用在WebViewComponent中处理
                else -> {
                    TimberLogger.d("WelfarePage", "处理副作用: ${effect::class.simpleName}")
                }
            }
        }
    }

    // 页面初始化
    LaunchedEffect(Unit) {
        viewModel.sendIntent(WelfareIntent.InitializePage)
    }

    val onPageStarted = remember {
        { viewModel.sendIntent(WelfareIntent.OnPageStarted) }
    }

    val onPageFinished = remember {
        { viewModel.sendIntent(WelfareIntent.OnPageFinished) }
    }

    val onPageError = remember<(String) -> Unit> {
        { error -> viewModel.sendIntent(WelfareIntent.OnPageError(error)) }
    }

    val onProgressChanged = remember<(Int) -> Unit> {
        { progress -> viewModel.sendIntent(WelfareIntent.UpdateProgress(progress)) }
    }

    val onNavigationStateChanged = remember<(Boolean, Boolean) -> Unit> {
        { canGoBack, canGoForward ->
            viewModel.updateNavigationState(canGoBack, canGoForward)
        }
    }

    val onTitleChanged = remember<(String) -> Unit> {
        { newTitle -> viewModel.updatePageTitle(newTitle) }
    }

    val onUrlChanged = remember<(String) -> Unit> {
        { newUrl -> viewModel.updateCurrentUrl(newUrl) }
    }

    // 使用NovelTheme确保主题适配
    NovelTheme(darkTheme = isDarkMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        // 添加调试日志
        LaunchedEffect(pageLoadingState, errorMessage, isLoading, currentUrl) {
            TimberLogger.d("WelfarePage", "渲染条件检查 - pageLoadingState: $pageLoadingState, errorMessage: $errorMessage, isLoading: $isLoading, currentUrl: $currentUrl")
        }
        
        when {
            // 显示错误状态
            pageLoadingState && !errorMessage.isNullOrBlank() -> {
                TimberLogger.d("WelfarePage", "显示错误组件")
                EnhancedErrorComponent(
                    errorType = ErrorType.NETWORK_ERROR,
                    customMessage = errorMessage ?: "未知错误",
                    onRetry = {
                        viewModel.sendIntent(WelfareIntent.ClearError)
                        viewModel.sendIntent(WelfareIntent.RefreshPage)
                    },
                    onSecondaryAction = onNavigateBack,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 显示初始加载状态
            isLoading && currentUrl.isBlank() -> {
                TimberLogger.d("WelfarePage", "显示骨架加载组件")
                SkeletonLoadingComponent(
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 显示WebView
            currentUrl.isNotBlank() -> {
                TimberLogger.d("WelfarePage", "显示WebView组件 - URL: $currentUrl")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // WebView内容区域 - 始终渲染，避免白屏问题
                    WebViewComponent(
                        url = currentUrl,
                        modifier = Modifier.fillMaxSize(),
                        savedState = savedWebViewState,
                        onStateChanged = { bundle ->
                            savedWebViewState = bundle
                        },
                        onWebViewCreated = { webView ->
                            currentWebView = webView
                            // 立即应用深色模式
                            updateWebViewDarkMode(webView, isDarkMode)
                        },
                        onPageStarted = onPageStarted,
                        onPageFinished = onPageFinished,
                        onPageError = onPageError,
                        onSslError = { error -> viewModel.sendIntent(WelfareIntent.OnSslError(error)) },
                        onHttpError = { code, message -> viewModel.sendIntent(WelfareIntent.OnHttpError(code, message)) },
                        onNetworkError = { error -> viewModel.sendIntent(WelfareIntent.OnNetworkError(error)) },
                        onProgressChanged = onProgressChanged,
                        onNavigationStateChanged = onNavigationStateChanged,
                        onTitleChanged = onTitleChanged,
                        onUrlChanged = onUrlChanged
                    )
                    
                    // 深色模式适配和可访问性配置
                    LaunchedEffect(currentWebView, isDarkMode) {
                        currentWebView?.let { webView ->
                            // 应用深色模式
                            updateWebViewDarkMode(webView, isDarkMode)
                            TimberLogger.d("WelfarePage", "深色模式状态变更: $isDarkMode")
                        }
                    }
                }
            }

            // 默认加载状态
            else -> {
                InitialLoadingState(
                    message = "准备加载页面...",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        // 加载进度条
        if (shouldShowProgress) {
            LoadingIndicator(
                progress = loadingProgress,
                isVisible = shouldShowProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
        
        // 记录WebView引用（用于深色模式和可访问性）
        LaunchedEffect(currentUrl) {
            if (currentUrl.isNotBlank()) {
                // 这里需要从WebViewComponent获取WebView实例
                // 由于当前架构限制，暂时使用null
                // 在后续版本中可以通过回调或其他方式获取WebView引用
                TimberLogger.d("WelfarePage", "WebView加载成功，深色模式支持: ${themeState.isWebViewDarkModeSupported}")
            }
        }
        
        // 下拉刷新功能暂时移除，等待Material3版本更新
        }
    }
}