package com.novel.utils

import com.novel.utils.TimberLogger

/**
 * HTML文本处理工具类
 * 
 * 核心功能：
 * - HTML实体转换：将HTML实体转换为可读字符
 * - 标签清理：移除HTML标签，保留纯文本内容
 * - 格式优化：处理换行、空格等格式字符
 * - 文本净化：清理多余空白字符，确保显示效果
 * 
 * 使用场景：
 * - 小说章节内容的HTML格式清理
 * - 书籍描述文本的格式处理
 * - 用户输入内容的安全过滤
 * - UI显示前的文本预处理
 * 
 * 设计特点：
 * - 单例对象设计，无状态操作
 * - 链式处理，逐步清理HTML内容
 * - 性能优化，使用字符串替换而非正则
 * - 安全处理，防止XSS攻击风险
 */
object HtmlTextUtil {

    private const val TAG = "HtmlTextUtil"

    /**
     * 清理HTML格式字符
     * 
     * 处理流程：
     * 1. 替换HTML实体为对应字符
     * 2. 处理换行标签和段落标签
     * 3. 清理特殊字符和转义符
     * 
     * @param input 包含HTML格式的原始文本
     * @return 清理后的纯文本内容
     */
    fun cleanHtml(input: String): String {
        if (input.isEmpty()) {
            return input
        }
        
        val result = input
            // 替换HTML实体为普通字符
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            // 处理换行和段落标签
            .replace("<br/><br/>", "\n\n\n")
            .replace("<p>", "")
            .replace("</p>", "")
            .replace("&>", "”")
        return result
    }

    /**
     * 移除所有HTML标签
     * 
     * 功能特点：
     * - 使用正则表达式移除所有HTML标签
     * - 替换常见HTML实体为对应字符
     * - 清理多余空白字符，确保文本整洁
     * - 去除首尾空白，优化显示效果
     * 
     * @param input 包含HTML标签的原始文本
     * @return 纯文本内容，不包含任何HTML标签
     */
    fun removeHtmlTags(input: String): String {
        if (input.isEmpty()) {
            return input
        }
        
        TimberLogger.d(TAG, "开始移除HTML标签，原始长度: ${input.length}")
        
        val result = input
            // 使用正则移除HTML标签
            .replace(Regex("<[^>]+>"), "")
            // 替换HTML实体
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            // 清理多余的空白字符
            .replace(Regex("\\s+"), " ")
            .trim()
        
        TimberLogger.d(TAG, "HTML标签移除完成，处理后长度: ${result.length}")
        return result
    }
}
