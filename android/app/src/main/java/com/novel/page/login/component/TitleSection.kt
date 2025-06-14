package com.novel.page.login.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.novel.page.component.NovelText
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 *标题
 */
@Composable
fun TitleSection() {
    NovelText(
        text = "登录\n可领现金红包",
        fontSize = 20.ssp,
        lineHeight = 24.ssp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.5.wdp, bottom = 40.wdp)
            //无障碍
            .semantics { contentDescription = "登录，可领现金红包" },
    )
}