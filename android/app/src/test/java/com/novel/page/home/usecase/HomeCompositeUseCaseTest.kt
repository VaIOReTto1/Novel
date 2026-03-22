package com.novel.page.home.usecase

import com.google.common.truth.Truth.assertThat
import com.novel.page.home.dao.IHomeRepository
import com.novel.page.home.viewmodel.CategoryInfo
import com.novel.page.login.dao.UserRepository
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import com.novel.utils.network.TokenProvider
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.network.cache.CacheStrategy
import com.novel.utils.network.repository.CachedBookRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class HomeCompositeUseCaseTest {

    @Test
    fun loadInitialData_returnsRealCategoryFilters_whenCategoryFilterFetchFails() {
        runBlocking {
            val useCase = HomeCompositeUseCase(
                homeRepository = FakeHomeRepository(
                    categoryFiltersFlow = flow { error("category filter fetch failed") },
                    categoriesFlow = flow {
                        emit(
                            persistentListOf(
                                BookService.BookCategory(id = 7, name = "玄幻"),
                            ),
                        )
                    },
                ),
                cachedBookRepository = allocateWithoutConstructor(),
                searchService = allocateWithoutConstructor(),
                userRepository = allocateWithoutConstructor(),
                tokenProvider = FakeTokenProvider,
                userDefaults = FakeNovelUserDefaults(),
            )

            val result = useCase(
                HomeCompositeUseCase.Params(loadInitialData = true),
            )

            assertThat(result.isSuccess).isTrue()
            assertThat(result.categoryFilters).containsExactly(
                CategoryInfo(id = "0", name = "首页"),
                CategoryInfo(id = "7", name = "玄幻"),
            ).inOrder()
            assertThat(result.categories.map { it.name }).containsExactly("玄幻")
        }
    }

    @Test
    fun loadInitialData_returnsEmptyCategoryFilters_whenNoRealCategoryDataExists() {
        runBlocking {
            val useCase = HomeCompositeUseCase(
                homeRepository = FakeHomeRepository(
                    categoryFiltersFlow = flow { error("category filter fetch failed") },
                    categoriesFlow = flow { emit(persistentListOf()) },
                ),
                cachedBookRepository = allocateWithoutConstructor(),
                searchService = allocateWithoutConstructor(),
                userRepository = allocateWithoutConstructor(),
                tokenProvider = FakeTokenProvider,
                userDefaults = FakeNovelUserDefaults(),
            )

            val result = useCase(
                HomeCompositeUseCase.Params(loadInitialData = true),
            )

            assertThat(result.isSuccess).isTrue()
            assertThat(result.categoryFilters).containsExactly(
                CategoryInfo(id = "0", name = "首页"),
            )
            assertThat(result.categories).isEmpty()
        }
    }

    @Test
    fun loadCategoryData_usesHomeRecommendBranch_forHomeAndLegacyRecommendFilter() {
        runBlocking {
            val homeBooks = persistentListOf(
                HomeService.HomeBook(
                    type = 3,
                    bookId = 101,
                    picUrl = "cover",
                    bookName = "Home Recommend",
                    authorName = "Author",
                    bookDesc = "desc",
                ),
            )
            val useCase = HomeCompositeUseCase(
                homeRepository = FakeHomeRepository(
                    categoryFiltersFlow = flow { emit(persistentListOf()) },
                    categoriesFlow = flow { emit(persistentListOf()) },
                    homeBooks = homeBooks,
                ),
                cachedBookRepository = allocateWithoutConstructor(),
                searchService = allocateWithoutConstructor(),
                userRepository = allocateWithoutConstructor(),
                tokenProvider = FakeTokenProvider,
                userDefaults = FakeNovelUserDefaults(),
            )

            val homeResult = useCase(
                HomeCompositeUseCase.Params(
                    categoryFilter = "首页",
                    categoryFilters = persistentListOf(CategoryInfo("0", "首页")),
                ),
            )
            val legacyResult = useCase(
                HomeCompositeUseCase.Params(
                    categoryFilter = "推荐",
                    categoryFilters = persistentListOf(CategoryInfo("0", "首页")),
                ),
            )

            assertThat(homeResult.homeRecommendBooks).isEqualTo(homeBooks)
            assertThat(homeResult.recommendBooks).isEmpty()
            assertThat(legacyResult.homeRecommendBooks).isEqualTo(homeBooks)
            assertThat(legacyResult.recommendBooks).isEmpty()
        }
    }

    private class FakeHomeRepository(
        private val categoryFiltersFlow: Flow<ImmutableList<BookService.BookCategory>>,
        private val categoriesFlow: Flow<ImmutableList<BookService.BookCategory>>,
        private val homeBooks: ImmutableList<HomeService.HomeBook> = persistentListOf(),
    ) : IHomeRepository {

        override suspend fun getRankBooks(
            rankType: String,
            strategy: CacheStrategy,
        ): List<BookService.BookRank> = emptyList()

        override suspend fun getHomeBooks(
            strategy: CacheStrategy,
        ): List<HomeService.HomeBook> = homeBooks

        override fun getBookCategories(
            workDirection: Int,
            strategy: CacheStrategy,
        ): Flow<ImmutableList<BookService.BookCategory>> = categoryFiltersFlow

        override fun getCategories(
            forceRefresh: Boolean,
        ): Flow<ImmutableList<BookService.BookCategory>> = categoriesFlow

        override fun getCarouselBooks(
            forceRefresh: Boolean,
        ): Flow<ImmutableList<HomeService.HomeBook>> = flow { emit(persistentListOf()) }

        override fun getHotBooks(
            forceRefresh: Boolean,
        ): Flow<ImmutableList<HomeService.HomeBook>> = flow { emit(persistentListOf()) }

        override fun getNewBooks(
            forceRefresh: Boolean,
        ): Flow<ImmutableList<HomeService.HomeBook>> = flow { emit(persistentListOf()) }

        override fun getVipBooks(
            forceRefresh: Boolean,
        ): Flow<ImmutableList<HomeService.HomeBook>> = flow { emit(persistentListOf()) }

        override suspend fun refreshAllData(): Triple<
            List<HomeService.HomeBook>,
            List<HomeService.FriendLink>,
            List<BookService.BookCategory>,
        > = Triple(emptyList(), emptyList(), emptyList())
    }

    private object FakeTokenProvider : TokenProvider {
        override fun getToken(): String? = null

        override fun accessToken(): String? = null

        override suspend fun saveToken(accessToken: String, refreshToken: String) = Unit

        override suspend fun clear() = Unit
    }

    private class FakeNovelUserDefaults : NovelUserDefaults {
        private val values = mutableMapOf<String, Any?>()

        override fun <T> set(value: T, forKey: NovelUserDefaultsKey) {
            values[forKey.key] = value
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(key: NovelUserDefaultsKey): T? = values[key.key] as? T

        override fun remove(key: NovelUserDefaultsKey) {
            values.remove(key.key)
        }

        override fun contains(key: NovelUserDefaultsKey): Boolean = values.containsKey(key.key)

        override fun clearAll() {
            values.clear()
        }

        override fun setString(key: String, value: String) {
            values[key] = value
        }

        override fun getString(key: String): String? = values[key] as? String

        override fun remove(key: String) {
            values.remove(key)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> allocateWithoutConstructor(): T {
        val unsafeClass = Class.forName("sun.misc.Unsafe")
        val field = unsafeClass.getDeclaredField("theUnsafe")
        field.isAccessible = true
        val unsafe = field.get(null)
        val allocateInstance = unsafeClass.getMethod("allocateInstance", Class::class.java)
        return allocateInstance.invoke(unsafe, T::class.java) as T
    }
}
