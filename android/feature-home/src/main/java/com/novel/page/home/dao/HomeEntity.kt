package com.novel.page.home.dao

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Stable
@Entity(tableName = "home_books")
data class HomeBookEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val author: String,
    val coverUrl: String,
    val description: String,
    val category: String,
    val categoryId: Long = 0,
    val rating: Double = 0.0,
    val readCount: Int = 0,
    val wordCount: Long = 0,
    val commentCount: Int = 0,
    val isCompleted: Boolean,
    val isVip: Boolean,
    val updateTime: Long,
    val lastChapterName: String? = null,
    val lastChapterUpdateTime: String? = null,
    val type: String,
    val sortOrder: Int = 0
)

@Stable
@Entity(tableName = "home_banners")
data class HomeBannerEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val imageUrl: String,
    val linkUrl: String?,
    val sortOrder: Int,
    val isActive: Boolean = true
)

@Stable
@Entity(tableName = "home_categories")
data class HomeCategoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val iconUrl: String?,
    val sortOrder: Int,
    val bookCount: Int = 0
)
