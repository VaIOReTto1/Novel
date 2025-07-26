package com.novel.page.login.component

import com.novel.utils.TimberLogger
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import com.novel.page.component.NovelMainButton
import androidx.compose.ui.Alignment
import com.novel.page.component.NovelText
import com.novel.page.component.NovelWeakenButton
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp
import com.novel.utils.wdp
import java.util.UUID

/**
 * 登录页面操作按钮组件
 *
 * 包含主要操作按钮（登录）和次要操作按钮（注册）
 * 支持按钮启用状态控制和自定义文案
 * 支持错误时的弹跳动画效果
 *
 * @param modifier 修饰符
 * @param firstText 主按钮文字，默认为"登录"
 * @param secondText 次按钮文字，默认为"暂无账号，进行注册"
 * @param onFirstClick 主按钮点击事件回调
 * @param isFirstEnabled 主按钮是否启用，默认为true
 * @param onSecondClick 次按钮点击事件回调
 * @param shouldTriggerBounce 是否触发弹跳动画，用于错误提示
 * @param onBounceComplete 弹跳动画完成回调
 */
@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    firstText: String = "登录",
    secondText: String = "暂无账号，进行注册",
    onFirstClick: () -> Unit,
    isFirstEnabled: Boolean = true,
    onSecondClick: () -> Unit,
    shouldTriggerBounce: Boolean,
    onBounceComplete: () -> Unit = {}
) {
    val TAG = "ActionButtons"

    // 弹跳动画状态
    val bounceAnimation = remember {
        Animatable(1f)
    }

    // 监听弹跳触发
    LaunchedEffect(shouldTriggerBounce) {
        if (shouldTriggerBounce) {
            TimberLogger.d(TAG, "触发按钮弹跳动画")
            // 弹跳动画：缩小 -> 放大 -> 恢复
            bounceAnimation.animateTo(
                targetValue = 0.9f,
                animationSpec = tween(100, easing = FastOutSlowInEasing)
            )
            bounceAnimation.animateTo(
                targetValue = 1.1f,
                animationSpec = tween(100, easing = FastOutSlowInEasing)
            )
            bounceAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(100, easing = FastOutSlowInEasing)
            )
            // 动画完成后调用回调，重置触发状态
            onBounceComplete()
        }
    }

    val firstClick = remember(onFirstClick) {
        {
            TimberLogger.d(TAG, "点击主操作按钮: $firstText")
            onFirstClick()
        }
    }
    val secondClick = remember(onSecondClick) {
        {
            TimberLogger.d(TAG, "点击次要操作按钮: $secondText")
            onSecondClick()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 主操作按钮（登录）
        NovelMainButton(
            content = {
                NovelText(
                    firstText,
                    fontSize = 16.ssp,
                    fontWeight = FontWeight.Bold,
                    color = NovelColors.NovelSecondaryBackground
                )
            },
            modifier = Modifier
                .padding(bottom = 16.wdp)
                .width(330.wdp)
                .height(48.wdp)
                .graphicsLayer {
                    scaleX = bounceAnimation.value
                    scaleY = bounceAnimation.value
                },
            onClick = firstClick
        )

        // 次要操作按钮（注册）
        NovelWeakenButton(
            content = {
                NovelText(
                    secondText,
                    fontSize = 16.ssp,
                    fontWeight = FontWeight.Bold,
                    color = NovelColors.NovelText
                )
            },
            modifier = Modifier
                .width(330.wdp)
                .height(48.wdp),
            onClick = secondClick
        )
    }
}