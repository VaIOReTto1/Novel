package com.novel.page.read.components

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.novel.utils.wdp
import kotlin.math.roundToInt
import com.novel.utils.TimberLogger
import androidx.compose.ui.graphics.toArgb
import com.novel.page.read.viewmodel.BackgroundTheme
import com.novel.page.read.viewmodel.ReaderSettings
import kotlinx.collections.immutable.persistentListOf

/**
 * 阅读器设置面板组件
 * 
 * 提供完整的阅读器个性化设置界面，包括：
 * - 亮度调节：支持10档离散亮度设置，即时生效
 * - 字体大小：12-44sp范围调节，适配不同用户需求
 * - 背景主题：5种预设主题，优化不同环境下的阅读体验
 * - 翻页效果：多种动画模式，平衡视觉效果与性能
 * 
 * 所有设置变更都会实时预览并自动保存到本地
 * 
 * @param settings 当前阅读器设置状态
 * @param onSettingsChange 设置变更回调函数，传递新的设置对象
 * @param modifier 额外的修饰符，用于自定义布局和样式
 */
@SuppressLint("ContextCastToActivity")
@Composable
fun ReaderSettingsPanel(
    settings: ReaderSettings,
    onSettingsChange: (ReaderSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    // 预定义的背景主题列表，经过护眼性和可读性优化
    val backgroundThemes = persistentListOf(
        BackgroundTheme("护眼绿", Color(0xFFCCE8CC), Color(0xFF2E2E2E)),  // 护眼绿色，减缓视觉疲劳
        BackgroundTheme("经典白", Color(0xFFFFFFFF), Color(0xFF2E2E2E)),  // 经典白色，适合明亮环境
        BackgroundTheme("温暖黄", Color(0xFFF5F5DC), Color(0xFF2E2E2E)),  // 温暖米黄，最佳护眼效果
        BackgroundTheme("夜间黑", Color(0xFF1E1E1E), Color(0xFFE0E0E0)),  // 夜间模式，适合暗光环境
        BackgroundTheme("羊皮纸", Color(0xFFF4ECD8), Color(0xFF5D4E37))   // 复古羊皮纸，仿古书籍质感
    )

    // 1. 把外层的 brightness 存成一个 State，仍然是 0f..1f 之间离散档位
    var brightness by remember { mutableFloatStateOf(settings.brightness) }
    val activity = LocalContext.current as? ComponentActivity
    val window = activity?.window

    LaunchedEffect(brightness) {
        window?.attributes = window.attributes?.apply {
            screenBrightness = brightness.coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(brightness) {
        window?.attributes = window.attributes?.apply {
            screenBrightness = brightness.coerceIn(0f, 1f)
        }
    }

    // 设置面板
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = settings.backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(20.wdp),
            verticalArrangement = Arrangement.spacedBy(20.wdp)
        ) {
            // 第一行：亮度调节
            BrightnessControl(
                brightness = brightness,
                backgroundColor = settings.backgroundColor,
                onBrightnessChange = { raw ->
                    // 同样把 raw 量化到最近的档位
                    val stepCount = 10
                    val stepSize = 1f / stepCount
                    val stepped = (raw / stepSize).roundToInt() * stepSize
                    val clamped = stepped.coerceIn(0f, 1f)
                    // 把量化后的离散值直接存回外层 brightness
                    brightness = clamped
                    // 同步回传给 ReaderSettingsPanel 的 settings
                    onSettingsChange(settings.copy(brightness = clamped))
                }
            )

            // 第二行：字体大小调节
            FontSizeControl(
                fontSize = settings.fontSize,
                onFontSizeChange = { fontSize ->
                    onSettingsChange(settings.copy(fontSize = fontSize))
                }
            )

            // 第三行：背景颜色选择
            BackgroundColorControl(
                backgroundThemes = backgroundThemes,
                currentBackgroundColor = settings.backgroundColor,
                onThemeChange = { theme ->
                    onSettingsChange(
                        settings.copy(
                            backgroundColor = theme.backgroundColor,
                            textColor = theme.textColor
                        )
                    )
                }
            )

            // 第四行：翻页效果选择
            PageFlipEffectControl(
                currentEffect = settings.pageFlipEffect,
                backgroundColor = settings.backgroundColor,
                onEffectChange = { effect ->
                    onSettingsChange(settings.copy(pageFlipEffect = effect))
                }
            )
        }
    }
}
