package com.novel.rn.bridge.delegate

import com.novel.utils.network.api.author.AuthorService

class NavigationAuthorDelegate(
    private val navigateToRoute: (String) -> Unit,
    private val navigateToWritePage: () -> Unit,
    private val navigateToBookManage: () -> Unit
) {

    fun buildBecomeWriterRoute(isAuthor: Boolean): String {
        return if (isAuthor) {
            "becomewriter?isAuthor=true"
        } else {
            "becomewriter?isAuthor=false"
        }
    }

    fun createAuthorRegisterRequest(
        penName: String,
        sex: Int
    ): AuthorService.AuthorRegisterRequest {
        return AuthorService.AuthorRegisterRequest(
            penName = penName,
            telPhone = penName,
            chatAccount = penName,
            email = "$penName@163.com",
            workDirection = sex
        )
    }

    fun navigateToBecomeWriterWithFlag(isAuthor: Boolean) {
        navigateToRoute(buildBecomeWriterRoute(isAuthor))
    }

    fun navigateToWritePage() {
        navigateToWritePage.invoke()
    }

    fun navigateToBookManage() {
        navigateToBookManage.invoke()
    }
}
