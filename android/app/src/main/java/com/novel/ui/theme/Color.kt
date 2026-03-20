package com.novel.ui.theme

import android.util.Log
import com.novel.utils.TimberLogger
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import timber.log.Timber

/**
 * 小说应用主题色彩系统
 * 
 * 提供统一的颜色定义和动态主题切换功能
 * 支持深色模式和用户自定义主题设置
 */
object NovelColors {

    /** 应用主色调 - 橙色 */
    val NovelMain: Color
        @Composable get() = Color(0xFFFF995D)

    /** 应用主色调 - 偏红橙色 */
    val NovelMainLight: Color
        @Composable get() = Color(0xFFF86827)

    /** 阅读页面背景色 - 护眼米黄色 */
    val NovelBookBackground: Color
        @Composable get() = dynamicColor(light = Color(0xFFE8E3CF), dark = Color(0xFFE8E3CF))

    /** 分割线颜色 */
    val NovelDivider: Color
        @Composable get() = dynamicColor(light = Color(0xFFF7F7F8), dark = Color(0xFF1C1C1E))

    /** 次要背景颜色 - 用于卡片、面板等 */
    val NovelSecondaryBackground: Color
        @Composable get() = dynamicColor(light = Color(0xFFF7F7F8), dark = Color(0xFF1C1C1E))

    /** 主要背景颜色 */
    val NovelBackground: Color
        @Composable get() = dynamicColor(light = Color(0xFFFFFFFF), dark = Color(0xFF000000))

    /** 次要文本颜色 - 灰色 */
    val NovelTextGray: Color
        @Composable get() = dynamicColor(light = Color(0xFF7F7F7F), dark = Color(0xFF97989F))

    /** 主要文本颜色 */
    val NovelText: Color
        @Composable get() = dynamicColor(light = Color(0xFF000000), dark = Color(0xFFFFFFFF))

    /** 浅灰色 - 用于边框、背景等 */
    val NovelLightGray: Color
        @Composable get() = dynamicColor(light = Color(0xFFDDDDDD), dark = Color(0xFF1C1C1E))

    /** 标签背景色 */
    val NovelChipBackground: Color
        @Composable get() = dynamicColor(light = Color(0xFFEBEDF0), dark = Color(0xFF23242B))

    /** 错误提示色 */
    val NovelError: Color
        @Composable get() = dynamicColor(light = Color(0xFFFF995D), dark = Color(0xFFFF0000))
}

/**
 * 动态颜色选择器
 * 
 * 根据用户设置的主题模式（深色/浅色）和系统主题跟随设置
 * 自动选择合适的颜色值
 * 
 * @param light 浅色模式下的颜色
 * @param dark 深色模式下的颜色
 * @return 当前主题下应使用的颜色
 */
@Composable
fun dynamicColor(light: Color, dark: Color): Color {
    val TAG = "NovelColors"
    val themeManager = ThemeManager.getInstance()
    val isDarkMode by themeManager.isDarkMode.collectAsState()
    val followSystemTheme by themeManager.followSystemTheme.collectAsState()
    
    // 根据设置确定实际的主题模式
    val actualDarkMode = if (followSystemTheme) {
        // 当跟随系统时，直接使用系统主题
        isSystemInDarkTheme()
    } else {
        // 当不跟随系统时，使用用户手动设置的主题模式
        isDarkMode
    }
    
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
        TimberLogger.v(TAG, "主题模式: 深色=$actualDarkMode, 跟随系统=$followSystemTheme, 手动设置深色=$isDarkMode")
    }
    
    return if (actualDarkMode) dark else light
}
