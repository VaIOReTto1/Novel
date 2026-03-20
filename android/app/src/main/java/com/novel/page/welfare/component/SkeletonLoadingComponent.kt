package com.novel.page.welfare.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 骨架屏加载组件
 * 
 * 功能特点：
 * - 模拟真实内容布局的骨架屏效果
 * - 流畅的闪烁动画，提升用户体验
 * - 主题适配，支持深色和浅色模式
 * - 可配置的骨架屏样式
 * 
 * 用户体验优化：
 * - 减少用户等待时的焦虑感
 * - 提供内容即将加载的预期
 * - 平滑的动画过渡效果
 */
@Composable
fun SkeletonLoadingComponent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true
) {
    if (!isLoading) return
    
    // 闪烁动画
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 模拟标题区域
        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(24.dp)
        )
        
        // 模拟副标题
        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(16.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 模拟内容区域
        repeat(6) {
            SkeletonItem(
                brush = brush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 模拟图片区域
        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cornerRadius = 8.dp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 模拟更多内容
        repeat(4) {
            SkeletonItem(
                brush = brush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
        }
    }
}

/**
 * 骨架屏单个项目组件
 */
@Composable
private fun SkeletonItem(
    brush: Brush,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 4.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

/**
 * 简化版骨架屏加载组件
 * 用于较小的加载区域
 */
@Composable
fun SimpleSkeletonLoading(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    itemCount: Int = 3
) {
    if (!isLoading) return
    
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition(label = "simple_shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "simple_shimmer_translate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(itemCount) {
            SkeletonItem(
                brush = brush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
        }
    }
}

/**
 * 卡片式骨架屏组件
 * 用于模拟卡片布局的加载状态
 */
@Composable
fun CardSkeletonLoading(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true
) {
    if (!isLoading) return
    
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition(label = "card_shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "card_shimmer_translate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 头部区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            SkeletonItem(
                brush = brush,
                modifier = Modifier.size(40.dp),
                cornerRadius = 20.dp
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 标题
                SkeletonItem(
                    brush = brush,
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                )
                
                // 副标题
                SkeletonItem(
                    brush = brush,
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                )
            }
        }
        
        // 内容区域
        repeat(3) {
            SkeletonItem(
                brush = brush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
        }
        
        // 图片区域
        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            cornerRadius = 8.dp
        )
    }
}
