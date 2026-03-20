package com.novel.utils.network.api.front.resource

import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService
import com.novel.utils.network.ApiService.BASE_URL_RESOURCE
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceService @Inject constructor() {
    
    // region 数据结构
    data class ImageUploadResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: String?, // 返回图片URL
        @SerializedName("ok") val ok: Boolean?
    )

    // 图片验证码相关数据结构
    data class ImageVerifyCodeResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: VerifyCodeData?,
        @SerializedName("ok") val ok: Boolean?
    )

    data class VerifyCodeData(
        @SerializedName("sessionId") val sessionId: String,
        @SerializedName("img") val imgBase64: String
    )
    // endregion

    // region 图片验证码相关方法

    /**
     * 获取图片验证码接口
     */
    private fun getImageVerifyCode(
        callback: (ImageVerifyCodeResponse?, Throwable?) -> Unit
    ) {
        ApiService.get(
            baseUrl = BASE_URL_RESOURCE,
            endpoint = "img_verify_code",
            params = mapOf(),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, ImageVerifyCodeResponse::class.java, callback)
        }
    }

    // endregion

    // region 网络请求方法
    
    /**
     * 图片上传接口
     */
    private fun uploadImage(
        imageFile: File,
        callback: (ImageUploadResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("ResourceService", "开始 uploadImage()，文件：${imageFile.name}")
        
        try {
            // 创建 multipart 请求体
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            
            // 由于现有的 ApiService 不支持 multipart，我们需要使用原始的 JSON 方式
            // 这里我们将文件转换为 base64 或者使用其他方式
            val params = mapOf(
                "file" to imageFile.absolutePath // 这里可能需要根据实际情况调整
            )
            
            ApiService.post(
                baseUrl = BASE_URL_RESOURCE,
                endpoint = "image",
                params = params,
                headers = mapOf(
                    "Content-Type" to "multipart/form-data",
                    "Accept" to "*/*"
                )
            ) { response, error ->
                handleResponse(response, error, ImageUploadResponse::class.java, callback)
            }
        } catch (e: Exception) {
            TimberLogger.e("ResourceService", "上传图片失败", e)
            callback(null, e)
        }
    }

    /**
     * 上传图片（使用字节数组）
     */
    private fun uploadImageBytes(
        imageBytes: ByteArray,
        fileName: String,
        callback: (ImageUploadResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("ResourceService", "开始 uploadImageBytes()，文件名：$fileName")
        
        try {
            // 将字节数组转换为临时文件
            val tempFile = File.createTempFile("upload_", fileName)
            tempFile.writeBytes(imageBytes)
            
            uploadImage(tempFile) { response, error ->
                // 清理临时文件
                tempFile.delete()
                callback(response, error)
            }
        } catch (e: Exception) {
            TimberLogger.e("ResourceService", "上传图片字节失败", e)
            callback(null, e)
        }
    }

    /**
     * 上传图片（使用 Base64 编码）
     */
    private fun uploadImageBase64(
        base64Image: String,
        fileName: String,
        callback: (ImageUploadResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("ResourceService", "开始 uploadImageBase64()，文件名：$fileName")
        
        val params = mapOf(
            "file" to base64Image,
            "fileName" to fileName
        )
        
        ApiService.post(
            baseUrl = BASE_URL_RESOURCE,
            endpoint = "image",
            params = params,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, ImageUploadResponse::class.java, callback)
        }
    }

    // endregion

    // region 协程版本
    suspend fun getImageVerifyCodeBlocking(): ImageVerifyCodeResponse {
        return suspendCancellableCoroutine { cont ->
            getImageVerifyCode { resp, err ->
                when {
                    err != null -> cont.resumeWith(Result.failure(err))
                    resp != null -> cont.resumeWith(Result.success(resp))
                    else -> cont.resumeWith(Result.failure(Exception("响应为空")))
                }
            }
        }
    }

    suspend fun uploadImageBlocking(imageFile: File): ImageUploadResponse {
        return suspendCancellableCoroutine { cont ->
            uploadImage(imageFile) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun uploadImageBytesBlocking(imageBytes: ByteArray, fileName: String): ImageUploadResponse {
        return suspendCancellableCoroutine { cont ->
            uploadImageBytes(imageBytes, fileName) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun uploadImageBase64Blocking(base64Image: String, fileName: String): ImageUploadResponse {
        return suspendCancellableCoroutine { cont ->
            uploadImageBase64(base64Image, fileName) { response, error ->
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
                    callback(Gson().fromJson(response, clazz), null)
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
