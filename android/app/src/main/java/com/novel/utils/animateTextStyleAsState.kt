package com.novel.utils

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 文本样式动画工具
 * 
 * 功能：
 * - 在两个TextStyle之间做平滑插值动画
 * - 支持字体大小动画过渡
 * - 保持其他样式属性不变
 * 
 * 注意：
 * - 目前只支持字体大小的动画插值
 * - 其他属性（颜色、粗细等）直接使用目标值
 * 
 * @param targetValue 目标文本样式
 * @param animationSpec 动画规格
 * @return 动画状态的文本样式
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun animateTextStyleAsState(
    targetValue: TextStyle,
    animationSpec: AnimationSpec<Float> = spring()
): State<TextStyle> {
    // 字体大小动画控制器
    val fontSizeAnim = remember { Animatable(targetValue.fontSize.value) }

    // 监听目标值变化，启动动画
    LaunchedEffect(targetValue) {
        fontSizeAnim.animateTo(
            targetValue = targetValue.fontSize.value,
            animationSpec = animationSpec
        )
    }

    // 返回动画状态：插值字体大小，保持其他属性
    return derivedStateOf {
        TextStyle(
            fontSize = fontSizeAnim.value.sp,
            fontWeight = targetValue.fontWeight ?: FontWeight.Normal,
            color = targetValue.color
            // 如需支持其他属性动画，可在此扩展
        )
    }
}
