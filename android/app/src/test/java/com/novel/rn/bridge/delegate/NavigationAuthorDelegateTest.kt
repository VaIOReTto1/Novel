package com.novel.rn.bridge.delegate

import com.novel.utils.network.api.author.AuthorService
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationAuthorDelegateTest {

    @Test
    fun buildBecomeWriterRoute_mapsBooleanFlag() {
        val delegate = NavigationAuthorDelegate(
            navigateToRoute = {},
            navigateToWritePage = {},
            navigateToBookManage = {}
        )

        assertEquals("becomewriter?isAuthor=true", delegate.buildBecomeWriterRoute(true))
        assertEquals("becomewriter?isAuthor=false", delegate.buildBecomeWriterRoute(false))
    }

    @Test
    fun createAuthorRegisterRequest_keepsExistingFieldSemantics() {
        val delegate = NavigationAuthorDelegate(
            navigateToRoute = {},
            navigateToWritePage = {},
            navigateToBookManage = {}
        )

        val request = delegate.createAuthorRegisterRequest(
            penName = "violet",
            sex = 1
        )

        assertEquals(
            AuthorService.AuthorRegisterRequest(
                penName = "violet",
                telPhone = "violet",
                chatAccount = "violet",
                email = "violet@163.com",
                workDirection = 1
            ),
            request
        )
    }

    @Test
    fun navigateToBecomeWriterWithFlag_routesThroughDelegate() {
        val recorder = mutableListOf<String>()
        val delegate = NavigationAuthorDelegate(
            navigateToRoute = { route -> recorder += route },
            navigateToWritePage = {},
            navigateToBookManage = {}
        )

        delegate.navigateToBecomeWriterWithFlag(false)

        assertEquals(listOf("becomewriter?isAuthor=false"), recorder)
    }

    @Test
    fun navigateToWritePageAndBookManage_useProvidedActions() {
        var writeCalls = 0
        var bookManageCalls = 0
        val delegate = NavigationAuthorDelegate(
            navigateToRoute = {},
            navigateToWritePage = { writeCalls += 1 },
            navigateToBookManage = { bookManageCalls += 1 }
        )

        delegate.navigateToWritePage()
        delegate.navigateToBookManage()

        assertEquals(1, writeCalls)
        assertEquals(1, bookManageCalls)
    }
}
