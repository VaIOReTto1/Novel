package com.novel.page.home.dao

fun com.novel.utils.network.api.front.HomeService.HomeBook.toEntity(type: String): HomeBookEntity {
    return HomeBookEntity(
        id = bookId,
        title = bookName,
        author = authorName,
        coverUrl = picUrl,
        description = bookDesc,
        category = "",
        categoryId = 0,
        rating = 0.0,
        readCount = 0,
        wordCount = 0,
        commentCount = 0,
        isCompleted = false,
        isVip = false,
        updateTime = System.currentTimeMillis(),
        lastChapterName = null,
        lastChapterUpdateTime = null,
        type = type
    )
}
