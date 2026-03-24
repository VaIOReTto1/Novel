package com.novel.page.read.service.common

import androidx.compose.ui.graphics.Color
import com.novel.page.read.viewmodel.PageFlipEffect

object ReaderServiceConfig {

    const val MAX_SESSION_CACHE_SIZE = 12

    const val MIN_FONT_SIZE = 12
    const val MAX_FONT_SIZE = 44
    const val DEFAULT_FONT_SIZE = 16

    const val MIN_BRIGHTNESS = 0f
    const val MAX_BRIGHTNESS = 1f
    const val DEFAULT_BRIGHTNESS = 0.5f

    const val FIRST_PAGE_RESERVE_LINES = 2
    const val LINE_SPACING_MULTIPLIER = 1.5f
    const val PAGE_PADDING_DP = 16
    const val DEFAULT_PAGE_COUNT = 5

    const val FLIP_COOLDOWN_MS = 300L
    const val PROGRESS_UPDATE_INTERVAL_MS = 1000L
    const val RETRY_DELAY_MS = 1500L
    const val MAX_RETRY_COUNT = 2

    val DEFAULT_BACKGROUND_COLOR = Color(0xFFF5F5DC)
    val DEFAULT_TEXT_COLOR = Color(0xFF2E2E2E)
    val DEFAULT_PAGE_FLIP_EFFECT = PageFlipEffect.PAGECURL
    const val DEFAULT_BACKGROUND_COLOR_STRING = "#FFF5F5DC"
    const val DEFAULT_TEXT_COLOR_STRING = "#FF2E2E2E"

    const val MIN_COLOR_CONTRAST = 2.5f
    const val MIN_ALPHA = 0.1f
    const val BRIGHTNESS_THRESHOLD = 0.5f

    const val ENABLE_VERBOSE_LOGGING = true
    const val ENABLE_PERFORMANCE_LOGGING = false
    const val LOG_TAG_PREFIX = "ReaderService"

    const val MAX_IO_CONCURRENCY = 4
    const val MAX_CHAPTER_FETCH_CONCURRENCY = 2
    const val PAGINATION_BATCH_SIZE = 5
    const val CACHE_OPERATION_TIMEOUT_MS = 1000L
}
