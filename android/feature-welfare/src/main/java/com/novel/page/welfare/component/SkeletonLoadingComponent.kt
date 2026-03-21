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
import androidx.compose.ui.unit.dp

@Composable
fun SkeletonLoadingComponent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = true
) {
    if (!isLoading) return

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
        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(24.dp)
        )

        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        repeat(6) {
            SkeletonItem(
                brush = brush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cornerRadius = 8.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonItem(
                brush = brush,
                modifier = Modifier.size(40.dp),
                cornerRadius = 20.dp
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SkeletonItem(
                    brush = brush,
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                )

                SkeletonItem(
                    brush = brush,
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                )
            }
        }

        repeat(3) {
            SkeletonItem(
                brush = brush,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
        }

        SkeletonItem(
            brush = brush,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            cornerRadius = 8.dp
        )
    }
}
