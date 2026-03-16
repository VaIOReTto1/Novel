package com.novel.utils.network.api.front.user

import androidx.compose.runtime.Stable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService.BASE_URL_USER
import com.novel.utils.network.ApiService.BASE_URL_FRONT
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户服务类
 * 
 * 核心功能：
 * - 用户认证：登录、注册、验证码处理
 * - 用户信息管理：查询、更新用户资料
 * - 书架操作：添加/移除书籍、状态查询
 * - 评论系统：发布、修改、删除、查询评论
 * - 用户反馈：意见建议提交
 * 
 * 技术特点：
 * - 单例模式设计，全局唯一实例
 * - 基于ApiService的统一网络调用
 * - 完善的异步回调和协程支持
 * - 统一的响应处理和错误处理
 * - 详细的操作日志记录
 * 
 * 数据结构：
 * - 登录注册相关DTO
 * - 用户信息相关DTO
 * - 分页响应和评论相关DTO
 * - 统一的基础响应格式
 */
@Stable
@Singleton
class UserService @Inject constructor(
    @Stable
    private val gson: Gson,
    private val networkFacade: NetworkFacade
) {
    
    // region 数据结构
    @Stable
    data class BaseResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: Any?,
        @SerializedName("ok") val ok: Boolean?
    )

    // 登录相关数据结构
    @Stable
    data class LoginRequest(
        @SerializedName("username") val username: String,
        @SerializedName("password") val password: String
    )

    @Stable
    data class LoginResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val msg: String?,
        @SerializedName("data") val data: LoginData?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class LoginData(
        @SerializedName("uid") val uid: Int,
        @SerializedName("nickName") val nickName: String,
        @SerializedName("token") val token: String,
    )

    // 注册相关数据结构
    @Stable
    data class RegisterRequest(
        @SerializedName("username") val username: String,
        @SerializedName("password") val password: String,
        @SerializedName("sessionId") val sessionId: String,
        @SerializedName("velCode") val velCode: String
    )

    @Stable
    data class RegisterResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val msg: String?,
        @SerializedName("data") val data: RegisterData?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class RegisterData(
        @SerializedName("uid") val uid: Int,
        @SerializedName("token") val token: String,
    )

    // 用户信息相关数据结构
    @Stable
    data class UserInfoResponse(
        @SerializedName("code") val code: String,
        @SerializedName("message") val msg: String,
        @SerializedName("data") val data: UserInfoData?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class UserInfoData(
        @SerializedName("nickName") val nickName: String,
        @SerializedName("userPhoto") val userPhoto: String?,
        @SerializedName("userSex") val userSex: Int?,
    )

    @Stable
    data class UserInfoUpdateRequest(
        @SerializedName("userId") val userId: Long?,
        @SerializedName("nickName") val nickName: String?,
        @SerializedName("userPhoto") val userPhoto: String?,
        @SerializedName("userSex") val userSex: Int?
    )

    data class CommentRequest(
        @SerializedName("userId") val userId: Long?,
        @SerializedName("bookId") val bookId: Long,
        @SerializedName("commentContent") val commentContent: String
    )

    data class UserCommentsResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: PageResponse<UserComment>?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class PageResponse<T>(
        @SerializedName("pageNum") val pageNum: Long,
        @SerializedName("pageSize") val pageSize: Long,
        @SerializedName("total") val total: Long,
        @SerializedName("list") val list: ImmutableList<T>,
        @SerializedName("pages") val pages: Long
    )

    data class UserComment(
        @SerializedName("commentContent") val commentContent: String,
        @SerializedName("commentBookPic") val commentBookPic: String,
        @SerializedName("commentBook") val commentBook: String,
        @SerializedName("commentTime") val commentTime: String
    )

    data class BookshelfStatusResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: Int?,
        @SerializedName("ok") val ok: Boolean?
    )

    data class PageRequest(
        val pageNum: Int = 1,
        val pageSize: Int = 10,
        val fetchAll: Boolean = false
    )
    // endregion

    // region 登录注册相关方法
    
    /**
     * 用户登录接口
     */
    fun login(
        request: LoginRequest,
        callback: (LoginResponse?, Throwable?) -> Unit
    ) {
        val requestBody = mapOf(
            "username" to request.username,
            "password" to request.password
        )
        TimberLogger.d("UserService", "开始 login()，参数：$request")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse(
                    request = NetworkRequest(
                        baseUrl = BASE_URL_USER,
                        endpoint = "login",
                        method = NetworkRequestMethod.POST,
                        bodyParams = requestBody,
                        headers = mapOf(
                            "Content-Type" to "application/json",
                            "Accept" to "*/*"
                        )
                    ),
                    clazz = LoginResponse::class.java
                )
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    /**
     * 用户注册接口
     */
    fun register(
        request: RegisterRequest,
        callback: (RegisterResponse?, Throwable?) -> Unit
    ) {
        val requestBody = mutableMapOf<String, String>().apply {
            put("username", request.username)
            put("password", request.password)
            put("sessionId", request.sessionId)
            put("velCode", request.velCode)
        }

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse(
                    request = NetworkRequest(
                        baseUrl = BASE_URL_USER,
                        endpoint = "register",
                        method = NetworkRequestMethod.POST,
                        bodyParams = requestBody,
                        headers = mapOf(
                            "Content-Type" to "application/json",
                            "Accept" to "*/*"
                        )
                    ),
                    clazz = RegisterResponse::class.java
                )
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    /**
     * 用户信息查询接口
     */
    fun getUserInfo(
        callback: (UserInfoResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始请求用户信息")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse(
                    request = NetworkRequest(
                        baseUrl = BASE_URL_FRONT,
                        endpoint = "user",
                        method = NetworkRequestMethod.GET,
                        headers = mapOf("Accept" to "application/json")
                    ),
                    clazz = UserInfoResponse::class.java
                )
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    // endregion

    // region 协程版本
    suspend fun loginBlocking(request: LoginRequest): LoginResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_USER,
                endpoint = "login",
                method = NetworkRequestMethod.POST,
                bodyParams = mapOf(
                    "username" to request.username,
                    "password" to request.password
                ),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            ),
            clazz = LoginResponse::class.java
        )
    }

    suspend fun registerBlocking(request: RegisterRequest): RegisterResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_USER,
                endpoint = "register",
                method = NetworkRequestMethod.POST,
                bodyParams = mapOf(
                    "username" to request.username,
                    "password" to request.password,
                    "sessionId" to request.sessionId,
                    "velCode" to request.velCode
                ),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            ),
            clazz = RegisterResponse::class.java
        )
    }

    suspend fun getUserInfoBlocking(): UserInfoResponse? = runCatching {
        requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_FRONT,
                endpoint = "user",
                method = NetworkRequestMethod.GET,
                headers = mapOf("Accept" to "application/json")
            ),
            clazz = UserInfoResponse::class.java
        )
    }.getOrElse { error ->
        TimberLogger.w("UserService", error.toString())
        null
    }

    suspend fun updateUserInfoBlocking(request: UserInfoUpdateRequest): BaseResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_USER,
                endpoint = "",
                method = NetworkRequestMethod.PUT,
                bodyParams = buildUserInfoUpdateParams(request),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            ),
            clazz = BaseResponse::class.java
        )
    }

    suspend fun postCommentBlocking(request: CommentRequest): BaseResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_USER,
                endpoint = "comment",
                method = NetworkRequestMethod.POST,
                bodyParams = mapOf(
                    "bookId" to request.bookId.toString(),
                    "commentContent" to request.commentContent
                ),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            ),
            clazz = BaseResponse::class.java
        )
    }

    suspend fun getUserCommentsBlocking(pageRequest: PageRequest): UserCommentsResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_USER,
                endpoint = "comments",
                method = NetworkRequestMethod.GET,
                queryParams = mapOf(
                    "pageNum" to pageRequest.pageNum.toString(),
                    "pageSize" to pageRequest.pageSize.toString(),
                    "fetchAll" to pageRequest.fetchAll.toString()
                ),
                headers = mapOf("Accept" to "*/*")
            ),
            clazz = UserCommentsResponse::class.java
        )
    }

    suspend fun getBookshelfStatusBlocking(bookId: String): BookshelfStatusResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_USER,
                endpoint = "bookshelf_status",
                method = NetworkRequestMethod.GET,
                queryParams = mapOf("bookId" to bookId),
                headers = mapOf("Accept" to "*/*")
            ),
            clazz = BookshelfStatusResponse::class.java
        )
    }
    // endregion

    private suspend fun <T> requestAndParse(
        request: NetworkRequest,
        clazz: Class<T>
    ): T {
        val response = networkFacade.execute(request)
        return gson.fromJson(response, clazz)
    }

    private fun buildUserInfoUpdateParams(request: UserInfoUpdateRequest): Map<String, String> =
        buildMap {
            request.userId?.let { put("userId", it.toString()) }
            request.nickName?.let { put("nickName", it) }
            request.userPhoto?.let { put("userPhoto", it) }
            request.userSex?.let { put("userSex", it.toString()) }
        }
    // endregion
}
