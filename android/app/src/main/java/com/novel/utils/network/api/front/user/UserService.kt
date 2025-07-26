package com.novel.utils.network.api.front.user

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService
import com.novel.utils.network.ApiService.BASE_URL_USER
import com.novel.utils.network.ApiService.BASE_URL_FRONT
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
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
    private val gson: Gson
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

        ApiService.post(
            baseUrl = BASE_URL_USER,
            endpoint = "login",
            params = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            TimberLogger.d("UserService", "login 回调，response=$response, error=$error")
            handleResponse(response, error, LoginResponse::class.java, callback)
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

        ApiService.post(
            baseUrl = BASE_URL_USER,
            endpoint = "register",
            params = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, RegisterResponse::class.java, callback)
        }
    }

    /**
     * 用户信息查询接口
     */
    fun getUserInfo(
        callback: (UserInfoResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始请求用户信息")
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            params = mapOf(),
            endpoint = "user",
            headers = mapOf("Accept" to "application/json")
        ) { response, error ->
            handleResponse(response, error, UserInfoResponse::class.java, callback)
        }
    }

    // endregion

    // region 网络请求方法
    
    /**
     * 用户信息修改接口
     */
    private fun updateUserInfo(
        request: UserInfoUpdateRequest,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 updateUserInfo()，参数：$request")
        
        val json = gson.toJson(request)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        
        ApiService.put(
            baseUrl = BASE_URL_USER,
            endpoint = "",
            body = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 发表评论接口
     */
    private fun postComment(
        request: CommentRequest,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 postComment()，参数：$request")
        
        val requestBody = mapOf(
            "bookId" to request.bookId.toString(),
            "commentContent" to request.commentContent
        )
        
        ApiService.post(
            baseUrl = BASE_URL_USER,
            endpoint = "comment",
            params = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 修改评论接口
     */
    private fun updateComment(
        commentId: Long,
        content: String,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 updateComment()，参数：commentId=$commentId, content=$content")
        
        ApiService.put(
            baseUrl = BASE_URL_USER,
            endpoint = "comment/$commentId?content=$content",
            body = "".toRequestBody("application/json".toMediaTypeOrNull()),
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 删除评论接口
     */
    private fun deleteComment(
        commentId: Long,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 deleteComment()，参数：$commentId")
        
        ApiService.delete(
            baseUrl = BASE_URL_USER,
            endpoint = "comment/$commentId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 查询会员评论列表接口
     */
    private fun getUserComments(
        pageRequest: PageRequest,
        callback: (UserCommentsResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 getUserComments()，参数：$pageRequest")
        
        val params = mapOf(
            "pageNum" to pageRequest.pageNum.toString(),
            "pageSize" to pageRequest.pageSize.toString(),
            "fetchAll" to pageRequest.fetchAll.toString()
        )
        
        ApiService.get(
            baseUrl = BASE_URL_USER,
            endpoint = "comments",
            params = params,
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, UserCommentsResponse::class.java, callback)
        }
    }

    /**
     * 用户反馈提交接口
     */
    private fun submitFeedback(
        feedback: String,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 submitFeedback()，参数：$feedback")
        
        ApiService.post(
            baseUrl = BASE_URL_USER,
            endpoint = "feedback",
            params = mapOf("feedback" to feedback),
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 用户反馈删除接口
     */
    private fun deleteFeedback(
        feedbackId: Long,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 deleteFeedback()，参数：$feedbackId")
        
        ApiService.delete(
            baseUrl = BASE_URL_USER,
            endpoint = "feedback/$feedbackId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 查询书架状态接口
     */
    private fun getBookshelfStatus(
        bookId: String,
        callback: (BookshelfStatusResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("UserService", "开始 getBookshelfStatus()，参数：$bookId")
        
        ApiService.get(
            baseUrl = BASE_URL_USER,
            endpoint = "bookshelf_status",
            params = mapOf("bookId" to bookId),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookshelfStatusResponse::class.java, callback)
        }
    }

    // endregion

    // region 协程版本
    suspend fun loginBlocking(request: LoginRequest): LoginResponse {
        return suspendCancellableCoroutine { cont ->
            login(request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun registerBlocking(request: RegisterRequest): RegisterResponse {
        return suspendCancellableCoroutine { cont ->
            register(request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    if (response == null) {
                        cont.resumeWith(Result.failure(Exception("Unknown error")))
                    } else {
                        cont.resumeWith(Result.success(response))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getUserInfoBlocking(): UserInfoResponse? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            TimberLogger.d("UserService", "开始请求用户信息")
            getUserInfo { response, error ->
                if (error != null) {
                    TimberLogger.w("UserService", error.toString())
                    cont.resume(null, onCancellation = null)
                } else if (response != null) {
                    cont.resume(response, onCancellation = null)
                } else {
                    TimberLogger.w("UserService", "收到空响应")
                    cont.resume(null, onCancellation = null)
                }
            }
        }
    }

    suspend fun updateUserInfoBlocking(request: UserInfoUpdateRequest): BaseResponse {
        return suspendCancellableCoroutine { cont ->
            updateUserInfo(request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun postCommentBlocking(request: CommentRequest): BaseResponse {
        return suspendCancellableCoroutine { cont ->
            postComment(request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getUserCommentsBlocking(pageRequest: PageRequest): UserCommentsResponse {
        return suspendCancellableCoroutine { cont ->
            getUserComments(pageRequest) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getBookshelfStatusBlocking(bookId: String): BookshelfStatusResponse {
        return suspendCancellableCoroutine { cont ->
            getBookshelfStatus(bookId) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }
    // endregion

    // region 响应处理
    private fun <T> handleResponse(
        response: String?,
        error: Throwable?,
        clazz: Class<T>,
        callback: (T?, Throwable?) -> Unit
    ) {
        when {
            error != null -> {
                callback(null, error)
            }
            response != null -> {
                try {
                    callback(gson.fromJson(response, clazz), null)
                } catch (e: Exception) {
                    callback(null, e)
                }
            }
            else -> {
                callback(null, Exception("Response is null"))
            }
        }
    }
    // endregion
}