package com.novel.rn.host

interface ReactRootViewCacheGateway {
    fun clearComponentCache(componentName: String)
    fun clearAllComponentCache()
}
