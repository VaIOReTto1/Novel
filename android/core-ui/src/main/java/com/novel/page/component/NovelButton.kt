package com.novel.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.wdp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 主要按钮组件（渐变背景）
 * 
 * 特点：
 * - 渐变色背景
 * - 防抖点击处理
 * - 可自定义圆角和颜色
 * 
 * @param content 按钮内容
 * @param modifier 修饰符
 * @param onClick 点击事件
 * @param round 圆角大小
 * @param enabldeClicke 是否可点击
 * @param colors 渐变色列表
 */
@Composable
fun NovelMainButton(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    round: Dp = 24.wdp,
    enabldeClicke: Boolean = true,
    colors: ImmutableList<Color> = persistentListOf(NovelColors.NovelMain, NovelColors.NovelMainLight)
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = colors
                ),
                shape = RoundedCornerShape(round)
            )
            .debounceClickable(onClick = onClick, enabled = enabldeClicke),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * 次要按钮组件（纯色背景）
 * 
 * 特点：
 * - 纯色背景
 * - 防抖点击处理
 * - 适合次要操作
 * 
 * @param content 按钮内容
 * @param modifier 修饰符
 * @param onClick 点击事件
 * @param round 圆角大小
 * @param color 背景颜色
 */
@Composable
fun NovelWeakenButton(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    round: Dp = 24.wdp,
    color: Color = NovelColors.NovelSecondaryBackground
) {
    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(round)
            )
            .debounceClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
