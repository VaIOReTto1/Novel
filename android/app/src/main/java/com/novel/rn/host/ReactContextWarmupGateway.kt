package com.novel.rn.host

import com.novel.MainApplication

interface ReactContextWarmupGateway {
    fun hasReactContext(): Boolean
    fun warmUpIfNeeded()
}

class DefaultReactContextWarmupGateway(
    private val hasReactContextAction: () -> Boolean = {
        MainApplication.getInstance()?.reactNativeHost?.reactInstanceManager?.currentReactContext != null
    },
    private val createReactContextAction: () -> Unit = {
        MainApplication.getInstance()?.reactNativeHost?.reactInstanceManager?.createReactContextInBackground()
    },
) : ReactContextWarmupGateway {

    override fun hasReactContext(): Boolean = hasReactContextAction()

    override fun warmUpIfNeeded() {
        if (!hasReactContext()) {
            createReactContextAction()
        }
    }
}
