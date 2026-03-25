package com.novel.rn.host

import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.ReactContext
import com.novel.MainApplication

class DefaultReactContextWarmupGateway(
    private val currentReactContextAction: () -> ReactContext? = {
        MainApplication.getInstance()?.reactNativeHost?.reactInstanceManager?.currentReactContext
    },
    private val reactInstanceManagerAction: () -> ReactInstanceManager? = {
        MainApplication.getInstance()?.reactNativeHost?.reactInstanceManager
    },
    private val hasReactContextAction: () -> Boolean = {
        currentReactContextAction() != null
    },
    private val createReactContextAction: () -> Unit = {
        MainApplication.getInstance()?.reactNativeHost?.reactInstanceManager?.createReactContextInBackground()
    },
) : ReactContextWarmupGateway {

    override fun hasReactContext(): Boolean = hasReactContextAction()

    override fun currentReactContextOrNull(): ReactContext? = currentReactContextAction()

    override fun reactInstanceManagerOrNull(): ReactInstanceManager? = reactInstanceManagerAction()

    override fun warmUpIfNeeded() {
        if (!hasReactContext()) {
            createReactContextAction()
        }
    }
}
