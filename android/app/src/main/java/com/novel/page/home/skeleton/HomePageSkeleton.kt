package com.novel.page.home.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.novel.ui.theme.NovelColors
import com.novel.ui.theme.NovelTheme
import com.novel.utils.wdp
import com.valentinilk.shimmer.shimmer

/**
 * 首页骨架屏
 */
@Composable
fun HomePageSkeleton() {
    val skeletonColor = NovelColors.NovelTextGray.copy(alpha = 0.2f)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NovelColors.NovelBackground)
            .shimmer(),
        contentPadding = PaddingValues(vertical = 10.wdp),
        verticalArrangement = Arrangement.spacedBy(10.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部搜索栏骨架
        item(key = "top_bar_skeleton") {
            HomeTopBarSkeleton(skeletonColor)
        }
        
        // 分类筛选器骨架
        item(key = "filter_bar_skeleton") {
            HomeFilterBarSkeleton(skeletonColor)
        }
        
        // 榜单面板骨架
        item(key = "rank_panel_skeleton") {
            HomeRankPanelSkeleton(skeletonColor)
        }
        
        // 推荐书籍网格骨架
        item(key = "recommend_grid_skeleton") {
            HomeRecommendGridSkeleton(skeletonColor)
        }
    }
}

@Composable
private fun HomeTopBarSkeleton(skeletonColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.wdp)
            .height(48.wdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.wdp)
    ) {
        // 搜索框骨架
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.wdp)
                .clip(RoundedCornerShape(20.wdp))
                .background(skeletonColor)
        )
        
        // 分类按钮骨架
        Box(
            modifier = Modifier
                .width(60.wdp)
                .height(36.wdp)
                .clip(RoundedCornerShape(18.wdp))
                .background(skeletonColor)
        )
    }
}

@Composable
private fun HomeFilterBarSkeleton(skeletonColor: Color) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 15.wdp),
        horizontalArrangement = Arrangement.spacedBy(8.wdp)
    ) {
        items(6) { index ->
            Box(
                modifier = Modifier
                    .width((50 + index * 10).wdp) // 不同宽度的标签
                    .height(32.wdp)
                    .clip(RoundedCornerShape(16.wdp))
                    .background(skeletonColor)
            )
        }
    }
}

@Composable
private fun HomeRankPanelSkeleton(skeletonColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.wdp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.wdp))
                .background(NovelColors.NovelBookBackground)
                .padding(16.wdp)
        ) {
            // 榜单标题骨架
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(60.wdp)
                            .height(20.wdp)
                            .clip(RoundedCornerShape(4.wdp))
                            .background(skeletonColor)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.wdp))
            
            // 榜单书籍列表骨架
            repeat(5) { index ->
                RankBookItemSkeleton(skeletonColor)
                if (index < 4) {
                    Spacer(modifier = Modifier.height(12.wdp))
                }
            }
        }
    }
}

@Composable
private fun RankBookItemSkeleton(skeletonColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.wdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名骨架
        Box(
            modifier = Modifier
                .size(24.wdp)
                .background(skeletonColor, CircleShape)
        )
        
        // 书籍封面骨架
        Box(
            modifier = Modifier
                .width(40.wdp)
                .height(56.wdp)
                .clip(RoundedCornerShape(4.wdp))
                .background(skeletonColor)
        )
        
        // 书籍信息骨架
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.wdp)
        ) {
            // 书名骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
            
            // 作者骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
        }
    }
}

@Composable
private fun HomeRecommendGridSkeleton(skeletonColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NovelColors.NovelBookBackground)
            .padding(16.wdp)
    ) {
        // 网格布局骨架 - 2列
        repeat(3) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.wdp)
            ) {
                repeat(2) {
                    RecommendBookItemSkeleton(
                        skeletonColor = skeletonColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (row < 2) {
                Spacer(modifier = Modifier.height(16.wdp))
            }
        }
    }
}

@Composable
private fun RecommendBookItemSkeleton(
    skeletonColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 书籍封面骨架
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f) // 书籍封面比例
                .clip(RoundedCornerShape(8.wdp))
                .background(skeletonColor)
        )
        
        Spacer(modifier = Modifier.height(8.wdp))
        
        // 书名骨架
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(16.wdp)
                .clip(RoundedCornerShape(2.wdp))
                .background(skeletonColor)
        )
        
        Spacer(modifier = Modifier.height(4.wdp))
        
        // 作者骨架
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(12.wdp)
                .clip(RoundedCornerShape(2.wdp))
                .background(skeletonColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePageSkeletonPreview() {
    NovelTheme {
        HomePageSkeleton()
    }
} 
