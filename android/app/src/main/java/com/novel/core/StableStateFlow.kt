package com.novel.core

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

/**
 * StateFlow 稳定包装器
 * 
 * 用于将 StateFlow 包装为 Compose 稳定类型，解决 Compose 编译器中的稳定性问题
 * 
 * 核心功能：
 * - 为 StateFlow 提供稳定性标记
 * - 解决 Compose 编译器中的 knownUnstableArguments 问题
 * - 保持原有 StateFlow 的所有功能
 * 
 * 使用场景：
 * - ViewModel 中暴露给 UI 的 StateFlow
 * - Repository 中提供给上层的数据流
 * - 需要保证 Compose 稳定性的任何 StateFlow
 * 
 * 示例：
 * ```kotlin
 * // 在 ViewModel 中使用
 * class MyViewModel {
 *     private val _uiState = MutableStateFlow(initialState)
 *     val uiState: StateFlow<MyState> = _uiState.asStable()
 * }
 * ```
 */
@Stable
class StableStateFlow<T>(
    private val delegate: StateFlow<T>
) : StateFlow<T> by delegate

/**
 * Flow 稳定包装器
 * 
 * 用于将 Flow 包装为 Compose 稳定类型，解决 Compose 编译器中的稳定性问题
 */
@Stable
class StableFlow<T>(
    private val delegate: Flow<T>
) : Flow<T> {
    override suspend fun collect(collector: FlowCollector<T>) {
        delegate.collect(collector)
    }
}

/**
 * StateFlow 稳定包装扩展函数
 * 
 * 为 StateFlow 提供便捷的稳定性包装方法
 * 
 * @return 包装后的稳定 StateFlow
 */
fun <T> StateFlow<T>.asStable(): StateFlow<T> = StableStateFlow(this)

/**
 * Flow 稳定包装扩展函数
 * 
 * 为 Flow 提供便捷的稳定性包装方法
 * 
 * @return 包装后的稳定 Flow
 */
fun <T> Flow<T>.asStable(): Flow<T> = StableFlow(this)
