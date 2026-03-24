package com.novel.page.welfare.viewmodel

import androidx.lifecycle.viewModelScope
import com.novel.core.logging.CoreLogger
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.welfare.usecase.InitializeWelfarePageUseCase
import com.novel.page.welfare.utils.WelfarePerformanceMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelfareViewModel @Inject constructor(
    private val initializeWelfarePageUseCase: InitializeWelfarePageUseCase,
) : BaseMviViewModel<WelfareIntent, WelfareState, WelfareEffect>() {

    companion object {
        private const val TAG = "WelfareViewModel"
    }

    val adapter = WelfareStateAdapter(state)
    private val performanceMonitor = WelfarePerformanceMonitor.getInstance()

    init {
        CoreLogger.d(TAG, "WelfareViewModel初始化 - MVI版本")
        sendIntent(WelfareIntent.InitializePage)
    }

    override fun createInitialState(): WelfareState = WelfareState()

    override fun getReducer(): MviReducer<WelfareIntent, WelfareState> {
        val effectReducer = WelfareReducer()
        return object : MviReducer<WelfareIntent, WelfareState> {
            override fun reduce(currentState: WelfareState, intent: WelfareIntent): WelfareState {
                val result = effectReducer.reduce(currentState, intent)
                result.effect?.let(::sendEffect)
                return result.newState
            }
        }
    }

    override fun onIntentProcessed(intent: WelfareIntent, newState: WelfareState) {
        super.onIntentProcessed(intent, newState)
        viewModelScope.launch {
            when (intent) {
                WelfareIntent.InitializePage -> handleInitializePage()
                else -> {
                }
            }
        }
    }

    private suspend fun handleInitializePage() {
        try {
            CoreLogger.d(TAG, "开始初始化福利页面")
            performanceMonitor.recordViewModelAction("initialize_start")

            val result = initializeWelfarePageUseCase(Unit)
            updateState(
                getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    isLoading = false,
                    isInitialized = true,
                    title = result.title,
                    currentUrl = result.defaultUrl,
                ),
            )

            performanceMonitor.recordViewModelAction("initialize_complete")
            CoreLogger.d(TAG, "福利页面初始化完成")
        } catch (e: Exception) {
            CoreLogger.e(TAG, "福利页面初始化失败", e)
            performanceMonitor.recordError("initialize_error", e.localizedMessage ?: "Unknown error")

            updateState(
                getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    isLoading = false,
                    error = "页面初始化失败：${e.localizedMessage}",
                ),
            )
            sendEffect(WelfareEffect.ShowToast("初始化失败：${e.localizedMessage}"))
            sendEffect(WelfareEffect.TriggerHapticFeedback)
        }
    }

    fun updateNavigationState(canGoBack: Boolean, canGoForward: Boolean) {
        updateState(
            getCurrentState().copy(
                version = getCurrentState().version + 1,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
            ),
        )
    }

    fun updatePageTitle(title: String) {
        updateState(
            getCurrentState().copy(
                version = getCurrentState().version + 1,
                title = title.ifBlank { "福利页面" },
            ),
        )
    }

    fun updateCurrentUrl(url: String) {
        if (url.isBlank() || url == "about:blank" || url.startsWith("data:")) {
            CoreLogger.d(TAG, "忽略无效URL更新: $url")
            return
        }

        val currentState = getCurrentState()
        if (currentState.currentUrl != url) {
            CoreLogger.d(TAG, "更新URL: ${currentState.currentUrl} -> $url")
            updateState(
                currentState.copy(
                    version = currentState.version + 1,
                    currentUrl = url,
                ),
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        CoreLogger.d(TAG, "WelfareViewModel清理资源")
    }
}
