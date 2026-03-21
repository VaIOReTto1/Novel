package com.novel.page.home.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomePerformanceOptimizerTest {

    @Before
    fun setUp() {
        HomePerformanceOptimizer.imageHeightCache.clear()
    }

    @Test
    fun getOptimizedImageHeight_reusesCachedValueForSameBook() {
        val firstHeight = HomePerformanceOptimizer.getOptimizedImageHeight(
            bookId = "book-1",
            minHeight = 220,
            maxHeight = 240,
        )

        val secondHeight = HomePerformanceOptimizer.getOptimizedImageHeight(
            bookId = "book-1",
            minHeight = 220,
            maxHeight = 240,
        )

        assertEquals(firstHeight, secondHeight)
        assertTrue(firstHeight in 220..240)
    }

    @Test
    fun getOptimizedImageHeight_clampsUnsafeBounds() {
        val height = HomePerformanceOptimizer.getOptimizedImageHeight(
            bookId = "book-2",
            minHeight = 10,
            maxHeight = 1000,
        )

        assertTrue(height in 100..500)
    }
}
