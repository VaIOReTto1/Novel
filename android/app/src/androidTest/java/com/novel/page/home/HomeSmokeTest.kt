package com.novel.page.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.novel.page.home.skeleton.HomePageSkeleton
import com.novel.ui.theme.NovelTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeSmokeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homePage_rendersCoreLayout() {
        composeTestRule.setContent {
            NovelTheme {
                HomePageSkeleton()
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().assertExists()
    }
}
