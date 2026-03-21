package com.novel.core.mvi

import androidx.compose.runtime.Stable

interface MviReducer<I : MviIntent, S : MviState> {
    fun reduce(currentState: S, intent: I): S
}

interface MviReducerWithEffect<I : MviIntent, S : MviState, E : MviEffect> {
    fun reduce(currentState: S, intent: I): ReduceResult<S, E>
}

@Stable
data class ReduceResult<S : MviState, E : MviEffect>(
    val newState: S,
    val effect: E? = null
)
