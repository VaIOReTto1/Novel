package com.novel.page.search.usecase

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.page.search.repository.SearchRepository
import javax.inject.Inject

/**
 * 切换搜索历史展开状态业务用例
 * 
 * 功能：
 * - 控制搜索历史的展开/收起状态
 * - 持久化用户的展开偏好
 */
@Stable
class ToggleHistoryExpansionUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    
    companion object {
        private const val TAG = "ToggleHistoryExpansionUseCase"
    }
    
    /**
     * 切换搜索历史展开状态
     * @param currentState 当前展开状态
     * @return 新的展开状态
     */
    operator fun invoke(currentState: Boolean): Boolean {
        val newState = !currentState
        TimberLogger.d(TAG, "切换搜索历史展开状态: $currentState -> $newState")
        searchRepository.saveHistoryExpansionState(newState)
        return newState
    }
} 
