package com.novel.page.welfare.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.StateFlow

class WelfareStateAdapter(
    stateFlow: StateFlow<WelfareState>,
) : StateAdapter<WelfareState>(stateFlow) {

    @Composable
    private fun getState(): State<WelfareState> {
        return stateFlow.collectAsStateWithLifecycle()
    }

    @Composable
    fun currentUrlState(): State<String> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.currentUrl } }
    }

    @Composable
    fun titleState(): State<String> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.title } }
    }

    @Composable
    fun loadingProgressState(): State<Int> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.loadingProgress } }
    }

    @Composable
    fun canGoBackState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.canGoBack } }
    }

    @Composable
    fun canGoForwardState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.canGoForward } }
    }

    @Composable
    fun isPageLoadingState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.isPageLoading } }
    }

    @Composable
    fun pageErrorState(): State<String?> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.pageError } }
    }

    @Composable
    fun shouldShowProgressState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.shouldShowProgress } }
    }

    @Composable
    fun shouldShowErrorState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.shouldShowError } }
    }

    @Composable
    fun displayErrorState(): State<String?> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.displayError } }
    }

    @Composable
    fun isInitializedState(): State<Boolean> {
        val state = getState()
        return remember(state.value.version) { derivedStateOf { state.value.isInitialized } }
    }
}
