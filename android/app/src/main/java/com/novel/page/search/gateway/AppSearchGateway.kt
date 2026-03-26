package com.novel.page.search.gateway

import com.novel.page.search.repository.PageRespDtoBookInfoRespDto
import com.novel.page.search.repository.RankingData
import com.novel.page.search.repository.SearchRepository
import com.novel.page.search.repository.SearchParams
import com.novel.page.search.usecase.AddSearchHistoryUseCase
import com.novel.page.search.usecase.GetCategoryFiltersUseCase
import com.novel.page.search.usecase.GetRankingListUseCase
import com.novel.page.search.usecase.GetSearchHistoryUseCase
import com.novel.page.search.usecase.ToggleHistoryExpansionUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSearchHistoryGateway @Inject constructor(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val toggleHistoryExpansionUseCase: ToggleHistoryExpansionUseCase,
) : SearchHistoryGateway {

    override suspend fun getSearchHistory(): List<String> = getSearchHistoryUseCase()

    override suspend fun addSearchHistory(keyword: String) {
        addSearchHistoryUseCase(keyword)
    }

    override suspend fun toggleHistoryExpansion(currentState: Boolean): Boolean =
        toggleHistoryExpansionUseCase(currentState)
}

@Singleton
class AppSearchRankingGateway @Inject constructor(
    private val getRankingListUseCase: GetRankingListUseCase,
) : SearchRankingGateway {

    override suspend fun getRankingData(): SearchRankingData {
        val rankingData: RankingData = getRankingListUseCase()
        return SearchRankingData(
            novelRanking = rankingData.novelRanking,
            dramaRanking = rankingData.dramaRanking,
            newBookRanking = rankingData.newBookRanking,
        )
    }
}

@Singleton
class AppSearchQueryGateway @Inject constructor(
    private val searchRepository: SearchRepository,
) : SearchQueryGateway {

    override suspend fun searchBooks(params: SearchParams): SearchQueryResult? {
        val result: PageRespDtoBookInfoRespDto? = searchRepository.searchBooksWithCache(params)
        return result?.let {
            SearchQueryResult(
                list = it.list,
                total = it.total,
                pages = it.pages,
            )
        }
    }
}

@Singleton
class AppCategoryFilterGateway @Inject constructor(
    private val getCategoryFiltersUseCase: GetCategoryFiltersUseCase,
) : CategoryFilterGateway {

    override suspend fun getCategoryFilters(): Result<List<com.novel.page.search.viewmodel.CategoryFilter>> {
        return getCategoryFiltersUseCase.execute()
    }
}
