package com.novel.page.search.repository

import com.novel.core.storage.StorageFacade
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchPreferenceStorage @Inject constructor(
    private val storageFacade: StorageFacade
) {

    fun getSearchHistoryJson(): String? = storageFacade.getString(SEARCH_HISTORY_KEY)

    fun setSearchHistoryJson(value: String) {
        storageFacade.putString(SEARCH_HISTORY_KEY, value)
    }

    fun clearSearchHistory() {
        storageFacade.remove(SEARCH_HISTORY_KEY)
    }

    fun getHistoryExpanded(): Boolean =
        storageFacade.getString(HISTORY_EXPANDED_KEY)?.toBoolean() ?: false

    fun setHistoryExpanded(isExpanded: Boolean) {
        storageFacade.putString(HISTORY_EXPANDED_KEY, isExpanded.toString())
    }

    companion object {
        const val SEARCH_HISTORY_KEY = "search_history"
        const val HISTORY_EXPANDED_KEY = "history_expanded"
    }
}
