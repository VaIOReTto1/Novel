package com.novel.page.home.utils

import androidx.compose.runtime.Stable
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps per-book image heights stable so staggered home grids do not jump
 * between recompositions.
 */
@Stable
object HomePerformanceOptimizer {

    @Stable
    @Volatile
    var imageHeightCache: MutableMap<String, Int> = ConcurrentHashMap()

    fun getOptimizedImageHeight(bookId: String, minHeight: Int = 280, maxHeight: Int = 330): Int {
        val safeMinHeight = minHeight.coerceAtLeast(100)
        val safeMaxHeight = maxHeight.coerceAtMost(500).coerceAtLeast(safeMinHeight)

        return imageHeightCache.getOrPut(bookId) {
            (safeMinHeight..safeMaxHeight).random()
        }
    }

    fun clearHeightCache() {
        imageHeightCache.clear()
    }

    fun getCacheSize(): Int {
        return imageHeightCache.size
    }
}
