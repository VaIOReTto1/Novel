package com.novel.page.login.dao

import androidx.compose.runtime.Stable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 * 
 * 核心功能：
 * - 提供用户信息的CRUD操作接口  
 * - 支持响应式数据查询（Flow）
 * - 用户查询和统计功能
 * - 批量操作和条件筛选
 * 
 * 数据库表：users
 * 主键：uid（用户唯一标识）
 * 
 * 设计特点：
 * - 使用REPLACE策略处理用户信息更新
 * - 提供同步和异步两种查询方式
 * - 支持模糊查询和条件筛选
 * - 按最后更新时间排序，保证数据时效性
 */
@Stable
@Dao
interface UserDao {

    // ——— 写操作 ———

    /**
     * 插入或更新用户信息
     * 
     * 使用REPLACE策略，如果用户已存在则更新信息
     * @param user 用户实体对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * 批量插入或更新用户信息
     * 
     * 用于批量同步用户数据，提升插入效率
     * @param users 用户实体列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    /**
     * 清空所有用户数据
     * 
     * 危险操作，用于重置或清理缓存
     * 通常在用户登出或数据迁移时调用
     */
    @Query("DELETE FROM users")
    suspend fun clearAll()

    /**
     * 删除指定用户
     * 
     * 根据用户ID删除特定用户信息
     * @param uid 用户唯一标识符
     */
    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUser(uid: String)

    // ——— 查询操作 ———

    /**
     * 获取所有用户（同步）
     * 
     * 按最后更新时间降序排列，最新用户排在前面
     * @return 用户实体列表
     */
    @Query("SELECT * FROM users ORDER BY lastUpdateTime DESC")
    suspend fun getAllUsers(): List<UserEntity>

    /**
     * 获取所有用户（响应式）
     * 
     * 支持数据变化时自动更新UI
     * @return 用户实体列表的Flow
     */
    @Query("SELECT * FROM users ORDER BY lastUpdateTime DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    /**
     * 根据UID获取用户（同步）
     * 
     * 根据用户唯一标识符查询特定用户
     * @param uid 用户唯一标识符
     * @return 用户实体，不存在时返回null
     */
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserByUid(uid: String): UserEntity?

    /**
     * 根据UID获取用户（响应式）
     * 
     * 支持用户信息变化时自动更新
     * @param uid 用户唯一标识符
     * @return 用户实体的Flow
     */
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserByUidFlow(uid: String): Flow<UserEntity?>

    /**
     * 根据昵称模糊查询用户
     * 
     * 支持部分匹配搜索，用于用户搜索功能
     * @param nickName 昵称关键词
     * @return 匹配的用户列表
     */
    @Query("SELECT * FROM users WHERE nickName LIKE '%' || :nickName || '%' ORDER BY lastUpdateTime DESC")
    suspend fun getUsersByNickName(nickName: String): List<UserEntity>

    /**
     * 获取用户总数（同步）
     * 
     * 统计数据库中的用户数量
     * @return 用户总数
     */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    /**
     * 获取用户总数（响应式）
     * 
     * 支持用户数量变化时自动更新
     * @return 用户总数的Flow
     */
    @Query("SELECT COUNT(*) FROM users")
    fun getUserCountFlow(): Flow<Int>

    /**
     * 检查用户是否存在
     * 
     * 高效的存在性检查，避免查询完整用户信息
     * @param uid 用户唯一标识符
     * @return true表示用户存在，false表示不存在
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE uid = :uid)")
    suspend fun isUserExists(uid: String): Boolean

    /**
     * 获取最近更新的用户
     * 
     * 用于显示活跃用户或最新注册用户
     * @param limit 限制返回数量，默认10个
     * @return 最近更新的用户列表
     */
    @Query("SELECT * FROM users ORDER BY lastUpdateTime DESC LIMIT :limit")
    suspend fun getRecentUsers(limit: Int = 10): List<UserEntity>

    /**
     * 获取指定性别的用户
     * 
     * 按性别筛选用户，用于统计分析
     * @param userSex 用户性别（通常0-女, 1-男, 2-其他）
     * @return 指定性别的用户列表
     */
    @Query("SELECT * FROM users WHERE userSex = :userSex ORDER BY lastUpdateTime DESC")
    suspend fun getUsersBySex(userSex: Int): List<UserEntity>

    /**
     * 获取时间范围内的用户
     * 
     * 按更新时间范围筛选用户，用于数据分析
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 时间范围内的用户列表
     */
    @Query("SELECT * FROM users WHERE lastUpdateTime BETWEEN :startTime AND :endTime ORDER BY lastUpdateTime DESC")
    suspend fun getUsersByTimeRange(startTime: Long, endTime: Long): List<UserEntity>
}
