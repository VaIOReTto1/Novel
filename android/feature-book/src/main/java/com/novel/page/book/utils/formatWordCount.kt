package com.novel.page.book.utils

/**
 * 格式化字数显示工具函数
 * 将数字转换为便于阅读的中文单位格式
 * 
 * @param wordCount 原始字数
 * @return 格式化后的字符串，如"1万"、"5千"
 */
fun formatWordCount(wordCount: Int): String {
    return when {
        wordCount >= 10000 -> "${wordCount / 10000}万"
        wordCount >= 1000 -> "${wordCount / 1000}千"
        else -> "$wordCount"
    }
}
