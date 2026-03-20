package com.novel.page.book.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.TimberLogger
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 书籍操作区域组件
 * 包含开始阅读、添加书架、分享等核心操作按钮
 */
@Composable
fun BookActionSection(
    bookInfo: BookDetailUiState.BookInfo?,
    isInBookshelf: Boolean = false,
    onStartReading: ((String, String?) -> Unit)? = null,
    onAddToBookshelf: ((String) -> Unit)? = null,
    onRemoveFromBookshelf: ((String) -> Unit)? = null,
    onShareBook: ((String, String) -> Unit)? = null
) {
    val TAG = "BookActionSection"
    
    // 空数据保护
    if (bookInfo == null) {
        TimberLogger.w(TAG, "BookInfo为空，跳过渲染")
        return
    }

    // 记忆化按钮点击事件
    val startReadingClick = remember(bookInfo.id, onStartReading) {
        {
            TimberLogger.d(TAG, "点击开始阅读: ${'$'}{bookInfo.bookName}")
            onStartReading?.invoke(bookInfo.id, null) ?: Unit
        }
    }

    val shelfClick = remember(isInBookshelf, bookInfo.id, bookInfo.bookName, onAddToBookshelf, onRemoveFromBookshelf) {
        {
            if (isInBookshelf) {
                TimberLogger.d(TAG, "点击移出书架: ${'$'}{bookInfo.bookName}")
                onRemoveFromBookshelf?.invoke(bookInfo.id) ?: Unit
            } else {
                TimberLogger.d(TAG, "点击加入书架: ${'$'}{bookInfo.bookName}")
                onAddToBookshelf?.invoke(bookInfo.id) ?: Unit
            }
        }
    }

    val shareClick = remember(bookInfo.id, bookInfo.bookName, onShareBook) {
        {
            TimberLogger.d(TAG, "点击分享书籍: ${'$'}{bookInfo.bookName}")
            onShareBook?.invoke(bookInfo.id, bookInfo.bookName) ?: Unit
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.wdp),
        verticalArrangement = Arrangement.spacedBy(12.wdp)
    ) {
        // 开始阅读按钮
        Button(
            onClick = startReadingClick,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "开始阅读按钮" }
                .height(48.wdp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NovelColors.NovelMain
            ),
            shape = RoundedCornerShape(8.wdp)
        ) {
            NovelText(
                text = "开始阅读",
                fontSize = 16.ssp,
                fontWeight = FontWeight.Bold,
                color = NovelColors.NovelBookBackground
            )
        }
        
        // 操作按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.wdp)
        ) {
            // 书架操作按钮
            OutlinedButton(
                onClick = shelfClick,
                modifier = Modifier
                    .weight(1f)
                    .height(40.wdp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NovelColors.NovelText
                ),
                shape = RoundedCornerShape(8.wdp)
            ) {
                NovelText(
                    text = if (isInBookshelf) "移出书架" else "加入书架",
                    fontSize = 14.ssp,
                    color = NovelColors.NovelText
                )
            }
            
            // 分享按钮
            OutlinedButton(
                onClick = shareClick,
                modifier = Modifier
                    .weight(1f)
                    .height(40.wdp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NovelColors.NovelText
                ),
                shape = RoundedCornerShape(8.wdp)
            ) {
                NovelText(
                    text = "分享",
                    fontSize = 14.ssp,
                    color = NovelColors.NovelText
                )
            }
        }
    }
} 
