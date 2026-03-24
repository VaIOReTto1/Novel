package com.novel.page.welfare.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WelfareReducerTest {

    private val reducer = WelfareReducer()

    @Test
    fun reduce_initializePage_marksLoadingAndEmitsDefaultUrl() {
        val currentState = WelfareState()

        val result = reducer.reduce(currentState, WelfareIntent.InitializePage)

        assertThat(result.newState.isLoading).isTrue()
        assertThat(result.newState.isInitialized).isFalse()
        assertThat(result.effect).isEqualTo(WelfareEffect.LoadWebViewUrl(currentState.currentUrl))
    }

    @Test
    fun reduce_pageError_clearsLoadingAndEmitsDialog() {
        val result = reducer.reduce(WelfareState(isPageLoading = true), WelfareIntent.OnPageError("boom"))

        assertThat(result.newState.isPageLoading).isFalse()
        assertThat(result.newState.pageError).isEqualTo("boom")
        assertThat(result.effect).isEqualTo(
            WelfareEffect.ShowErrorDialog(
                title = "页面加载失败",
                message = "boom",
            ),
        )
    }
}
