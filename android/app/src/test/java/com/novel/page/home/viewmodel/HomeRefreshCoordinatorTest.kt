package com.novel.page.home.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.page.home.usecase.HomeCompositeUseCase
import com.novel.utils.network.api.front.HomeService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class HomeRefreshCoordinatorTest {

    @Test
    fun coordinate_returnsSuccessIntentsEffectsAndCachedBooks() {
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
                homeRecommendBooks = homeRecommendBooks,
                hasMoreRecommend = true,
                isSuccess = true,
            )

            val outcome = HomeRefreshCoordinator {
                result
            }.coordinate()

            assertThat(outcome.cachedHomeBooks).isEqualTo(homeRecommendBooks)
            assertThat(outcome.intents).containsExactly(
                HomeIntent.CategoryFiltersLoadSuccess(
                    persistentListOf(CategoryInfo("0", "首页")),
                ),
                HomeIntent.CategoriesLoadSuccess(result.categories),
                HomeIntent.BooksLoadSuccess(
                    carouselBooks = result.carouselBooks,
                    hotBooks = result.hotBooks,
                    newBooks = result.newBooks,
                    vipBooks = result.vipBooks,
                ),
                HomeIntent.HomeRecommendBooksLoadSuccess(
                    books = homeRecommendBooks,
                    isRefresh = true,
                    hasMore = true,
                ),
                HomeIntent.RefreshComplete,
            ).inOrder()
            assertThat(outcome.effects).containsExactly(
                HomeEffect.ShowToast("刷新成功"),
            )
        }
    }

    @Test
    fun coordinate_returnsFailureIntentAndToastWhenUseCaseFails() {
        runBlocking {
            val outcome = HomeRefreshCoordinator {
                HomeCompositeUseCase.Result(
                    isSuccess = false,
                    errorMessage = "刷新失败",
                )
            }.coordinate()

            assertThat(outcome.cachedHomeBooks).isNull()
            assertThat(outcome.intents).containsExactly(
                HomeIntent.BooksLoadFailure("刷新失败"),
            )
            assertThat(outcome.effects).containsExactly(
                HomeEffect.ShowToast("刷新失败"),
            )
        }
    }

    @Test
    fun coordinate_returnsFailureIntentAndToastWhenLoaderThrows() {
        runBlocking {
            val outcome = HomeRefreshCoordinator {
                error("boom")
            }.coordinate()

            assertThat(outcome.cachedHomeBooks).isNull()
            assertThat(outcome.intents).containsExactly(
                HomeIntent.BooksLoadFailure("boom"),
            )
            assertThat(outcome.effects).containsExactly(
                HomeEffect.ShowToast("刷新失败"),
            )
        }
    }
}
