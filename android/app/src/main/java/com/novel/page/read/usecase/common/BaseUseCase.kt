package com.novel.page.read.usecase.common

import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.SafeService
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.service.common.ReaderServiceConfig
import kotlinx.coroutines.withContext

/**
 * UseCase 抽象基类
 * 
 * 提供统一的协程调度、异常处理和性能监控：
 * 1. 继承 SafeService 的异常安全处理机制
 * 2. 统一的线程调度策略
 * 3. 性能监控和日志记录
 * 4. 可测试的设计
 * 
 * 使用方式：
 * ```kotlin
 * class SomeUseCase @Inject constructor(
 *     private val someService: SomeService,
 *     dispatchers: DispatcherProvider,
 *     logger: ServiceLogger
 * ) : BaseUseCase(dispatchers, logger) {
 *     
 *     suspend fun execute() = executeIo("执行某操作") {
 *         someService.doSomething()
 *     }
 * }
 * ```
 */
abstract class BaseUseCase(
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : SafeService(dispatchers, logger) {

    /**
     * 执行 IO 操作（带性能监控）
     * 
     * 自动切换到 IO 线程，捕获异常，记录性能日志
     * 
     * @param operationName 操作名称，用于日志记录
     * @param block 要执行的操作
     * @return 操作结果，失败时返回 null
     */
    protected suspend fun <T> executeIo(
        operationName: String,
        block: suspend () -> T
    ): T? = safeIoWithTimeout({
        withPerformanceMonitoring(operationName) {
            logger.logDebug("开始执行 $operationName", getServiceTag())
            val result = block()
            logger.logDebug("$operationName 执行完成", getServiceTag())
            result
        }
    }, ReaderServiceConfig.CACHE_OPERATION_TIMEOUT_MS)

    /**
     * 执行 IO 操作（带默认值）
     * 
     * @param operationName 操作名称
     * @param defaultValue 失败时的默认返回值
     * @param block 要执行的操作
     * @return 操作结果或默认值
     */
    protected suspend fun <T> executeIoWithDefault(
        operationName: String,
        defaultValue: T,
        block: suspend () -> T
    ): T = safeIoWithDefault(defaultValue) {
        withPerformanceMonitoring(operationName) {
            logger.logDebug("开始执行 $operationName", getServiceTag())
            val result = block()
            logger.logDebug("$operationName 执行完成", getServiceTag())
            result
        }
    }

    /**
     * 执行计算密集型操作
     * 
     * 自动切换到 Default 线程，适用于 CPU 密集型任务
     * 
     * @param operationName 操作名称
     * @param block 要执行的操作
     * @return 操作结果，失败时返回 null
     */
    protected suspend fun <T> executeComputation(
        operationName: String,
        block: suspend () -> T
    ): T? = safeComputation {
        withPerformanceMonitoring(operationName) {
            logger.logDebug("开始执行计算任务 $operationName", getServiceTag())
            val result = block()
            logger.logDebug("计算任务 $operationName 执行完成", getServiceTag())
            result
        }
    }

    /**
     * 执行带重试的 IO 操作
     * 
     * @param operationName 操作名称
     * @param maxRetries 最大重试次数
     * @param retryDelay 重试间隔（毫秒）
     * @param block 要执行的操作
     * @return 操作结果，失败时返回 null
     */
    protected suspend fun <T> executeIoWithRetry(
        operationName: String,
        maxRetries: Int = ReaderServiceConfig.MAX_RETRY_COUNT,
        retryDelay: Long = ReaderServiceConfig.RETRY_DELAY_MS,
        block: suspend () -> T
    ): T? = safeIoWithRetry(maxRetries, retryDelay) {
        withPerformanceMonitoring(operationName) {
            logger.logDebug("开始执行 $operationName（支持重试）", getServiceTag())
            val result = block()
            logger.logDebug("$operationName 执行完成", getServiceTag())
            result
        }
    }

    /**
     * 执行 Result 包装的操作
     * 
     * 将异常转换为 Result.failure，成功结果转换为 Result.success
     * 
     * @param operationName 操作名称
     * @param block 要执行的操作
     * @return Result 包装的结果
     */
    protected suspend fun <T> executeWithResult(
        operationName: String,
        block: suspend () -> T
    ): Result<T> = try {
        withPerformanceMonitoring(operationName) {
            logger.logDebug("开始执行 $operationName", getServiceTag())
            val result = block()
            logger.logDebug("$operationName 执行成功", getServiceTag())
            Result.success(result)
        }
    } catch (e: Exception) {
        logger.logError("$operationName 执行失败", e, getServiceTag())
        Result.failure(e)
    }

    /**
     * 记录操作开始日志
     */
    protected fun logOperationStart(operationName: String, details: String = "") {
        logger.logInfo("开始$operationName${if (details.isNotEmpty()) ": $details" else ""}", getServiceTag())
    }

    /**
     * 记录操作完成日志
     */
    protected fun logOperationComplete(operationName: String, details: String = "") {
        logger.logInfo("完成$operationName${if (details.isNotEmpty()) ": $details" else ""}", getServiceTag())
    }

    /**
     * 记录操作失败日志
     */
    protected fun logOperationError(operationName: String, error: Throwable, details: String = "") {
        logger.logError("$operationName 失败${if (details.isNotEmpty()) ": $details" else ""}", error, getServiceTag())
    }
} 
