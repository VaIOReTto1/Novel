package com.novel.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.novel.ui.theme.NovelDesignTokens
import com.novel.ui.theme.PingFangFamily
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 自定义输入框组件
 * 
 * 功能：
 * - 占位符文本显示
 * - 密码输入支持
 * - 错误状态显示
 * - 密码可见性切换
 * 
 * @param value 输入框的值
 * @param onValueChange 值改变回调
 * @param modifier 修饰符
 * @param round 圆角大小
 * @param placeText 占位符文字
 * @param isPassword 是否为密码输入框
 * @param isError 是否为错误状态
 * @param errorMessage 错误提示信息
 */
@Composable
fun NovelTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier,
    round: Dp = 26.wdp,
    placeText: String = "",
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val textPrimary = NovelDesignTokens.lightColor("color.text.primary")
    val textSecondary = NovelDesignTokens.lightColor("color.text.secondary")
    val surface = NovelDesignTokens.lightColor("color.bg.surface")
    val danger = NovelDesignTokens.lightColor("color.status.danger")
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    // 密码可见状态
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(
                color = surface,
                shape = RoundedCornerShape(round)
            ),
        contentAlignment = Alignment.Center
    ) {
        // 占位符显示逻辑
        if (value == "" && !hasFocus && !isError) {
            NovelText(
                text = placeText,
                style = TextStyle(
                    fontWeight = FontWeight.W500,
                    fontSize = 16.ssp,
                    textAlign = TextAlign.Center
                    ),
                    color = textSecondary,
            )
        }
        
        // 主要输入框
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 16.ssp,
                color = textPrimary,
                textAlign = TextAlign.Center,
                fontFamily = PingFangFamily,
            ),
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    hasFocus = focusState.isFocused
                }
        )

        // 错误提示显示
        if (isError) {
            if (errorMessage != null) {
                NovelText(
                    text = errorMessage,
                    style = TextStyle(
                        fontSize = 12.ssp,
                        fontWeight = FontWeight.W400
                    ),
                    color = danger,
                    modifier = Modifier.padding(start = 16.wdp, top = 4.wdp)
                )
            }
        }

        // 密码可见性切换按钮
        if (isPassword && value != "") {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier.padding(end = 16.wdp)
                    .align(Alignment.CenterEnd)
                    .size(24.wdp)
            ) {
                Icon(
                    imageVector = if (passwordVisible)
                        Icons.Default.FavoriteBorder
                    else
                        Icons.Default.Favorite,
                    contentDescription = if (passwordVisible)
                        "隐藏密码"
                    else
                        "显示密码"
                )
            }
        }
    }
}
