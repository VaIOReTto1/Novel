package com.novel.page.search.usecase

import androidx.compose.runtime.Stable
import com.novel.page.search.repository.SearchRepository
import javax.inject.Inject

/**
 * 添加搜索历史UseCase
 */
@Stable
class AddSearchHistoryUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    
    /**
     * 添加搜索历史
     * @param query 搜索关键词
     */
    operator fun invoke(query: String) {
        if (query.isNotBlank()) {
            searchRepository.addSearchHistory(query.trim())
        }
    }
} 
