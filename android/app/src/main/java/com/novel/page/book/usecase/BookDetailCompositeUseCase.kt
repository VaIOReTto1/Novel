package com.novel.page.book.usecase

import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.core.domain.ComposeUseCase
import com.novel.page.book.viewmodel.BookDetailState
import com.novel.utils.TimberLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/**
 * BookDetail组合UseCase
 * 
 * 处理复杂的业务逻辑组合，如同时加载书籍信息和章节信息
 */
class BookDetailCompositeUseCase @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val getLastChapterUseCase: GetLastChapterUseCase,
    private val checkBookInShelfUseCase: CheckBookInShelfUseCase,
    private val composeUseCase: ComposeUseCase
) : BaseUseCase<BookDetailCompositeUseCase.Params, BookDetailCompositeUseCase.Result>() {
    
    companion object {
        private const val TAG = "BookDetailCompositeUseCase"
    }
    
    @Stable
    data class Params(
        val bookId: String,
        val useCache: Boolean = true
    )

    @Stable
    data class Result(
        val bookInfo: BookDetailState.BookInfo?,
        val reviews: ImmutableList<BookDetailState.BookReview>,
        val lastChapter: BookDetailState.LastChapter?,
        val isInBookshelf: Boolean
    )
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "开始组合加载书籍详情: bookId=${params.bookId}")
        
        return try {
            // 并行执行多个UseCase
            val bookDetailResult = getBookDetailUseCase(GetBookDetailUseCase.Params(params.bookId, params.useCache))
            val lastChapterResult = getLastChapterUseCase(GetLastChapterUseCase.Params(params.bookId))
            val bookshelfResult = checkBookInShelfUseCase(CheckBookInShelfUseCase.Params(params.bookId))
            
            TimberLogger.d(TAG, "组合加载完成: bookId=${params.bookId}")
            
            Result(
                bookInfo = bookDetailResult.bookInfo,
                reviews = bookDetailResult.reviews.toImmutableList(),
                lastChapter = lastChapterResult.lastChapter,
                isInBookshelf = bookshelfResult.isInShelf
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "组合加载失败: bookId=${params.bookId}", e)
            throw e
        }
    }
}

/**
 * 书籍状态检查UseCase
 * 
 * 检查书籍的各种状态（是否在书架、是否关注作者等）
 */
class BookDetailStatusCheckUseCase @Inject constructor(
    private val checkBookInShelfUseCase: CheckBookInShelfUseCase
) : BaseUseCase<BookDetailStatusCheckUseCase.Params, BookDetailStatusCheckUseCase.Result>() {
    
    companion object {
        private const val TAG = "BookDetailStatusCheckUseCase"
    }
    
    @Stable
    data class Params(
        val bookId: String,
        val authorName: String
    )
    
    @Stable
    data class Result(
        val isInBookshelf: Boolean,
        val isAuthorFollowed: Boolean
    )
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "检查书籍状态: bookId=${params.bookId}, author=${params.authorName}")
        
        return try {
            val bookshelfResult = checkBookInShelfUseCase(
                CheckBookInShelfUseCase.Params(params.bookId)
            )
            
            // TODO: 添加检查作者关注状态的逻辑
            val isAuthorFollowed = false
            
            Result(
                isInBookshelf = bookshelfResult.isInShelf,
                isAuthorFollowed = isAuthorFollowed
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "状态检查失败: bookId=${params.bookId}", e)
            // 返回默认状态而不是抛出异常
            Result(
                isInBookshelf = false,
                isAuthorFollowed = false
            )
        }
    }
}
