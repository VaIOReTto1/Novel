package com.novel.core.domain

import com.novel.core.logging.CoreLogger
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

abstract class BaseUseCase<in Params, out Result> {

    companion object {
        private const val TAG = "BaseUseCase"
        private val DEFAULT_TIMEOUT = 30.seconds
    }

    protected abstract suspend fun execute(params: Params): Result

    protected open fun getDispatcher(): CoroutineDispatcher = Dispatchers.IO

    protected open fun getTimeout(): Duration = DEFAULT_TIMEOUT

    suspend operator fun invoke(params: Params): Result {
        val startTime = System.currentTimeMillis()
        CoreLogger.d(TAG, "开始执行UseCase: ${this::class.simpleName}")

        return try {
            withTimeout(getTimeout()) {
                withContext(getDispatcher()) {
                    execute(params)
                }
            }.also {
                val duration = System.currentTimeMillis() - startTime
                CoreLogger.d(TAG, "UseCase执行成功: ${this::class.simpleName}, 耗时: ${duration}ms")
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            CoreLogger.e(TAG, "UseCase执行失败: ${this::class.simpleName}, 耗时: ${duration}ms", e)
            throw e
        }
    }
}

abstract class BaseUseCaseNoParams<out Result> : BaseUseCase<Unit, Result>() {
    suspend operator fun invoke(): Result = invoke(Unit)
}

abstract class FlowUseCase<in Params, out Result> {

    companion object {
        private const val TAG = "FlowUseCase"
        private const val DEFAULT_RETRY_ATTEMPTS = 3L
    }

    protected abstract suspend fun execute(params: Params): Flow<Result>

    protected open fun getDispatcher(): CoroutineDispatcher = Dispatchers.IO

    protected open fun getRetryAttempts(): Long = DEFAULT_RETRY_ATTEMPTS

    operator fun invoke(params: Params): Flow<Result> {
        CoreLogger.d(TAG, "开始执行FlowUseCase: ${this::class.simpleName}")

        return flow {
            execute(params).collect { result ->
                emit(result)
            }
        }
            .retry(getRetryAttempts()) { cause ->
                CoreLogger.e(TAG, "FlowUseCase执行失败，准备重试: ${this::class.simpleName}", cause)
                true
            }
            .flowOn(getDispatcher())
    }
}

abstract class FlowUseCaseNoParams<out Result> : FlowUseCase<Unit, Result>() {
    operator fun invoke(): Flow<Result> = invoke(Unit)
}

class UseCaseExecutor {

    companion object {
        private const val TAG = "UseCaseExecutor"
    }

    suspend fun <T> executeParallel(
        useCases: List<suspend () -> T>
    ): List<T> {
        CoreLogger.d(TAG, "并行执行${useCases.size}个UseCase")

        return kotlinx.coroutines.coroutineScope {
            useCases.map { useCase ->
                async { useCase() }
            }.map { it.await() }
        }
    }

    suspend fun <T> executeSequential(
        useCases: List<suspend () -> T>
    ): List<T> {
        CoreLogger.d(TAG, "串行执行${useCases.size}个UseCase")

        return useCases.map { useCase ->
            useCase()
        }
    }

    suspend fun <T> executeChain(
        initialParams: T,
        vararg useCases: suspend (T) -> T
    ): T {
        CoreLogger.d(TAG, "执行UseCase链，包含${useCases.size}个UseCase")

        return useCases.fold(initialParams) { acc, useCase ->
            useCase(acc)
        }
    }
}

class ComposeUseCase {

    companion object {
        private const val TAG = "ComposeUseCase"
    }

    suspend fun <T1, T2, R> combine(
        useCase1: suspend () -> T1,
        useCase2: suspend () -> T2,
        combiner: (T1, T2) -> R
    ): R {
        CoreLogger.d(TAG, "合并两个UseCase的结果")

        return kotlinx.coroutines.coroutineScope {
            val result1 = async { useCase1() }
            val result2 = async { useCase2() }
            combiner(result1.await(), result2.await())
        }
    }

    suspend fun <T> conditional(
        condition: Boolean,
        trueUseCase: suspend () -> T,
        falseUseCase: suspend () -> T
    ): T {
        CoreLogger.d(TAG, "条件执行UseCase: condition=$condition")

        return if (condition) {
            trueUseCase()
        } else {
            falseUseCase()
        }
    }

    suspend fun <T> withRetry(
        maxRetries: Int = 3,
        delay: Duration = 1.seconds,
        useCase: suspend () -> T
    ): T {
        CoreLogger.d(TAG, "带重试机制执行UseCase，最大重试次数: $maxRetries")

        var lastException: Exception? = null

        repeat(maxRetries + 1) { attempt ->
            try {
                return useCase()
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxRetries) {
                    CoreLogger.e(TAG, "UseCase执行失败，准备重试 (${attempt + 1}/$maxRetries)", e)
                    delay(delay)
                }
            }
        }

        throw lastException ?: Exception("UseCase执行失败")
    }
}
