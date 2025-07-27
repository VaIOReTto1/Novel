package com.novel.page.welfare.viewmodel

import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.utils.TimberLogger

/**
 * Welfare模块状态变更处理器
 * 
 * 负责处理WelfareIntent并产生新的WelfareState和WelfareEffect
 * 遵循纯函数原则，不包含副作用逻辑
 */
class WelfareReducer : MviReducerWithEffect<WelfareIntent, WelfareState, WelfareEffect> {
    
    companion object {
        private const val TAG = "WelfareReducer"
    }
    
    override fun reduce(
        currentState: WelfareState,
        intent: WelfareIntent
    ): ReduceResult<WelfareState, WelfareEffect> {
        
        TimberLogger.d(TAG, "处理Intent: ${intent::class.simpleName}")
        
        return when (intent) {
            is WelfareIntent.InitializePage -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true,
                        isInitialized = false,
                        error = null
                    ),
                    effect = WelfareEffect.LoadWebViewUrl(currentState.currentUrl)
                )
            }
            
            is WelfareIntent.LoadUrl -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        currentUrl = intent.url,
                        isPageLoading = true,
                        pageError = null,
                        loadingProgress = 0
                    ),
                    effect = WelfareEffect.LoadWebViewUrl(intent.url)
                )
            }
            
            is WelfareIntent.RefreshPage -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isPageLoading = true,
                        pageError = null,
                        loadingProgress = 0
                    ),
                    effect = WelfareEffect.RefreshWebView
                )
            }
            
            is WelfareIntent.GoBack -> {
                if (currentState.canGoBack) {
                    ReduceResult(
                        newState = currentState.copy(
                            version = currentState.version + 1
                        ),
                        effect = WelfareEffect.WebViewGoBack
                    )
                } else {
                    ReduceResult(
                        newState = currentState,
                        effect = WelfareEffect.NavigateBack
                    )
                }
            }
            
            is WelfareIntent.GoForward -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1
                    ),
                    effect = if (currentState.canGoForward) {
                        WelfareEffect.WebViewGoForward
                    } else {
                        WelfareEffect.ShowToast("无法前进")
                    }
                )
            }
            
            is WelfareIntent.OnPageStarted -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isPageLoading = true,
                        pageError = null,
                        loadingProgress = 0
                    )
                )
            }
            
            is WelfareIntent.OnPageFinished -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        isPageLoading = false,
                        isInitialized = true,
                        loadingProgress = 100,
                        pageError = null
                    )
                )
            }
            
            is WelfareIntent.OnPageError -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        isPageLoading = false,
                        pageError = intent.errorMessage,
                        loadingProgress = 0
                    ),
                    effect = WelfareEffect.ShowErrorDialog(
                        title = "页面加载失败",
                        message = intent.errorMessage
                    )
                )
            }
            
            is WelfareIntent.OnSslError -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isPageLoading = false,
                        pageError = intent.errorMessage,
                        loadingProgress = 0
                    ),
                    effect = WelfareEffect.ShowSslErrorDialog(intent.errorMessage)
                )
            }
            
            is WelfareIntent.OnHttpError -> {
                val errorMessage = "HTTP错误 ${intent.errorCode}: ${intent.description}"
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isPageLoading = false,
                        pageError = errorMessage,
                        loadingProgress = 0
                    ),
                    effect = WelfareEffect.ShowHttpErrorPage(intent.errorCode, intent.description)
                )
            }
            
            is WelfareIntent.OnNetworkError -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isPageLoading = false,
                        pageError = intent.errorMessage,
                        loadingProgress = 0
                    ),
                    effect = WelfareEffect.ShowNetworkErrorSnackbar(intent.errorMessage)
                )
            }
            
            is WelfareIntent.UpdateProgress -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        loadingProgress = intent.progress
                    )
                )
            }
            
            is WelfareIntent.ClearError -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        error = null,
                        pageError = null
                    )
                )
            }
            
            is WelfareIntent.NavigateBack -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1
                    ),
                    effect = WelfareEffect.NavigateBack
                )
            }
        }
    }
}