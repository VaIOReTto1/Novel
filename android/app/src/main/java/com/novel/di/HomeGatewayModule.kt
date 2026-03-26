package com.novel.di

import com.novel.page.home.gateway.AppHomeFeedGateway
import com.novel.page.home.gateway.AppHomeRnSyncGateway
import com.novel.page.home.gateway.HomeFeedGateway
import com.novel.page.home.gateway.HomeRnSyncGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeGatewayModule {

    @Binds
    @Singleton
    abstract fun bindHomeFeedGateway(
        impl: AppHomeFeedGateway,
    ): HomeFeedGateway

    @Binds
    @Singleton
    abstract fun bindHomeRnSyncGateway(
        impl: AppHomeRnSyncGateway,
    ): HomeRnSyncGateway
}
