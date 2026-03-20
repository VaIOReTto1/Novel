package com.novel.core.mvi

import androidx.compose.runtime.Stable

/**
 * MVI架构中的Reducer基础接口
 * 
 * Reducer负责根据当前状态和Intent计算新状态
 * 是MVI架构中状态转换的核心逻辑
 * 
 * 特性：
 * - 纯函数：相同输入总是产生相同输出
 * - 无副作用：不修改输入参数，只返回新状态
 * - 可预测：状态变更逻辑清晰可追踪
 * - 可测试：纯函数易于单元测试
 */
interface MviReducer<I : MviIntent, S : MviState> {
    
    /**
     * 根据当前状态和Intent计算新状态
     * 
     * @param currentState 当前状态
     * @param intent 用户意图或系统事件
     * @return 新的状态，如果无需变更则返回原状态
     */
    fun reduce(currentState: S, intent: I): S
}

/**
 * 支持副作用的Reducer接口
 * 
 * 在某些情况下，状态变更可能需要触发副作用
 * 此接口支持在状态变更的同时返回副作用
 */
interface MviReducerWithEffect<I : MviIntent, S : MviState, E : MviEffect> {
    
    /**
     * 根据当前状态和Intent计算新状态和副作用
     * 
     * @param currentState 当前状态
     * @param intent 用户意图或系统事件
     * @return 包含新状态和可选副作用的结果
     */
    fun reduce(currentState: S, intent: I): ReduceResult<S, E>
}

/**
 * Reducer处理结果
 * 
 * @param newState 新的状态
 * @param effect 可选的副作用
 */
@Stable
data class ReduceResult<S : MviState, E : MviEffect>(
    val newState: S,
    val effect: E? = null
) 
