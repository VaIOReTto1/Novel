package com.novel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReactRootViewCacheStoreTest {

    @Test
    fun `getOrCreate reuses existing value for same key`() {
        val store = ReactRootViewCacheStore<Any>()
        val first = Any()
        val second = Any()

        val cachedFirst = store.getOrCreate("profile") { first }
        val cachedSecond = store.getOrCreate("profile") { second }

        assertThat(cachedFirst === first).isTrue()
        assertThat(cachedSecond === first).isTrue()
    }

    @Test
    fun `remove clears only target key`() {
        val store = ReactRootViewCacheStore<Any>()
        val profile = store.getOrCreate("profile") { Any() }
        val settings = store.getOrCreate("settings") { Any() }

        val removed = store.remove("profile")

        assertThat(removed === profile).isTrue()
        assertThat(store.peek("profile")).isNull()
        assertThat(store.peek("settings") === settings).isTrue()
    }

    @Test
    fun `clear removes all cached values`() {
        val store = ReactRootViewCacheStore<Any>()
        store.getOrCreate("profile") { Any() }
        store.getOrCreate("settings") { Any() }

        store.clear()

        assertThat(store.peek("profile")).isNull()
        assertThat(store.peek("settings")).isNull()
    }
}
