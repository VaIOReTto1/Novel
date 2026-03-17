package com.novel.rn.bridge

import com.facebook.react.bridge.Promise
import com.novel.core.result.AppError

data class BridgePromiseError(
    val code: String,
    val message: String,
    val appError: AppError
)

object BridgePromiseErrorMapper {
    fun map(
        throwable: Throwable,
        defaultCode: String,
        defaultMessagePrefix: String,
        timeoutMessage: String = "操作超时"
    ): BridgePromiseError {
        val appError = AppError.fromThrowable(throwable)
        return when (appError) {
            is AppError.Timeout -> BridgePromiseError(
                code = "TIMEOUT_ERROR",
                message = timeoutMessage,
                appError = appError
            )

            else -> BridgePromiseError(
                code = defaultCode,
                message = "$defaultMessagePrefix: ${appError.message ?: "未知错误"}",
                appError = appError
            )
        }
    }
}

fun Promise.rejectMapped(
    throwable: Throwable,
    defaultCode: String,
    defaultMessagePrefix: String,
    timeoutMessage: String = "操作超时"
) {
    val mapped = BridgePromiseErrorMapper.map(
        throwable = throwable,
        defaultCode = defaultCode,
        defaultMessagePrefix = defaultMessagePrefix,
        timeoutMessage = timeoutMessage
    )
    reject(mapped.code, mapped.message, throwable)
}
