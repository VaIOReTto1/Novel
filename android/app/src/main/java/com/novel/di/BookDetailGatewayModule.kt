package com.novel.di

import com.novel.page.book.gateway.AppBookDetailGateway
import com.novel.page.book.gateway.BookDetailGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookDetailGatewayModule {

    @Binds
    @Singleton
    abstract fun bindBookDetailGateway(
        impl: AppBookDetailGateway,
    ): BookDetailGateway
}
