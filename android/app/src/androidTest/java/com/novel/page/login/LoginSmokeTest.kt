package com.novel.page.login

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.novel.ui.theme.NovelTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginSmokeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginPage_rendersCoreLayout() {
        composeTestRule.setContent {
            NovelTheme {
                LoginPage()
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().assertExists()
    }
}
