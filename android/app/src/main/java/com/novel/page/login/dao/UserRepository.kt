package com.novel.page.login.dao

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import com.novel.utils.network.api.front.user.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户数据仓库
 * 
 * 统一管理用户数据的缓存、同步和持久化操作
 * 提供本地数据库和网络数据的统一访问接口
 * 确保数据一致性和安全性
 */
@Stable
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userDefaults: NovelUserDefaults
) {
    
    companion object {
        private const val TAG = "UserRepository"
    }
    
    /**
     * 缓存用户信息到本地数据库
     * 
     * 将网络获取的用户数据存储到本地，支持离线访问
     * 
     * @param data 用户信息数据传输对象
     * @return 是否缓存成功
     */
    suspend fun cacheUser(data: UserService.UserInfoData): Boolean = withContext(Dispatchers.IO) {
        try {
            val uid = userDefaults.get<Int>(NovelUserDefaultsKey.USER_ID)?.toString()
            
            if (uid.isNullOrEmpty()) {
                TimberLogger.e(TAG, "缓存用户失败: UID为空")
                return@withContext false
            }
            
            TimberLogger.d(TAG, "开始缓存用户数据: uid=$uid")
            
            // 先清空旧数据，确保数据一致性
            userDao.clearAll()
            
            // 转换并插入新数据
            val userEntity = data.toEntity(uid)
            userDao.insertUser(userEntity)
            
            TimberLogger.d(TAG, "用户数据缓存成功: ${userEntity.nickName}")
            true
        } catch (e: Exception) {
            TimberLogger.e(TAG, "缓存用户数据失败", e)
            false
        }
    }
    
    /**
     * 获取当前登录用户信息
     * 
     * @return 当前用户实体，如果未登录或数据不存在则返回null
     */
    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.IO) {
        try {
            val uid = userDefaults.get<Int>(NovelUserDefaultsKey.USER_ID)?.toString()
            if (uid.isNullOrEmpty()) {
                TimberLogger.w(TAG, "获取当前用户失败: UID为空")
                return@withContext null
            }
            
            val user = userDao.getUserByUid(uid)
            if (user != null) {
                TimberLogger.v(TAG, "成功获取当前用户: ${user.nickName}")
            }
            user
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取当前用户失败", e)
            null
        }
    }
    
    /**
     * 获取所有本地用户数据流
     * 
     * 返回响应式数据流，自动更新UI
     * 
     * @return 用户列表的Flow
     */
    fun getAllUsersFlow(): Flow<List<UserEntity>> = flow {
        try {
            val users = userDao.getAllUsers()
            TimberLogger.v(TAG, "发射用户列表，数量: ${users.size}")
            emit(users)
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取用户列表失败", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * 获取所有本地用户数据
     * 
     * 挂起函数版本，用于一次性获取
     * 
     * @return 用户列表
     */
    suspend fun getAllUsers(): List<UserEntity> = withContext(Dispatchers.IO) {
        try {
            userDao.getAllUsers()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取用户列表失败", e)
            emptyList()
        }
    }
    
    /**
     * 清理所有本地用户数据
     * 
     * 用于登出或数据重置场景
     * 
     * @return 是否清理成功
     */
    suspend fun clearAllUsers(): Boolean = withContext(Dispatchers.IO) {
        try {
            userDao.clearAll()
            TimberLogger.d(TAG, "已清理所有用户数据")
            true
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理用户数据失败", e)
            false
        }
    }
    
    /**
     * 更新用户信息
     * 
     * 使用Room的REPLACE策略进行更新
     * 
     * @param userEntity 要更新的用户实体
     * @return 是否更新成功
     */
    suspend fun updateUser(userEntity: UserEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            userDao.insertUser(userEntity) // Room的REPLACE策略会自动更新
            TimberLogger.d(TAG, "用户信息更新成功: ${userEntity.nickName}")
            true
        } catch (e: Exception) {
            TimberLogger.e(TAG, "用户信息更新失败", e)
            false
        }
    }
    
    /**
     * 检查本地是否有用户数据缓存
     * 
     * @return 是否有缓存数据
     */
    suspend fun hasUserCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            userDao.getUserCount() > 0
        } catch (e: Exception) {
            TimberLogger.e(TAG, "检查用户缓存失败", e)
            false
        }
    }
    
    /**
     * 获取用户缓存数量
     * 
     * @return 缓存的用户数量
     */
    suspend fun getUserCacheCount(): Int = withContext(Dispatchers.IO) {
        try {
            userDao.getUserCount()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取用户缓存数量失败", e)
            0
        }
    }
}

// ――― DTO ↔ Entity 转换扩展函数 ―――

/**
 * 将网络层DTO转换为本地Entity
 * 
 * 确保数据转换的完整性和安全性
 * 处理API响应中可能为null的字段
 * 
 * @param uid 用户唯一标识
 * @return 用户实体对象
 */
private fun UserService.UserInfoData.toEntity(uid: String) = UserEntity(
    uid = uid,
    nickName = nickName,
    userPhoto = userPhoto ?: "", // 处理null值，使用空字符串作为默认值
    userSex = userSex ?: 0 // 处理null值，使用0作为默认值（未知性别）
)

/**
 * 将本地Entity转换为业务层模型
 * 
 * @return 业务层用户信息对象
 */
fun UserEntity.toDomainModel() = UserInfo(
    uid = uid,
    nickName = nickName,
    userPhoto = userPhoto,
    userSex = userSex
)

/**
 * 业务层用户信息数据类
 * 
 * 用于在业务逻辑层传递用户信息，与数据层解耦
 * 
 * @property uid 用户唯一标识
 * @property nickName 用户昵称
 * @property userPhoto 用户头像URL
 * @property userSex 用户性别
 */
data class UserInfo(
    val uid: String,
    val nickName: String,
    val userPhoto: String,
    val userSex: Int
)
