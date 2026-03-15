package com.novel.page.read.viewmodel

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.novel.page.read.components.NoAnimationContainer
import com.novel.ui.theme.NovelTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReaderSmokeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun readerPage_entersInitialStateWithoutCrash() {
        composeTestRule.setContent {
            NovelTheme {
                NoAnimationContainer(
                    uiState = ReaderState(),
                    readerSettings = ReaderSettings.getDefault(),
                    onPageChange = { },
                    onClick = { }
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().assertExists()
    }
}
