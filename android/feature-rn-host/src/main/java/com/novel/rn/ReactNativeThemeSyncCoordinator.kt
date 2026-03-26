package com.novel.rn

class ReactNativeThemeSyncCoordinator {

    sealed interface ThemeSyncAction {
        data object Skip : ThemeSyncAction

        data class Dispatch(val theme: String) : ThemeSyncAction
    }

    fun resolveSyncAction(actualTheme: String?): ThemeSyncAction {
        val resolvedTheme = actualTheme?.trim().orEmpty()
        if (resolvedTheme.isEmpty()) {
            return ThemeSyncAction.Skip
        }

        return ThemeSyncAction.Dispatch(resolvedTheme)
    }

    fun syncActualTheme(
        actualTheme: String?,
        notifyThemeChanged: (String) -> Unit,
    ): Boolean {
        return when (val action = resolveSyncAction(actualTheme)) {
            ThemeSyncAction.Skip -> false
            is ThemeSyncAction.Dispatch -> {
                notifyThemeChanged(action.theme)
                true
            }
        }
    }
}
