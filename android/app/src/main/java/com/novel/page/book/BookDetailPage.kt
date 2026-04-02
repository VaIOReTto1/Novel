package com.novel.page.book

import com.novel.core.StableThrowable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.novel.page.book.components.AuthorSection
import com.novel.page.book.components.BookActionSection
import com.novel.page.book.components.BookCoverSection
import com.novel.page.book.components.BookDescriptionSection
import com.novel.page.book.components.BookReviewsSection
import com.novel.page.book.components.BookStatsSection
import com.novel.page.book.components.BookTitleSection
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.book.viewmodel.BookDetailViewModel
import com.novel.page.book.viewmodel.BookDetailEffect
import com.novel.page.component.FlipBookAnimationController
import com.novel.page.component.LoadingStateComponent
import com.novel.page.component.ViewState
import com.novel.ui.theme.NovelDesignTokens
import com.novel.utils.wdp
import com.novel.utils.NavViewModel
import com.novel.utils.TimberLogger
import android.widget.Toast
import kotlinx.collections.immutable.toImmutableList

/**
 * 书籍详情页面组件
 * 
 * 作为小说应用的核心页面之一，提供完整的书籍信息展示：
 * - 书籍封面、标题、作者等基本信息
 * - 字数、分类、阅读量等统计数据
 * - 简介内容和用户评价展示
 * - 支持3D翻书动画的无缝体验
 * - iOS风格的侧滑返回交互
 * - 左滑进入阅读器功能
 * 
 * 性能优化特性：
 * - LoadingStateComponent统一状态管理
 * - 内容分离减少不必要重组
 * - remember缓存避免重复计算
 * - 使用优化的@Composable状态访问方法提升skippable比例
 * 
 * @param bookId 书籍唯一标识符
 * @param viewModel 书籍详情视图模型，管理数据和状态
 * @param flipBookController 3D翻书动画控制器，支持动画交互
 * @param onNavigateToReader 导航到阅读器的回调函数
 */
@Composable
fun BookDetailPage(
    bookId: String,
    flipBookController: FlipBookAnimationController? = null,
    onNavigateToReader: ((bookId: String, chapterId: String?) -> Unit)? = null
) {
    val viewModel: BookDetailViewModel = hiltViewModel()
    // 性能优化：使用 StateAdapter 创建稳定状态，使用新的@Composable状态访问方法
    val adapter = viewModel.adapter
    
    // 使用优化的@Composable状态访问方法替代unstable的Flow.collectAsState()
    val isLoading by adapter.createLoadingState()
    val error by adapter.createErrorState()
    val isSuccess by adapter.createSuccessState()
    val bookInfo by adapter.bookInfoState()
    val lastChapter by adapter.lastChapterState()
    val reviews by adapter.reviewsState()
    val isDescriptionExpanded by adapter.isDescriptionExpandedState()
    val isInBookshelf by adapter.isInBookshelfState()
    val isAuthorFollowed by adapter.isAuthorFollowedState()
    
    val context = LocalContext.current
    rememberCoroutineScope()

    // 初始化书籍详情数据
    LaunchedEffect(bookId) {
        TimberLogger.d("BookDetailPage", "开始加载书籍详情: $bookId")
        viewModel.loadBookDetail(bookId)
    }
    
    // 性能优化：缓存副作用处理回调
    val handleNavigateToReader = remember(flipBookController, onNavigateToReader) { { bookId: String, chapterId: String? ->
        TimberLogger.d("BookDetailPage", "导航到阅读器: bookId=$bookId, chapterId=$chapterId")
        // 把当前的 FlipBookAnimationController 暂存到全局
        NavViewModel.setFlipBookController(flipBookController)
        onNavigateToReader?.invoke(bookId, chapterId)
    } }
    
    val handleShowToast = remember(context) { { message: String ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    } }
    
    val handleShareBook = remember(context) { { title: String ->
        // TODO: 实现分享功能
        TimberLogger.d("BookDetailPage", "分享书籍: $title")
        Toast.makeText(context, "分享功能待实现", Toast.LENGTH_SHORT).show()
    } }
    
    val handleShowLoadingDialog = remember { {
        // TODO: 显示加载对话框
        TimberLogger.d("BookDetailPage", "显示加载对话框")
    } }
    
    val handleHideLoadingDialog = remember { {
        // TODO: 隐藏加载对话框
        TimberLogger.d("BookDetailPage", "隐藏加载对话框")
    } }
    
    val handleHapticFeedback = remember { {
        // TODO: 触发震动反馈
        TimberLogger.d("BookDetailPage", "触发震动反馈")
    } }
    
    // 处理副作用Effect
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            TimberLogger.d("BookDetailPage", "处理Effect: ${effect::class.simpleName}")
            when (effect) {
                is BookDetailEffect.NavigateToReader -> {
                    handleNavigateToReader(effect.bookId, effect.chapterId)
                }
                is BookDetailEffect.ShowToast -> {
                    handleShowToast(effect.message)
                }
                is BookDetailEffect.ShareBook -> {
                    handleShareBook(effect.title)
                }
                is BookDetailEffect.ShowLoadingDialog -> {
                    handleShowLoadingDialog()
                }
                is BookDetailEffect.HideLoadingDialog -> {
                    handleHideLoadingDialog()
                }
                is BookDetailEffect.TriggerHapticFeedback -> {
                    handleHapticFeedback()
                }
            }
        }
    }

    // 性能优化：缓存重试回调
    val handleRetry = remember(viewModel, bookId) { {
        TimberLogger.d("BookDetailPage", "重试加载书籍详情: $bookId")
        viewModel.loadBookDetail(bookId)
    } }
    
    // 性能优化：使用细粒度状态访问，避免不必要的重组
    val hasError = remember(error) { error != null }
    val isEmpty = remember(bookInfo, isLoading, error) { bookInfo == null && !isLoading && error == null }
    
    // 性能优化：使用 remember 避免重复创建适配器对象，并稳定依赖项
    val loadingStateComponent by remember(
        hasError,
        isEmpty,
        error,
        isLoading,
        bookId,
        handleRetry
    ) {
        derivedStateOf {
            object : LoadingStateComponent {
                override val loading: Boolean get() = isLoading
                override val containsCancelable: Boolean get() = false
                override val viewState: ViewState
                    get() = when {
                        hasError -> ViewState.Error(StableThrowable(Exception(error)))
                        isEmpty -> ViewState.Empty
                        else -> ViewState.Idle
                    }

                override fun showLoading(show: Boolean) {
                    // 状态由ViewModel管理，无需实现
                }

                override fun cancelLoading() {
                    // 无需实现
                }

                override fun showViewState(viewState: ViewState) {
                    // 状态由ViewModel管理，无需实现
                }

                override fun retry() {
                    handleRetry()
                }
            }
        }
    }

    // 性能优化：检查动画状态，避免频繁检查
    val isAnimating by remember(flipBookController?.animationState) {
        derivedStateOf {
            flipBookController?.animationState?.isAnimating == true
        }
    }

    // 左滑进入阅读器的回调函数
    val handleLeftSwipeToReader: () -> Unit = {
        TimberLogger.d("BookDetailPage", "左滑进入阅读器: bookId=$bookId")
        // 把当前的 FlipBookAnimationController 暂存到全局
        NavViewModel.setFlipBookController(flipBookController)
        onNavigateToReader?.invoke(bookId, null)
    }

    // 根据是否在动画状态使用统一的LoadingStateComponent，但传递不同参数
    LoadingStateComponent(
        component = loadingStateComponent,
        modifier = Modifier.fillMaxSize(),
            backgroundColor = NovelDesignTokens.color("color.bg.canvas"),
        flipBookController = flipBookController,
        onLeftSwipeToReader = handleLeftSwipeToReader
    ) {
        // 前景内容 - 书籍详情页面
        Box(
            modifier = Modifier
                .fillMaxSize()
                    .background(NovelDesignTokens.color("color.bg.canvas"))
        ) {
            // 性能优化：只在成功状态下渲染内容，避免不必要的组合
            if (isSuccess) {
                TimberLogger.d("BookDetailPage", "渲染书籍详情内容")
                
                // 性能优化：缓存BookDetailContent的回调函数
                val onFollowAuthor = remember(viewModel) { { authorName: String ->
                    viewModel.sendIntent(
                        com.novel.page.book.viewmodel.BookDetailIntent.FollowAuthor(authorName)
                    )
                } }
                
                val onStartReading = remember(viewModel) { { bookId: String, chapterId: String? ->
                    viewModel.sendIntent(
                        com.novel.page.book.viewmodel.BookDetailIntent.StartReading(bookId, chapterId)
                    )
                } }
                
                val onAddToBookshelf = remember(viewModel) { { bookId: String ->
                    viewModel.sendIntent(
                        com.novel.page.book.viewmodel.BookDetailIntent.AddToBookshelf(bookId)
                    )
                } }
                
                val onShareBook = remember(viewModel) { { bookId: String, bookName: String ->
                    viewModel.sendIntent(
                        com.novel.page.book.viewmodel.BookDetailIntent.ShareBook(bookId, bookName)
                    )
                } }
                
                val onToggleDescription = remember(viewModel) { {
                    viewModel.sendIntent(
                        com.novel.page.book.viewmodel.BookDetailIntent.ToggleDescriptionExpanded
                    )
                } }
                
                val onRemoveFromBookshelf = remember(viewModel) { { bookId: String ->
                    viewModel.sendIntent(
                        com.novel.page.book.viewmodel.BookDetailIntent.RemoveFromBookshelf(bookId)
                    )
                } }
                
                val onMoreReviewsClick = remember(bookId) { {
                    NavViewModel.navigateToCommentPage(bookId)
                } }
                
                // 使用优化的BookDetailContent，直接传递细粒度状态
                BookDetailContent(
                    bookInfo = bookInfo,
                    lastChapter = lastChapter,
                    reviews = reviews,
                    isDescriptionExpanded = isDescriptionExpanded,
                    isInBookshelf = isInBookshelf,
                    isAuthorFollowed = isAuthorFollowed,
                    onFollowAuthor = onFollowAuthor,
                    onStartReading = onStartReading,
                    onAddToBookshelf = onAddToBookshelf,
                    onRemoveFromBookshelf = onRemoveFromBookshelf,
                    onShareBook = onShareBook,
                    onToggleDescription = onToggleDescription,
                    onMoreReviewsClick = onMoreReviewsClick
                )
            }
        }
    }
}

/**
 * 书籍详情内容组件（优化版本）
 * 
 * 分离的内容渲染组件，提升性能和可维护性：
 * - 封面展示区域
 * - 标题和作者信息
 * - 统计数据展示
 * - 简介和评价内容
 * 
 * 性能优化特性：
 * - 接受细粒度的状态参数而不是大的复合状态对象
 * - 减少不必要的状态转换和重组
 * - 使用stable的参数类型
 * 
 * @param bookInfo 书籍基本信息
 * @param lastChapter 最新章节信息
 * @param reviews 用户评价列表
 * @param isDescriptionExpanded 简介是否展开
 * @param isInBookshelf 是否在书架中
 * @param isAuthorFollowed 是否关注作者
 * @param onFollowAuthor 关注作者回调
 * @param onStartReading 开始阅读回调
 * @param onAddToBookshelf 添加到书架回调
 * @param onShareBook 分享书籍回调
 * @param onToggleDescription 切换简介展开回调
 */
@Composable
fun BookDetailContent(
    bookInfo: com.novel.page.book.viewmodel.BookDetailState.BookInfo?,
    lastChapter: com.novel.page.book.viewmodel.BookDetailState.LastChapter?,
    reviews: kotlinx.collections.immutable.ImmutableList<com.novel.page.book.viewmodel.BookDetailState.BookReview>,
    isDescriptionExpanded: Boolean,
    isInBookshelf: Boolean,
    isAuthorFollowed: Boolean,
    onFollowAuthor: ((String) -> Unit)? = null,
    onStartReading: ((String, String?) -> Unit)? = null,
    onAddToBookshelf: ((String) -> Unit)? = null,
    onRemoveFromBookshelf: ((String) -> Unit)? = null,
    onShareBook: ((String, String) -> Unit)? = null,
    onToggleDescription: (() -> Unit)? = null,
    onMoreReviewsClick: (() -> Unit)? = null
) {
    // 转换为旧的UI状态格式以保持兼容性
    val uiState = remember(bookInfo, lastChapter, reviews, isDescriptionExpanded) {
        BookDetailUiState(
            bookInfo = bookInfo?.let { info ->
                BookDetailUiState.BookInfo(
                    id = info.id,
                    bookName = info.bookName,
                    authorName = info.authorName,
                    bookDesc = info.bookDesc,
                    picUrl = info.picUrl,
                    visitCount = info.visitCount,
                    wordCount = info.wordCount,
                    categoryName = info.categoryName
                )
            },
            lastChapter = lastChapter?.let { chapter ->
                BookDetailUiState.LastChapter(
                    chapterName = chapter.chapterName,
                    chapterUpdateTime = chapter.chapterUpdateTime
                )
            },
            reviews = reviews.map { review ->
                BookDetailUiState.BookReview(
                    id = review.id,
                    content = review.content,
                    rating = review.rating,
                    readTime = review.readTime,
                    userName = review.userName
                )
            }.toImmutableList(),
            isDescriptionExpanded = isDescriptionExpanded
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.wdp),
        verticalArrangement = Arrangement.spacedBy(16.wdp)
    ) {
        // 书籍封面区域
        BookCoverSection(bookInfo = uiState.bookInfo)

        // 书籍标题区域
        BookTitleSection(bookInfo = uiState.bookInfo)

        // 作者信息区域
        AuthorSection(
            bookInfo = uiState.bookInfo,
            isAuthorFollowed = isAuthorFollowed,
            onFollowAuthor = onFollowAuthor
        )

        // 统计数据区域（字数、阅读量、最新章节等）
        BookStatsSection(
            bookInfo = uiState.bookInfo,
            lastChapter = uiState.lastChapter
        )

        // 书籍简介区域
        BookDescriptionSection(
            description = uiState.bookInfo?.bookDesc ?: "",
            isExpanded = uiState.isDescriptionExpanded,
            onToggleExpanded = onToggleDescription
        )

        // 书籍操作区域
        BookActionSection(
            bookInfo = uiState.bookInfo,
            isInBookshelf = isInBookshelf,
            onStartReading = onStartReading,
            onAddToBookshelf = onAddToBookshelf,
            onRemoveFromBookshelf = onRemoveFromBookshelf,
            onShareBook = onShareBook
        )

        // 用户评价区域
        BookReviewsSection(
            reviews = uiState.reviews,
            onMoreReviewsClick = onMoreReviewsClick ?: {}
        )
    }
}
