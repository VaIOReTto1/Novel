package com.novel.page.home.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.page.home.usecase.HomeCompositeUseCase
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class HomeInitialLoadCoordinatorTest {

    @Test
    fun coordinate_returnsSuccessIntentsAndCachedBooks() {
        runBlocking {
            val homeRecommendBooks = persistentListOf(
                HomeService.HomeBook(
                    type = 3,
                    bookId = 101,
                    picUrl = "cover",
                    bookName = "Home Recommend",
                    authorName = "Author",
                    bookDesc = "desc",
                ),
            )
            val result = HomeCompositeUseCase.Result(
                categoryFilters = persistentListOf(CategoryInfo("0", "推荐")),
                categories = persistentListOf(HomeCategoryEntity(1, "玄幻", null, 0)),
                carouselBooks = persistentListOf(
                    HomeBookEntity(
                        id = 1,
                        title = "Carousel",
                        author = "Author",
                        coverUrl = "cover",
                        description = "desc",
                        category = "",
                        isCompleted = false,
                        isVip = false,
                        updateTime = 1L,
                        type = "carousel",
                    ),
                ),
                hotBooks = persistentListOf(),
                newBooks = persistentListOf(),
                vipBooks = persistentListOf(),
                rankBooks = persistentListOf(
                    BookService.BookRank(
                        id = 1,
                        categoryId = 1,
                        categoryName = "玄幻",
                        picUrl = "cover",
                        bookName = "Rank",
                        authorName = "Author",
                        bookDesc = "desc",
                        wordCount = 1000,
                        lastChapterName = "chapter",
                        lastChapterUpdateTime = "today",
                    ),
                ),
                homeRecommendBooks = homeRecommendBooks,
                hasMoreRecommend = true,
                isSuccess = true,
            )

            val outcome = HomeInitialLoadCoordinator {
                result
            }.coordinate()

            assertThat(outcome.cachedHomeBooks).isEqualTo(homeRecommendBooks)
            assertThat(outcome.intents).hasSize(5)
            assertThat(outcome.intents[0]).isEqualTo(
                HomeIntent.CategoryFiltersLoadSuccess(result.categoryFilters),
            )
            assertThat(outcome.intents[1]).isEqualTo(
                HomeIntent.CategoriesLoadSuccess(result.categories),
            )
            assertThat(outcome.intents[2]).isEqualTo(
                HomeIntent.BooksLoadSuccess(
                    carouselBooks = result.carouselBooks,
                    hotBooks = result.hotBooks,
                    newBooks = result.newBooks,
                    vipBooks = result.vipBooks,
                ),
            )
            assertThat(outcome.intents[3]).isEqualTo(
                HomeIntent.RankBooksLoadSuccess(result.rankBooks),
            )
            assertThat(outcome.intents[4]).isEqualTo(
                HomeIntent.HomeRecommendBooksLoadSuccess(
                    books = homeRecommendBooks,
                    isRefresh = true,
                    hasMore = true,
                ),
            )
        }
    }

    @Test
    fun coordinate_returnsFailureIntentWhenUseCaseFails() {
        runBlocking {
            val outcome = HomeInitialLoadCoordinator {
                HomeCompositeUseCase.Result(
                    isSuccess = false,
                    errorMessage = "加载失败",
                )
            }.coordinate()

            assertThat(outcome.cachedHomeBooks).isNull()
            assertThat(outcome.intents).containsExactly(
                HomeIntent.BooksLoadFailure("加载失败"),
            )
        }
    }

    @Test
    fun coordinate_returnsFailureIntentWhenLoaderThrows() {
        runBlocking {
            val outcome = HomeInitialLoadCoordinator {
                error("boom")
            }.coordinate()

            assertThat(outcome.cachedHomeBooks).isNull()
            assertThat(outcome.intents).containsExactly(
                HomeIntent.BooksLoadFailure("boom"),
            )
        }
    }
}
