package com.novel.page.login.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.novel.ui.theme.NovelColors
import com.novel.ui.theme.NovelTheme
import com.novel.utils.wdp
import com.valentinilk.shimmer.shimmer

/**
 * 登录页面骨架屏组件
 * 
 * 在登录页面数据加载时显示的占位符界面
 * 使用Shimmer动画效果提升用户体验
 * 模拟真实登录页面的布局结构
 */
@Composable
fun LoginPageSkeleton() {
    val skeletonColor = NovelColors.NovelTextGray.copy(alpha = 0.2f)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NovelColors.NovelBackground)
            .shimmer() // 应用闪烁动画效果
            .padding(20.wdp),
        verticalArrangement = Arrangement.spacedBy(24.wdp)
    ) {
        // 顶部导航栏骨架
        LoginAppBarSkeleton(skeletonColor)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.wdp)
                .background(color = NovelColors.NovelBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.wdp)
        ) {
            // 标题区域骨架
            TitleSectionSkeleton(skeletonColor)
            
            // 分割线骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.5.wdp)
                    .height(1.wdp)
                    .background(skeletonColor)
            )
            
            // 运营商信息骨架
            OperatorSectionSkeleton(skeletonColor)
            
            // 手机号区域骨架
            PhoneSectionSkeleton(skeletonColor)
            
            // 输入框区域骨架
            InputSectionSkeleton(skeletonColor)
            
            // 按钮区域骨架
            ActionButtonsSkeleton(skeletonColor)
            
            // 协议区域骨架
            AgreementSectionSkeleton(skeletonColor)
        }
    }
}

/**
 * 顶部导航栏骨架组件
 */
@Composable
private fun LoginAppBarSkeleton(skeletonColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.wdp)
            .padding(horizontal = 16.wdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮骨架
        Box(
            modifier = Modifier
                .size(24.wdp)
                .clip(RoundedCornerShape(12.wdp))
                .background(skeletonColor)
        )
    }
}

/**
 * 标题区域骨架组件
 */
@Composable
private fun TitleSectionSkeleton(skeletonColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.wdp)
    ) {
        // 主标题骨架
        Box(
            modifier = Modifier
                .width(120.wdp)
                .height(32.wdp)
                .clip(RoundedCornerShape(4.wdp))
                .background(skeletonColor)
        )
        
        // 副标题骨架
        Box(
            modifier = Modifier
                .width(200.wdp)
                .height(16.wdp)
                .clip(RoundedCornerShape(4.wdp))
                .background(skeletonColor)
        )
    }
}

/**
 * 运营商信息骨架组件
 */
@Composable
private fun OperatorSectionSkeleton(skeletonColor: Color) {
    Box(
        modifier = Modifier
            .width(100.wdp)
            .height(20.wdp)
            .clip(RoundedCornerShape(4.wdp))
            .background(skeletonColor)
    )
}

/**
 * 手机号区域骨架组件
 */
@Composable
private fun PhoneSectionSkeleton(skeletonColor: Color) {
    Box(
        modifier = Modifier
            .width(150.wdp)
            .height(24.wdp)
            .clip(RoundedCornerShape(4.wdp))
            .background(skeletonColor)
    )
}

/**
 * 输入框区域骨架组件
 */
@Composable
private fun InputSectionSkeleton(skeletonColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.5.wdp),
        verticalArrangement = Arrangement.spacedBy(16.wdp)
    ) {
        // 用户名输入框骨架
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.wdp)
                .clip(RoundedCornerShape(25.wdp))
                .background(skeletonColor)
        )
        
        // 密码输入框骨架
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.wdp)
                .clip(RoundedCornerShape(25.wdp))
                .background(skeletonColor)
        )
        
        // 验证码输入框骨架（可能存在）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.wdp)
                .clip(RoundedCornerShape(25.wdp))
                .background(skeletonColor)
        )
    }
}

/**
 * 操作按钮区域骨架组件
 */
@Composable
private fun ActionButtonsSkeleton(skeletonColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.5.wdp),
        verticalArrangement = Arrangement.spacedBy(12.wdp)
    ) {
        // 主要按钮骨架（登录/注册）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.wdp)
                .clip(RoundedCornerShape(25.wdp))
                .background(skeletonColor)
        )
        
        // 次要按钮骨架（切换模式）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.wdp)
                .clip(RoundedCornerShape(25.wdp))
                .background(skeletonColor)
        )
    }
}

/**
 * 协议区域骨架组件
 */
@Composable
private fun AgreementSectionSkeleton(skeletonColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.wdp)
    ) {
        // 协议文字骨架（多行）
        repeat(2) { index ->
            Box(
                modifier = Modifier
                    .width(if (index == 0) 250.wdp else 180.wdp)
                    .height(14.wdp)
                    .clip(RoundedCornerShape(2.wdp))
                    .background(skeletonColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPageSkeletonPreview() {
    NovelTheme {
        LoginPageSkeleton()
    }
} 
