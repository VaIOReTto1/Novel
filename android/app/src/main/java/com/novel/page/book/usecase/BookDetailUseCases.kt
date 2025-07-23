package com.novel.page.book.usecase

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.novel.core.domain.BaseUseCase
import com.novel.page.book.viewmodel.BookDetailState
import com.novel.utils.network.repository.CachedBookRepository
import com.novel.utils.network.cache.CacheStrategy
import com.novel.utils.TimberLogger
import javax.inject.Inject

/**
 * 获取书籍详情UseCase
 */
class GetBookDetailUseCase @Inject constructor(
    private val cachedBookRepository: CachedBookRepository
) : BaseUseCase<GetBookDetailUseCase.Params, GetBookDetailUseCase.Result>() {
    
    companion object {
        private const val TAG = "GetBookDetailUseCase"
    }
    
    @Stable
    data class Params(
        val bookId: String,
        val useCache: Boolean = true
    )

    @Stable
    data class Result(
        val bookInfo: BookDetailState.BookInfo?,
        val reviews: ImmutableList<BookDetailState.BookReview>
    )
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "获取书籍详情: bookId=${params.bookId}, useCache=${params.useCache}")
        
        val strategy = if (params.useCache) CacheStrategy.CACHE_FIRST else CacheStrategy.NETWORK_ONLY
        
        val bookInfo = cachedBookRepository.getBookInfoWithIncrementalSync(
            bookId = params.bookId.toLong(),
            strategy = strategy
        )
        
        val bookDetailInfo = bookInfo?.let { info ->
            BookDetailState.BookInfo(
                id = info.id.toString(),
                bookName = info.bookName,
                authorName = info.authorName,
                bookDesc = info.bookDesc,
                picUrl = info.picUrl,
                visitCount = info.visitCount,
                wordCount = info.wordCount,
                categoryName = info.categoryName
            )
        }
        
        // 生成模拟书评数据（与原有逻辑保持一致）
        val reviews = generateMockReviews()
        
        return Result(
            bookInfo = bookDetailInfo,
            reviews = reviews
        )
    }
    
    private fun generateMockReviews(): ImmutableList<BookDetailState.BookReview> {
        return persistentListOf(
            BookDetailState.BookReview(
                id = "1",
                content = "这个职业(老板)无敌了，全天下的天才为之打工。",
                rating = 5,
                readTime = "阅读54分钟后点评",
                userName = "用户1"
            ),
            BookDetailState.BookReview(
                id = "2", 
                content = "很不错的脑洞，题材也很新颖，就是主角有点感太低了，全是手下在发力，主角变考全程躺平...",
                rating = 4,
                readTime = "阅读2小时后点评",
                userName = "用户2"
            ),
            BookDetailState.BookReview(
                id = "3",
                content = "我看书是不是有点太快了",
                rating = 5,
                readTime = "阅读不足30分钟后点评",
                userName = "用户3"
            )
        )
    }
}

/**
 * 获取最新章节UseCase
 */
class GetLastChapterUseCase @Inject constructor(
    private val cachedBookRepository: CachedBookRepository
) : BaseUseCase<GetLastChapterUseCase.Params, GetLastChapterUseCase.Result>() {
    
    companion object {
        private const val TAG = "GetLastChapterUseCase"
    }
    
    @Stable
    data class Params(val bookId: String)
    
    @Stable
    data class Result(val lastChapter: BookDetailState.LastChapter?)
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "获取最新章节: bookId=${params.bookId}")
        
        val chapters = cachedBookRepository.getBookChaptersWithIncrementalSync(
            bookId = params.bookId.toLong(),
            strategy = CacheStrategy.CACHE_FIRST
        )
        
        val lastChapter = if (chapters.isNotEmpty()) {
            val chapter = chapters.last()
            BookDetailState.LastChapter(
                chapterName = chapter.chapterName,
                chapterUpdateTime = chapter.chapterUpdateTime
            )
        } else null
        
        return Result(lastChapter = lastChapter)
    }
}

/**
 * 添加到书架UseCase
 */
class AddToBookshelfUseCase @Inject constructor(
    // TODO: 注入书架相关的Repository
) : BaseUseCase<AddToBookshelfUseCase.Params, AddToBookshelfUseCase.Result>() {
    
    companion object {
        private const val TAG = "AddToBookshelfUseCase"
    }
    
    @Stable
    data class Params(val bookId: String)
    
    @Stable
    data class Result(val success: Boolean, val message: String = "")
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "添加到书架: bookId=${params.bookId}")
        
        return try {
            // TODO: 实现实际的书架操作
            // 模拟网络延迟
            kotlinx.coroutines.delay(500)
            
            // 模拟成功添加
            TimberLogger.d(TAG, "书籍添加到书架成功: bookId=${params.bookId}")
            Result(success = true, message = "已添加到书架")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "添加到书架失败: bookId=${params.bookId}", e)
            Result(success = false, message = "添加失败，请重试")
        }
    }
}

/**
 * 从书架移除UseCase
 */
class RemoveFromBookshelfUseCase @Inject constructor(
    // TODO: 注入书架相关的Repository
) : BaseUseCase<RemoveFromBookshelfUseCase.Params, RemoveFromBookshelfUseCase.Result>() {
    
    companion object {
        private const val TAG = "RemoveFromBookshelfUseCase"
    }
    
    @Stable
    data class Params(val bookId: String)
    
    @Stable
    data class Result(val success: Boolean, val message: String = "")
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "从书架移除: bookId=${params.bookId}")
        
        return try {
            // TODO: 实现实际的书架操作
            // 模拟网络延迟
            kotlinx.coroutines.delay(300)
            
            // 模拟成功移除
            TimberLogger.d(TAG, "书籍从书架移除成功: bookId=${params.bookId}")
            Result(success = true, message = "已从书架移除")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "从书架移除失败: bookId=${params.bookId}", e)
            Result(success = false, message = "移除失败，请重试")
        }
    }
}

/**
 * 检查书籍是否在书架UseCase
 */
class CheckBookInShelfUseCase @Inject constructor(
    // TODO: 注入书架相关的Repository
) : BaseUseCase<CheckBookInShelfUseCase.Params, CheckBookInShelfUseCase.Result>() {
    
    companion object {
        private const val TAG = "CheckBookInShelfUseCase"
    }
    
    @Stable
    data class Params(val bookId: String)
    
    @Stable
    data class Result(val isInShelf: Boolean)
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "检查书籍是否在书架: bookId=${params.bookId}")
        
        // TODO: 实现实际的书架检查
        // 目前返回false，保持原有行为
        return Result(isInShelf = false)
    }
}

/**
 * 关注作者UseCase
 */
class FollowAuthorUseCase @Inject constructor(
    // TODO: 注入作者相关的Repository
) : BaseUseCase<FollowAuthorUseCase.Params, FollowAuthorUseCase.Result>() {
    
    companion object {
        private const val TAG = "FollowAuthorUseCase"
    }
    
    @Stable
    data class Params(val authorName: String)
    
    @Stable
    data class Result(val success: Boolean, val message: String = "")
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "关注作者: authorName=${params.authorName}")
        
        return try {
            // TODO: 实现实际的关注操作
            // 模拟网络延迟
            kotlinx.coroutines.delay(400)
            
            // 模拟成功关注
            TimberLogger.d(TAG, "关注作者成功: authorName=${params.authorName}")
            Result(success = true, message = "已关注作者：${params.authorName}")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "关注作者失败: authorName=${params.authorName}", e)
            Result(success = false, message = "关注失败，请重试")
        }
    }
}