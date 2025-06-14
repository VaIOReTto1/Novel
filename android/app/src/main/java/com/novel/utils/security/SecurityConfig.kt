package com.novel.utils.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 安全配置管理 - 统一管理安全相关的配置和常量
 * ⚠ 安全检查: 所有安全相关的配置都在这里集中管理
 */
@Singleton
class SecurityConfig @Inject constructor() {
    
    companion object {
        // ——— 密码安全配置 ———
        const val MIN_PASSWORD_LENGTH = 6
        const val MAX_PASSWORD_LENGTH = 20
        const val PASSWORD_REGEX = "^[A-Za-z0-9@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]+$"
        
        // ——— 手机号验证配置 ———
        const val PHONE_REGEX = "^1[3-9]\\d{9}$"
        
        // ——— 验证码配置 ———
        const val CAPTCHA_MIN_LENGTH = 4
        const val CAPTCHA_MAX_LENGTH = 6
        const val CAPTCHA_REGEX = "^[A-Za-z0-9]{4,6}$"
        const val CAPTCHA_CACHE_PREFIX = "captcha_"
        const val CAPTCHA_FILE_EXTENSION = ".jpg"
        
        // ——— Token安全配置 ———
        const val TOKEN_EXPIRY_DURATION_MS = 30 * 24 * 3600_000L // 1小时
        const val TOKEN_REFRESH_THRESHOLD_MS = 300_000L // 5分钟内刷新
        
        // ——— 文件安全配置 ———
        val ALLOWED_IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "webp")
        const val MAX_CACHE_FILE_SIZE_MB = 10
        const val CACHE_CLEANUP_INTERVAL_HOURS = 24
        
        // ——— 网络安全配置 ———
        const val REQUEST_TIMEOUT_SECONDS = 30L
        const val CONNECT_TIMEOUT_SECONDS = 15L
        const val READ_TIMEOUT_SECONDS = 30L
        const val MAX_RETRY_ATTEMPTS = 3
        
        // ——— 输入限制配置 ———
        const val MAX_USERNAME_LENGTH = 50
        const val MAX_NICKNAME_LENGTH = 30
        const val MIN_USERNAME_LENGTH = 1
        
        // ——— 运营商客服电话映射 ———
        val OPERATOR_SERVICE_NUMBERS = mapOf(
            "移动" to "10086",
            "联通" to "10010",
            "电信" to "10000",
            "广电" to "96655",
            "default" to "10000"
        )
    }
    
    /**
     * 获取Token过期时间戳
     * @return 过期时间戳
     */
    fun getTokenExpiryTimestamp(): Long {
        return System.currentTimeMillis() + TOKEN_EXPIRY_DURATION_MS
    }
    
    /**
     * 检查Token是否需要刷新
     * @param expiryTimestamp Token过期时间戳
     * @return 是否需要刷新
     */
    fun shouldRefreshToken(expiryTimestamp: Long): Boolean {
        return (expiryTimestamp - System.currentTimeMillis()) <= TOKEN_REFRESH_THRESHOLD_MS
    }
    
    /**
     * 验证文件扩展名是否安全
     * @param fileName 文件名
     * @return 是否安全
     */
    fun isFileExtensionAllowed(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extension in ALLOWED_IMAGE_EXTENSIONS
    }
    
    /**
     * 获取运营商客服电话
     * @param operatorName 运营商名称
     * @return 客服电话
     */
    fun getOperatorServiceNumber(operatorName: String): String {
        return OPERATOR_SERVICE_NUMBERS[operatorName] ?: OPERATOR_SERVICE_NUMBERS["default"]!!
    }
    
    /**
     * 生成验证码文件名
     * @param sessionId 会话ID
     * @return 文件名
     */
    fun generateCaptchaFileName(sessionId: String): String {
        val timestamp = System.currentTimeMillis()
        return "${CAPTCHA_CACHE_PREFIX}${sessionId}_${timestamp}${CAPTCHA_FILE_EXTENSION}"
    }
    
    /**
     * 检查是否是验证码文件
     * @param fileName 文件名
     * @return 是否是验证码文件
     */
    fun isCaptchaFile(fileName: String): Boolean {
        return fileName.startsWith(CAPTCHA_CACHE_PREFIX) && 
               fileName.endsWith(CAPTCHA_FILE_EXTENSION)
    }
    
    /**
     * 验证输入长度
     * @param input 输入字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否在有效范围内
     */
    fun isInputLengthValid(input: String, minLength: Int, maxLength: Int): Boolean {
        return input.length in minLength..maxLength
    }
    
    /**
     * 清理敏感数据（用于日志输出）
     * @param data 原始数据
     * @return 脱敏后的数据
     */
    fun sanitizeForLog(data: String): String {
        return when {
            data.isEmpty() -> "[空]"
            data.length <= 4 -> "*".repeat(data.length)
            else -> "${data.take(2)}${"*".repeat(data.length - 4)}${data.takeLast(2)}"
        }
    }
} 