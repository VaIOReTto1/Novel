package com.novel.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class NovelDesignTokensTest {

    @Test
    fun resolvesLightSemanticColors() {
        assertEquals(Color(0xFFFAF6F0), NovelDesignTokens.lightColor("color.bg.canvas"))
        assertEquals(Color(0xFFC96A34), NovelDesignTokens.lightColor("color.brand.primary"))
        assertEquals(Color(0xFFB3453C), NovelDesignTokens.lightColor("color.status.danger"))
    }

    @Test
    fun resolvesDarkSemanticColors() {
        assertEquals(Color(0xFF161311), NovelDesignTokens.darkColor("color.bg.canvas"))
        assertEquals(Color(0xFFE08B56), NovelDesignTokens.darkColor("color.brand.primary"))
        assertEquals(Color(0xFFE0756C), NovelDesignTokens.darkColor("color.status.danger"))
    }

    @Test
    fun resolvesSharedSpacingTokens() {
        assertEquals(8.dp, NovelDesignTokens.space("100"))
        assertEquals(12.dp, NovelDesignTokens.space("150"))
        assertEquals(40.dp, NovelDesignTokens.space("500"))
    }
}
