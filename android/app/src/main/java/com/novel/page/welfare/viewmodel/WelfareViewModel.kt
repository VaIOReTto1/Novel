package com.novel.page.welfare.viewmodel

import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.welfare.usecase.InitializeWelfarePageUseCase
import com.novel.page.welfare.utils.WelfarePerformanceMonitor
import com.novel.utils.TimberLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Welfare模块ViewModel - MVI架构版本
 *
 * 基于统一MVI框架实现，职责：
 * - 继承BaseMviViewModel，使用统一状态管理
 * - 协调UseCase处理业务逻辑
 * - 处理Intent到UseCase的调用转换
 * - 管理WebView相关的生命周期
 */
@HiltViewModel
class WelfareViewModel @Inject constructor(
    /** 初始化页面UseCase */
    private val initializeWelfarePageUseCase: InitializeWelfarePageUseCase
) : BaseMviViewModel<WelfareIntent, WelfareState, WelfareEffect>() {

    companion object {
        private const val TAG = "WelfareViewModel"
    }

    /** 状态适配器，提供便利的状态访问方法 */
    val adapter = WelfareStateAdapter(state)
    
    /** 性能监控器 */
    private val performanceMonitor = WelfarePerformanceMonitor.getInstance()

    init {
        TimberLogger.d(TAG, "WelfareViewModel初始化 - MVI版本")
        // 自动初始化页面数据
        sendIntent(WelfareIntent.InitializePage)
    }

    override fun createInitialState(): WelfareState {
        return WelfareState()
    }

    override fun getReducer(): MviReducer<WelfareIntent, WelfareState> {
        // 返回一个适配器，将MviReducerWithEffect适配为MviReducer
        val effectReducer = WelfareReducer()
        return object : MviReducer<WelfareIntent, WelfareState> {
            override fun reduce(currentState: WelfareState, intent: WelfareIntent): WelfareState {
                val result = effectReducer.reduce(currentState, intent)
                // 在这里处理副作用
                result.effect?.let { effect ->
                    sendEffect(effect)
                }
                return result.newState
            }
        }
    }

    override fun onIntentProcessed(intent: WelfareIntent, newState: WelfareState) {
        super.onIntentProcessed(intent, newState)

        // 根据Intent类型触发相应的UseCase
        viewModelScope.launch {
            when (intent) {
                is WelfareIntent.InitializePage -> handleInitializePage()
                else -> {
                    // 其他Intent由Reducer直接处理，无需额外UseCase调用
                }
            }
        }
    }

    /**
     * 处理页面初始化
     */
    private suspend fun handleInitializePage() {
        try {
            TimberLogger.d(TAG, "开始初始化福利页面")
            performanceMonitor.recordViewModelAction("initialize_start")

            val result = initializeWelfarePageUseCase(Unit)

            // 更新初始化完成状态
            val newState = getCurrentState().copy(
                version = getCurrentState().version + 1,
                isLoading = false,
                isInitialized = true,
                title = result.title,
                currentUrl = result.defaultUrl
            )
            updateState(newState)
            
            performanceMonitor.recordViewModelAction("initialize_complete")
            TimberLogger.d(TAG, "福利页面初始化完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "福利页面初始化失败", e)
            performanceMonitor.recordError("initialize_error", e.localizedMessage ?: "Unknown error")
            
            val newState = getCurrentState().copy(
                version = getCurrentState().version + 1,
                isLoading = false,
                error = "页面初始化失败：${e.localizedMessage}"
            )
            updateState(newState)
            sendEffect(WelfareEffect.ShowToast("初始化失败：${e.localizedMessage}"))
            sendEffect(WelfareEffect.TriggerHapticFeedback)
        }
    }

    /**
     * 更新WebView导航状态
     * 由WebView组件调用
     */
    fun updateNavigationState(canGoBack: Boolean, canGoForward: Boolean) {
        val newState = getCurrentState().copy(
            version = getCurrentState().version + 1,
            canGoBack = canGoBack,
            canGoForward = canGoForward
        )
        updateState(newState)
    }

    /**
     * 更新页面标题
     * 由WebView组件调用
     */
    fun updatePageTitle(title: String) {
        val newState = getCurrentState().copy(
            version = getCurrentState().version + 1,
            title = title.ifBlank { "福利页面" }
        )
        updateState(newState)
    }

    /**
     * 更新当前URL
     * 由WebView组件调用
     * 过滤掉about:blank等无效URL，避免状态混乱
     */
    fun updateCurrentUrl(url: String) {
        // 过滤掉about:blank和其他无效URL
        if (url.isBlank() || url == "about:blank" || url.startsWith("data:")) {
            TimberLogger.d(TAG, "忽略无效URL更新: $url")
            return
        }
        
        val currentState = getCurrentState()
        // 只有当URL真正发生变化时才更新状态
        if (currentState.currentUrl != url) {
            TimberLogger.d(TAG, "更新URL: ${currentState.currentUrl} -> $url")
            val newState = currentState.copy(
                version = currentState.version + 1,
                currentUrl = url
            )
            updateState(newState)
        }
    }

    override fun onCleared() {
        super.onCleared()
        TimberLogger.d(TAG, "WelfareViewModel清理资源")
    }
}
