package com.novel.page.book.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.ui.theme.PingFangFamily
import com.novel.utils.HtmlTextUtil
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 布局信息数据类 - 缓存计算结果
 * 使用 @Stable 注解提升 Compose 稳定性
 */
@Stable
private data class LayoutInfo(
    val showExpand: Boolean,
    val firstLine: String, 
    val restAll: String
)

/**
 * 稳定的文本样式常量 - 避免每次重组都创建
 */
@Stable
private val DescriptionTextStyle = TextStyle(
    fontSize = 14.ssp,
    fontFamily = PingFangFamily
)

/**
 * 书籍简介组件 - 性能优化版本
 * 
 * 功能特性：
 * - 自动检测简介内容长度，超过2行时显示"更多"按钮
 * - 点击"更多"按钮弹出底部弹窗展示完整简介
 * - 弹窗具有动画效果，可拖拽关闭
 * - 支持HTML标签清理和文本格式化
 * - 性能优化：缓存计算结果，减少重组
 * - MVI集成：支持外部状态管理
 * 
 * 性能优化改进：
 * - 缓存 AnnotatedString 创建，避免重复对象分配
 * - 使用 @Stable 注解提升参数稳定性
 * - 优化文本测量逻辑，减少重复计算
 * - 静态文本样式常量，避免每次重组都创建
 * 
 * @param description 书籍简介原始内容（可能包含HTML标签）
 * @param isExpanded 简介是否展开（来自MVI状态）
 * @param onToggleExpanded 切换展开状态的回调
 */
@Composable
fun BookDescriptionSection(
    description: String,
    isExpanded: Boolean = false,
    onToggleExpanded: (() -> Unit)? = null
) {
    // 性能优化：使用 derivedStateOf 缓存 HTML 清理结果
    val cleaned by remember(description) { 
        derivedStateOf {
            if (description.isBlank()) "" else HtmlTextUtil.cleanHtml(description)
        }
    }
    
    // 性能优化：缓存 AnnotatedString 创建，避免重复对象分配
    val annotatedString by remember(cleaned) {
        derivedStateOf { AnnotatedString(cleaned) }
    }
    
    // 弹窗状态管理 - 优先使用外部状态，否则使用内部状态
    var showBottomSheet by remember { mutableStateOf(false) }
    val actualIsExpanded = if (onToggleExpanded != null) isExpanded else showBottomSheet

    // 容器宽度状态
    var containerWidthPx by remember { mutableIntStateOf(0) }

    // 性能优化：使用 remember 缓存 TextMeasurer，避免每次重组都创建
    val textMeasurer = rememberTextMeasurer()
    
    // 性能优化：使用 derivedStateOf 计算布局信息，缓存计算结果
    val layoutInfo by remember(annotatedString, containerWidthPx) {
        derivedStateOf {
            if (containerWidthPx <= 0 || cleaned.isBlank()) {
                LayoutInfo(false, "", "")
            } else {
                val fullLayout = textMeasurer.measure(
                    text = annotatedString, // 使用缓存的 AnnotatedString
                    style = DescriptionTextStyle, // 使用静态样式常量
                    constraints = Constraints(maxWidth = containerWidthPx)
                )
                val totalLines = fullLayout.lineCount
                val showExpand = totalLines > 2
                
                if (showExpand) {
                    val firstEnd = fullLayout.getLineEnd(0).coerceAtMost(cleaned.length)
                    val firstLine = cleaned.substring(0, firstEnd)
                    val restAll = cleaned.substring(firstEnd)
                    LayoutInfo(true, firstLine, restAll)
                } else {
                    LayoutInfo(false, cleaned, "")
                }
            }
        }
    }

    // 性能优化：缓存 onClick 回调，避免每次重组都创建新 lambda
    val onMoreClick = remember(onToggleExpanded) {
        {
            if (onToggleExpanded != null) {
                onToggleExpanded()
            } else {
                showBottomSheet = true
            }
        }
    }
    
    // 性能优化：缓存 onDismiss 回调
    val onDismiss = remember(onToggleExpanded) {
        { 
            if (onToggleExpanded != null) {
                onToggleExpanded()
            } else {
                showBottomSheet = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                val newWidth = (size.width * 0.95).toInt()
                if (newWidth != containerWidthPx) {
                    containerWidthPx = newWidth
                }
            }
    ) {
        NovelText(
            text = "简介",
            fontSize = 18.ssp,
            fontWeight = FontWeight.Bold,
            color = NovelColors.NovelText,
            modifier = Modifier.padding(bottom = 8.wdp)
        )

        if (cleaned.isNotBlank()) {
            // 渲染第一行
            NovelText(
                text = layoutInfo.firstLine,
                fontSize = 14.ssp,
                lineHeight = 14.ssp,
                color = NovelColors.NovelText.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            )

            // 第二行和更多按钮
            if (layoutInfo.restAll.isNotBlank() || layoutInfo.showExpand) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (layoutInfo.restAll.isNotBlank()) {
                        NovelText(
                            text = layoutInfo.restAll,
                            fontSize = 14.ssp,
                            lineHeight = 14.ssp,
                            color = NovelColors.NovelText.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(end = 60.wdp)
                                .weight(5f)
                        )
                    }

                    if (layoutInfo.showExpand) {
                        NovelText(
                            text = "更多",
                            fontSize = 14.ssp,
                            lineHeight = 14.ssp,
                            color = NovelColors.NovelMain,
                            modifier = Modifier.debounceClickable(onClick = onMoreClick)
                        )
                    }
                }
            }
        }
    }

    // 半屏弹窗 - 展示完整简介内容
    if (actualIsExpanded) {
        BookDescriptionBottomSheet(
            description = description,
            onDismiss = onDismiss
        )
    }
}
