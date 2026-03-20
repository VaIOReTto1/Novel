package com.novel.page.component

import androidx.compose.runtime.Stable

/**
 * 默认值配置基类
 * 
 * 设计模式：
 * - 提供组件默认配置的统一管理机制
 * - 支持全局默认值的动态修改
 * - 确保组件配置的一致性和可维护性
 * 
 * 使用场景：
 * - UI组件的默认样式配置
 * - 主题相关的默认值管理
 * - 用户偏好设置的默认值
 * 
 * 设计特点：
 * - 抽象基类设计，支持泛型扩展
 * - Target内部类提供实例管理
 * - 支持默认值的动态修改和获取
 */
abstract class Defaults {

    /**
     * 默认值目标管理器
     * 
     * 管理特定类型默认值的实例，提供获取和设置接口
     * 
     * @param T 默认值类型，必须继承自Defaults
     * @param defaults 初始默认值实例
     */
    @Stable
    abstract class Target<T : Defaults>(defaults: T) {

        /** 当前默认值实例 */
        private var _instance: T = defaults
        
        /** 
         * 获取当前默认值实例
         * @return 当前的默认值配置实例
         */
        val instance: T get() = _instance

        /**
         * 设置新的默认值实例
         * 
         * 用于动态修改全局默认配置
         * @param defaults 新的默认值配置实例
         */
        fun set(defaults: T) {
            _instance = defaults
        }
    }
}
