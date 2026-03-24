package com.novel.page.welfare.usecase

import com.novel.core.domain.BaseUseCase
import com.novel.core.logging.CoreLogger
import com.novel.page.welfare.utils.WelfareWebSecurityConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitializeWelfarePageUseCase @Inject constructor() :
    BaseUseCase<Unit, InitializeWelfarePageUseCase.Result>() {

    companion object {
        private const val TAG = "InitializeWelfarePageUseCase"
        private const val DEFAULT_TITLE = WelfareWebSecurityConfig.DEFAULT_TITLE
        private const val DEFAULT_URL = WelfareWebSecurityConfig.DEFAULT_URL
    }

    data class Result(
        val title: String,
        val defaultUrl: String,
        val isSuccess: Boolean,
        val message: String,
    )

    override suspend fun execute(parameters: Unit): Result {
        return try {
            CoreLogger.d(TAG, "开始初始化福利页面")
            CoreLogger.d(TAG, "福利页面初始化成功")
            Result(
                title = DEFAULT_TITLE,
                defaultUrl = DEFAULT_URL,
                isSuccess = true,
                message = "初始化成功",
            )
        } catch (e: Exception) {
            CoreLogger.e(TAG, "福利页面初始化失败", e)
            Result(
                title = DEFAULT_TITLE,
                defaultUrl = DEFAULT_URL,
                isSuccess = false,
                message = "初始化失败：${e.localizedMessage}",
            )
        }
    }
}
