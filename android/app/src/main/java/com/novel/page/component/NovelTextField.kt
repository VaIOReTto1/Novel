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
import com.novel.ui.theme.NovelColors
import com.novel.ui.theme.PingFangFamily
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 带占位符的输入框
 * @param value 输入框的值
 * @param onValueChange 输入框的值改变的回调
 * @param modifier 修饰符
 * @param round 圆角
 * @param placeText 占位符文字
 * @param isPassword 是否是密码输入框
 */
@Composable
fun NovelTextField(
    value: String, // 改为 value（与状态绑定）
    onValueChange: (String) -> Unit = {}, // 新增回调
    modifier: Modifier,
    round: Dp = 26.wdp,
    placeText: String = "",
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    // 密码可见状态
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(round)
            ),
        contentAlignment = Alignment.Center
    ) {
        // 如果输入框值为空，则显示占位符
        if (value == "" && !hasFocus && !isError) {
            NovelText(
                text = placeText, // 显示占位符文字
                style = TextStyle(
                    fontWeight = FontWeight.W500,
                    fontSize = 16.ssp,
                    textAlign = TextAlign.Center
                ),
                color = NovelColors.NovelTextGray, // 淡化颜色
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange, // 绑定回调
            textStyle = TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 16.ssp,
                color = NovelColors.NovelTextGray,
                textAlign = TextAlign.Center,
                fontFamily = PingFangFamily,
            ),
            visualTransformation = if (isPassword) { // 核心修改点
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },

            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    // 监听焦点变化
                    hasFocus = focusState.isFocused
                }
        )

        // 错误提示区域
        if (isError) {
            if (errorMessage != null) {
                NovelText(
                    text = errorMessage,
                    style = TextStyle(
                        fontSize = 12.ssp,
                        fontWeight = FontWeight.W400
                    ),
                    color = NovelColors.NovelError, // 错误提示颜色
                    modifier = Modifier.padding(start = 16.wdp, top = 4.wdp)
                )
            }
        }

        // 密码框才展示可见切换按钮
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