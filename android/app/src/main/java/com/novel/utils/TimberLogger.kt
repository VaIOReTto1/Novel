package com.novel.utils

import androidx.compose.runtime.Stable
import com.novel.BuildConfig
import com.novel.core.StableThrowable
import timber.log.Timber

/**
 * 统一Timber日志工具类
 * 
 * 基于现有的AndroidServiceLogger模式，为整个应用提供统一的日志记录接口：
 * - 自动处理Release版本的日志过滤
 * - 保留原始TAG系统以便调试
 * - 与现有的ServiceLogger接口兼容
 * - 支持性能日志和错误日志
 * 
 * 使用方式：
 * ```kotlin
 * class SomeClass {
 *     companion object {
 *         private const val TAG = "SomeClass"
 *     }
 *     
 *     fun doSomething() {
 *         TimberLogger.d(TAG, "开始执行操作")
 *         // ... 
 *         TimberLogger.i(TAG, "操作完成")
 *     }
 * }
 * ```
 */
@Stable
object TimberLogger {
    
    /**
     * 调试日志
     * 仅在Debug版本中输出，Release版本自动过滤
     */
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(tag).d(message)
        }
    }
    
    /**
     * 信息日志
     * 在所有版本中输出
     */
    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }
    
    /**
     * 警告日志
     * 在所有版本中输出
     */
    fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }

    fun w(tag: String, message: String, throwable: Throwable) {
        Timber.tag(tag).w(throwable, message)
    }
    
    /**
     * 警告日志（带StableThrowable）
     * 在所有版本中输出，专门用于Compose稳定的异常处理
     */
    fun w(tag: String, message: String, stableThrowable: StableThrowable) {
        val warningMessage = buildString {
            append(message)
            stableThrowable.message?.let { append(" - ").append(it) }
            stableThrowable.cause?.let { append(" (Caused by: ").append(it).append(")") }
        }
        Timber.tag(tag).w(warningMessage)
    }
    
    /**
     * 错误日志
     * 在所有版本中输出
     */
    fun e(tag: String, message: String) {
        Timber.tag(tag).e(message)
    }
    
    /**
     * 错误日志（带异常）
     * 在所有版本中输出
     */
    fun e(tag: String, message: String, throwable: Throwable) {
        Timber.tag(tag).e(throwable, message)
    }
    
    /**
     * 错误日志（带StableThrowable）
     * 在所有版本中输出，专门用于Compose稳定的异常处理
     */
    fun e(tag: String, message: String, stableThrowable: StableThrowable) {
        val errorMessage = buildString {
            append(message)
            stableThrowable.message?.let { append(" - ").append(it) }
            stableThrowable.cause?.let { append(" (Caused by: ").append(it).append(")") }
        }
        Timber.tag(tag).e(errorMessage)
    }
    
    /**
     * 详细日志
     * 仅在Debug版本中输出，Release版本自动过滤
     */
    fun v(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(tag).v(message)
        }
    }
    
    /**
     * 性能日志
     * 仅在Debug版本中输出，用于性能监控和调试
     */
    fun performance(tag: String, operationName: String, durationMs: Long) {
        if (BuildConfig.DEBUG) {
            Timber.tag(tag).d("Performance: $operationName took ${durationMs}ms")
        }
    }
}
