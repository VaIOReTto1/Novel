package com.novel.page.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.wdp
import com.novel.utils.ssp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.novel.ui.theme.NovelTheme
import com.novel.utils.AdaptiveScreen
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/**
 * 启动弹窗类型
 */
enum class LaunchDialogType {
    UPDATE_TIP,           // 图一：版本升级提醒
    SIGNIN_BONUS          // 图二：签到赠金提示
}

/**
 * 统一入口，根据不同类型展示不同弹窗
 */
@Composable
fun AppLaunchDialog(
    type: LaunchDialogType,
    onDismiss: () -> Unit,
    onPrimaryAction: (() -> Unit)? = null
) {
    when (type) {
        LaunchDialogType.UPDATE_TIP -> UpdateTipDialog(
            onDismiss = onDismiss,
            onConfirm = { onPrimaryAction?.invoke(); onDismiss() }
        )

        LaunchDialogType.SIGNIN_BONUS -> SignInBonusDialog(
            onDismiss = onDismiss,
            onGo = { onPrimaryAction?.invoke(); onDismiss() }
        )
    }
}

/**
 * 图一：版本升级提醒弹窗
 */
@Composable
fun UpdateTipDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.wdp),
            shape = RoundedCornerShape(16.wdp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 顶部大图占满上方区域（此处用色块占位，可替换为实际图片）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.wdp)
                        .clip(RoundedCornerShape(topStart = 16.wdp, topEnd = 16.wdp))
                        .background(NovelColors.NovelMainLight)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.wdp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NovelText(
                        text = "番薯小说有新版来啦!",
                        color = NovelColors.NovelText,
                        fontSize = 16.ssp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.wdp))

                    NovelText(
                        text = "番薯小说邀请你使用新版优化体验，点击\"现在升级\"按钮下载安装苹果新版本~",
                        color = NovelColors.NovelText,
                        fontSize = 12.ssp,
                        modifier = Modifier.padding(horizontal = 45.wdp)
                    )

                    Spacer(modifier = Modifier.height(25.wdp))

                    // 内容与操作按钮之间的分割线
                    HorizontalDivider(
                        thickness = 1.wdp,
                        color = NovelColors.NovelDivider
                    )

                    // 文本样式操作按钮（无圆角背景）
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.wdp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.debounceClickable(onClick = { onDismiss() }),
                            contentAlignment = Alignment.Center
                        ) {
                            NovelText(
                                textAlign = TextAlign.Center,
                                text = "以后再说",
                                color = NovelColors.NovelText,
                                fontSize = 14.ssp
                            )
                        }

                        Box(
                            modifier = Modifier.debounceClickable(onClick = { onConfirm() }),
                            contentAlignment = Alignment.Center
                        ) {
                            NovelText(
                                text = "优先体验",
                                color = NovelColors.NovelMain,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.ssp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 图二：签到赠金弹窗
 */
@Composable
fun SignInBonusDialog(
    onDismiss: () -> Unit,
    onGo: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)
    ) {
        // 外层背景色为F6BAC2，内部使用上下两个圆角卡片+虚线分割，形成票根样式
        Column(
            modifier = Modifier.padding(horizontal = 24.wdp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部卡片：标题与说明
            Surface(
                shape = RoundedCornerShape(16.wdp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFE2E7),
                        shape = RoundedCornerShape(16.wdp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFE2E7),
                            shape = RoundedCornerShape(16.wdp)
                        )
                        .padding(horizontal = 20.wdp, vertical = 18.wdp),
                    horizontalAlignment = Alignment.Start // 居左对齐
                ) {
                    NovelText(
                        text = "番茄图书超值购",
                        color = NovelColors.NovelText,
                        fontSize = 12.ssp,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(6.wdp))
                    NovelText(
                        text = "图书商城签到送金币",
                        color = NovelColors.NovelText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.ssp,
                        textAlign = TextAlign.Start
                    )
                }
            }

            // 中部票根虚线分隔
            DashedSeparator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.wdp)
                    .padding(horizontal = 16.wdp)
            )

            // 底部卡片：商品 + CTA按钮
            Surface(
                shape = RoundedCornerShape(16.wdp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFE2E7),
                        shape = RoundedCornerShape(16.wdp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFE2E7),
                            shape = RoundedCornerShape(16.wdp)
                        )
                        .padding(horizontal = 20.wdp, vertical = 16.wdp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 三个商品占位图与价格 - 外层方框包装
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.wdp),
                        color = Color(0xFFFFE2E7)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.wdp)
                                .background(NovelColors.NovelBackground, RoundedCornerShape(8.wdp))
                                .padding(vertical = 8.wdp, horizontal = 8.wdp),
                        ) {
                            // 商品标题
                            NovelText(
                                text = "好书低价优惠专享",
                                color = NovelColors.NovelText,
                                fontSize = 14.ssp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(2.wdp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                repeat(3) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.wdp, 60.wdp)
                                                .clip(RoundedCornerShape(8.wdp))
                                                .background(NovelColors.NovelSecondaryBackground)
                                        )
                                        Spacer(modifier = Modifier.height(1.wdp))
                                        NovelText(
                                            text = "¥${listOf("3.8", "3.9", "4.88")[it % 3]}",
                                            color = NovelColors.NovelMain,
                                            fontSize = 12.ssp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.wdp))

                    Button(
                        onClick = onGo,
                        modifier = Modifier
                            .padding(horizontal = 20.wdp, vertical = 8.wdp)
                            .fillMaxWidth()
                            .height(44.wdp),
                        shape = RoundedCornerShape(24.wdp),
                        colors = ButtonDefaults.buttonColors(containerColor = NovelColors.NovelMain)
                    ) {
                        NovelText(
                            text = "去签到领200金币",
                            color = NovelColors.NovelBackground,
                            fontSize = 18.ssp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.wdp))
            // 底部关闭按钮（圆形x）
            Box(
                modifier = Modifier
                    .size(40.wdp)
                    .clip(CircleShape)
                    .background(Color(0x30000000))
                    .border(0.2.wdp, NovelColors.NovelSecondaryBackground, CircleShape)
                    .debounceClickable(onClick = { onDismiss() }),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "close", tint = Color.White)
            }
        }
    }
}

// 票根虚线分割线
@Composable
private fun DashedSeparator(
    modifier: Modifier = Modifier,
    color: Color = NovelColors.NovelSecondaryBackground,
    strokeWidth: Float = 4f,
    intervals: FloatArray = floatArrayOf(10f, 8f)
) {
    Canvas(modifier = modifier.background(Color(0xFFFFE2E7))) {
        val y = size.height / 2f
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(intervals, 0f)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AdaptiveScreen {
        NovelTheme {
            SignInBonusDialog(
                onDismiss = {},
                onGo = {},
            )
        }
    }
}

/**
 * Welfare 专用：红包弹窗（仅在福利页展示）
 */
@Composable
fun WelfareRedPacketDialog(
    onDismiss: () -> Unit,
    onOpen: () -> Unit
){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 红包主体卡片
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.wdp),
                shape = RoundedCornerShape(20.wdp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFFF6C6C), Color(0xFFFF8A5C))
                            ),
                            shape = RoundedCornerShape(20.wdp)
                        )
                        .padding(vertical = 24.wdp, horizontal = 20.wdp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NovelText(
                            text = "番茄小说 送你现金红包",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.ssp
                        )
                        Spacer(modifier = Modifier.height(36.wdp))
                        NovelText(
                            text = "恭喜发财",
                            color = Color.White,
                            fontSize = 28.ssp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(26.wdp))
                        NovelText(
                            text = "大吉大利",
                            color = Color.White,
                            fontSize = 28.ssp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(22.wdp))

                        // 开 按钮（圆形）
                        Box(
                            modifier = Modifier
                                .size(96.wdp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.95f))
                                .border(
                                    BorderStroke(3.wdp, Color(0xFFFF7A61)),
                                    CircleShape
                                )
                                .debounceClickable(onClick = onOpen),
                            contentAlignment = Alignment.Center
                        ) {
                            NovelText(
                                text = "开",
                                color = Color(0xFFFF6C6C),
                                fontSize = 30.ssp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(36.wdp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.wdp))
            // 底部关闭按钮
            Box(
                modifier = Modifier
                    .size(40.wdp)
                    .clip(CircleShape)
                    .background(Color(0x30000000))
                    .border(0.2.wdp, NovelColors.NovelSecondaryBackground, CircleShape)
                    .debounceClickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "close", tint = Color.White)
            }
        }
    }
}
