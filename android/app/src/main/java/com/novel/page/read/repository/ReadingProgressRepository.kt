package com.novel.page.read.repository

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.novel.page.read.viewmodel.PageFlipEffect
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 阅读进度数据模型
 * 
 * 保存用户在每本书的阅读位置和进度信息
 */
data class ReadingProgressData(
    /** 书籍唯一标识 */
    val bookId: String,
    /** 章节唯一标识 */
    val chapterId: String,
    /** 章节内页码索引（从0开始） */
    val pageIndex: Int,
    /** 全书页码索引（可选，用于全书定位） */
    val globalPageIndex: Int = 0,
    /** 章节内进度百分比（0.0-1.0） */
    val chapterProgress: Float = 0f,
    /** 全书进度百分比（0.0-1.0） */
    val globalProgress: Float = 0f,
    /** 最后阅读时间戳 */
    val lastReadTime: Long = System.currentTimeMillis(),
    /** 翻页效果设置 */
    val pageFlipEffect: PageFlipEffect = PageFlipEffect.PAGECURL
)

/**
 * 阅读器设置数据模型
 * 
 * 保存用户的阅读偏好设置
 */
data class ReaderSettingsData(
    /** 字体大小（sp单位） */
    val fontSize: Int = 16,
    /** 屏幕亮度（0.0-1.0） */
    val brightness: Float = 0.5f,
    /** 背景颜色（Color序列化为字符串） */
    val backgroundColor: String = "#FFF5F5DC",
    /** 文字颜色（Color序列化为字符串） */
    val textColor: String = "#FF000000",
    /** 翻页效果 */
    val pageFlipEffect: PageFlipEffect = PageFlipEffect.PAGECURL
)

/**
 * 阅读进度持久化Repository
 * 
 * 核心功能：
 * - 保存和恢复用户阅读进度
 * - 管理阅读器个性化设置
 * - 支持翻页模式切换时的状态保留
 * - 提供批量操作接口
 * 
 * 技术特点：
 * - 使用JSON序列化存储复杂数据
 * - 协程异步操作避免阻塞UI
 * - 异常安全处理，失败静默处理
 * - 支持增量更新和批量操作
 */
@Singleton
@Stable
class ReadingProgressRepository @Inject constructor(
    /** 用户配置存储服务 */
    private val userDefaults: NovelUserDefaults
) {
    
    companion object {
        private const val TAG = "ReadingProgressRepo"
        private const val READING_PROGRESS_KEY = "reading_progress_data"
        private const val READER_SETTINGS_KEY = "reader_settings_data"
    }
    
    /** JSON序列化工具 */
    private val gson = Gson()

    /**
     * 保存阅读进度
     * 
     * 采用增量更新策略，只更新指定书籍的进度
     * 不影响其他书籍的进度数据
     */
    suspend fun saveReadingProgress(progress: ReadingProgressData) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "保存阅读进度: bookId=${progress.bookId}, chapter=${progress.chapterId}, page=${progress.pageIndex}")
            val progressMap = getAllReadingProgress().toMutableMap()
            progressMap[progress.bookId] = progress
            val json = gson.toJson(progressMap)
            userDefaults.setString(READING_PROGRESS_KEY, json)
            TimberLogger.d(TAG, "阅读进度保存成功")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "保存阅读进度失败", e)
        }
    }

    /**
     * 获取指定书籍的阅读进度
     * 
     * @param bookId 书籍ID
     * @return 阅读进度数据，未找到时返回null
     */
    suspend fun getReadingProgress(bookId: String): ReadingProgressData? = withContext(Dispatchers.IO) {
        try {
            val progressMap = getAllReadingProgress()
            val progress = progressMap[bookId]
            TimberLogger.d(TAG, "获取阅读进度: bookId=$bookId, found=${progress != null}")
            progress
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取阅读进度失败: bookId=$bookId", e)
            null
        }
    }

    /**
     * 获取所有阅读进度
     * 
     * 私有方法，用于内部数据操作
     * 解析失败时返回空Map，确保程序稳定性
     */
    private suspend fun getAllReadingProgress(): Map<String, ReadingProgressData> = withContext(Dispatchers.IO) {
        try {
            val json = userDefaults.getString(READING_PROGRESS_KEY) ?: return@withContext emptyMap()
            val type = object : TypeToken<Map<String, ReadingProgressData>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "解析阅读进度数据失败，返回空数据", e)
            emptyMap()
        }
    }

    /**
     * 保存阅读器设置
     * 
     * 全局设置，影响所有书籍的阅读体验
     */
    suspend fun saveReaderSettings(settings: ReaderSettingsData) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "保存阅读器设置: 字体${settings.fontSize}sp, 翻页效果${settings.pageFlipEffect}")
            val json = gson.toJson(settings)
            userDefaults.setString(READER_SETTINGS_KEY, json)
            TimberLogger.d(TAG, "阅读器设置保存成功")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "保存阅读器设置失败", e)
        }
    }

    /**
     * 获取阅读器设置
     * 
     * @return 阅读器设置数据，未设置时返回null（将使用默认值）
     */
    suspend fun getReaderSettings(): ReaderSettingsData? = withContext(Dispatchers.IO) {
        try {
            val json = userDefaults.getString(READER_SETTINGS_KEY) ?: return@withContext null
            val settings = gson.fromJson(json, ReaderSettingsData::class.java)
            TimberLogger.d(TAG, "获取阅读器设置成功")
            settings
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取阅读器设置失败", e)
            null
        }
    }

    /**
     * 清理指定书籍的阅读进度
     * 
     * 用户删除书籍或重置进度时调用
     */
    suspend fun clearReadingProgress(bookId: String) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "清理阅读进度: bookId=$bookId")
            val progressMap = getAllReadingProgress().toMutableMap()
            val removed = progressMap.remove(bookId)
            if (removed != null) {
                val json = gson.toJson(progressMap)
                userDefaults.setString(READING_PROGRESS_KEY, json)
                TimberLogger.d(TAG, "阅读进度清理成功")
            } else {
                TimberLogger.d(TAG, "阅读进度不存在，无需清理")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理阅读进度失败: bookId=$bookId", e)
        }
    }

    /**
     * 清理所有阅读进度
     * 
     * 用户重置应用或清理数据时调用
     */
    suspend fun clearAllReadingProgress() = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "清理所有阅读进度")
            userDefaults.remove(READING_PROGRESS_KEY)
            TimberLogger.d(TAG, "所有阅读进度清理完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理所有阅读进度失败", e)
        }
    }

    /**
     * 更新指定书籍的翻页模式
     * 
     * 用于模式切换时保留当前阅读位置
     * 只更新翻页效果，不影响阅读进度
     */
    suspend fun updatePageFlipEffect(bookId: String, pageFlipEffect: PageFlipEffect) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "更新翻页效果: bookId=$bookId, effect=$pageFlipEffect")
            val progressMap = getAllReadingProgress().toMutableMap()
            val existingProgress = progressMap[bookId]
            if (existingProgress != null) {
                progressMap[bookId] = existingProgress.copy(
                    pageFlipEffect = pageFlipEffect,
                    lastReadTime = System.currentTimeMillis()
                )
                val json = gson.toJson(progressMap)
                userDefaults.setString(READING_PROGRESS_KEY, json)
                TimberLogger.d(TAG, "翻页效果更新成功")
            } else {
                TimberLogger.d(TAG, "书籍进度不存在，跳过翻页效果更新")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "更新翻页效果失败: bookId=$bookId", e)
        }
    }

    /**
     * 批量更新多个书籍的翻页模式
     * 
     * 全局设置更改时使用，同步所有书籍的翻页效果
     * 保持用户体验的一致性
     */
    suspend fun updateAllBooksPageFlipEffect(pageFlipEffect: PageFlipEffect) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "批量更新翻页效果: effect=$pageFlipEffect")
            val progressMap = getAllReadingProgress().toMutableMap()
            var updateCount = 0
            progressMap.forEach { (bookId, progress) ->
                progressMap[bookId] = progress.copy(pageFlipEffect = pageFlipEffect)
                updateCount++
            }
            if (updateCount > 0) {
                val json = gson.toJson(progressMap)
                userDefaults.setString(READING_PROGRESS_KEY, json)
                TimberLogger.d(TAG, "批量更新完成，影响$updateCount 本书籍")
            } else {
                TimberLogger.d(TAG, "无阅读进度数据，跳过批量更新")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "批量更新翻页效果失败", e)
        }
    }
} 
