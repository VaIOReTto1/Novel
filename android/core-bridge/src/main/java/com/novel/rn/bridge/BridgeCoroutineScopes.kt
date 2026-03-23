package com.novel.rn.bridge

import com.novel.core.concurrency.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BridgeCoroutineScopes @Inject constructor(
    dispatchers: DispatcherProvider
) {
    val io: CoroutineScope = CoroutineScope(SupervisorJob() + dispatchers.io)
    val main: CoroutineScope = CoroutineScope(SupervisorJob() + dispatchers.main)
}
