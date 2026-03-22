package com.novel.rn

internal class ReactNativeThemeSyncCoordinator {

    fun syncActualTheme(
        actualTheme: String?,
        notifyThemeChanged: (String) -> Unit,
    ): Boolean {
        val resolvedTheme = actualTheme?.trim().orEmpty()
        if (resolvedTheme.isEmpty()) {
            return false
        }

        notifyThemeChanged(resolvedTheme)
        return true
    }
}
