package com.novel.page.read.viewmodel

internal object ReaderMappingHelper {

    fun toReaderInfo(state: ReaderState): ReaderInfo {
        return ReaderInfo(
            paginationState = state.paginationState,
            pageCountCache = state.pageCountCache,
            currentChapter = state.currentChapter,
            perChapterPageIndex = state.currentPageIndex,
        )
    }

    fun totalPages(readerInfo: ReaderInfo): Int {
        return readerInfo.pageCountCache?.totalPages
            ?: readerInfo.paginationState.estimatedTotalPages.takeIf { it > 0 }
            ?: 1
    }

    fun calculateGlobalPageNumber(
        readerInfo: ReaderInfo,
        currentChapterIndex: Int?,
        totalChapters: Int?,
    ): Int {
        if (readerInfo.pageCountCache != null && readerInfo.currentChapter != null) {
            val chapterRange = readerInfo.pageCountCache.chapterPageRanges.find {
                it.chapterId == readerInfo.currentChapter.id
            }
            if (chapterRange != null) {
                val pageIndexInChapter = readerInfo.perChapterPageIndex.coerceAtLeast(0)
                val totalPages = readerInfo.pageCountCache.totalPages
                return (chapterRange.startPage + pageIndexInChapter + 1).coerceIn(1, totalPages)
            }
        }

        if (currentChapterIndex != null && totalChapters != null) {
            val estimatedPagesPerChapter = 5
            val pageIndexInChapter = readerInfo.perChapterPageIndex.coerceAtLeast(0)
            return (currentChapterIndex * estimatedPagesPerChapter + pageIndexInChapter + 1)
                .coerceAtLeast(1)
        }

        return (readerInfo.perChapterPageIndex + 1).coerceAtLeast(1)
    }

    fun buildPageInfoText(
        readerInfo: ReaderInfo,
        currentChapterIndex: Int?,
        totalChapters: Int?,
    ): String {
        val totalPages = totalPages(readerInfo)
        val currentGlobalPage = calculateGlobalPageNumber(
            readerInfo = readerInfo,
            currentChapterIndex = currentChapterIndex,
            totalChapters = totalChapters,
        )

        return when {
            readerInfo.paginationState.isCalculating && totalPages > 0 ->
                "$currentGlobalPage / $totalPages (计算中...)"

            readerInfo.paginationState.isCalculating ->
                "页数计算中..."

            totalPages > 0 ->
                "$currentGlobalPage / $totalPages"

            else ->
                "1 / 1"
        }
    }
}
