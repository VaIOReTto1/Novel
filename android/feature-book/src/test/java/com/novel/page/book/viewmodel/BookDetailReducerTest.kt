package com.novel.page.book.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BookDetailReducerTest {

    @Test
    fun reduce_toggleDescriptionExpanded_flipsExpandedState() {
        val reducer = BookDetailReducer()

        val firstResult = reducer.reduce(
            currentState = BookDetailState(isDescriptionExpanded = false),
            intent = BookDetailIntent.ToggleDescriptionExpanded,
        )
        val secondResult = reducer.reduce(
            currentState = firstResult.newState,
            intent = BookDetailIntent.ToggleDescriptionExpanded,
        )

        assertThat(firstResult.newState.isDescriptionExpanded).isTrue()
        assertThat(secondResult.newState.isDescriptionExpanded).isFalse()
    }
}
