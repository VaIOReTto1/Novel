package com.novel.page.home.viewmodel

import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal data class HomePagingOutcome(
    val intents: List<HomeIntent>,
    val cachedHomeBooks: ImmutableList<HomeService.HomeBook>? = null,
)

internal class HomePagingCoordinator {

    suspend fun coordinateCategoryPaging(
        currentState: HomeUiState,
        pageSize: Int,
        resolveCategoryId: (String) -> Int,
        loadCategoryBooks: suspend (categoryId: Int, pageNum: Int, pageSize: Int) -> SearchService.PageResponse<SearchService.BookInfo>,
    ): HomePagingOutcome {
        if (!currentState.hasMoreRecommend) {
            return HomePagingOutcome(intents = emptyList())
        }

        return try {
            val nextPage = currentState.recommendPage + 1
            val categoryId = resolveCategoryId(currentState.selectedCategoryFilter)
            val result = loadCategoryBooks(categoryId, nextPage, pageSize)

            HomePagingOutcome(
                intents = listOf(
                    HomeIntent.CategoryRecommendBooksLoadSuccess(
                        books = result.list.toImmutableList(),
                        isLoadMore = true,
                        hasMore = result.list.size >= pageSize,
                        totalPages = result.pages.toInt(),
                    ),
                ),
            )
        } catch (error: Exception) {
            HomePagingOutcome(
                intents = listOf(
                    HomeIntent.CategoryRecommendBooksLoadFailure(
                        error.message ?: "加载更多失败",
                    ),
                ),
            )
        }
    }

    suspend fun coordinateHomePaging(
        currentState: HomeUiState,
        pageSize: Int,
        cachedHomeBooks: ImmutableList<HomeService.HomeBook>,
        loadHomeBooks: suspend () -> ImmutableList<HomeService.HomeBook>,
    ): HomePagingOutcome {
        if (!currentState.hasMoreHomeRecommend) {
            return HomePagingOutcome(intents = emptyList())
        }

        return try {
            val resolvedCache = if (cachedHomeBooks.isEmpty()) {
                loadHomeBooks()
            } else {
                cachedHomeBooks
            }

            val nextPage = currentState.homeRecommendPage + 1
            val startIndex = (nextPage - 1) * pageSize
            val endIndex = startIndex + pageSize
            val moreBooks = resolvedCache.drop(startIndex).take(pageSize).toImmutableList()
            val hasMore = endIndex < resolvedCache.size

            HomePagingOutcome(
                intents = listOf(
                    HomeIntent.HomeRecommendBooksLoadSuccess(
                        books = moreBooks,
                        isRefresh = false,
                        hasMore = hasMore,
                    ),
                ),
                cachedHomeBooks = resolvedCache,
            )
        } catch (error: Exception) {
            HomePagingOutcome(
                intents = listOf(
                    HomeIntent.HomeRecommendBooksLoadFailure(
                        error.message ?: "加载更多失败",
                    ),
                ),
            )
        }
    }
}
