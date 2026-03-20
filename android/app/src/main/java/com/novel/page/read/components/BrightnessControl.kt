package com.novel.page.read.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.novel.page.component.NovelText
import com.novel.page.component.SolidCircleSlider
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 亮度控制组件
 * 
 * 核心功能：
 * - 提供可视化的亮度调节滑块
 * - 支持分档亮度控制（10档）
 * - 自动量化亮度值到最近档位
 * - 护眼模式提示显示
 * 
 * 设计特点：
 * - 使用自定义SolidCircleSlider确保交互体验
 * - 透明度渐变设计符合视觉习惯  
 * - 分档控制避免频繁触发回调
 * - 支持主题色彩自适应
 * 
 * @param brightness 当前亮度值（0.0-1.0）
 * @param backgroundColor 背景主题色，用于滑块着色
 * @param onBrightnessChange 亮度变化回调
 */
@Composable
fun BrightnessControl(
    brightness: Float,
    backgroundColor: Color,
    onBrightnessChange: (Float) -> Unit
) {
    val TAG = "BrightnessControl"
    val STEP_COUNT = 10 // 亮度分档数量
    
    val stepSize = 1f / STEP_COUNT

    val valueChange = remember(brightness, onBrightnessChange) {
        { raw: Float ->
            val stepped = (raw / stepSize).roundToInt() * stepSize
            val finalValue = stepped.coerceIn(0f, 1f)
            if (abs(finalValue - brightness) > 0.01f) {
                TimberLogger.d(TAG, "亮度调节: $brightness -> $finalValue (档位: ${(finalValue * STEP_COUNT).toInt()})")
                onBrightnessChange(finalValue)
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.wdp)
    ) {
        // 左侧亮度标签
        NovelText("亮度", fontSize = 14.ssp)
        
        // 中间亮度调节滑块
        SolidCircleSlider(
            modifier = Modifier.weight(1f),
            progress = brightness,
            onValueChange = valueChange,
            // 轨道颜色：浅灰色透明
            trackColor = Color.Gray.copy(alpha = 0.1f),
            // 进度颜色：灰色半透明  
            progressColor = Color.Gray.copy(alpha = 0.5f),
            // 滑块颜色：使用背景主题色
            thumbColor = backgroundColor,
            trackHeightDp = 24.dp,
            thumbRadiusDp = 16.dp
        )
        
        // 右侧护眼模式提示
        NovelText("护眼模式", fontSize = 14.ssp, fontWeight = FontWeight.Bold)
    }
}
