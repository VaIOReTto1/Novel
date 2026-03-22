package com.novel.rn

internal class ReactNativeHostPathTraceCoordinator {

    fun formatContextTrace(
        trigger: String,
        hasReactContext: Boolean,
    ): String {
        val contextPath = if (hasReactContext) {
            "ALREADY_READY"
        } else {
            "FIRST_CREATE"
        }
        return "trigger=$trigger reactContextPath=$contextPath"
    }

    fun formatRootViewTrace(
        componentName: String,
        reused: Boolean,
        hasReactContext: Boolean,
    ): String {
        val rootViewPath = if (reused) {
            "REUSED"
        } else {
            "FIRST_CREATE"
        }
        val contextPath = if (hasReactContext) {
            "ALREADY_READY"
        } else {
            "FIRST_CREATE"
        }
        return "component=$componentName reactRootViewPath=$rootViewPath reactContextPath=$contextPath"
    }
}
