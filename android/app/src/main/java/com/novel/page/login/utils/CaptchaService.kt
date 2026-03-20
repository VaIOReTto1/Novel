package com.novel.page.login.utils

import android.content.Context
import android.util.Base64
import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.api.front.resource.ResourceService
import com.novel.utils.network.api.getImageVerifyCodeHighPriority
import com.novel.utils.security.SecurityConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 验证码服务
 * 
 * 专门处理验证码的获取、缓存、清理和安全管理
 * 提供验证码图片的本地存储和状态管理功能
 * 确保验证码文件的安全性和及时清理
 */
@Singleton
@Stable
class CaptchaService @Inject constructor(
    @Stable private val resourceService: ResourceService,
    @Stable private val securityConfig: SecurityConfig,
    @ApplicationContext @Stable val context: Context
) {
    
    companion object {
        private const val TAG = "CaptchaService"
    }
    
    // 验证码状态管理
    @Stable
    val _captchaState = MutableStateFlow(CaptchaState())
    @Stable
    val captchaState: StateFlow<CaptchaState> = _captchaState.asStateFlow()
    
    /**
     * 加载验证码图片
     * 
     * 从服务器获取验证码图片并缓存到本地
     * 
     * @return 是否加载成功
     */
    suspend fun loadCaptcha(): Boolean = withContext(Dispatchers.IO) {
        TimberLogger.d(TAG, "开始加载验证码")
        
        // 设置加载状态
        _captchaState.value = _captchaState.value.copy(
            isLoading = true,
            error = null
        )
        
        runCatching {
            // 使用高优先级请求获取验证码
            val response = try {
                resourceService.getImageVerifyCodeHighPriority()
            } catch (e: Exception) {
                TimberLogger.w(TAG, "高优先级验证码请求失败，使用普通请求: ${e.message}")
                resourceService.getImageVerifyCodeBlocking()
            }
            
            response.data?.let { data ->
                // 验证数据安全性
                if (!isValidCaptchaData(data.imgBase64, data.sessionId)) {
                    throw SecurityException("验证码数据不安全")
                }
                
                // 解码Base64图片数据
                val imageBytes = Base64.decode(data.imgBase64, Base64.DEFAULT)
                
                // 验证文件大小
                if (!isFileSizeValid(imageBytes)) {
                    throw SecurityException("验证码文件大小超限")
                }
                
                // 创建临时文件
                val tempFile = createTempFile(data.sessionId, imageBytes)
                
                // 更新状态
                _captchaState.value = CaptchaState(
                    imagePath = tempFile.absolutePath,
                    sessionId = data.sessionId,
                    isLoading = false,
                    error = null
                )
                
                TimberLogger.d(TAG, "验证码加载成功: sessionId=${securityConfig.sanitizeForLog(data.sessionId)}")
                true
            } ?: run {
                TimberLogger.e(TAG, "验证码数据为空")
                _captchaState.value = _captchaState.value.copy(
                    isLoading = false,
                    error = "验证码数据为空"
                )
                false
            }
        }.getOrElse { exception ->
            TimberLogger.e(TAG, "验证码加载失败", exception)
            _captchaState.value = _captchaState.value.copy(
                isLoading = false,
                error = exception.localizedMessage ?: "验证码加载失败"
            )
            false
        }
    }
    
    /**
     * 刷新验证码
     * 
     * 清理旧验证码并重新加载新的验证码
     * 
     * @return 是否刷新成功
     */
    suspend fun refreshCaptcha(): Boolean {
        TimberLogger.d(TAG, "刷新验证码")
        // 先清理旧的验证码文件
        clearCurrentCaptcha()
        // 重新加载
        return loadCaptcha()
    }
    
    /**
     * 清理当前验证码
     * 
     * 删除当前验证码文件并重置状态
     */
    fun clearCurrentCaptcha() {
        val currentState = _captchaState.value
        if (currentState.imagePath.isNotEmpty()) {
            val deleted = File(currentState.imagePath).delete()
            TimberLogger.d(TAG, "清理当前验证码文件: ${if (deleted) "成功" else "失败"}")
        }
        
        _captchaState.value = CaptchaState()
    }
    
    /**
     * 清理所有临时验证码文件
     * 
     * 定期清理防止缓存文件泄露和占用存储空间
     */
    suspend fun clearAllCaptchaFiles() = withContext(Dispatchers.IO) {
        try {
            val cacheDir = context.cacheDir
            val deletedCount = cacheDir.listFiles()
                ?.filter { securityConfig.isCaptchaFile(it.name) }
                ?.count { file ->
                    if (file.delete()) {
                        TimberLogger.v(TAG, "已删除验证码文件: ${file.name}")
                        true
                    } else {
                        TimberLogger.w(TAG, "删除验证码文件失败: ${file.name}")
                        false
                    }
                } ?: 0
                
            TimberLogger.d(TAG, "已清理 $deletedCount 个验证码文件")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理验证码文件失败", e)
        }
    }
    
    /**
     * 获取当前验证码信息
     * 
     * @return 验证码信息对象，如果没有有效验证码则返回null
     */
    fun getCurrentCaptchaInfo(): CaptchaInfo? {
        val state = _captchaState.value
        return if (state.sessionId.isNotEmpty() && state.imagePath.isNotEmpty()) {
            CaptchaInfo(
                sessionId = state.sessionId,
                imagePath = state.imagePath
            )
        } else null
    }
    
    /**
     * 创建临时验证码文件
     * 
     * @param sessionId 会话ID
     * @param imageBytes 图片字节数组
     * @return 创建的临时文件
     */
    private suspend fun createTempFile(sessionId: String, imageBytes: ByteArray): File = 
        withContext(Dispatchers.IO) {
            val fileName = securityConfig.generateCaptchaFileName(sessionId)
            val file = File(context.cacheDir, fileName)
            file.writeBytes(imageBytes)
            TimberLogger.v(TAG, "创建验证码临时文件: ${file.name}")
            file
        }
    
    /**
     * 验证验证码数据的安全性
     * 
     * 检查Base64数据和会话ID的有效性，防止恶意数据
     * 
     * @param base64Data Base64编码的图片数据
     * @param sessionId 会话ID
     * @return 是否为有效的安全数据
     */
    private fun isValidCaptchaData(base64Data: String, sessionId: String): Boolean {
        return try {
            // 检查Base64格式
            Base64.decode(base64Data, Base64.DEFAULT)
            
            // 检查会话ID格式
            sessionId.isNotBlank() && 
            sessionId.length <= 64 && // 限制会话ID长度
            sessionId.matches(Regex("^[a-zA-Z0-9\\-_]+$")) // 只允许安全字符
        } catch (e: Exception) {
            TimberLogger.e(TAG, "验证码数据验证失败", e)
            false
        }
    }
    
    /**
     * 验证文件大小是否合法
     * 
     * 防止恶意大文件攻击，保护存储空间
     * 
     * @param fileBytes 文件字节数组
     * @return 文件大小是否在允许范围内
     */
    private fun isFileSizeValid(fileBytes: ByteArray): Boolean {
        val fileSizeMB = fileBytes.size / (1024 * 1024)
        val isValid = fileSizeMB <= SecurityConfig.MAX_CACHE_FILE_SIZE_MB
        if (!isValid) {
            TimberLogger.w(TAG, "验证码文件大小超限: ${fileSizeMB}MB")
        }
        return isValid
    }
    
    /**
     * 清理过期的验证码文件
     * 
     * 定期任务，清理超过指定时间的验证码文件
     */
    suspend fun cleanupExpiredFiles() = withContext(Dispatchers.IO) {
        try {
            val cacheDir = context.cacheDir
            val expiryTime = System.currentTimeMillis() - 
                            (SecurityConfig.CACHE_CLEANUP_INTERVAL_HOURS * 60 * 60 * 1000)
            
            val cleanedCount = cacheDir.listFiles()
                ?.filter { securityConfig.isCaptchaFile(it.name) && it.lastModified() < expiryTime }
                ?.count { it.delete() } ?: 0
                
            if (cleanedCount > 0) {
                TimberLogger.d(TAG, "清理了 $cleanedCount 个过期验证码文件")
            }else{
                TimberLogger.d(TAG, "没有过期验证码文件需要清理")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理过期文件失败", e)
        }
    }
}

/**
 * 验证码状态数据类
 * 
 * 封装验证码的加载状态和相关信息
 * 
 * @property imagePath 验证码图片本地路径
 * @property sessionId 验证码会话ID
 * @property isLoading 是否正在加载
 * @property error 错误信息，无错误时为null
 */
data class CaptchaState(
    val imagePath: String = "",
    val sessionId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * 是否有有效的验证码
     * 
     * @return 当有图片路径、会话ID且无错误时返回true
     */
    val hasValidCaptcha: Boolean
        get() = imagePath.isNotEmpty() && sessionId.isNotEmpty() && error == null
}

/**
 * 验证码信息数据类
 * 
 * 用于传递验证码的基本信息
 * 
 * @property sessionId 验证码会话ID
 * @property imagePath 验证码图片本地路径
 */
data class CaptchaInfo(
    val sessionId: String,
    val imagePath: String
)
