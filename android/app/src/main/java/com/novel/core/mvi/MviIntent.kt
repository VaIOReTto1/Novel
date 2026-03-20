package com.novel.core.mvi

import java.util.UUID

/**
 * MVI架构中的Intent基础接口
 * 
 * Intent代表用户意图或系统事件，是MVI架构中状态变更的唯一入口
 * 
 * 特性：
 * - 每个Intent都有唯一ID，便于调试和事件追踪
 * - 包含时间戳，支持事件时序分析
 * - 不可变设计，确保状态变更的可预测性
 */
interface MviIntent {
    /** 
     * Intent创建时间戳，用于：
     * - 事件时序分析
     * - 性能监控
     * - 调试日志
     */
    val timestamp: Long get() = System.currentTimeMillis()
    
    /** 
     * Intent唯一标识符，用于：
     * - 事件去重处理
     * - 调试追踪
     * - 日志关联
     */
    val id: String get() = UUID.randomUUID().toString()
} 
