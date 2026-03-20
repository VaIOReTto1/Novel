package com.novel.page.read.service.common

import androidx.compose.ui.graphics.Color
import com.novel.page.read.viewmodel.PageFlipEffect

/**
 * 阅读器服务统一配置
 * 
 * 将各Service中的硬编码常量统一管理：
 * - 缓存相关配置
 * - 分页相关参数
 * - 性能相关设置
 * - 默认值配置
 */
object ReaderServiceConfig {
    
    // ==================== 缓存配置 ====================
    
    /** 章节会话缓存最大大小 */
    const val MAX_SESSION_CACHE_SIZE = 12
    
    /** 默认字体大小范围 */
    const val MIN_FONT_SIZE = 12
    const val MAX_FONT_SIZE = 44
    const val DEFAULT_FONT_SIZE = 16
    
    /** 亮度范围 */
    const val MIN_BRIGHTNESS = 0f
    const val MAX_BRIGHTNESS = 1f
    const val DEFAULT_BRIGHTNESS = 0.5f
    
    // ==================== 分页配置 ====================
    
    /** 分页器首页预留行数 */
    const val FIRST_PAGE_RESERVE_LINES = 2
    
    /** 行间距倍数 */
    const val LINE_SPACING_MULTIPLIER = 1.5f
    
    /** 页面边距（dp） */
    const val PAGE_PADDING_DP = 16
    
    /** 默认页数（当分页计算失败时使用） */
    const val DEFAULT_PAGE_COUNT = 5
    
    // ==================== 性能配置 ====================
    
    /** 翻页防抖时间（毫秒） */
    const val FLIP_COOLDOWN_MS = 300L
    
    /** 进度更新间隔（毫秒） */
    const val PROGRESS_UPDATE_INTERVAL_MS = 1000L
    
    /** 重试延迟时间（毫秒） */
    const val RETRY_DELAY_MS = 1500L
    
    /** 最大重试次数 */
    const val MAX_RETRY_COUNT = 2
    
    // ==================== 默认值配置 ====================
    
    /** 默认背景颜色 */
    val DEFAULT_BACKGROUND_COLOR = Color(0xFFF5F5DC) // 米色
    
    /** 默认文字颜色 */
    val DEFAULT_TEXT_COLOR = Color(0xFF2E2E2E) // 深灰色
    
    /** 默认翻页效果 */
    val DEFAULT_PAGE_FLIP_EFFECT = PageFlipEffect.PAGECURL
    
    /** 默认背景颜色字符串 */
    const val DEFAULT_BACKGROUND_COLOR_STRING = "#FFF5F5DC"
    
    /** 默认文字颜色字符串 */
    const val DEFAULT_TEXT_COLOR_STRING = "#FF2E2E2E"
    
    // ==================== 颜色验证配置 ====================
    
    /** 最小对比度要求 */
    const val MIN_COLOR_CONTRAST = 2.5f
    
    /** 最小透明度要求 */
    const val MIN_ALPHA = 0.1f
    
    /** 亮度阈值（用于自动选择文字颜色） */
    const val BRIGHTNESS_THRESHOLD = 0.5f
    
    // ==================== 日志配置 ====================
    
    /** 是否启用详细日志 */
    const val ENABLE_VERBOSE_LOGGING = true // 生产环境关闭详细日志
    
    /** 是否启用性能日志 */
    const val ENABLE_PERFORMANCE_LOGGING = false // 生产环境关闭性能日志，避免ANR
    
    /** 日志标签前缀 */
    const val LOG_TAG_PREFIX = "ReaderService"
    
    // ==================== 并发控制配置 ====================
    
    /** IO线程池最大并发数 */
    const val MAX_IO_CONCURRENCY = 4
    
    /** 章节内容获取最大并发数 */
    const val MAX_CHAPTER_FETCH_CONCURRENCY = 2
    
    /** 分页计算分批大小 */
    const val PAGINATION_BATCH_SIZE = 5
    
    /** 缓存操作超时时间（毫秒） */
    const val CACHE_OPERATION_TIMEOUT_MS = 1000L
} 
