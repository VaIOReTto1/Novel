package com.novel.page.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 排名序号组件
 * 
 * 功能：
 * - 显示榜单排名数字
 * - 前三名特殊颜色处理（金银铜）
 * - 统一样式和字体
 * 
 * @param rank 排名数字
 * @param fontSize 字体大小
 */
@Composable
fun RankingNumber(
    rank: Int,
    fontSize: TextUnit = 14.ssp,
) {
    // 根据排名设置不同颜色
    val backgroundColor = when (rank) {
        1 -> Color(0xFFFFD700) // 金色
        2 -> Color(0xFFC0C0C0) // 银色
        3 -> Color(0xFFCD7F32) // 铜色
        else -> NovelColors.NovelText // 默认文本色
    }

    NovelText(
        text = rank.toString(),
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        color = backgroundColor,
        modifier = Modifier.padding(top = 2.wdp)
    )
}
