package com.novel.page.welfare.viewmodel

import androidx.compose.runtime.Stable
import com.novel.core.mvi.MviEffect
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.page.welfare.utils.WelfareWebSecurityConfig

sealed class WelfareIntent : MviIntent {
    data object InitializePage : WelfareIntent()
    data class LoadUrl(val url: String) : WelfareIntent()
    data object RefreshPage : WelfareIntent()
    data object GoBack : WelfareIntent()
    data object GoForward : WelfareIntent()
    data object OnPageStarted : WelfareIntent()
    data object OnPageFinished : WelfareIntent()
    data class OnPageError(val errorMessage: String) : WelfareIntent()
    data class OnSslError(val errorMessage: String) : WelfareIntent()
    data class OnHttpError(val errorCode: Int, val description: String) : WelfareIntent()
    data class OnNetworkError(val errorMessage: String) : WelfareIntent()
    data class UpdateProgress(val progress: Int) : WelfareIntent()
    data object ClearError : WelfareIntent()
    data class OpenExternalUrl(val url: String) : WelfareIntent()
    data object NavigateBack : WelfareIntent()
}

@Stable
data class WelfareState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val currentUrl: String = WelfareWebSecurityConfig.DEFAULT_URL,
    val title: String = WelfareWebSecurityConfig.DEFAULT_TITLE,
    val loadingProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isPageLoading: Boolean = false,
    val pageError: String? = null,
    val isInitialized: Boolean = false,
) : MviState {

    override val isEmpty: Boolean
        get() = currentUrl.isBlank()

    override val isSuccess: Boolean
        get() = !isLoading && !hasError && !isEmpty && isInitialized && !isPageLoading

    val shouldShowProgress: Boolean
        get() = isPageLoading && loadingProgress in 1..99

    val shouldShowError: Boolean
        get() = pageError != null

    val displayError: String?
        get() = pageError ?: error
}

sealed class WelfareEffect : MviEffect {
    data class ShowToast(val message: String) : WelfareEffect()
    data object NavigateBack : WelfareEffect()
    data object RefreshWebView : WelfareEffect()
    data object WebViewGoBack : WelfareEffect()
    data object WebViewGoForward : WelfareEffect()
    data class LoadWebViewUrl(val url: String) : WelfareEffect()
    data class ShowErrorDialog(val title: String, val message: String) : WelfareEffect()
    data object TriggerHapticFeedback : WelfareEffect()
    data class ShowSslErrorDialog(val message: String) : WelfareEffect()
    data class ShowHttpErrorPage(val errorCode: Int, val description: String) : WelfareEffect()
    data class ShowNetworkErrorSnackbar(val message: String) : WelfareEffect()
    data class OpenInBrowser(val url: String) : WelfareEffect()
}
