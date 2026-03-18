package com.novel.core.concurrency

import java.util.concurrent.atomic.AtomicBoolean

class OnDemandInitializer(
    private val initializer: () -> Unit
) {
    private val initialized = AtomicBoolean(false)

    fun initializeIfNeeded(): Boolean {
        if (initialized.compareAndSet(false, true)) {
            initializer()
            return true
        }
        return false
    }

    fun isInitialized(): Boolean = initialized.get()
}
