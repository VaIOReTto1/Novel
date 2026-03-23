package com.novel.page.book.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.page.book.usecase.AddToBookshelfUseCase
import com.novel.page.book.usecase.CheckBookInShelfUseCase
import com.novel.page.book.usecase.FollowAuthorUseCase
import com.novel.page.book.usecase.GetBookDetailUseCase
import com.novel.page.book.usecase.GetLastChapterUseCase
import com.novel.page.book.usecase.RemoveFromBookshelfUseCase
import com.novel.core.ui.StateHolderImpl
import com.novel.utils.TimberLogger
import com.novel.utils.network.repository.CachedBookRepository
import com.novel.utils.network.api.front.BookService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍详情页面视图模型 - MVI重构版本
 * 
 * 采用统一的MVI架构模式：
 * - 继承BaseMviViewModel，获得完整的MVI支持
 * - 使用Intent处理所有用户交互和系统事件
 * - 通过Reducer进行纯函数状态转换
 * - 使用Effect处理一次性副作用
 * - 业务逻辑完全委托给UseCase层
 * 
 * 功能特性：
 * - 书籍基本信息的加载和缓存
 * - 最新章节信息的异步获取
 * - 用户评价数据的展示
 * - 简介展开/收起状态管理
 * - 书架操作和作者关注
 * - 错误处理和重试机制
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    /** 带缓存的书籍数据仓库 */
    private val cachedBookRepository: CachedBookRepository,
    private val bookService: BookService
) : BaseMviViewModel<BookDetailIntent, BookDetailState, BookDetailEffect>() {
    
    // 手动创建UseCase实例，避免Hilt泛型问题
    private val getBookDetailUseCase: GetBookDetailUseCase = GetBookDetailUseCase(cachedBookRepository, bookService)
    private val getLastChapterUseCase: GetLastChapterUseCase = GetLastChapterUseCase(cachedBookRepository, bookService)
    private val addToBookshelfUseCase: AddToBookshelfUseCase = AddToBookshelfUseCase()
    private val removeFromBookshelfUseCase: RemoveFromBookshelfUseCase = RemoveFromBookshelfUseCase()
    private val checkBookInShelfUseCase: CheckBookInShelfUseCase = CheckBookInShelfUseCase()
    private val followAuthorUseCase: FollowAuthorUseCase = FollowAuthorUseCase()
    
    companion object {
        private const val TAG = "BookDetailViewModel"
    }
    
    /** 新的StateAdapter实例 */
    val adapter = BookDetailStateAdapter(state)
    
    /** 兼容性属性：UI状态流，适配原有的UI层期望格式 */
    @Stable
    val uiState: StateFlow<StateHolderImpl<BookDetailUiState>> = state.map { mviState ->
        adapter.toUiState()
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = BookDetailStateAdapter(MutableStateFlow(createInitialState())).toUiState()
    )

    /**
     * 创建初始状态
     */
    override fun createInitialState(): BookDetailState {
        return BookDetailState()
    }
    
    /**
     * 获取Reducer实例
     */
    override fun getReducer(): com.novel.core.mvi.MviReducer<BookDetailIntent, BookDetailState> {
        // 返回一个适配器，将MviReducerWithEffect适配为MviReducer
        val effectReducer = BookDetailReducer()
        return object : com.novel.core.mvi.MviReducer<BookDetailIntent, BookDetailState> {
            override fun reduce(currentState: BookDetailState, intent: BookDetailIntent): BookDetailState {
                val result = effectReducer.reduce(currentState, intent)
                // 在这里处理副作用
                result.effect?.let { effect ->
                    sendEffect(effect)
                }
                return result.newState
            }
        }
    }

    /**
     * Intent处理完成后的回调
     * 在这里处理需要调用UseCase的Intent
     */
    override fun onIntentProcessed(intent: BookDetailIntent, newState: BookDetailState) {
        when (intent) {
            is BookDetailIntent.LoadBookDetail -> {
                loadBookDetailData(intent.bookId, intent.useCache)
            }
            is BookDetailIntent.RefreshBookDetail -> {
                loadBookDetailData(intent.bookId, useCache = false)
            }
            is BookDetailIntent.RetryLoading -> {
                loadBookDetailData(intent.bookId, useCache = true)
            }
            is BookDetailIntent.AddToBookshelf -> {
                handleAddToBookshelf(intent.bookId)
            }
            is BookDetailIntent.RemoveFromBookshelf -> {
                handleRemoveFromBookshelf(intent.bookId)
            }
            is BookDetailIntent.FollowAuthor -> {
                handleFollowAuthor(intent.authorName)
            }
            else -> {
                // 其他Intent由Reducer处理，无需额外操作
            }
        }
    }
    
    // ========== 公共方法 - 保持与原有ViewModel的兼容性 ==========
    
    /**
     * 加载书籍详情信息（兼容性方法）
     * 
     * @param bookId 书籍唯一标识符
     * @param useCache 是否使用缓存，默认为true（缓存优先）
     */
    fun loadBookDetail(bookId: String, useCache: Boolean = true) {
        TimberLogger.d(TAG, "loadBookDetail调用，转换为LoadBookDetail Intent")
        sendIntent(BookDetailIntent.LoadBookDetail(bookId, useCache))
    }
    
    /**
     * 切换书籍简介的展开/收起状态（兼容性方法）
     */
    fun toggleDescriptionExpanded() {
        TimberLogger.d(TAG, "toggleDescriptionExpanded调用，转换为ToggleDescriptionExpanded Intent")
        sendIntent(BookDetailIntent.ToggleDescriptionExpanded)
    }
    
    // ========== 私有方法 - 业务逻辑处理 ==========
    
    /**
     * 加载书籍详情数据
     * 
     * @param bookId 书籍ID
     * @param useCache 是否使用缓存
     */
    private fun loadBookDetailData(bookId: String, useCache: Boolean) {
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "开始加载书籍详情数据: bookId=$bookId, useCache=$useCache")
                    
                // 调用UseCase获取书籍详情
                val result = getBookDetailUseCase(
                    GetBookDetailUseCase.Params(bookId, useCache)
                )
                
                if (result.bookInfo != null) {
                    TimberLogger.d(TAG, "书籍信息加载成功: ${result.bookInfo.bookName}")
                    
                    // 发送成功Intent
                    sendIntent(BookDetailIntent.BookInfoLoadSuccess(
                        bookInfo = result.bookInfo,
                        reviews = result.reviews
                    ))
                    
                    // 异步加载最新章节信息
                    loadLastChapterData(bookId)
                } else {
                    TimberLogger.w(TAG, "书籍信息加载失败: bookId=$bookId")
                    sendIntent(BookDetailIntent.LoadFailure("书籍信息加载失败"))
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "加载书籍详情异常: bookId=$bookId", e)
                sendIntent(BookDetailIntent.LoadFailure(e.message ?: "未知错误"))
            }
        }
    }
    
    /**
     * 异步加载最新章节信息
     * 
     * @param bookId 书籍ID
     */
    private fun loadLastChapterData(bookId: String) {
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "开始加载最新章节信息: bookId=$bookId")
                
                val result = getLastChapterUseCase(
                    GetLastChapterUseCase.Params(bookId)
                )
                
                result.lastChapter?.let { lastChapter ->
                    TimberLogger.d(TAG, "最新章节加载成功: ${lastChapter.chapterName}")
                    sendIntent(BookDetailIntent.LastChapterLoadSuccess(lastChapter))
                } ?: run {
                    TimberLogger.w(TAG, "未找到章节信息: bookId=$bookId")
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "最新章节信息加载失败，不影响主要功能", e)
                // 章节信息加载失败，不影响书籍信息显示
            }
        }
    }

    /**
     * 处理添加到书架
     * 
     * @param bookId 书籍ID
     */
    private fun handleAddToBookshelf(bookId: String) {
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "处理添加到书架: bookId=$bookId")
                
                val result = addToBookshelfUseCase(
                    AddToBookshelfUseCase.Params(bookId)
                )
                
                if (!result.success) {
                    // 如果失败，发送错误Toast
                    sendEffect(BookDetailEffect.ShowToast(result.message.ifEmpty { "添加到书架失败" }))
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "添加到书架异常: bookId=$bookId", e)
                sendEffect(BookDetailEffect.ShowToast("添加到书架失败"))
    }
        }
    }
    
    /**
     * 处理从书架移除
     * 
     * @param bookId 书籍ID
     */
    private fun handleRemoveFromBookshelf(bookId: String) {
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "处理从书架移除: bookId=$bookId")
                
                val result = removeFromBookshelfUseCase(
                    RemoveFromBookshelfUseCase.Params(bookId)
                )
                
                if (!result.success) {
                    // 如果失败，发送错误Toast
                    sendEffect(BookDetailEffect.ShowToast(result.message.ifEmpty { "从书架移除失败" }))
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "从书架移除异常: bookId=$bookId", e)
                sendEffect(BookDetailEffect.ShowToast("从书架移除失败"))
            }
    }
}

/**
     * 处理关注作者
 * 
     * @param authorName 作者名称
     */
    private fun handleFollowAuthor(authorName: String) {
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "处理关注作者: authorName=$authorName")
                
                val result = followAuthorUseCase(
                    FollowAuthorUseCase.Params(authorName)
                )
                
                if (!result.success) {
                    // 如果失败，发送错误Toast
                    sendEffect(BookDetailEffect.ShowToast(result.message.ifEmpty { "关注作者失败" }))
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "关注作者异常: authorName=$authorName", e)
                sendEffect(BookDetailEffect.ShowToast("关注作者失败"))
            }
        }
    }
}
