package com.novel.page.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.novel.R
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.wdp
import com.novel.utils.ssp

/**
 * 首页顶部搜索栏和分类按钮
 */
@Composable
fun HomeTopBar(
    onSearchClick: () -> Unit,
    onCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.wdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 搜索框
        Box(
            modifier = Modifier
                .width(275.wdp)
                .height(48.wdp)
                .clip(RoundedCornerShape(5.wdp))
                .background(NovelColors.NovelBackground)
                .debounceClickable(onClick = { onSearchClick() }),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.wdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = NovelColors.NovelTextGray,
                    modifier = Modifier.size(20.wdp)
                )

                Spacer(modifier = Modifier.width(8.wdp))

                NovelText(
                    text = "搜索书名、作者",
                    fontSize = 14.ssp,
                    color = NovelColors.NovelTextGray
                )
            }
        }

        // 分类按钮
        Box(
            modifier = Modifier
                .padding(start = 5.wdp)
                .width(65.wdp)
                .height(48.wdp)
                .clip(RoundedCornerShape(5.wdp))
                .background(NovelColors.NovelBackground)
                .clickable { onCategoryClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.clarify),
                contentDescription = "分类", // 使用文字作为无障碍描述
                modifier = Modifier.size(30.wdp, 30.wdp),
            )
        }
    }
} 
