package com.novel.page.read.service.settings

import androidx.compose.ui.graphics.toArgb
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.service.common.ReaderServiceConfig
import com.novel.page.read.viewmodel.PageFlipEffect
import com.novel.page.read.viewmodel.ReaderSettings

/**
 * 设置保存器
 * 
 * 负责将ReaderSettings序列化保存到UserDefaults：
 * - 翻页效果保存
 * - 字体大小保存
 * - 亮度保存  
 * - 颜色设置保存（背景色、文字色）
 * - 异常安全处理
 */
class SettingsSaver @javax.inject.Inject constructor(
    private val readerSettingsStorage: ReaderSettingsStorage,
    private val logger: ServiceLogger
) {
    
    companion object {
        private const val TAG = "SettingsSaver"
    }

    /**
     * 保存完整的阅读器设置
     */
    fun saveSettings(settings: ReaderSettings) {
        logger.logDebug("开始保存阅读器设置", TAG)
        logger.logDebug("保存设置详情: 字体=${settings.fontSize}sp, 亮度=${(settings.brightness * 100).toInt()}%, " +
                "背景色=${colorToHex(settings.backgroundColor)}, 文字色=${colorToHex(settings.textColor)}, " +
                "翻页效果=${settings.pageFlipEffect}", TAG)
        
        try {
            savePageFlipEffect(settings.pageFlipEffect)
            saveFontSize(settings.fontSize)
            saveBrightness(settings.brightness)
            saveBackgroundColor(settings.backgroundColor)
            saveTextColor(settings.textColor)
            readerSettingsStorage.mirrorSettings(settings)
            
            logger.logInfo("所有设置保存完成", TAG)
        } catch (e: Exception) {
            logger.logError("设置保存失败", e, TAG)
        }
    }

    /**
     * 保存翻页效果设置
     */
    fun savePageFlipEffect(pageFlipEffect: PageFlipEffect) {
        try {
            readerSettingsStorage.setPageFlipEffect(pageFlipEffect.name)
            logger.logDebug("翻页效果保存成功: ${pageFlipEffect.name}", TAG)
        } catch (e: Exception) {
            logger.logError("翻页效果保存失败", e, TAG)
        }
    }

    /**
     * 保存字体大小设置
     */
    fun saveFontSize(fontSize: Int) {
        try {
            if (fontSize in ReaderServiceConfig.MIN_FONT_SIZE..ReaderServiceConfig.MAX_FONT_SIZE) {
                readerSettingsStorage.setFontSize(fontSize)
                logger.logDebug("字体大小保存成功: ${fontSize}sp", TAG)
            } else {
                logger.logWarning("字体大小超出范围: ${fontSize}sp, 保存默认值", TAG)
                readerSettingsStorage.setFontSize(ReaderServiceConfig.DEFAULT_FONT_SIZE)
            }
        } catch (e: Exception) {
            logger.logError("字体大小保存失败", e, TAG)
        }
    }

    /**
     * 保存亮度设置
     */
    fun saveBrightness(brightness: Float) {
        try {
            val clampedBrightness = brightness.coerceIn(
                ReaderServiceConfig.MIN_BRIGHTNESS, 
                ReaderServiceConfig.MAX_BRIGHTNESS
            )
            readerSettingsStorage.setBrightness(clampedBrightness)
            logger.logDebug("亮度保存成功: ${(clampedBrightness * 100).toInt()}%", TAG)
        } catch (e: Exception) {
            logger.logError("亮度保存失败", e, TAG)
        }
    }

    /**
     * 保存背景颜色设置
     */
    fun saveBackgroundColor(backgroundColor: androidx.compose.ui.graphics.Color) {
        try {
            val colorString = formatColor(backgroundColor)
            readerSettingsStorage.setBackgroundColor(colorString)
            logger.logDebug("背景颜色保存成功: ${colorToHex(backgroundColor)} -> $colorString", TAG)
        } catch (e: Exception) {
            logger.logError("背景颜色保存失败，使用默认值", e, TAG)
            readerSettingsStorage.setBackgroundColor(ReaderServiceConfig.DEFAULT_BACKGROUND_COLOR_STRING)
        }
    }

    /**
     * 保存文字颜色设置
     */
    fun saveTextColor(textColor: androidx.compose.ui.graphics.Color) {
        try {
            val colorString = formatColor(textColor)
            readerSettingsStorage.setTextColor(colorString)
            logger.logDebug("文字颜色保存成功: ${colorToHex(textColor)} -> $colorString", TAG)
        } catch (e: Exception) {
            logger.logError("文字颜色保存失败，使用默认值", e, TAG)
            readerSettingsStorage.setTextColor(ReaderServiceConfig.DEFAULT_TEXT_COLOR_STRING)
        }
    }

    /**
     * 格式化颜色为十六进制字符串
     */
    private fun formatColor(color: androidx.compose.ui.graphics.Color): String {
        val colorInt = color.toArgb()
        return String.format("#%08X", colorInt)
    }

    /**
     * 将Color对象转换为十六进制字符串，用于日志显示
     */
    private fun colorToHex(color: androidx.compose.ui.graphics.Color): String {
        return try {
            String.format("#%08X", color.toArgb())
        } catch (e: Exception) {
            "INVALID_COLOR"
        }
    }
} 
