package com.novel.utils.network

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.interceptor.AuthInterceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException

/**
 * Retrofit 服务接口定义
 * 
 * 统一封装所有HTTP请求方法：
 * - 支持GET、POST、DELETE、PATCH、PUT请求
 * - 动态URL和参数支持
 * - 统一请求头管理
 */
interface ApiServiceInterface {
    /**
     * GET请求接口
     * @param endpoint 接口路径
     * @param params 查询参数（可选）
     * @param headers 请求头部信息
     */
    @GET
    fun get(
        @Url endpoint: String,
        @QueryMap params: Map<String, String>? = null,
        @HeaderMap headers: Map<String, String> = mapOf()
    ): Call<ResponseBody>

    /**
     * POST请求接口
     * @param endpoint 接口路径
     * @param body 请求体（JSON格式）
     * @param headers 请求头部信息
     */
    @POST
    fun post(
        @Url endpoint: String,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String> = mapOf()
    ): Call<ResponseBody>

    /**
     * DELETE请求接口
     * @param endpoint 接口路径
     * @param headers 请求头部信息
     */
    @DELETE
    fun delete(
        @Url endpoint: String,
        @HeaderMap headers: Map<String, String> = mapOf()
    ): Call<ResponseBody>

    /**
     * PATCH请求接口（表单格式）
     * @param endpoint 接口路径
     * @param params 表单参数（可选）
     * @param headers 请求头部信息
     */
    @FormUrlEncoded
    @PATCH
    fun patch(
        @Url endpoint: String,
        @FieldMap params: Map<String, String>? = null,
        @HeaderMap headers: Map<String, String> = mapOf()
    ): Call<ResponseBody>

    /**
     * PUT请求接口
     * @param endpoint 接口路径
     * @param body 请求体（JSON格式）
     * @param headers 请求头部信息
     */
    @PUT
    fun put(
        @Url endpoint: String,
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String> = mapOf()
    ): Call<ResponseBody>
}

/**
 * Retrofit客户端管理器
 * 
 * 职责：
 * - 创建和缓存Retrofit实例
 * - 统一OkHttp配置管理
 * - 自动认证拦截器集成
 * - 多BaseURL支持
 */
@Stable
object RetrofitClient {

    private const val TAG = "RetrofitClient"
    
    /** Retrofit实例缓存，避免重复创建 */
    private val retrofitInstances = mutableMapOf<String, Retrofit>()

    /** 认证拦截器，自动添加Token */
    private lateinit var authInterceptor: AuthInterceptor
    /** Token提供器 */
    private lateinit var tokenProvider: TokenProvider
    /** 配置好的Gson实例 */
    private lateinit var gson: com.google.gson.Gson

    /**
     * 初始化方法，在Application中调用
     * @param authInterceptor 认证拦截器实例
     * @param tokenProvider Token提供器实例
     * @param gson 配置好的Gson实例
     */
    fun init(
        authInterceptor: AuthInterceptor,
        tokenProvider: TokenProvider,
        gson: com.google.gson.Gson
    ) {
        TimberLogger.d(TAG, "初始化RetrofitClient")
        this.authInterceptor = authInterceptor
        this.tokenProvider = tokenProvider
        this.gson = gson
    }

    /**
     * 获取指定BaseURL的Retrofit实例
     * 使用缓存机制，相同BaseURL复用实例
     */
    private fun getRetrofit(baseUrl: String): Retrofit =
        retrofitInstances.getOrPut(baseUrl) {
            TimberLogger.d(TAG, "创建新的Retrofit实例: $baseUrl")
            // 组装OkHttpClient
            val builder = OkHttpClient.Builder()
                .addInterceptor(authInterceptor) // 自动加Token

            // 生成Retrofit实例
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

    /**
     * 获取API服务接口实例
     * @param baseUrl 基础URL
     * @return ApiServiceInterface实例
     */
    fun getService(baseUrl: String): ApiServiceInterface =
        getRetrofit(baseUrl).create(ApiServiceInterface::class.java)
}


/**
 * 网络请求服务类
 * 
 * 核心功能：
 * - 封装各种HTTP请求方法
 * - 统一请求回调处理
 * - 错误处理和日志记录
 * - 多服务端点支持
 * 
 * 设计特点：
 * - 单例模式，全局复用
 * - 异步回调机制
 * - 统一错误处理
 * - 完整的请求生命周期日志
 */
@Stable
object ApiService {
    /** 各个服务的基础URL配置 */
    const val BASE_URL_FRONT = "http://47.110.147.60:8080/api/front/"
    const val BASE_URL_USER = "http://47.110.147.60:8080/api/front/user/"
    const val BASE_URL_RESOURCE = "http://47.110.147.60:8080/api/front/resource/"
    const val BASE_URL_AUTHOR = "http://47.110.147.60:8080/api/author/"
    const val BASE_URL_AI = "http://47.110.147.60:8080/api/author/ai/"
    
    private const val TAG = "ApiService"

    /** 获取指定BaseURL的服务实例 */
    private fun getService(baseUrl: String) = RetrofitClient.getService(baseUrl)

    /**
     * GET请求方法
     * @param baseUrl 基础URL
     * @param endpoint 接口端点
     * @param params 查询参数
     * @param headers 请求头
     * @param callback 结果回调
     */
    fun get(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>? = mapOf(),
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        TimberLogger.d(TAG, "GET请求: $endpoint")
        getService(baseUrl).get(endpoint, params, headers)
            .enqueue(createCallback(callback))
    }

    /**
     * POST请求方法
     * @param baseUrl 基础URL
     * @param endpoint 接口端点
     * @param params 请求参数
     * @param headers 请求头
     * @param callback 结果回调
     */
    fun post(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String> = mapOf(),
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        TimberLogger.d(TAG, "POST请求: $endpoint")
        getService(baseUrl).post(endpoint, createJsonBody(params), headers)
            .enqueue(createCallback(callback))
    }

    /**
     * DELETE请求方法
     * @param baseUrl 基础URL
     * @param endpoint 接口端点
     * @param headers 请求头
     * @param callback 结果回调
     */
    fun delete(
        baseUrl: String,
        endpoint: String,
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        TimberLogger.d(TAG, "DELETE请求: $endpoint")
        getService(baseUrl).delete(endpoint, headers).enqueue(createCallback(callback))
    }

    /**
     * PATCH请求方法
     * @param baseUrl 基础URL
     * @param endpoint 接口端点
     * @param params 表单参数
     * @param headers 请求头
     * @param callback 结果回调
     */
    fun patch(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>? = mapOf(),
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        TimberLogger.d(TAG, "PATCH请求: $endpoint")
        getService(baseUrl).patch(endpoint, params, headers)
            .enqueue(createCallback(callback))
    }

    /**
     * PUT请求方法
     * @param baseUrl 基础URL
     * @param endpoint 接口端点
     * @param body 请求体
     * @param headers 请求头
     * @param callback 结果回调
     */
    fun put(
        baseUrl: String,
        endpoint: String,
        body: RequestBody,
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        TimberLogger.d(TAG, "PUT请求: $endpoint")
        getService(baseUrl).put(endpoint, body, headers).enqueue(createCallback(callback))
    }

    /**
     * 创建统一的请求回调处理器
     * 
     * 功能：
     * - 统一成功响应处理
     * - 统一错误处理和日志
     * - HTTP状态码检查
     * - 响应体解析异常处理
     */
    private fun createCallback(callback: (String?, Throwable?) -> Unit) =
        object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                try {
                    if (response.isSuccessful) {
                        val body = response.body()?.string()
                        TimberLogger.d(TAG, "请求成功: ${call.request().url}")
                        if (body.isNullOrEmpty()) {
                            TimberLogger.w(TAG, "响应为空: ${call.request().url}")
                        }
                        TimberLogger.d(TAG, "响应数据: $body")
                        callback(body, null)
                    } else {
                        val errorMsg = "HTTP错误: ${response.code()}"
                        TimberLogger.w(TAG, "$errorMsg - ${call.request().url}")
                        callback(null, IOException(errorMsg))
                    }
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "响应解析异常: ${call.request().url}", e)
                    callback(null, e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TimberLogger.e(TAG, "请求失败: ${call.request().url} - ${t.message}")
                callback(null, t)
            }
        }

    /**
     * 创建JSON格式的请求体
     * @param json JSON字符串
     * @return RequestBody实例
     */
    private fun createJsonRequestBody(json: String): RequestBody {
        return json.toRequestBody("application/json".toMediaTypeOrNull())
    }

    // 直接使用 JSON 格式的请求体，而不是使用 FormBody
    private fun createJsonBody(params: Map<String, String>): RequestBody {
        val json = params.entries.joinToString(",") { "\"${it.key}\":\"${it.value}\"" }
        val jsonBody = "{ $json }"
        return createJsonRequestBody(jsonBody)  // 创建 JSON 请求体
    }
}
