package com.novel.page.home.utils

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import com.novel.utils.TimberLogger
import java.util.concurrent.ConcurrentHashMap

/**
 * 首页性能优化工具类
 *
 * 核心功能：
 * - 图片高度缓存：避免重复计算随机高度，提升列表性能
 * - 瀑布流优化：为不同书籍生成不同高度，营造丰富视觉效果
 * - 内存管理：使用Map缓存减少重复计算开销
 * - 安全检查：确保高度值在合理范围内，避免UI异常
 *
 * 性能优化策略：
 * - 一次计算，多次使用：同一书籍的高度保持一致
 * - 懒加载缓存：只在需要时计算并存储
 * - 参数验证：防止异常参数导致UI问题
 * - 内存友好：使用轻量级Map存储，避免内存泄漏
 *
 * 使用场景：
 * - 首页推荐书籍列表的瀑布流布局
 * - 书架页面的网格布局优化
 * - 任何需要动态高度的图片展示场景
 */
@Stable
object HomePerformanceOptimizer {

    private const val TAG = "HomePerformanceOptimizer"

    /**
     * 图片高度缓存
     *
     * 存储结构：bookId -> height
     * 避免重复计算随机高度，确保同一书籍显示高度的一致性
     * 使用 ConcurrentHashMap 替代 mutableStateMapOf 提高稳定性和线程安全性
     */
    @Stable
    @Volatile
    var imageHeightCache: MutableMap<String, Int> = ConcurrentHashMap()

    /**
     * 获取优化后的图片高度
     *
     * 优化逻辑：
     * 1. 首先检查缓存中是否存在该书籍的高度
     * 2. 如果不存在，生成随机高度并缓存
     * 3. 对输入参数进行安全验证
     * 4. 确保生成的高度在合理范围内
     *
     * @param bookId 书籍唯一标识符
     * @param minHeight 最小高度，默认280px
     * @param maxHeight 最大高度，默认330px
     * @return 优化后的图片高度值
     */
    fun getOptimizedImageHeight(bookId: String, minHeight: Int = 280, maxHeight: Int = 330): Int {
        // 安全检查：验证高度参数
        val safeMinHeight = minHeight.coerceAtLeast(100)
        val safeMaxHeight = maxHeight.coerceAtMost(500).coerceAtLeast(safeMinHeight)

        return imageHeightCache.getOrPut(bookId) {
            val height = (safeMinHeight..safeMaxHeight).random()
            height
        }
    }

    /**
     * 清理高度缓存
     *
     * 用于内存管理，在适当时机清理不再需要的缓存数据
     * 建议在用户离开首页或内存压力较大时调用
     */
    fun clearHeightCache() {
        TimberLogger.d(TAG, "清理高度缓存，缓存项数量: ${imageHeightCache.size}")
        imageHeightCache.clear()
        TimberLogger.d(TAG, "高度缓存清理完成")
    }

    /**
     * 获取缓存统计信息
     *
     * @return 当前缓存的书籍数量
     */
    fun getCacheSize(): Int {
        return imageHeightCache.size.also {
            TimberLogger.d(TAG, "当前缓存大小: $it")
        }
    }
}
