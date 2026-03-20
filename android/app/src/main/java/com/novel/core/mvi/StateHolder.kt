//package com.novel.core.mvi
//
//import com.novel.utils.TimberLogger
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
///**
// * 状态容器接口
// *
// * 提供状态的存储、持久化和恢复功能
// */
////interface StateHolder<S : MviState> {
////
////    /** 当前状态流 */
////    val currentState: StateFlow<S>
////
////    /** 更新状态 */
////    fun updateState(newState: S)
////
////    /** 保存状态到持久化存储 */
////    suspend fun saveState()
////
////    /** 从持久化存储恢复状态 */
////    suspend fun restoreState(): S?
////
////    /** 清除持久化状态 */
////    suspend fun clearPersistedState()
////}
//
///**
// * 默认状态容器实现
// *
// * @param initialState 初始状态
// * @param persistor 状态持久化器（可选）
// */
////class StateHolderImpl<S : MviState>(
////    initialState: S,
////    private val persistor: StatePersistor<S>? = null
////) : StateHolder<S> {
////
////    companion object {
////        private const val TAG = "StateHolder"
////    }
////
////    private val _state = MutableStateFlow(initialState)
////    override val currentState: StateFlow<S> = _state.asStateFlow()
////
////    override fun updateState(newState: S) {
////        val oldState = _state.value
////        if (oldState != newState) {
////            TimberLogger.d(TAG, "状态更新: 版本 ${oldState.version} -> ${newState.version}")
////            _state.value = newState
////        }
////    }
////
////    override suspend fun saveState() {
////        persistor?.let {
////            try {
////                it.saveState(_state.value)
////                TimberLogger.d(TAG, "状态已保存到持久化存储")
////            } catch (e: Exception) {
////                TimberLogger.e(TAG, "状态保存失败", e)
////            }
////        }
////    }
////
////    override suspend fun restoreState(): S? {
////        return persistor?.let {
////            try {
////                val restoredState = it.restoreState()
////                if (restoredState != null) {
////                    TimberLogger.d(TAG, "状态已从持久化存储恢复")
////                    _state.value = restoredState
////                }
////                restoredState
////            } catch (e: Exception) {
////                TimberLogger.e(TAG, "状态恢复失败", e)
////                null
////            }
////        }
////    }
////
////    override suspend fun clearPersistedState() {
////        persistor?.let {
////            try {
////                it.clearState()
////                TimberLogger.d(TAG, "持久化状态已清除")
////            } catch (e: Exception) {
////                TimberLogger.e(TAG, "持久化状态清除失败", e)
////            }
////        }
////    }
////}
//
///**
// * 状态持久化接口
// *
// * 定义状态的持久化和恢复策略
// */
////interface StatePersistor<S : MviState> {
////
////    /** 保存状态 */
////    suspend fun saveState(state: S)
////
////    /** 恢复状态 */
////    suspend fun restoreState(): S?
////
////    /** 清除状态 */
////    suspend fun clearState()
////}
//
///**
// * 副作用处理器
// *
// * 负责处理一次性副作用的分发和执行
// */
//interface EffectHandler<E : MviEffect> {
//
//    /** 处理副作用 */
//    suspend fun handleEffect(effect: E)
//}
//
///**
// * 默认副作用处理器实现
// */
//class EffectHandlerImpl<E : MviEffect> : EffectHandler<E> {
//
//    companion object {
//        private const val TAG = "EffectHandler"
//    }
//
//    override suspend fun handleEffect(effect: E) {
//        TimberLogger.d(TAG, "处理副作用: ${effect::class.simpleName} (id=${effect.id})")
//        // 具体的副作用处理逻辑由子类实现
//    }
//}
//
///**
// * 状态时间旅行调试工具（仅Debug版本可用）
// *
// * 记录状态变更历史，支持状态回溯和重放
// */
//class StateTimeTravel<S : MviState>(
//    private val maxHistorySize: Int = 50
//) {
//
//    companion object {
//        private const val TAG = "StateTimeTravel"
//    }
//
//    private val stateHistory = mutableListOf<StateSnapshot<S>>()
//    private var currentIndex = -1
//
//    /**
//     * 状态快照
//     */
//    data class StateSnapshot<S : MviState>(
//        val state: S,
//        val timestamp: Long,
//        val intentClass: String?
//    )
//
//    /**
//     * 记录状态变更
//     */
//    fun recordState(state: S, intentClass: String? = null) {
//        if (com.novel.BuildConfig.DEBUG) {
//            val snapshot = StateSnapshot(
//                state = state,
//                timestamp = System.currentTimeMillis(),
//                intentClass = intentClass
//            )
//
//            // 如果当前不在历史末尾，删除后面的记录
//            if (currentIndex < stateHistory.size - 1) {
//                stateHistory.subList(currentIndex + 1, stateHistory.size).clear()
//            }
//
//            stateHistory.add(snapshot)
//            currentIndex = stateHistory.size - 1
//
//            // 保持历史大小限制
//            if (stateHistory.size > maxHistorySize) {
//                stateHistory.removeAt(0)
//                currentIndex--
//            }
//
//            TimberLogger.d(TAG, "记录状态快照: ${state::class.simpleName} (历史大小: ${stateHistory.size})")
//        }
//    }
//
//    /**
//     * 撤销到上一个状态
//     */
//    fun undo(): S? {
//        return if (com.novel.BuildConfig.DEBUG && canUndo()) {
//            currentIndex--
//            val snapshot = stateHistory[currentIndex]
//            TimberLogger.d(TAG, "撤销到状态: ${snapshot.state::class.simpleName}")
//            snapshot.state
//        } else null
//    }
//
//    /**
//     * 重做到下一个状态
//     */
//    fun redo(): S? {
//        return if (com.novel.BuildConfig.DEBUG && canRedo()) {
//            currentIndex++
//            val snapshot = stateHistory[currentIndex]
//            TimberLogger.d(TAG, "重做到状态: ${snapshot.state::class.simpleName}")
//            snapshot.state
//        } else null
//    }
//
//    /**
//     * 是否可以撤销
//     */
//    fun canUndo(): Boolean = com.novel.BuildConfig.DEBUG && currentIndex > 0
//
//    /**
//     * 是否可以重做
//     */
//    fun canRedo(): Boolean = com.novel.BuildConfig.DEBUG && currentIndex < stateHistory.size - 1
//
//    /**
//     * 获取状态历史
//     */
//    fun getHistory(): List<StateSnapshot<S>> = if (com.novel.BuildConfig.DEBUG) {
//        stateHistory.toList()
//    } else emptyList()
//
//    /**
//     * 清除历史
//     */
//    fun clearHistory() {
//        if (com.novel.BuildConfig.DEBUG) {
//            stateHistory.clear()
//            currentIndex = -1
//            TimberLogger.d(TAG, "状态历史已清除")
//        }
//    }
//}
