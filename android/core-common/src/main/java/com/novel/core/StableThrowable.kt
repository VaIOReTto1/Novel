package com.novel.core

import androidx.compose.runtime.Stable

@Stable
data class StableThrowable(
    val message: String?,
    val cause: String?
) {
    constructor(throwable: Throwable) : this(
        message = throwable.message,
        cause = throwable.cause?.toString()
    )
}
