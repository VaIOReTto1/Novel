package com.novel.page.search.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 搜索筛选标签组件
 */
@Composable
fun SearchFilterChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) {
        NovelColors.NovelMain.copy(alpha = 0.1f)
    } else {
        NovelColors.NovelTextGray.copy(alpha = 0.1f)
    }

    val textColor = if (selected) {
        NovelColors.NovelMain
    } else {
        NovelColors.NovelText
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(5.wdp)
            )
            .debounceClickable(onClick = onClick)
            .padding(vertical = 2.wdp),
        contentAlignment = Alignment.Center
    ) {
        NovelText(
            text = text,
            fontSize = 13.ssp,
            color = textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
        )
    }
}

/**
 * 无边框的分类筛选标签组件（用于搜索结果页面）
 */
@Composable
fun CategoryFilterChip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val textColor = if (selected) {
        NovelColors.NovelMain
    } else {
        NovelColors.NovelTextGray
    }

    NovelText(
        text = text,
        fontSize = 13.ssp,
        color = textColor,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        modifier = modifier
            .debounceClickable(onClick = onClick)
    )
} 
