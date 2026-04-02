package com.novel.page.search

import android.annotation.SuppressLint
import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.novel.page.component.NovelText
import com.novel.page.component.RankingNumber
import com.novel.page.search.component.SearchRankingItem
import com.novel.page.search.skeleton.FullRankingPageSkeleton
import com.novel.ui.theme.NovelDesignTokens
import com.novel.utils.debounceClickable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 全排行榜页面 - 性能优化版
 * 
 * 实现功能：
 * - 丝滑的上滑折叠效果：标题保留，副标题淡出
 * - 顶栏尺寸固定，使用 graphicsLayer 位移 + clip 避免每帧重新测量
 * - 使用缓动函数提供自然的动画过渡
 * - 优化的列表渲染，使用稳定key提升性能
 * 
 * @param rankingType 排行榜类型名称
 * @param rankingItems 排行榜数据列表
 * @param onNavigateBack 返回导航回调
 * @param onNavigateToBookDetail 跳转书籍详情回调
 */
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullRankingPage(
    rankingType: String,
    rankingItems: List<SearchRankingItem>,
    onNavigateBack: () -> Unit,
    onNavigateToBookDetail: (Long) -> Unit
) {
    val TAG = "FullRankingPage"
    TimberLogger.d(TAG, "渲染全排行榜页面: $rankingType, 条目数量: ${rankingItems.size}")
    
    /* ---------- ScrollBehavior ---------- */
    val toolbarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(toolbarState)

    /* ---------- 尺寸常量 - 使用remember避免重复计算 ---------- */
    val expandedHeight = 180.dp      // 顶栏物理尺寸固定不变
    val collapsedHeight = 56.dp
    val density = LocalDensity.current

    // 差值（像素）——用来位移整块顶栏
    val diffPx by remember(density) {
        derivedStateOf {
            val diff = with(density) { (expandedHeight - collapsedHeight).toPx() }
            TimberLogger.v(TAG, "计算顶栏高度差: ${diff}px")
            diff
        }
    }

    // 内部元素偏移量 - 优化动画曲线
    val titleShiftYPx by remember(diffPx) { derivedStateOf { diffPx * 0.4f } }  // 主标题需向下平移
    val subtitleStartYPx by remember(density) { derivedStateOf { with(density) { 24.dp.toPx() } } }

    // 告诉 TopAppBarState 能收缩多少像素（负值）
    LaunchedEffect(diffPx) {
        TimberLogger.d(TAG, "设置顶栏高度偏移限制: ${-diffPx}px")
        toolbarState.heightOffsetLimit = -diffPx
    }

    // 0f(展开) → 1f(折叠) - 使用平滑的插值
    val progress by derivedStateOf {
        val limit = toolbarState.heightOffsetLimit
        if (limit == 0f) 0f else {
            val rawProgress = -toolbarState.heightOffset / limit
            // 使用缓动函数让动画更自然
            easeInOutCubic(rawProgress.coerceIn(0f, 1f))
        }
    }

    // 当前日期格式化 - 简单计算，无需 remember
    val currentDate = SimpleDateFormat("yyyy年M月d日", Locale.getDefault()).format(Date())

    // 预计算动画值，避免在重组中计算
    val titleTranslationY by remember(progress, titleShiftYPx) { derivedStateOf { titleShiftYPx * progress } }
    val subtitleTranslationY by remember(progress, subtitleStartYPx, titleShiftYPx) { 
        derivedStateOf { subtitleStartYPx - titleShiftYPx * progress }
    }
    val subtitleAlpha by remember(progress) { 
        derivedStateOf {
        // 副标题提前淡出，让过渡更自然
        (1f - progress * 1.2f).coerceAtLeast(0f) 
    }
    }
    val topBarTranslationY by remember(progress, diffPx) { derivedStateOf { -diffPx * progress } }

    /* ---------- UI ---------- */
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            /**
             * 顶栏容器保持 180dp，高度不再变化；
             * 通过 translationY 把整块 content 往上推，clip=true 截掉多余部分，
             * 视觉上就只剩 56dp。
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(expandedHeight)
                    .zIndex(10f) // 确保顶栏在最上层
                    .graphicsLayer {
                        translationY = topBarTranslationY   // 整块上移
                        clip = true                         // 裁剪到自身边界
                    }
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFFDF8F6), Color.White),
                            startY = 0f,
                            endY = with(density) { expandedHeight.toPx() }
                        )
                    )
                    .statusBarsPadding()
            ) {
                /* 返回按钮 - 固定位置不动 */
                IconButton(
                    onClick = {
                        TimberLogger.d(TAG, "点击返回按钮")
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = NovelDesignTokens.lightColor("color.text.primary")
                    )
                }

                /* 主标题——随 progress 向下平移进入可见区域 */
                NovelText(
                    text = rankingType,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                        color = NovelDesignTokens.lightColor("color.text.primary"),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            translationY = titleTranslationY
                        }
                )

                /* 副标题——向上移动并淡出，提供平滑过渡 */
                NovelText(
                    text = "根据真实搜索更新 ($currentDate)",
                    fontSize = 12.sp,
                        color = NovelDesignTokens.lightColor("color.text.secondary"),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            translationY = subtitleTranslationY
                            alpha = subtitleAlpha
                            // 添加轻微的缩放效果，增强过渡感
                            scaleX = 1f - progress * 0.1f
                            scaleY = 1f - progress * 0.1f
                        }
                )
            }
        }
    ) { innerPadding ->
        if (rankingItems.isEmpty()) {
            /* 显示骨架屏或空状态 */
            TimberLogger.w(TAG, "排行榜数据为空，显示骨架屏")
            FullRankingPageSkeleton()
        } else {
            /* 榜单列表 - 性能优化 */
            TimberLogger.d(TAG, "渲染排行榜列表，条目数量: ${rankingItems.size}")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection) // 让列表把剩余 delta 继续上传
                        .background(NovelDesignTokens.lightColor("color.bg.surface")),
                contentPadding = innerPadding,
            ) {
                items(
                    items = rankingItems, 
                    key = { it.id } // 稳定的key确保重组性能
                ) { item ->
                    // 性能优化：使用 remember 缓存点击回调，避免每次重组都创建新 Lambda
                    val onItemClick = remember(item.id, onNavigateToBookDetail) { {
                        TimberLogger.d(TAG, "点击榜单条目: ${item.title} (ID: ${item.id})")
                        onNavigateToBookDetail(item.id) 
                    } }
                    
                    FullRankingItem(
                        item = item, 
                        onClick = onItemClick
                    )
                }
                // 底部间距
                item(key = "bottom_spacer") { 
                    Spacer(modifier = Modifier.height(16.dp)) 
                }
            }
        }
    }
}

/**
 * 缓动函数：立方缓入缓出，让动画更自然
 * 
 * @param t 输入值 [0,1]
 * @return 缓动后的值 [0,1]
 */
private fun easeInOutCubic(t: Float): Float {
    return if (t < 0.5f) {
        4f * t * t * t
    } else {
        1f - ((-2f * t + 2f) * (-2f * t + 2f) * (-2f * t + 2f)) / 2f
    }
}

/**
 * 优化的排行榜条目组件
 * 
 * 使用remember缓存计算结果，避免重复渲染
 * 
 * @param item 排行榜条目数据
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
private fun FullRankingItem(
    item: SearchRankingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 性能优化：使用 remember + derivedStateOf 缓存计算结果，避免每次重组都计算
    val hotSearchValue by remember(item.rank) {
        derivedStateOf {
            "${(30 - item.rank) * 1000 + kotlin.random.Random.nextInt(500)}热搜"
        }
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .debounceClickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RankingNumber(rank = item.rank)
        
        Column(modifier = Modifier.weight(1f)) {
            NovelText(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                    color = NovelDesignTokens.lightColor("color.text.primary"),
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            NovelText(
                text = item.author,
                fontSize = 13.sp,
                    color = NovelDesignTokens.lightColor("color.text.secondary"),
                maxLines = 1
            )
        }
        
        NovelText(
            text = hotSearchValue,
            fontSize = 12.sp,
                color = NovelDesignTokens.lightColor("color.text.secondary").copy(alpha = 0.7f)
        )
    }
}
