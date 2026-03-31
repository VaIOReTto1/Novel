package com.novel.ui.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.novel.ui.theme.NovelTheme
import com.novel.ui.theme.Stage7Tokens

@Composable
fun Stage7ShowcaseScreen() {
    val sections = stage7ShowcaseSections()
    val lightColors = Stage7Tokens.LightColors
    val space = Stage7Tokens.Space

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightColors.getValue("color.bg.canvas.light"))
            .padding(space.getValue("300")),
        verticalArrangement = Arrangement.spacedBy(space.getValue("200")),
    ) {
        Text(
            text = "Stage 7 Showcase",
            color = lightColors.getValue("color.brand.secondary.light"),
        )
        Text(
            text = "Novel Visual System",
            color = lightColors.getValue("color.text.primary.light"),
            fontWeight = FontWeight.SemiBold,
        )

        sections.forEach { section ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = lightColors.getValue("color.bg.surface.light"),
                ),
            ) {
                Column(
                    modifier = Modifier.padding(space.getValue("200")),
                    verticalArrangement = Arrangement.spacedBy(space.getValue("100")),
                ) {
                    Text(
                        text = section.title,
                        color = lightColors.getValue("color.text.primary.light"),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = section.description,
                        color = lightColors.getValue("color.text.secondary.light"),
                    )
                }
            }
        }
    }
}
