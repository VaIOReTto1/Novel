package com.novel.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object NovelDesignTokens {
    val LightColors = mapOf(
        "color.bg.canvas.light" to Color(0xFFFAF6F0),
        "color.bg.surface.light" to Color(0xFFFFFDFC),
        "color.bg.elevated.light" to Color(0xFFF3ECE3),
        "color.bg.surfaceMuted.light" to Color(0xFFF8F1E7),
        "color.text.primary.light" to Color(0xFF201A17),
        "color.text.secondary.light" to Color(0xFF6F6258),
        "color.text.inverse.light" to Color(0xFFFFFDFC),
        "color.border.subtle.light" to Color(0xFFE8DDD1),
        "color.border.strong.light" to Color(0xFFC7B39F),
        "color.border.focus.light" to Color(0xFFB85F2E),
        "color.brand.primary.light" to Color(0xFFC96A34),
        "color.brand.secondary.light" to Color(0xFF8B4A2C),
        "color.brand.accent.light" to Color(0xFFD4A25A),
        "color.status.success.light" to Color(0xFF4D7A52),
        "color.status.warning.light" to Color(0xFFA16A1F),
        "color.status.danger.light" to Color(0xFFB3453C),
        "color.interaction.selected.light" to Color(0xFFF7E1D2),
        "color.interaction.disabled.light" to Color(0xFFD6CCC2),
        "color.interaction.focus.light" to Color(0xFFB85F2E),
        "color.reader.emphasis.light" to Color(0xFFF0E1CF),
        "color.reader.chrome.light" to Color(0xFFEAD8C7)
    )

    val DarkColors = mapOf(
        "color.bg.canvas.dark" to Color(0xFF161311),
        "color.bg.surface.dark" to Color(0xFF211C19),
        "color.bg.elevated.dark" to Color(0xFF2B2521),
        "color.bg.surfaceMuted.dark" to Color(0xFF26201C),
        "color.text.primary.dark" to Color(0xFFF5EEE7),
        "color.text.secondary.dark" to Color(0xFFB8AA9D),
        "color.text.inverse.dark" to Color(0xFF161311),
        "color.border.subtle.dark" to Color(0xFF3A302B),
        "color.border.strong.dark" to Color(0xFF8D7865),
        "color.border.focus.dark" to Color(0xFFF0A06F),
        "color.brand.primary.dark" to Color(0xFFE08B56),
        "color.brand.secondary.dark" to Color(0xFFC47049),
        "color.brand.accent.dark" to Color(0xFFE7BA74),
        "color.status.success.dark" to Color(0xFF80B887),
        "color.status.warning.dark" to Color(0xFFD9A94B),
        "color.status.danger.dark" to Color(0xFFE0756C),
        "color.interaction.selected.dark" to Color(0xFF56382B),
        "color.interaction.disabled.dark" to Color(0xFF51453D),
        "color.interaction.focus.dark" to Color(0xFFF0A06F),
        "color.reader.emphasis.dark" to Color(0xFF3B2F2A),
        "color.reader.chrome.dark" to Color(0xFF312824)
    )

    val Space = mapOf(
        "100" to 8.dp,
        "150" to 12.dp,
        "200" to 16.dp,
        "300" to 24.dp,
        "400" to 32.dp,
        "500" to 40.dp,
        "600" to 48.dp,
        "700" to 64.dp,
        "050" to 4.dp
    )

    val Radius = mapOf(
        "sm" to 8.dp,
        "md" to 12.dp,
        "lg" to 18.dp,
        "xl" to 24.dp,
        "xxl" to 32.dp,
        "full" to 999.dp
    )

    fun lightColor(token: String): Color = LightColors.getValue("$token.light")

    fun darkColor(token: String): Color = DarkColors.getValue("$token.dark")

    fun color(token: String, darkTheme: Boolean = false): Color =
        if (darkTheme) darkColor(token) else lightColor(token)

    fun space(token: String) = Space.getValue(token)

    fun radius(token: String) = Radius.getValue(token)
}
