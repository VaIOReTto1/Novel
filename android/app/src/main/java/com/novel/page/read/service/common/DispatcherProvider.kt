package com.novel.page.read.service.common

import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

typealias DispatcherProvider = com.novel.core.concurrency.DispatcherProvider

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
