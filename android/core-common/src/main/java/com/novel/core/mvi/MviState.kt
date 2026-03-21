package com.novel.core.mvi

import androidx.compose.runtime.Stable

@Stable
interface MviState {
    val version: Long
    val isLoading: Boolean get() = false
    val error: String? get() = null
    val hasError: Boolean get() = error != null
    val isEmpty: Boolean get() = false
    val isSuccess: Boolean get() = !isLoading && !hasError && !isEmpty
}
