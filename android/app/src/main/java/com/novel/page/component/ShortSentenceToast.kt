package com.novel.page.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 短剧 Toast 数据
 */
data class ShortDramaToastData(
    val imageUrl: String?,
    val dramaName: String,
    val watchedEpisodes: Int,
    val remainingEpisodes: Int
)

/**
 * 短剧观看进度 Toast 提示
 * 
 * 功能特点：
 * - 悬浮于底部导航栏上方
 * - 显示短剧封面、名称和观看进度
 * - 提供继续观看和关闭功能
 * - 支持优雅的显示/隐藏动画
 * - 可与 Dialog 同时存在
 * 
 * @param data 短剧数据信息
 * @param visible 是否显示
 * @param onContinue 继续观看回调
 * @param onClose 关闭回调
 * @param modifier 修饰符
 */
@Composable
fun ShortSentenceToast(
    data: ShortDramaToastData,
    visible: Boolean,
    onContinue: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it }
        ) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.wdp)
                .shadow(
                    elevation = 8.wdp,
                    shape = RoundedCornerShape(12.wdp)
                ),
            color = NovelColors.NovelTextGray, // 半透明黑色背景
            shape = RoundedCornerShape(12.wdp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.wdp, vertical = 10.wdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧封面图片
                NovelImageView(
                    imageUrl = data.imageUrl,
                    widthDp = 36,
                    heightDp = 48,
                    contentScale = ContentScale.Crop,
                    loadingStrategy = ImageLoadingStrategy.HIGH_QUALITY,
                    modifier = Modifier
                        .background(
                            color = NovelColors.NovelLightGray,
                            shape = RoundedCornerShape(6.wdp)
                        )
                )

                Spacer(modifier = Modifier.width(12.wdp))

                // 中间内容区域
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    // 短剧名称
                    NovelText(
                        text = data.dramaName,
                        color = Color.White,
                        fontSize = 13.ssp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // 观看进度
                    NovelText(
                        text = "已看到第${data.watchedEpisodes}集，还有${data.remainingEpisodes}集未看",
                        color = Color(0xFFCCCCCC),
                        fontSize = 11.ssp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(4.wdp))

                // 继续观看按钮
                Box(
                    modifier = Modifier
                        .background(
                            color = NovelColors.NovelBackground,
                            shape = RoundedCornerShape(16.wdp)
                        )
                        .debounceClickable(onClick = onContinue)
                        .padding(horizontal = 8.wdp, vertical = 3.wdp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.wdp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = NovelColors.NovelMain,
                            modifier = Modifier.size(12.wdp)
                        )
                        NovelText(
                            text = "继续观看",
                            color = NovelColors.NovelMain,
                            fontSize = 11.ssp,
                            fontWeight = FontWeight.W800
                        )
                    }
                }

                Spacer(modifier = Modifier.width(2.wdp))

                // 关闭按钮
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(32.wdp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = NovelColors.NovelBackground,
                        modifier = Modifier.size(18.wdp)
                    )
                }
            }
        }
    }
}