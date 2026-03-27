package com.novel.rn

class ReactNativeHostPathTraceCoordinator {

    private enum class ReactRootViewPath(val value: String) {
        COLD_OPEN("COLD_OPEN"),
        OPEN("OPEN"),
        REUSED("REUSED"),
    }

    fun formatContextTrace(
        trigger: String,
        hasReactContext: Boolean,
    ): String {
        val contextPath = if (hasReactContext) {
            "ALREADY_READY"
        } else {
            "COLD_OPEN"
        }
        return "trigger=$trigger reactContextPath=$contextPath"
    }

    fun formatRootViewTrace(
        componentName: String,
        reused: Boolean,
        hasReactContext: Boolean,
    ): String {
        val rootViewPath = resolveRootViewPath(
            reused = reused,
            hasReactContext = hasReactContext,
        ).value
        val contextPath = if (hasReactContext) {
            "ALREADY_READY"
        } else {
            "COLD_OPEN"
        }
        return "component=$componentName reactRootViewPath=$rootViewPath reactContextPath=$contextPath"
    }

    private fun resolveRootViewPath(
        reused: Boolean,
        hasReactContext: Boolean,
    ): ReactRootViewPath {
        return when {
            reused -> ReactRootViewPath.REUSED
            hasReactContext -> ReactRootViewPath.OPEN
            else -> ReactRootViewPath.COLD_OPEN
        }
    }
}
