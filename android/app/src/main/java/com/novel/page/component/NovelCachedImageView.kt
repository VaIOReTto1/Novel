package com.novel.page.component

import android.annotation.SuppressLint
import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.novel.BuildConfig
import com.novel.utils.TimberLogger
import androidx.collection.LruCache
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.novel.utils.Store.NovelKeyChain.NovelKeyChainType
import com.novel.utils.Store.NovelKeyChain.NovelKeyChain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import javax.inject.Qualifier

/**
 * Token 提供者接口
 * 抽象Token获取逻辑，便于单元测试和依赖注入
 */
interface TokenProvider {
    fun getToken(): String?
}

/**
 * 基于KeyChain的Token提供者实现
 * 从安全存储中读取访问令牌
 */
@Singleton
class KeyChainTokenProvider @Inject constructor(
    private val novelKeyChain: NovelKeyChain
) : TokenProvider {
    override fun getToken(): String? =
        novelKeyChain.read(NovelKeyChainType.TOKEN)
}

/**
 * 内存缓存接口
 * 定义图片内存缓存的基本操作
 */
interface MemoryCache {
    fun get(key: String): Bitmap?
    fun put(key: String, bitmap: Bitmap)
}

/**
 * 增强的LRU内存缓存实现
 * 
 * 新增特性：
 * - 集成Bitmap复用池减少内存分配
 * - 内存压力自适应清理
 * - 详细的统计信息和监控
 * - 支持配置化的缓存大小
 */
@Singleton
@Stable
class LruMemoryCache @Inject constructor(
    private val config: ImageOptimizationConfig,
    private val bitmapPool: BitmapPool?,
    private val memoryPressureManager: MemoryPressureManager?
): MemoryCache, MemoryPressureCallback {
    
    companion object {
        private const val TAG = "LruMemoryCache"
    }
    
    @Stable
    private val cache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(
        config.memoryCacheSizeMB * 1024 * 1024
    ) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.allocationByteCount
        }
        
        override fun entryRemoved(evicted: Boolean, key: String, oldValue: Bitmap, newValue: Bitmap?) {
            // 将被移除的Bitmap放入复用池
            if (evicted && config.enableBitmapPool && !oldValue.isRecycled) {
                bitmapPool?.put(oldValue)
                TimberLogger.v(TAG, "缓存淘汰的Bitmap已放入复用池: ${oldValue.width}x${oldValue.height}")
            }
        }
    }
    
    // 统计信息
    @Stable
    private val hitCount = java.util.concurrent.atomic.AtomicInteger(0)
    @Stable
    private val missCount = java.util.concurrent.atomic.AtomicInteger(0)
    
    init {
        // 注册内存压力回调
        if (config.enableMemoryPressureHandling) {
            memoryPressureManager?.registerCallback(this)
        }
        TimberLogger.d(TAG, "增强LRU内存缓存初始化完成，大小: ${config.memoryCacheSizeMB}MB")
    }
    
    override fun get(key: String): Bitmap? {
        val bitmap = cache[key]
        if (bitmap != null) {
            hitCount.incrementAndGet()
            TimberLogger.v(TAG, "缓存命中: $key")
        } else {
            missCount.incrementAndGet()
            TimberLogger.v(TAG, "缓存未命中: $key")
        }
        return bitmap
    }
    
    override fun put(key: String, bitmap: Bitmap) {
        if (bitmap.isRecycled) {
            TimberLogger.w(TAG, "尝试缓存已回收的Bitmap: $key")
            return
        }
        
        cache.put(key, bitmap)
        TimberLogger.v(TAG, "Bitmap已缓存: $key, 大小: ${bitmap.allocationByteCount}字节")
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getStats(): MemoryCacheStats {
        return MemoryCacheStats(
            size = cache.size(),
            maxSize = cache.maxSize(),
            hitCount = hitCount.get(),
            missCount = missCount.get(),
            hitRate = if (hitCount.get() + missCount.get() > 0) {
                hitCount.get().toFloat() / (hitCount.get() + missCount.get())
            } else 0f,
            evictionCount = cache.evictionCount()
        )
    }
    
    /**
     * 手动清理缓存
     */
    fun trimToSize(maxSize: Int) {
        cache.trimToSize(maxSize)
        TimberLogger.d(TAG, "缓存已清理到指定大小: ${maxSize}字节")
    }
    
    /**
     * 清空所有缓存
     */
    fun clear() {
        cache.evictAll()
        TimberLogger.d(TAG, "缓存已清空")
    }
    
    // ========== MemoryPressureCallback 实现 ==========
    
    override fun onMemoryPressure(level: Int) {
        val trimRatio = when (level) {
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> 0.8f // 清理80%
            ComponentCallbacks2.TRIM_MEMORY_MODERATE -> 0.5f // 清理50%
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> 0.3f // 清理30%
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> 0.7f
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> 0.4f
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> 0.2f
            else -> 0f
        }
        
        if (trimRatio > 0f) {
            val targetSize = (cache.maxSize() * (1f - trimRatio)).toInt()
            trimToSize(targetSize)
            TimberLogger.i(TAG, "内存压力清理完成，清理比例: ${(trimRatio * 100).toInt()}%")
        }
    }
    
    override fun onLowMemory() {
        // 低内存时清理大部分缓存
        val targetSize = cache.maxSize() / 4 // 保留25%
        trimToSize(targetSize)
        TimberLogger.w(TAG, "低内存警告，缓存已大幅清理")
    }
    
    override fun getPriority(): Int = 20 // 较高优先级，早点清理图片缓存
}

/**
 * 图片加载服务接口
 * 定义异步图片加载的契约
 */
interface ImageLoaderService {
    suspend fun loadImage(url: String, needToken: Boolean): Bitmap?
}

/**
 * HTTP图片加载服务实现
 * 
 * 采用三级缓存策略：内存缓存 -> OkHttp磁盘缓存 -> 网络请求
 * 提供高性能的图片加载体验
 */
@Singleton
@Stable
class HttpImageLoaderService @Inject constructor(
    @Stable
    private val client: OkHttpClient,
    @Stable
    private val memoryCache: MemoryCache,
    @Stable
    private val tokenProvider: TokenProvider
) : ImageLoaderService {
    
    companion object {
        private const val BASE_URL = BuildConfig.API_BASE_HOST
        private const val TAG = "HttpImageLoaderService"
    }
    
    override suspend fun loadImage(url: String, needToken: Boolean): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                // 1. 内存缓存命中检查
                memoryCache.get(url)?.let { 
                    TimberLogger.v(TAG, "内存缓存命中: $url")
                    return@withContext it 
                }
                
                // 2. URL处理 - 相对路径转绝对路径
                val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                    url
                } else {
                    "$BASE_URL$url"
                }
                
                TimberLogger.d(TAG, "开始加载图片: $fullUrl")
                
                // 3. 构建HTTP请求
                val reqBuilder = Request.Builder()
                    .url(fullUrl)
                    .get()
                if (needToken) tokenProvider.getToken()?.let {
                    reqBuilder.header("Authorization", it)
                }
                
                // 4. 执行网络请求（自动利用OkHttp磁盘缓存）
                client.newCall(reqBuilder.build()).execute().use { resp ->
                    if (!resp.isSuccessful) {
                        TimberLogger.w(TAG, "网络请求失败: $fullUrl, 状态码: ${resp.code}")
                        return@withContext null
                    }
                    
                    resp.body?.byteStream()?.let { stream ->
                        BitmapFactory.decodeStream(stream)?.also { bitmap ->
                            // 5. 写入内存缓存
                            memoryCache.put(url, bitmap)
                            TimberLogger.d(TAG, "图片加载成功并缓存: $url")
                        }
                    }
                }
            } catch (e: Exception) {
                // 记录错误但不抛出异常，返回null让UI显示占位符
                TimberLogger.e(TAG, "加载图片失败: $url", e)
                null
            }
        }
}

/**
 * 内存缓存统计信息
 */
@Stable
data class MemoryCacheStats(
    val size: Int,
    val maxSize: Int,
    val hitCount: Int,
    val missCount: Int,
    val hitRate: Float,
    val evictionCount: Int
) {
    override fun toString(): String {
        return "MemoryCacheStats(size: $size/${maxSize/1024/1024}MB, " +
               "hit: $hitCount, miss: $missCount, hitRate: ${(hitRate * 100).toInt()}%, " +
               "evicted: $evictionCount)"
    }
}

/**
 * Qualifier for image-specific cache
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageCache

/**
 * Hilt图片优化依赖提供模块
 * 配置图片加载所需的网络和优化组件
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {
    @Provides @Singleton @ImageCache
    fun provideOkHttpCache(@ApplicationContext ctx: Context, config: ImageOptimizationConfig): Cache =
        Cache(ctx.cacheDir.resolve("http_cache"), config.diskCacheSizeMB * 1024 * 1024)

    @Provides @Singleton
    fun provideOkHttpClient(@ImageCache cache: Cache): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .build()
            
    @Provides @Singleton 
    fun provideImageOptimizationConfig(@ApplicationContext ctx: Context): ImageOptimizationConfig {
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        val totalMemoryMB = memInfo.totalMem / (1024 * 1024)
        return ImageOptimizationConfig.forMemoryCapacity(totalMemoryMB)
    }
    
    @Provides @Singleton
    fun provideBitmapPool(config: ImageOptimizationConfig): BitmapPool? {
        return if (config.enableBitmapPool) {
            LruBitmapPool() // 使用默认配置
        } else null
    }

    @Provides @Singleton
    fun provideMemoryCache(
        config: ImageOptimizationConfig,
        bitmapPool: BitmapPool?,
        memoryPressureManager: MemoryPressureManager?
    ): MemoryCache = LruMemoryCache(config, bitmapPool, memoryPressureManager)

    @Provides @Singleton
    fun provideTokenProvider(keyChainTokenProvider: KeyChainTokenProvider): TokenProvider = keyChainTokenProvider

    @Provides @Singleton
    fun provideImageCompressor(
        bitmapPool: BitmapPool?,
        config: ImageOptimizationConfig
    ): ImageCompressor = ImageCompressor(bitmapPool, config)
    
    @Provides @Singleton
    fun provideImageLoaderService(
        client: OkHttpClient,
        memCache: MemoryCache,
        tokenProv: TokenProvider
    ): ImageLoaderService = HttpImageLoaderService(client, memCache, tokenProv)
    
    @Provides @Singleton
    fun provideProgressiveImageLoader(
        imageLoader: ImageLoaderService,
        imageCompressor: ImageCompressor,
        config: ImageOptimizationConfig
    ): ProgressiveImageLoader = ProgressiveImageLoader(imageLoader, imageCompressor, config)
    
    @Provides @Singleton
    fun provideImagePreloadManager(
        imageLoader: ImageLoaderService,
        memoryPressureManager: MemoryPressureManager,
        config: ImageOptimizationConfig
    ): ImagePreloadManager = ImagePreloadManager(imageLoader, memoryPressureManager, config)
}

/**
 * 图片加载状态
 * 封装加载过程中的不同状态
 */
sealed class LoadState {
    @Stable
    data object Loading : LoadState()
    @Stable
    data object Error   : LoadState()
    @Stable
    data class Success(@Stable val image: ImageBitmap) : LoadState()
}

/**
 * CompositionLocal图片加载器
 * 简化在Composable中获取ImageLoaderService的方式
 */
val LocalImageLoaderService = staticCompositionLocalOf<ImageLoaderService> {
    error("请在CompositionLocalProvider中提供ImageLoaderService")
}

/**
 * 多级缓存图片加载组件
 * 
 * 提供高性能的图片加载体验，支持内存缓存、磁盘缓存和网络加载
 * 自动处理加载状态和错误情况
 *
 * @param url 图片URL，支持相对路径和绝对路径
 * @param needToken 是否需要在请求头中附带访问令牌
 * @param modifier 外部传入的修饰符
 * @param content 加载成功后展示ImageBitmap的Composable插槽
 * @param placeholder 加载中或失败时展示的Composable插槽
 * @param imageLoader 图片加载服务，默认从CompositionLocal获取
 */
@Composable
fun NovelCachedImageView(
    url: String?,
    needToken: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    content: @Composable (ImageBitmap) -> Unit,
    placeholder: @Composable () -> Unit,
    imageLoader: ImageLoaderService = LocalImageLoaderService.current
) {
    val state by produceState<LoadState>(initialValue = LoadState.Loading, url, needToken) {
        val bmp = url?.let { imageLoader.loadImage(it, needToken) }
        value = if (bmp != null) LoadState.Success(bmp.asImageBitmap()) else LoadState.Error
    }

    Box(modifier) {
        when (state) {
            is LoadState.Success -> {
                TimberLogger.v("NovelCachedImageView", "渲染图片成功: $url")
                content((state as LoadState.Success).image)
            }
            LoadState.Loading -> {
                TimberLogger.v("NovelCachedImageView", "图片加载中: $url")
                placeholder()
            }
            LoadState.Error -> {
                TimberLogger.w("NovelCachedImageView", "图片加载失败，显示占位符: $url")
                placeholder()
            }
        }
    }
}
