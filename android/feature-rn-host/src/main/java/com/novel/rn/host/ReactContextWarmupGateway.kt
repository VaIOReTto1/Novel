package com.novel.rn.host

import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.ReactContext

interface ReactContextWarmupGateway {
    fun hasReactContext(): Boolean
    fun currentReactContextOrNull(): ReactContext?
    fun reactInstanceManagerOrNull(): ReactInstanceManager?
    fun warmUpIfNeeded()
}
