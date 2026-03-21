package com.novel.core

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

@Stable
class StableStateFlow<T>(
    private val delegate: StateFlow<T>
) : StateFlow<T> by delegate

@Stable
class StableFlow<T>(
    private val delegate: Flow<T>
) : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        delegate.collect(collector)
    }
}

fun <T> StateFlow<T>.asStable(): StateFlow<T> = StableStateFlow(this)

fun <T> Flow<T>.asStable(): Flow<T> = StableFlow(this)
