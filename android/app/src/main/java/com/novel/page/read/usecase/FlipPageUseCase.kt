package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.core.StableThrowable

import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.utils.TimberLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.PageFlipEffect
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.VirtualPage
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * 翻页用例
 * 
 * 负责处理页面翻页逻辑：
 * 1. 防抖处理避免快速重复触发
 * 2. 虚拟页面索引管理和边界检查
 * 3. 平移模式特殊处理避免循环更新
 * 4. 章节边界预加载检查
 * 5. BookDetailPage和ContentPage的切换
 * 6. 到达虚拟列表边界时的章节切换
 */
@Stable
class FlipPageUseCase @Inject constructor(
    private val switchChapterUseCase: SwitchChapterUseCase,
    private val preloadChaptersUseCase: PreloadChaptersUseCase,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.FLIP_PAGE_USE_CASE
        private const val FLIP_COOLDOWN_MS = 300L // 翻页防抖时间
    }

    override fun getServiceTag(): String = TAG

    // 翻页防抖控制
    private var lastFlipTime = 0L

    sealed class FlipResult {
        /** 虚拟页面内翻页成功 */
        data class VirtualPageChanged(
            val newVirtualPageIndex: Int,
            val newVirtualPage: VirtualPage,
            val needsPreloadCheck: Boolean = false
        ) : FlipResult()
        
        /** 章节切换成功 */
        data class ChapterChanged(val switchResult: SwitchChapterUseCase.SwitchResult) : FlipResult()
        
        /** 操作失败 */
        @Stable
        data class Failure(@Stable val error: StableThrowable) : FlipResult()
        
        /** 无操作（已到边界或其他原因） */
        data object NoOp : FlipResult()
        
        /** 需要重建虚拟页面 */
        data class NeedsVirtualPageRebuild(
            val newVirtualPageIndex: Int,
            val newVirtualPage: VirtualPage,
            val needsPreloadCheck: Boolean
        ) : FlipResult()
    }

    /**
     * 执行翻页操作
     * 
     * @param state 当前阅读器状态
     * @param direction 翻页方向
     * @param scope 协程作用域
     * @return 翻页结果
     */
    suspend fun execute(
        state: ReaderState,
        direction: FlipDirection,
        scope: CoroutineScope
    ): FlipResult {
        return executeWithResult("翻页操作") {
        logOperationStart("翻页操作", "方向=$direction, 当前虚拟页面索引=${state.virtualPageIndex}")
        
        // 1. 防抖检查
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFlipTime < FLIP_COOLDOWN_MS) {
            logger.logDebug("翻页被防抖拦截: 距离上次翻页${currentTime - lastFlipTime}ms", TAG)
            return@executeWithResult FlipResult.NoOp
        }
        lastFlipTime = currentTime

        // 2. 检查虚拟页面是否为空
        val virtualPages = state.virtualPages
        if (virtualPages.isEmpty()) {
            logger.logWarning("虚拟页面列表为空，无法翻页", TAG)
            return@executeWithResult FlipResult.NoOp
        }

        // 3. 计算新的虚拟页面索引
        val currentVirtualIndex = state.virtualPageIndex
        val newVirtualIndex = when (direction) {
            FlipDirection.NEXT -> currentVirtualIndex + 1
            FlipDirection.PREVIOUS -> currentVirtualIndex - 1
        }

        logger.logDebug("虚拟页面索引变化: $currentVirtualIndex -> $newVirtualIndex (总页数=${virtualPages.size})", TAG)

        // 4. 检查是否在虚拟页面列表范围内
        val result = if (newVirtualIndex in virtualPages.indices) {
            // 在虚拟页面列表内移动
            handleVirtualPageFlip(state, newVirtualIndex, virtualPages[newVirtualIndex], scope)
        } else {
            // 到达虚拟列表边界，需要切换章节
            logger.logDebug("到达虚拟列表边界，尝试切换章节", TAG)
            handleChapterFlip(state, direction, scope)
        }
        
            logOperationComplete("翻页操作", "结果类型=${result::class.simpleName}")
            result
        }.getOrElse { throwable ->
            val stableError = StableThrowable(throwable)
            TimberLogger.e(TAG, "页面翻转失败", stableError)
            FlipResult.Failure(stableError)
        }
    }

    /**
     * 处理虚拟页面内的翻页
     */
    private suspend fun handleVirtualPageFlip(
        state: ReaderState,
        newVirtualIndex: Int,
        newVirtualPage: VirtualPage,
        scope: CoroutineScope
    ): FlipResult {
        logger.logDebug("虚拟页面内翻页: 索引=$newVirtualIndex, 页面类型=${newVirtualPage::class.simpleName}", TAG)

        // 检查是否为平移模式，平移模式需要特殊处理避免循环更新
        val isSlideMode = state.readerSettings.pageFlipEffect == PageFlipEffect.SLIDE
        logger.logDebug("翻页模式: ${if (isSlideMode) "平移模式" else "其他模式"}", TAG)

        // 检查是否需要预加载
        val needsPreloadCheck = when (newVirtualPage) {
            is VirtualPage.ContentPage -> {
                val pageData = state.loadedChapterData[newVirtualPage.chapterId]
                if (pageData != null) {
                    val isAtChapterBoundary = newVirtualPage.pageIndex == 0 ||
                                            newVirtualPage.pageIndex == pageData.pages.size - 1
                    logger.logDebug("内容页面边界检查: 页面索引=${newVirtualPage.pageIndex}, 总页数=${pageData.pages.size}, 是否边界=$isAtChapterBoundary", TAG)
                    isAtChapterBoundary
                } else {
                    logger.logWarning("未找到章节数据: ${newVirtualPage.chapterId}", TAG)
                    false
                }
            }
            is VirtualPage.BookDetailPage -> {
                logger.logDebug("书籍详情页，无需预加载检查", TAG)
                false
            }
            is VirtualPage.ChapterSection -> {
                logger.logDebug("章节区域页面，无需预加载检查", TAG)
                false
            }
        }

        // 检查是否有新的相邻章节被加载，如果有则需要重建虚拟页面
        if (newVirtualPage is VirtualPage.ContentPage) {
            val currentChapterIndex = state.chapterList.indexOfFirst { it.id == newVirtualPage.chapterId }
            if (currentChapterIndex >= 0) {
                val hasNewAdjacentChapters = preloadChaptersUseCase.checkIfNewAdjacentChaptersLoaded(state, currentChapterIndex)
                if (hasNewAdjacentChapters) {
                    logger.logDebug("检测到新的相邻章节已加载，需要重建虚拟页面", TAG)
                    return FlipResult.NeedsVirtualPageRebuild(
                        newVirtualPageIndex = newVirtualIndex,
                        newVirtualPage = newVirtualPage,
                        needsPreloadCheck = needsPreloadCheck
                    )
                }
            }
        }

        return FlipResult.VirtualPageChanged(
            newVirtualPageIndex = newVirtualIndex,
            newVirtualPage = newVirtualPage,
            needsPreloadCheck = needsPreloadCheck
        )
    }

    /**
     * 处理章节边界的翻页（切换章节）
     */
    private suspend fun handleChapterFlip(
        state: ReaderState,
        direction: FlipDirection,
        scope: CoroutineScope
    ): FlipResult {
        logger.logDebug("处理章节切换: direction=$direction", TAG)
        
        val chapterList = state.chapterList
        val currentChapterIndex = state.currentChapterIndex

        val targetChapterIndex = when (direction) {
            FlipDirection.NEXT -> currentChapterIndex + 1
            FlipDirection.PREVIOUS -> currentChapterIndex - 1
        }

        logger.logDebug("目标章节索引: $currentChapterIndex -> $targetChapterIndex (总章节数=${chapterList.size})", TAG)

        // 检查目标章节是否存在
        if (targetChapterIndex !in chapterList.indices) {
            logger.logDebug("已到达书籍边界，无法继续翻页", TAG)
            return FlipResult.NoOp
        }

        val targetChapter = chapterList[targetChapterIndex]
        logger.logDebug("切换到章节: ${targetChapter.chapterName}", TAG)

        // 执行章节切换
        val switchResult = switchChapterUseCase.execute(state, targetChapter.id, scope, direction)
        return FlipResult.ChapterChanged(switchResult)
    }

    /**
     * 执行预加载检查
     * 当翻页到章节边界时调用
     */
    fun executePreloadCheck(
        state: ReaderState,
        currentVirtualPage: VirtualPage.ContentPage,
        scope: CoroutineScope
    ) {
        logger.logDebug("执行预加载检查: 章节=${currentVirtualPage.chapterId}, 页面=${currentVirtualPage.pageIndex}", TAG)

        val chapterList = state.chapterList
        val currentChapterIndex = chapterList.indexOfFirst { it.id == currentVirtualPage.chapterId }

        if (currentChapterIndex < 0) {
            logger.logWarning("预加载检查失败: 未找到当前章节索引", TAG)
            return
        }

        // 执行预加载检查（简化版本，避免调用不存在的方法）
        logger.logDebug("触发预加载检查", TAG)
        preloadChaptersUseCase.execute(scope, chapterList, currentVirtualPage.chapterId)
    }
}
