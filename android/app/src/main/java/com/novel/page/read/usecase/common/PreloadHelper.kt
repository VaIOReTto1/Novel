package com.novel.page.read.usecase.common

import com.novel.page.read.viewmodel.Chapter


/**
 * 预加载辅助工具类
 * 
 * 提供预加载相关的通用逻辑：
 * 1. 预加载范围计算
 * 2. 章节边界检测
 * 3. 缓存清理策略
 * 4. 预加载优先级计算
 */
object PreloadHelper {
    
    // 预加载配置常量
    private const val MIN_PRELOAD_RANGE = 2  // 最小预加载范围：前后各2章
    private const val MAX_PRELOAD_RANGE = 4  // 最大预加载范围：前后各4章
    private const val MAX_CACHE_SIZE = 12    // 最大缓存大小
    
    /**
     * 计算预加载范围
     * 
     * @param currentIndex 当前章节索引
     * @param totalChapters 总章节数
     * @param triggerExpansion 是否触发范围扩展
     * @return 预加载范围 (startIndex, endIndex)
     */
    fun calculatePreloadRange(
        currentIndex: Int,
        totalChapters: Int,
        triggerExpansion: Boolean = false
    ): Pair<Int, Int> {
        val preloadRange = if (triggerExpansion) MAX_PRELOAD_RANGE else MIN_PRELOAD_RANGE
        
        val startIndex = (currentIndex - preloadRange).coerceAtLeast(0)
        val endIndex = (currentIndex + preloadRange).coerceAtMost(totalChapters - 1)
        
        return Pair(startIndex, endIndex)
    }
    
    /**
     * 检查是否接近章节边界
     * 
     * @param currentPageIndex 当前页面索引
     * @param totalPages 总页数
     * @param threshold 边界阈值（页数）
     * @return 是否接近边界
     */
    fun isNearChapterBoundary(
        currentPageIndex: Int,
        totalPages: Int,
        threshold: Int = 2
    ): Boolean {
        return currentPageIndex <= threshold || currentPageIndex >= totalPages - threshold - 1
    }
    
    /**
     * 检查是否需要预加载下一章
     * 
     * @param currentPageIndex 当前页面索引
     * @param totalPages 总页数
     * @param threshold 触发阈值
     * @return 是否需要预加载下一章
     */
    fun shouldPreloadNext(
        currentPageIndex: Int,
        totalPages: Int,
        threshold: Int = 3
    ): Boolean {
        return currentPageIndex >= totalPages - threshold
    }
    
    /**
     * 检查是否需要预加载上一章
     * 
     * @param currentPageIndex 当前页面索引
     * @param threshold 触发阈值
     * @return 是否需要预加载上一章
     */
    fun shouldPreloadPrevious(
        currentPageIndex: Int,
        threshold: Int = 3
    ): Boolean {
        return currentPageIndex <= threshold
    }
    
    /**
     * 获取需要预加载的章节索引列表
     * 
     * @param currentIndex 当前章节索引
     * @param chapterList 章节列表
     * @param preloadRange 预加载范围
     * @return 需要预加载的章节索引列表
     */
    fun getPreloadIndices(
        currentIndex: Int,
        chapterList: List<Chapter>,
        preloadRange: Int = MIN_PRELOAD_RANGE
    ): List<Int> {
        val indices = mutableListOf<Int>()
        
        // 添加前序章节
        for (i in 1..preloadRange) {
            val prevIndex = currentIndex - i
            if (prevIndex >= 0) {
                indices.add(prevIndex)
            }
        }
        
        // 添加后续章节
        for (i in 1..preloadRange) {
            val nextIndex = currentIndex + i
            if (nextIndex < chapterList.size) {
                indices.add(nextIndex)
            }
        }
        
        return indices
    }
    
    /**
     * 获取需要清理的章节ID列表
     * 
     * @param cachedChapterIds 已缓存的章节ID集合
     * @param keepIndices 需要保留的章节索引
     * @param chapterList 章节列表
     * @return 需要清理的章节ID列表
     */
    fun getChaptersToCleanup(
        cachedChapterIds: Collection<String>,
        keepIndices: List<Int>,
        chapterList: List<Chapter>
    ): List<String> {
        val keepChapterIds = keepIndices.mapNotNull { index ->
            chapterList.getOrNull(index)?.id
        }.toSet()
        
        return cachedChapterIds.filter { chapterId ->
            chapterId !in keepChapterIds
        }
    }
    
    /**
     * 计算预加载优先级
     * 
     * 距离当前章节越近，优先级越高
     * 
     * @param targetIndex 目标章节索引
     * @param currentIndex 当前章节索引
     * @return 优先级分数（越小优先级越高）
     */
    fun calculatePreloadPriority(targetIndex: Int, currentIndex: Int): Int {
        return kotlin.math.abs(targetIndex - currentIndex)
    }
    
    /**
     * 检查缓存是否超出限制
     * 
     * @param cacheSize 当前缓存大小
     * @return 是否需要清理缓存
     */
    fun shouldCleanupCache(cacheSize: Int): Boolean {
        return cacheSize > MAX_CACHE_SIZE
    }
    
    /**
     * 获取基础预加载范围的章节索引
     * 
     * @param currentIndex 当前章节索引
     * @param totalChapters 总章节数
     * @return 基础预加载范围的章节索引列表
     */
    fun getBasicPreloadIndices(currentIndex: Int, totalChapters: Int): List<Int> {
        val indices = mutableListOf<Int>()
        
        // 前后各2章
        for (offset in 1..MIN_PRELOAD_RANGE) {
            // 前序章节
            val prevIndex = currentIndex - offset
            if (prevIndex >= 0) {
                indices.add(prevIndex)
            }
            
            // 后续章节
            val nextIndex = currentIndex + offset
            if (nextIndex < totalChapters) {
                indices.add(nextIndex)
            }
        }
        
        return indices.sorted()
    }
} 
