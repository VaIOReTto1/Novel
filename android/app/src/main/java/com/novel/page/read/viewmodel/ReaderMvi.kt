package com.novel.page.read.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect
import com.novel.page.read.repository.PageCountCacheData
import com.novel.page.read.repository.ProgressiveCalculationState
import com.novel.utils.TimberLogger

/**
 * Reader模块MVI契约类
 * 
 * 基于核心MVI接口，统一Reader模块的状态管理
 */

/**
 * Reader Intent - 用户意图和系统事件
 * 
 * 基于核心MviIntent接口，包含所有Reader模块的用户操作
 */
sealed class ReaderIntent : MviIntent {
    // 初始化相关
    data class InitReader(val bookId: String, val chapterId: String?) : ReaderIntent()
    data object Retry : ReaderIntent()
    
    // 翻页相关
    data class PageFlip(val direction: FlipDirection) : ReaderIntent()
    data object PreviousChapter : ReaderIntent()
    data object NextChapter : ReaderIntent()
    
    // 章节切换相关
    data class SwitchToChapter(val chapterId: String) : ReaderIntent()
    data class SeekToProgress(val progress: Float) : ReaderIntent()
    
    // 设置相关
    data class UpdateSettings(val settings: ReaderSettings) : ReaderIntent()
    data class UpdateContainerSize(val size: IntSize, val density: Density) : ReaderIntent()
    
    // 菜单和UI相关
    data class ToggleMenu(val show: Boolean) : ReaderIntent()
    data class ShowChapterList(val show: Boolean) : ReaderIntent()
    data class ShowSettingsPanel(val show: Boolean) : ReaderIntent()
    
    // 进度保存相关
    data class SaveProgress(val force: Boolean = false) : ReaderIntent()
    
    // 预加载相关
    data class PreloadChapters(val currentChapterId: String) : ReaderIntent()
    
    // 历史记录相关
    data class SaveToHistory(
        val bookId: String, 
        val chapterId: String,
        val bookTitle: String? = null,
        val author: String? = null,
        val coverUrl: String? = null,
        val chapterTitle: String? = null
    ) : ReaderIntent()

    // UI交互相关
    data class UpdateScrollPosition(val pageIndex: Int) : ReaderIntent()
    data class UpdateSlideIndex(val index: Int) : ReaderIntent()
    /**
     * 显示进度恢复提示
     */
    data class ShowProgressRestoredHint(val show: Boolean) : ReaderIntent()
    
    /**
     * 加载书籍评论
     */
    data class LoadBookReviews(val bookId: String) : ReaderIntent()
    
    /**
     * 评论加载成功
     */
    data class BookReviewsLoadSuccess(val reviews: ImmutableList<BookReview>) : ReaderIntent()
    
    /**
     * 评论加载失败
     */
    data class BookReviewsLoadFailure(val error: String) : ReaderIntent()
}

/**
 * Reader State - 完整的UI状态
 * 
 * 基于核心MviState接口，包含Reader模块的所有状态信息
 */
@Stable
data class ReaderState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    
    // 基础状态
    val bookId: String = "",
    val chapterList: ImmutableList<Chapter> = persistentListOf(),
    val currentChapter: Chapter? = null,
    val currentChapterIndex: Int = 0,
    val bookContent: String = "",
    val readingProgress: Float = 0f,
    
    // 阅读器设置
    val readerSettings: ReaderSettings = ReaderSettings.getDefault(),
    
    // 分页相关状态
    val currentPageData: PageData? = null,
    val currentPageIndex: Int = 0,
    val isSwitchingChapter: Boolean = false,
    val containerSize: IntSize = IntSize.Zero,
    val density: Density? = null,
    
    // 统一翻页模式所需的状态
    val virtualPages: ImmutableList<VirtualPage> = persistentListOf(),
    val virtualPageIndex: Int = 0,
    val loadedChapterData: Map<String, PageData> = emptyMap(),
    
    // 全书分页缓存
    val pageCountCache: PageCountCacheData? = null,
    val paginationState: ProgressiveCalculationState = ProgressiveCalculationState(),
    
    // 相邻章节数据
    val previousChapterData: PageData? = null,
    val nextChapterData: PageData? = null,
    
    // UI状态
    val isMenuVisible: Boolean = false,
    val isChapterListVisible: Boolean = false,
    val isSettingsPanelVisible: Boolean = false,
    val showProgressRestoredHint: Boolean = false,
    
    // 评论相关状态
    val bookReviews: ImmutableList<BookReview> = persistentListOf(),
    val isLoadingReviews: Boolean = false,
    val reviewsError: String? = null
) : MviState {
    
    override val isEmpty: Boolean 
        get() = !isLoading && !hasError && chapterList.isEmpty()
    
    override val isSuccess: Boolean
        get() = !isLoading && !hasError && currentChapter != null
    
    // 扩展属性
    val isFirstChapter: Boolean 
        get() = currentChapterIndex == 0
    
    val isLastChapter: Boolean 
        get() = currentChapterIndex >= chapterList.size - 1
    
    val computedReadingProgress: Float
        get() {
            if (readerSettings.pageFlipEffect == PageFlipEffect.VERTICAL) {
                // 纵向滚动模式下，进度按章节计算
                if (chapterList.isEmpty()) return 0f
                return (currentChapterIndex + 1).toFloat() / chapterList.size.toFloat()
            }

            val cache = pageCountCache ?: return 0f
            if (cache.totalPages <= 0) return 0f

            val chapterRange = cache.chapterPageRanges.find { it.chapterId == currentChapter?.id }

            val globalCurrentPage = if (chapterRange != null) {
                chapterRange.startPage + currentPageIndex
            } else {
                0
            }

            return (globalCurrentPage + 1).toFloat() / cache.totalPages.toFloat()
        }
}

/**
 * Reader Effect - 一次性副作用
 * 
 * 基于核心MviEffect接口，包含Reader模块的所有副作用
 */
sealed class ReaderEffect : MviEffect {
    // 导航相关
    data class NavigateBack(val reason: String = "") : ReaderEffect()
    data class NavigateToBookDetail(val bookId: String) : ReaderEffect()
    data class NavigateToChapter(val chapterId: String) : ReaderEffect()
    
    // 提示相关
    data class ShowToast(val message: String) : ReaderEffect()
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : ReaderEffect()
    
    // 系统相关
    data class SetBrightness(val brightness: Float) : ReaderEffect()
    data class SetKeepScreenOn(val keepOn: Boolean) : ReaderEffect()
    data class TriggerHapticFeedback(val type: HapticFeedbackType = HapticFeedbackType.LIGHT) : ReaderEffect()
    
    // 分享相关
    data class ShareContent(val content: String, val title: String) : ReaderEffect()
    
    // 错误处理
    data class ShowErrorDialog(val title: String, val message: String, val canRetry: Boolean = true) : ReaderEffect()
    
    // 进度相关
    data class SaveProgressCompleted(val success: Boolean) : ReaderEffect()
    
    // 预加载相关
    data class PreloadCompleted(val chapterId: String, val success: Boolean) : ReaderEffect()
}

/**
 * 触觉反馈类型
 */
enum class HapticFeedbackType {
    LIGHT,
    MEDIUM,
    HEAVY
}

/**
 * 章节信息数据类
 *
 * @property id 章节唯一标识
 * @property chapterName 章节名称
 * @property chapterNum 章节序号（可选）
 * @property isVip VIP标识（"0"为免费，"1"为VIP）
 */
@Immutable
data class Chapter(
    val id: String,
    val chapterName: String,
    val chapterNum: String? = null,
    val isVip: String = "0"
)

/**
 * 翻页方向
 */
enum class FlipDirection {
    PREVIOUS,
    NEXT
}

/**
 * 虚拟页面，用于统一所有翻页模式
 */
sealed class VirtualPage {
    /**
     * 代表书籍详情页
     */
    data object BookDetailPage : VirtualPage()

    /**
     * 代表一个实际的内容页
     * @param chapterId 所属章节ID
     * @param pageIndex 在该章节内的页码 (从0开始)
     */
    data class ContentPage(val chapterId: String, val pageIndex: Int) : VirtualPage()

    /**
     * 代表一个完整的章节，主要用于纵向滚动模式
     */
    data class ChapterSection(val chapterId: String) : VirtualPage()
}

/**
 * 章节缓存数据
 */
@Stable
data class ChapterCache(
    val chapter: Chapter,
    val content: String,
    var pageData: PageData? = null
)

/**
 * 单页数据
 */
@Stable
data class PageData(
    val chapterId: String,
    val chapterName: String,
    val content: String,
    val pages: ImmutableList<String>,
    val isFirstPage: Boolean = false,
    val isLastPage: Boolean = false,
    val isFirstChapter: Boolean = false,
    val isLastChapter: Boolean = false,
    val nextChapterData: PageData? = null,
    val previousChapterData: PageData? = null,
    val bookInfo: BookInfo? = null, // 书籍信息，用于第0页
    val hasBookDetailPage: Boolean = false // 是否有书籍详情页
) {
    val pageCount: Int get() = pages.size

    @Immutable
    data class BookInfo(
        val bookId: String,
        val bookName: String,
        val authorName: String,
        val bookDesc: String,
        val picUrl: String,
        val visitCount: Long,
        val wordCount: Int,
        val categoryName: String
    )
}

/**
 * 书籍评论数据类
 * 
 * 用于在阅读器书籍详情页显示用户评论
 */
@Stable
data class BookReview(
    val id: String,
    val content: String,
    val rating: Int, // 1-5星评级
    val readTime: String,
    val userName: String,
    val userPhoto: String? = null,
    val commentTime: String? = null
)

/**
 * 背景主题配置类
 *
 * 预定义的阅读背景主题，每个主题都包含优化搭配的背景色和文字色
 * 确保在不同光线环境下都有良好的可读性和舒适度
 *
 * @param name 主题名称，显示在设置界面
 * @param backgroundColor 背景颜色，影响整个阅读区域
 * @param textColor 文字颜色，与背景色形成适当对比度
 */
@Immutable
data class BackgroundTheme(
    val name: String,
    val backgroundColor: Color,
    val textColor: Color
)

/**
 * 状态信息，通过 CompositionLocal 提供给子组件
 */
@Stable
data class ReaderInfo(
    val paginationState: ProgressiveCalculationState,
    val pageCountCache: PageCountCacheData?,
    val currentChapter: Chapter?,
    val perChapterPageIndex: Int
)
