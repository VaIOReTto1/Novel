package com.novel.page.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.novel.page.search.component.SearchRankingItem
import com.novel.ui.theme.NovelTheme
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchSmokeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchPageContent_rendersCoreLayout() {
        composeTestRule.setContent {
            NovelTheme {
                SearchPageContent(
                    searchQuery = "",
                    searchHistory = persistentListOf("History Smoke"),
                    isHistoryExpanded = false,
                    novelRanking = persistentListOf(
                        SearchRankingItem(
                            id = 1L,
                            title = "Smoke Ranking",
                            author = "Contract Author",
                            rank = 1,
                        )
                    ),
                    dramaRanking = persistentListOf(),
                    newBookRanking = persistentListOf(),
                    onIntent = { }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().assertExists()
    }
}
