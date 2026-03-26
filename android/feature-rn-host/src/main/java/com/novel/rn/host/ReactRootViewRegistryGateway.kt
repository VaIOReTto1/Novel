package com.novel.rn.host

import android.os.Bundle
import com.facebook.react.ReactRootView

interface ReactRootViewRegistryGateway {
    fun getOrCreateReactRootView(
        componentName: String,
        initialProps: Bundle? = null,
    ): ReactRootView
}
