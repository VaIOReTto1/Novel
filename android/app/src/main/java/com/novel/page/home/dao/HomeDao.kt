package com.novel.page.home.dao

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 首页数据访问对象
 * 
 * 核心功能：
 * - 管理首页书籍数据的CRUD操作
 * - 支持按类型分组的书籍查询
 * - 轮播图和分类数据管理
 * - 提供响应式数据流和同步查询接口
 * 
 * 数据表结构：
 * - home_books: 首页推荐书籍表
 * - home_banners: 轮播图表
 * - home_categories: 分类信息表
 */
@Stable
@Dao
interface HomeDao {
    
    // region 书籍相关操作
    
    /**
     * 获取指定类型的书籍列表（响应式）
     * 
     * 按排序字段和更新时间排序
     * @param type 书籍类型（如"hot", "new", "recommend"等）
     * @return 响应式书籍列表Flow
     */
    @Query("SELECT * FROM home_books WHERE type = :type ORDER BY sortOrder ASC, updateTime DESC")
    fun getBooksByType(type: String): Flow<List<HomeBookEntity>>
    
    /**
     * 获取指定类型的书籍列表（同步）
     * 
     * 用于一次性数据获取，不需要观察数据变化的场景
     * @param type 书籍类型
     * @return 书籍列表
     */
    @Query("SELECT * FROM home_books WHERE type = :type ORDER BY sortOrder ASC, updateTime DESC")
    suspend fun getBooksByTypeSync(type: String): List<HomeBookEntity>
    
    /**
     * 批量插入书籍列表
     * 
     * 使用REPLACE策略，相同ID的数据会被覆盖
     * @param books 书籍实体列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<HomeBookEntity>)
    
    /**
     * 删除指定类型的书籍
     * 
     * 用于清理特定类型的过期数据
     * @param type 书籍类型
     */
    @Query("DELETE FROM home_books WHERE type = :type")
    suspend fun deleteBooksByType(type: String)
    
    /**
     * 清空所有书籍数据
     * 
     * 危险操作，用于重置或清理缓存
     */
    @Query("DELETE FROM home_books")
    suspend fun clearAllBooks()
    
    // endregion
    
    // region 轮播图相关操作
    
    /**
     * 获取活跃的轮播图列表（响应式）
     * 
     * 只返回启用状态的轮播图，按排序字段排序
     * @return 响应式轮播图列表Flow
     */
    @Query("SELECT * FROM home_banners WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveBanners(): Flow<List<HomeBannerEntity>>
    
    /**
     * 获取活跃的轮播图列表（同步）
     * 
     * @return 轮播图列表
     */
    @Query("SELECT * FROM home_banners WHERE isActive = 1 ORDER BY sortOrder ASC")
    suspend fun getActiveBannersSync(): List<HomeBannerEntity>
    
    /**
     * 批量插入轮播图列表
     * 
     * @param banners 轮播图实体列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<HomeBannerEntity>)
    
    /**
     * 清空所有轮播图数据
     */
    @Query("DELETE FROM home_banners")
    suspend fun clearAllBanners()
    
    // endregion
    
    // region 分类相关操作
    
    /**
     * 获取分类列表（响应式）
     * 
     * 按排序字段排序
     * @return 响应式分类列表Flow
     */
    @Query("SELECT * FROM home_categories ORDER BY sortOrder ASC")
    fun getCategories(): Flow<List<HomeCategoryEntity>>
    
    /**
     * 获取分类列表（同步）
     * 
     * @return 分类列表
     */
    @Query("SELECT * FROM home_categories ORDER BY sortOrder ASC")
    suspend fun getCategoriesSync(): List<HomeCategoryEntity>
    
    /**
     * 批量插入分类列表
     * 
     * @param categories 分类实体列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<HomeCategoryEntity>)
    
    /**
     * 清空所有分类数据
     */
    @Query("DELETE FROM home_categories")
    suspend fun clearAllCategories()
    
    // endregion
    
    // region 综合操作
    
    /**
     * 清空所有首页数据
     * 
     * 事务操作，确保数据一致性
     * 用于重置应用或清理缓存时调用
     */
    @Transaction
    suspend fun clearAllHomeData() {
        TimberLogger.d("HomeDao", "开始清空所有首页数据")
        clearAllBooks()
        clearAllBanners()
        clearAllCategories()
        TimberLogger.d("HomeDao", "所有首页数据清空完成")
    }
    
    // endregion
} 
