package com.novel.page.search

import com.novel.core.StableThrowable
import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.novel.page.component.LoadingStateComponent
import com.novel.page.component.ViewState
import com.novel.page.search.viewmodel.SearchIntent
import com.novel.page.search.viewmodel.SearchEffect
import com.novel.page.search.viewmodel.SearchViewModel
import com.novel.page.search.component.SearchHistorySection
import com.novel.page.search.component.RankingSection
import com.novel.page.search.component.SearchTopBar
import com.novel.page.search.skeleton.SearchPageSkeleton
import com.novel.ui.theme.NovelColors
import com.novel.utils.NavViewModel
import com.novel.utils.wdp

/**
 * 搜索页面组件
 * 
 * 小说应用的核心搜索功能页面，提供完整的搜索体验：
 * 
 * 🔍 核心功能：
 * - 实时搜索输入和关键词联想
 * - 搜索历史记录管理
 * - 热门书籍榜单展示
 * - 搜索结果导航
 * 
 * 📊 数据展示：
 * - 点击榜、推荐榜、新书榜
 * - 历史搜索记录（支持展开/收起）
 * - 骨架屏加载状态
 * 
 * 🎯 交互特性：
 * - MVI架构的响应式状态管理
 * - 一次性事件处理（导航、Toast）
 * - 错误状态的统一处理
 * 
 * @param onNavigateBack 返回上级页面的回调
 * @param onNavigateToBookDetail 导航到书籍详情的回调
 * @param viewModel 搜索页面的视图模型
 */
@Composable
fun SearchPage(
    onNavigateBack: () -> Unit,
    onNavigateToBookDetail: (Long) -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val TAG = "SearchPage"
    
    // 性能优化：使用新的@Composable状态访问方法替代collectAsState()
    val adapter = viewModel.adapter
    
    // 使用优化的@Composable状态访问方法
    val isLoading by adapter.mapState { it.isLoading }.collectAsState(initial = false)
    val error by adapter.mapState { it.error }.collectAsState(initial = null)
    val searchQuery by adapter.searchQueryState()
    val searchHistory by adapter.searchHistoryState()
    val isHistoryExpanded by adapter.isHistoryExpandedState()
    val novelRanking by adapter.novelRankingState()
    val dramaRanking by adapter.dramaRankingState()
    val newBookRanking by adapter.newBookRankingState()

    // 性能优化：缓存副作用处理回调
    val handleNavigateToBookDetail = remember(onNavigateToBookDetail) { { bookId: Long ->
        TimberLogger.d(TAG, "导航到书籍详情: $bookId")
        onNavigateToBookDetail(bookId)
    } }
    
    val handleNavigateToSearchResult = remember { { query: String ->
        TimberLogger.d(TAG, "导航到搜索结果: $query")
        NavViewModel.navigateToSearchResult(query)
    } }
    
    val handleNavigateBack = remember(onNavigateBack) { {
        TimberLogger.d(TAG, "返回上级页面")
        onNavigateBack()
    } }
    
    val handleShowToast = remember { { message: String ->
        TimberLogger.d(TAG, "显示Toast: $message")
        // TODO: 集成Toast显示组件
    } }
    
    // 处理一次性副作用（导航、Toast等）
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.NavigateToBookDetail -> {
                    handleNavigateToBookDetail(effect.bookId)
                }

                is SearchEffect.NavigateToSearchResult -> {
                    handleNavigateToSearchResult(effect.query)
                }

                is SearchEffect.NavigateBack -> {
                    handleNavigateBack()
                }

                is SearchEffect.ShowToast -> {
                    handleShowToast(effect.message)
                }
            }
        }
    }

    // 初始化搜索页面数据
    LaunchedEffect(Unit) {
        TimberLogger.d(TAG, "初始化搜索页面数据")
        viewModel.sendIntent(SearchIntent.LoadInitialData)
    }

    // 性能优化：缓存重试回调
    val handleLoadingRetry = remember(viewModel) { {
        TimberLogger.d(TAG, "重试加载搜索页面数据")
        viewModel.sendIntent(SearchIntent.LoadInitialData)
    } }
    
    // LoadingStateComponent适配器，统一管理加载和错误状态
    val loadingStateComponent = remember(
        isLoading,
        error,
        handleLoadingRetry
    ) {
        object : LoadingStateComponent {
            override val loading: Boolean get() = isLoading
            override val containsCancelable: Boolean get() = false
            override val viewState: ViewState
                get() = when {
                    error != null -> ViewState.Error(StableThrowable(Exception(error)))
                    else -> ViewState.Idle
                }

            override fun showLoading(show: Boolean) {}
            override fun cancelLoading() {}
            override fun showViewState(viewState: ViewState) {}
            override fun retry() {
                handleLoadingRetry()
            }
        }
    }

    LoadingStateComponent(
        component = loadingStateComponent,
        modifier = Modifier.fillMaxSize(),
        backgroundColor = NovelColors.NovelBookBackground.copy(alpha = 0.7f)
    ) {
        // 根据加载状态显示骨架屏或正常内容
        if (isLoading) {
            SearchPageSkeleton()
        } else {
            SearchPageContent(
                searchQuery = searchQuery,
                searchHistory = searchHistory,
                isHistoryExpanded = isHistoryExpanded,
                novelRanking = novelRanking,
                dramaRanking = dramaRanking,
                newBookRanking = newBookRanking,
                onIntent = viewModel::sendIntent
            )
        }
    }
}

/**
 * 搜索页面内容组件
 * 
 * 分离的内容渲染组件，提升可读性和性能：
 * - 搜索输入栏
 * - 历史记录区域
 * - 榜单推荐区域
 * 
 * @param searchQuery 当前搜索查询
 * @param searchHistory 搜索历史记录
 * @param isHistoryExpanded 历史记录是否展开
 * @param novelRanking 小说榜单数据
 * @param dramaRanking 剧本榜单数据
 * @param newBookRanking 新书榜单数据
 * @param onIntent 用户操作回调
 */
@Composable
fun SearchPageContent(
    searchQuery: String,
    searchHistory: kotlinx.collections.immutable.ImmutableList<String>,
    isHistoryExpanded: Boolean,
    novelRanking: kotlinx.collections.immutable.ImmutableList<com.novel.page.search.component.SearchRankingItem>,
    dramaRanking: kotlinx.collections.immutable.ImmutableList<com.novel.page.search.component.SearchRankingItem>,
    newBookRanking: kotlinx.collections.immutable.ImmutableList<com.novel.page.search.component.SearchRankingItem>,
    onIntent: (SearchIntent) -> Unit
) {
    val TAG = "SearchPage"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NovelColors.NovelBackground)
    ) {
        // 性能优化：缓存搜索栏的回调函数
        val onQueryChange = remember(onIntent) { { query: String ->
            onIntent(SearchIntent.UpdateSearchQuery(query))
        } }
        
        val onBackClick = remember(onIntent) { {
            onIntent(SearchIntent.NavigateBack)
        } }
        
        val onSearchClick = remember(onIntent, searchQuery) { {
            if (searchQuery.isNotBlank()) {
                TimberLogger.d(TAG, "执行搜索: $searchQuery")
                onIntent(SearchIntent.PerformSearch(searchQuery))
            }
        } }
        
        // 顶部搜索栏
        SearchTopBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onBackClick = onBackClick,
            onSearchClick = onSearchClick
        )

        // 主要内容区域 - 可滚动
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 搜索历史记录区域
            if (searchHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.wdp))
                
                // 性能优化：缓存历史记录回调函数
                val onHistoryClick = remember(onIntent) { { query: String ->
                    // 点击历史记录执行搜索
                    onIntent(SearchIntent.UpdateSearchQuery(query))
                    onIntent(SearchIntent.PerformSearch(query))
                } }
                
                val onToggleExpansion = remember(onIntent) { {
                    onIntent(SearchIntent.ToggleHistoryExpansion)
                } }
                
                SearchHistorySection(
                    history = searchHistory,
                    isExpanded = isHistoryExpanded,
                    onHistoryClick = onHistoryClick,
                    onToggleExpansion = onToggleExpansion
                )
            }

            Spacer(modifier = Modifier.height(24.wdp))

            // 性能优化：缓存榜单区域回调函数
            val onRankingItemClick = remember(onIntent) { { bookId: Long ->
                onIntent(SearchIntent.NavigateToBookDetail(bookId))
            } }
            
            val onViewFullRanking = remember(onIntent, novelRanking, dramaRanking, newBookRanking) { { rankingType: String ->
                // 根据榜单类型获取对应数据
                val rankingItems = when (rankingType) {
                    "点击榜" -> novelRanking
                    "推荐榜" -> dramaRanking
                    "新书榜" -> newBookRanking
                    else -> emptyList()
                }
                TimberLogger.d(TAG, "查看完整榜单: $rankingType, 项目数: ${rankingItems.size}")
                NavViewModel.navigateToFullRanking(rankingType, rankingItems)
            } }
            
            // 推荐榜单区域
            RankingSection(
                novelRanking = novelRanking,
                dramaRanking = dramaRanking,
                newBookRanking = newBookRanking,
                onRankingItemClick = onRankingItemClick,
                onViewFullRanking = onViewFullRanking
            )
            Spacer(modifier = Modifier.height(24.wdp))
        }
    }
}
