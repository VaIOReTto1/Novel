package com.novel.page.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.home.dao.IHomeRepository
import com.novel.page.home.usecase.*
import com.novel.utils.TimberLogger
import com.novel.core.asStable
import com.novel.utils.network.cache.CacheStrategy
import com.novel.utils.network.repository.CachedBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.novel.page.home.usecase.HomeRecommendPagingSource
import com.novel.page.home.usecase.CategoryRecommendPagingSourceFactory
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.SharingStarted

/**
 * 稳定的分页配置包装器
 */
@Stable
private data class StablePagingConfig(
    val pageSize: Int,
    val enablePlaceholders: Boolean,
    val initialLoadSize: Int,
    val prefetchDistance: Int
) {
    fun toPagingConfig(): PagingConfig = PagingConfig(
        pageSize = pageSize,
        enablePlaceholders = enablePlaceholders,
        initialLoadSize = initialLoadSize,
        prefetchDistance = prefetchDistance
    )
}

/**
 * 首页ViewModel - MVI重构版本
 *
 * 采用统一的MVI架构模式：
 * - 继承BaseMviViewModel，获得完整的MVI支持
 * - 使用Intent处理所有用户交互和系统事件
 * - 通过Reducer进行纯函数状态转换
 * - 使用Effect处理一次性副作用
 * - 业务逻辑完全委托给UseCase层（特别是HomeCompositeUseCase）
 *
 * 功能特性：
 * - 首页数据的加载和缓存
 * - 分类筛选和推荐书籍展示
 * - 榜单数据的异步获取
 * - 搜索功能集成
 * - React Native数据同步
 * - 下拉刷新和加载更多
 * - 错误处理和重试机制
 */
@Stable
@HiltViewModel
class HomeViewModel @Inject constructor(
    /** 主要数据仓库 - 使用稳定接口避免Compose重组问题 */
    @Stable
    private val homeRepository: IHomeRepository,
    /** 缓存书籍数据仓库 */
    @Stable
    private val cachedBookRepository: CachedBookRepository,
    /** 预初始化UseCase实例，避免Hilt泛型问题和lazy delegate的unstable问题 */
    @Stable
    private val homeCompositeUseCase: HomeCompositeUseCase,
    @Stable
    private val getHomeCategoriesUseCase: GetHomeCategoriesUseCase,
    @Stable
    private val getHomeRecommendBooksUseCase: GetHomeRecommendBooksUseCase,
    @Stable
    private val getRankingBooksUseCase: GetRankingBooksUseCase,
    @Stable
    private val refreshHomeDataUseCase: RefreshHomeDataUseCase,
    @Stable
    private val sendReactNativeDataUseCase: SendReactNativeDataUseCase,
    @Stable
    private val getCategoryRecommendBooksUseCase: GetCategoryRecommendBooksUseCase,
    @Stable
    private val homeRecommendPagingSource: HomeRecommendPagingSource,
    @Stable
    private val categoryRecommendPagingSourceFactory: CategoryRecommendPagingSourceFactory,
    @Stable
    private val homeStatusCheckUseCase: HomeStatusCheckUseCase
) : BaseMviViewModel<HomeIntent, HomeState, HomeEffect>() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val RECOMMEND_PAGE_SIZE = 8
    }

    // Paging配置 - 使用@Stable标记
    @Stable
    private val pagingConfig: StablePagingConfig = StablePagingConfig(
        pageSize = RECOMMEND_PAGE_SIZE,
        enablePlaceholders = false,
        initialLoadSize = RECOMMEND_PAGE_SIZE,
        prefetchDistance = 2
    )

    // 缓存全量首页推荐数据 - 使用@Stable标记
    @Stable
    @Volatile
    private var cachedHomeBooks: ImmutableList<HomeService.HomeBook> = persistentListOf()

    private val homeInitialLoadCoordinator = HomeInitialLoadCoordinator {
        homeCompositeUseCase(
            HomeCompositeUseCase.Params(loadInitialData = true),
        )
    }

    private val homeRefreshCoordinator = HomeRefreshCoordinator {
        homeCompositeUseCase(
            HomeCompositeUseCase.Params(refreshData = true),
        )
    }

    private val homePagingCoordinator = HomePagingCoordinator()

    /** 新的StateAdapter实例 */
    @Stable
    val adapter = HomeStateAdapter(state.asStable(), viewModelScope)

    /** UI组合状态 - 提供稳定的 State<HomeScreenState> */
    @Stable
    val screenState: StateFlow<HomeScreenState> = state.map { mviState ->
        adapter.toScreenState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeScreenState(
            isLoading = false,
            error = null,
            isRefreshing = false,
            searchQuery = "",
            categories = persistentListOf(),
            selectedCategoryFilter = "推荐",
            categoryFilters = persistentListOf(),
            carouselBooks = persistentListOf(),
            hotBooks = persistentListOf(),
            newBooks = persistentListOf(),
            vipBooks = persistentListOf(),
            rankBooks = persistentListOf(),
            selectedRankType = "点击榜",
            currentRecommendBooks = persistentListOf(),
            canPerformSearch = false,
            searchHint = "搜索您喜欢的小说",
            canLoadMoreRecommend = false,
            loadMoreText = "点击加载更多",
            homeStatusSummary = "加载中",
            shouldShowEmptyState = false,
            shouldShowLoadMoreButton = false,
            recommendModeText = "首页推荐",
            isRecommendMode = true
        )
    ).asStable()

    // Paging3 数据流 - 使用@Stable标记
    /** 首页推荐书籍的分页数据流 */
    @Stable
    val homeRecommendPagingData: Flow<PagingData<HomeService.HomeBook>> = Pager(
        config = pagingConfig.toPagingConfig(),
        pagingSourceFactory = { homeRecommendPagingSource }
    ).flow.cachedIn(viewModelScope)
    
    /** 分类推荐书籍的分页数据流 - 基于当前选中的分类 */
    @Stable
    @Volatile
    private var _categoryRecommendPagingData: Flow<PagingData<SearchService.BookInfo>>? = null
    
    @Stable
    val categoryRecommendPagingData: Flow<PagingData<SearchService.BookInfo>>
        get() = _categoryRecommendPagingData ?: createCategoryPagingData(getCurrentCategoryId(getCurrentState().selectedCategoryFilter))

    /** 兼容性属性：UI状态流，适配原有的UI层期望格式 */
    @Stable
    val uiState: StateFlow<HomeUiState> = state.map { mviState ->
        adapter.toHomeUiState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = HomeUiState()
    ).asStable()

    /** 兼容性属性：当前状态 */
    val currentState: HomeUiState get() = uiState.value

    init {
        // 初始化时加载数据
        sendIntent(HomeIntent.LoadInitialData)

        // 从IHomeRepository收集数据，触发Compose收集 Immutable 数据
        collectHomeData()

        // 发送测试数据到RN（延迟执行，确保RN能接收到数据）
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            sendReactNativeDataUseCase(Unit)
        }
    }

    /**
     * 创建初始状态
     */
    override fun createInitialState(): HomeState {
        return HomeState()
    }

    /**
     * 获取Reducer实例
     */
    override fun getReducer(): MviReducer<HomeIntent, HomeState> {
        // 返回一个适配器，将MviReducerWithEffect适配为MviReducer
        val effectReducer = HomeReducer()
        return object : MviReducer<HomeIntent, HomeState> {
            override fun reduce(currentState: HomeState, intent: HomeIntent): HomeState {
                val result = effectReducer.reduce(currentState, intent)
                // 在这里处理副作用
                result.effect?.let { effect ->
                    sendEffect(effect)
                }
                return result.newState
            }
        }
    }

    /**
     * Intent处理完成后的回调
     * 在这里处理需要调用UseCase的Intent
     */
    override fun onIntentProcessed(intent: HomeIntent, newState: HomeState) {
        when (intent) {
            is HomeIntent.LoadInitialData -> {
                loadInitialData()
            }

            is HomeIntent.RefreshData -> {
                refreshData()
            }

            is HomeIntent.SelectCategoryFilter -> {
                selectCategoryFilter(intent.categoryName)
            }

            is HomeIntent.SelectRankType -> {
                selectRankType(intent.rankType)
            }

            is HomeIntent.LoadMoreRecommend -> {
                val currentState = getCurrentState()
                if (currentState.isRecommendMode) {
                    loadMoreHomeRecommend()
                } else {
                    loadMoreRecommend()
                }
            }

            is HomeIntent.LoadMoreHomeRecommend -> {
                loadMoreHomeRecommend()
            }

            is HomeIntent.RestoreData -> {
                restoreDataIfNeeded()
            }

            else -> {
                // 其他Intent由Reducer处理，无需额外操作
            }
        }
    }

    // ========== 私有方法 - 业务逻辑处理 ==========

    /**
     * 加载初始数据
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            TimberLogger.d(TAG, "开始加载初始数据")

            val outcome = homeInitialLoadCoordinator.coordinate()
            outcome.cachedHomeBooks?.let {
                cachedHomeBooks = it
            }
            outcome.intents.forEach(::sendIntent)

            if (outcome.cachedHomeBooks != null) {
                TimberLogger.d(TAG, "初始数据加载完成")
            }
        }
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        viewModelScope.launch {
            TimberLogger.d(TAG, "开始刷新数据")

            val outcome = homeRefreshCoordinator.coordinate()
            outcome.cachedHomeBooks?.let {
                cachedHomeBooks = it
            }
            outcome.intents.forEach(::sendIntent)
            outcome.effects.forEach(::sendEffect)

            if (outcome.cachedHomeBooks != null) {
                TimberLogger.d(TAG, "数据刷新完成")
            }
        }
    }

    /**
     * 选择分类筛选器
     */
    private fun selectCategoryFilter(categoryName: String) {
        val currentState = getCurrentState()
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "选择分类筛选器: $categoryName")

                val result = homeCompositeUseCase(
                    HomeCompositeUseCase.Params(
                        categoryFilter = categoryName,
                        categoryFilters = currentState.categoryFilters,
                        currentPage = 1
                    )
                )

                if (result.isSuccess) {
                    if (categoryName == "推荐") {
                        sendIntent(
                            HomeIntent.HomeRecommendBooksLoadSuccess(
                                books = result.homeRecommendBooks.toImmutableList(),
                                isRefresh = true,
                                hasMore = result.hasMoreRecommend
                            )
                        )
                    } else {
                        sendIntent(
                            HomeIntent.CategoryRecommendBooksLoadSuccess(
                                books = result.recommendBooks.toImmutableList(),
                                isLoadMore = false,
                                hasMore = result.hasMoreRecommend,
                                totalPages = result.totalPages
                            )
                        )
                    }
                } else {
                    sendIntent(
                        HomeIntent.CategoryRecommendBooksLoadFailure(
                            result.errorMessage ?: "加载分类数据失败"
                        )
                    )
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "选择分类筛选器异常", e)
                sendIntent(HomeIntent.CategoryRecommendBooksLoadFailure(e.message ?: "未知错误"))
            }
        }
    }

    /**
     * 选择榜单类型
     */
    private fun selectRankType(rankType: String) {
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "选择榜单类型: $rankType")

                val result = homeCompositeUseCase(
                    HomeCompositeUseCase.Params(rankType = rankType)
                )

                if (result.isSuccess) {
                    sendIntent(HomeIntent.RankBooksLoadSuccess(result.rankBooks.toImmutableList()))
                } else {
                    sendIntent(
                        HomeIntent.RankBooksLoadFailure(
                            result.errorMessage ?: "加载榜单数据失败"
                        )
                    )
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "选择榜单类型异常", e)
                sendIntent(HomeIntent.RankBooksLoadFailure(e.message ?: "未知错误"))
            }
        }
    }

    /**
     * 加载更多推荐内容
     */
    private fun loadMoreRecommend() {
        val currentUiState = currentState

        // 检查是否已经在加载或没有更多数据
//        if (currentState.recommendLoading) {
//            TimberLogger.d(TAG, "加载更多推荐内容已在进行中，跳过重复请求")
//            return
//        }

        if (!currentUiState.hasMoreRecommend) {
            TimberLogger.d(TAG, "没有更多推荐内容可加载")
            return
        }

        viewModelScope.launch {
            TimberLogger.d(TAG, "开始加载更多推荐内容 - 当前页: ${currentUiState.recommendPage}")

            val outcome = homePagingCoordinator.coordinateCategoryPaging(
                currentState = currentUiState,
                pageSize = RECOMMEND_PAGE_SIZE,
                resolveCategoryId = ::getCurrentCategoryId,
                loadCategoryBooks = { categoryId, pageNum, pageSize ->
                    TimberLogger.d(
                        TAG,
                        "请求参数 - categoryId: $categoryId, pageNum: $pageNum, pageSize: $pageSize"
                    )
                    getCategoryRecommendBooksUseCase(
                        GetCategoryRecommendBooksUseCase.Params(
                            categoryId = categoryId,
                            pageNum = pageNum,
                            pageSize = pageSize,
                            strategy = CacheStrategy.CACHE_FIRST,
                        )
                    )
                },
            )

            outcome.intents.forEach(::sendIntent)
        }
    }

    /**
     * 加载更多首页推荐内容
     */
    private fun loadMoreHomeRecommend() {
        val currentUiState = currentState

        // 检查是否已经在加载或没有更多数据
//        if (currentState.homeRecommendLoading) {
//            TimberLogger.d(TAG, "加载更多首页推荐内容已在进行中，跳过重复请求")
//            return
//        }

        if (!currentUiState.hasMoreHomeRecommend) {
            TimberLogger.d(TAG, "没有更多首页推荐内容可加载")
            return
        }

        viewModelScope.launch {
            TimberLogger.d(
                TAG,
                "加载更多首页推荐内容 - 当前页: ${currentUiState.homeRecommendPage}"
            )

            val outcome = homePagingCoordinator.coordinateHomePaging(
                currentState = currentUiState,
                pageSize = RECOMMEND_PAGE_SIZE,
                cachedHomeBooks = cachedHomeBooks,
                loadHomeBooks = {
                    getHomeRecommendBooksUseCase(GetHomeRecommendBooksUseCase.Params())
                        .toImmutableList()
                },
            )

            outcome.cachedHomeBooks?.let {
                cachedHomeBooks = it
            }
            outcome.intents.forEach(::sendIntent)
        }
    }

    /**
     * 恢复数据（页面重新获得焦点时）
     */
    private fun restoreDataIfNeeded() {
        viewModelScope.launch {
            try {
                TimberLogger.d(TAG, "检查并恢复数据")

                val statusResult = homeStatusCheckUseCase(
                    HomeStatusCheckUseCase.Params()
                )

                val currentState = getCurrentState()

                // 检查并恢复榜单数据
                if (currentState.rankBooks.isEmpty()) {
                    selectRankType(currentState.selectedRankType)
                }

                // 检查并恢复分类数据
                if (currentState.categoryFilters.size <= 1) {
                    loadCategoryFilters()
                }

                // 检查并恢复推荐数据
                if (currentState.isRecommendMode && currentState.homeRecommendBooks.isEmpty()) {
                    loadHomeRecommendBooks()
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "恢复数据异常", e)
            }
        }
    }

    /**
     * 加载分类筛选器数据
     */
    private fun loadCategoryFilters() {
        viewModelScope.launch {
                            try {
                    getHomeCategoriesUseCase(GetHomeCategoriesUseCase.Params())
                        .collect { filters ->
                            sendIntent(HomeIntent.CategoryFiltersLoadSuccess(filters.toImmutableList()))
                        }
                } catch (e: Exception) {
                TimberLogger.e(TAG, "加载分类筛选器异常", e)
                sendIntent(HomeIntent.CategoryFiltersLoadFailure(e.message ?: "加载分类失败"))
            }
        }
    }

    /**
     * 加载首页推荐书籍
     */
    private fun loadHomeRecommendBooks(isRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (isRefresh || cachedHomeBooks.isEmpty()) {
                    val strategy =
                        if (isRefresh) CacheStrategy.NETWORK_ONLY else CacheStrategy.CACHE_FIRST
                    val homeBooks =
                        getHomeRecommendBooksUseCase(GetHomeRecommendBooksUseCase.Params(strategy))
                    cachedHomeBooks = homeBooks.toImmutableList()
                }

                // 基于缓存数据进行分页
                val currentPage = if (isRefresh) 1 else getCurrentState().homeRecommendPage
                val startIndex = (currentPage - 1) * RECOMMEND_PAGE_SIZE
                val endIndex = startIndex + RECOMMEND_PAGE_SIZE

                val currentBooks = if (isRefresh) {
                    cachedHomeBooks.take(RECOMMEND_PAGE_SIZE)
                } else {
                    cachedHomeBooks.take(endIndex)
                }

                val hasMore = endIndex < cachedHomeBooks.size

                sendIntent(
                    HomeIntent.HomeRecommendBooksLoadSuccess(
                        books = currentBooks.toImmutableList(),
                        isRefresh = isRefresh,
                        hasMore = hasMore
                    )
                )
            } catch (e: Exception) {
                TimberLogger.e(TAG, "加载首页推荐书籍异常", e)
                sendIntent(
                    HomeIntent.HomeRecommendBooksLoadFailure(
                        e.message ?: "加载推荐书籍失败"
                    )
                )
            }
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 获取当前分类ID
     */
    private fun getCurrentCategoryId(categoryName: String): Int {
        val currentState = getCurrentState()

        // 打印调试信息
        TimberLogger.d(TAG, "获取分类ID - 分类名称: $categoryName")
        TimberLogger.d(
            TAG,
            "可用分类筛选器: ${currentState.categoryFilters.map { "${it.name}(${it.id})" }}"
        )

        // 特殊处理推荐模式
        if (categoryName == "推荐") {
            TimberLogger.d(TAG, "推荐模式，返回categoryId: 0")
            return 0
        }

        // 查找对应的分类ID
        val categoryInfo = currentState.categoryFilters.find { it.name == categoryName }
        val categoryId = categoryInfo?.id?.toIntOrNull() ?: run {
            // 如果找不到，尝试用分类名称创建一个映射
            val mappedId = when (categoryName) {
                "玄幻奇幻" -> 1
                "武侠仙侠" -> 2
                "都市言情" -> 3
                "历史军情" -> 4
                "科幻灵异" -> 5
                "网游竞技" -> 6
                else -> 1 // 默认返回1
            }
            TimberLogger.w(TAG, "未找到分类 '$categoryName' 的ID，使用映射值: $mappedId")
            mappedId
        }

        TimberLogger.d(TAG, "分类 '$categoryName' 对应ID: $categoryId")
        return categoryId
    }

    /**
     * 收集来自IHomeRepository的响应式数据流
     *
     * 直接从稳定接口获取ImmutableList数据，避免Room DAO的Unstable问题：
     * - 分类数据：自动更新UI分类筛选器
     * - 轮播书籍：更新首页轮播内容
     * - 热门/新书/VIP：更新推荐书籍列表
     * - 数据变化时自动触发UI重组，保持界面实时性
     */
    private fun collectHomeData() {
        // 收集分类数据
        viewModelScope.launch {
            homeRepository.getCategories()
                .catch { e ->
                    TimberLogger.e(TAG, "收集分类数据失败", e)
                    sendIntent(HomeIntent.CategoriesLoadFailure(e.message ?: "获取分类数据失败"))
                }
                .collect { categories ->
                    TimberLogger.d(TAG, "收集到分类数据: ${categories.size} 个")
                    sendIntent(
                        HomeIntent.CategoryFiltersLoadSuccess(
                            filters = categories.map { category ->
                                CategoryInfo(
                                    id = category.id.toString(),
                                    name = category.name,
                                )
                            }.toImmutableList()
                        )
                    )
                }
        }

        // 收集轮播图书籍
        viewModelScope.launch {
            homeRepository.getCarouselBooks()
                .catch { e ->
                    TimberLogger.e(TAG, "收集轮播书籍失败", e)
                }
                .collect { books ->
                    TimberLogger.d(TAG, "收集到轮播书籍: ${books.size} 本")
                    // 将HomeService.HomeBook转换为HomeBookEntity
                    val bookEntities = books.map { book ->
                        com.novel.page.home.dao.HomeBookEntity(
                            id = book.bookId,
                            title = book.bookName,
                            author = book.authorName,
                            coverUrl = book.picUrl,
                            description = book.bookDesc,
                            category = "",
                            isCompleted = false,
                            isVip = false,
                            updateTime = System.currentTimeMillis(),
                            type = "carousel"
                        )
                    }
                    // 触发书籍数据更新
                    val currentState = getCurrentState()
                    sendIntent(
                        HomeIntent.BooksLoadSuccess(
                            carouselBooks = bookEntities.toImmutableList(),
                            hotBooks = currentState.hotBooks,
                            newBooks = currentState.newBooks,
                            vipBooks = currentState.vipBooks
                        )
                    )
                }
        }

        // 收集热门书籍
        viewModelScope.launch {
            homeRepository.getHotBooks()
                .catch { e ->
                    TimberLogger.e(TAG, "收集热门书籍失败", e)
                }
                .collect { books ->
                    TimberLogger.d(TAG, "收集到热门书籍: ${books.size} 本")
                    val bookEntities = books.map { book ->
                        com.novel.page.home.dao.HomeBookEntity(
                            id = book.bookId,
                            title = book.bookName,
                            author = book.authorName,
                            coverUrl = book.picUrl,
                            description = book.bookDesc,
                            category = "",
                            isCompleted = false,
                            isVip = false,
                            updateTime = System.currentTimeMillis(),
                            type = "hot"
                        )
                    }
                    val currentState = getCurrentState()
                    sendIntent(
                        HomeIntent.BooksLoadSuccess(
                            carouselBooks = currentState.carouselBooks,
                            hotBooks = bookEntities.toImmutableList(),
                            newBooks = currentState.newBooks,
                            vipBooks = currentState.vipBooks
                        )
                    )
                }
        }

        // 收集最新书籍
        viewModelScope.launch {
            homeRepository.getNewBooks()
                .catch { e ->
                    TimberLogger.e(TAG, "收集最新书籍失败", e)
                }
                .collect { books ->
                    TimberLogger.d(TAG, "收集到最新书籍: ${books.size} 本")
                    val bookEntities = books.map { book ->
                        com.novel.page.home.dao.HomeBookEntity(
                            id = book.bookId,
                            title = book.bookName,
                            author = book.authorName,
                            coverUrl = book.picUrl,
                            description = book.bookDesc,
                            category = "",
                            isCompleted = false,
                            isVip = false,
                            updateTime = System.currentTimeMillis(),
                            type = "new"
                        )
                    }
                    val currentState = getCurrentState()
                    sendIntent(
                        HomeIntent.BooksLoadSuccess(
                            carouselBooks = currentState.carouselBooks,
                            hotBooks = currentState.hotBooks,
                            newBooks = bookEntities.toImmutableList(),
                            vipBooks = currentState.vipBooks
                        )
                    )
                }
        }

        // 收集VIP书籍
        viewModelScope.launch {
            homeRepository.getVipBooks()
                .catch { e ->
                    TimberLogger.e(TAG, "收集VIP书籍失败", e)
                }
                .collect { books ->
                    TimberLogger.d(TAG, "收集到VIP书籍: ${books.size} 本")
                    val bookEntities = books.map { book ->
                        com.novel.page.home.dao.HomeBookEntity(
                            id = book.bookId,
                            title = book.bookName,
                            author = book.authorName,
                            coverUrl = book.picUrl,
                            description = book.bookDesc,
                            category = "",
                            isCompleted = false,
                            isVip = true,
                            updateTime = System.currentTimeMillis(),
                            type = "vip"
                        )
                    }
                    val currentState = getCurrentState()
                    sendIntent(
                        HomeIntent.BooksLoadSuccess(
                            carouselBooks = currentState.carouselBooks,
                            hotBooks = currentState.hotBooks,
                            newBooks = currentState.newBooks,
                            vipBooks = bookEntities.toImmutableList()
                        )
                    )
                }
        }
    }
    
    // === Paging3 辅助方法 ===
    
    /**
     * 创建分类推荐的分页数据流
     */
    private fun createCategoryPagingData(categoryId: Int): Flow<PagingData<SearchService.BookInfo>> {
        return Pager(
            config = pagingConfig.toPagingConfig(),
            pagingSourceFactory = { categoryRecommendPagingSourceFactory.create(categoryId) }
        ).flow.cachedIn(viewModelScope)
    }
    
    /**
     * 更新分类推荐的分页数据流
     */
    fun updateCategoryPagingData(categoryName: String) {
        val categoryId = getCurrentCategoryId(categoryName)
        _categoryRecommendPagingData = createCategoryPagingData(categoryId)
    }
    
    /**
     * 刷新首页推荐数据源
     */
    fun refreshHomeRecommendPaging() {
        homeRecommendPagingSource.clearCache()
    }
}
