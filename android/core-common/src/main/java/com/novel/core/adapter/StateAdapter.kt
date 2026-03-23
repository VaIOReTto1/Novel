package com.novel.core.adapter

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.novel.core.asStable
import com.novel.core.common.BuildConfig
import com.novel.core.logging.CoreLogger
import com.novel.core.mvi.MviState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Stable
abstract class StateAdapter<S : MviState>(
    @Stable val stateFlow: StateFlow<S>
) {

    companion object {
        private const val TAG = "StateAdapter"
    }

    @Stable
    val currentState: StateFlow<S> = stateFlow.asStable()

    @Stable
    val isLoading: Flow<Boolean> = stateFlow.asStable()
        .map { it.isLoading }
        .distinctUntilChanged()
        .asStable()

    @Stable
    val error: Flow<String?> = stateFlow.asStable()
        .map { it.error }
        .distinctUntilChanged()
        .asStable()

    @Stable
    val hasError: Flow<Boolean> = stateFlow.asStable()
        .map { it.hasError }
        .distinctUntilChanged()
        .asStable()

    @Stable
    val isEmpty: Flow<Boolean> = stateFlow.asStable()
        .map { it.isEmpty }
        .distinctUntilChanged()
        .asStable()

    @Stable
    val isSuccess: Flow<Boolean> = stateFlow.asStable()
        .map { it.isSuccess }
        .distinctUntilChanged()
        .asStable()

    @Stable
    val version: Flow<Long> = stateFlow.asStable()
        .map { it.version }
        .distinctUntilChanged()
        .asStable()

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun <T> createStableState(
        selector: (S) -> T
    ): State<T> {
        val mappedFlow = remember(stateFlow, selector) {
            stateFlow.map(selector)
        }

        return mappedFlow.collectAsStateWithLifecycle(
            initialValue = selector(stateFlow.value)
        )
    }

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

    @Composable
    fun createLoadingState(): State<Boolean> =
        createStableState { it.isLoading }

    @Composable
    fun createErrorState(): State<String?> =
        createStableState { it.error }

    @Composable
    fun createSuccessState(): State<Boolean> =
        createStableState { it.isSuccess }

    fun getCurrentSnapshot(): S = stateFlow.value

    fun isCurrentlyLoading(): Boolean = getCurrentSnapshot().isLoading

    fun getCurrentError(): String? = getCurrentSnapshot().error

    fun hasCurrentError(): Boolean = getCurrentSnapshot().hasError

    fun isCurrentlyEmpty(): Boolean = getCurrentSnapshot().isEmpty

    fun isCurrentlySuccess(): Boolean = getCurrentSnapshot().isSuccess

    fun getCurrentVersion(): Long = getCurrentSnapshot().version

    @Stable
    inline fun <T> mapState(
        crossinline selector: (S) -> T
    ): Flow<T> = stateFlow
        .map { selector(it) }
        .distinctUntilChanged()
        .asStable()

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

    @Stable
    inline fun <T> combineState(
        crossinline combiner: (S) -> T
    ): Flow<T> = stateFlow
        .map { combiner(it) }
        .distinctUntilChanged()
        .asStable()

    @Stable
    inline fun createConditionFlow(
        crossinline condition: (S) -> Boolean
    ): Flow<Boolean> = stateFlow
        .map { condition(it) }
        .distinctUntilChanged()
        .asStable()

    fun logStateChange(message: String) {
        if (BuildConfig.DEBUG) {
            val state = getCurrentSnapshot()
            CoreLogger.d(
                TAG,
                "$message - 版本: ${state.version}, 加载: ${state.isLoading}, 错误: ${state.hasError}"
            )
        }
    }

    fun getStateSummary(): String {
        val state = getCurrentSnapshot()
        return "StateAdapter(${this::class.simpleName}): 版本=${state.version}, 加载=${state.isLoading}, 错误=${state.hasError}, 空=${state.isEmpty}"
    }
}

inline fun <S : MviState, A : StateAdapter<S>> StateFlow<S>.createAdapter(
    adapterFactory: (StateFlow<S>) -> A
): A = adapterFactory(this)

fun <S : MviState> StateAdapter<S>.onStateChange(
    action: (S) -> Unit
): Flow<S> = currentState
    .map { state ->
        action(state)
        state
    }

class StateUpdateListener<S : MviState>(
    private val adapter: StateAdapter<S>
) {

    fun onLoadingChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isLoading.map { loading ->
            action(loading)
            loading
        }
    }

    fun onErrorChanged(action: (String?) -> Unit): Flow<String?> {
        return adapter.error.map { currentError ->
            action(currentError)
            currentError
        }
    }

    fun onSuccessChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isSuccess.map { success ->
            action(success)
            success
        }
    }
}

fun <S : MviState> StateAdapter<S>.createUpdateListener(): StateUpdateListener<S> {
    return StateUpdateListener(this)
}

object StateComparator {

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

    fun <S : MviState> getChangeSummary(oldState: S, newState: S): String {
        val differences = compareBasicFields(oldState, newState)
        return if (differences.isEmpty()) {
            "无变更"
        } else {
            "变更: ${differences.joinToString(", ")}"
        }
    }
}
