package com.novel.page.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novel.page.component.LoadingStateComponent
import com.novel.page.component.NovelText
import com.novel.page.search.component.*
import com.novel.page.search.viewmodel.*
import com.novel.ui.theme.NovelColors
import com.novel.utils.NavViewModel
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlinx.coroutines.launch

/**
 * 搜索结果页面
 */
@Composable
fun SearchResultPage(
    initialQuery: String = "",
    viewModel: SearchResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle(initialValue = null)
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 处理事件
    LaunchedEffect(events) {
        events?.let { event ->
            when (event) {
                is SearchResultEvent.NavigateToDetail -> {
                    NavViewModel.navigateToBookDetail(event.bookId, fromRank = false)
                }
                is SearchResultEvent.NavigateBack -> {
                    NavViewModel.navigateBack()
                }
            }
        }
    }

    // 初始化查询
    LaunchedEffect(initialQuery) {
        if (initialQuery.isNotEmpty() && uiState.data.query.isEmpty()) {
            viewModel.onAction(SearchResultAction.UpdateQuery(initialQuery))
            viewModel.onAction(SearchResultAction.PerformSearch(initialQuery))
        }
    }

    // 监听滚动到底部，触发加载更多
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val totalItemsNumber = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

                if (lastVisibleItemIndex > (totalItemsNumber - 3) && 
                    uiState.data.hasMore && 
                    !uiState.isLoading) {
                    viewModel.onAction(SearchResultAction.LoadNextPage)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 顶部搜索栏
        SearchTopBar(
            query = uiState.data.query,
            onQueryChange = { query ->
                viewModel.onAction(SearchResultAction.UpdateQuery(query))
            },
            onSearchClick = {
                viewModel.onAction(SearchResultAction.PerformSearch(uiState.data.query))
            },
            onBackClick = {
                viewModel.onAction(SearchResultAction.NavigateBack)
            }
        )

        // 分类筛选 + 筛选按钮
        CategoryFilterRow(
            categories = uiState.data.categoryFilters,
            selectedCategoryId = uiState.data.selectedCategoryId,
            onCategorySelected = { categoryId ->
                viewModel.onAction(SearchResultAction.SelectCategory(categoryId))
            },
            onFilterClick = {
                viewModel.onAction(SearchResultAction.OpenFilterSheet)
            }
        )

        // 结果列表
        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isLoading && uiState.data.books.isEmpty()) {
                // 显示加载状态
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = NovelColors.NovelMain,
                        modifier = Modifier.size(32.wdp)
                    )
                }
            } else {
                if (uiState.data.books.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.wdp)
                    ) {
                        items(
                            items = uiState.data.books,
                            key = { it.id }
                        ) { book ->
                            SearchResultItem(
                                book = book,
                                onClick = {
                                    viewModel.onAction(SearchResultAction.NavigateToDetail(book.id.toString()))
                                }
                            )
                        }

                        // 加载更多指示器
                        if (uiState.data.hasMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.wdp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            color = NovelColors.NovelMain,
                                            modifier = Modifier.size(24.wdp)
                                        )
                                    } else {
                                        NovelText(
                                            text = "加载更多...",
                                            fontSize = 14.ssp,
                                            color = NovelColors.NovelTextGray,
                                            modifier = Modifier.debounceClickable(
                                                onClick =  {
                                                    viewModel.onAction(SearchResultAction.LoadNextPage)
                                                }
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else if (!uiState.isLoading && uiState.data.isEmpty) {
                    // 空状态
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            NovelText(
                                text = "🔍",
                                fontSize = 48.ssp,
                                color = NovelColors.NovelTextGray
                            )
                            Spacer(modifier = Modifier.height(16.wdp))
                            NovelText(
                                text = "没有找到相关结果",
                                fontSize = 16.ssp,
                                color = NovelColors.NovelTextGray
                            )
                            Spacer(modifier = Modifier.height(8.wdp))
                            NovelText(
                                text = "试试其他关键词或调整筛选条件",
                                fontSize = 14.ssp,
                                color = NovelColors.NovelTextGray.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

    // 筛选弹窗
    if (uiState.data.isFilterSheetOpen) {
        SearchFilterBottomSheet(
            filters = uiState.data.filters,
            onFiltersChange = { filters ->
                viewModel.onAction(SearchResultAction.UpdateFilters(filters))
            },
            onDismiss = {
                viewModel.onAction(SearchResultAction.CloseFilterSheet)
            },
            onClear = {
                viewModel.onAction(SearchResultAction.ClearFilters)
            },
            onApply = {
                viewModel.onAction(SearchResultAction.ApplyFilters)
            }
        )
    }
}

/**
 * 分类筛选行
 */
@Composable
private fun CategoryFilterRow(
    categories: List<CategoryFilter>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int?) -> Unit,
    onFilterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.wdp, vertical = 8.wdp),
    ) {
        // 分类标签
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.wdp)
        ) {
            Log.d("CategoryFilterRow", "categories: $categories")
            categories.forEach { category ->
                SearchFilterChip(
                    text = category.name ?: "未知分类",
                    selected = selectedCategoryId == category.id || (selectedCategoryId == null && category.id == -1),
                    onClick = {
                        val targetId = if (category.id == -1) null else category.id
                        onCategorySelected(targetId)
                    }
                )
            }
        }
        // 筛选按钮
//        IconButton(
//            onClick = onFilterClick,
//            modifier = Modifier.size(40.wdp).align(Alignment.CenterEnd)
//        ) {
//            Icon(
//                Icons.Default.MoreVert,
//                contentDescription = "筛选",
//                tint = NovelColors.NovelMain,
//                modifier = Modifier.size(20.wdp)
//            )
//        }
    }
}
