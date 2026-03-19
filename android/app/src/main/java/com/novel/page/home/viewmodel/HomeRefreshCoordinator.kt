package com.novel.page.home.viewmodel

import com.novel.page.home.usecase.HomeCompositeUseCase
import com.novel.utils.network.api.front.HomeService
import kotlinx.collections.immutable.ImmutableList

internal data class HomeRefreshOutcome(
    val intents: List<HomeIntent>,
    val effects: List<HomeEffect>,
    val cachedHomeBooks: ImmutableList<HomeService.HomeBook>? = null,
)

internal class HomeRefreshCoordinator(
    private val refreshData: suspend () -> HomeCompositeUseCase.Result,
) {

    suspend fun coordinate(): HomeRefreshOutcome {
        return try {
            val result = refreshData()
            if (result.isSuccess) {
                HomeRefreshOutcome(
                    intents = listOf(
                        HomeIntent.CategoryFiltersLoadSuccess(result.categoryFilters),
                        HomeIntent.CategoriesLoadSuccess(result.categories),
                        HomeIntent.BooksLoadSuccess(
                            carouselBooks = result.carouselBooks,
                            hotBooks = result.hotBooks,
                            newBooks = result.newBooks,
                            vipBooks = result.vipBooks,
                        ),
                        HomeIntent.HomeRecommendBooksLoadSuccess(
                            books = result.homeRecommendBooks,
                            isRefresh = true,
                            hasMore = result.hasMoreRecommend,
                        ),
                        HomeIntent.RefreshComplete,
                    ),
                    effects = listOf(HomeEffect.ShowToast("刷新成功")),
                    cachedHomeBooks = result.homeRecommendBooks,
                )
            } else {
                HomeRefreshOutcome(
                    intents = listOf(
                        HomeIntent.BooksLoadFailure(result.errorMessage ?: "刷新失败"),
                    ),
                    effects = listOf(HomeEffect.ShowToast("刷新失败")),
                )
            }
        } catch (error: Exception) {
            HomeRefreshOutcome(
                intents = listOf(
                    HomeIntent.BooksLoadFailure(error.message ?: "刷新失败"),
                ),
                effects = listOf(HomeEffect.ShowToast("刷新失败")),
            )
        }
    }
}
