package com.novel.page.home.viewmodel

import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

object HomeStateProjector {

    fun canPerformSearch(state: HomeState): Boolean {
        return state.searchQuery.isNotBlank() && !state.isLoading
    }

    fun getSearchHint(state: HomeState): String {
        return when {
            state.isLoading -> "加载中..."
            state.hasError -> "搜索失败，请重试"
            else -> "搜索您喜欢的小说"
        }
    }

    fun canLoadMoreRecommend(state: HomeState): Boolean {
        return when {
            state.isRecommendMode -> state.hasMoreHomeRecommend && !state.homeRecommendLoading
            else -> state.hasMoreRecommend && !state.recommendLoading
        }
    }

    fun getLoadMoreText(state: HomeState): String {
        return when {
            state.isRecommendMode && state.homeRecommendLoading -> "加载中..."
            !state.isRecommendMode && state.recommendLoading -> "加载中..."
            !canLoadMoreRecommend(state) -> "已加载全部"
            state.hasError -> "加载失败，点击重试"
            else -> "点击加载更多"
        }
    }

    fun getHomeStatusSummary(state: HomeState): String {
        return when {
            state.isLoading -> "加载中"
            state.hasError -> "加载失败"
            state.isRefreshing -> "刷新中"
            state.isEmpty -> "暂无数据"
            else -> "加载完成"
        }
    }

    fun shouldShowEmptyState(state: HomeState): Boolean {
        return !state.isLoading &&
            !state.hasError &&
            state.categories.isEmpty() &&
            state.carouselBooks.isEmpty() &&
            state.hotBooks.isEmpty() &&
            state.newBooks.isEmpty() &&
            state.vipBooks.isEmpty()
    }

    fun shouldShowLoadMoreButton(state: HomeState): Boolean {
        return canLoadMoreRecommend(state) &&
            (state.recommendBooks.isNotEmpty() || state.homeRecommendBooks.isNotEmpty())
    }

    fun getRecommendModeText(state: HomeState): String {
        return if (state.isRecommendMode) {
            "首页推荐"
        } else {
            "分类推荐 - ${state.selectedCategoryFilter}"
        }
    }

    fun toHomeUiState(state: HomeState): HomeUiState {
        return HomeUiState(
            version = state.version,
            isLoading = state.isLoading,
            error = state.error,
            isRefreshing = state.isRefreshing,
            categories = state.categories,
            categoryLoading = state.categoryLoading,
            carouselBooks = state.carouselBooks,
            hotBooks = state.hotBooks,
            newBooks = state.newBooks,
            vipBooks = state.vipBooks,
            booksLoading = state.booksLoading,
            searchQuery = state.searchQuery,
            selectedCategoryFilter = state.selectedCategoryFilter,
            categoryFilters = state.categoryFilters,
            categoryFiltersLoading = state.categoryFiltersLoading,
            selectedRankType = state.selectedRankType,
            rankBooks = state.rankBooks,
            rankLoading = state.rankLoading,
            recommendBooks = state.recommendBooks,
            homeRecommendBooks = state.homeRecommendBooks,
            recommendLoading = state.recommendLoading,
            hasMoreRecommend = state.hasMoreRecommend,
            recommendPage = state.recommendPage,
            totalRecommendPages = state.totalRecommendPages,
            homeRecommendLoading = state.homeRecommendLoading,
            hasMoreHomeRecommend = state.hasMoreHomeRecommend,
            homeRecommendPage = state.homeRecommendPage,
            isRecommendMode = state.isRecommendMode
        )
    }

    fun toScreenState(state: HomeState): HomeScreenState {
        return HomeScreenState(
            isLoading = state.isLoading,
            error = state.error,
            isRefreshing = state.isRefreshing,
            searchQuery = state.searchQuery,
            categories = state.categories,
            selectedCategoryFilter = state.selectedCategoryFilter,
            categoryFilters = state.categoryFilters,
            carouselBooks = state.carouselBooks,
            hotBooks = state.hotBooks,
            newBooks = state.newBooks,
            vipBooks = state.vipBooks,
            rankBooks = state.rankBooks,
            selectedRankType = state.selectedRankType,
            currentRecommendBooks = if (state.isRecommendMode) {
                state.homeRecommendBooks.map { HomeRecommendItem(it) }.toImmutableList()
            } else {
                state.recommendBooks.map { CategoryRecommendItem(it) }.toImmutableList()
            },
            canPerformSearch = canPerformSearch(state),
            searchHint = getSearchHint(state),
            canLoadMoreRecommend = canLoadMoreRecommend(state),
            loadMoreText = getLoadMoreText(state),
            homeStatusSummary = getHomeStatusSummary(state),
            shouldShowEmptyState = shouldShowEmptyState(state),
            shouldShowLoadMoreButton = shouldShowLoadMoreButton(state),
            recommendModeText = getRecommendModeText(state),
            isRecommendMode = state.isRecommendMode
        )
    }
}
