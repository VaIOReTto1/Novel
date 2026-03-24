package com.novel.rn.host

internal class ReactNativeModuleRegistry<T>(
    private val moduleFactories: List<() -> T>,
) {

    fun createModules(): List<T> = moduleFactories.map { factory -> factory() }
}
