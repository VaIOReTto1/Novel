package com.novel.page.search.utils

import com.novel.utils.TimberLogger
import java.text.SimpleDateFormat
import java.util.*

/**
 * 搜索相关的工具类
 */
object SearchUtils {
    
    private const val TAG = "SearchUtils"
    
    /**
     * 清理搜索关键词
     * 去除首尾空格，过滤特殊字符
     */
    private fun cleanSearchQuery(query: String): String {
        return query.trim()
            .replace(Regex("[\\n\\r\\t]"), "") // 移除换行符、制表符
            .replace(Regex("\\s+"), " ") // 多个空格合并为一个
    }
    
    /**
     * 验证搜索关键词是否有效
     */
    fun isValidSearchQuery(query: String): Boolean {
        val cleanedQuery = cleanSearchQuery(query)
        return cleanedQuery.isNotBlank() && cleanedQuery.length >= 1
    }
    
    /**
     * 生成搜索建议
     * 基于输入的关键词生成相关搜索建议
     */
    fun generateSearchSuggestions(query: String, history: List<String>): List<String> {
        if (query.isBlank()) return emptyList()
        
        val cleanedQuery = cleanSearchQuery(query).lowercase()
        
        return history.filter { historyItem ->
            historyItem.lowercase().contains(cleanedQuery)
        }.take(5) // 最多返回5个建议
    }
    
    /**
     * 高亮搜索关键词
     * 在文本中高亮显示搜索关键词
     */
    fun highlightSearchKeyword(text: String, keyword: String): String {
        if (keyword.isBlank()) return text
        
        val cleanedKeyword = cleanSearchQuery(keyword)
        if (cleanedKeyword.isBlank()) return text
        
        return text.replace(
            oldValue = cleanedKeyword,
            newValue = "<b>$cleanedKeyword</b>",
            ignoreCase = true
        )
    }
    
    /**
     * 格式化搜索结果数量
     */
    fun formatSearchResultCount(count: Int): String {
        return when {
            count >= 100000000 -> "${count / 100000000}亿"
            count >= 10000 -> "${count / 10000}万"
            count >= 1000 -> "${count / 1000}千"
            else -> "$count"
        }
    }
    
    /**
     * 检查是否为空搜索
     */
    fun isEmptySearch(query: String): Boolean {
        return cleanSearchQuery(query).isBlank()
    }
    
    /**
     * 记录搜索事件
     */
    fun logSearchEvent(query: String, resultCount: Int) {
        TimberLogger.d(TAG, "搜索事件: 关键词='$query', 结果数量=$resultCount")
    }
    
    fun formatWordCount(wordCount: Int): String {
        return when {
            wordCount >= 10000 -> "${wordCount / 10000}万字"
            wordCount >= 1000 -> "${wordCount / 1000}千字"
            else -> "${wordCount}字"
        }
    }
    
    fun formatUpdateTime(updateTime: String?): String {
        if (updateTime.isNullOrEmpty()) return "未知时间"
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(updateTime)
            val now = Date()
            val diffInMillis = now.time - (date?.time ?: 0)
            val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
            
            when {
                diffInDays < 1 -> "今天更新"
                diffInDays < 7 -> "${diffInDays}天前更新"
                diffInDays < 30 -> "${diffInDays / 7}周前更新"
                else -> "${diffInDays / 30}月前更新"
            }
        } catch (e: Exception) {
            TimberLogger.w(TAG, "时间格式化失败: updateTime=$updateTime", e)
            "未知时间"
        }
    }
    
    fun sanitizeSearchQuery(query: String): String {
        return query.trim().replace(Regex("[<>\"'&]"), "")
    }
}
