package com.novel.utils

/**
 * 手机号码掩码处理工具
 * 
 * 功能特点：
 * - 中间四位数字替换为星号
 * - 保护用户隐私信息
 * - 兼容不同长度的号码
 * 
 * 处理规则：
 * - 长度>=9位：显示前3位和后4位，中间用****替换
 * - 长度<9位：直接返回原字符串（避免格式错误）
 * 
 * 使用场景：
 * - 用户信息展示页面
 * - 验证码发送提示
 * - 订单信息显示
 * - 日志记录脱敏
 * 
 * 示例：
 * - 输入："13812345678" -> 输出："138****5678"
 * - 输入："1234567" -> 输出："1234567"（长度不足直接返回）
 * 
 * @param number 原始手机号码字符串
 * @return 掩码处理后的手机号码
 */
fun maskPhoneNumber(number: String): String {
    return if (number.length >= 9) {
        // 取前3位
        val start = number.take(3)
        // 取后4位
        val end = number.takeLast(4)
        // 中间用****替换
        "$start****$end"
    } else {
        // 长度不足时直接返回原字符串
        number
    }
}
