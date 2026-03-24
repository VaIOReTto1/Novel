package com.novel.page.read.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.novel.core.logging.CoreLogger

@Stable
data class ReaderSettings(
    val brightness: Float = 0.5f,
    val fontSize: Int = 16,
    val backgroundColor: Color = Color(0xFFF5F5DC),
    val textColor: Color = Color(0xFF2E2E2E),
    val pageFlipEffect: PageFlipEffect = PageFlipEffect.PAGECURL
) {
    companion object {
        fun getDefault(): ReaderSettings {
            val defaultSettings = ReaderSettings(
                brightness = 0.5f,
                fontSize = 16,
                backgroundColor = Color(0xFFF5F5DC),
                textColor = Color(0xFF2E2E2E),
                pageFlipEffect = PageFlipEffect.PAGECURL
            )

            CoreLogger.d("ReaderSettings", "创建默认设置:")
            CoreLogger.d("ReaderSettings", "  - 字体大小: ${defaultSettings.fontSize}sp")
            CoreLogger.d("ReaderSettings", "  - 亮度: ${(defaultSettings.brightness * 100).toInt()}%")
            CoreLogger.d("ReaderSettings", "  - 背景颜色: ${String.format("#%08X", defaultSettings.backgroundColor.toArgb())}")
            CoreLogger.d("ReaderSettings", "  - 文字颜色: ${String.format("#%08X", defaultSettings.textColor.toArgb())}")
            CoreLogger.d("ReaderSettings", "  - 翻页效果: ${defaultSettings.pageFlipEffect}")

            return defaultSettings
        }
    }
}
