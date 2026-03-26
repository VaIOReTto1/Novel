package com.novel.di

import com.novel.page.login.gateway.AppLoginGateway
import com.novel.page.login.gateway.AppPhoneInfoProvider
import com.novel.page.login.gateway.LoginGateway
import com.novel.page.login.gateway.PhoneInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginGatewayModule {

    @Binds
    @Singleton
    abstract fun bindLoginGateway(
        impl: AppLoginGateway,
    ): LoginGateway

    @Binds
    @Singleton
    abstract fun bindPhoneInfoProvider(
        impl: AppPhoneInfoProvider,
    ): PhoneInfoProvider
}
