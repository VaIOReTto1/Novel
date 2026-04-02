package com.novel.page.welfare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novel.core.logging.CoreLogger
import com.novel.page.welfare.component.EnhancedErrorComponent
import com.novel.page.welfare.component.ErrorType
import com.novel.page.welfare.component.InitialLoadingState
import com.novel.page.welfare.component.LoadingIndicator
import com.novel.page.welfare.component.SkeletonLoadingComponent
import com.novel.page.welfare.component.WebViewComponent
import com.novel.page.welfare.component.WelfareWebPerformanceCoordinator
import com.novel.page.welfare.component.rememberThemeState
import com.novel.page.welfare.component.updateWebViewDarkMode
import com.novel.page.welfare.utils.WebViewPreloadManager
import com.novel.page.welfare.utils.WelfarePerformanceMonitor
import com.novel.page.welfare.viewmodel.WelfareEffect
import com.novel.page.welfare.viewmodel.WelfareIntent
import com.novel.page.welfare.viewmodel.WelfarePageBootstrapCoordinator
import com.novel.page.welfare.viewmodel.WelfareViewModel
import com.novel.ui.theme.NovelDesignTokens
import com.novel.ui.theme.NovelTheme
import com.novel.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelfarePageContent(
    onNavigateBack: () -> Unit = {},
    viewModel: WelfareViewModel,
) {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager.getInstance() }
    val isDarkMode by themeManager.isDarkMode.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    var savedWebViewState by rememberSaveable { mutableStateOf<Bundle?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    val isPageVisible = lifecycleState.isAtLeast(Lifecycle.State.STARTED)

    LaunchedEffect(isPageVisible) {
        CoreLogger.d("WelfarePage", "页面可见性: $isPageVisible, 生命周期状态: $lifecycleState")
    }

    val performanceMonitor = remember { WelfarePerformanceMonitor.getInstance() }
    val performanceCoordinator = remember { WelfareWebPerformanceCoordinator() }
    val bootstrapCoordinator = remember { WelfarePageBootstrapCoordinator() }
    val webViewPreloadManager = remember { WebViewPreloadManager.getInstance(context) }
    var hasBootstrappedPage by remember { mutableStateOf(false) }

    val adapter = viewModel.adapter
    val isLoading by adapter.createLoadingState()
    val currentUrl by adapter.currentUrlState()
    val pageLoadingState by adapter.isPageLoadingState()
    val errorMessage by adapter.pageErrorState()
    val loadingProgress by adapter.loadingProgressState()
    val shouldShowProgress by adapter.shouldShowProgressState()
    val themeState = rememberThemeState()
    var currentWebView by remember { mutableStateOf<android.webkit.WebView?>(null) }

    LaunchedEffect(hasBootstrappedPage) {
        val bootstrapPlan = bootstrapCoordinator.createInitialPlan(
            alreadyBootstrapped = hasBootstrappedPage,
        )
        if (bootstrapPlan.shouldInitializePreloadManager) {
            webViewPreloadManager.initialize()
        }
        if (bootstrapPlan.shouldDispatchInitializeIntent) {
            viewModel.sendIntent(WelfareIntent.InitializePage)
        }
        if (!hasBootstrappedPage) {
            hasBootstrappedPage = true
        }
    }

    LaunchedEffect(currentUrl) {
        val navigationPlan = performanceCoordinator.createNavigationPlan(currentUrl = currentUrl)
        if (navigationPlan.shouldStartPageLoadMonitoring) {
            performanceMonitor.startPageLoad(currentUrl)
        }
    }

    LaunchedEffect(isLoading, currentUrl) {
        if (performanceCoordinator.shouldRecordPageLoadComplete(isLoading = isLoading, currentUrl = currentUrl)) {
            performanceMonitor.recordPageLoadComplete()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { error -> performanceMonitor.recordError("page_error", error) }
    }

    val effects by viewModel.effect.collectAsStateWithLifecycle(initialValue = null)
    LaunchedEffect(effects) {
        effects?.let { effect ->
            when (effect) {
                is WelfareEffect.ShowToast -> snackbarHostState.showSnackbar(message = effect.message)
                is WelfareEffect.NavigateBack -> onNavigateBack()
                is WelfareEffect.ShowErrorDialog ->
                    snackbarHostState.showSnackbar(message = "错误: ${effect.message}")

                is WelfareEffect.ShowSslErrorDialog ->
                    snackbarHostState.showSnackbar(message = "SSL错误：${effect.message}")

                is WelfareEffect.ShowHttpErrorPage ->
                    snackbarHostState.showSnackbar(
                        message = "HTTP错误 ${effect.errorCode}: ${effect.description}",
                    )

                is WelfareEffect.ShowNetworkErrorSnackbar ->
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = "重试",
                    )

                is WelfareEffect.OpenInBrowser -> {
                    runCatching {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(browserIntent)
                    }.onFailure { error ->
                        CoreLogger.e("WelfarePage", "外部打开链接失败: ${effect.url}", error)
                        snackbarHostState.showSnackbar("无法打开外部链接")
                    }
                }

                WelfareEffect.TriggerHapticFeedback ->
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                else -> CoreLogger.d("WelfarePage", "处理副作用: ${effect::class.simpleName}")
            }
        }
    }

    val onPageStarted = remember { { viewModel.sendIntent(WelfareIntent.OnPageStarted) } }
    val onPageFinished = remember { { viewModel.sendIntent(WelfareIntent.OnPageFinished) } }
    val onPageError = remember<(String) -> Unit> {
        { error -> viewModel.sendIntent(WelfareIntent.OnPageError(error)) }
    }
    val onProgressChanged = remember<(Int) -> Unit> {
        { progress -> viewModel.sendIntent(WelfareIntent.UpdateProgress(progress)) }
    }
    val onNavigationStateChanged = remember<(Boolean, Boolean) -> Unit> {
        { canGoBack, canGoForward -> viewModel.updateNavigationState(canGoBack, canGoForward) }
    }
    val onTitleChanged = remember<(String) -> Unit> { { newTitle -> viewModel.updatePageTitle(newTitle) } }
    val onUrlChanged = remember<(String) -> Unit> { { newUrl -> viewModel.updateCurrentUrl(newUrl) } }

    NovelTheme(darkTheme = isDarkMode) {
        Box(modifier = Modifier.fillMaxSize()) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            LaunchedEffect(pageLoadingState, errorMessage, isLoading, currentUrl) {
                CoreLogger.d(
                    "WelfarePage",
                    "渲染条件检查- pageLoadingState: $pageLoadingState, errorMessage: $errorMessage, isLoading: $isLoading, currentUrl: $currentUrl",
                )
            }

            when {
                pageLoadingState && !errorMessage.isNullOrBlank() -> {
                    CoreLogger.d("WelfarePage", "显示错误组件")
                    EnhancedErrorComponent(
                        errorType = ErrorType.NETWORK_ERROR,
                        customMessage = errorMessage ?: "未知错误",
                        onRetry = {
                            viewModel.sendIntent(WelfareIntent.ClearError)
                            viewModel.sendIntent(WelfareIntent.RefreshPage)
                        },
                        onSecondaryAction = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                isLoading && currentUrl.isBlank() -> {
                    CoreLogger.d("WelfarePage", "显示骨架加载组件")
                    SkeletonLoadingComponent(modifier = Modifier.fillMaxSize())
                }

                currentUrl.isNotBlank() -> {
                    CoreLogger.d("WelfarePage", "显示WebView组件 - URL: $currentUrl")
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NovelDesignTokens.color("color.bg.surface")),
                    ) {
                        WebViewComponent(
                            url = currentUrl,
                            modifier = Modifier.fillMaxSize(),
                            performanceCoordinator = performanceCoordinator,
                            savedState = savedWebViewState,
                            onStateChanged = { bundle -> savedWebViewState = bundle },
                            onWebViewCreated = { webView ->
                                currentWebView = webView
                                updateWebViewDarkMode(webView, isDarkMode)
                            },
                            onPageStarted = onPageStarted,
                            onPageFinished = onPageFinished,
                            onPageError = onPageError,
                            onSslError = { error -> viewModel.sendIntent(WelfareIntent.OnSslError(error)) },
                            onHttpError = { code, message ->
                                viewModel.sendIntent(WelfareIntent.OnHttpError(code, message))
                            },
                            onNetworkError = { error -> viewModel.sendIntent(WelfareIntent.OnNetworkError(error)) },
                            onExternalUrlRequested = { externalUrl ->
                                viewModel.sendIntent(WelfareIntent.OpenExternalUrl(externalUrl))
                            },
                            onProgressChanged = onProgressChanged,
                            onNavigationStateChanged = onNavigationStateChanged,
                            onTitleChanged = onTitleChanged,
                            onUrlChanged = onUrlChanged,
                            forceContentWidthDp = 375,
                        )

                        LaunchedEffect(currentWebView, isDarkMode) {
                            currentWebView?.let { webView ->
                                updateWebViewDarkMode(webView, isDarkMode)
                                CoreLogger.d("WelfarePage", "深色模式状态变更: $isDarkMode")
                            }
                        }
                    }
                }

                else -> {
                    InitialLoadingState(
                        message = "准备加载页面...",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }

            if (shouldShowProgress) {
                LoadingIndicator(
                    progress = loadingProgress,
                    isVisible = shouldShowProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                )
            }

            LaunchedEffect(currentUrl) {
                if (currentUrl.isNotBlank()) {
                    CoreLogger.d(
                        "WelfarePage",
                        "WebView加载成功，深色模式支持: ${themeState.isWebViewDarkModeSupported}",
                    )
                }
            }
        }
    }
}
