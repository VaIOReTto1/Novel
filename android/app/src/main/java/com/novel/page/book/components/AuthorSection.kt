package com.novel.page.book.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.component.NovelText
import com.novel.page.component.NovelWeakenButton
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 作者信息展示组件
 * 包含作者头像、姓名和关注按钮
 * 
 * 性能优化特性：
 * - 支持关注状态动态显示
 * - 使用stable参数避免不必要重组
 * - 缓存点击事件处理器
 * 
 * @param bookInfo 书籍信息，包含作者名称
 * @param isAuthorFollowed 是否已关注作者，默认false
 * @param onFollowAuthor 关注作者回调函数
 */
@Composable
fun AuthorSection(
    bookInfo: BookDetailUiState.BookInfo?,
    isAuthorFollowed: Boolean = false,
    onFollowAuthor: ((String) -> Unit)? = null
) {
    val TAG = "AuthorSection"
    
    // 空数据保护
    if (bookInfo == null) {
        TimberLogger.w(TAG, "BookInfo为空，跳过渲染")
        return
    }

    // 根据关注状态确定按钮样式
    val buttonText = if (isAuthorFollowed) "已关注" else "关注"
    val buttonColor = if (isAuthorFollowed) {
        NovelColors.NovelTextGray.copy(alpha = 0.2f)
    } else {
        NovelColors.NovelTextGray.copy(alpha = 0.1f)
    }
    val textColor = if (isAuthorFollowed) {
        NovelColors.NovelText.copy(alpha = 0.6f)
    } else {
        NovelColors.NovelText.copy(alpha = 0.8f)
    }

    // 记忆化关注按钮点击事件
    val followClick = remember(bookInfo.authorName, isAuthorFollowed, onFollowAuthor) {
        {
            if (!isAuthorFollowed) {
                TimberLogger.d(TAG, "点击关注作者: ${bookInfo.authorName}")
                onFollowAuthor?.invoke(bookInfo.authorName)
            } else {
                TimberLogger.d(TAG, "作者已关注: ${bookInfo.authorName}")
                // 已关注状态下可以考虑不响应点击或者显示取消关注选项
            }
            Unit // 确保返回非nullable Unit
        }
    }
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 15.wdp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 作者头像（实心圆圈）
        Box(
            modifier = Modifier
                .size(25.wdp)
                .background(NovelColors.NovelTextGray, CircleShape)
        )
        
        // 作者名字
        NovelText(
            text = bookInfo.authorName,
            fontSize = 14.ssp,
            color = NovelColors.NovelText.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 8.wdp)
        )
        
        // 关注按钮
        NovelWeakenButton(
            content = {
                NovelText(
                    text = buttonText,
                    fontSize = 14.ssp,
                    color = textColor,
                    modifier = Modifier.padding(horizontal = 12.wdp)
                )
            },
            onClick = followClick,
            round = 18.wdp,
            color = buttonColor,
        )
    }
}
