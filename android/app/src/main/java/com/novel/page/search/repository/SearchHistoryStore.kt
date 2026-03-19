package com.novel.page.search.repository

import androidx.compose.runtime.Stable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.novel.utils.TimberLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Stable
class SearchHistoryStore @Inject constructor(
    @Stable
    private val searchPreferenceStorage: SearchPreferenceStorage,
    @Stable
    private val gson: Gson
) {

    fun getSearchHistory(): List<String> {
        return try {
            val historyJson = searchPreferenceStorage.getSearchHistoryJson()
            if (historyJson != null) {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson<List<String>>(historyJson, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to read search history", e)
            emptyList()
        }
    }

    fun addSearchHistory(keyword: String) {
        try {
            val currentHistory = getSearchHistory().toMutableList()
            currentHistory.remove(keyword)
            currentHistory.add(0, keyword)

            if (currentHistory.size > MAX_SEARCH_HISTORY) {
                currentHistory.removeAt(currentHistory.lastIndex)
            }

            searchPreferenceStorage.setSearchHistoryJson(gson.toJson(currentHistory))
            TimberLogger.d(TAG, "Saved search history keyword: $keyword")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to save search history", e)
        }
    }

    fun clearSearchHistory() {
        try {
            searchPreferenceStorage.clearSearchHistory()
            TimberLogger.d(TAG, "Cleared search history")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to clear search history", e)
        }
    }

    fun getHistoryExpansionState(): Boolean {
        return try {
            searchPreferenceStorage.getHistoryExpanded()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to read history expansion state", e)
            false
        }
    }

    fun saveHistoryExpansionState(isExpanded: Boolean) {
        try {
            searchPreferenceStorage.setHistoryExpanded(isExpanded)
            TimberLogger.d(TAG, "Saved history expansion state: $isExpanded")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to save history expansion state", e)
        }
    }

    private companion object {
        const val TAG = "SearchHistoryStore"
        const val MAX_SEARCH_HISTORY = 10
    }
}
