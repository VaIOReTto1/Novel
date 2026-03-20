package com.novel.page.read.service.common

import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 协程调度器提供者接口
 * 
 * 提供各种协程调度器的抽象，便于：
 * - 单元测试时注入测试调度器
 * - 性能优化时切换调度器策略
 * - 统一管理协程调度
 */
@Stable
interface DispatcherProvider {
    /** IO操作调度器 */
    @Stable
    val io: CoroutineDispatcher
    
    /** 默认调度器（CPU密集型任务） */
    @Stable
    val default: CoroutineDispatcher
    
    /** 主线程调度器 */
    @Stable
    val main: CoroutineDispatcher
    
    /** 不受限制的调度器 */
    @Stable
    val unconfined: CoroutineDispatcher
}

/**
 * 默认调度器提供者实现
 * 
 * 使用标准的Kotlin协程调度器
 */
@Singleton
@Stable
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    @Stable
    override val io: CoroutineDispatcher = Dispatchers.IO
    @Stable
    override val default: CoroutineDispatcher = Dispatchers.Default
    @Stable
    override val main: CoroutineDispatcher = Dispatchers.Main
    @Stable
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}

/**
 * 性能优化调度器提供者
 * 
 * 限制并发数，避免ANR和资源争抢
 */
@Singleton
@Stable
class OptimizedDispatcherProvider @Inject constructor() : DispatcherProvider {
    
    // 限制IO线程池大小，避免过度并发
    @Stable
    override val io: CoroutineDispatcher = Executors.newFixedThreadPool(
        ReaderServiceConfig.MAX_IO_CONCURRENCY
    ).asCoroutineDispatcher()
    
    @Stable
    override val default: CoroutineDispatcher = Dispatchers.Default
    @Stable
    override val main: CoroutineDispatcher = Dispatchers.Main
    @Stable
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
