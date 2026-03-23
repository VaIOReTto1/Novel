package com.novel.core.adapter

import com.google.common.truth.Truth.assertThat
import com.novel.core.mvi.MviState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class StateAdapterTest {

    @Test
    fun getCurrentSnapshot_exposesLatestStateValues() {
        val adapter = TestStateAdapter(
            MutableStateFlow(
                TestState(
                    version = 3L,
                    isLoading = true,
                    error = "boom",
                ),
            ),
        )

        assertThat(adapter.getCurrentVersion()).isEqualTo(3L)
        assertThat(adapter.isCurrentlyLoading()).isTrue()
        assertThat(adapter.getCurrentError()).isEqualTo("boom")
    }

    @Test
    fun stateComparator_describesChangedFields() {
        val summary = StateComparator.getChangeSummary(
            oldState = TestState(version = 1L, isLoading = false, error = null),
            newState = TestState(version = 2L, isLoading = true, error = "boom"),
        )

        assertThat(summary).contains("版本")
        assertThat(summary).contains("加载状态")
        assertThat(summary).contains("错误状态")
    }

    private class TestStateAdapter(
        stateFlow: MutableStateFlow<TestState>,
    ) : StateAdapter<TestState>(stateFlow)

    private data class TestState(
        override val version: Long = 0L,
        override val isLoading: Boolean = false,
        override val error: String? = null,
    ) : MviState
}
