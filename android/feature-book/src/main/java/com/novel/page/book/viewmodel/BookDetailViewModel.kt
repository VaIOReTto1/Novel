package com.novel.page.book.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.ui.StateHolderImpl
import com.novel.page.book.gateway.BookDetailGateway
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val gateway: BookDetailGateway,
) : BaseMviViewModel<BookDetailIntent, BookDetailState, BookDetailEffect>() {

    companion object {
        private const val TAG = "BookDetailViewModel"
    }

    val adapter = BookDetailStateAdapter(state)

    @Stable
    val uiState: StateFlow<StateHolderImpl<BookDetailUiState>> = state.map {
        adapter.toUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = BookDetailStateAdapter(MutableStateFlow(createInitialState())).toUiState(),
    )

    override fun createInitialState(): BookDetailState = BookDetailState()

    override fun getReducer(): com.novel.core.mvi.MviReducer<BookDetailIntent, BookDetailState> {
        val effectReducer = BookDetailReducer()
        return object : com.novel.core.mvi.MviReducer<BookDetailIntent, BookDetailState> {
            override fun reduce(
                currentState: BookDetailState,
                intent: BookDetailIntent,
            ): BookDetailState {
                val result = effectReducer.reduce(currentState, intent)
                result.effect?.let(::sendEffect)
                return result.newState
            }
        }
    }

    override fun onIntentProcessed(intent: BookDetailIntent, newState: BookDetailState) {
        when (intent) {
            is BookDetailIntent.LoadBookDetail -> loadBookDetailData(intent.bookId, intent.useCache)
            is BookDetailIntent.RefreshBookDetail -> loadBookDetailData(intent.bookId, useCache = false)
            is BookDetailIntent.RetryLoading -> loadBookDetailData(intent.bookId, useCache = true)
            is BookDetailIntent.AddToBookshelf -> handleAddToBookshelf(intent.bookId)
            is BookDetailIntent.RemoveFromBookshelf -> handleRemoveFromBookshelf(intent.bookId)
            is BookDetailIntent.FollowAuthor -> handleFollowAuthor(intent.authorName)
            else -> Unit
        }
    }

    fun loadBookDetail(bookId: String, useCache: Boolean = true) {
        sendIntent(BookDetailIntent.LoadBookDetail(bookId, useCache))
    }

    fun toggleDescriptionExpanded() {
        sendIntent(BookDetailIntent.ToggleDescriptionExpanded)
    }

    private fun loadBookDetailData(bookId: String, useCache: Boolean) {
        viewModelScope.launch {
            runCatching {
                gateway.loadBookDetail(bookId, useCache)
            }.onSuccess { result ->
                if (result.bookInfo != null) {
                    sendIntent(
                        BookDetailIntent.BookInfoLoadSuccess(
                            bookInfo = result.bookInfo,
                            reviews = result.reviews,
                        ),
                    )
                    loadLastChapterData(bookId)
                } else {
                    sendIntent(BookDetailIntent.LoadFailure("书籍信息加载失败"))
                }
            }.onFailure { error ->
                sendIntent(BookDetailIntent.LoadFailure(error.message ?: "未知错误"))
            }
        }
    }

    private fun loadLastChapterData(bookId: String) {
        viewModelScope.launch {
            runCatching {
                gateway.loadLastChapter(bookId)
            }.onSuccess { lastChapter ->
                lastChapter?.let { sendIntent(BookDetailIntent.LastChapterLoadSuccess(it)) }
            }
        }
    }

    private fun handleAddToBookshelf(bookId: String) {
        viewModelScope.launch {
            runCatching {
                gateway.addToBookshelf(bookId)
            }.onSuccess { result ->
                if (!result.success) {
                    sendEffect(BookDetailEffect.ShowToast(result.message.ifEmpty { "添加到书架失败" }))
                }
            }.onFailure {
                sendEffect(BookDetailEffect.ShowToast("添加到书架失败"))
            }
        }
    }

    private fun handleRemoveFromBookshelf(bookId: String) {
        viewModelScope.launch {
            runCatching {
                gateway.removeFromBookshelf(bookId)
            }.onSuccess { result ->
                if (!result.success) {
                    sendEffect(BookDetailEffect.ShowToast(result.message.ifEmpty { "从书架移除失败" }))
                }
            }.onFailure {
                sendEffect(BookDetailEffect.ShowToast("从书架移除失败"))
            }
        }
    }

    private fun handleFollowAuthor(authorName: String) {
        viewModelScope.launch {
            runCatching {
                gateway.followAuthor(authorName)
            }.onSuccess { result ->
                if (!result.success) {
                    sendEffect(BookDetailEffect.ShowToast(result.message.ifEmpty { "关注作者失败" }))
                }
            }.onFailure {
                sendEffect(BookDetailEffect.ShowToast("关注作者失败"))
            }
        }
    }
}
