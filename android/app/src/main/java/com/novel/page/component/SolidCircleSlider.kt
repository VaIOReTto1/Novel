package com.novel.page.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 去除左右空白、让 Track 充满宽度的版本：
 *
 * @param progress        当前进度（0f..1f）
 * @param onValueChange   拖动过程中（或结束时）回调新的 progress
 * @param modifier        外部传入的 Modifier，通常会指定宽度、高度
 * @param trackColor      整条 Track（背景）的颜色
 * @param progressColor   进度部分的颜色
 * @param thumbColor      拖拽圆点的颜色
 * @param trackHeightDp   Track（圆角矩形）的高度
 * @param thumbRadiusDp   Thumb 圆的半径
 */
@Composable
fun SolidCircleSlider(
    progress: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xFFCCCCCC),
    progressColor: Color = Color(0xFF888888),
    thumbColor: Color = Color(0xFFF0EAD6),
    trackHeightDp: Dp = 10.dp,
    thumbRadiusDp: Dp = 18.dp
) {
    // 1. 把进度限制在 0f..1f
    val progressClamped = progress.coerceIn(0f, 1f)

    // 2. 先记录整个组件的“像素宽度” fullWidthPx
    var fullWidthPx by remember { mutableFloatStateOf(0f) }

    // 3. Thumb 的圆心在 x 方向的真实位置（像素值），它要介于 [thumbRadiusPx, fullWidthPx - thumbRadiusPx]
    var thumbCenterX by remember { mutableFloatStateOf(0f) }

    // 4. Dp → Px
    val thumbRadiusPx = with(LocalDensity.current) { thumbRadiusDp.toPx() }
    val trackHeightPx = with(LocalDensity.current) { trackHeightDp.toPx() }

    // 5. 当 fullWidthPx 或 progress 改变时，更新 thumbCenterX
    LaunchedEffect(fullWidthPx, progressClamped) {
        if (fullWidthPx > 0f) {
            // 计算：进度 * (可滑动的范围)
            // 可滑动范围 = fullWidthPx - 2 * thumbRadiusPx（因为要保证圆心不越界）
            val effectiveRange = (fullWidthPx - 2 * thumbRadiusPx).coerceAtLeast(0f)
            thumbCenterX = thumbRadiusPx + effectiveRange * progressClamped
        }
    }

    // 6. 在拖拽时，累加横向偏移并回调新的进度
    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { /* 可选：做按下小动画 */ },
            onDragEnd = {
                // 拖动结束后，把最终的进度（0f..1f）回调一次
                val effectiveRange = (fullWidthPx - 2 * thumbRadiusPx).coerceAtLeast(0f)
                val newProgress = ((thumbCenterX - thumbRadiusPx) / effectiveRange).coerceIn(0f, 1f)
                onValueChange(newProgress)
            },
            onDragCancel = { /* 可选 */ },
            onDrag = { change, dragAmount ->
                change.consume()
                // 叠加 x 方向偏移
                val rawNext = thumbCenterX + dragAmount.x
                // 限制：圆心不能小于 thumbRadiusPx 也不能大于 fullWidthPx - thumbRadiusPx
                val nextX = rawNext.coerceIn(thumbRadiusPx, fullWidthPx - thumbRadiusPx)
                thumbCenterX = nextX
                // 实时回调 progress
                val effectiveRange = (fullWidthPx - 2 * thumbRadiusPx).coerceAtLeast(0f)
                val newProgress = ((nextX - thumbRadiusPx) / effectiveRange).coerceIn(0f, 1f)
                onValueChange(newProgress)
            }
        )
    }

    // 7. 真正绘制
    Box(
        modifier = modifier
            // 高度至少要容得下 Track 与 Thumb
            .height(maxOf(trackHeightDp, thumbRadiusDp * 2))
            // 为了测量“整个组件的宽度”，这里加上 onSizeChanged
            .onSizeChanged { size ->
                fullWidthPx = size.width.toFloat()
            }
            .then(dragModifier),
        contentAlignment = Alignment.CenterStart
    ) {
        // 8. 用 Canvas 先画“充满全宽”的 Track（背景 + 进度）
        Canvas(modifier = Modifier.fillMaxSize()) {
            val fullWidth = size.width
            val fullHeight = size.height

            // 8.1. 先画“背景 Track”：从 x=0 开始，宽度 = fullWidth
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(x = 0f, y = (fullHeight - trackHeightPx) / 2f),
                size = Size(width = fullWidth, height = trackHeightPx),
                cornerRadius = CornerRadius(trackHeightPx / 2f, trackHeightPx / 2f)
            )

            // 8.2. 再画“已完成进度”：从 x=0 开始，宽度 = thumbCenterX
            //      （因为我们把 thumbCenterX 设置成了圆心的真实坐标，
            //       所以画到 thumbCenterX 就能和 Thumb 圆心对齐，视觉上 Track 与 Thumb 位置一致）
            drawRoundRect(
                color = progressColor,
                topLeft = Offset(x = 0f, y = (fullHeight - trackHeightPx) / 2f),
                size = Size(width = thumbCenterX, height = trackHeightPx),
                cornerRadius = CornerRadius(trackHeightPx / 2f, trackHeightPx / 2f)
            )
        }

        // 9. 在 Canvas 之上放一个 “圆形 Box” 作为 Thumb，圆心 x = thumbCenterX，圆心 y = height/2
        Box(
            modifier = Modifier
                .offset {
                    // 注意：offset 要传左上角的偏移，这里左上角 x = 圆心X - 半径
                    IntOffset(
                        x = (thumbCenterX - thumbRadiusPx).roundToInt(),
                        y = 0
                    )
                }
                .size(thumbRadiusDp * 2f) // Thumb 的直径 = 半径 * 2
                .shadow(elevation = 8.dp, shape = CircleShape, clip = true)
                .background(color = thumbColor, shape = CircleShape)
        )
    }
}
