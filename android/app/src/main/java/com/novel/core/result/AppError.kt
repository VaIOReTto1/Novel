package com.novel.core.result

import com.google.gson.JsonParseException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

sealed interface AppError {
    val message: String?
    val cause: Throwable?

    data class Network(
        override val message: String?,
        override val cause: Throwable? = null
    ) : AppError

    data class Timeout(
        override val message: String?,
        override val cause: Throwable? = null
    ) : AppError

    data class Serialization(
        override val message: String?,
        override val cause: Throwable? = null
    ) : AppError

    data class Unexpected(
        override val message: String?,
        override val cause: Throwable? = null
    ) : AppError

    companion object {
        fun fromThrowable(throwable: Throwable): AppError = when (throwable) {
            is SocketTimeoutException,
            is TimeoutException -> Timeout(throwable.message, throwable)

            is JsonParseException -> Serialization(throwable.message, throwable)

            is IOException -> Network(throwable.message, throwable)

            else -> Unexpected(throwable.message, throwable)
        }
    }
}

sealed interface DataResult<out T> {
    data class Success<T>(val value: T) : DataResult<T>
    data class Failure(val error: AppError) : DataResult<Nothing>

    fun <R> map(transform: (T) -> R): DataResult<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    fun errorOrNull(): AppError? = when (this) {
        is Success -> null
        is Failure -> error
    }
}
