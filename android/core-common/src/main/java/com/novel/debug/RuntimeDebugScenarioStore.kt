package com.novel.debug

object RuntimeDebugScenarioStore {

    @Volatile
    private var searchPageSizeOverride: Int? = null

    fun updateSearchPageSizeOverride(pageSize: Int?) {
        searchPageSizeOverride = pageSize
    }

    fun currentSearchPageSizeOverride(): Int? = searchPageSizeOverride
}
