package com.novel.page.read.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 字体大小控制组件
 * 
 * 提供字体大小的增大和减小操作
 * 使用预定义的字号列表，支持范围限制和防抖点击
 * 
 * @param fontSize 当前字体大小
 * @param onFontSizeChange 字体大小变化回调
 */
@Composable
fun FontSizeControl(
    fontSize: Int,
    onFontSizeChange: (Int) -> Unit
) {
    val TAG = "FontSizeControl"
    // 预定义的字号列表（12-44）
    val fontSizes = listOf(
        12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
    )

    // 找到当前字体大小在列表中的索引
    val currentIndex = fontSizes.indexOf(fontSize).coerceAtLeast(0)

    // 边界检查
    val canDecrease = currentIndex > 0
    val canIncrease = currentIndex < fontSizes.size - 1

    val decreaseClick = remember(fontSize, onFontSizeChange, canDecrease) {
        {
            if (canDecrease) {
                val newSize = fontSizes[currentIndex - 1]
                TimberLogger.d(TAG, "减小字号: $fontSize -> $newSize")
                onFontSizeChange(newSize)
            }
        }
    }
    val increaseClick = remember(fontSize, onFontSizeChange, canIncrease) {
        {
            if (canIncrease) {
                val newSize = fontSizes[currentIndex + 1]
                TimberLogger.d(TAG, "增大字号: $fontSize -> $newSize")
                onFontSizeChange(newSize)
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.wdp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // 左侧标签
        NovelText(
            text = "字号",
            fontSize = 14.ssp,
            color = NovelColors.NovelText
        )

        // 减小字号按钮
        Card(
            modifier = Modifier
                .size(width = 48.wdp, height = 32.wdp)
                .debounceClickable(
                    intervalMillis = 200,
                    enabled = canDecrease,
                    onClick = decreaseClick
                ),
            shape = RoundedCornerShape(16.wdp),
            colors = CardDefaults.cardColors(
                containerColor = if (canDecrease) Color.Gray.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NovelText(
                    text = "A－",
                    fontSize = 14.ssp,
                    color = if (canDecrease) Color.Black else NovelColors.NovelTextGray.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 当前字号显示
        NovelText(
            text = fontSize.toString(),
            fontSize = 12.ssp,
            color = NovelColors.NovelText,
            fontWeight = FontWeight.Medium
        )

        // 增大字号按钮
        Card(
            modifier = Modifier
                .size(width = 48.wdp, height = 32.wdp)
                .debounceClickable(
                    intervalMillis = 200,
                    enabled = canIncrease,
                    onClick = increaseClick
                ),
            shape = RoundedCornerShape(16.wdp),
            colors = CardDefaults.cardColors(
                containerColor = if (canIncrease) Color.Gray.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NovelText(
                    text = "A＋",
                    fontSize = 14.ssp,
                    color = if (canIncrease) Color.Black else NovelColors.NovelTextGray.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
