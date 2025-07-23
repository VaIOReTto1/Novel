package com.novel.page.home.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.novel.page.component.NovelText
import com.novel.page.component.NovelImageView
import com.novel.page.component.ImageLoadingStrategy
import com.novel.page.component.FlipBookAnimationController
import com.novel.page.home.dao.HomeRepository
import com.novel.page.home.viewmodel.CategoryInfo
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.network.api.front.BookService
import com.novel.utils.wdp
import com.novel.utils.ssp
import kotlinx.coroutines.launch
import com.novel.page.component.rememberBookClickHandler
import androidx.compose.ui.graphics.Color
import com.novel.page.component.RankingNumber
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 首页榜单面板组件 - 连续滚动但带精确距离控制，支持3D翻书动画
 */
@Composable
fun HomeRankPanel(
    rankBooks: ImmutableList<BookService.BookRank>,
    selectedRankType: String,
    onRankTypeSelected: (String) -> Unit,
    onBookClick: (Long, Offset, Size) -> Unit,
    modifier: Modifier = Modifier,
    // 翻书动画控制器
    flipBookController: FlipBookAnimationController? = null
) {
    // 静态常量列表，无需 remember 包装
    val rankTypes = kotlinx.collections.immutable.persistentListOf(
        CategoryInfo(HomeRepository.RANK_TYPE_VISIT, "点击榜"),
        CategoryInfo(HomeRepository.RANK_TYPE_UPDATE, "更新榜"),
        CategoryInfo(HomeRepository.RANK_TYPE_NEWEST, "新书榜")
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(370.wdp),
        shape = RoundedCornerShape(8.wdp),
        colors = CardDefaults.cardColors(
            containerColor = NovelColors.NovelBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.wdp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 榜单类型筛选器
            RankFilterBar(
                rankTypes = rankTypes,
                selectedRankType = selectedRankType,
                onRankTypeSelected = onRankTypeSelected,
                modifier = Modifier.padding(top = 10.wdp)
            )

            // 榜单书籍连续滚动列表 - 最多16本书，每列4本
            // 优化：预计算限制后的书籍列表
            val limitedBooks by remember(rankBooks) {
                derivedStateOf {
                    rankBooks.take(16).toImmutableList()
                }
            }

            if (limitedBooks.isNotEmpty()) {
                RankBooksScrollableGrid(
                    books = limitedBooks,
                    selectedRankType = selectedRankType,
                    onBookClick = onBookClick,
                    flipBookController = flipBookController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.wdp)
                )
            }
        }
    }
}

/**
 * 榜单书籍可滚动网格 - 精确距离控制，支持翻书动画
 */
@SuppressLint("RememberReturnType")
@Composable
private fun RankBooksScrollableGrid(
    books: ImmutableList<BookService.BookRank>,
    selectedRankType: String,
    onBookClick: (Long, Offset, Size) -> Unit,
    modifier: Modifier = Modifier,
    flipBookController: FlipBookAnimationController? = null
) {
    // 使用 derivedStateOf 优化书籍分组计算，仅在 books 变化时重新计算
    val bookColumns by remember(books) { derivedStateOf { books.chunked(4).map { it.toImmutableList() } } }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val snapFlingBehavior = rememberSnapFlingBehavior(listState)

    // 监听筛选器变化，重置滚动位置
    LaunchedEffect(selectedRankType) {
        coroutineScope.launch {
            listState.animateScrollToItem(0, 0)
        }
    }

    LazyRow(
        state = listState,
        flingBehavior = snapFlingBehavior,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.wdp),
        contentPadding = PaddingValues(horizontal = 4.wdp)
    ) {
        items(
            count = bookColumns.size,
            key = { columnIndex -> "column_$columnIndex" } // 优化：添加稳定的key
        ) { columnIndex ->
            // 优化：预计算列宽
            val columnWidth by remember(columnIndex, bookColumns.size) {
                derivedStateOf {
                    if (columnIndex != bookColumns.size - 1) 200.wdp else 312.wdp
                }
            }

            RankBookColumn(
                books = bookColumns[columnIndex],
                startRank = columnIndex * 4 + 1,
                onBookClick = onBookClick,
                flipBookController = flipBookController,
                modifier = Modifier.width(columnWidth)
            )
        }
    }
}

/**
 * 榜单书籍列 - 每列显示4本书，支持翻书动画
 */
@Composable
private fun RankBookColumn(
    books: ImmutableList<BookService.BookRank>,
    startRank: Int,
    onBookClick: (Long, Offset, Size) -> Unit,
    modifier: Modifier = Modifier,
    flipBookController: FlipBookAnimationController? = null
) {
    Column(
        modifier = modifier.padding(vertical = 8.wdp),
        verticalArrangement = Arrangement.spacedBy(8.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        books.forEachIndexed { index, book ->
            // 优化：使用derivedStateOf计算排名，避免重复计算
            val rankNumber by remember(startRank, index) {
                derivedStateOf { startRank + index }
            }

            // 优化：使用稳定的key避免重组
            key(book.id) {
                RankBookGridItem(
                    book = book,
                    rank = rankNumber,
                    onClick = onBookClick,
                    flipBookController = flipBookController,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 榜单筛选器 - 与HomeFilterBar相同的UI风格
 */
@Composable
private fun RankFilterBar(
    rankTypes: ImmutableList<CategoryInfo>,
    selectedRankType: String,
    onRankTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        rankTypes.forEach { rankType ->
            // 优化：使用稳定的key
            key(rankType.name) {
                // 性能优化：缓存点击回调，避免每次重组都创建 Lambda
                val onChipClick = remember(rankType.name, onRankTypeSelected) {
                    {
                        onRankTypeSelected(rankType.name)
                    }
                }

                RankFilterChip(
                    filter = rankType.name,
                    isSelected = rankType.name == selectedRankType,
                    onClick = onChipClick
                )
            }
        }
    }
}

/**
 * 榜单筛选按钮 - 与FilterChip相同的UI
 */
@Composable
private fun RankFilterChip(
    filter: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NovelText(
        text = filter,
        fontSize = if (isSelected) 16.ssp else 14.ssp,
        modifier = Modifier
            .debounceClickable(onClick = onClick)
            .padding(vertical = 8.wdp, horizontal = 12.wdp),
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) NovelColors.NovelText else NovelColors.NovelTextGray
    )
}

/**
 * 榜单书籍网格项 - 垂直布局适应网格，支持翻书动画
 */
@Composable
private fun RankBookGridItem(
    book: BookService.BookRank,
    rank: Int,
    onClick: (Long, Offset, Size) -> Unit,
    modifier: Modifier = Modifier,
    flipBookController: FlipBookAnimationController? = null
) {
    // 优化：使用单个状态存储位置和尺寸信息
    var positionInfo by remember {
        mutableStateOf(Pair(Offset.Zero, Size.Zero))
    }

    // 创建翻书动画点击处理器
    if (flipBookController != null) {
        rememberBookClickHandler(
            controller = flipBookController,
            bookId = book.id.toString(),
            imageUrl = book.picUrl,
            position = positionInfo.first,
            size = positionInfo.second
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .debounceClickable(onClick = {
                // 先触发翻书动画，然后执行原有的点击逻辑
//                bookClickHandler?.invoke()
                onClick(
                    book.id,
                    positionInfo.first,
                    positionInfo.second
                )
            })
            .padding(2.wdp),
        verticalAlignment = Alignment.Top
    ) {
        // 书籍封面 - 精确追踪位置和尺寸
        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    // 优化：减少频繁的位置更新，只在显著变化时更新
                    val windowRect = coordinates.boundsInWindow()
                    val newPosition = Offset(windowRect.left, windowRect.top)
                    val newSize = Size(
                        coordinates.size.width.toFloat(),
                        coordinates.size.height.toFloat()
                    )

                    val currentInfo = positionInfo
                    if ((newPosition - currentInfo.first).getDistanceSquared() > 1f ||
                        kotlin.math.abs(newSize.width - currentInfo.second.width) > 1f ||
                        kotlin.math.abs(newSize.height - currentInfo.second.height) > 1f
                    ) {

                        positionInfo = newPosition to newSize
                    }
                }
        ) {
            BookCoverImage(book = book, flipBookController = flipBookController)
        }

        Spacer(modifier = Modifier.width(4.wdp))

        // 排名数字
        RankingNumber(
            rank = rank,
            fontSize = 16.ssp
        )

        Spacer(modifier = Modifier.width(4.wdp))

        // 书籍信息 - 垂直布局
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            // 书名
            NovelText(
                text = book.bookName,
                fontSize = 14.ssp,
                fontWeight = FontWeight.Medium,
                color = NovelColors.NovelText,
                lineHeight = 15.ssp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(1.wdp))

            // 分类
            NovelText(
                text = book.categoryName,
                fontSize = 12.ssp,
                lineHeight = 13.ssp,
                color = NovelColors.NovelMain,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * 书籍封面图片组件 - 支持共享元素动画，在动画进行时隐藏原始图片
 */
@Composable
private fun BookCoverImage(
    book: BookService.BookRank,
    flipBookController: FlipBookAnimationController? = null
) {
    // 优化：使用derivedStateOf检查是否当前书籍正在进行动画，减少重复计算
    val isCurrentBookAnimating by remember {
        derivedStateOf {
            flipBookController?.animationState?.let { animState ->
                animState.isAnimating &&
                        animState.hideOriginalImage &&
                        animState.bookId == book.id.toString()
            } == true
        }
    }

    Box(
        modifier = Modifier
            .width(50.wdp)
            .height(65.wdp)
            .clip(RoundedCornerShape(4.wdp))
            .background(if (isCurrentBookAnimating) Color.Transparent else NovelColors.NovelMain)
    ) {
        if (!isCurrentBookAnimating) {
            // 正常状态：显示图片 - 榜单高性能模式
            NovelImageView(
                imageUrl = book.picUrl,
                loadingStrategy = ImageLoadingStrategy.HIGH_PERFORMANCE,
                useAdvancedCache = true,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholderContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NovelColors.NovelMain),
                        contentAlignment = Alignment.Center
                    ) {
                        NovelText(
                            text = "暂无封面",
                            fontSize = 6.ssp,
                            color = Color.Gray
                        )
                    }
                }
            )
        }
        // 优化：移除动画状态的log输出
    }
}
