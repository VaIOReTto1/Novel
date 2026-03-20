package com.novel.page.read.service

import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.TimberLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 历史记录数据模型
 */
@Serializable
data class HistoryItem(
    val id: String,
    val bookId: String,
    val chapterId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val chapterTitle: String,
    val readProgress: Float,
    val lastReadTime: Long,
    val totalChapters: Int,
    val currentChapter: Int
)

/**
 * 历史记录服务接口
 */
interface HistoryService {
    /**
     * 保存阅读历史记录
     */
    suspend fun saveHistory(
        bookId: String, 
        chapterId: String, 
        bookTitle: String? = null,
        author: String? = null,
        coverUrl: String? = null,
        chapterTitle: String? = null
    )
    
    /**
     * 获取所有历史记录
     */
    suspend fun getAllHistory(): List<HistoryItem>
    
    /**
     * 删除指定历史记录
     */
    suspend fun deleteHistory(bookId: String)
    
    /**
     * 清空所有历史记录
     */
    suspend fun clearAllHistory()
}

/**
 * 历史记录服务实现类
 */
@Singleton
class HistoryServiceImpl @Inject constructor(
    private val userDefaults: NovelUserDefaults
) : HistoryService {
    
    companion object {
        private const val TAG = "HistoryService"
        private const val HISTORY_KEY = "reading_history"
        private val json = Json { ignoreUnknownKeys = true }
    }
    
    override suspend fun saveHistory(
        bookId: String, 
        chapterId: String, 
        bookTitle: String?,
        author: String?,
        coverUrl: String?,
        chapterTitle: String?
    ) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "保存历史记录: bookId=$bookId, chapterId=$chapterId, title=$bookTitle")
            
            val currentHistory = getAllHistoryInternal().toMutableList()
            
            // 移除已存在的相同书籍记录
            currentHistory.removeAll { it.bookId == bookId }
            
            // 创建新的历史记录项
            val newHistoryItem = HistoryItem(
                id = "${bookId}_${System.currentTimeMillis()}",
                bookId = bookId,
                chapterId = chapterId,
                title = bookTitle ?: getMockBookTitle(bookId),
                author = author ?: getMockAuthor(bookId),
                coverUrl = coverUrl ?: getMockCoverUrl(bookId),
                chapterTitle = chapterTitle ?: getMockChapterTitle(chapterId),
                readProgress = getMockProgress(),
                lastReadTime = System.currentTimeMillis(),
                totalChapters = getMockTotalChapters(),
                currentChapter = getMockCurrentChapter(chapterId)
            )
            
            // 添加到列表开头（最新的在前面）
            currentHistory.add(0, newHistoryItem)
            
            // 限制历史记录数量（最多保留100条）
            if (currentHistory.size > 100) {
                currentHistory.removeAt(currentHistory.size - 1)
            }
            
            // 序列化并保存
            val historyJson = json.encodeToString(currentHistory)
            userDefaults.setString(HISTORY_KEY, historyJson)
            
            TimberLogger.d(TAG, "历史记录保存成功，当前总数: ${currentHistory.size}")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "保存历史记录失败", e)
        }
    }
    
    override suspend fun getAllHistory(): List<HistoryItem> = withContext(Dispatchers.IO) {
        return@withContext getAllHistoryInternal()
    }
    
    private fun getAllHistoryInternal(): List<HistoryItem> {
        return try {
            val historyJson = userDefaults.getString(HISTORY_KEY)
            if (historyJson.isNullOrEmpty()) {
                TimberLogger.d(TAG, "没有找到历史记录")
                emptyList()
            } else {
                val history = json.decodeFromString<List<HistoryItem>>(historyJson)
                TimberLogger.d(TAG, "读取历史记录成功，数量: ${history.size}")
                history
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "读取历史记录失败", e)
            emptyList()
        }
    }
    
    override suspend fun deleteHistory(bookId: String) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "删除历史记录: bookId=$bookId")
            
            val currentHistory = getAllHistoryInternal().toMutableList()
            currentHistory.removeAll { it.bookId == bookId }
            
            val historyJson = json.encodeToString(currentHistory)
            userDefaults.setString(HISTORY_KEY, historyJson)
            
            TimberLogger.d(TAG, "历史记录删除成功")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "删除历史记录失败", e)
        }
    }
    
    override suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "清空所有历史记录")
            userDefaults.remove(HISTORY_KEY)
            TimberLogger.d(TAG, "历史记录清空成功")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清空历史记录失败", e)
        }
    }
    
    // Mock数据生成方法，用于补充RN端缺少的字段
    private fun getMockBookTitle(bookId: String): String {
        val titles = listOf(
            "斗破苍穹", "完美世界", "遮天", "武动乾坤", "大主宰",
            "元尊", "斗罗大陆", "神墓", "盘龙", "星辰变"
        )
        return titles[bookId.hashCode().rem(titles.size).let { if (it < 0) -it else it }]
    }
    
    private fun getMockAuthor(bookId: String): String {
        val authors = listOf(
            "天蚕土豆", "辰东", "唐家三少", "我吃西红柿", "梦入神机",
            "烽火戏诸侯", "猫腻", "忘语", "耳根", "净无痕"
        )
        return authors[bookId.hashCode().rem(authors.size).let { if (it < 0) -it else it }]
    }
    
    private fun getMockCoverUrl(bookId: String): String {
        return "https://example.com/covers/${bookId}.jpg"
    }
    
    private fun getMockChapterTitle(chapterId: String): String {
        val chapterTitles = listOf(
            "初入修炼", "突破境界", "遇见强敌", "奇遇机缘", "生死危机",
            "实力大增", "新的挑战", "神秘宝物", "师父传授", "最终决战"
        )
        return chapterTitles[chapterId.hashCode().rem(chapterTitles.size).let { if (it < 0) -it else it }]
    }
    
    private fun getMockProgress(): Float {
        return (20..95).random() / 100f
    }
    
    private fun getMockTotalChapters(): Int {
        return (100..2000).random()
    }
    
    private fun getMockCurrentChapter(chapterId: String): Int {
        return chapterId.hashCode().rem(500).let { if (it < 0) -it else it } + 1
    }
}
