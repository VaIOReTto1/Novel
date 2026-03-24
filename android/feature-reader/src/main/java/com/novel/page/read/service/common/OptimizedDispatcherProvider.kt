package com.novel.page.read.service.common

import androidx.compose.runtime.Stable
import com.novel.core.concurrency.DispatcherProvider
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher

@Singleton
@Stable
class OptimizedDispatcherProvider @Inject constructor() : DispatcherProvider {

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
