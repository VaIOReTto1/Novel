package com.novel.di

import android.content.Context
import android.content.SharedPreferences
import com.novel.core.config.NovelUserDefaultsBackedRefactorFeatureFlags
import com.novel.core.config.RefactorFeatureFlagDefaults
import com.novel.core.config.RefactorFeatureFlags
import com.novel.core.storage.LegacyStorageFacade
import com.novel.core.storage.SettingsDataStorePilot
import com.novel.core.storage.StorageFacade
import com.novel.utils.TimberLogger
import com.novel.utils.Store.NovelKeyChain.NovelKeyChain
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.SharedPrefsUserDefaults
import com.novel.utils.network.TokenProvider
import com.novel.utils.StringProvider
import com.novel.utils.AndroidStringProvider
import com.novel.rn.settings.SettingsPreferenceStorage
import com.novel.rn.settings.SettingsUtils
import java.time.Clock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 用户数据和配置依赖注入模块
 * 
 * 负责提供用户数据存储和配置管理相关的核心服务：
 * - SharedPreferences：基础键值对存储
 * - NovelUserDefaults：应用配置管理
 * - TokenProvider：用户认证令牌管理
 * - StringProvider：字符串资源服务
 * - SettingsUtils：设置工具类
 * - Clock：时间服务
 */
@Module
@InstallIn(SingletonComponent::class)
object NovelUserDefaultsModule {

    private const val TAG = "NovelUserDefaultsModule"
    private const val PREFS_NAME = "Novel_user_defaults"

    /**
     * 提供SharedPreferences实例
     * 
     * 应用的基础键值对存储，用于持久化：
     * - 用户配置信息
     * - 应用状态数据
     * - 临时缓存数据
     * 
     * @param ctx 应用上下文
     * @return SharedPreferences实例
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext ctx: Context
    ): SharedPreferences {
        TimberLogger.d(TAG, "创建SharedPreferences: $PREFS_NAME")
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * 提供NovelUserDefaults服务
     * 
     * 应用层的配置管理抽象，提供类型安全的配置访问接口
     * 
     * @param impl SharedPreferences的具体实现
     * @return NovelUserDefaults接口实例
     */
    @Provides
    @Singleton
    fun provideNovelUserDefaults(
        impl: SharedPrefsUserDefaults
    ): NovelUserDefaults {
        TimberLogger.d(TAG, "创建NovelUserDefaults服务")
        return impl
    }

    /**
     * 提供令牌管理服务
     * 
     * 负责用户认证令牌的安全存储和管理：
     * - JWT令牌的加密存储
     * - 令牌过期检查和刷新
     * - 自动登录状态管理
     * 
     * @param keyChain 安全密钥链服务
     * @return TokenProvider实例
     */
    @Provides
    @Singleton
    fun provideStorageFacade(
        legacyStorageFacade: LegacyStorageFacade
    ): StorageFacade {
        TimberLogger.d(TAG, "创建StorageFacade兼容层")
        return legacyStorageFacade
    }

    @Provides
    @Singleton
    fun provideRefactorFeatureFlagDefaults(): RefactorFeatureFlagDefaults {
        return RefactorFeatureFlagDefaults(
            enableBridgeErrorMapper = com.novel.BuildConfig.REFACTOR_ENABLE_BRIDGE_ERROR_MAPPER,
            enableBridgeSharedScopes = com.novel.BuildConfig.REFACTOR_ENABLE_BRIDGE_SHARED_SCOPES,
            enableSettingsDataStorePilot = com.novel.BuildConfig.REFACTOR_ENABLE_SETTINGS_DATASTORE_PILOT
        )
    }

    @Provides
    @Singleton
    fun provideRefactorFeatureFlags(
        impl: NovelUserDefaultsBackedRefactorFeatureFlags
    ): RefactorFeatureFlags {
        TimberLogger.d(TAG, "创建RefactorFeatureFlags服务")
        return impl
    }

    @Provides
    @Singleton
    fun provideTokenProvider(userDefaults: NovelUserDefaults): TokenProvider {
        return object : TokenProvider {
            override fun getToken(): String? {
                return userDefaults.getString("access_token")
            }
            
            override fun accessToken(): String? {
                return userDefaults.getString("access_token")
            }
            
            override suspend fun saveToken(accessToken: String, refreshToken: String) {
                userDefaults.setString("access_token", accessToken)
                userDefaults.setString("refresh_token", refreshToken)
            }
            
            override suspend fun clear() {
                userDefaults.remove("access_token")
                userDefaults.remove("refresh_token")
            }
        }
    }

    /**
     * 提供应用上下文
     * 
     * @param context 应用上下文
     * @return Context实例
     */
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    /**
     * 提供系统时钟服务
     * 
     * 用于获取系统时间，支持：
     * - 时间戳生成
     * - 日期格式化
     * - 时区处理
     * 
     * @return Clock实例
     */
    @Provides
    @Singleton
    fun provideClock(): Clock {
        TimberLogger.d(TAG, "创建Clock服务")
        return Clock.systemDefaultZone()
    }

    /**
     * 提供字符串资源服务
     * 
     * 统一的字符串资源访问接口，支持：
     * - 多语言国际化
     * - 动态字符串格式化
     * - 主题相关文本
     * 
     * @param androidStringProvider Android平台实现
     * @return StringProvider接口实例
     */
    @Provides
    @Singleton
    fun provideStringProvider(
        androidStringProvider: AndroidStringProvider
    ): StringProvider {
        TimberLogger.d(TAG, "创建StringProvider服务")
        return androidStringProvider
    }

    /**
     * 提供设置工具类
     * 
     * 高级设置管理工具，提供：
     * - 复杂配置的读写
     * - 配置变更监听
     * - 默认值管理
     * 
     * @param context 应用上下文
     * @param novelUserDefaults 基础配置服务
     * @param networkCacheManager 网络缓存管理器
     * @return SettingsUtils实例
     */
    @Provides
    @Singleton
    fun provideSettingsUtils(
        @ApplicationContext context: Context,
        settingsPreferenceStorage: SettingsPreferenceStorage,
        settingsDataStorePilot: SettingsDataStorePilot,
        refactorFeatureFlags: RefactorFeatureFlags
    ): SettingsUtils {
        TimberLogger.d(TAG, "创建SettingsUtils工具")
        return SettingsUtils(context, settingsPreferenceStorage, settingsDataStorePilot, refactorFeatureFlags)
    }
}
