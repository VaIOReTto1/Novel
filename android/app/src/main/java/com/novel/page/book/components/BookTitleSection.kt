package com.novel.page.book.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp

/**
 * 书籍标题展示组件
 * 居中显示书籍名称，粗体样式
 */
@Composable
fun BookTitleSection(bookInfo: BookDetailUiState.BookInfo?) {
    val TAG = "BookTitleSection"
    
    // 空数据保护
    if (bookInfo == null) {
        TimberLogger.w(TAG, "BookInfo为空，跳过渲染")
        return
    }
    
    NovelText(
        text = bookInfo.bookName,
        fontSize = 20.ssp,
        fontWeight = FontWeight.Bold,
        color = NovelColors.NovelText,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    
    TimberLogger.v(TAG, "渲染书籍标题: ${bookInfo.bookName}")
} 
