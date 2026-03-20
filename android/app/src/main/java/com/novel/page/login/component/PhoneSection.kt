package com.novel.page.login.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.novel.page.component.NovelText
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 手机号展示组件
 * 
 * 显示用户的手机号码，支持空值处理
 * 包含测试标签，便于UI自动化测试
 * 
 * @param phone 手机号码字符串，为空时显示默认提示文字
 */
@Composable
fun PhoneSection(phone: String) {
    NovelText(
        text = phone.ifBlank { "手机号获取失败" },
        fontSize = 16.ssp,
        lineHeight = 18.ssp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.5.wdp, top = 5.wdp, bottom = 9.wdp)
            .testTag("PhoneText") // 测试标签
    )
}
