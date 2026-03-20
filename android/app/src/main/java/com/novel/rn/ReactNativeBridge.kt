package com.novel.rn

import android.annotation.SuppressLint
import com.novel.utils.TimberLogger
import com.facebook.react.bridge.Arguments
import com.novel.MainApplication
import com.novel.utils.network.api.front.HomeService

/**
 * React Native 桥接工具类
 * 
 * 功能职责：
 * - Android原生与RN之间的数据通信
 * - 用户登录状态同步
 * - 书籍推荐数据传递
 * - 测试数据模拟发送
 * 
 * 技术实现：
 * - 基于RCTDeviceEventEmitter事件机制
 * - Arguments数据序列化
 * - ReactContext生命周期管理
 * - 异常安全处理机制
 * 
 * 事件类型：
 * - onUserDataReceived: 用户数据接收
 * - onRecommendBooksReceived: 推荐书籍接收
 */
object ReactNativeBridge {
    
    private const val TAG = "ReactNativeBridge"
    
    /**
     * 发送用户登录数据到RN
     * 
     * 数据包含：
     * - uid: 用户ID
     * - token: 认证令牌
     * - nickname: 用户昵称
     * - photo: 头像URL
     * - sex: 性别（可选）
     * 
     * @param uid 用户ID
     * @param token 认证令牌
     * @param nickname 用户昵称
     * @param photo 头像URL
     * @param sex 性别信息（可选）
     */
    @SuppressLint("VisibleForTests")
    fun sendUserDataToRN(
        uid: String,
        token: String,
        nickname: String,
        photo: String,
        sex: String? = null
    ) {
        TimberLogger.d(TAG, "🚀 发送用户数据到RN: uid=${uid.take(8)}***, nickname=$nickname")
        
        val application = MainApplication.getInstance()
        val reactContext = application?.reactNativeHost?.reactInstanceManager?.currentReactContext
        
        reactContext?.let { context ->
            val params = Arguments.createMap().apply {
                putString("uid", uid)
                putString("token", token)
                putString("nickname", nickname)
                putString("photo", photo)
                sex?.let { putString("sex", it) }
            }
            
            RCTDeviceEventEmitter.sendEvent(
                context,
                "onUserDataReceived",
                params
            )
            
            TimberLogger.d(TAG, "✅ 用户数据已发送到RN")
        } ?: run {
            TimberLogger.w(TAG, "❌ ReactContext为空，无法发送用户数据")
        }
    }
} 
