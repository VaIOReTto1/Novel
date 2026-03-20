package com.novel.di

import com.novel.page.welfare.usecase.InitializeWelfarePageUseCase
import com.novel.utils.TimberLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * 福利页面依赖注入模块
 * 
 * 负责提供福利页面相关的依赖注入配置：
 * - UseCase层的依赖提供
 * - ViewModel作用域的生命周期管理
 * - 福利页面相关服务的配置
 * 
 * 采用ViewModelComponent作用域，确保依赖与ViewModel生命周期一致
 */
@Module
@InstallIn(ViewModelComponent::class)
object WelfareModule {
    
    private const val TAG = "WelfareModule"
    
    /**
     * 提供初始化福利页面UseCase
     * 
     * @return InitializeWelfarePageUseCase实例
     */
    @Provides
    @ViewModelScoped
    fun provideInitializeWelfarePageUseCase(): InitializeWelfarePageUseCase {
        TimberLogger.d(TAG, "创建InitializeWelfarePageUseCase")
        return InitializeWelfarePageUseCase()
    }
}
