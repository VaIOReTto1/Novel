package com.novel.di

import com.novel.page.read.service.HistoryService
import com.novel.page.read.service.HistoryServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 历史记录服务依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryModule {
    
    /**
     * 绑定HistoryService接口到具体实现
     */
    @Binds
    @Singleton
    abstract fun bindHistoryService(
        impl: HistoryServiceImpl
    ): HistoryService
}