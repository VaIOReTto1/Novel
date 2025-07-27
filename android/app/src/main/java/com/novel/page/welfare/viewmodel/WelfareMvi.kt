package com.novel.page.welfare.viewmodel

import androidx.compose.runtime.Stable
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect

/**
 * Welfare模块MVI契约定义
 * 
 * 福利页面主要用于展示WebView内容，显示GitHub链接
 * 提供基本的WebView操作功能
 */

// ===== Intent定义 =====

/**
 * Welfare模块意图定义
 * 处理WebView相关的用户交互
 */
sealed class WelfareIntent : MviIntent {
    /** 初始化页面 */
    object InitializePage : WelfareIntent()
    
    /** 加载指定URL */
    data class LoadUrl(val url: String) : WelfareIntent()
    
    /** 刷新页面 */
    object RefreshPage : WelfareIntent()
    
    /** 返回上一页 */
    object GoBack : WelfareIntent()
    
    /** 前进到下一页 */
    object GoForward : WelfareIntent()
    
    /** WebView开始加载 */
    object OnPageStarted : WelfareIntent()
    
    /** WebView加载完成 */
    object OnPageFinished : WelfareIntent()
    
    /** WebView加载错误 */
    data class OnPageError(val errorMessage: String) : WelfareIntent()
    
    /** SSL错误 */
    data class OnSslError(val errorMessage: String) : WelfareIntent()
    
    /** HTTP错误 */
    data class OnHttpError(val errorCode: Int, val description: String) : WelfareIntent()
    
    /** 网络连接错误 */
    data class OnNetworkError(val errorMessage: String) : WelfareIntent()
    
    /** 更新加载进度 */
    data class UpdateProgress(val progress: Int) : WelfareIntent()
    
    /** 清除错误状态 */
    object ClearError : WelfareIntent()
    
    /** 导航返回 */
    object NavigateBack : WelfareIntent()
}

// ===== State定义 =====

/**
 * Welfare模块状态定义
 * 管理WebView的状态和数据
 */
@Stable
data class WelfareState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    
    // WebView相关状态
    val currentUrl: String = "https://juejin.cn/",
    val title: String = "福利页面",
    val loadingProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    
    // 页面状态
    val isPageLoading: Boolean = false,
    val pageError: String? = null,
    val isInitialized: Boolean = false
) : MviState {
    
    override val isEmpty: Boolean
        get() = currentUrl.isBlank()
    
    override val isSuccess: Boolean
        get() = !isLoading && !hasError && !isEmpty && isInitialized && !isPageLoading
    
    /**
     * 是否显示加载进度条
     */
    val shouldShowProgress: Boolean
        get() = isPageLoading && loadingProgress in 1..99
    
    /**
     * 是否显示错误状态
     */
    val shouldShowError: Boolean
        get() = pageError != null
    
    /**
     * 获取显示的错误信息
     */
    val displayError: String?
        get() = pageError ?: error
}

// ===== Effect定义 =====

/**
 * Welfare模块副作用定义
 * 处理一次性副作用事件
 */
sealed class WelfareEffect : MviEffect {
    /** 显示Toast消息 */
    data class ShowToast(val message: String) : WelfareEffect()
    
    /** 导航返回 */
    object NavigateBack : WelfareEffect()
    
    /** 刷新WebView */
    object RefreshWebView : WelfareEffect()
    
    /** WebView后退 */
    object WebViewGoBack : WelfareEffect()
    
    /** WebView前进 */
    object WebViewGoForward : WelfareEffect()
    
    /** 加载指定URL */
    data class LoadWebViewUrl(val url: String) : WelfareEffect()
    
    /** 显示加载错误对话框 */
    data class ShowErrorDialog(val title: String, val message: String) : WelfareEffect()
    
    /** 触觉反馈 */
    object TriggerHapticFeedback : WelfareEffect()
    
    /** 显示SSL错误对话框 */
    data class ShowSslErrorDialog(val message: String) : WelfareEffect()
    
    /** 显示HTTP错误页面 */
    data class ShowHttpErrorPage(val errorCode: Int, val description: String) : WelfareEffect()
    
    /** 显示网络错误提示 */
    data class ShowNetworkErrorSnackbar(val message: String) : WelfareEffect()
    
    /** 用浏览器打开URL */
    data class OpenInBrowser(val url: String) : WelfareEffect()
}