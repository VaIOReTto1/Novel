package com.novel.page.search.skeleton

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
 * 搜索页面骨架屏
 */
@Composable
fun SearchPageSkeleton() {
    val skeletonColor = NovelColors.NovelTextGray.copy(alpha = 0.2f)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NovelColors.NovelBackground)
            .shimmer(),
        contentPadding = PaddingValues(vertical = 10.wdp),
        verticalArrangement = Arrangement.spacedBy(16.wdp)
    ) {
        // 顶部搜索栏骨架
        item {
            SearchTopBarSkeleton(skeletonColor)
        }

        // 主要内容区域骨架
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.wdp),
                verticalArrangement = Arrangement.spacedBy(24.wdp)
            ) {
                // 搜索历史记录骨架
                SearchHistorySkeleton(skeletonColor)
                
                // 推荐榜单区域骨架
                RankingSectionSkeleton(skeletonColor)
            }
        }
    }
}

@Composable
private fun SearchTopBarSkeleton(skeletonColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.wdp)
            .background(Color.White)
            .padding(horizontal = 16.wdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.wdp)
    ) {
        // 返回按钮骨架
        Box(
            modifier = Modifier
                .size(24.wdp)
                .background(skeletonColor, CircleShape)
        )
        
        // 搜索框骨架
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.wdp)
                .clip(RoundedCornerShape(20.wdp))
                .background(skeletonColor)
        )
        
        // 搜索按钮骨架
        Box(
            modifier = Modifier
                .width(48.wdp)
                .height(32.wdp)
                .clip(RoundedCornerShape(16.wdp))
                .background(skeletonColor)
        )
    }
}

@Composable
private fun SearchHistorySkeleton(skeletonColor: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.wdp)
    ) {
        // 标题骨架
        Box(
            modifier = Modifier
                .width(80.wdp)
                .height(20.wdp)
                .clip(RoundedCornerShape(4.wdp))
                .background(skeletonColor)
        )
        
        // 历史记录标签骨架
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.wdp)
        ) {
            items(5) { index ->
                Box(
                    modifier = Modifier
                        .width((60 + index * 15).wdp) // 不同宽度
                        .height(32.wdp)
                        .clip(RoundedCornerShape(16.wdp))
                        .background(skeletonColor)
                )
            }
        }
    }
}

@Composable
private fun RankingSectionSkeleton(skeletonColor: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.wdp)
    ) {
        // 榜单区域 - 重复三个榜单
        repeat(3) {
            RankingListSkeleton(
                skeletonColor = skeletonColor
            )
        }
    }
}

@Composable
private fun RankingListSkeleton(skeletonColor: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.wdp)
    ) {
        // 榜单标题和更多按钮骨架
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(80.wdp)
                    .height(20.wdp)
                    .clip(RoundedCornerShape(4.wdp))
                    .background(skeletonColor)
            )
            
            Box(
                modifier = Modifier
                    .width(40.wdp)
                    .height(16.wdp)
                    .clip(RoundedCornerShape(4.wdp))
                    .background(skeletonColor)
            )
        }
        
        // 榜单书籍列表骨架
        repeat(5) {
            RankingBookItemSkeleton(skeletonColor)
        }
    }
}

@Composable
private fun RankingBookItemSkeleton(skeletonColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.wdp),
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
                .width(45.wdp)
                .height(60.wdp)
                .clip(RoundedCornerShape(4.wdp))
                .background(skeletonColor)
        )
        
        // 书籍信息骨架
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.wdp)
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
            
            // 简介骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(12.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
        }
        
        // 更多按钮骨架
        Box(
            modifier = Modifier
                .size(24.wdp)
                .background(skeletonColor, CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPageSkeletonPreview() {
    NovelTheme {
        SearchPageSkeleton()
    }
} 
