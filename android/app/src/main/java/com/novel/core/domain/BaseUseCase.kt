package com.novel.core.domain

import com.novel.utils.TimberLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * UseCase基础抽象类
 * 
 * 提供统一的业务逻辑执行框架：
 * - 协程调度器管理
 * - 错误处理和重试机制
 * - 超时控制
 * - 日志记录
 * 
 * @param Params 输入参数类型
 * @param Result 返回结果类型
 */
abstract class BaseUseCase<in Params, out Result> {
    
    companion object {
        private const val TAG = "BaseUseCase"
        private val DEFAULT_TIMEOUT = 30.seconds
    }
    
    /**
     * 执行UseCase的主要逻辑
     * 子类必须实现此方法
     */
    protected abstract suspend fun execute(params: Params): Result
    
    /**
     * 获取执行UseCase的协程调度器
     * 默认使用IO调度器，子类可覆盖
     */
    protected open fun getDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    /**
     * 获取超时时长
     * 默认30秒，子类可覆盖
     */
    protected open fun getTimeout(): Duration = DEFAULT_TIMEOUT
    
    /**
     * 执行UseCase
     * 
     * @param params 输入参数
     * @return 执行结果
     */
    suspend operator fun invoke(params: Params): Result {
        val startTime = System.currentTimeMillis()
        TimberLogger.d(TAG, "开始执行UseCase: ${this::class.simpleName}")
        
        return try {
            withTimeout(getTimeout()) {
                withContext(getDispatcher()) {
                    execute(params)
                }
            }.also {
                val duration = System.currentTimeMillis() - startTime
                TimberLogger.d(TAG, "UseCase执行成功: ${this::class.simpleName}, 耗时: ${duration}ms")
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            TimberLogger.e(TAG, "UseCase执行失败: ${this::class.simpleName}, 耗时: ${duration}ms", e)
            throw e
        }
    }
}

/**
 * 无参数UseCase基础类
 */
abstract class BaseUseCaseNoParams<out Result> : BaseUseCase<Unit, Result>() {
    
    suspend operator fun invoke(): Result = invoke(Unit)
}

/**
 * 流式UseCase基础类
 * 
 * 用于需要持续数据流的业务场景
 * 
 * @param Params 输入参数类型
 * @param Result 流数据类型
 */
abstract class FlowUseCase<in Params, out Result> {
    
    companion object {
        private const val TAG = "FlowUseCase"
        private const val DEFAULT_RETRY_ATTEMPTS = 3L
    }
    
    /**
     * 执行流式UseCase的主要逻辑
     * 子类必须实现此方法
     */
    protected abstract suspend fun execute(params: Params): Flow<Result>
    
    /**
     * 获取执行UseCase的协程调度器
     * 默认使用IO调度器，子类可覆盖
     */
    protected open fun getDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    /**
     * 获取重试次数
     * 默认3次，子类可覆盖
     */
    protected open fun getRetryAttempts(): Long = DEFAULT_RETRY_ATTEMPTS
    
    /**
     * 执行流式UseCase
     * 
     * @param params 输入参数
     * @return 数据流
     */
    operator fun invoke(params: Params): Flow<Result> {
        TimberLogger.d(TAG, "开始执行FlowUseCase: ${this::class.simpleName}")
        
        return flow {
            execute(params).collect { result ->
                emit(result)
            }
        }
        .retry(getRetryAttempts()) { cause ->
            TimberLogger.e(TAG, "FlowUseCase执行失败，准备重试: ${this::class.simpleName}", cause)
            true
        }
        .flowOn(getDispatcher())
    }
}

/**
 * 无参数流式UseCase基础类
 */
abstract class FlowUseCaseNoParams<out Result> : FlowUseCase<Unit, Result>() {
    
    operator fun invoke(): Flow<Result> = invoke(Unit)
}

/**
 * UseCase执行器
 * 
 * 提供UseCase的统一执行、组合和管理功能
 */
class UseCaseExecutor {
    
    companion object {
        private const val TAG = "UseCaseExecutor"
    }
    
    /**
     * 并行执行多个UseCase
     * 
     * @param useCases UseCase列表
     * @return 所有UseCase的执行结果
     */
    suspend fun <T> executeParallel(
        useCases: List<suspend () -> T>
    ): List<T> {
        TimberLogger.d(TAG, "并行执行${useCases.size}个UseCase")
        
        return kotlinx.coroutines.coroutineScope {
            useCases.map { useCase ->
                async { useCase() }
            }.map { it.await() }
        }
    }
    
    /**
     * 串行执行多个UseCase
     * 
     * @param useCases UseCase列表
     * @return 所有UseCase的执行结果
     */
    suspend fun <T> executeSequential(
        useCases: List<suspend () -> T>
    ): List<T> {
        TimberLogger.d(TAG, "串行执行${useCases.size}个UseCase")
        
        return useCases.map { useCase ->
            useCase()
        }
    }
    
    /**
     * 执行UseCase链
     * 前一个UseCase的结果作为后一个UseCase的输入
     * 
     * @param initialParams 初始参数
     * @param useCases UseCase链
     * @return 最终结果
     */
    suspend fun <T> executeChain(
        initialParams: T,
        vararg useCases: suspend (T) -> T
    ): T {
        TimberLogger.d(TAG, "执行UseCase链，包含${useCases.size}个UseCase")
        
        return useCases.fold(initialParams) { acc, useCase ->
            useCase(acc)
        }
    }
}

/**
 * UseCase组合器
 * 
 * 支持多个UseCase的复杂组合逻辑
 */
class ComposeUseCase {
    
    companion object {
        private const val TAG = "ComposeUseCase"
    }
    
    /**
     * 合并两个UseCase的结果
     * 
     * @param useCase1 第一个UseCase
     * @param useCase2 第二个UseCase
     * @param combiner 结果合并函数
     * @return 合并后的结果
     */
    suspend fun <T1, T2, R> combine(
        useCase1: suspend () -> T1,
        useCase2: suspend () -> T2,
        combiner: (T1, T2) -> R
    ): R {
        TimberLogger.d(TAG, "合并两个UseCase的结果")
        
        return kotlinx.coroutines.coroutineScope {
            val result1 = async { useCase1() }
            val result2 = async { useCase2() }
            combiner(result1.await(), result2.await())
        }
    }
    
    /**
     * 条件执行UseCase
     * 
     * @param condition 条件判断
     * @param trueUseCase 条件为真时执行的UseCase
     * @param falseUseCase 条件为假时执行的UseCase
     * @return 执行结果
     */
    suspend fun <T> conditional(
        condition: Boolean,
        trueUseCase: suspend () -> T,
        falseUseCase: suspend () -> T
    ): T {
        TimberLogger.d(TAG, "条件执行UseCase: condition=$condition")
        
        return if (condition) {
            trueUseCase()
        } else {
            falseUseCase()
        }
    }
    
    /**
     * 带重试机制的UseCase执行
     * 
     * @param maxRetries 最大重试次数
     * @param delay 重试延迟
     * @param useCase 要执行的UseCase
     * @return 执行结果
     */
    suspend fun <T> withRetry(
        maxRetries: Int = 3,
        delay: Duration = 1.seconds,
        useCase: suspend () -> T
    ): T {
        TimberLogger.d(TAG, "带重试机制执行UseCase，最大重试次数: $maxRetries")
        
        var lastException: Exception? = null
        
        repeat(maxRetries + 1) { attempt ->
            try {
                return useCase()
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxRetries) {
                    TimberLogger.e(TAG, "UseCase执行失败，准备重试 (${attempt + 1}/$maxRetries)", e)
                    delay(delay)
                }
            }
        }
        
        throw lastException ?: Exception("UseCase执行失败")
    }
} 
