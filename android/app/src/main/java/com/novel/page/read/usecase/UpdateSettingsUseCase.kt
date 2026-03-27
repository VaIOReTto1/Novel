package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import java.util.Locale
import androidx.compose.ui.graphics.toArgb
import com.novel.page.read.service.SettingsService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.PageFlipEffect
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import javax.inject.Inject

/**
 * 更新设置用例
 * 
 * 负责处理阅读器设置更新的完整流程：
 * 1. 保存新设置到本地存储
 * 2. 检查是否需要重新分页
 * 3. 重新分页并恢复阅读进度
 * 4. 处理分页失败的回退逻辑
 */
@Stable
class UpdateSettingsUseCase @Inject constructor(
    private val settingsService: SettingsService,
    private val paginateChapterUseCase: PaginateChapterUseCase,
    private val splitContentUseCase: SplitContentUseCase,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.UPDATE_SETTINGS_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    sealed class UpdateResult {
        data class Success(val newPageData: PageData?, val newPageIndex: Int = 0) : UpdateResult()
        // No failure case for now, as saveSettings is fire-and-forget
    }

    /**
     * 执行设置更新
     * 
     * @param newSettings 新的阅读器设置
     * @param state 当前阅读器状态
     * @return 更新结果
     */
    suspend fun execute(
        newSettings: ReaderSettings,
        state: ReaderState
    ): UpdateResult {
        return executeWithResult("更新设置") {
        logOperationStart("更新设置", "开始更新阅读器设置")
        
        logger.logDebug("新设置详情:", TAG)
        logger.logDebug("  - 字体大小: ${newSettings.fontSize}sp", TAG)
        logger.logDebug("  - 亮度: ${(newSettings.brightness * 100).toInt()}%", TAG)
        logger.logDebug("  - 背景颜色: ${colorToHex(newSettings.backgroundColor)}", TAG)
        logger.logDebug("  - 文字颜色: ${colorToHex(newSettings.textColor)}", TAG)
        logger.logDebug("  - 翻页效果: ${newSettings.pageFlipEffect}", TAG)
        
        logger.logDebug("保存设置到本地存储", TAG)
        settingsService.saveSettings(newSettings)

        val needsRepagination = needsRepagination(state.readerSettings, newSettings, state)
        logger.logDebug("是否需要重新分页: $needsRepagination", TAG)

        if (!needsRepagination) {
            logger.logDebug("无需重新分页，直接返回成功", TAG)
            return@executeWithResult UpdateResult.Success(null)
        }

        val currentChapter = state.currentChapter
        if (currentChapter == null) {
            logger.logWarning("当前章节为空，无法重新分页", TAG)
            return@executeWithResult UpdateResult.Success(null)
        }
        
        val chapterContent = state.bookContent
        val density = state.density
        if (density == null) {
            logger.logWarning("屏幕密度为空，无法重新分页", TAG)
            return@executeWithResult UpdateResult.Success(null)
        }

        // 计算当前进度以便重新分页后恢复
        val currentPageData = state.currentPageData
        val currentProgress = if (currentPageData?.pages?.isNotEmpty() == true && state.currentPageIndex >= 0) {
            (state.currentPageIndex.toFloat() + 1) / currentPageData.pages.size.toFloat()
        } else 0f
        
        logger.logDebug(
            "重新分页前当前进度: ${(currentProgress * 100).toInt()}% " +
            "(页面${state.currentPageIndex + 1}/${currentPageData?.pages?.size ?: 0})",
            TAG
        )

        // 创建用于重新分页的状态
        val stateForSplitting = state.copy(
            readerSettings = newSettings,
            currentPageIndex = state.currentPageIndex
        )

        try {
            logger.logDebug("开始使用SplitContentUseCase重新分页", TAG)
            // Use SplitContentUseCase for re-pagination
            val splitResult = splitContentUseCase.execute(
                state = stateForSplitting,
                restoreProgress = currentProgress,
                includeAdjacentChapters = true
            )

            when (splitResult) {
                is SplitContentUseCase.SplitResult.Success -> {
                    logger.logDebug("重新分页成功: 新页数=${splitResult.pageData.pages.size}, 恢复到页面=${splitResult.safePageIndex + 1}", TAG)
                    val result = UpdateResult.Success(splitResult.pageData, splitResult.safePageIndex)
                    logOperationComplete("更新设置", "设置更新成功，重新分页完成")
                    result
                }
                is SplitContentUseCase.SplitResult.Failure -> {
                    logger.logWarning("SplitContentUseCase失败，回退到基础分页", TAG)
                    // 回退到基础分页
                    val newPageData = paginateChapterUseCase.execute(
                        chapter = currentChapter,
                        content = chapterContent,
                        readerSettings = newSettings,
                        containerSize = state.containerSize,
                        density = density,
                        isFirstChapter = state.isFirstChapter,
                        isLastChapter = state.isLastChapter
                    )
                    
                    val restoredPageIndex = if (newPageData.pages.isNotEmpty()) {
                        (currentProgress * newPageData.pages.size).toInt()
                            .coerceIn(0, newPageData.pages.size - 1)
                    } else 0
                    
                    logger.logDebug("基础分页完成: 新页数=${newPageData.pages.size}, 恢复到页面=${restoredPageIndex + 1}", TAG)
                    val result = UpdateResult.Success(newPageData, restoredPageIndex)
                    logOperationComplete("更新设置", "设置更新成功，基础分页完成")
                    result
                }
            }
        } catch (e: Exception) {
            logger.logError("重新分页失败", e, TAG)
            // 如果分页失败，返回成功但无新页面数据
            UpdateResult.Success(null)
        }
        }.getOrElse { throwable ->
            logger.logError("设置更新失败", throwable as? Exception ?: Exception(throwable), TAG)
            UpdateResult.Success(null)
        }
    }

    /**
     * 检查是否需要重新分页
     * 根据设置变化判断是否影响页面布局
     */
    private fun needsRepagination(oldSettings: ReaderSettings, newSettings: ReaderSettings, state: ReaderState): Boolean {
        if (state.containerSize.width == 0 || state.containerSize.height == 0) {
            logger.logDebug("容器尺寸无效，跳过重新分页", TAG)
            return false
        }
        
        val fontSizeChanged = oldSettings.fontSize != newSettings.fontSize
        val backgroundColorChanged = oldSettings.backgroundColor != newSettings.backgroundColor
        val pageFlipEffectChanged = oldSettings.pageFlipEffect != newSettings.pageFlipEffect &&
                (oldSettings.pageFlipEffect.isVertical() || newSettings.pageFlipEffect.isVertical())
        
        logger.logDebug("分页影响因素检查:", TAG)
        logger.logDebug("  - 字体大小变化: $fontSizeChanged (${oldSettings.fontSize}sp -> ${newSettings.fontSize}sp)", TAG)
        logger.logDebug("  - 背景颜色变化: $backgroundColorChanged (${colorToHex(oldSettings.backgroundColor)} -> ${colorToHex(newSettings.backgroundColor)})", TAG)
        logger.logDebug("  - 翻页效果变化影响布局: $pageFlipEffectChanged", TAG)
        
        return fontSizeChanged || backgroundColorChanged || pageFlipEffectChanged
    }
    
    /**
     * 将Color对象转换为十六进制字符串，用于日志显示
     */
    private fun colorToHex(color: androidx.compose.ui.graphics.Color): String {
        return try {
            String.format(Locale.US, "#%08X", color.toArgb())
        } catch (e: Exception) {
            "INVALID_COLOR"
        }
    }
}

private fun PageFlipEffect.isVertical() = this == PageFlipEffect.VERTICAL
