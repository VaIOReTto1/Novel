package com.novel.page.search.component

import com.novel.utils.TimberLogger
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.novel.page.component.NovelTextField
import com.novel.ui.theme.NovelDesignTokens
import com.novel.utils.wdp

private const val TAG = "SearchTopBar"

/**
 * 搜索页面顶部导航栏组件
 * 
 * 提供返回按钮、搜索输入框和搜索按钮的统一界面
 * 
 * @param query 当前搜索关键词
 * @param onQueryChange 搜索词变更回调
 * @param onBackClick 返回按钮点击回调
 * @param onSearchClick 搜索按钮点击回调
 */
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.wdp, vertical = 12.wdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.wdp)
    ) {
        // 性能优化：缓存返回按钮回调，避免每次重组都创建新 Lambda
        val onBackClickCached = remember(onBackClick) { {
            TimberLogger.d(TAG, "返回按钮被点击")
            onBackClick() ?: Unit
        } }
        
        // 返回按钮
        IconButton(
            onClick = onBackClickCached,
            modifier = Modifier.size(40.wdp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = NovelDesignTokens.lightColor("color.text.primary")
            )
        }

        // 搜索输入框
        NovelTextField(
            value = query,
            onValueChange = { newQuery ->
                TimberLogger.v(TAG, "搜索词变更: '$newQuery'")
                onQueryChange(newQuery)
            },
            placeText = "搜索小说或作者",
            modifier = Modifier
                .weight(1f)
                .height(40.wdp)
                .clip(RoundedCornerShape(5.dp))
        )

        // 性能优化：缓存搜索按钮回调，避免每次重组都创建新 Lambda
        val onSearchClickCached = remember(onSearchClick, query) { {
            TimberLogger.d(TAG, "搜索按钮被点击, 关键词: '$query'")
            onSearchClick()
        } }
        
        // 搜索按钮
        IconButton(
            onClick = onSearchClickCached,
            modifier = Modifier.size(40.wdp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                tint = NovelDesignTokens.lightColor("color.brand.primary")
            )
        }
    }
}
