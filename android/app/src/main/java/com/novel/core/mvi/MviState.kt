package com.novel.core.mvi

import androidx.compose.runtime.Stable

/**
 * MVI架构中的State基础接口
 * 
 * State代表UI的完整状态，是MVI架构中的单一数据源
 * 
 * 特性：
 * - 版本控制：每次状态变更都会增加版本号
 * - 加载状态：统一的加载状态管理
 * - 错误处理：统一的错误状态管理
 * - 不可变设计：确保状态变更的可预测性
 */
@Stable
interface MviState {
    /** 
     * 状态版本号，每次状态变更时递增
     * 用于：
     * - 状态变更追踪
     * - 乐观更新处理
     * - 调试和日志
     */
    val version: Long
    
    /** 
     * 加载状态标识
     * 默认为false，子类可根据实际情况覆盖
     */
    val isLoading: Boolean get() = false
    
    /** 
     * 错误信息
     * null表示无错误，非null表示存在错误
     */
    val error: String? get() = null
    
    /** 
     * 是否处于错误状态
     * 基于error字段计算得出
     */
    val hasError: Boolean get() = error != null
    
    /** 
     * 是否处于空状态
     * 默认实现返回false，子类可根据实际情况覆盖
     */
    val isEmpty: Boolean get() = false
    
    /** 
     * 是否处于成功状态
     * 即非加载、非错误、非空状态
     */
    val isSuccess: Boolean get() = !isLoading && !hasError && !isEmpty
} 
