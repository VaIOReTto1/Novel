package com.novel.page.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.novel.ui.theme.PingFangFamily

/**
 * 自定义文本组件
 * 
 * 功能：
 * - 统一使用PingFang字体
 * - 默认省略号溢出处理
 * - 主题色适配
 * 
 * @param text 显示文本
 * @param modifier 修饰符
 * @param color 文字颜色
 * @param fontSize 字体大小
 * @param fontStyle 字体样式
 * @param fontWeight 字体粗细
 * @param letterSpacing 字符间距
 * @param textDecoration 文本装饰
 * @param textAlign 文本对齐
 * @param lineHeight 行高
 * @param overflow 溢出处理
 * @param softWrap 软换行
 * @param maxLines 最大行数
 * @param minLines 最小行数
 * @param onTextLayout 文本布局回调
 * @param style 文本样式
 */
@Composable
fun NovelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        modifier = modifier,
        fontFamily = PingFangFamily,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}
