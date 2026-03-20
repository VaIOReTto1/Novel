package com.novel.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/**
 * 稳定回调工厂
 * 
 * 提供预定义的单例Lambda和记忆化回调创建工具，用于优化Compose性能：
 * - 单例Lambda避免每次重组都创建新对象
 * - remember包装函数缓存动态Lambda
 * - 类型安全的回调管理
 * 
 * 使用场景：
 * - 组件默认回调参数
 * - 事件处理函数缓存
 * - 导航回调优化
 */
@Stable
object StableCallbacks {
    
    // region 单例回调常量
    
    /** 空字符串回调 - 用于搜索、输入等场景 */
    @Stable
    val EmptyStringCallback: (String) -> Unit = { }
    
    /** 空Long回调 - 用于ID点击等场景 */
    @Stable
    val EmptyLongCallback: (Long) -> Unit = { }
    
    /** 空Unit回调 - 用于按钮点击等场景 */
    @Stable
    val EmptyUnitCallback: () -> Unit = { }
    
    /** 空Boolean回调 - 用于开关切换等场景 */
    @Stable
    val EmptyBooleanCallback: (Boolean) -> Unit = { }
    
    /** 空Int回调 - 用于索引选择等场景 */
    @Stable
    val EmptyIntCallback: (Int) -> Unit = { }
    
    // endregion
    
    // region 专业场景单例回调
    
    /** 默认导航到分类回调 */
    @Stable
    val DefaultNavigateToCategory: (Long) -> Unit = { }
    
    /** 默认书籍点击回调 */
    @Stable
    val DefaultOnBookClick: (String) -> Unit = { }
    
    /** 默认返回回调 */
    @Stable
    val DefaultOnBack: () -> Unit = { }
    
    /** 默认刷新回调 */
    @Stable
    val DefaultOnRefresh: () -> Unit = { }
    
    /** 默认加载更多回调 */
    @Stable
    val DefaultOnLoadMore: () -> Unit = { }
    
    /** 默认搜索回调 */
    @Stable
    val DefaultOnSearch: (String) -> Unit = { }
    
    /** 默认筛选回调 */
    @Stable
    val DefaultOnFilter: (String) -> Unit = { }
    
    // endregion
    
    // region 记忆化回调创建工具
    
    /**
     * 创建记忆化的单参数回调
     * 
     * @param key 缓存键，当key变化时重新创建回调
     * @param callback 实际的回调函数
     * @return 记忆化的回调函数
     */
    @Composable
    fun <T> rememberCallback(
        key: Any?,
        callback: (T) -> Unit
    ): (T) -> Unit = remember(key) { callback }
    
    /**
     * 创建记忆化的无参回调
     */
    @Composable
    fun rememberUnitCallback(
        key: Any?,
        callback: () -> Unit
    ): () -> Unit = remember(key) { callback }
    
    /**
     * 创建记忆化的双参数回调
     */
    @Composable
    fun <T, U> rememberCallback2(
        key: Any?,
        callback: (T, U) -> Unit
    ): (T, U) -> Unit = remember(key) { callback }
    
    /**
     * 创建记忆化的书籍点击回调
     * 专门为书籍列表优化
     */
    @Composable
    fun rememberBookClickCallback(
        bookId: String,
        onBookClick: (String) -> Unit
    ): () -> Unit = remember(bookId, onBookClick) { 
        { onBookClick(bookId) } 
    }
    
    /**
     * 创建记忆化的导航回调
     * 专门为页面导航优化
     */
    @Composable
    fun rememberNavigationCallback(
        targetId: String,
        onNavigate: (String) -> Unit
    ): () -> Unit = remember(targetId, onNavigate) { 
        { onNavigate(targetId) } 
    }
    
    /**
     * 创建记忆化的切换回调
     * 专门为开关状态优化
     */
    @Composable
    fun rememberToggleCallback(
        currentValue: Boolean,
        onToggle: (Boolean) -> Unit
    ): () -> Unit = remember(currentValue, onToggle) { 
        { onToggle(!currentValue) } 
    }
    
    // endregion
    
    // region 条件回调工具
    
    /**
     * 根据条件选择回调
     * 用于避免在Composable中进行条件判断
     */
    @Stable
    fun <T> conditionalCallback(
        condition: Boolean,
        trueCallback: (T) -> Unit,
        falseCallback: (T) -> Unit = EmptyCallback()
    ): (T) -> Unit = if (condition) trueCallback else falseCallback
    
    /**
     * 创建空回调
     * 泛型版本的空回调工厂
     */
    @Stable
    fun <T> EmptyCallback(): (T) -> Unit = { }
    
    // endregion
}

/**
 * 扩展函数：为任意函数类型创建稳定包装
 */
@Stable
fun <T> ((T) -> Unit).asStable(): (T) -> Unit = this

@Stable  
fun (() -> Unit).asStable(): () -> Unit = this

/**
 * 扩展函数：为组件参数提供默认稳定回调
 */
@Composable
fun <T> ((T) -> Unit)?.orEmpty(): (T) -> Unit =
    this ?: StableCallbacks.EmptyCallback()

@Composable
fun (() -> Unit)?.orEmpty(): () -> Unit =
    this ?: StableCallbacks.EmptyUnitCallback 
