package com.novel.page.read.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.novel.page.component.NovelText
import com.novel.page.read.viewmodel.BackgroundTheme
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlinx.collections.immutable.ImmutableList

/**
 * 背景颜色控制组件
 * 
 * 提供阅读器背景主题选择功能，用户可以从多个预设主题中选择
 * 每个主题包含经过优化的背景色和文字色搭配，确保最佳阅读体验
 * 
 * 特性：
 * - 圆形色卡展示，直观显示主题颜色
 * - 当前选中主题会显示边框高亮
 * - 点击即可切换主题，实时预览效果
 * - 自动保存用户选择
 * 
 * @param backgroundThemes 可选择的背景主题列表
 * @param currentBackgroundColor 当前选中的背景颜色，用于高亮显示
 * @param onThemeChange 主题切换回调，传递选中的新主题
 */
@Composable
fun BackgroundColorControl(
    backgroundThemes: ImmutableList<BackgroundTheme>,
    currentBackgroundColor: Color,
    onThemeChange: (BackgroundTheme) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        NovelText(text = "背景", fontSize = 14.ssp)
        Spacer(modifier = Modifier.width(15.wdp))
        backgroundThemes.forEach { theme ->
            val isSelected = currentBackgroundColor == theme.backgroundColor

            Card(
                modifier = Modifier
                    .padding(end = 15.wdp)
                    .size(32.wdp)
                    .clickable {
                        onThemeChange(theme)
                    },
                colors = CardDefaults.cardColors(containerColor = theme.backgroundColor),
                shape = CircleShape,
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(1.dp, NovelColors.NovelText)
                } else null
            ){}
        }
        Spacer(modifier = Modifier.width(15.wdp))
    }
}
