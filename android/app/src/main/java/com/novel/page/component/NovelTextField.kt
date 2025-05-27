package com.novel.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp
import com.novel.utils.wdp

@Composable
fun NovelTextField(
    value: String, // 改为 value（与状态绑定）
    onValueChange: (String) -> Unit = {}, // 新增回调
    modifier: Modifier,
    round: Dp = 26.wdp,
    placeText: String = ""
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(round)
            ),
        contentAlignment = Alignment.Center
    ) {
        // 如果输入框值为空，则显示占位符
        if (value == "" && !hasFocus) {
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
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    // 监听焦点变化
                    hasFocus = focusState.isFocused
                }
        )
    }
}