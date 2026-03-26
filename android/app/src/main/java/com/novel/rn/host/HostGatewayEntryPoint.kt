package com.novel.rn.host

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface HostGatewayEntryPoint {
    fun hostBridgeViewModelGateway(): HostBridgeViewModelGateway
    fun hostNavigationGateway(): HostNavigationGateway
    fun reactRootViewCacheGateway(): ReactRootViewCacheGateway
    fun reactContextWarmupGateway(): ReactContextWarmupGateway
    fun reactRootViewRegistryGateway(): ReactRootViewRegistryGateway
}

internal fun Context.hostGatewayEntryPoint(): HostGatewayEntryPoint {
    return EntryPointAccessors.fromApplication(
        applicationContext,
        HostGatewayEntryPoint::class.java,
    )
}
