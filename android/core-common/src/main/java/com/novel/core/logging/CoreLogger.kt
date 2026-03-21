package com.novel.core.logging

import androidx.compose.runtime.Stable
import com.novel.core.StableThrowable
import com.novel.core.common.BuildConfig
import timber.log.Timber

@Stable
object CoreLogger {

    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(tag).d(message)
        }
    }

    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }

    fun w(tag: String, message: String, throwable: Throwable) {
        Timber.tag(tag).w(throwable, message)
    }

    fun w(tag: String, message: String, stableThrowable: StableThrowable) {
        val warningMessage = buildString {
            append(message)
            stableThrowable.message?.let { append(" - ").append(it) }
            stableThrowable.cause?.let { append(" (Caused by: ").append(it).append(")") }
        }
        Timber.tag(tag).w(warningMessage)
    }

    fun e(tag: String, message: String) {
        Timber.tag(tag).e(message)
    }

    fun e(tag: String, message: String, throwable: Throwable) {
        Timber.tag(tag).e(throwable, message)
    }

    fun e(tag: String, message: String, stableThrowable: StableThrowable) {
        val errorMessage = buildString {
            append(message)
            stableThrowable.message?.let { append(" - ").append(it) }
            stableThrowable.cause?.let { append(" (Caused by: ").append(it).append(")") }
        }
        Timber.tag(tag).e(errorMessage)
    }

    fun v(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(tag).v(message)
        }
    }

    fun performance(tag: String, operationName: String, durationMs: Long) {
        if (BuildConfig.DEBUG) {
            Timber.tag(tag).d("Performance: $operationName took ${durationMs}ms")
        }
    }
}
