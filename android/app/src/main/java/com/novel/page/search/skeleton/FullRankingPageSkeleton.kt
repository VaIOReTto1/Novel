package com.novel.page.search.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.novel.ui.theme.NovelDesignTokens
import com.novel.ui.theme.NovelTheme
import com.novel.utils.wdp
import com.valentinilk.shimmer.shimmer

/**
 * 完整排行榜页面骨架屏
 */
@Composable
fun FullRankingPageSkeleton() {
    val skeletonColor = NovelDesignTokens.lightColor("color.text.secondary").copy(alpha = 0.2f)
    LocalDensity.current
    val expandedHeight = 180.dp
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NovelDesignTokens.lightColor("color.bg.surface"))
            .shimmer()
    ) {
        // 顶部折叠栏骨架
        FullRankingTopBarSkeleton(skeletonColor, expandedHeight)
        
        // 榜单列表骨架
        FullRankingListSkeleton(skeletonColor)
    }
}

@Composable
private fun FullRankingTopBarSkeleton(
    skeletonColor: Color,
    expandedHeight: androidx.compose.ui.unit.Dp
) {
    val density = LocalDensity.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(expandedHeight)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFDF8F6), Color.White),
                    startY = 0f,
                    endY = with(density) { expandedHeight.toPx() }
                )
            )
            .statusBarsPadding()
    ) {
        // 返回按钮骨架
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.wdp, top = 12.wdp)
                .size(24.wdp)
                .background(skeletonColor, CircleShape)
        )
        
        // 主标题骨架
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(100.wdp)
                .height(24.wdp)
                .clip(RoundedCornerShape(4.wdp))
                .background(skeletonColor)
        )
        
        // 副标题骨架
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 40.wdp)
                .width(180.wdp)
                .height(14.wdp)
                .clip(RoundedCornerShape(4.wdp))
                .background(skeletonColor)
        )
    }
}

@Composable
private fun FullRankingListSkeleton(skeletonColor: Color) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(vertical = 8.wdp)
    ) {
        items(20) { index -> // 显示20个骨架项目
            FullRankingItemSkeleton(skeletonColor, index + 1)
        }
    }
}

@Composable
private fun FullRankingItemSkeleton(skeletonColor: Color, rank: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.wdp, vertical = 12.wdp),
        horizontalArrangement = Arrangement.spacedBy(12.wdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名骨架
        Box(
            modifier = Modifier
                .size(32.wdp)
                .background(
                    // 前三名用特殊颜色
                    when (rank) {
                        1 -> skeletonColor.copy(alpha = 0.4f)
                        2 -> skeletonColor.copy(alpha = 0.35f)
                        3 -> skeletonColor.copy(alpha = 0.3f)
                        else -> skeletonColor
                    },
                    CircleShape
                )
        )
        
        // 书籍封面骨架
        Box(
            modifier = Modifier
                .width(60.wdp)
                .height(80.wdp)
                .clip(RoundedCornerShape(6.wdp))
                .background(skeletonColor)
        )
        
        // 书籍信息骨架
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.wdp)
        ) {
            // 书名骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(18.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
            
            // 作者骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(14.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
            
            // 简介骨架（两行）
            repeat(2) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .let { if (index == 1) it.fillMaxWidth(0.75f) else it }
                        .height(12.wdp)
                        .clip(RoundedCornerShape(2.wdp))
                        .background(skeletonColor)
                )
            }
            
            // 标签和统计信息骨架
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.wdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分类标签骨架
                Box(
                    modifier = Modifier
                        .width(50.wdp)
                        .height(20.wdp)
                        .clip(RoundedCornerShape(10.wdp))
                        .background(skeletonColor)
                )
                
                // 字数骨架
                Box(
                    modifier = Modifier
                        .width(60.wdp)
                        .height(12.wdp)
                        .clip(RoundedCornerShape(2.wdp))
                        .background(skeletonColor)
                )
                
                // 状态骨架
                Box(
                    modifier = Modifier
                        .width(35.wdp)
                        .height(12.wdp)
                        .clip(RoundedCornerShape(2.wdp))
                        .background(skeletonColor)
                )
            }
        }
        
        // 热度指示器骨架
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.wdp)
        ) {
            Box(
                modifier = Modifier
                    .width(30.wdp)
                    .height(14.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
            
            Box(
                modifier = Modifier
                    .width(20.wdp)
                    .height(10.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FullRankingPageSkeletonPreview() {
    NovelTheme {
        FullRankingPageSkeleton()
    }
} 
