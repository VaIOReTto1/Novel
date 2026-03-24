package com.novel.page.read.viewmodel

internal data class ReaderHistorySaveOutcome(
    val saved: Boolean,
)

internal class ReaderHistoryCoordinator {

    fun createSaveToHistoryIntent(
        state: ReaderState,
        bookId: String,
    ): ReaderIntent.SaveToHistory? {
        val currentChapter = state.currentChapter
        if (!state.isSuccess || currentChapter == null || bookId.isBlank()) {
            return null
        }

        val bookInfo = state.currentPageData?.bookInfo

        return ReaderIntent.SaveToHistory(
            bookId = bookId,
            chapterId = currentChapter.id,
            bookTitle = bookInfo?.bookName,
            author = bookInfo?.authorName,
            coverUrl = bookInfo?.picUrl,
            chapterTitle = currentChapter.chapterName,
        )
    }

    suspend fun saveHistory(
        intent: ReaderIntent.SaveToHistory,
        persist: suspend (ReaderIntent.SaveToHistory) -> Unit,
    ): ReaderHistorySaveOutcome {
        return try {
            persist(intent)
            ReaderHistorySaveOutcome(saved = true)
        } catch (_: Exception) {
            ReaderHistorySaveOutcome(saved = false)
        }
    }
}
