package com.novel.debug

object RuntimeDebugScenarioStore {

    @Volatile
    private var searchPageSizeOverride: Int? = null

    @Volatile
    private var readerAutoFlipDirection: String? = null

    fun updateSearchPageSizeOverride(pageSize: Int?) {
        searchPageSizeOverride = pageSize
    }

    fun currentSearchPageSizeOverride(): Int? = searchPageSizeOverride

    fun updateReaderAutoFlipDirection(direction: String?) {
        readerAutoFlipDirection = direction
    }

    fun currentReaderAutoFlipDirection(): String? = readerAutoFlipDirection
}
