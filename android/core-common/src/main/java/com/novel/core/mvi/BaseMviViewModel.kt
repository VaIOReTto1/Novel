package com.novel.core.mvi

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novel.core.asStable
import com.novel.core.logging.CoreLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Stable
@OptIn(FlowPreview::class)
abstract class BaseMviViewModel<I : MviIntent, S : MviState, E : MviEffect> : ViewModel() {

    companion object {
        private const val TAG = "BaseMviViewModel"
        private val DEFAULT_DEBOUNCE_DURATION = 300.milliseconds
    }

    @Stable
    val stateMutex = Mutex()

    @Stable
    val intentChannel = Channel<I>(Channel.UNLIMITED)

    @Stable
    val _state = MutableStateFlow(createInitialState())

    @Stable
    val state: StateFlow<S> = _state.asStateFlow().asStable()

    @Stable
    val _effect = MutableSharedFlow<E>()

    @Stable
    val effect: SharedFlow<E> = _effect.asSharedFlow()

    @Stable
    val debouncedIntents = MutableSharedFlow<I>()

    init {
        CoreLogger.d(TAG, "初始化MVI ViewModel: ${this::class.simpleName}")

        viewModelScope.launch {
            intentChannel.receiveAsFlow()
                .onEach { intent ->
                    CoreLogger.d(TAG, "处理Intent: ${intent::class.simpleName} (id=${intent.id})")
                    processIntent(intent)
                }
                .launchIn(this)
        }

        viewModelScope.launch {
            debouncedIntents
                .debounce(getDebounceDuration())
                .onEach { intent ->
                    CoreLogger.d(TAG, "处理防抖Intent: ${intent::class.simpleName} (id=${intent.id})")
                    processIntent(intent)
                }
                .launchIn(this)
        }

        CoreLogger.d(TAG, "MVI ViewModel初始化完成，初始状态: ${_state.value}")
    }

    protected abstract fun createInitialState(): S

    protected abstract fun getReducer(): MviReducer<I, S>

    protected open fun getDebounceDuration(): Duration = DEFAULT_DEBOUNCE_DURATION

    fun sendIntent(intent: I) {
        CoreLogger.d(TAG, "发送Intent: ${intent::class.simpleName} (id=${intent.id})")
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    fun sendDebouncedIntent(intent: I) {
        CoreLogger.d(TAG, "发送防抖Intent: ${intent::class.simpleName} (id=${intent.id})")
        viewModelScope.launch {
            debouncedIntents.emit(intent)
        }
    }

    protected fun sendEffect(effect: E) {
        CoreLogger.d(TAG, "发送Effect: ${effect::class.simpleName} (id=${effect.id})")
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    protected fun updateState(newState: S) {
        val oldState = _state.value
        if (oldState != newState) {
            CoreLogger.d(
                TAG,
                "状态更新: ${oldState::class.simpleName} -> ${newState::class.simpleName} (版本: ${oldState.version} -> ${newState.version})"
            )
            _state.value = newState
        }
    }

    protected fun getCurrentState(): S = _state.value

    private suspend fun processIntent(intent: I) {
        stateMutex.withLock {
            try {
                val currentState = getCurrentState()
                val reducer = getReducer()

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

                onIntentProcessed(intent, getCurrentState())
            } catch (e: Exception) {
                CoreLogger.e(TAG, "Intent处理失败: ${intent::class.simpleName}", e)
                handleIntentError(intent, e)
            }
        }
    }

    protected open fun onIntentProcessed(intent: I, newState: S) {
        // 默认空实现
    }

    protected open fun handleIntentError(intent: I, error: Exception) {
        CoreLogger.e(TAG, "Intent处理错误，将被忽略: ${intent::class.simpleName}", error)
    }

    override fun onCleared() {
        super.onCleared()
        CoreLogger.d(TAG, "MVI ViewModel清理: ${this::class.simpleName}")
        intentChannel.close()
    }
}
