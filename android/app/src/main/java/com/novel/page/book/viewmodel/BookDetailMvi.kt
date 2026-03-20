package com.novel.page.book.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect
import com.novel.core.mvi.MviReducer
import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.utils.TimberLogger

/**
 * BookDetail MVI契约定义
 * 
 * 定义书籍详情页面的Intent、State、Effect和Reducer
 * 确保与原有功能完全一致，无任何遗漏
 */

/**
 * BookDetail Intent - 用户意图和系统事件
 */
sealed class BookDetailIntent : MviIntent {
    /**
     * 加载书籍详情
     * @param bookId 书籍ID
     * @param useCache 是否使用缓存，默认true
     */
    data class LoadBookDetail(
        val bookId: String,
        val useCache: Boolean = true
    ) : BookDetailIntent()
    
    /**
     * 刷新书籍详情数据
     * @param bookId 书籍ID
     */
    data class RefreshBookDetail(val bookId: String) : BookDetailIntent()
    
    /**
     * 切换简介展开/收起状态
     */
    object ToggleDescriptionExpanded : BookDetailIntent()
    
    /**
     * 开始阅读书籍
     * @param bookId 书籍ID
     * @param chapterId 章节ID，可选
     */
    data class StartReading(
        val bookId: String,
        val chapterId: String? = null
    ) : BookDetailIntent()
    
    /**
     * 添加到书架
     * @param bookId 书籍ID
     */
    data class AddToBookshelf(val bookId: String) : BookDetailIntent()
    
    /**
     * 从书架移除
     * @param bookId 书籍ID
     */
    data class RemoveFromBookshelf(val bookId: String) : BookDetailIntent()
    
    /**
     * 分享书籍
     * @param bookId 书籍ID
     * @param bookName 书籍名称
     */
    data class ShareBook(
        val bookId: String,
        val bookName: String
    ) : BookDetailIntent()
    
    /**
     * 关注作者
     * @param authorName 作者名称
     */
    data class FollowAuthor(val authorName: String) : BookDetailIntent()
    
    /**
     * 重试加载（错误状态下）
     * @param bookId 书籍ID
     */
    data class RetryLoading(val bookId: String) : BookDetailIntent()
    
    /**
     * 清除错误状态
     */
    object ClearError : BookDetailIntent()
    
    /**
     * 内部Intent：书籍基本信息加载成功
     */
    @Stable
    data class BookInfoLoadSuccess(
        val bookInfo: BookDetailState.BookInfo,
        val reviews: ImmutableList<BookDetailState.BookReview>
    ) : BookDetailIntent()
    
    /**
     * 内部Intent：最新章节加载成功
     */
    data class LastChapterLoadSuccess(
        val lastChapter: BookDetailState.LastChapter
    ) : BookDetailIntent()
    
    /**
     * 内部Intent：加载失败
     */
    data class LoadFailure(val error: String) : BookDetailIntent()
}

/**
 * BookDetail State - 页面状态
 */
@Stable
data class BookDetailState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val bookInfo: BookInfo? = null,
    val lastChapter: LastChapter? = null,
    val reviews: ImmutableList<BookReview> = persistentListOf(),
    val isDescriptionExpanded: Boolean = false,
    val isInBookshelf: Boolean = false,
    val isAuthorFollowed: Boolean = false,
    val currentBookId: String? = null
) : MviState {
    
    override val isEmpty: Boolean
        get() = bookInfo == null && !isLoading && error == null
    
    override val isSuccess: Boolean
        get() = bookInfo != null && !isLoading && error == null
    
    /**
     * 书籍基本信息
     */
    @Immutable
    data class BookInfo(
        val id: String,
        val bookName: String,
        val authorName: String,
        val bookDesc: String,
        val picUrl: String,
        val visitCount: Long,
        val wordCount: Int,
        val categoryName: String
    )
    
    /**
     * 最新章节信息
     */
    @Immutable
    data class LastChapter(
        val chapterName: String,
        val chapterUpdateTime: String
    )
    
    /**
     * 用户评价
     */
    @Immutable
    data class BookReview(
        val id: String,
        val content: String,
        val rating: Int, // 1-5星评级
        val readTime: String,
        val userName: String
    )
}

/**
 * BookDetail Effect - 一次性副作用
 */
sealed class BookDetailEffect : MviEffect {
    /**
     * 导航到阅读器
     * @param bookId 书籍ID
     * @param chapterId 章节ID，可选
     */
    data class NavigateToReader(
        val bookId: String,
        val chapterId: String? = null
    ) : BookDetailEffect()
    
    /**
     * 显示Toast消息
     * @param message 消息内容
     */
    data class ShowToast(val message: String) : BookDetailEffect()
    
    /**
     * 分享书籍
     * @param title 书籍标题
     * @param content 分享内容
     */
    data class ShareBook(
        val title: String,
        val content: String
    ) : BookDetailEffect()
    
    /**
     * 显示加载对话框
     */
    object ShowLoadingDialog : BookDetailEffect()
    
    /**
     * 隐藏加载对话框
     */
    object HideLoadingDialog : BookDetailEffect()
    
    /**
     * 触发震动反馈
     */
    object TriggerHapticFeedback : BookDetailEffect()
}

/**
 * BookDetail Reducer - 状态转换逻辑
 */
class BookDetailReducer : MviReducerWithEffect<BookDetailIntent, BookDetailState, BookDetailEffect> {
    
    companion object {
        private const val TAG = "BookDetailReducer"
    }
    
    override fun reduce(
        currentState: BookDetailState,
        intent: BookDetailIntent
    ): ReduceResult<BookDetailState, BookDetailEffect> {
        TimberLogger.d(TAG, "处理Intent: ${intent::class.simpleName}")
        
        return when (intent) {
            is BookDetailIntent.LoadBookDetail -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true,
                        error = null,
                        currentBookId = intent.bookId
                    )
                )
            }
            
            is BookDetailIntent.RefreshBookDetail -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true,
                        error = null,
                        currentBookId = intent.bookId
                    )
                )
            }
            
            is BookDetailIntent.BookInfoLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        error = null,
                        bookInfo = intent.bookInfo,
                        reviews = intent.reviews
                    )
                )
            }
            
            is BookDetailIntent.LastChapterLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        lastChapter = intent.lastChapter
                    )
                )
            }
            
            is BookDetailIntent.LoadFailure -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        error = intent.error
                    )
                )
            }
            
            is BookDetailIntent.ToggleDescriptionExpanded -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isDescriptionExpanded = !currentState.isDescriptionExpanded
                    )
                )
            }
            
            is BookDetailIntent.StartReading -> {
                ReduceResult(
                    newState = currentState,
                    effect = BookDetailEffect.NavigateToReader(
                        bookId = intent.bookId,
                        chapterId = intent.chapterId
                    )
                )
            }
            
            is BookDetailIntent.AddToBookshelf -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isInBookshelf = true
                    ),
                    effect = BookDetailEffect.ShowToast("已添加到书架")
                )
            }
            
            is BookDetailIntent.RemoveFromBookshelf -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isInBookshelf = false
                    ),
                    effect = BookDetailEffect.ShowToast("已从书架移除")
                )
            }
            
            is BookDetailIntent.ShareBook -> {
                ReduceResult(
                    newState = currentState,
                    effect = BookDetailEffect.ShareBook(
                        title = intent.bookName,
                        content = "推荐一本好书：${intent.bookName}"
                    )
                )
            }
            
            is BookDetailIntent.FollowAuthor -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isAuthorFollowed = true
                    ),
                    effect = BookDetailEffect.ShowToast("已关注作者：${intent.authorName}")
                )
            }
            
            is BookDetailIntent.RetryLoading -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true,
                        error = null,
                        currentBookId = intent.bookId
                    )
                )
            }
            
            is BookDetailIntent.ClearError -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        error = null
                    )
                )
            }
        }
    }
} 
