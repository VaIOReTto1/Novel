package com.novel.core.mvi

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novel.core.asStable
import com.novel.utils.TimberLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * MVI架构的基础ViewModel
 * 
 * 提供完整的MVI实现，包括：
 * - StateFlow状态管理
 * - SharedFlow副作用管理  
 * - Intent处理队列
 * - 防抖机制
 * - 生命周期安全的协程作用域
 * - 状态变化日志记录
 * 
 * @param I Intent类型
 * @param S State类型
 * @param E Effect类型
 */
@Stable
abstract class BaseMviViewModel<I : MviIntent, S : MviState, E : MviEffect> : ViewModel() {

    companion object {
        private const val TAG = "BaseMviViewModel"
        private val DEFAULT_DEBOUNCE_DURATION = 300.milliseconds
    }

    /** Intent处理的互斥锁，确保状态变更的原子性 */
    @Stable
    val stateMutex = Mutex()
    
    /** Intent处理队列 */
    @Stable
    val intentChannel = Channel<I>(Channel.UNLIMITED)
    
    /** 内部状态流 */
    @Stable
    val _state = MutableStateFlow(createInitialState())
    
    /** 对外暴露的状态流，只读 */
    @Stable
    val state: StateFlow<S> = _state.asStateFlow().asStable()
    
    /** 内部副作用流 */
    @Stable
    val _effect = MutableSharedFlow<E>()
    
    /** 对外暴露的副作用流，只读 */
    @Stable
    val effect: SharedFlow<E> = _effect.asSharedFlow()
    
    /** Intent防抖处理 */
    @Stable
    val debouncedIntents = MutableSharedFlow<I>()
    
    init {
        TimberLogger.d(TAG, "初始化MVI ViewModel: ${this::class.simpleName}")
        
        // 启动Intent处理协程
        viewModelScope.launch {
            intentChannel.receiveAsFlow()
                .onEach { intent ->
                    TimberLogger.d(TAG, "处理Intent: ${intent::class.simpleName} (id=${intent.id})")
                    processIntent(intent)
                }
                .launchIn(this)
        }
        
        // 启动防抖Intent处理协程
        viewModelScope.launch {
            debouncedIntents
                .debounce(getDebounceDuration())
                .onEach { intent ->
                    TimberLogger.d(TAG, "处理防抖Intent: ${intent::class.simpleName} (id=${intent.id})")
                    processIntent(intent)
                }
                .launchIn(this)
        }
        
        TimberLogger.d(TAG, "MVI ViewModel初始化完成，初始状态: ${_state.value}")
    }
    
    /**
     * 创建初始状态
     * 子类必须实现此方法
     */
    protected abstract fun createInitialState(): S
    
    /**
     * 获取Reducer实例
     * 子类必须实现此方法
     */
    protected abstract fun getReducer(): MviReducer<I, S>
    
    /**
     * 获取防抖时长
     * 子类可覆盖此方法自定义防抖时长
     */
    protected open fun getDebounceDuration(): Duration = DEFAULT_DEBOUNCE_DURATION
    
    /**
     * 发送Intent
     * 立即处理，不经过防抖
     * 
     * @param intent 要处理的Intent
     */
    fun sendIntent(intent: I) {
        TimberLogger.d(TAG, "发送Intent: ${intent::class.simpleName} (id=${intent.id})")
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }
    
    /**
     * 发送防抖Intent
     * 经过防抖处理，适用于用户输入等高频事件
     * 
     * @param intent 要处理的Intent
     */
    fun sendDebouncedIntent(intent: I) {
        TimberLogger.d(TAG, "发送防抖Intent: ${intent::class.simpleName} (id=${intent.id})")
        viewModelScope.launch {
            debouncedIntents.emit(intent)
        }
    }
    
    /**
     * 发送副作用
     * 
     * @param effect 要发送的副作用
     */
    protected fun sendEffect(effect: E) {
        TimberLogger.d(TAG, "发送Effect: ${effect::class.simpleName} (id=${effect.id})")
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
    
    /**
     * 更新状态
     * 
     * @param newState 新状态
     */
    protected fun updateState(newState: S) {
        val oldState = _state.value
        if (oldState != newState) {
            TimberLogger.d(TAG, "状态更新: ${oldState::class.simpleName} -> ${newState::class.simpleName} (版本: ${oldState.version} -> ${newState.version})")
            _state.value = newState
        }
    }
    
    /**
     * 获取当前状态
     */
    protected fun getCurrentState(): S = _state.value
    
    /**
     * 处理Intent的核心逻辑
     */
    private suspend fun processIntent(intent: I) {
        stateMutex.withLock {
            try {
                val currentState = getCurrentState()
                val reducer = getReducer()
                
                // 检查是否为支持副作用的Reducer
                when (reducer) {
                    is MviReducerWithEffect<*, *, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        val effectReducer = reducer as MviReducerWithEffect<I, S, E>
                        val result = effectReducer.reduce(currentState, intent)
                        updateState(result.newState)
                        result.effect?.let { sendEffect(it) }
                    }
                    else -> {
                        val newState = reducer.reduce(currentState, intent)
                        updateState(newState)
                    }
                }
                
                // 处理完成后的回调
                onIntentProcessed(intent, getCurrentState())
                
            } catch (e: Exception) {
                TimberLogger.e(TAG, "Intent处理失败: ${intent::class.simpleName}", e)
                handleIntentError(intent, e)
            }
        }
    }
    
    /**
     * Intent处理完成后的回调
     * 子类可覆盖此方法进行额外处理
     * 
     * @param intent 已处理的Intent
     * @param newState 处理后的新状态
     */
    protected open fun onIntentProcessed(intent: I, newState: S) {
        // 默认空实现
    }
    
    /**
     * Intent处理错误的回调
     * 子类可覆盖此方法进行错误处理
     * 
     * @param intent 处理失败的Intent
     * @param error 错误信息
     */
    protected open fun handleIntentError(intent: I, error: Exception) {
        TimberLogger.e(TAG, "Intent处理错误，将被忽略: ${intent::class.simpleName}", error)
    }
    
    override fun onCleared() {
        super.onCleared()
        TimberLogger.d(TAG, "MVI ViewModel清理: ${this::class.simpleName}")
        intentChannel.close()
    }
}
