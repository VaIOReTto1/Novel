package com.novel.page.login.component

import com.novel.utils.TimberLogger
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 运营商信息展示组件
 * 
 * 显示用户当前使用的网络运营商名称信息
 * 通常用于登录页面展示网络环境信息
 * 
 * @param name 运营商名称，如"中国移动"、"中国联通"等
 */
@Composable
fun OperatorSection(name: String) {
    val TAG = "OperatorSection"
    
    // 记录运营商信息渲染
    TimberLogger.d(TAG, "渲染运营商信息: $name")
    
    NovelText(
        text = name,
        fontSize = 12.ssp,
        lineHeight = 15.ssp,
        color = NovelColors.NovelTextGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.5.wdp, top = 25.wdp)
    )
}
