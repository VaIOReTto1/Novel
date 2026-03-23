package com.novel.rn.bridge

import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class BridgeStateAdapterTest {

    @Test
    fun canNavigate_returnsTrue_whenBridgeReadyAndNoCacheOperation() {
        val adapter = BridgeStateAdapter(
            MutableStateFlow(
                BridgeState(
                    isBridgeInitialized = true,
                    isLoading = false,
                    isCacheOperationInProgress = false,
                ),
            ),
        )

        assertThat(adapter.canNavigate()).isTrue()
        assertThat(adapter.canPerformCacheOperation()).isTrue()
    }

    @Test
    fun getBridgeStatusSummary_includesRouteAndCachedComponentCount() {
        val adapter = BridgeStateAdapter(
            MutableStateFlow(
                BridgeState(
                    isBridgeInitialized = true,
                    currentRoute = "profile",
                    cachedComponents = persistentSetOf("ProfilePage", "SettingsPage"),
                    isCacheOperationInProgress = true,
                ),
            ),
        )

        val summary = adapter.getBridgeStatusSummary()

        assertThat(summary).contains("桥接: 已初始化")
        assertThat(summary).contains("缓存操作中")
        assertThat(summary).contains("当前路由: profile")
        assertThat(summary).contains("缓存组件: 2个")
    }
}
