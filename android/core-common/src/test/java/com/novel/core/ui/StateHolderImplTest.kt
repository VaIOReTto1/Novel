package com.novel.core.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StateHolderImplTest {

    @Test
    fun exposesSuccessStateWhenDataExistsWithoutError() {
        val stateHolder = StateHolderImpl(
            data = "book",
            isLoading = false,
            error = null,
        )

        assertThat(stateHolder.hasError).isFalse()
        assertThat(stateHolder.isSuccess).isTrue()
        assertThat(stateHolder.isEmpty).isFalse()
    }

    @Test
    fun exposesErrorStateWhenErrorExists() {
        val stateHolder = StateHolderImpl(
            data = "book",
            isLoading = false,
            error = "boom",
        )

        assertThat(stateHolder.hasError).isTrue()
        assertThat(stateHolder.isSuccess).isFalse()
    }
}
