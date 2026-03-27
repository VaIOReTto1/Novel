package com.novel.page.search.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.search.gateway.CategoryFilterGateway
import com.novel.page.search.gateway.SearchQueryGateway
import com.novel.page.search.gateway.SearchQueryResult
import com.novel.page.search.repository.SearchParams
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchResultViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `perform search updates search results`() = runTest(dispatcher) {
        val gateway = FakeSearchQueryGateway()
        val viewModel = SearchResultViewModel(
            searchQueryGateway = gateway,
            categoryFilterGateway = FakeCategoryFilterGateway(),
        )

        viewModel.sendIntent(SearchResultIntent.PerformSearch("novel"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.books).hasSize(1)
        assertThat(state.totalResults).isEqualTo(1)
        assertThat(gateway.lastParams?.pageSize).isEqualTo(20)
    }

    @Test
    fun `perform search uses debug page size override when provided`() = runTest(dispatcher) {
        val gateway = FakeSearchQueryGateway()
        val viewModel = SearchResultViewModel(
            searchQueryGateway = gateway,
            categoryFilterGateway = FakeCategoryFilterGateway(),
            debugSearchPageSizeOverrideProvider = { 5 },
        )

        viewModel.sendIntent(SearchResultIntent.PerformSearch("novel"))
        advanceUntilIdle()

        assertThat(gateway.lastParams?.pageSize).isEqualTo(5)
    }

    private class FakeSearchQueryGateway : SearchQueryGateway {
        var lastParams: SearchParams? = null

        override suspend fun searchBooks(params: SearchParams): SearchQueryResult {
            lastParams = params
            return SearchQueryResult(
                list = persistentListOf(
                    BookInfoRespDto(
                        id = 1L,
                        categoryId = 1L,
                        categoryName = "Fantasy",
                        picUrl = null,
                        bookName = "Novel",
                        authorId = 2L,
                        authorName = "Author",
                        bookDesc = "Desc",
                        bookStatus = 0,
                        visitCount = 1L,
                        wordCount = 1000,
                        commentCount = 0,
                        firstChapterId = null,
                        lastChapterId = null,
                        lastChapterName = null,
                        updateTime = null,
                    ),
                ),
                total = 1,
                pages = 1,
            )
        }
    }

    private class FakeCategoryFilterGateway : CategoryFilterGateway {
        override suspend fun getCategoryFilters(): Result<List<CategoryFilter>> {
            return Result.success(listOf(CategoryFilter(-1, "All")))
        }
    }
}
