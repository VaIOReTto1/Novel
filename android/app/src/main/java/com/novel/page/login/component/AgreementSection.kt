package com.novel.page.login.component

import com.novel.utils.TimberLogger
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.novel.ui.theme.NovelColors
import com.novel.ui.theme.PingFangFamily
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.remember
import com.novel.utils.debounceClickable
import com.novel.utils.wdp
import com.novel.utils.ssp

/**
 * 登录页面协议同意组件
 *
 * 包含用户协议、注册协议和运营商认证服务协议的勾选框和链接
 * 支持协议文本点击和同意状态切换
 *
 * @param operator 运营商名称
 * @param isChecked 是否已勾选同意
 * @param onCheckedChange 勾选状态变化回调
 * @param onTelServiceClick 点击运营商认证服务协议回调
 * @param onUserAgreementClick 点击用户协议回调
 * @param onRegisterAgreementClick 点击注册协议回调
 * @param modifier 修饰符
 */
@Composable
fun AgreementSection(
    operator: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTelServiceClick: () -> Unit,
    onUserAgreementClick: () -> Unit,
    onRegisterAgreementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val TAG = "AgreementSection"
    // 预取主题色，避免在Canvas DrawScope内重复调用@Composable Getter
    val checkedColor = NovelColors.NovelMainLight
    val uncheckedColor = NovelColors.NovelTextGray

    // 统一文本样式
    val textStyle = androidx.compose.ui.text.TextStyle(
        color = NovelColors.NovelTextGray,
        fontSize = 12.ssp,
        lineHeight = 15.ssp,
        fontWeight = FontWeight.Thin,
        fontFamily = PingFangFamily
    )

    val toggleChecked = remember(isChecked, onCheckedChange) {
        {
            TimberLogger.d(TAG, "切换协议同意状态: $isChecked -> ${!isChecked}")
            onCheckedChange(!isChecked)
        }
    }
    val telClick = remember(onTelServiceClick, operator) {
        { annotation: LinkAnnotation ->
            TimberLogger.d(TAG, "点击运营商认证服务协议: $operator")
            onTelServiceClick()
        }
    }
    val userClick = remember(onUserAgreementClick) {
        { annotation: LinkAnnotation ->
            TimberLogger.d(TAG, "点击用户协议")
            onUserAgreementClick()
        }
    }
    val registerClick = remember(onRegisterAgreementClick) {
        { annotation: LinkAnnotation ->
            TimberLogger.d(TAG, "点击注册协议")
            onRegisterAgreementClick()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 自定义勾选框
            Box(
                modifier = Modifier
                    .size(12.wdp)
                    .debounceClickable(onClick = toggleChecked),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    if (isChecked) {
                        // 绘制实心圆
                        drawCircle(color = checkedColor, style = Fill)
                    } else {
                        // 绘制空心圆
                        drawCircle(
                            color = uncheckedColor,
                            style = Stroke(width = 1.wdp.toPx())
                        )
                    }
                }
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "已选中",
                        tint = NovelColors.NovelSecondaryBackground,
                        modifier = Modifier.size(10.wdp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.wdp))

            // 构建第一行可点击文本（运营商认证服务协议）
            val annotatedText = buildAnnotatedString {
                append("已阅读并同意 ")
                addLink(
                    LinkAnnotation.Clickable(
                        tag = "TEL",
                        styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline)),
                        linkInteractionListener = telClick
                    ),
                    start = this.length,
                    end = this.length + operator.length + "认证服务协议".length
                )
                append(operator + "认证服务协议")
                append(" 以及")
            }
            BasicText(
                text = annotatedText,
                style = textStyle,
                maxLines = 1
            )
        }

        // 构建第二行可点击文本（用户协议和注册协议）
        val policyText = buildAnnotatedString {
            addLink(
                LinkAnnotation.Clickable(
                    tag = "USER",
                    styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline)),
                    linkInteractionListener = userClick
                ),
                start = this.length,
                end = this.length + "用户协议".length
            )
            append("用户协议")
            append(" 和 ")
            addLink(
                LinkAnnotation.Clickable(
                    tag = "REGISTER",
                    styles = TextLinkStyles(SpanStyle(textDecoration = TextDecoration.Underline)),
                    linkInteractionListener = registerClick
                ),
                start = this.length,
                end = this.length + "注册协议".length
            )
            append("注册协议")
        }
        BasicText(
            text = policyText,
            style = textStyle
        )
    }
}
