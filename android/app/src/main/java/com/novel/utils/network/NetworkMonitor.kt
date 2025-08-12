package com.novel.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Stable
import com.novel.core.asStable
import com.novel.utils.TimberLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络类型枚举
 */
enum class NetworkType {
    WIFI,           // Wi-Fi网络
    CELLULAR,       // 蜂窝网络
    ETHERNET,       // 以太网
    NONE           // 无网络
}

/**
 * 网络状态信息
 */
@Stable
data class NetworkState(
    val type: NetworkType,
    val isConnected: Boolean,
    val isMetered: Boolean,        // 是否按流量计费
    val linkDownstreamBandwidth: Int = 0,  // 下行带宽 Kbps
    val linkUpstreamBandwidth: Int = 0,    // 上行带宽 Kbps
    val signalStrength: Int = -1   // 信号强度 (-1 表示未知)
)

/**
 * 网络监控器
 *
 * 功能：
 * - 监听网络状态变化
 * - 提供响应式的网络状态流
 * - 支持防抖动处理
 * - 提供网络质量评估
 */
@Stable
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "NetworkMonitor"
        private const val DEBOUNCE_TIMEOUT_MS = 300L // 防抖动时间
    }

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // 内部状态流
    private val _networkState = MutableStateFlow(getCurrentNetworkState())

    // 公开的状态流，带防抖动处理
    private val _processedNetworkState = _networkState
        .debounce(DEBOUNCE_TIMEOUT_MS)
        .distinctUntilChanged()
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _networkState.value
        )
    val networkState: StateFlow<NetworkState> = _processedNetworkState

    // 简化的网络类型流，向后兼容
    private val _networkType = MutableStateFlow(_networkState.value.type)
    val networkType: StateFlow<NetworkType> = _networkType.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            TimberLogger.d(TAG, "网络连接可用: $network")
            updateNetworkState()
        }

        override fun onLost(network: Network) {
            TimberLogger.d(TAG, "网络连接丢失: $network")
            updateNetworkState()
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            TimberLogger.d(TAG, "网络能力变化: $network")
            updateNetworkState()
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: android.net.LinkProperties) {
            TimberLogger.d(TAG, "网络链路属性变化: $network")
            updateNetworkState()
        }
    }

    init {
        startMonitoring()
        TimberLogger.d(TAG, "网络监控器初始化完成: ${_networkState.value}")
    }

    /**
     * 开始监控网络状态
     */
    private fun startMonitoring() {
        try {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            TimberLogger.d(TAG, "网络监控已启动")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "启动网络监控失败", e)
        }
    }

    /**
     * 停止监控网络状态
     */
    fun stopMonitoring() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            TimberLogger.d(TAG, "网络监控已停止")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "停止网络监控失败", e)
        }
    }

    /**
     * 更新网络状态
     */
    private fun updateNetworkState() {
        CoroutineScope(Dispatchers.IO).launch {
            val newState = getCurrentNetworkState()
            if (newState != _networkState.value) {
                _networkState.value = newState
                _networkType.value = newState.type
                TimberLogger.d(TAG, "网络状态更新: $newState")
            }
        }
    }

    /**
     * 获取当前网络状态
     */
    private fun getCurrentNetworkState(): NetworkState {
        return try {
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork == null) {
                NetworkState(
                    type = NetworkType.NONE,
                    isConnected = false,
                    isMetered = false
                )
            } else {
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                val networkInfo = connectivityManager.getNetworkInfo(activeNetwork)

                val networkType = when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkType.WIFI
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkType.CELLULAR
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> NetworkType.ETHERNET
                    else -> NetworkType.NONE
                }

                val isConnected = networkInfo?.isConnected == true
                val isMetered = connectivityManager.isActiveNetworkMetered

                // 获取带宽信息
                val downstreamBandwidth = capabilities?.linkDownstreamBandwidthKbps ?: 0
                val upstreamBandwidth = capabilities?.linkUpstreamBandwidthKbps ?: 0

                // 获取信号强度（仅对蜂窝网络）
                val signalStrength = if (networkType == NetworkType.CELLULAR) {
                    capabilities?.signalStrength ?: -1
                } else -1

                NetworkState(
                    type = networkType,
                    isConnected = isConnected,
                    isMetered = isMetered,
                    linkDownstreamBandwidth = downstreamBandwidth,
                    linkUpstreamBandwidth = upstreamBandwidth,
                    signalStrength = signalStrength
                )
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取网络状态失败", e)
            NetworkState(
                type = NetworkType.NONE,
                isConnected = false,
                isMetered = false
            )
        }
    }

    /**
     * 获取当前网络类型（同步方法，向后兼容）
     */
    fun getCurrentNetworkType(): NetworkType {
        return _networkState.value.type
    }

    /**
     * 检查是否有网络连接
     */
    fun isConnected(): Boolean {
        return _networkState.value.isConnected
    }

    /**
     * 检查是否为按流量计费网络
     */
    fun isMetered(): Boolean {
        return _networkState.value.isMetered
    }

    /**
     * 检查是否为高速网络（Wi-Fi或高速蜂窝网络）
     */
    fun isHighSpeed(): Boolean {
        val state = _networkState.value
        return when (state.type) {
            NetworkType.WIFI, NetworkType.ETHERNET -> true
            NetworkType.CELLULAR -> {
                // 基于下行带宽判断是否为高速网络
                state.linkDownstreamBandwidth > 5000 // 5Mbps
            }
            NetworkType.NONE -> false
        }
    }

    /**
     * 获取网络质量评分（0-100）
     */
    fun getNetworkQuality(): Int {
        val state = _networkState.value

        if (!state.isConnected) return 0

        return when (state.type) {
            NetworkType.WIFI, NetworkType.ETHERNET -> {
                // Wi-Fi基于带宽评分
                when {
                    state.linkDownstreamBandwidth >= 50000 -> 100  // >=50Mbps
                    state.linkDownstreamBandwidth >= 10000 -> 80   // >=10Mbps
                    state.linkDownstreamBandwidth >= 5000 -> 60    // >=5Mbps
                    state.linkDownstreamBandwidth >= 1000 -> 40    // >=1Mbps
                    else -> 20
                }
            }
            NetworkType.CELLULAR -> {
                // 蜂窝网络基于信号强度和带宽评分
                val bandwidthScore = when {
                    state.linkDownstreamBandwidth >= 20000 -> 50  // 5G
                    state.linkDownstreamBandwidth >= 5000 -> 40   // 4G+
                    state.linkDownstreamBandwidth >= 1000 -> 30   // 4G
                    state.linkDownstreamBandwidth >= 100 -> 20    // 3G
                    else -> 10                                   // 2G
                }

                val signalScore = when (state.signalStrength) {
                    in -70..-1 -> 25      // 强信号
                    in -85..-71 -> 20     // 中等信号
                    in -100..-86 -> 15    // 弱信号
                    else -> 10           // 很弱信号
                }

                bandwidthScore + signalScore
            }
            NetworkType.NONE -> 0
        }
    }

    /**
     * 获取推荐的并发连接数
     */
    fun getRecommendedConcurrency(): Int {
        val quality = getNetworkQuality()
        val state = _networkState.value

        return when {
            !state.isConnected -> 0
            state.type == NetworkType.WIFI -> {
                when {
                    quality >= 80 -> 8
                    quality >= 60 -> 6
                    quality >= 40 -> 4
                    else -> 2
                }
            }
            state.type == NetworkType.CELLULAR -> {
                if (state.isMetered) {
                    // 按流量计费网络限制并发数
                    when {
                        quality >= 60 -> 3
                        quality >= 40 -> 2
                        else -> 1
                    }
                } else {
                    when {
                        quality >= 60 -> 4
                        quality >= 40 -> 3
                        else -> 2
                    }
                }
            }
            else -> 2
        }
    }

    /**
     * 获取推荐的超时配置
     */
    fun getRecommendedTimeouts(): TimeoutConfig {
        val state = _networkState.value
        val quality = getNetworkQuality()

        return when (state.type) {
            NetworkType.WIFI, NetworkType.ETHERNET -> {
                // AI 接口较慢，整体放宽默认网络超时建议
                TimeoutConfig(
                    connectTimeout = 10_000,
                    readTimeout = 60_000,
                    callTimeout = 90_000
                )
            }
            NetworkType.CELLULAR -> {
                when {
                    quality >= 60 -> TimeoutConfig(12_000, 75_000, 110_000)  // 4G+
                    quality >= 30 -> TimeoutConfig(15_000, 90_000, 130_000)  // 4G
                    else -> TimeoutConfig(18_000, 120_000, 160_000)          // 3G/2G
                }
            }
            NetworkType.NONE -> TimeoutConfig(0, 0, 0) // 无网络
        }
    }
}

/**
 * 超时配置
 */
@Stable
data class TimeoutConfig(
    val connectTimeout: Long,  // 连接超时（毫秒）
    val readTimeout: Long,     // 读取超时（毫秒）
    val callTimeout: Long      // 整体调用超时（毫秒）
)