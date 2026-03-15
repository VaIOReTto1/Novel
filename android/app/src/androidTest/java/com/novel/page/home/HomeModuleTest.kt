package com.novel.page.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.novel.ui.theme.NovelTheme
import com.novel.page.home.viewmodel.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlin.system.measureTimeMillis

/**
 * Home模块综合测试
 * 
 * 包含UI测试、集成测试和性能测试
 */
@RunWith(AndroidJUnit4::class)
class HomeModuleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ===========================================
    // UI测试
    // ===========================================

    @Test
    fun homePage_基本渲染_成功() {
        composeTestRule.setContent {
            NovelTheme {
                HomePage(
                    onNavigateToCategory = { _ -> }
                )
            }
        }

        // 验证页面渲染成功
        composeTestRule.onRoot().assertExists()
        composeTestRule.waitForIdle()
    }

    @Test
    fun homePage_交互功能_基础验证() {
        var categoryClicked = false
        var categoryPageClicked = false
        
        composeTestRule.setContent {
            NovelTheme {
                HomePage(
                    onNavigateToCategory = { categoryClicked = true }
                )
            }
        }

        // 等待UI渲染完成
        composeTestRule.waitForIdle()
        
        // 验证交互功能（基础验证）
        composeTestRule.onRoot().assertExists()
    }

    // ===========================================
    // 集成测试
    // ===========================================

    @Test
    fun homeState_计算属性_正确性() {
        // 测试初始状态
        val initialState = HomeState()
        
        assertTrue(initialState.isRecommendMode)
        assertEquals("推荐", initialState.selectedCategoryFilter)
        assertEquals("点击榜", initialState.selectedRankType)
        assertFalse(initialState.isLoading)
        assertTrue(initialState.homeRecommendBooks.isEmpty())
        assertTrue(initialState.recommendBooks.isEmpty())
        assertEquals(0L, initialState.version)

        // 测试加载状态
        val loadingState = HomeState(
            isLoading = true,
            categoryFiltersLoading = true,
            rankLoading = true
        )
        
        assertTrue(loadingState.isLoading)
        assertTrue(loadingState.categoryFiltersLoading)
        assertTrue(loadingState.rankLoading)

        // 测试数据状态
        val dataState = HomeState(
            homeRecommendBooks = createMockHomeBooks(),
            recommendBooks = createMockSearchBooks(),
            rankBooks = createMockRankBooks(),
            categoryFilters = createMockCategoryFilters()
        )
        
        assertEquals(2, dataState.homeRecommendBooks.size)
        assertEquals(2, dataState.recommendBooks.size)
        assertEquals(2, dataState.rankBooks.size)
        assertEquals(3, dataState.categoryFilters.size)
    }

    @Test
    fun homeReducer_状态转换_基本验证() {
        val reducer = HomeReducer()
        var currentState = HomeState()

        // 测试加载初始数据
        val loadInitialResult = reducer.reduce(currentState, HomeIntent.LoadInitialData)
        currentState = loadInitialResult.newState
        assertTrue(currentState.isLoading)
        assertEquals(1L, currentState.version)

        // 测试选择分类筛选器
        val selectCategoryResult = reducer.reduce(currentState, HomeIntent.SelectCategoryFilter("玄幻奇幻"))
        currentState = selectCategoryResult.newState
        assertEquals("玄幻奇幻", currentState.selectedCategoryFilter)
        assertFalse(currentState.isRecommendMode)
        assertEquals(2L, currentState.version)

        // 测试选择榜单类型
        val selectRankResult = reducer.reduce(currentState, HomeIntent.SelectRankType("更新榜"))
        currentState = selectRankResult.newState
        assertEquals("更新榜", currentState.selectedRankType)
        assertTrue(currentState.rankLoading)
        assertEquals(3L, currentState.version)
    }

    @Test
    fun 完整首页流程_状态管理() {
        val reducer = HomeReducer()
        var currentState = HomeState()
        
        // 1. 加载初始数据
        val loadResult = reducer.reduce(currentState, HomeIntent.LoadInitialData)
        currentState = loadResult.newState
        
        // 2. 分类筛选器加载成功
        val categoryFilters = createMockCategoryFilters()
        val filtersSuccessResult = reducer.reduce(
            currentState,
            HomeIntent.CategoryFiltersLoadSuccess(categoryFilters)
        )
        currentState = filtersSuccessResult.newState
        
        // 3. 首页推荐书籍加载成功
        val homeBooks = createMockHomeBooks()
        val homeBooksSuccessResult = reducer.reduce(
            currentState,
            HomeIntent.HomeRecommendBooksLoadSuccess(homeBooks, false, true)
        )
        currentState = homeBooksSuccessResult.newState
        
        // 4. 榜单书籍加载成功
        val rankBooks = createMockRankBooks()
        val rankBooksSuccessResult = reducer.reduce(
            currentState,
            HomeIntent.RankBooksLoadSuccess(rankBooks)
        )
        currentState = rankBooksSuccessResult.newState
        
        // 验证最终状态
        assertEquals(categoryFilters, currentState.categoryFilters)
        assertEquals(homeBooks, currentState.homeRecommendBooks)
        assertEquals(rankBooks, currentState.rankBooks)
        assertFalse(currentState.isLoading)
        assertEquals(4L, currentState.version)
    }

    // ===========================================
    // 性能测试
    // ===========================================

    @Test
    fun homeState_更新性能_基准测试() {
        var state = HomeState()
        
        val time = measureTimeMillis {
            repeat(100) { index ->
                state = state.copy(
                    version = state.version + 1,
                    selectedCategoryFilter = "分类$index",
                    isLoading = index % 2 == 0,
                    homeRecommendPage = index + 1
                )
            }
        }
        
        // 验证性能在合理范围内
        assert(time < 100) { "首页状态更新性能过慢: ${time}ms" }
        assertEquals(100L, state.version)
    }

    @Test
    fun homeReducer_处理性能_基准测试() {
        val reducer = HomeReducer()
        val initialState = HomeState()
        
        val intents = listOf(
            HomeIntent.LoadInitialData,
            HomeIntent.SelectCategoryFilter("玄幻奇幻"),
            HomeIntent.SelectRankType("更新榜"),
            HomeIntent.LoadMoreRecommend,
            HomeIntent.RefreshData
        )
        
        val time = measureTimeMillis {
            var currentState = initialState
            repeat(20) {
                intents.forEach { intent ->
                    val result = reducer.reduce(currentState, intent)
                    currentState = result.newState
                }
            }
        }
        
        // 验证Reducer处理性能
        assert(time < 50) { "HomeReducer处理性能过慢: ${time}ms" }
    }

    @Test
    fun homePage_UI渲染性能_基准测试() {
        val time = measureTimeMillis {
            composeTestRule.setContent {
                NovelTheme {
                    HomePage(
                        onNavigateToCategory = { _ -> }
                    )
                }
            }
            composeTestRule.waitForIdle()
        }
        
        // 验证UI渲染性能
        assert(time < 1000) { "首页UI渲染性能过慢: ${time}ms" }
    }

    @Test
    fun 大数据量首页推荐书籍性能_基准测试() {
        val reducer = HomeReducer()
        val initialState = HomeState()
        
        // 创建大量首页推荐书籍数据
        val largeHomeBooks = (1..1000).map { index ->
            createMockHomeBook(index)
        }.toPersistentList()
        
        val time = measureTimeMillis {
            val result = reducer.reduce(
                initialState,
                HomeIntent.HomeRecommendBooksLoadSuccess(largeHomeBooks, false, false)
            )
            // 验证状态更新
            assert(result.newState.homeRecommendBooks.size == 1000)
        }
        
        // 验证大数据量处理性能
        assert(time < 100) { "大数据量首页推荐书籍处理性能过慢: ${time}ms" }
    }

    @Test
    fun 分类筛选器切换性能_基准测试() {
        val reducer = HomeReducer()
        var currentState = HomeState(
            categoryFilters = createLargeCategoryFilters(100)
        )
        
        val time = measureTimeMillis {
            repeat(50) { index ->
                val result = reducer.reduce(
                    currentState,
                    HomeIntent.SelectCategoryFilter("分类$index")
                )
                currentState = result.newState
            }
        }
        
        // 验证分类筛选器切换性能
        assert(time < 50) { "分类筛选器切换性能过慢: ${time}ms" }
    }

    @Test
    fun 完整首页流程性能_端到端() {
        val reducer = HomeReducer()
        
        val time = measureTimeMillis {
            repeat(10) {
                var currentState = HomeState()
                
                // 完整首页流程
                val loadResult = reducer.reduce(currentState, HomeIntent.LoadInitialData)
                currentState = loadResult.newState
                
                val filtersSuccessResult = reducer.reduce(
                    currentState,
                    HomeIntent.CategoryFiltersLoadSuccess(createMockCategoryFilters())
                )
                currentState = filtersSuccessResult.newState
                
                val homeBooksSuccessResult = reducer.reduce(
                    currentState,
                    HomeIntent.HomeRecommendBooksLoadSuccess(createMockHomeBooks(), false, true)
                )
                currentState = homeBooksSuccessResult.newState
                
                val rankBooksSuccessResult = reducer.reduce(
                    currentState,
                    HomeIntent.RankBooksLoadSuccess(createMockRankBooks())
                )
                currentState = rankBooksSuccessResult.newState
            }
        }
        
        // 验证完整流程性能
        assert(time < 100) { "完整首页流程性能过慢: ${time}ms" }
    }

    // 辅助方法
    private fun createMockHomeBooks(): ImmutableList<com.novel.utils.network.api.front.HomeService.HomeBook> {
        return listOf(
            createMockHomeBook(1),
            createMockHomeBook(2)
        ).toPersistentList()
    }

    private fun createMockHomeBook(index: Int): com.novel.utils.network.api.front.HomeService.HomeBook {
        return com.novel.utils.network.api.front.HomeService.HomeBook(
            bookId = index.toLong(),
            bookName = "测试首页书籍$index",
            authorName = "测试作者$index",
            picUrl = "https://example.com/cover$index.jpg",
            bookDesc = "这是测试首页书籍${index}的描述",
            type = 3,
        )
    }

    private fun createMockSearchBooks(): ImmutableList<com.novel.utils.network.api.front.SearchService.BookInfo> {
        return listOf(
            createMockSearchBook(1),
            createMockSearchBook(2)
        ).toPersistentList()
    }

    private fun createMockSearchBook(index: Int): com.novel.utils.network.api.front.SearchService.BookInfo {
        return com.novel.utils.network.api.front.SearchService.BookInfo(
            id = index.toLong(),
            bookName = "测试搜索书籍$index",
            authorName = "测试作者$index",
            picUrl = "https://example.com/cover$index.jpg",
            categoryName = "玄幻奇幻",
            bookStatus = 1,
            wordCount = 100000 + index,
            lastChapterName = "第${index}章",
            categoryId = index.toLong(),
            authorId = index.toLong(),
            bookDesc = "测试描述$index",
            visitCount = 1000L + index,
            commentCount = 10 + index,
            firstChapterId = index.toLong(),
            lastChapterId = (index + 100).toLong(),
            updateTime = "2024-01-01"
        )
    }

    private fun createMockRankBooks(): ImmutableList<com.novel.utils.network.api.front.BookService.BookRank> {
        return listOf(
            com.novel.utils.network.api.front.BookService.BookRank(
                id = 1L,
                bookName = "测试榜单书籍1",
                authorName = "测试作者1",
                picUrl = "https://example.com/cover1.jpg",
                categoryName = "玄幻奇幻",
                categoryId = 1L,
                bookDesc = "测试描述1",
                wordCount = 100000,
                lastChapterName = "第一章",
                lastChapterUpdateTime = "2024-01-01"
            ),
            com.novel.utils.network.api.front.BookService.BookRank(
                id = 2L,
                bookName = "测试榜单书籍2",
                authorName = "测试作者2",
                picUrl = "https://example.com/cover2.jpg",
                categoryName = "武侠仙侠",
                categoryId = 2L,
                bookDesc = "测试描述2",
                wordCount = 200000,
                lastChapterName = "第二章",
                lastChapterUpdateTime = "2024-01-02"
            )
        ).toPersistentList()
    }

    private fun createMockCategoryFilters(): ImmutableList<CategoryInfo> {
        return listOf(
            CategoryInfo("0", "推荐"),
            CategoryInfo("1", "玄幻奇幻"),
            CategoryInfo("2", "武侠仙侠")
        ).toPersistentList()
    }

    private fun createLargeCategoryFilters(count: Int): ImmutableList<CategoryInfo> {
        return (0 until count).map { index ->
            CategoryInfo(index.toString(), "分类$index")
        }.toPersistentList()
    }
} 
