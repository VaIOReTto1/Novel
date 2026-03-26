package com.novel.page.home.gateway

import com.novel.page.home.usecase.GetCategoryRecommendBooksUseCase
import com.novel.page.home.usecase.GetHomeCategoriesUseCase
import com.novel.page.home.usecase.GetHomeRecommendBooksUseCase
import com.novel.page.home.usecase.GetRankingBooksUseCase
import com.novel.page.home.usecase.HomeCompositeUseCase
import com.novel.page.home.usecase.SendReactNativeDataUseCase
import com.novel.page.home.viewmodel.CategoryInfo
import com.novel.page.home.viewmodel.CategoryRecommendItem
import com.novel.page.home.viewmodel.HomeCategoryFilterSupport
import com.novel.page.home.viewmodel.HomeRankBook
import com.novel.page.home.viewmodel.HomeRecommendItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import com.novel.utils.network.cache.CacheStrategy

@Singleton
class AppHomeFeedGateway @Inject constructor(
    private val homeCompositeUseCase: HomeCompositeUseCase,
    private val getHomeCategoriesUseCase: GetHomeCategoriesUseCase,
    private val getHomeRecommendBooksUseCase: GetHomeRecommendBooksUseCase,
    private val getRankingBooksUseCase: GetRankingBooksUseCase,
    private val getCategoryRecommendBooksUseCase: GetCategoryRecommendBooksUseCase,
) : HomeFeedGateway {

    override suspend fun loadInitialData(): HomeFeedSnapshot {
        val result = homeCompositeUseCase(HomeCompositeUseCase.Params(loadInitialData = true))
        return result.toSnapshot()
    }

    override suspend fun refreshData(): HomeFeedSnapshot {
        val result = homeCompositeUseCase(HomeCompositeUseCase.Params(refreshData = true))
        return result.toSnapshot()
    }

    override suspend fun loadRankBooks(rankType: String): ImmutableList<HomeRankBook> {
        val books = getRankingBooksUseCase(GetRankingBooksUseCase.Params(rankType))
            .ifEmpty {
                getRankingBooksUseCase(
                    GetRankingBooksUseCase.Params(
                        rankType = rankType,
                        strategy = CacheStrategy.NETWORK_ONLY,
                    ),
                )
            }
        return books
            .map { book ->
                HomeRankBook(
                    id = book.id,
                    bookName = book.bookName,
                    picUrl = book.picUrl,
                    categoryName = book.categoryName,
                )
            }
            .toImmutableList()
    }

    override suspend fun loadCategoryFilters(): ImmutableList<CategoryInfo> {
        return getHomeCategoriesUseCase(GetHomeCategoriesUseCase.Params())
            .first()
            .toImmutableList()
    }

    override suspend fun loadCategoryRecommendBooks(
        categoryName: String,
        categoryFilters: ImmutableList<CategoryInfo>,
        pageNum: Int,
        pageSize: Int,
    ): HomeRecommendPage {
        val normalized = HomeCategoryFilterSupport.normalizeSelectedFilter(categoryName)
        val categoryId = categoryFilters.find {
            HomeCategoryFilterSupport.normalizeSelectedFilter(it.name) == normalized
        }?.id?.toIntOrNull() ?: 0

        val result = getCategoryRecommendBooksUseCase(
            GetCategoryRecommendBooksUseCase.Params(
                categoryId = categoryId,
                pageNum = pageNum,
                pageSize = pageSize,
            ),
        )

        val items = result.list.map { book ->
            CategoryRecommendItem(
                id = book.id,
                title = book.bookName,
                author = book.authorName,
                coverUrl = book.picUrl,
                categoryName = book.categoryName,
                bookStatus = book.bookStatus,
                wordCount = book.wordCount.toLong(),
            )
        }.toImmutableList()

        return HomeRecommendPage(
            items = items,
            hasMore = result.list.size >= pageSize,
            totalPages = result.pages.toInt(),
        )
    }

    override suspend fun loadAllHomeRecommendBooks(refresh: Boolean): ImmutableList<HomeRecommendItem> {
        return getHomeRecommendBooksUseCase(
            GetHomeRecommendBooksUseCase.Params(
                strategy = if (refresh) CacheStrategy.NETWORK_ONLY else CacheStrategy.CACHE_FIRST,
            ),
        ).map { book ->
            HomeRecommendItem(
                id = book.bookId,
                title = book.bookName,
                author = book.authorName,
                coverUrl = book.picUrl,
                description = book.bookDesc,
            )
        }.toImmutableList()
    }

    private suspend fun HomeCompositeUseCase.Result.toSnapshot(): HomeFeedSnapshot {
        return HomeFeedSnapshot(
            categories = categories,
            categoryFilters = categoryFilters,
            carouselBooks = carouselBooks,
            hotBooks = hotBooks,
            newBooks = newBooks,
            vipBooks = vipBooks,
            rankBooks = rankBooks.map { book ->
                HomeRankBook(
                    id = book.id,
                    bookName = book.bookName,
                    picUrl = book.picUrl,
                    categoryName = book.categoryName,
                )
            }.toImmutableList(),
            allHomeRecommendBooks = homeRecommendBooks.map { book ->
                HomeRecommendItem(
                    id = book.bookId,
                    title = book.bookName,
                    author = book.authorName,
                    coverUrl = book.picUrl,
                    description = book.bookDesc,
                )
            }.toImmutableList(),
            homeRecommendUsedNetworkFallback = homeRecommendUsedNetworkFallback,
        )
    }
}

@Singleton
class AppHomeRnSyncGateway @Inject constructor(
    private val sendReactNativeDataUseCase: SendReactNativeDataUseCase,
) : HomeRnSyncGateway {
    override suspend fun sync() {
        sendReactNativeDataUseCase(Unit)
    }
}
