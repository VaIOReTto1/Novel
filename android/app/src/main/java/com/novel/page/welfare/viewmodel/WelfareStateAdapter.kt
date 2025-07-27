package com.novel.page.welfare.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.StateFlow

/**
 * Welfare状态适配器
 * 
 * 提供便利的状态访问方法，优化Compose重组性能
 * 通过细粒度的状态订阅减少不必要的重组
 */
class WelfareStateAdapter(
    stateFlow: StateFlow<WelfareState>
) : StateAdapter<WelfareState>(stateFlow) {
    /**
     * 获取完整状态 - 一次性收集，避免重复订阅
     */
    @Composable
    private fun getState(): State<WelfareState> {
        return stateFlow.collectAsStateWithLifecycle()
    }
    
    /**
     * 获取当前URL状态
     */
    @Composable
    fun currentUrlState(): State<String> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.currentUrl }
        }
    }
    
    /**
     * 获取页面标题状态
     */
    @Composable
    fun titleState(): State<String> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.title }
        }
    }
    
    /**
     * 获取加载进度状态
     */
    @Composable
    fun loadingProgressState(): State<Int> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.loadingProgress }
        }
    }
    
    /**
     * 获取是否可以后退状态
     */
    @Composable
    fun canGoBackState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.canGoBack }
        }
    }
    
    /**
     * 获取是否可以前进状态
     */
    @Composable
    fun canGoForwardState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.canGoForward }
        }
    }
    
    /**
     * 获取页面加载状态
     */
    @Composable
    fun isPageLoadingState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.isPageLoading }
        }
    }
    
    /**
     * 获取页面错误状态
     */
    @Composable
    fun pageErrorState(): State<String?> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.pageError }
        }
    }
    
    /**
     * 获取是否显示进度条状态
     */
    @Composable
    fun shouldShowProgressState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.shouldShowProgress }
        }
    }
    
    /**
     * 获取是否显示错误状态
     */
    @Composable
    fun shouldShowErrorState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.shouldShowError }
        }
    }
    
    /**
     * 获取显示错误信息状态
     */
    @Composable
    fun displayErrorState(): State<String?> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.displayError }
        }
    }
    
    /**
     * 获取是否初始化完成状态
     */
    @Composable
    fun isInitializedState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) {
            derivedStateOf { state.value.isInitialized }
        }
    }
}