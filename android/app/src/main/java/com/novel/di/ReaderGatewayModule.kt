package com.novel.di

import com.novel.page.read.gateway.AppReaderHistoryGateway
import com.novel.page.read.gateway.AppReaderPaginationGateway
import com.novel.page.read.gateway.AppReaderSettingsGateway
import com.novel.page.read.gateway.ReaderHistoryGateway
import com.novel.page.read.gateway.ReaderPaginationGateway
import com.novel.page.read.gateway.ReaderSettingsGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReaderGatewayModule {

    @Binds
    @Singleton
    abstract fun bindReaderPaginationGateway(
        impl: AppReaderPaginationGateway,
    ): ReaderPaginationGateway

    @Binds
    @Singleton
    abstract fun bindReaderSettingsGateway(
        impl: AppReaderSettingsGateway,
    ): ReaderSettingsGateway

    @Binds
    @Singleton
    abstract fun bindReaderHistoryGateway(
        impl: AppReaderHistoryGateway,
    ): ReaderHistoryGateway
}
