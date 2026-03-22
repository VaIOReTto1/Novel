package com.novel.page.home.viewmodel

import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeStateProjectorTest {

    @Test
    fun toHomeUiState_preservesCoreFields() {
        val state = HomeState(
            version = 2,
            isLoading = true,
            error = "boom",
            isRefreshing = true,
            categories = persistentListOf(
                HomeCategoryEntity(1, "玄幻", null, 0)
            ),
            carouselBooks = persistentListOf(
                HomeBookEntity(
                    id = 11,
                    title = "Book",
                    author = "Author",
                    coverUrl = "cover",
                    description = "desc",
                    category = "",
                    isCompleted = false,
                    isVip = false,
                    updateTime = 1L,
                    type = "carousel"
                )
            ),
            selectedCategoryFilter = "首页",
            selectedRankType = "点击榜",
            isRecommendMode = true
        )

        val uiState = HomeStateProjector.toHomeUiState(state)

        assertEquals(2L, uiState.version)
        assertTrue(uiState.isLoading)
        assertEquals("boom", uiState.error)
        assertTrue(uiState.isRefreshing)
        assertEquals("首页", uiState.selectedCategoryFilter)
        assertEquals("点击榜", uiState.selectedRankType)
        assertTrue(uiState.isRecommendMode)
        assertEquals(1, uiState.categories.size)
        assertEquals(1, uiState.carouselBooks.size)
    }

    @Test
    fun toScreenState_usesHomeRecommendBooksInRecommendMode() {
        val state = HomeState(
            searchQuery = "斗破",
            selectedCategoryFilter = "首页",
            categoryFilters = persistentListOf(CategoryInfo("0", "首页")),
            carouselBooks = persistentListOf(),
            hotBooks = persistentListOf(),
            newBooks = persistentListOf(),
            vipBooks = persistentListOf(),
            rankBooks = persistentListOf(
                BookService.BookRank(
                    id = 1,
                    categoryId = 1,
                    categoryName = "玄幻",
                    picUrl = "cover",
                    bookName = "Rank Book",
                    authorName = "Author",
                    bookDesc = "desc",
                    wordCount = 1000,
                    lastChapterName = "chapter",
                    lastChapterUpdateTime = "today"
                )
            ),
            homeRecommendBooks = persistentListOf(
                HomeService.HomeBook(
                    type = 3,
                    bookId = 101,
                    picUrl = "cover",
                    bookName = "Home Recommend",
                    authorName = "Author",
                    bookDesc = "desc"
                )
            ),
            isRecommendMode = true,
            hasMoreHomeRecommend = true,
            homeRecommendLoading = false
        )

        val screenState = HomeStateProjector.toScreenState(state)

        assertTrue(screenState.isRecommendMode)
        assertEquals("首页推荐", screenState.recommendModeText)
        assertEquals(1, screenState.currentRecommendBooks.size)
        assertEquals("Home Recommend", screenState.currentRecommendBooks.first().title)
        assertTrue(screenState.canPerformSearch)
        assertTrue(screenState.canLoadMoreRecommend)
        assertEquals("点击加载更多", screenState.loadMoreText)
    }

    @Test
    fun toScreenState_usesCategoryRecommendBooksOutsideRecommendMode() {
        val state = HomeState(
            selectedCategoryFilter = "玄幻奇幻",
            categoryFilters = persistentListOf(
                CategoryInfo("0", "首页"),
                CategoryInfo("1", "玄幻奇幻")
            ),
            recommendBooks = persistentListOf(
                SearchService.BookInfo(
                    id = 201,
                    categoryId = 1,
                    categoryName = "玄幻奇幻",
                    picUrl = "cover",
                    bookName = "Category Recommend",
                    authorId = 2,
                    authorName = "Author",
                    bookDesc = "desc",
                    bookStatus = 1,
                    visitCount = 10,
                    wordCount = 3000,
                    commentCount = 1,
                    firstChapterId = 1,
                    lastChapterId = 2,
                    lastChapterName = "chapter",
                    updateTime = "today"
                )
            ),
            isRecommendMode = false,
            hasMoreRecommend = false,
            recommendLoading = false
        )

        val screenState = HomeStateProjector.toScreenState(state)

        assertFalse(screenState.isRecommendMode)
        assertEquals("分类推荐 - 玄幻奇幻", screenState.recommendModeText)
        assertEquals(1, screenState.currentRecommendBooks.size)
        assertEquals("Category Recommend", screenState.currentRecommendBooks.first().title)
        assertFalse(screenState.canLoadMoreRecommend)
        assertEquals("已加载全部", screenState.loadMoreText)
    }
}
