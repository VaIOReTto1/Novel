package com.novel.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 统一定义颜色方案
@Composable
private fun colorScheme(isDarkTheme: Boolean) = if (isDarkTheme) {
    darkColorScheme(
        primary = NovelColors.NovelMain,
        secondary = NovelColors.NovelMainLight,
        background = NovelColors.NovelBackground,
        surface = NovelColors.NovelSecondaryBackground,
        onPrimary = NovelColors.NovelText,
        onSecondary = NovelColors.NovelTextGray,
        onBackground = NovelColors.NovelLightGray,
        onSurface = NovelColors.NovelLightGray
    )
} else {
    lightColorScheme(
        primary = NovelColors.NovelMain,
        secondary = NovelColors.NovelMainLight,
        background = NovelColors.NovelBackground,
        surface = NovelColors.NovelSecondaryBackground,
        onPrimary = NovelColors.NovelText,
        onSecondary = NovelColors.NovelTextGray,
        onBackground = NovelColors.NovelDivider,
        onSurface = NovelColors.NovelLightGray
    )
}

/**
 * 应用主题适配
 * @param darkTheme 是否为深色模式
 * @param content Composable 内容
 */
@Composable
fun NovelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val currentColorScheme = colorScheme(darkTheme)

    // 状态栏颜色适配
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = currentColorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = currentColorScheme,
        typography = Typography,
        content = content
    )
}
