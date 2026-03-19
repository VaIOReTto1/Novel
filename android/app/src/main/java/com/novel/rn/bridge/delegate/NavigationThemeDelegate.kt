package com.novel.rn.bridge.delegate

import com.novel.rn.settings.SettingsEffect

sealed class NavigationThemeResult {
    data class Success(val message: String) : NavigationThemeResult()
    data class Failure(val errorCode: String, val errorMessage: String) : NavigationThemeResult()
}

class NavigationThemeDelegate {

    fun mapEffect(effect: SettingsEffect): NavigationThemeResult? {
        return when (effect) {
            is SettingsEffect.ShowToast -> NavigationThemeResult.Success(effect.message)
            is SettingsEffect.ShowError -> NavigationThemeResult.Failure(
                errorCode = "THEME_CHANGE_ERROR",
                errorMessage = effect.error
            )
            else -> null
        }
    }
}
