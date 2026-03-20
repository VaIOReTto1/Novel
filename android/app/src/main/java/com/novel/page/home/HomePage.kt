package com.novel.page.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.novel.page.home.component.HomeFilterBar
import com.novel.page.home.component.HomeRankPanel
import com.novel.page.home.component.HomeRecommendGrid
import com.novel.page.home.component.HomeRecommendLoadMoreIndicator
import com.novel.page.home.component.HomeTopBar
import com.novel.page.home.viewmodel.HomeIntent
import com.novel.page.home.viewmodel.HomeEffect
import com.novel.page.home.viewmodel.HomeViewModel
import com.novel.page.home.skeleton.HomePageSkeleton
import com.novel.page.component.rememberFlipBookAnimationController
import com.novel.ui.theme.NovelColors
import com.novel.utils.wdp
import com.novel.utils.NavViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.novel.page.component.FlipBookAnimationController
import com.novel.utils.StableCallbacks
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * 新版首页 - 支持下拉刷新、上拉加载和3D翻书动画
 * 
 * 采用完整MVI架构：
 * - 直接使用HomeIntent处理用户交互
 * - 监听HomeEffect处理一次性副作用
 * - 订阅HomeState流获取UI状态
 * - 无业务逻辑，纯UI展示和事件转发
 * 
 * 性能优化特性：
 * - 使用优化的@Composable状态访问方法提升skippable比例
 * - 细粒度状态订阅，避免不必要的重组
 * - 稳定的回调函数缓存
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomePage(
    onNavigateToCategory: (Long) -> Unit = StableCallbacks.DefaultNavigateToCategory,
    globalFlipBookController: FlipBookAnimationController? = null
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    
    // 使用优化的@Composable状态访问方法替代unstable的screenState.collectAsStateWithLifecycle()
    val adapter = viewModel.adapter
    val isLoading by adapter.createLoadingState()
    val error by adapter.createErrorState()
    val isSuccess by adapter.createSuccessState()
    val isRefreshing by adapter.isRefreshingState()
    val searchQuery by adapter.searchQueryState()
    val categories by adapter.categoriesState()
    val selectedCategoryFilter by adapter.selectedCategoryFilterState()
    val categoryFilters by adapter.categoryFiltersState()
    val carouselBooks by adapter.carouselBooksState()
    val hotBooks by adapter.hotBooksState()
    val newBooks by adapter.newBooksState()
    val vipBooks by adapter.vipBooksState()
    val rankBooks by adapter.rankBooksState()
    val selectedRankType by adapter.selectedRankTypeState()
    val currentRecommendBooks by adapter.currentRecommendBooksState()
    val isRecommendMode by adapter.isRecommendModeState()
    val homeRecommendLoading by adapter.homeRecommendLoadingState()
    val recommendLoading by adapter.recommendLoadingState()
    val hasMoreRecommend by adapter.hasMoreRecommendState()
    val hasMoreHomeRecommend by adapter.hasMoreHomeRecommendState()
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    // 使用传入的全局动画控制器，如果没有则创建本地控制器
    val flipBookController = globalFlipBookController ?: rememberFlipBookAnimationController()
    
    // 生命周期感知 - 页面恢复时检查数据
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onStart(owner: androidx.lifecycle.LifecycleOwner) {
                super.onStart(owner)
                // 页面恢复可见时检查并恢复数据
                viewModel.sendIntent(HomeIntent.RestoreData)
            }
        })
    }
    
    // 下拉刷新状态
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = isRefreshing
    )
    
    // 优化：使用derivedStateOf进行屏幕尺寸计算，避免每次重组都重新计算
    val screenSize by remember {
        derivedStateOf {
            Pair(
                configuration.screenWidthDp * density.density,
                configuration.screenHeightDp * density.density
            )
        }
    }
    
    // 优化：使用细粒度状态访问，计算复合状态
    val canLoadMoreRecommend by remember {
        derivedStateOf {
            when {
                isRecommendMode -> hasMoreHomeRecommend && !homeRecommendLoading
                else -> hasMoreRecommend && !recommendLoading
            }
        }
    }
    
    // 性能优化：使用稳定回调，避免每次重组都创建新Lambda对象
    val navigateToSearch = StableCallbacks.rememberUnitCallback("navigateToSearch") {
        viewModel.sendIntent(HomeIntent.NavigateToSearch(""))
    }
    
    val navigateToCategoryPage = StableCallbacks.rememberUnitCallback("navigateToCategoryPage") {
        viewModel.sendIntent(HomeIntent.NavigateToCategory(1L))
    }
    
    val filterSelected = StableCallbacks.rememberCallback<String>("filterSelected") { filter ->
        viewModel.sendIntent(HomeIntent.SelectCategoryFilter(filter))
    }
    
    val rankTypeSelected = StableCallbacks.rememberCallback<String>("rankTypeSelected") { rankType ->
        viewModel.sendIntent(HomeIntent.SelectRankType(rankType))
    }
    
    val bookClick = StableCallbacks.rememberCallback<Long>("bookClick") { bookId ->
        viewModel.sendIntent(HomeIntent.NavigateToBookDetail(bookId))
    }
    
    val refresh = StableCallbacks.rememberUnitCallback("refresh") {
        viewModel.sendIntent(HomeIntent.RefreshData)
    }
    
    // 稳定的回调函数集合 - 使用稳定的对象封装，避免多个参数传递
    val callbacks = remember(navigateToSearch, navigateToCategoryPage, filterSelected, rankTypeSelected, bookClick, refresh) {
        @Stable
        object {
            val navigateToSearch: () -> Unit = navigateToSearch
            val navigateToCategoryPage: () -> Unit = navigateToCategoryPage  
            val filterSelected: (String) -> Unit = filterSelected
            val rankTypeSelected: (String) -> Unit = rankTypeSelected
            val bookClick: (Long) -> Unit = bookClick
            val refresh: () -> Unit = refresh
        }
    }
    
    // 监听副作用 - 使用MVI Effect系统
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToBook -> {
                    // 不再跳转，书籍内容在动画中显示
                }
                is HomeEffect.NavigateToBookDetail -> {
                    // 不再跳转，书籍内容在动画中显示
                }
                is HomeEffect.NavigateToCategory -> onNavigateToCategory(effect.categoryId)
                is HomeEffect.NavigateToSearch -> {
                    // 直接调用NavViewModel导航到搜索页面
                    NavViewModel.navigateToSearch(effect.query)
                }
                is HomeEffect.NavigateToCategoryPage -> callbacks.navigateToCategoryPage()
                is HomeEffect.NavigateToFullRanking -> {
                    // 导航到完整榜单页面 - 待实现
                }
                is HomeEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is HomeEffect.SendToReactNative -> {
                    // 处理React Native数据发送 - 已经在UseCase层处理
                }
            }
        }
    }
    
    // 优化滚动检测 - 使用derivedStateOf减少重组，并添加防抖
    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            val totalItems = layoutInfo.totalItemsCount
            
            totalItems > 0 && 
            visibleItems.isNotEmpty() && 
            visibleItems.lastOrNull()?.index == totalItems - 1
        }
    }
    
    // 监听滚动状态，实现上拉加载 - 添加distinctUntilChanged防止每帧触发
    LaunchedEffect(listState) {
        snapshotFlow { isAtBottom }
            .distinctUntilChanged()
            .collectLatest { atBottom ->
                if (atBottom && canLoadMoreRecommend) {
                    // 触发加载更多
                    if (isRecommendMode) {
                        viewModel.sendIntent(HomeIntent.LoadMoreHomeRecommend)
                    } else {
                        viewModel.sendIntent(HomeIntent.LoadMoreRecommend)
                    }
                }
            }
    }
    
    // 显示骨架屏或正常内容
    if (isLoading && currentRecommendBooks.isEmpty()) {
        HomePageSkeleton()
    } else {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = callbacks.refresh,
            modifier = Modifier.fillMaxSize()
        ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(NovelColors.NovelDivider),
            contentPadding = PaddingValues(vertical = 10.wdp),
            verticalArrangement = Arrangement.spacedBy(10.wdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 顶部搜索栏和分类按钮
            item(key = "top_bar") {
                HomeTopBar(
                    onSearchClick = callbacks.navigateToSearch,
                    onCategoryClick = callbacks.navigateToCategoryPage,
                    modifier = Modifier.padding(horizontal = 15.wdp)
                )
            }
            
            // 2. 分类筛选器
            item(key = "filter_bar") {
                HomeFilterBar(
                    filters = categoryFilters,
                    selectedFilter = selectedCategoryFilter,
                    onFilterSelected = callbacks.filterSelected
                )
            }
            
            // 3. 榜单面板 - 只在推荐模式下显示，支持3D翻书动画
            if (isRecommendMode) {
                item(key = "rank_panel_${selectedRankType}") {
                    // 单个榜单面板，支持内部切换和翻书动画
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.wdp),
                        contentAlignment = Alignment.Center
                    ) {
                        HomeRankPanel(
                            rankBooks = rankBooks,
                            selectedRankType = selectedRankType,
                            onRankTypeSelected = callbacks.rankTypeSelected,
                            onBookClick = { bookId, offset, size ->
                                // 榜单点击触发翻书动画
                                coroutineScope.launch {
                                    // 查找对应的书籍信息
                                    val book = rankBooks.find { it.id == bookId }
                                    
                                    flipBookController.startFlipAnimation(
                                        bookId = bookId.toString(),
                                        imageUrl = book?.picUrl ?: "",
                                        originalPosition = offset,
                                        originalSize = size,
                                        screenWidth = screenSize.first,
                                        screenHeight = screenSize.second
                                    )
                                }
                            },
                            // 传递翻书动画控制器
                            flipBookController = flipBookController
                        )
                    }
                }
            }
            
            // 4. 推荐书籍瀑布流 - 使用统一的 RecommendItem 类型
            item(key = "recommend_grid_${selectedCategoryFilter}_${isRecommendMode}") {
                HomeRecommendGrid(
                    recommendItems = currentRecommendBooks,
                    onBookClick = callbacks.bookClick,
                    onBookClickWithPosition = { bookId, offset, size ->
                        // 推荐流点击触发放大透明动画
                        coroutineScope.launch {
                            // 查找对应书籍的图片URL
                            val item = currentRecommendBooks.find { it.id == bookId }
                            
                            flipBookController.startScaleFadeAnimation(
                                bookId = bookId.toString(),
                                imageUrl = item?.coverUrl ?: "",
                                originalPosition = offset,
                                originalSize = size,
                                screenWidth = screenSize.first,
                                screenHeight = screenSize.second
                            )
                        }
                    },
                    onLoadMore = { /* 由上拉监听处理 */ },
                    modifier = Modifier.fillMaxWidth(),
                    fixedHeight = true,  // 在 LazyColumn 中使用固定高度版本
                    flipBookController = flipBookController  // 传递动画控制器
                )
            }
            
            // 5. 加载更多指示器 - 使用统一的状态
            item(key = "load_more_indicator_${canLoadMoreRecommend}") {
                // 修复：isLoading应该直接使用对应模式的loading状态，而不是基于canLoadMoreRecommend计算
                val isLoadingParam = if (isRecommendMode) homeRecommendLoading else recommendLoading
                val hasMoreDataParam = canLoadMoreRecommend
                HomeRecommendLoadMoreIndicator(
                    isLoading = isLoadingParam,
                    hasMoreData = hasMoreDataParam,
                    totalDataCount = currentRecommendBooks.size
                )
            }
            
            // 6. 全局加载状态
            if (isLoading) {
                item(key = "global_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.wdp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NovelColors.NovelMain
                        )
                    }
                }
            }
            
            // 7. 错误提示
            error?.let { errorMsg ->
                item(key = "error_card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.wdp),
                        colors = CardDefaults.cardColors(
                            containerColor = NovelColors.NovelError.copy(alpha = 0.9f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.wdp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "加载失败",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onError
                                )
                                Text(
                                    text = errorMsg,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                            TextButton(
                                onClick = { viewModel.sendIntent(HomeIntent.ClearError) }
                            ) {
                                Text("关闭")
                            }
                        }
                    }
                }
            }
        }
    }
    }
}
