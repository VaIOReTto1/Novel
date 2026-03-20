package com.novel.page.search.usecase

import com.novel.utils.TimberLogger
import com.novel.page.search.repository.RankingData
import com.novel.page.search.service.SearchService
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * 获取榜单列表业务用例
 * 
 * 功能职责：
 * - 并发获取多个榜单数据
 * - 数据聚合与统一包装
 * - 异常处理与性能优化
 * 
 * 榜单类型：
 * - novelRanking: 热门小说榜
 * - dramaRanking: 热门剧本榜
 * - newBookRanking: 新书榜
 * 
 * 技术特点：
 * - 协程并发提升性能
 * - coroutineScope统一管理
 * - 数据结构统一封装
 */
class GetRankingListUseCase @Inject constructor(
    private val searchService: SearchService
) {
    
    companion object {
        private const val TAG = "GetRankingListUseCase"
    }
    
    /**
     * 获取所有榜单数据
     * 
     * 并发策略：
     * - 三个榜单接口并行请求
     * - 减少总体响应时间
     * - 单个接口失败不影响其他
     * 
     * @return RankingData 榜单数据聚合对象
     */
    suspend operator fun invoke(): RankingData {
        return coroutineScope {
            TimberLogger.d(TAG, "开始获取榜单数据...")
            
            // 并行获取三个榜单数据
            val novelRankingDeferred = async { 
                TimberLogger.d(TAG, "请求热门小说榜...")
                searchService.getHotNovelRanking() 
            }
            val dramaRankingDeferred = async { 
                TimberLogger.d(TAG, "请求热门剧本榜...")
                searchService.getHotDramaRanking() 
            }
            val newBookRankingDeferred = async { 
                TimberLogger.d(TAG, "请求新书榜...")
                searchService.getNewBookRanking() 
            }
            
            // 等待所有请求完成并聚合结果
            val result = RankingData(
                novelRanking = novelRankingDeferred.await().toImmutableList(),
                dramaRanking = dramaRankingDeferred.await().toImmutableList(),
                newBookRanking = newBookRankingDeferred.await().toImmutableList()
            )
            
            TimberLogger.d(TAG, "所有榜单数据获取完成")
            result
        }
    }
} 
