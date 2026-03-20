package com.novel.page.login.usecase

import com.novel.core.domain.BaseUseCase
import com.novel.page.login.viewmodel.PhoneInfo
import com.novel.utils.PhoneInfoUtil
import com.novel.utils.TimberLogger
import com.novel.utils.maskPhoneNumber
import javax.inject.Inject

/**
 * 页面初始化UseCase
 * 
 * 封装页面初始化业务逻辑，包括手机信息获取等
 * 基于现有逻辑重构，保持功能不变
 */
class InitializePageUseCase @Inject constructor(
    private val phoneInfoUtil: PhoneInfoUtil
) : BaseUseCase<Unit, InitializePageUseCase.Result>() {
    
    companion object {
        private const val TAG = "InitializePageUseCase"
    }
    
    data class Result(
        val phoneInfo: PhoneInfo
    )
    
    override suspend fun execute(params: Unit): Result {
        TimberLogger.d(TAG, "初始化页面数据")
        
        return try {
            // 获取手机信息
            val phoneInfoData = phoneInfoUtil.fetch()
            
            val phoneInfo = PhoneInfo(
                phoneNumber = maskPhoneNumber(phoneInfoData.phoneNumber),
                operatorName = phoneInfoData.operatorName
            )
            
            TimberLogger.d(TAG, "页面数据初始化完成，运营商: ${phoneInfo.operatorName}")
            Result(phoneInfo = phoneInfo)
        } catch (e: Exception) {
            TimberLogger.e(TAG, "页面数据初始化失败", e)
            // 返回默认值，确保页面能正常显示
            Result(phoneInfo = PhoneInfo())
        }
    }
} 
