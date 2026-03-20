package com.novel.utils

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.novel.R

/**
 * 小说日期格式化工具类
 * 
 * 功能特点：
 * - 新闻时间智能展示格式
 * - 多种时间范围适配
 * - 依赖注入设计保证可测试性
 * - 国际化支持
 * 
 * 展示规则：
 * - 今天：只显示"HH:mm"
 * - 昨天：前缀"昨天 " + "HH:mm"
 * - 本年：显示"M月d日"
 * - 往年：显示"yyyy-MM-dd HH:mm"
 * 
 * 技术实现：
 * - 依赖注入Clock与StringProvider保证可测试性
 * - 线程安全的DateTimeFormatter复用
 * - 异常安全处理机制
 * - ActivityRetainedScoped生命周期管理
 */
@Stable
@ActivityRetainedScoped
class NovelDateFormatter
@Inject constructor(
    @Stable
    private val clock: Clock,                            // 可注入FakeClock进行单元测试
    @Stable
    private val stringProvider: StringProvider           // 国际化文本提供者
) {
    
    companion object {
        private const val TAG = "NovelDateFormatter"
    }
    
    // 线程安全、可复用的DateTimeFormatter
    @Stable
    private val parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    @Stable
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    @Stable
    private val monthDayFormatter = DateTimeFormatter.ofPattern("M月d日")
    @Stable
    private val fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    /**
     * 将ISO-8601时间字符串格式化为新闻展示格式
     * 
     * 支持格式：
     * - 输入："2025-05-06T14:30:00"
     * - 输出：根据时间范围自动选择合适格式
     * 
     * @param dateString ISO-8601格式时间字符串
     * @return 格式化后的时间字符串，出错返回空串
     */
    fun parseNewsDate(dateString: String): String = runCatching {
        TimberLogger.d(TAG, "格式化日期: $dateString")
        
        // 1. 解析输入时间字符串
        val localDateTime = try {
            LocalDateTime.parse(dateString, parseFormatter)
        } catch (e: java.time.format.DateTimeParseException) {
            TimberLogger.w(TAG, "主格式解析失败，尝试备用格式: $dateString", e)
            // 尝试解析 "yyyy-MM-dd HH:mm:ss" 格式
            val altFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            try {
                LocalDateTime.parse(dateString, altFormatter)
            } catch (e2: java.time.format.DateTimeParseException) {
                TimberLogger.e(TAG, "所有日期格式解析失败: $dateString", e2)
                throw e2
            }
        }
        val dateTime = localDateTime.atZone(ZoneId.systemDefault())
        val now = ZonedDateTime.now(clock)

        // 2. 根据时间范围选择展示格式
        val result = when {
            // 今天：只显示时间
            dateTime.toLocalDate().isEqual(now.toLocalDate()) -> {
                dateTime.format(timeFormatter)                              // "HH:mm"
            }

            // 昨天：前缀"昨天"
            dateTime.toLocalDate().isEqual(now.toLocalDate().minusDays(1)) -> {
                stringProvider.getString(
                    R.string.yesterday_format,
                    dateTime.format(timeFormatter)
                )                                                          // "昨天 HH:mm"
            }

            // 本年：显示月日
            dateTime.year == now.year -> {
                dateTime.format(monthDayFormatter)                         // "M月d日"
            }

            // 往年：显示完整日期时间
            else -> {
                dateTime.format(fullFormatter)                             // "yyyy-MM-dd HH:mm"
            }
        }
        
        TimberLogger.d(TAG, "格式化完成: $dateString -> $result")
        result
    }.getOrElse { exception ->
        TimberLogger.e(TAG, "日期格式化失败: $dateString", exception)
        ""                                                                 // 异常时返回空串
    }
}
