package com.novel.page.home.viewmodel

import androidx.compose.runtime.Stable
import com.novel.core.mvi.MviEffect
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.MviState
import com.novel.core.mvi.ReduceResult
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
sealed class HomeIntent : MviIntent {
    data object LoadInitialData : HomeIntent()
    data object RefreshData : HomeIntent()
    data class UpdateSearchQuery(val query: String) : HomeIntent()
    data class SelectCategoryFilter(val categoryName: String) : HomeIntent()
    data class SelectRankType(val rankType: String) : HomeIntent()
    data object LoadMoreRecommend : HomeIntent()
    data object LoadMoreHomeRecommend : HomeIntent()
    data class NavigateToSearch(val query: String) : HomeIntent()
    data class NavigateToBookDetail(val bookId: Long) : HomeIntent()
    data class NavigateToCategory(val categoryId: Long) : HomeIntent()
    data class NavigateToFullRanking(val rankType: String) : HomeIntent()
    data object RestoreData : HomeIntent()
    data object ClearError : HomeIntent()
}

@Stable
sealed interface ReactNativeData

@Stable
data class HomeRankBook(
    val id: Long,
    val bookName: String,
    val picUrl: String,
    val categoryName: String,
)

@Stable
sealed interface RecommendItem {
    val id: Long
    val title: String
    val author: String
    val coverUrl: String
}

@Stable
data class CategoryRecommendItem(
    override val id: Long,
    override val title: String,
    override val author: String,
    override val coverUrl: String,
    val categoryName: String,
    val bookStatus: Int,
    val wordCount: Long,
) : RecommendItem

@Stable
data class HomeRecommendItem(
    override val id: Long,
    override val title: String,
    override val author: String,
    override val coverUrl: String,
    val description: String,
) : RecommendItem

@Stable
data class HomeState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val isRefreshing: Boolean = false,
    val categories: ImmutableList<HomeCategoryEntity> = persistentListOf(),
    val carouselBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val hotBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val newBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val vipBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val searchQuery: String = "",
    val selectedCategoryFilter: String = HomeCategoryFilterSupport.HOME_FILTER_LABEL,
    val categoryFilters: ImmutableList<CategoryInfo> =
        persistentListOf(HomeCategoryFilterSupport.homeCategoryFilter()),
    val rankBooks: ImmutableList<HomeRankBook> = persistentListOf(),
    val selectedRankType: String = "点击榜",
    val currentRecommendBooks: ImmutableList<RecommendItem> = persistentListOf(),
    val homeRecommendLoading: Boolean = false,
    val recommendLoading: Boolean = false,
    val hasMoreRecommend: Boolean = true,
    val hasMoreHomeRecommend: Boolean = true,
    val recommendPage: Int = 1,
    val homeRecommendPage: Int = 1,
    val isRecommendMode: Boolean = true,
) : MviState {
    override val isEmpty: Boolean
        get() = currentRecommendBooks.isEmpty() && rankBooks.isEmpty()
}

sealed class HomeEffect : MviEffect {
    data class NavigateToBook(val bookId: Long) : HomeEffect()
    data class NavigateToCategory(val categoryId: Long) : HomeEffect()
    data class NavigateToSearch(val query: String = "") : HomeEffect()
    data object NavigateToCategoryPage : HomeEffect()
    data class ShowToast(val message: String) : HomeEffect()
    data class NavigateToBookDetail(val bookId: Long) : HomeEffect()
    data class NavigateToFullRanking(val rankType: String) : HomeEffect()
    data class SendToReactNative(@Stable val data: ReactNativeData) : HomeEffect()
}

class HomeReducer : MviReducerWithEffect<HomeIntent, HomeState, HomeEffect> {
    override fun reduce(currentState: HomeState, intent: HomeIntent): ReduceResult<HomeState, HomeEffect> {
        return when (intent) {
            HomeIntent.LoadInitialData -> ReduceResult(
                currentState.copy(version = currentState.version + 1, isLoading = true, error = null),
            )
            HomeIntent.RefreshData -> ReduceResult(
                currentState.copy(version = currentState.version + 1, isRefreshing = true, error = null),
            )
            is HomeIntent.UpdateSearchQuery -> ReduceResult(
                currentState.copy(version = currentState.version + 1, searchQuery = intent.query),
            )
            is HomeIntent.SelectCategoryFilter -> ReduceResult(
                currentState.copy(
                    version = currentState.version + 1,
                    selectedCategoryFilter = HomeCategoryFilterSupport.normalizeSelectedFilter(intent.categoryName),
                    isRecommendMode = HomeCategoryFilterSupport.isHomeFilter(intent.categoryName),
                    recommendPage = 1,
                ),
            )
            is HomeIntent.SelectRankType -> ReduceResult(
                currentState.copy(version = currentState.version + 1, selectedRankType = intent.rankType),
            )
            HomeIntent.LoadMoreRecommend -> ReduceResult(
                currentState.copy(version = currentState.version + 1, recommendLoading = true),
            )
            HomeIntent.LoadMoreHomeRecommend -> ReduceResult(
                currentState.copy(version = currentState.version + 1, homeRecommendLoading = true),
            )
            is HomeIntent.NavigateToSearch -> ReduceResult(currentState, HomeEffect.NavigateToSearch(intent.query))
            is HomeIntent.NavigateToBookDetail -> ReduceResult(currentState, HomeEffect.NavigateToBookDetail(intent.bookId))
            is HomeIntent.NavigateToCategory -> ReduceResult(currentState, HomeEffect.NavigateToCategory(intent.categoryId))
            is HomeIntent.NavigateToFullRanking -> ReduceResult(currentState, HomeEffect.NavigateToFullRanking(intent.rankType))
            HomeIntent.RestoreData -> ReduceResult(currentState.copy(version = currentState.version + 1))
            HomeIntent.ClearError -> ReduceResult(currentState.copy(version = currentState.version + 1, error = null))
        }
    }
}
