package com.novel.rn

class ReactNativeThemeSyncCoordinator {

    sealed interface ThemeSyncAction {
        data object Skip : ThemeSyncAction

        data class Dispatch(val theme: String) : ThemeSyncAction
    }

    fun resolveSyncAction(
        actualTheme: String?,
        fallbackTheme: String? = null,
        preferFallbackTheme: Boolean = false,
    ): ThemeSyncAction {
        val resolvedTheme = actualTheme?.trim().orEmpty()
        val resolvedFallbackTheme = fallbackTheme?.trim().orEmpty()
        val candidate = when {
            preferFallbackTheme && resolvedFallbackTheme.isNotEmpty() -> resolvedFallbackTheme
            resolvedTheme.isNotEmpty() -> resolvedTheme
            resolvedFallbackTheme.isNotEmpty() -> resolvedFallbackTheme
            else -> ""
        }
        if (candidate.isEmpty()) {
            return ThemeSyncAction.Skip
        }

        return ThemeSyncAction.Dispatch(candidate)
    }

    fun syncActualTheme(
        actualTheme: String?,
        notifyThemeChanged: (String) -> Unit,
    ): Boolean {
        return when (val action = resolveSyncAction(actualTheme = actualTheme)) {
            ThemeSyncAction.Skip -> false
            is ThemeSyncAction.Dispatch -> {
                notifyThemeChanged(action.theme)
                true
            }
        }
    }
}
