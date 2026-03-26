package com.novel.page.home.gateway

import androidx.compose.runtime.Stable
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.page.home.viewmodel.CategoryInfo
import com.novel.page.home.viewmodel.HomeRankBook
import com.novel.page.home.viewmodel.HomeRecommendItem
import com.novel.page.home.viewmodel.RecommendItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface HomeFeedGateway {
    suspend fun loadInitialData(): HomeFeedSnapshot

    suspend fun refreshData(): HomeFeedSnapshot

    suspend fun loadRankBooks(rankType: String): ImmutableList<HomeRankBook>

    suspend fun loadCategoryFilters(): ImmutableList<CategoryInfo>

    suspend fun loadCategoryRecommendBooks(
        categoryName: String,
        categoryFilters: ImmutableList<CategoryInfo>,
        pageNum: Int,
        pageSize: Int,
    ): HomeRecommendPage

    suspend fun loadAllHomeRecommendBooks(refresh: Boolean): ImmutableList<HomeRecommendItem>
}

interface HomeRnSyncGateway {
    suspend fun sync()
}

@Stable
data class HomeFeedSnapshot(
    val categories: ImmutableList<HomeCategoryEntity> = persistentListOf(),
    val categoryFilters: ImmutableList<CategoryInfo> = persistentListOf(),
    val carouselBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val hotBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val newBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val vipBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val rankBooks: ImmutableList<HomeRankBook> = persistentListOf(),
    val allHomeRecommendBooks: ImmutableList<HomeRecommendItem> = persistentListOf(),
    val homeRecommendUsedNetworkFallback: Boolean = false,
)

@Stable
data class HomeRecommendPage(
    val items: ImmutableList<RecommendItem> = persistentListOf(),
    val hasMore: Boolean = false,
    val totalPages: Int = 1,
)
