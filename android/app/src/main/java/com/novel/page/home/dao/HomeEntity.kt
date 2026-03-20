package com.novel.page.home.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * 首页书籍数据实体
 * 
 * 对应数据库表：home_books
 * 用于缓存首页各类推荐书籍数据
 */
@Stable
@Entity(tableName = "home_books")
data class HomeBookEntity(
    /** 书籍唯一标识符 */
    @PrimaryKey
    val id: Long,
    /** 书籍标题 */
    val title: String,
    /** 作者姓名 */
    val author: String,
    /** 封面图片URL */
    val coverUrl: String,
    /** 书籍简介描述 */
    val description: String,
    /** 书籍分类名称 */
    val category: String,
    /** 分类ID */
    val categoryId: Long = 0,
    /** 评分（0.0-5.0） */
    val rating: Double = 0.0,
    /** 阅读人数 */
    val readCount: Int = 0,
    /** 字数统计 */
    val wordCount: Long = 0,
    /** 评论数量 */
    val commentCount: Int = 0,
    /** 是否已完结 */
    val isCompleted: Boolean,
    /** 是否VIP书籍 */
    val isVip: Boolean,
    /** 最后更新时间戳 */
    val updateTime: Long,
    /** 最新章节名称 */
    val lastChapterName: String? = null,
    /** 最新章节更新时间 */
    val lastChapterUpdateTime: String? = null,
    /** 推荐类型：carousel(轮播), hot(热门), new(新书), vip等 */
    val type: String,
    /** 排序权重，数值越小越靠前 */
    val sortOrder: Int = 0
)

/**
 * 首页轮播图数据实体
 * 
 * 对应数据库表：home_banners
 * 用于管理首页顶部轮播广告和推荐内容
 */
@Stable
@Entity(tableName = "home_banners")
data class HomeBannerEntity(
    /** 轮播图唯一标识符 */
    @PrimaryKey
    val id: Long,
    /** 轮播图标题 */
    val title: String,
    /** 轮播图片URL */
    val imageUrl: String,
    /** 点击跳转链接（可为空） */
    val linkUrl: String?,
    /** 排序权重，数值越小越靠前 */
    val sortOrder: Int,
    /** 是否启用状态 */
    val isActive: Boolean = true
)

/**
 * 首页分类数据实体
 * 
 * 对应数据库表：home_categories
 * 用于管理书籍分类信息和导航
 */
@Stable
@Entity(tableName = "home_categories")
data class HomeCategoryEntity(
    /** 分类唯一标识符 */
    @PrimaryKey
    val id: Long,
    /** 分类名称 */
    val name: String,
    /** 分类图标URL（可为空） */
    val iconUrl: String?,
    /** 排序权重，数值越小越靠前 */
    val sortOrder: Int,
    /** 该分类下的书籍数量 */
    val bookCount: Int = 0
)

/**
 * 首页服务响应数据转实体扩展函数
 * 
 * 将网络API返回的HomeBook对象转换为本地数据库实体
 * 用于数据缓存和离线访问
 * 
 * @param type 书籍推荐类型，用于区分不同的推荐位
 * @return HomeBookEntity 数据库实体对象
 */
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
