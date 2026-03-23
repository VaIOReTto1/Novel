package com.novel.core.ui

import androidx.compose.runtime.Stable

@Stable
data class StateHolderImpl<T>(
    val data: T,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val hasError: Boolean get() = error != null

    val isEmpty: Boolean get() = data == null && !isLoading && !hasError

    val isSuccess: Boolean get() = data != null && !isLoading && !hasError
}
