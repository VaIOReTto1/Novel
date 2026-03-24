package com.novel

import java.util.concurrent.ConcurrentHashMap

internal class ReactRootViewCacheStore<T> {

    private val cache = ConcurrentHashMap<String, T>()

    fun peek(key: String): T? = cache[key]

    fun getOrCreate(key: String, create: () -> T): T {
        return cache[key] ?: synchronized(this) {
            cache[key] ?: create().also { cache[key] = it }
        }
    }

    fun remove(key: String): T? = cache.remove(key)

    fun clear() {
        cache.clear()
    }
}
