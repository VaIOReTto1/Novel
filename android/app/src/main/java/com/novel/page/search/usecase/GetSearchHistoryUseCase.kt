package com.novel.page.search.usecase

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.page.search.repository.SearchRepository
import javax.inject.Inject

/**
 * 获取搜索历史业务用例
 * 
 * 功能：
 * - 获取用户搜索历史记录
 * - 支持历史记录展示
 */
@Stable
class GetSearchHistoryUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    
    companion object {
        private const val TAG = "GetSearchHistoryUseCase"
    }
    
    /**
     * 获取搜索历史列表
     * @return 搜索历史字符串列表
     */
    operator fun invoke(): List<String> {
        TimberLogger.d(TAG, "获取搜索历史")
        val history = searchRepository.getSearchHistory()
        TimberLogger.d(TAG, "搜索历史数量: ${history.size}")
        return history
    }
} 
