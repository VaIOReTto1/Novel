package com.novel.ui.showcase

data class NovelDesignShowcaseSection(
    val title: String,
    val description: String,
)

fun novelDesignShowcaseSections(): List<NovelDesignShowcaseSection> = listOf(
    NovelDesignShowcaseSection(
        title = "Foundations",
        description = "Warm editorial tokens for RN and Android",
    ),
    NovelDesignShowcaseSection(
        title = "Icons",
        description = "Legacy assets bridged through the Stage 7 icon registry",
    ),
    NovelDesignShowcaseSection(
        title = "Media",
        description = "Deterministic Picsum placeholders and Pexels credit overlays",
    ),
)
