package com.novel.page.home.viewmodel

import com.novel.page.home.usecase.HomeCompositeUseCase
import com.novel.utils.network.api.front.HomeService
import kotlinx.collections.immutable.ImmutableList

internal data class HomeInitialLoadOutcome(
    val intents: List<HomeIntent>,
    val cachedHomeBooks: ImmutableList<HomeService.HomeBook>? = null,
)

internal class HomeInitialLoadCoordinator(
    private val loadInitialData: suspend () -> HomeCompositeUseCase.Result,
) {

    suspend fun coordinate(): HomeInitialLoadOutcome {
        return try {
            val result = loadInitialData()
            if (result.isSuccess) {
                val normalizedCategoryFilters = HomeCategoryFilterSupport.normalizeFilters(result.categoryFilters)
                HomeInitialLoadOutcome(
                    intents = listOf(
                        HomeIntent.CategoryFiltersLoadSuccess(normalizedCategoryFilters),
                        HomeIntent.CategoriesLoadSuccess(result.categories),
                        HomeIntent.BooksLoadSuccess(
                            carouselBooks = result.carouselBooks,
                            hotBooks = result.hotBooks,
                            newBooks = result.newBooks,
                            vipBooks = result.vipBooks,
                        ),
                        HomeIntent.RankBooksLoadSuccess(result.rankBooks),
                        HomeIntent.HomeRecommendBooksLoadSuccess(
                            books = result.homeRecommendBooks,
                            isRefresh = true,
                            hasMore = result.hasMoreRecommend,
                        ),
                    ),
                    cachedHomeBooks = result.homeRecommendBooks,
                )
            } else {
                HomeInitialLoadOutcome(
                    intents = listOf(
                        HomeIntent.BooksLoadFailure(result.errorMessage ?: "加载失败"),
                    ),
                )
            }
        } catch (error: Exception) {
            HomeInitialLoadOutcome(
                intents = listOf(
                    HomeIntent.BooksLoadFailure(error.message ?: "未知错误"),
                ),
            )
        }
    }
}
