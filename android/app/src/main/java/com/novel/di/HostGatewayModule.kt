package com.novel.di

import com.novel.rn.host.DefaultHostNavigationGateway
import com.novel.rn.host.DefaultHostBridgeViewModelGateway
import com.novel.rn.host.DefaultReactContextWarmupGateway
import com.novel.rn.host.DefaultReactRootViewCacheGateway
import com.novel.rn.host.DefaultReactRootViewRegistryGateway
import com.novel.rn.host.HostBridgeViewModelGateway
import com.novel.rn.host.HostNavigationGateway
import com.novel.rn.host.ReactContextWarmupGateway
import com.novel.rn.host.ReactRootViewCacheGateway
import com.novel.rn.host.ReactRootViewRegistryGateway
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HostGatewayModule {

    @Provides
    @Singleton
    fun provideHostNavigationGateway(): HostNavigationGateway = DefaultHostNavigationGateway()

    @Provides
    @Singleton
    internal fun provideHostBridgeViewModelGateway(): HostBridgeViewModelGateway =
        DefaultHostBridgeViewModelGateway()

    @Provides
    @Singleton
    fun provideReactRootViewCacheGateway(): ReactRootViewCacheGateway =
        DefaultReactRootViewCacheGateway()

    @Provides
    @Singleton
    fun provideReactContextWarmupGateway(): ReactContextWarmupGateway =
        DefaultReactContextWarmupGateway()

    @Provides
    @Singleton
    fun provideReactRootViewRegistryGateway(): ReactRootViewRegistryGateway =
        DefaultReactRootViewRegistryGateway()
}
