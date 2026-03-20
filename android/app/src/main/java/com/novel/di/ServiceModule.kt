package com.novel.di

import com.novel.page.read.service.common.AndroidServiceLogger
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.LruSessionCache
import com.novel.page.read.service.common.OptimizedDispatcherProvider
import com.novel.page.read.service.common.ReaderServiceConfig
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.service.common.SessionCache
import com.novel.page.read.service.settings.ReaderSettingsStorage
import com.novel.page.read.service.settings.SettingsParser
import com.novel.page.read.service.settings.SettingsSaver
import com.novel.page.read.viewmodel.ChapterCache
import com.novel.utils.performance.StartupPerformanceMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * 服务层依赖注入模块
 * 
 * 负责提供应用的核心服务组件：
 * - 网络请求相关服务
 * - 数据缓存和存储服务
 * - 业务逻辑处理服务
 * - 性能监控服务
 * 
 * 所有服务采用单例模式，确保资源利用最优化
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    /**
     * 提供协程调度器（性能优化版）
     */
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = OptimizedDispatcherProvider()

    /**
     * 提供服务日志记录器
     */
    @Provides
    @Singleton
    fun provideServiceLogger(): ServiceLogger = AndroidServiceLogger()

    /**
     * 提供章节会话缓存
     */
    @Provides
    @Singleton
    fun provideChapterSessionCache(): SessionCache<String, ChapterCache> = 
        LruSessionCache(ReaderServiceConfig.MAX_SESSION_CACHE_SIZE)

    /**
     * 提供设置解析器
     */
    @Provides
    @Singleton
    fun provideSettingsParser(
        readerSettingsStorage: ReaderSettingsStorage,
        logger: ServiceLogger
    ): SettingsParser = SettingsParser(readerSettingsStorage, logger)

    /**
     * 提供设置保存器
     */
    @Provides
    @Singleton
    fun provideSettingsSaver(
        readerSettingsStorage: ReaderSettingsStorage,
        logger: ServiceLogger
    ): SettingsSaver = SettingsSaver(readerSettingsStorage, logger)

    /**
     * 提供启动性能监控器
     * 
     * @param context 应用上下文
     * @return StartupPerformanceMonitor - 启动性能监控服务
     */
    @Provides
    @Singleton
    fun provideStartupPerformanceMonitor(
        @ApplicationContext context: Context
    ): StartupPerformanceMonitor {
        return StartupPerformanceMonitor(context)
    }
} 
