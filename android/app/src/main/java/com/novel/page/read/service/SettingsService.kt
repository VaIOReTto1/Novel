package com.novel.page.read.service

import androidx.compose.ui.graphics.toArgb
import java.util.Locale
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.SafeService
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.service.settings.SettingsParser
import com.novel.page.read.service.settings.SettingsSaver
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.ReaderSettings
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设置服务类 - 重构优化版
 * 
 * 负责阅读器设置的持久化存储和读取：
 * 1. 设置加载和保存的统一入口
 * 2. 委托给SettingsParser和SettingsSaver处理具体逻辑
 * 3. 统一异步调度和错误处理
 * 4. 性能监控和结构化日志
 * 
 * 优化特性：
 * - 继承SafeService统一异步调度和错误处理
 * - 职责分离：解析、验证、保存逻辑下沉到策略类
 * - 统一配置管理
 * - 结构化日志记录
 * - 支持性能监控
 */
@Singleton
class SettingsService @Inject constructor(
    private val settingsParser: SettingsParser,
    private val settingsSaver: SettingsSaver,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : SafeService(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.SETTINGS_SERVICE
    }
    
    override fun getServiceTag(): String = TAG
    
    /**
     * 加载阅读器设置
     * 
     * 从UserDefaults中恢复所有设置项，包含完整的容错处理
     * @return 完整的阅读器设置对象
     */
    fun loadSettings(): ReaderSettings {
        return withSyncPerformanceMonitoring("loadSettings") {
            logger.logDebug("开始加载阅读器设置", TAG)
            
            try {
                val defaultSettings = ReaderSettings.getDefault()
                logger.logDebug("默认设置初始化完成", TAG)
                
                // 使用策略类解析各项设置
                val pageFlipEffect = settingsParser.parsePageFlipEffect(defaultSettings.pageFlipEffect)
                val fontSize = settingsParser.parseFontSize(defaultSettings.fontSize)
                val brightness = settingsParser.parseBrightness(defaultSettings.brightness)
                val backgroundColor = settingsParser.parseBackgroundColor(defaultSettings.backgroundColor)
                val textColor = settingsParser.parseTextColor(defaultSettings.textColor)
                
                // 验证并修复颜色设置
                val (validBackgroundColor, validTextColor) = settingsParser.validateAndFixColors(backgroundColor, textColor)
                
                val loadedSettings = ReaderSettings(
                    pageFlipEffect = pageFlipEffect,
                    fontSize = fontSize,
                    brightness = brightness,
                    backgroundColor = validBackgroundColor,
                    textColor = validTextColor
                )
                
                logger.logInfo("设置加载完成: 字体=${loadedSettings.fontSize}sp, " +
                    "亮度=${(loadedSettings.brightness * 100).toInt()}%, " +
                    "背景色=${colorToHex(loadedSettings.backgroundColor)}, " +
                    "文字色=${colorToHex(loadedSettings.textColor)}, " +
                    "翻页效果=${loadedSettings.pageFlipEffect}", TAG)
                
                loadedSettings
                
            } catch (e: Exception) {
                logger.logError("设置加载失败，使用默认设置", e, TAG)
                val defaultSettings = ReaderSettings.getDefault()
                logger.logDebug("返回默认设置: 字体=${defaultSettings.fontSize}sp, " +
                    "背景色=${colorToHex(defaultSettings.backgroundColor)}", TAG)
                defaultSettings
            }
        }
    }

    /**
     * 保存阅读器设置
     * 
     * 将所有设置项序列化保存到UserDefaults
     * @param settings 要保存的设置对象
     */
    fun saveSettings(settings: ReaderSettings) {
        withSyncPerformanceMonitoring("saveSettings") {
            logger.logDebug("开始保存阅读器设置", TAG)
            logger.logDebug("保存设置详情: 字体=${settings.fontSize}sp, " +
                "亮度=${(settings.brightness * 100).toInt()}%, " +
                "背景色=${colorToHex(settings.backgroundColor)}, " +
                "文字色=${colorToHex(settings.textColor)}, " +
                "翻页效果=${settings.pageFlipEffect}", TAG)
            
            // 委托给设置保存器处理
            settingsSaver.saveSettings(settings)
            
            logger.logInfo("阅读器设置保存完成", TAG)
        }
    }

    /**
     * 将Color对象转换为十六进制字符串，用于日志显示
     */
    private fun colorToHex(color: androidx.compose.ui.graphics.Color): String {
        return try {
            String.format(Locale.US, "#%08X", color.toArgb())
        } catch (e: Exception) {
            "INVALID_COLOR"
        }
    }
}
