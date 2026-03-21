package com.novel.utils.Store.UserDefaults

import android.content.SharedPreferences
import androidx.compose.runtime.Stable
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import timber.log.Timber

/**
 * 用户配置存储键枚举
 * 
 * 对应Swift中的NovelUserDefaults.Key，定义应用中各种用户配置的存储键
 * 统一管理配置键名，避免硬编码和重复定义
 */
enum class NovelUserDefaultsKey(val key: String) {
    /** 用户登录状态标识 */
    IS_LOGGED_IN("isLoggedIn"),
    /** 新闻推送类型配置 */
    NEWS_TYPE("newsType"),
    /** Token过期时间戳 */
    TOKEN_EXPIRES_AT("tokenExpiresAt"),
    /** 用户唯一标识符 */
    USER_ID("uid"),
    /** 页面翻转效果设置 */
    PAGE_FLIP_EFFECT("pageFlipEffect"),
    /** 屏幕亮度设置 */
    BRIGHTNESS("brightness"),
    /** 字体大小设置 */
    FONT_SIZE("fontSize"),
    /** 文字颜色设置 */
    TEXT_COLOR("textColor"),
    /** 背景颜色设置 */
    BACKGROUND_COLOR("backgroundColor")
}

/**
 * 用户配置存储接口
 * 
 * 提供用户偏好设置和应用配置的统一存储接口
 * 抽象接口设计便于Mock测试和替换实现（如迁移到DataStore）
 * 
 * 核心功能：
 * - 类型安全的配置存储和读取
 * - 支持枚举键和字符串键两种方式
 * - 提供配置检查和批量清理功能
 * - 兼容多种数据类型存储
 */
interface NovelUserDefaults {
    /**
     * 存储配置值（枚举键）
     * @param value 配置值，支持String、Int、Boolean、Float、Long、Set<String>
     * @param forKey 配置键枚举
     */
    fun <T> set(value: T, forKey: NovelUserDefaultsKey)
    
    /**
     * 获取配置值（枚举键）
     * @param key 配置键枚举
     * @return 配置值，不存在时返回null
     */
    fun <T> get(key: NovelUserDefaultsKey): T?
    
    /**
     * 删除配置项（枚举键）
     * @param key 配置键枚举
     */
    fun remove(key: NovelUserDefaultsKey)
    
    /**
     * 检查配置是否存在（枚举键）
     * @param key 配置键枚举
     * @return true表示配置存在，false表示不存在
     */
    fun contains(key: NovelUserDefaultsKey): Boolean
    
    /**
     * 清空所有已定义的配置项
     * 危险操作，将清除所有枚举定义的配置
     */
    fun clearAll()

    /**
     * 存储字符串配置（字符串键）
     * @param key 配置键名
     * @param value 字符串值
     */
    fun setString(key: String, value: String)
    
    /**
     * 获取字符串配置（字符串键）
     * @param key 配置键名
     * @return 字符串值，不存在时返回null
     */
    fun getString(key: String): String?
    
    /**
     * 删除配置项（字符串键）
     * @param key 配置键名
     */
    fun remove(key: String)
}

/**
 * 基于SharedPreferences的用户配置存储实现
 * 
 * 核心特性：
 * - 使用SharedPreferences作为底层存储
 * - 支持多种数据类型的自动识别和存储
 * - 异步提交确保性能，避免阻塞主线程
 * - 类型安全的配置读取和写入
 * - 完善的异常处理和日志记录
 */
@Stable
@Singleton
class SharedPrefsUserDefaults @Inject constructor(
    @Stable
    private val prefs: SharedPreferences
) : NovelUserDefaults {

    companion object {
        private const val TAG = "SharedPrefsUserDefaults"
    }

    /**
     * 存储配置值（枚举键）
     * 
     * 根据值的类型自动选择对应的存储方法
     * 支持的类型：String, Int, Boolean, Float, Long, Set<String>
     */
    override fun <T> set(value: T, forKey: NovelUserDefaultsKey) {
        try {
            Timber.tag(TAG).d("存储配置: key=${forKey.key}, type=$value")
            prefs.edit {
                when (value) {
                    is String -> putString(forKey.key, value)
                    is Int -> putInt(forKey.key, value)
                    is Boolean -> putBoolean(forKey.key, value)
                    is Float -> putFloat(forKey.key, value)
                    is Long -> putLong(forKey.key, value)
                    is Set<*> -> @Suppress("UNCHECKED_CAST")
                    putStringSet(forKey.key, value as Set<String>)
                    else -> {
//                        Timber.tag(TAG).w("不支持的配置类型: ${value?.javaClass?.simpleName}")
                        throw IllegalArgumentException("不支持的类型：${value}")
                    }
                }
            } // 异步提交
            Timber.tag(TAG).d("配置存储成功: key=${forKey.key}")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "配置存储失败: key=${forKey.key}")
            throw e
        }
    }

    /**
     * 获取配置值（枚举键）
     * 
     * 从SharedPreferences中获取配置值并进行类型转换
     * @return 配置值，类型转换失败或不存在时返回null
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: NovelUserDefaultsKey): T? {
        return try {
            val value = prefs.all[key.key] as? T
            Timber.tag(TAG).d("读取配置: key=${key.key}, found=${value != null}")
            value
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "配置读取失败: key=${key.key}")
            null
        }
    }

    /**
     * 删除配置项（枚举键）
     */
    override fun remove(key: NovelUserDefaultsKey) {
        try {
            Timber.tag(TAG).d("删除配置: key=${key.key}")
            prefs.edit { remove(key.key) }
            Timber.tag(TAG).d("配置删除成功: key=${key.key}")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "配置删除失败: key=${key.key}")
        }
    }

    /**
     * 检查配置是否存在（枚举键）
     */
    override fun contains(key: NovelUserDefaultsKey): Boolean {
        val exists = prefs.contains(key.key)
        Timber.tag(TAG).d("检查配置存在性: key=${key.key}, exists=$exists")
        return exists
    }

    /**
     * 清空所有已定义的配置项
     * 
     * 只清除枚举中定义的配置项，不影响其他可能存在的配置
     */
    override fun clearAll() {
        try {
            Timber.tag(TAG).d("开始清空所有配置")
            prefs.edit {
                NovelUserDefaultsKey.entries.forEach { remove(it.key) }
            }
            Timber.tag(TAG).d("所有配置清空完成")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "配置清空失败")
        }
    }

    /**
     * 存储字符串配置（字符串键）
     */
    override fun setString(key: String, value: String) {
        try {
            Timber.tag(TAG).d("存储字符串配置: key=$key")
            prefs.edit { putString(key, value) }
            Timber.tag(TAG).d("字符串配置存储成功: key=$key")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "字符串配置存储失败: key=$key")
        }
    }

    /**
     * 获取字符串配置（字符串键）
     */
    override fun getString(key: String): String? {
        return try {
            val value = prefs.getString(key, null)
            Timber.tag(TAG).d("读取字符串配置: key=$key, found=${value != null}")
            value
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "字符串配置读取失败: key=$key")
            null
        }
    }

    /**
     * 删除配置项（字符串键）
     */
    override fun remove(key: String) {
        try {
            Timber.tag(TAG).d("删除字符串配置: key=$key")
            prefs.edit { remove(key) }
            Timber.tag(TAG).d("字符串配置删除成功: key=$key")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "字符串配置删除失败: key=$key")
        }
    }
}
