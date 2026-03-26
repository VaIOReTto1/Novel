package com.novel.page.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.novel.page.home.viewmodel.CategoryInfo
import com.novel.page.home.viewmodel.CategoryRecommendItem
import com.novel.page.home.viewmodel.HomeIntent
import com.novel.page.home.viewmodel.HomeRankBook
import com.novel.page.home.viewmodel.HomeReducer
import com.novel.page.home.viewmodel.HomeState
import com.novel.page.home.viewmodel.HomeStateProjector
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeModuleTest {

    @Test
    fun homeState_defaultsMatchFeatureContract() {
        val state = HomeState()

        assertTrue(state.isRecommendMode)
        assertTrue(state.currentRecommendBooks.isEmpty())
        assertTrue(state.rankBooks.isEmpty())
        assertEquals(HomeIntent.LoadInitialData, HomeIntent.LoadInitialData)
    }

    @Test
    fun homeReducer_loadInitialDataMarksLoading() {
        val result = HomeReducer().reduce(
            currentState = HomeState(),
            intent = HomeIntent.LoadInitialData,
        )

        assertTrue(result.newState.isLoading)
        assertEquals(1L, result.newState.version)
    }

    @Test
    fun homeReducer_selectCategorySwitchesOutOfRecommendMode() {
        val initialState = HomeState(
            categoryFilters = persistentListOf(
                CategoryInfo("0", "首页"),
                CategoryInfo("1", "玄幻奇幻"),
            ),
        )

        val result = HomeReducer().reduce(
            currentState = initialState,
            intent = HomeIntent.SelectCategoryFilter("玄幻奇幻"),
        )

        assertEquals("玄幻奇幻", result.newState.selectedCategoryFilter)
        assertFalse(result.newState.isRecommendMode)
        assertEquals(1L, result.newState.version)
    }

    @Test
    fun homeStateProjector_reflectsCategoryRecommendMode() {
        val state = HomeState(
            categoryFilters = persistentListOf(
                CategoryInfo("0", "首页"),
                CategoryInfo("1", "玄幻奇幻"),
            ),
            selectedCategoryFilter = "玄幻奇幻",
            isRecommendMode = false,
            currentRecommendBooks = persistentListOf(
                CategoryRecommendItem(
                    id = 11L,
                    title = "Category Recommend",
                    author = "Author",
                    coverUrl = "cover",
                    categoryName = "玄幻奇幻",
                    bookStatus = 1,
                    wordCount = 4_000L,
                ),
            ),
            rankBooks = persistentListOf(
                HomeRankBook(
                    id = 1L,
                    bookName = "Rank",
                    picUrl = "cover",
                    categoryName = "玄幻",
                ),
            ),
            hasMoreRecommend = false,
        )

        val screenState = HomeStateProjector.toScreenState(state)

        assertFalse(screenState.isRecommendMode)
        assertEquals(1, screenState.currentRecommendBooks.size)
        assertFalse(screenState.canLoadMoreRecommend)
    }
}
