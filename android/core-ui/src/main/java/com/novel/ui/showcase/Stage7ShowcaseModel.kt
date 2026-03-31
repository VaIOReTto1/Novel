package com.novel.ui.showcase

data class Stage7ShowcaseSection(
    val title: String,
    val description: String,
)

fun stage7ShowcaseSections(): List<Stage7ShowcaseSection> = listOf(
    Stage7ShowcaseSection(
        title = "Foundations",
        description = "Warm editorial tokens for RN and Android",
    ),
    Stage7ShowcaseSection(
        title = "Icons",
        description = "Legacy assets bridged through the Stage 7 icon registry",
    ),
    Stage7ShowcaseSection(
        title = "Media",
        description = "Deterministic Picsum placeholders and Pexels credit overlays",
    ),
)
