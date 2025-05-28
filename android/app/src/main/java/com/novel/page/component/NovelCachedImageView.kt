package com.novel.page.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

/**
 * 抽象 Token 提供者，方便 Mock 测试
 */
interface TokenProvider {
    fun getToken(): String?
}

/** 从 KeyChain 读取 Token 的默认实现 */
@Singleton
class KeyChainTokenProvider @Inject constructor(
    private val novelKeyChain: NovelKeyChain
) : TokenProvider {
    override fun getToken(): String? =
        novelKeyChain.read(NovelKeyChainType.TOKEN)
}

/**
 * 内存缓存抽象
 */
interface MemoryCache {
    fun get(key: String): Bitmap?
    fun put(key: String, bitmap: Bitmap)
}

/** 基于 LruCache 的内存缓存实现 */
@Singleton
class LruMemoryCache @Inject constructor(): MemoryCache {
    private val cache = LruCache<String, Bitmap>(20 * 1024 * 1024)
    override fun get(key: String) = cache[key]
    override fun put(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }
}

/**
 * 图片加载服务抽象
 */
interface ImageLoaderService {
    suspend fun loadImage(url: String, needToken: Boolean): Bitmap?
}

/**
 * HTTP + 多级缓存实现：内存缓存 -> OkHttp 硬盘缓存 -> 网络请求
 */
@Singleton
class HttpImageLoaderService @Inject constructor(
    private val client: OkHttpClient,
    private val memoryCache: MemoryCache,
    private val tokenProvider: TokenProvider
) : ImageLoaderService {
    
    companion object {
        private const val BASE_URL = "http://47.110.147.60:8080" // 基础URL
    }
    
    override suspend fun loadImage(url: String, needToken: Boolean): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                // 1. 内存缓存
                memoryCache.get(url)?.let { return@withContext it }
                
                // 2. 处理URL - 如果是相对路径，添加基础URL
                val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                    url
                } else {
                    "$BASE_URL$url"
                }
                
                // 3. 构建请求
                val reqBuilder = Request.Builder()
                    .url(fullUrl)
                    .get()
                if (needToken) tokenProvider.getToken()?.let {
                    reqBuilder.header("Authorization", "$it")
                }
                
                // 4. 执行请求（OkHttp 自动硬盘缓存）
                client.newCall(reqBuilder.build()).execute().use { resp ->
                    if (!resp.isSuccessful) return@withContext null
                    resp.body?.byteStream()?.let { stream ->
                        BitmapFactory.decodeStream(stream)?.also {
                            // 5. 写入内存缓存
                            memoryCache.put(url, it)
                        }
                    }
                }
            } catch (e: Exception) {
                // 记录错误但不抛出异常，返回null让UI显示占位符
                android.util.Log.w("ImageLoader", "加载图片失败: $url", e)
                null
            }
        }
}

/**
 * Hilt 网络依赖提供
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {
    @Provides @Singleton
    fun provideOkHttpCache(@ApplicationContext ctx: Context): Cache =
        Cache(ctx.cacheDir.resolve("http_cache"), 10L * 1024 * 1024)

    @Provides @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient =
        OkHttpClient.Builder()
            .cache(cache)
            .build()

    @Provides @Singleton
    fun provideMemoryCache(): MemoryCache = LruMemoryCache()

    @Provides @Singleton
    fun provideTokenProvider(keyChainTokenProvider: KeyChainTokenProvider): TokenProvider = keyChainTokenProvider

    @Provides @Singleton
    fun provideImageLoaderService(
        client: OkHttpClient,
        memCache: MemoryCache,
        tokenProv: TokenProvider
    ): ImageLoaderService = HttpImageLoaderService(client, memCache, tokenProv)
}

/**
 * 加载状态：Loading / Success / Error
 */
sealed class LoadState {
    data object Loading : LoadState()
    data object Error   : LoadState()
    data class Success(val image: ImageBitmap) : LoadState()
}

/** CompositionLocal，简化在 Composable 中获取 ImageLoaderService */
val LocalImageLoaderService = staticCompositionLocalOf<ImageLoaderService> {
    error("请在 CompositionLocalProvider 中提供 ImageLoaderService")
}

/**
 * Compose 组件：多级缓存图片加载
 *
 * @param url 图片地址
 * @param needToken 是否需要附带 Token
 * @param modifier 外部传入 Modifier
 * @param content 加载成功后展示 ImageBitmap 的 Composable 插槽
 * @param placeholder 加载中或失败时展示的 Composable 插槽
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
            is LoadState.Success -> content((state as LoadState.Success).image)
            else                -> placeholder()
        }
    }
}