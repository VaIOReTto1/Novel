package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.page.read.repository.ProgressiveCalculationState
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * 观察分页进度用例
 * 
 * 负责观察分页服务的进度状态：
 * 1. 返回分页状态的 Flow
 * 2. 记录分页进度变化
 * 3. 提供进度监控功能
 */
@Stable
class ObservePaginationProgressUseCase @Inject constructor(
    private val paginationService: PaginationService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.OBSERVE_PAGINATION_PROGRESS_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    /**
     * 执行观察分页进度
     * 
     * @return 分页状态的 Flow
     */
    fun execute(): Flow<ProgressiveCalculationState> {
        logger.logDebug("开始观察分页进度", TAG)
        
        return paginationService.paginationState
            .onEach { state ->
                if (state.isCalculating) {
                    logger.logDebug("分页状态: 进行中 - ${state.calculatedChapters}/${state.totalChapters} 章节", TAG)
                } else {
                    logger.logInfo("分页状态: 完成 - 总页数: ${state.currentCalculatedPages}", TAG)
                }
            }
    }
} 
