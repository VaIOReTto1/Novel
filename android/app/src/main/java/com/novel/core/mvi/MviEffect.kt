package com.novel.core.mvi

import java.util.UUID

/**
 * MVI架构中的Effect基础接口
 * 
 * Effect代表一次性副作用，如导航、Toast、Dialog等
 * 与State不同，Effect不会被保存，只会被消费一次
 * 
 * 特性：
 * - 一次性消费：Effect被处理后不会再次触发
 * - 唯一标识：每个Effect都有唯一ID，防止重复处理
 * - 时间戳：支持效果的时序分析
 * - 不可变设计：确保副作用的可预测性
 */
interface MviEffect {
    /** 
     * Effect创建时间戳，用于：
     * - 效果时序分析
     * - 过期效果过滤
     * - 调试日志
     */
    val timestamp: Long get() = System.currentTimeMillis()
    
    /** 
     * Effect唯一标识符，用于：
     * - 防止重复处理
     * - 调试追踪
     * - 日志关联
     */
    val id: String get() = UUID.randomUUID().toString()
} 
