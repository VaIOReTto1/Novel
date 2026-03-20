package com.novel.page.book.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.novel.BuildConfig
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.component.NovelImageView
import com.novel.page.component.ImageLoadingStrategy
import com.novel.ui.theme.NovelColors
import com.novel.utils.wdp

/**
 * 书籍封面展示组件
 * 居中显示书籍封面图片，带圆角背景
 */
@Composable
fun BookCoverSection(bookInfo: BookDetailUiState.BookInfo?) {
    val TAG = "BookCoverSection"

    // 空数据保护
    if (bookInfo == null) {
        TimberLogger.w(TAG, "BookInfo为空，跳过渲染")
        return
    }

    Box(
        modifier = Modifier.padding(top = 55.wdp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        NovelImageView(
            imageUrl = bookInfo.picUrl,
            loadingStrategy = ImageLoadingStrategy.HIGH_QUALITY,
            useAdvancedCache = true,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(125.wdp)
                .height(190.wdp)
                .clip(RoundedCornerShape(5.wdp))
                .background(NovelColors.NovelMainLight),
        )
        
        if (BuildConfig.DEBUG && bookInfo.picUrl.isNotEmpty()) {
            TimberLogger.v(TAG, "渲染书籍封面: ${bookInfo.picUrl}")
        }
    }
}
