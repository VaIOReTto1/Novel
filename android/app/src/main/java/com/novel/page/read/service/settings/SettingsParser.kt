package com.novel.page.read.service.settings

import androidx.compose.ui.graphics.Color
import java.util.Locale
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.service.common.ReaderServiceConfig
import com.novel.page.read.viewmodel.PageFlipEffect

/**
 * 设置解析器
 * 
 * 负责从UserDefaults中解析和验证各种设置项：
 * - 翻页效果设置
 * - 字体大小设置
 * - 亮度设置
 * - 颜色设置（背景色、文字色）
 * - 设置项验证和修复
 */
class SettingsParser @javax.inject.Inject constructor(
    private val readerSettingsStorage: ReaderSettingsStorage,
    private val logger: ServiceLogger
) {
    
    companion object {
        private const val TAG = "SettingsParser"
    }

    /**
     * 解析翻页效果设置
     */
    fun parsePageFlipEffect(defaultValue: PageFlipEffect = ReaderServiceConfig.DEFAULT_PAGE_FLIP_EFFECT): PageFlipEffect {
        return readerSettingsStorage.getPageFlipEffect()?.let { savedEffect ->
            try {
                val effect = PageFlipEffect.valueOf(savedEffect)
                logger.logDebug("翻页效果设置解析成功: $savedEffect", TAG)
                effect
            } catch (e: Exception) {
                logger.logError("翻页效果设置解析失败: $savedEffect, 使用默认值", e, TAG)
                defaultValue
            }
        } ?: run {
            logger.logDebug("翻页效果设置未找到，使用默认值: $defaultValue", TAG)
            defaultValue
        }
    }

    /**
     * 解析字体大小设置
     */
    fun parseFontSize(defaultValue: Int = ReaderServiceConfig.DEFAULT_FONT_SIZE): Int {
        return readerSettingsStorage.getFontSize()?.let { fontSize ->
            if (fontSize in ReaderServiceConfig.MIN_FONT_SIZE..ReaderServiceConfig.MAX_FONT_SIZE) {
                logger.logDebug("字体大小设置解析成功: ${fontSize}sp", TAG)
                fontSize
            } else {
                logger.logWarning("字体大小超出范围: ${fontSize}sp, 使用默认值: ${defaultValue}sp", TAG)
                defaultValue
            }
        } ?: run {
            logger.logDebug("字体大小设置未找到，使用默认值: ${defaultValue}sp", TAG)
            defaultValue
        }
    }

    /**
     * 解析亮度设置
     */
    fun parseBrightness(defaultValue: Float = ReaderServiceConfig.DEFAULT_BRIGHTNESS): Float {
        return readerSettingsStorage.getBrightness()?.let { brightness ->
            if (brightness in ReaderServiceConfig.MIN_BRIGHTNESS..ReaderServiceConfig.MAX_BRIGHTNESS) {
                logger.logDebug("亮度设置解析成功: ${(brightness * 100).toInt()}%", TAG)
                brightness
            } else {
                logger.logWarning("亮度值超出范围: $brightness, 使用默认值: ${(defaultValue * 100).toInt()}%", TAG)
                defaultValue
            }
        } ?: run {
            logger.logDebug("亮度设置未找到，使用默认值: ${(defaultValue * 100).toInt()}%", TAG)
            defaultValue
        }
    }

    /**
     * 解析背景颜色设置
     */
    fun parseBackgroundColor(defaultValue: Color = ReaderServiceConfig.DEFAULT_BACKGROUND_COLOR): Color {
        return readerSettingsStorage.getBackgroundColor()?.let { colorString ->
            logger.logDebug("尝试解析背景颜色: $colorString", TAG)
            parseColor(colorString, "背景颜色", defaultValue)
        } ?: run {
            logger.logDebug("背景颜色设置未找到，使用默认值: ${colorToHex(defaultValue)}", TAG)
            defaultValue
        }
    }

    /**
     * 解析文字颜色设置
     */
    fun parseTextColor(defaultValue: Color = ReaderServiceConfig.DEFAULT_TEXT_COLOR): Color {
        return readerSettingsStorage.getTextColor()?.let { colorString ->
            logger.logDebug("尝试解析文字颜色: $colorString", TAG)
            parseColor(colorString, "文字颜色", defaultValue)
        } ?: run {
            logger.logDebug("文字颜色设置未找到，使用默认值: ${colorToHex(defaultValue)}", TAG)
            defaultValue
        }
    }

    /**
     * 解析颜色字符串
     */
    private fun parseColor(colorString: String, colorType: String, defaultValue: Color): Color {
        return try {
            if (colorString.isNotBlank() && colorString.startsWith("#") && 
                (colorString.length == 7 || colorString.length == 9)) {
                
                val colorHex = if (colorString.length == 7) {
                    "FF${colorString.substring(1)}"
                } else {
                    colorString.substring(1)
                }
                
                val colorInt = colorHex.toLong(16).toInt()
                val color = Color(colorInt)
                
                logger.logDebug("$colorType 解析成功: $colorString -> ${colorToHex(color)}", TAG)
                color
            } else {
                logger.logWarning("$colorType 格式无效: $colorString, 使用默认值: ${colorToHex(defaultValue)}", TAG)
                defaultValue
            }
        } catch (e: Exception) {
            logger.logError("$colorType 解析失败: $colorString, 使用默认值", e, TAG)
            defaultValue
        }
    }

    /**
     * 验证并修复颜色设置
     * 确保背景色和文字色有效且对比度充足
     */
    fun validateAndFixColors(backgroundColor: Color, textColor: Color): Pair<Color, Color> {
        var validBackgroundColor = backgroundColor
        var validTextColor = textColor

        // 1. 修复无效的透明颜色
        if (backgroundColor.alpha < ReaderServiceConfig.MIN_ALPHA) {
            logger.logWarning("检测到无效的背景颜色 (alpha=${backgroundColor.alpha})，重置为默认值", TAG)
            validBackgroundColor = ReaderServiceConfig.DEFAULT_BACKGROUND_COLOR
        }
        
        if (textColor.alpha < ReaderServiceConfig.MIN_ALPHA) {
            logger.logWarning("检测到无效的文字颜色 (alpha=${textColor.alpha})，重置为默认值", TAG)
            validTextColor = ReaderServiceConfig.DEFAULT_TEXT_COLOR
        }

        // 2. 确保对比度充足
        val contrast = ColorUtils.calculateContrast(validTextColor.toArgb(), validBackgroundColor.toArgb())
        logger.logDebug("颜色对比度检查: ${String.format(Locale.US, "%.2f", contrast)}", TAG)
        
        if (contrast < ReaderServiceConfig.MIN_COLOR_CONTRAST) {
            logger.logWarning("颜色对比度不足 (${String.format(Locale.US, "%.2f", contrast)})，重置文字颜色", TAG)
            // 基于背景亮度选择高对比度的文字颜色
            validTextColor = if (validBackgroundColor.luminance() > ReaderServiceConfig.BRIGHTNESS_THRESHOLD) {
                ReaderServiceConfig.DEFAULT_TEXT_COLOR
            } else {
                Color.White
            }
            logger.logDebug("根据背景亮度 (${validBackgroundColor.luminance()})，已将文字颜色调整为: ${colorToHex(validTextColor)}", TAG)
        }

        return Pair(validBackgroundColor, validTextColor)
    }

    /**
     * 将Color对象转换为十六进制字符串，用于日志显示
     */
    private fun colorToHex(color: Color): String {
        return try {
            String.format(Locale.US, "#%08X", color.toArgb())
        } catch (e: Exception) {
            "INVALID_COLOR"
        }
    }
} 
