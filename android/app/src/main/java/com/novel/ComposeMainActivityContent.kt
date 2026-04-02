package com.novel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.novel.page.component.ImageLoaderService
import com.novel.page.component.LocalImageLoaderService
import com.novel.ui.theme.NovelDesignTokens
import com.novel.ui.theme.NovelTheme
import com.novel.utils.AdaptiveScreen
import com.novel.utils.NavigationSetup

@Composable
internal fun ComposeMainActivityContent(
    imageLoaderService: ImageLoaderService,
    debugRoute: String?,
) {
    NovelTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = NovelDesignTokens.color("color.bg.canvas"),
        ) {
            CompositionLocalProvider(
                LocalImageLoaderService provides imageLoaderService,
            ) {
                AdaptiveScreen {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavigationSetup(debugRoute = debugRoute)
                    }
                }
            }
        }
    }
}
