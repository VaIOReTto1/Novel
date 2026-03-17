package com.novel.rn.bridge

import com.novel.page.read.service.common.DispatcherProvider
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

class BridgeCoroutineScopesTest {

    @Test
    fun returnsStableScopeInstances() {
        val scopes = BridgeCoroutineScopes(FakeDispatcherProvider())

        assertSame(scopes.io, scopes.io)
        assertSame(scopes.main, scopes.main)
    }

    @Test
    fun ioAndMainScopesAreDistinct() {
        val scopes = BridgeCoroutineScopes(FakeDispatcherProvider())

        assertNotSame(scopes.io, scopes.main)
    }

    private class FakeDispatcherProvider : DispatcherProvider {
        override val io = Dispatchers.Unconfined
        override val default = Dispatchers.Unconfined
        override val main = Dispatchers.Unconfined
        override val unconfined = Dispatchers.Unconfined
    }
}
