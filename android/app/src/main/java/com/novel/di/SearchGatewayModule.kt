package com.novel.di

import com.novel.page.search.gateway.AppCategoryFilterGateway
import com.novel.page.search.gateway.AppSearchHistoryGateway
import com.novel.page.search.gateway.AppSearchQueryGateway
import com.novel.page.search.gateway.AppSearchRankingGateway
import com.novel.page.search.gateway.CategoryFilterGateway
import com.novel.page.search.gateway.SearchHistoryGateway
import com.novel.page.search.gateway.SearchQueryGateway
import com.novel.page.search.gateway.SearchRankingGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchGatewayModule {

    @Binds
    @Singleton
    abstract fun bindSearchHistoryGateway(
        impl: AppSearchHistoryGateway,
    ): SearchHistoryGateway

    @Binds
    @Singleton
    abstract fun bindSearchRankingGateway(
        impl: AppSearchRankingGateway,
    ): SearchRankingGateway

    @Binds
    @Singleton
    abstract fun bindSearchQueryGateway(
        impl: AppSearchQueryGateway,
    ): SearchQueryGateway

    @Binds
    @Singleton
    abstract fun bindCategoryFilterGateway(
        impl: AppCategoryFilterGateway,
    ): CategoryFilterGateway
}
