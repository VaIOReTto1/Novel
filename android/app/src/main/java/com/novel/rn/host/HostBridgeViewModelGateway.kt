package com.novel.rn.host

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.facebook.react.bridge.ReactApplicationContext
import com.novel.rn.bridge.BridgeViewModel
import com.novel.rn.settings.SettingsViewModel
import com.novel.utils.TimberLogger

internal interface HostBridgeViewModelGateway {
    fun getBridgeViewModelOrNull(owner: ViewModelStoreOwner?): BridgeViewModel?

    fun getSettingsViewModelOrNull(
        owner: ViewModelStoreOwner?,
        reactContext: ReactApplicationContext? = null,
    ): SettingsViewModel?
}

internal class DefaultHostBridgeViewModelGateway(
    private val bridgeViewModelFactory: (ViewModelStoreOwner) -> BridgeViewModel = { owner ->
        ViewModelProvider(owner)[BridgeViewModel::class.java]
    },
    private val settingsViewModelFactory: (ViewModelStoreOwner) -> SettingsViewModel = { owner ->
        ViewModelProvider(owner)[SettingsViewModel::class.java]
    },
) : HostBridgeViewModelGateway {

    companion object {
        private const val TAG = "HostBridgeViewModelGateway"
    }

    override fun getBridgeViewModelOrNull(owner: ViewModelStoreOwner?): BridgeViewModel? {
        if (owner == null) {
            return null
        }

        return runCatching {
            bridgeViewModelFactory(owner)
        }.getOrElse { error ->
            TimberLogger.e(TAG, "无法获取BridgeViewModel", error)
            null
        }
    }

    override fun getSettingsViewModelOrNull(
        owner: ViewModelStoreOwner?,
        reactContext: ReactApplicationContext?,
    ): SettingsViewModel? {
        if (owner == null) {
            return null
        }

        return runCatching {
            settingsViewModelFactory(owner).also { viewModel ->
                reactContext?.let(viewModel::initReactContext)
            }
        }.getOrElse { error ->
            TimberLogger.e(TAG, "无法获取SettingsViewModel", error)
            null
        }
    }
}
