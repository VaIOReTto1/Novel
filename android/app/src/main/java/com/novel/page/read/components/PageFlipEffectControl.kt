package com.novel.page.read.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color
import com.novel.page.read.viewmodel.PageFlipEffect

/**
 * ItemInfoDp 用于存储按钮位置的几何信息：
 *  - 若 isPixel=true，则 xPx 和 widthPx 存储的是 px 信息，后续转换为 Dp
 *  - 若 isPixel=false，则 xDp 和 widthDp 存储的是最终的 Dp 信息，可供动画使用
 */
private data class ItemInfoDp(
    val xDp: Dp = 0.dp,
    val widthDp: Dp = 0.dp,
    val xPx: Int = 0,
    val widthPx: Int = 0,
    val isPixel: Boolean = false
)

/**
 * 翻页效果控制组件（性能优化版）
 *
 * 要点：
 *  1. 使用 updateTransition 同步动画 X 偏移与宽度，避免双重 animateDpAsState。
 *  2. 首次测量时一次性将 px 转换为 Dp，并缓存，避免每帧都做转换计算。
 *  3. 只在首次写入 itemInfoMap（相同键不存在时）避免重复触发 recomposition。
 *  4. 日志打印仅在 DEBUG 模式生效，release 包不输出，减少运行时开销。
 *
 * @param currentEffect   当前选中的翻页效果
 * @param onEffectChange  当点击切换时的回调
 */
@Composable
fun PageFlipEffectControl(
    currentEffect: PageFlipEffect,
    backgroundColor: Color,
    onEffectChange: (PageFlipEffect) -> Unit
) {
    // Density 用于 px ↔ dp 转换
    val density = LocalDensity.current

    // 存储每个效果对应的几何信息（已转换成 Dp 单位）
    val itemInfoMap = remember { mutableStateMapOf<PageFlipEffect, ItemInfoDp>() }

    /**
     * LaunchedEffect 监听 itemInfoMap 大小变化，一旦键的数量有变动，就把 map 中的 px 信息转换到 Dp 并缓存到 itemInfoMap 中。
     * 通过 snapshotFlow 保证这里只在实际测量后的值改变时才触发，避免每帧都执行转换。
     */
    LaunchedEffect(itemInfoMap.size) {
        // 收集一次性测量得到的 px 数据，然后转为 Dp 后更新 map
        snapshotFlow { itemInfoMap.toMap() }.collect { currentMap ->
            currentMap.forEach { (effect, infoDp) ->
                // 如果 value 还是 px（在初次测量时写入的为 px 或者覆盖 Dp），做一次转换
                if (infoDp.isPixel) {
                    val xDp = with(density) { infoDp.xPx.toDp() }
                    val wDp = with(density) { infoDp.widthPx.toDp() }
                    itemInfoMap[effect] = ItemInfoDp(xDp = xDp, widthDp = wDp)
                }
            }
        }
    }

    // 用 updateTransition 同步动画 X 偏移和宽度
    val transition = updateTransition(targetState = currentEffect, label = "HighlightTransition")

    // 高亮框的 X 偏移动画，单位 Dp
    val highlightX by transition.animateDp(
        transitionSpec = {
            spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioNoBouncy
            )
        },
        label = "AnimateX"
    ) { effect ->
        itemInfoMap[effect]?.xDp ?: 0.dp
    }

    // 高亮框的宽度动画，单位 Dp
    val highlightW by transition.animateDp(
        transitionSpec = {
            spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioNoBouncy
            )
        },
        label = "AnimateWidth"
    ) { effect ->
        itemInfoMap[effect]?.widthDp ?: 0.dp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.wdp),  // 整行高度
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.wdp)
    ) {
        // 左侧"翻页"文字，不在轨道背景内
        NovelText(
            text = "翻页",
            fontSize = 14.ssp,
            color = NovelColors.NovelText
        )

        // 按钮区域（包含灰色轨道、高亮框、以及文字按钮）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.wdp),  // 轨道与高亮框的高度
            contentAlignment = Alignment.CenterStart
        ) {
            // 1) 浅灰色轨道背景，圆角形状
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = Color.Gray.copy(alpha = 0.1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.wdp)
                    )
                    .padding(horizontal = 6.wdp)  // 与按钮左右间距一致
            )

            // 2) 高亮框：偏移和宽度由 transition 驱动，背景色为主色
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = highlightX.roundToPx(), y = 0) }
                    .width(highlightW + 13.5.wdp)  // +13.wdp 用于补足内外边距
                    .height(32.wdp)
                    .padding(1.wdp)
                    .background(
                        color = backgroundColor,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.wdp)
                    )
            )

            // 3) 按钮文字：覆盖在高亮框之上，监听位置以初始化 geometry
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 6.wdp),
                horizontalArrangement = Arrangement.spacedBy(4.5.wdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PageFlipEffect.entries.forEach { effect ->
                    NovelText(
                        text = effect.displayName,
                        fontSize = 12.ssp,
                        lineHeight = 12.ssp,
                        color = NovelColors.NovelText,
                        fontWeight = if (currentEffect == effect) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .wrapContentHeight(Alignment.CenterVertically)
                            // 获取测量信息：相对父布局的 x 坐标与宽度（单位 px）
                            .onGloballyPositioned { coords ->
                                val pxX = coords.positionInParent().x.roundToInt()
                                val pxW = coords.size.width
                                // 仅在 map 中不存在时写入，避免每次重新测量都触发重组
                                if (itemInfoMap[effect] == null) {
                                    // 临时先以 px 单位保存，后续 LaunchedEffect 会转为 Dp
                                    itemInfoMap[effect] =
                                        ItemInfoDp(xPx = pxX, widthPx = pxW, isPixel = true)
                                }
                            }
                            // 防抖点击，仅在该项未选中时可点击
                            .debounceClickable(
                                enabled = (effect != currentEffect),
                                onClick = {
                                    onEffectChange(effect)
                                }
                            )
                            // 文字按钮内边距
                            .padding(horizontal = 12.wdp, vertical = 6.wdp)
                    )
                }
            }
        }
    }
}
