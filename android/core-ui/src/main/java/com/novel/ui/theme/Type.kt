package com.novel.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import com.novel.core.ui.R
import com.novel.utils.ssp

/**
 * 苹方字体家族定义
 * 
 * 包含从Light到Heavy的完整字重范围
 * 为小说阅读提供优雅的中文显示效果
 */
val PingFangFamily = FontFamily(
    Font(R.font.pingfang_regular, FontWeight.Normal),      // 常规字重
    Font(R.font.pingfang_bold, FontWeight.Bold),           // 粗体
    Font(R.font.pingfang_light, FontWeight.Light),         // 细体
    Font(R.font.pingfang_medium, FontWeight.Medium),       // 中等字重
    Font(R.font.pingfang_semibold, FontWeight.SemiBold),   // 半粗体
    Font(R.font.pingfang_heavy, FontWeight.Black)          // 特粗体
)

/**
 * 应用整体字体排版系统
 * 
 * 基于Material3 Typography规范，使用苹方字体
 * 针对小说阅读场景优化各级标题和正文样式
 */
val Typography = Typography(
    /** 中等标题样式 - 用于页面主标题 */
    titleMedium = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.ssp
    ),
    
    /** 小标题样式 - 用于副标题、区块标题 */
    titleSmall = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.ssp
    ),
    
    /** 正文中等样式 - 用于主要文本内容 */
    bodyMedium = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.ssp
    ),
    
    /** 大标签样式 - 用于按钮文字、重要提示 */
    labelLarge = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.ssp
    ),
    
    /** 小标签样式 - 用于次要信息、说明文字 */
    labelSmall = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.ssp
    ),
    
    /** 小正文样式 - 用于辅助信息，采用半粗体增强可读性 */
    bodySmall = TextStyle(
        fontFamily = PingFangFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.ssp
    )
)
