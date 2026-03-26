package com.novel.page.home.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.home.gateway.HomeFeedGateway
import com.novel.page.home.gateway.HomeFeedSnapshot
import com.novel.page.home.gateway.HomeRecommendPage
import com.novel.page.home.gateway.HomeRnSyncGateway
import kotlinx.collections.immutable.ImmutableList
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
class HomeViewModelTest {

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
    fun `load initial data hydrates rank books and recommend books`() = runTest(dispatcher) {
        val viewModel = HomeViewModel(
            homeFeedGateway = FakeHomeFeedGateway(),
            homeRnSyncGateway = FakeHomeRnSyncGateway(),
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.rankBooks).hasSize(1)
        assertThat(state.currentRecommendBooks).hasSize(1)
    }

    @Test
    fun `load more home recommend appends cached books`() = runTest(dispatcher) {
        val viewModel = HomeViewModel(
            homeFeedGateway = FakeHomeFeedGateway(
                snapshot = HomeFeedSnapshot(
                    categoryFilters = persistentListOf(CategoryInfo("0", "首页")),
                    allHomeRecommendBooks = (1L..10L).map { index ->
                        HomeRecommendItem(
                            id = index,
                            title = "Recommend $index",
                            author = "Author",
                            coverUrl = "cover",
                            description = "Desc",
                        )
                    }.toPersistentRecommendList(),
                ),
            ),
            homeRnSyncGateway = FakeHomeRnSyncGateway(),
        )

        advanceUntilIdle()
        viewModel.sendIntent(HomeIntent.LoadMoreHomeRecommend)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.currentRecommendBooks).hasSize(10)
        assertThat(state.hasMoreHomeRecommend).isFalse()
    }

    @Test
    fun `restore data reloads initial snapshot when screen state is empty`() = runTest(dispatcher) {
        val gateway = FakeHomeFeedGateway(
            snapshot = HomeFeedSnapshot(
                categoryFilters = persistentListOf(CategoryInfo("0", "首页")),
                rankBooks = persistentListOf(),
                allHomeRecommendBooks = persistentListOf(),
            ),
            restoreSnapshot = HomeFeedSnapshot(
                categoryFilters = persistentListOf(CategoryInfo("0", "首页")),
                rankBooks = persistentListOf(
                    HomeRankBook(
                        id = 7L,
                        bookName = "Recovered Rank",
                        picUrl = "cover",
                        categoryName = "Fantasy",
                    ),
                ),
                allHomeRecommendBooks = persistentListOf(
                    HomeRecommendItem(
                        id = 8L,
                        title = "Recovered Recommend",
                        author = "Author",
                        coverUrl = "cover",
                        description = "Desc",
                    ),
                ),
            ),
        )
        val viewModel = HomeViewModel(
            homeFeedGateway = gateway,
            homeRnSyncGateway = FakeHomeRnSyncGateway(),
        )

        advanceUntilIdle()
        viewModel.sendIntent(HomeIntent.RestoreData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.rankBooks).hasSize(1)
        assertThat(state.currentRecommendBooks).hasSize(1)
        assertThat(gateway.loadInitialDataCalls).isEqualTo(2)
    }

    @Test
    fun `select category filter loads category recommend page`() = runTest(dispatcher) {
        val viewModel = HomeViewModel(
            homeFeedGateway = FakeHomeFeedGateway(
                snapshot = HomeFeedSnapshot(
                    categoryFilters = persistentListOf(
                        CategoryInfo("0", "首页"),
                        CategoryInfo("1", "玄幻奇幻"),
                    ),
                ),
                categoryRecommendPage = HomeRecommendPage(
                    items = persistentListOf(
                        CategoryRecommendItem(
                            id = 11L,
                            title = "Category Recommend",
                            author = "Author",
                            coverUrl = "cover",
                            categoryName = "玄幻奇幻",
                            bookStatus = 1,
                            wordCount = 4000L,
                        ),
                    ),
                    hasMore = false,
                ),
            ),
            homeRnSyncGateway = FakeHomeRnSyncGateway(),
        )

        advanceUntilIdle()
        viewModel.sendIntent(HomeIntent.SelectCategoryFilter("玄幻奇幻"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.isRecommendMode).isFalse()
        assertThat(state.currentRecommendBooks).hasSize(1)
        assertThat((state.currentRecommendBooks.first() as CategoryRecommendItem).categoryName)
            .isEqualTo("玄幻奇幻")
    }

    private class FakeHomeFeedGateway(
        private val snapshot: HomeFeedSnapshot = HomeFeedSnapshot(
            categoryFilters = persistentListOf(CategoryInfo("0", "首页")),
            rankBooks = persistentListOf(
                HomeRankBook(
                    id = 1L,
                    bookName = "Rank",
                    picUrl = "cover",
                    categoryName = "Fantasy",
                ),
            ),
            allHomeRecommendBooks = persistentListOf(
                HomeRecommendItem(
                    id = 2L,
                    title = "Recommend",
                    author = "Author",
                    coverUrl = "cover",
                    description = "Desc",
                ),
            ),
        ),
        private val categoryRecommendPage: HomeRecommendPage = HomeRecommendPage(),
        private val restoreSnapshot: HomeFeedSnapshot? = null,
        private val allHomeRecommendBooks: ImmutableList<HomeRecommendItem> = persistentListOf(),
    ) : HomeFeedGateway {
        var loadInitialDataCalls: Int = 0
        var loadAllHomeRecommendBooksCalls: Int = 0

        override suspend fun loadInitialData(): HomeFeedSnapshot {
            loadInitialDataCalls += 1
            return if (loadInitialDataCalls == 1) snapshot else restoreSnapshot ?: snapshot
        }

        override suspend fun refreshData(): HomeFeedSnapshot = snapshot

        override suspend fun loadRankBooks(rankType: String) = snapshot.rankBooks

        override suspend fun loadCategoryFilters() = snapshot.categoryFilters

        override suspend fun loadCategoryRecommendBooks(
            categoryName: String,
            categoryFilters: ImmutableList<CategoryInfo>,
            pageNum: Int,
            pageSize: Int,
        ): HomeRecommendPage = categoryRecommendPage

        override suspend fun loadAllHomeRecommendBooks(refresh: Boolean): ImmutableList<HomeRecommendItem> {
            loadAllHomeRecommendBooksCalls += 1
            return allHomeRecommendBooks
        }
    }

    private class FakeHomeRnSyncGateway : HomeRnSyncGateway {
        override suspend fun sync() = Unit
    }

    private fun List<HomeRecommendItem>.toPersistentRecommendList(): ImmutableList<HomeRecommendItem> =
        persistentListOf<HomeRecommendItem>().addAll(this)
}
