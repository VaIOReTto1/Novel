package com.novel.page.home.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class HomePagingCoordinatorTest {

    @Test
    fun coordinateCategoryPaging_returnsEmptyWhenNoMoreData() {
        runBlocking {
            val outcome = HomePagingCoordinator().coordinateCategoryPaging(
                currentState = HomeUiState(
                    selectedCategoryFilter = "玄幻奇幻",
                    hasMoreRecommend = false,
                ),
                pageSize = 8,
                resolveCategoryId = { 1 },
                loadCategoryBooks = { _, _, _ -> error("should not load category books") },
            )

            assertThat(outcome.intents).isEmpty()
            assertThat(outcome.cachedHomeBooks).isNull()
        }
    }

    @Test
    fun coordinateCategoryPaging_returnsLoadMoreIntent() {
        runBlocking {
            val currentState = HomeUiState(
                selectedCategoryFilter = "玄幻奇幻",
                hasMoreRecommend = true,
                recommendPage = 2,
            )

            val outcome = HomePagingCoordinator().coordinateCategoryPaging(
                currentState = currentState,
                pageSize = 8,
                resolveCategoryId = { categoryName ->
                    assertThat(categoryName).isEqualTo("玄幻奇幻")
                    12
                },
                loadCategoryBooks = { categoryId, pageNum, pageSize ->
                    assertThat(categoryId).isEqualTo(12)
                    assertThat(pageNum).isEqualTo(3)
                    assertThat(pageSize).isEqualTo(8)
                    SearchService.PageResponse(
                        list = persistentListOf(
                            SearchService.BookInfo(
                                id = 101,
                                categoryId = 12,
                                categoryName = "玄幻奇幻",
                                picUrl = "cover",
                                bookName = "Paged Book",
                                authorId = 1,
                                authorName = "Author",
                                bookDesc = "desc",
                                bookStatus = 1,
                                visitCount = 99,
                                wordCount = 1000,
                                commentCount = 3,
                                firstChapterId = 1,
                                lastChapterId = 2,
                                lastChapterName = "chapter",
                                updateTime = "today",
                            ),
                        ),
                        total = 20L,
                        pages = 4L,
                        pageNum = 3L,
                        pageSize = 8L,
                    )
                },
            )

            assertThat(outcome.intents).containsExactly(
                HomeIntent.CategoryRecommendBooksLoadSuccess(
                    books = persistentListOf(
                        SearchService.BookInfo(
                            id = 101,
                            categoryId = 12,
                            categoryName = "玄幻奇幻",
                            picUrl = "cover",
                            bookName = "Paged Book",
                            authorId = 1,
                            authorName = "Author",
                            bookDesc = "desc",
                            bookStatus = 1,
                            visitCount = 99,
                            wordCount = 1000,
                            commentCount = 3,
                            firstChapterId = 1,
                            lastChapterId = 2,
                            lastChapterName = "chapter",
                            updateTime = "today",
                        ),
                    ),
                    isLoadMore = true,
                    hasMore = false,
                    totalPages = 4,
                ),
            )
        }
    }

    @Test
    fun coordinateHomePaging_loadsCacheWhenMissingAndReturnsNextSlice() {
        runBlocking {
            val homeBooks = persistentListOf(
                HomeService.HomeBook(3, 1, "cover-1", "Book-1", "Author", "desc"),
                HomeService.HomeBook(3, 2, "cover-2", "Book-2", "Author", "desc"),
                HomeService.HomeBook(3, 3, "cover-3", "Book-3", "Author", "desc"),
                HomeService.HomeBook(3, 4, "cover-4", "Book-4", "Author", "desc"),
            )

            val outcome = HomePagingCoordinator().coordinateHomePaging(
                currentState = HomeUiState(
                    hasMoreHomeRecommend = true,
                    homeRecommendPage = 1,
                ),
                pageSize = 2,
                cachedHomeBooks = persistentListOf(),
                loadHomeBooks = { homeBooks },
            )

            assertThat(outcome.cachedHomeBooks).isEqualTo(homeBooks)
            assertThat(outcome.intents).containsExactly(
                HomeIntent.HomeRecommendBooksLoadSuccess(
                    books = persistentListOf(
                        homeBooks[2],
                        homeBooks[3],
                    ),
                    isRefresh = false,
                    hasMore = false,
                ),
            )
        }
    }

    @Test
    fun coordinateHomePaging_returnsFailureIntentWhenLoadingThrows() {
        runBlocking {
            val outcome = HomePagingCoordinator().coordinateHomePaging(
                currentState = HomeUiState(
                    hasMoreHomeRecommend = true,
                    homeRecommendPage = 1,
                ),
                pageSize = 2,
                cachedHomeBooks = persistentListOf(),
                loadHomeBooks = { error("boom") },
            )

            assertThat(outcome.cachedHomeBooks).isNull()
            assertThat(outcome.intents).containsExactly(
                HomeIntent.HomeRecommendBooksLoadFailure("boom"),
            )
        }
    }
}
