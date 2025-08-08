package com.novel.core.adapter

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.react.BuildConfig
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.Flow
import com.novel.utils.TimberLogger
import com.novel.core.mvi.MviState
import com.novel.core.asStable

/**
 * MVI状态适配器基类
 * 
 * 为各模块提供统一的状态适配功能基础，包括：
 * - 细粒度状态订阅
 * - 性能优化（distinctUntilChanged）
 * - 类型安全的状态访问
 * - 通用状态转换方法
 * - 状态快照功能
 * 
 * @param S MviState类型
 */
@Stable
abstract class StateAdapter<S : MviState>(
    @Stable val stateFlow: StateFlow<S>
) {
    
    companion object {
        private const val TAG = "StateAdapter"
    }
    
    // region 基础状态适配
    
    /** 当前完整状态流 */
    @Stable
    val currentState: StateFlow<S> = stateFlow.asStable()
    
    /** 基础加载状态 */
    @Stable
    val isLoading: Flow<Boolean> = stateFlow.asStable()
        .map { it.isLoading }
        .distinctUntilChanged()
        .asStable()
    
    /** 基础错误状态 */
    @Stable
    val error: Flow<String?> = stateFlow.asStable()
        .map { it.error }
        .distinctUntilChanged()
        .asStable()
    
    /** 是否处于错误状态 */
    @Stable
    val hasError: Flow<Boolean> = stateFlow.asStable()
        .map { it.hasError }
        .distinctUntilChanged()
        .asStable()
    
    /** 是否为空状态 */
    @Stable
    val isEmpty: Flow<Boolean> = stateFlow.asStable()
        .map { it.isEmpty }
        .distinctUntilChanged()
        .asStable()
    
    /** 是否处于成功状态 */
    @Stable
    val isSuccess: Flow<Boolean> = stateFlow.asStable()
        .map { it.isSuccess }
        .distinctUntilChanged()
        .asStable()
    
    /** 状态版本号 */
    @Stable
    val version: Flow<Long> = stateFlow.asStable()
        .map { it.version }
        .distinctUntilChanged()
        .asStable()
    
    // endregion
    
        // region 稳定状态创建工具

    /** 
     * 创建稳定的 State<T> 对象
     * 使用 collectAsStateWithLifecycle 确保响应 StateFlow 变化
     */
    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun <T> createStableState(
        selector: (S) -> T
    ): State<T> {
        return stateFlow.map(selector).collectAsStateWithLifecycle(
            initialValue = selector(stateFlow.value)
        )
    }
    
    /** 
     * 创建稳定的 State<T> 对象，带缓存键
     * 当 key 变化时重新计算
     */
    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun <T> createStableState(
        key: Any?,
        selector: (S) -> T
    ): State<T> {
        return remember(key) {
            stateFlow.map(selector)
        }.collectAsStateWithLifecycle(
            initialValue = selector(stateFlow.value)
        )
    }
    
    /** 
     * 创建简单值的 State，确保响应 StateFlow 变化
     */
    @Composable
    fun createLoadingState(): State<Boolean> = 
        createStableState { it.isLoading }
    
    @Composable  
    fun createErrorState(): State<String?> = 
        createStableState { it.error }
        
    @Composable
    fun createSuccessState(): State<Boolean> = 
        createStableState { it.isSuccess }
    
    // endregion
    
    // region 便利方法
    
    /** 获取当前状态快照 */
    fun getCurrentSnapshot(): S = stateFlow.value
    
    /** 获取当前加载状态快照 */
    fun isCurrentlyLoading(): Boolean = getCurrentSnapshot().isLoading
    
    /** 获取当前错误状态快照 */
    fun getCurrentError(): String? = getCurrentSnapshot().error
    
    /** 检查当前是否有错误 */
    fun hasCurrentError(): Boolean = getCurrentSnapshot().hasError
    
    /** 检查当前是否为空状态 */
    fun isCurrentlyEmpty(): Boolean = getCurrentSnapshot().isEmpty
    
    /** 检查当前是否为成功状态 */
    fun isCurrentlySuccess(): Boolean = getCurrentSnapshot().isSuccess
    
    /** 获取当前版本号 */
    fun getCurrentVersion(): Long = getCurrentSnapshot().version
    
    // endregion
    
    // region 状态转换辅助方法
    
    /** 
     * 创建状态字段的Flow
     * 提供类型安全的状态字段访问
     */
    @Stable
    inline fun <T> mapState(
        crossinline selector: (S) -> T
    ): Flow<T> = stateFlow
        .map { selector(it) }
        .distinctUntilChanged()
        .asStable()
    
    /** 
     * 创建带条件的状态Flow
     * 根据条件过滤状态变更
     */
    @Stable
    inline fun <T> mapStateWhen(
        crossinline condition: (S) -> Boolean,
        crossinline selector: (S) -> T,
        defaultValue: T
    ): Flow<T> = stateFlow
        .map { state ->
            if (condition(state)) {
                selector(state)
            } else {
                defaultValue
            }
        }
        .distinctUntilChanged()
        .asStable()
    
    /** 
     * 创建组合状态Flow
     * 将多个状态字段组合成一个Flow
     */
    @Stable
    inline fun <T> combineState(
        crossinline combiner: (S) -> T
    ): Flow<T> = stateFlow
        .map { combiner(it) }
        .distinctUntilChanged()
        .asStable()
    
    /** 
     * 创建条件状态Flow
     * 返回布尔条件的Flow
     */
    @Stable
    inline fun createConditionFlow(
        crossinline condition: (S) -> Boolean
    ): Flow<Boolean> = stateFlow
        .map { condition(it) }
        .distinctUntilChanged()
        .asStable()
    
    // endregion
    
    // region 调试和日志
    
    /** 
     * 记录状态变更日志
     * 仅在Debug模式下启用
     */
    fun logStateChange(message: String) {
        if (BuildConfig.DEBUG) {
            val state = getCurrentSnapshot()
            TimberLogger.d(TAG, "$message - 版本: ${state.version}, 加载: ${state.isLoading}, 错误: ${state.hasError}")
        }
    }
    
    /** 
     * 获取状态摘要信息
     * 用于调试和日志
     */
    fun getStateSummary(): String {
        val state = getCurrentSnapshot()
        return "StateAdapter(${this::class.simpleName}): 版本=${state.version}, 加载=${state.isLoading}, 错误=${state.hasError}, 空=${state.isEmpty}"
    }
    
    // endregion
}

/**
 * StateAdapter工厂方法
 * 为StateFlow提供便捷的适配器创建
 */
inline fun <S : MviState, A : StateAdapter<S>> StateFlow<S>.createAdapter(
    adapterFactory: (StateFlow<S>) -> A
): A = adapterFactory(this)

/**
 * StateAdapter扩展方法
 * 提供通用的状态监听功能
 */
fun <S : MviState> StateAdapter<S>.onStateChange(
    action: (S) -> Unit
): Flow<S> = currentState
    .map { state ->
        action(state)
        state
    }

/**
 * 状态更新监听器
 * 提供状态特定字段的变更监听
 */
class StateUpdateListener<S : MviState>(
    private val adapter: StateAdapter<S>
) {
    
    /** 监听加载状态变更 */
    fun onLoadingChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isLoading.map { loading ->
            action(loading)
            loading
        }
    }
    
    /** 监听错误状态变更 */
    fun onErrorChanged(action: (String?) -> Unit): Flow<String?> {
        return adapter.error.map { error ->
            action(error)
            error
        }
    }
    
    /** 监听成功状态变更 */
    fun onSuccessChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isSuccess.map { success ->
            action(success)
            success
        }
    }
}

/**
 * 为StateAdapter创建更新监听器
 */
fun <S : MviState> StateAdapter<S>.createUpdateListener(): StateUpdateListener<S> {
    return StateUpdateListener(this)
}

/**
 * 状态比较器
 * 用于比较两个状态的差异
 */
object StateComparator {

    /**
     * 比较两个状态的基础字段差异
     */
    fun <S : MviState> compareBasicFields(oldState: S, newState: S): List<String> {
        val differences = mutableListOf<String>()

        if (oldState.version != newState.version) {
            differences.add("版本: ${oldState.version} -> ${newState.version}")
        }
        if (oldState.isLoading != newState.isLoading) {
            differences.add("加载状态: ${oldState.isLoading} -> ${newState.isLoading}")
        }
        if (oldState.error != newState.error) {
            differences.add("错误状态: ${oldState.error} -> ${newState.error}")
        }
        if (oldState.isEmpty != newState.isEmpty) {
            differences.add("空状态: ${oldState.isEmpty} -> ${newState.isEmpty}")
        }

        return differences
    }

    /**
     * 获取状态变更摘要
     */
    fun <S : MviState> getChangeSummary(oldState: S, newState: S): String {
        val differences = compareBasicFields(oldState, newState)
        return if (differences.isEmpty()) {
            "无变更"
        } else {
            "变更: ${differences.joinToString(", ")}"
        }
    }
}