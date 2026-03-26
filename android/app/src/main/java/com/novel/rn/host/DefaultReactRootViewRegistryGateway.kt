package com.novel.rn.host

import android.os.Bundle
import com.facebook.react.ReactRootView
import com.novel.MainApplication

class DefaultReactRootViewRegistryGateway(
    private val getOrCreateReactRootViewAction: (String, Bundle?) -> ReactRootView = { componentName, initialProps ->
        requireNotNull(MainApplication.getInstance()) {
            "MainApplication is not ready"
        }.getOrCreateReactRootView(componentName, initialProps)
    },
) : ReactRootViewRegistryGateway {

    override fun getOrCreateReactRootView(
        componentName: String,
        initialProps: Bundle?,
    ): ReactRootView = getOrCreateReactRootViewAction(componentName, initialProps)
}
