package com.novel.core.concurrency

import androidx.compose.runtime.Stable
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Stable
interface DispatcherProvider {
    @Stable
    val io: CoroutineDispatcher

    @Stable
    val default: CoroutineDispatcher

    @Stable
    val main: CoroutineDispatcher

    @Stable
    val unconfined: CoroutineDispatcher
}

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
