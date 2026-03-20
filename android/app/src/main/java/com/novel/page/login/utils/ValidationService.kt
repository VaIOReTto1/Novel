package com.novel.page.login.utils

import com.novel.utils.TimberLogger
import com.novel.utils.security.SecurityConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 输入验证服务
 * 
 * 核心功能：
 * - 表单验证：手机号、密码、验证码等输入验证
 * - 安全检查：密码强度、输入长度等安全验证
 * - 批量验证：登录、注册表单的整体验证
 * - 错误收集：统一的验证错误信息管理
 * 
 * 安全特性：
 * - 所有输入验证都遵循严格的业务规则
 * - 防护恶意输入和注入攻击
 * - 密码强度检查，确保账户安全
 * - 统一的验证规则，避免前后端不一致
 * 
 * 设计特点：
 * - 单例模式，全局验证规则一致
 * - 依赖注入SecurityConfig，配置集中管理
 * - 链式验证，支持多种验证规则组合
 * - 详细的错误信息，提升用户体验
 */
@Singleton
class ValidationService @Inject constructor(
    /** 安全配置，提供验证规则和常量 */
    private val securityConfig: SecurityConfig
) {
    
    companion object {
        private const val TAG = "ValidationService"
    }
    
    /**
     * 验证手机号格式
     * 
     * 验证规则：
     * - 不能为空
     * - 符合手机号正则表达式
     * - 长度符合标准格式
     * 
     * @param phone 待验证的手机号
     * @return 验证结果，null表示验证通过
     */
    fun validatePhone(phone: String): ValidationResult? {
        TimberLogger.d(TAG, "验证手机号格式")
        
        return when {
            phone.isBlank() -> {
                TimberLogger.w(TAG, "手机号为空")
                ValidationResult.Error("手机号不能为空")
            }
            !phone.matches(Regex(SecurityConfig.PHONE_REGEX)) -> {
                TimberLogger.w(TAG, "手机号格式不正确: $phone")
                ValidationResult.Error("请输入有效的手机号")
            }
            else -> {
                TimberLogger.d(TAG, "手机号验证通过")
                null
            }
        }
    }
    
    /**
     * 验证密码强度
     * 
     * 安全检查规则：
     * - 密码不能为空
     * - 长度在最小-最大范围内
     * - 不包含非法字符
     * - 符合密码复杂度要求
     * 
     * @param password 待验证的密码
     * @return 验证结果，null表示验证通过
     */
    fun validatePassword(password: String): ValidationResult? {
        TimberLogger.d(TAG, "验证密码强度")
        
        return when {
            password.isBlank() -> {
                TimberLogger.w(TAG, "密码为空")
                ValidationResult.Error("密码不能为空")
            }
            password.length < SecurityConfig.MIN_PASSWORD_LENGTH -> {
                TimberLogger.w(TAG, "密码长度不足: ${password.length}")
                ValidationResult.Error("密码长度需至少${SecurityConfig.MIN_PASSWORD_LENGTH}位")
            }
            password.length > SecurityConfig.MAX_PASSWORD_LENGTH -> {
                TimberLogger.w(TAG, "密码长度超限: ${password.length}")
                ValidationResult.Error("密码长度不能超过${SecurityConfig.MAX_PASSWORD_LENGTH}位")
            }
            !password.matches(Regex(SecurityConfig.PASSWORD_REGEX)) -> {
                TimberLogger.w(TAG, "密码包含非法字符")
                ValidationResult.Error("密码包含非法字符")
            }
            else -> {
                TimberLogger.d(TAG, "密码验证通过")
                null
            }
        }
    }
    
    /**
     * 验证密码确认
     * 
     * 验证规则：
     * - 确认密码不能为空
     * - 两次输入的密码必须一致
     * 
     * @param password 原始密码
     * @param confirmPassword 确认密码
     * @return 验证结果，null表示验证通过
     */
    fun validatePasswordConfirm(password: String, confirmPassword: String): ValidationResult? {
        TimberLogger.d(TAG, "验证密码确认")
        
        return when {
            confirmPassword.isBlank() -> {
                TimberLogger.w(TAG, "确认密码为空")
                ValidationResult.Error("确认密码不能为空")
            }
            confirmPassword != password -> {
                TimberLogger.w(TAG, "两次密码输入不一致")
                ValidationResult.Error("两次输入的密码不一致")
            }
            else -> {
                TimberLogger.d(TAG, "密码确认验证通过")
                null
            }
        }
    }
    
    /**
     * 验证验证码
     * 
     * 验证规则：
     * - 根据isRequired参数决定是否必填
     * - 长度符合验证码格式要求
     * - 字符符合验证码字符集
     * 
     * @param verifyCode 验证码
     * @param isRequired 是否必填，默认为true
     * @return 验证结果，null表示验证通过
     */
    fun validateVerifyCode(verifyCode: String, isRequired: Boolean = true): ValidationResult? {
        TimberLogger.d(TAG, "验证验证码，是否必填: $isRequired")
        
        return when {
            isRequired && verifyCode.isBlank() -> {
                TimberLogger.w(TAG, "验证码为空且为必填项")
                ValidationResult.Error("验证码不能为空")
            }
            isRequired && verifyCode.length < SecurityConfig.CAPTCHA_MIN_LENGTH -> {
                TimberLogger.w(TAG, "验证码长度不足: ${verifyCode.length}")
                ValidationResult.Error("验证码格式错误")
            }
            verifyCode.isNotBlank() && !verifyCode.matches(Regex(SecurityConfig.CAPTCHA_REGEX)) -> {
                TimberLogger.w(TAG, "验证码格式不正确")
                ValidationResult.Error("验证码格式错误")
            }
            else -> {
                TimberLogger.d(TAG, "验证码验证通过")
                null
            }
        }
    }
    
    /**
     * 验证用户名长度
     * 
     * @param username 用户名
     * @return 验证结果，null表示验证通过
     */
    fun validateUsernameLength(username: String): ValidationResult? {
        TimberLogger.d(TAG, "验证用户名长度")
        
        return when {
            !securityConfig.isInputLengthValid(
                username, 
                SecurityConfig.MIN_USERNAME_LENGTH, 
                SecurityConfig.MAX_USERNAME_LENGTH
            ) -> {
                TimberLogger.w(TAG, "用户名长度不符合要求: ${username.length}")
                ValidationResult.Error("用户名长度需在${SecurityConfig.MIN_USERNAME_LENGTH}-${SecurityConfig.MAX_USERNAME_LENGTH}位之间")
            }
            else -> {
                TimberLogger.d(TAG, "用户名长度验证通过")
                null
            }
        }
    }
    
    /**
     * 批量验证登录表单
     * 
     * @param form 登录表单数据
     * @return 验证结果映射，包含各字段的验证结果
     */
    fun validateLoginForm(form: LoginForm): ValidationResults {
        TimberLogger.d(TAG, "开始批量验证登录表单")
        
        val results = ValidationResults(
            phoneError = validatePhone(form.phone),
            passwordError = validatePassword(form.password)
        )
        
        TimberLogger.d(TAG, "登录表单验证完成，是否通过: ${results.isValid}")
        return results
    }
    
    /**
     * 批量验证注册表单
     * 
     * @param form 注册表单数据
     * @return 验证结果映射，包含各字段的验证结果
     */
    fun validateRegisterForm(form: RegisterForm): ValidationResults {
        TimberLogger.d(TAG, "开始批量验证注册表单")
        
        val results = ValidationResults(
            phoneError = validatePhone(form.phone),
            passwordError = validatePassword(form.password),
            passwordConfirmError = validatePasswordConfirm(form.password, form.passwordConfirm),
            verifyCodeError = validateVerifyCode(form.verifyCode, isRequired = true)
        )
        
        TimberLogger.d(TAG, "注册表单验证完成，是否通过: ${results.isValid}")
        return results
    }
    
    /**
     * 验证用户昵称
     * 
     * @param nickname 昵称
     * @return 验证结果，null表示验证通过
     */
    fun validateNickname(nickname: String): ValidationResult? {
        TimberLogger.d(TAG, "验证用户昵称")
        
        return when {
            nickname.isBlank() -> {
                TimberLogger.w(TAG, "昵称为空")
                ValidationResult.Error("昵称不能为空")
            }
            !securityConfig.isInputLengthValid(
                nickname, 
                1, 
                SecurityConfig.MAX_NICKNAME_LENGTH
            ) -> {
                TimberLogger.w(TAG, "昵称长度超限: ${nickname.length}")
                ValidationResult.Error("昵称长度不能超过${SecurityConfig.MAX_NICKNAME_LENGTH}位")
            }
            else -> {
                TimberLogger.d(TAG, "昵称验证通过")
                null
            }
        }
    }
}

/**
 * 验证结果
 */
sealed class ValidationResult {
    data class Error(val message: String) : ValidationResult()
}

/**
 * 批量验证结果
 */
data class ValidationResults(
    val phoneError: ValidationResult? = null,
    val passwordError: ValidationResult? = null,
    val passwordConfirmError: ValidationResult? = null,
    val verifyCodeError: ValidationResult? = null
) {
    /**
     * 检查是否所有验证都通过
     */
    val isValid: Boolean
        get() = phoneError == null && 
                passwordError == null && 
                passwordConfirmError == null && 
                verifyCodeError == null
    
    /**
     * 获取所有错误信息
     */
    fun getAllErrors(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        phoneError?.let { errors["phone"] = (it as ValidationResult.Error).message }
        passwordError?.let { errors["password"] = (it as ValidationResult.Error).message }
        passwordConfirmError?.let { errors["passwordConfirm"] = (it as ValidationResult.Error).message }
        verifyCodeError?.let { errors["verifyCode"] = (it as ValidationResult.Error).message }
        return errors
    }
}

/**
 * 登录表单数据
 */
data class LoginForm(
    val phone: String,
    val password: String
)

/**
 * 注册表单数据
 */
data class RegisterForm(
    val phone: String,
    val password: String,
    val passwordConfirm: String,
    val verifyCode: String
) 
