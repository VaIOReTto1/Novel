package com.novel.utils.dao

import androidx.compose.runtime.Stable
import androidx.room.Database
import androidx.room.RoomDatabase
import com.novel.page.home.dao.HomeDao
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeBannerEntity
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.page.login.dao.UserDao
import com.novel.page.login.dao.UserEntity

/**
 * 小说应用数据库
 * 
 * 功能：
 * - 用户信息存储
 * - 首页数据缓存
 * - 书籍、横幅、分类数据管理
 * 
 * 特点：
 * - Room数据库框架
 * - 版本4，支持数据迁移
 * - 多实体统一管理
 */
@Stable
@Database(
    entities = [
        UserEntity::class,         // 用户信息
        HomeBookEntity::class,     // 首页书籍
        HomeBannerEntity::class,   // 首页横幅
        HomeCategoryEntity::class  // 首页分类
    ],
    version = 4,
    exportSchema = true
)
abstract class NovelDatabase : RoomDatabase() {
    /** 用户数据访问对象 */
    abstract fun userDao(): UserDao
    
    /** 首页数据访问对象 */
    abstract fun homeDao(): HomeDao
}
