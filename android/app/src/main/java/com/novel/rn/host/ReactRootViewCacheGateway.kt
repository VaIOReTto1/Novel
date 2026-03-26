package com.novel.rn.host

import com.novel.MainApplication

class DefaultReactRootViewCacheGateway(
    private val clearComponentCacheAction: (String) -> Unit = { componentName ->
        MainApplication.getInstance()?.clearReactRootViewCache(componentName)
    },
    private val clearAllComponentCacheAction: () -> Unit = {
        MainApplication.getInstance()?.clearAllReactRootViewCache()
    },
) : ReactRootViewCacheGateway {

    override fun clearComponentCache(componentName: String) {
        clearComponentCacheAction(componentName)
    }

    override fun clearAllComponentCache() {
        clearAllComponentCacheAction()
    }
}
