package com.novel.di

import android.content.Context
import androidx.compose.runtime.Stable
import com.google.gson.Gson
import com.novel.BuildConfig
import com.novel.core.network.DefaultLegacyApiExecutor
import com.novel.core.network.LegacyApiExecutor
import com.novel.core.network.LegacyApiServiceAdapter
import com.novel.core.network.NetworkFacade
import com.google.gson.GsonBuilder
import com.novel.rn.bridge.network.NavigationBridgeNetworkGateway
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService
import com.novel.utils.network.ImmutableListTypeAdapterFactory
import com.novel.utils.network.NetworkMonitor
import com.novel.utils.network.TokenProvider
import com.novel.utils.network.cache.CoalesceStats
import com.novel.utils.network.cache.IntelligentPrefetcher
import com.novel.utils.network.cache.NetworkCacheManager
import com.novel.utils.network.cache.ReadingBehaviorAnalyzer
import com.novel.utils.network.cache.RequestCoalescer
import com.novel.utils.network.interceptor.AuthInterceptor
import com.novel.utils.network.interceptor.PriorityInterceptor
import com.novel.utils.network.interceptor.RequestCoalescingInterceptor
import com.novel.utils.network.interceptor.SmartRetryInterceptor
import com.novel.utils.network.priority.PriorityRequestDispatcher
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.NewsService
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.network.api.front.user.UserService
import com.novel.utils.network.api.author.AuthorService
import com.novel.utils.network.repository.CachedBookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 网络客户端类型限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OptimizedOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StandardOkHttpClient

/**
 * 网络模块 - 集成所有网络层优化
 */
@Module
@InstallIn(SingletonComponent::class)
@Stable
object NetworkModule {
    
    private const val TAG = "NetworkModule"
    private const val CACHE_SIZE = 50L * 1024 * 1024 // 50MB
    private const val NETWORK_TIMEOUT = 30L // 30秒
    
    /**
     * 提供网络监控器
     */
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
    
    /**
     * 提供请求合并器
     */
    @Provides
    @Singleton
    fun provideRequestCoalescer(): RequestCoalescer {
        return RequestCoalescer()
    }
    
    /**
     * 提供优先级请求分发器
     */
    @Provides
    @Singleton
    fun providePriorityRequestDispatcher(networkMonitor: NetworkMonitor): PriorityRequestDispatcher {
        TimberLogger.d(TAG, "创建优先级请求分发器")
        return PriorityRequestDispatcher(networkMonitor)
    }
    
    @Provides
    @Singleton
    fun providePriorityInterceptor(): PriorityInterceptor {
        return PriorityInterceptor()
    }

    /**
     * 提供智能重试拦截器
     */
    @Provides
    @Singleton
    fun provideSmartRetryInterceptor(networkMonitor: NetworkMonitor): SmartRetryInterceptor {
        return SmartRetryInterceptor(networkMonitor)
    }
    
    /**
     * 提供请求合并拦截器
     */
    @Provides
    @Singleton
    fun provideRequestCoalescingInterceptor(requestCoalescer: RequestCoalescer): RequestCoalescingInterceptor {
        return RequestCoalescingInterceptor(requestCoalescer)
    }
    
    /**
     * 提供认证拦截器
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenProvider: TokenProvider): AuthInterceptor {
        return AuthInterceptor(tokenProvider)
    }
    

    
    /**
     * 提供Gson实例
     */
    @Provides
    @Singleton
    fun provideLegacyApiExecutor(): LegacyApiExecutor = DefaultLegacyApiExecutor

    @Provides
    @Singleton
    fun provideNetworkFacade(
        legacyApiExecutor: LegacyApiExecutor
    ): NetworkFacade {
        return LegacyApiServiceAdapter(legacyApiExecutor)
    }

    @Provides
    @Singleton
    fun provideNavigationBridgeNetworkGateway(
        networkFacade: NetworkFacade
    ): NavigationBridgeNetworkGateway {
        return NavigationBridgeNetworkGateway(
            networkFacade = networkFacade,
            frontBaseUrl = ApiService.BASE_URL_FRONT,
            authorBaseUrl = ApiService.BASE_URL_AUTHOR
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapterFactory(ImmutableListTypeAdapterFactory())
            .setLenient()
            .create()
    }
    
    /**
     * 提供HTTP缓存
     */
    @Provides
    @Singleton
    fun provideHttpCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, "http")
        return Cache(cacheDir, CACHE_SIZE)
    }
    
    /**
     * 提供证书锁定器（生产环境使用）
     */
    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // 示例：为生产环境添加证书锁定
            // .add("reader.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAA=")
            .build()
    }
    
    /**
     * 提供优化版OkHttpClient
     */
    @OptimizedOkHttpClient
    @Provides
    @Singleton
    fun provideOptimizedOkHttpClient(
        @ApplicationContext context: Context,
        cache: Cache,
        certificatePinner: CertificatePinner,
        authInterceptor: AuthInterceptor,
        priorityInterceptor: PriorityInterceptor,
        smartRetryInterceptor: SmartRetryInterceptor,
        coalescingInterceptor: RequestCoalescingInterceptor,
        priorityDispatcher: PriorityRequestDispatcher,
        networkMonitor: NetworkMonitor
    ): OkHttpClient {
        
        // 超时配置 - 根据网络状态自适应
        val timeouts = networkMonitor.getRecommendedTimeouts()
        
        // 创建优化的OkHttp客户端，集成所有网络优化功能
        val builder = OkHttpClient.Builder()
            .dispatcher(priorityDispatcher.createOkHttpDispatcher())
            .cache(cache)
            .certificatePinner(certificatePinner)
            .connectTimeout(timeouts.connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(timeouts.readTimeout, TimeUnit.MILLISECONDS)
            .callTimeout(timeouts.callTimeout, TimeUnit.MILLISECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(priorityInterceptor)
            .addInterceptor(smartRetryInterceptor)
            .addInterceptor(coalescingInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
        
        // 网络状态变化监听
        setupNetworkStateListener(networkMonitor, priorityDispatcher)
        
        TimberLogger.d(TAG, "优化版OkHttpClient初始化完成")
        return builder.build()
    }
    
    /**
     * 提供标准OkHttpClient（向后兼容）
     */
    @StandardOkHttpClient
    @Provides
    @Singleton
    fun provideStandardOkHttpClient(
        cache: Cache,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .callTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * 提供主要的Retrofit实例（使用优化客户端）
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        @OptimizedOkHttpClient okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL_FRONT)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * 提供网络缓存管理器
     */
    @Provides
    @Singleton
    fun provideNetworkCacheManager(
        @ApplicationContext context: Context,
        gson: Gson
    ): NetworkCacheManager {
        return NetworkCacheManager(context, gson)
    }
    
    /**
     * 提供阅读行为分析器
     */
    @Provides
    @Singleton
    fun provideReadingBehaviorAnalyzer(
        @ApplicationContext context: Context,
        userDefaults: com.novel.utils.Store.UserDefaults.NovelUserDefaults
    ): ReadingBehaviorAnalyzer {
        return ReadingBehaviorAnalyzer(context, userDefaults)
    }
    
    /**
     * 提供智能预取器
     */
    @Provides
    @Singleton
    fun provideIntelligentPrefetcher(
        bookService: BookService,
        cacheManager: NetworkCacheManager,
        behaviorAnalyzer: ReadingBehaviorAnalyzer
    ): IntelligentPrefetcher {
        return IntelligentPrefetcher(bookService, cacheManager, behaviorAnalyzer)
    }
    
    /**
     * 提供各种Service实例
     */
    @Provides
    @Singleton
    fun provideBookService(
        gson: Gson,
        networkFacade: NetworkFacade
    ): BookService {
        return BookService(gson, networkFacade)
    }
    
    @Provides
    @Singleton
    fun provideSearchService(
        gson: Gson,
        networkFacade: NetworkFacade
    ): SearchService {
        return SearchService(gson, networkFacade)
    }
    
    @Provides
    @Singleton
    fun provideHomeService(
        gson: Gson,
        networkFacade: NetworkFacade
    ): HomeService {
        return HomeService(gson, networkFacade)
    }
    
    @Provides
    @Singleton
    fun provideNewsService(gson: Gson): NewsService {
        return NewsService(gson)
    }
    
    @Provides
    @Singleton
    fun provideUserService(
        gson: Gson,
        networkFacade: NetworkFacade
    ): UserService {
        return UserService(gson, networkFacade)
    }
    
    @Provides
    @Singleton
    fun provideAuthorService(retrofit: Retrofit): AuthorService {
        return AuthorService()
    }
    

    
    /**
     * 设置网络状态变化监听
     */
    private fun setupNetworkStateListener(
        networkMonitor: NetworkMonitor,
        priorityDispatcher: PriorityRequestDispatcher
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            networkMonitor.networkState.collect { networkState ->
                TimberLogger.d(TAG, "网络状态变化: $networkState")
                
                // 更新优先级分发器配置
                priorityDispatcher.updateConfiguration()
                
                // 这里可以添加其他网络状态变化的处理逻辑
                // 比如调整缓存策略、预取策略等
            }
        }
    }
}

/**
 * 网络优化统计管理器
 */
@Singleton
class NetworkOptimizationStats @javax.inject.Inject constructor(
    private val requestCoalescer: RequestCoalescer,
    private val priorityDispatcher: PriorityRequestDispatcher,
    private val coalescingInterceptor: RequestCoalescingInterceptor
) {
    
    /**
     * 获取综合统计信息
     */
    fun getOverallStats(): NetworkOptimizationReport {
        return NetworkOptimizationReport(
            coalescingStats = coalescingInterceptor.getStats(),
            priorityStats = priorityDispatcher.getStats(),
            timestamp = System.currentTimeMillis()
        )
    }
}

/**
 * 网络优化报告
 */
@Stable
data class NetworkOptimizationReport(
    val coalescingStats: CoalesceStats,
    val priorityStats: com.novel.utils.network.priority.PriorityStats,
    val timestamp: Long
)
