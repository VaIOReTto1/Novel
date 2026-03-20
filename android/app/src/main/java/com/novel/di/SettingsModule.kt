 package com.novel.di

import android.content.Context
import com.novel.rn.settings.SettingsUtils
import com.novel.rn.settings.usecase.ClearCacheUseCase
import com.novel.rn.settings.usecase.ExportUserDataUseCase
import com.novel.rn.settings.usecase.GetUserSettingsUseCase
import com.novel.rn.settings.usecase.ImportUserDataUseCase
import com.novel.rn.settings.usecase.UpdateSettingsUseCase
import com.novel.ui.theme.ThemeManager
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 设置模块依赖注入
 * 
 * 提供设置相关的所有依赖：
 * - SettingsUtils 工具类
 * - ThemeManager 主题管理器
 * - Settings相关的UseCase
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    /**
     * 提供设置工具类
     */
//    @Provides
//    @Singleton
//    fun provideSettingsUtils(
//        @ApplicationContext context: Context,
//        novelUserDefaults: NovelUserDefaults
//    ): SettingsUtils = SettingsUtils(context, novelUserDefaults)

    /**
     * 提供主题管理器单例
     * 注意：ThemeManager使用单例模式，需要通过getInstance()获取
     */
    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context
    ): ThemeManager {
        // 使用ThemeManager的单例获取方法
        return ThemeManager.getInstance(context)
    }

    /**
     * 提供获取用户设置UseCase
     */
    @Provides
    @Singleton
    fun provideGetUserSettingsUseCase(
        settingsUtils: SettingsUtils,
        themeManager: ThemeManager
    ): GetUserSettingsUseCase = GetUserSettingsUseCase(settingsUtils, themeManager)

    /**
     * 提供更新设置UseCase
     */
    @Provides
    @Singleton
    fun provideUpdateSettingsUseCase(
        settingsUtils: SettingsUtils,
        themeManager: ThemeManager
    ): UpdateSettingsUseCase = UpdateSettingsUseCase(settingsUtils, themeManager)

    /**
     * 提供清理缓存UseCase
     */
    @Provides
    @Singleton
    fun provideClearCacheUseCase(
        settingsUtils: SettingsUtils
    ): ClearCacheUseCase = ClearCacheUseCase(settingsUtils)

    /**
     * 提供导出用户数据UseCase
     */
    @Provides
    @Singleton
    fun provideExportUserDataUseCase(
        @ApplicationContext context: Context,
        settingsUtils: SettingsUtils,
        novelUserDefaults: NovelUserDefaults
    ): ExportUserDataUseCase = ExportUserDataUseCase(context, settingsUtils, novelUserDefaults)

    /**
     * 提供导入用户数据UseCase
     */
    @Provides
    @Singleton
    fun provideImportUserDataUseCase(
        @ApplicationContext context: Context,
        settingsUtils: SettingsUtils,
        themeManager: ThemeManager,
        novelUserDefaults: NovelUserDefaults
    ): ImportUserDataUseCase = ImportUserDataUseCase(context, settingsUtils, themeManager, novelUserDefaults)
}
