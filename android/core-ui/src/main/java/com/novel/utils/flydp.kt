package com.novel.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 屏幕适配工具类 - FlyDP
 * 
 * 功能特点：
 * - 全局统一的屏幕适配方案
 * - 支持宽度和高度独立缩放
 * - 字体大小自适应调整
 * - 扩展属性语法简洁易用
 * 
 * 技术实现：
 * - 基于全局缩放比例计算
 * - 扩展属性动态计算尺寸
 * - Compose单位自动转换
 * 
 * 使用场景：
 * - 多屏幕尺寸适配
 * - UI组件尺寸统一管理
 * - 响应式布局设计
 * 
 * 使用示例：
 * ```
 * // 宽度适配
 * width = 100.wdp
 * 
 * // 高度适配
 * height = 200.hdp
 * 
 * // 字体适配
 * fontSize = 16.ssp
 * ```
 */

/**
 * 全局宽度缩放比例
 * 根据设备屏幕宽度动态计算
 */
var globalScaleX: Float = 1.0f

/**
 * 全局高度缩放比例
 * 根据设备屏幕高度动态计算
 */
var globalScaleY: Float = 1.0f

/**
 * 宽度适配扩展属性 - Int版本
 * 自动应用全局宽度缩放比例
 */
val Int.wdp: Dp
    get() = (this * globalScaleX).dp

/**
 * 宽度适配扩展属性 - Double版本
 * 支持小数点精度的尺寸适配
 */
val Double.wdp: Dp
    get() = (this * globalScaleX).dp

/**
 * 宽度适配扩展属性 - Float版本
 * 常用于计算后的浮点数值
 */
val Float.wdp: Dp
    get() = (this * globalScaleX).dp

/**
 * 高度适配扩展属性 - Int版本
 * 自动应用全局高度缩放比例
 */
val Int.hdp: Dp
    get() = (this * globalScaleY).dp

/**
 * 高度适配扩展属性 - Double版本
 * 支持精确的高度尺寸计算
 */
val Double.hdp: Dp
    get() = (this * globalScaleY).dp

/**
 * 字体大小适配扩展属性 - Int版本
 * 基于宽度缩放比例调整字体大小
 */
val Int.ssp: TextUnit
    get() = (this * globalScaleX).sp

/**
 * 字体大小适配扩展属性 - Double版本
 * 支持精确的字体大小调整
 */
val Double.ssp: TextUnit
    get() = (this * globalScaleX).sp
