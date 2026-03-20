package com.novel.rn

import com.novel.utils.TimberLogger
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * React Native 设备事件发射器封装类
 * 
 * 功能职责：
 * - 简化Android到RN的事件发送过程
 * - 统一事件发送接口封装
 * - 异常安全处理机制
 * 
 * 技术实现：
 * - 基于DeviceEventManagerModule的事件机制
 * - 支持WritableMap数据传递
 * - ReactContext生命周期检查
 * 
 * 使用场景：
 * - 用户状态变更通知
 * - 数据更新事件推送
 * - 系统状态同步
 */
class RCTDeviceEventEmitter {
    companion object {
        private const val TAG = "RCTDeviceEventEmitter"
        
        /**
         * 发送事件到React Native
         * 
         * @param reactContext RN上下文对象
         * @param eventName 事件名称
         * @param params 事件参数（WritableMap格式）
         */
        fun sendEvent(
            reactContext: ReactContext?,
            eventName: String,
            params: WritableMap?
        ) {
            reactContext?.let { context ->
                try {
                    TimberLogger.d(TAG, "发送RN事件: $eventName")
                    context
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                        ?.emit(eventName, params)
                    TimberLogger.d(TAG, "RN事件发送成功: $eventName")
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "RN事件发送失败: $eventName", e)
                }
            } ?: run {
                TimberLogger.w(TAG, "ReactContext为空，无法发送事件: $eventName")
            }
        }
    }
}
