package com.novel.rn.bridge

import com.google.common.truth.Truth.assertThat
import com.novel.rn.bridge.BridgeComponentCachePolicy
import com.novel.rn.host.HostNavigationGateway
import com.novel.rn.host.ReactRootViewCacheGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BridgeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `navigate to login delegates to host navigation gateway`() = runTest(dispatcher) {
        val routes = mutableListOf<String>()
        val viewModel = BridgeViewModel(
            hostNavigationGateway = FakeHostNavigationGateway(
                onNavigateToRoute = { route -> routes += route },
            ),
            reactRootViewCacheGateway = FakeReactRootViewCacheGateway(),
        )

        viewModel.sendIntent(BridgeIntent.NavigateToLogin)
        advanceUntilIdle()

        assertThat(routes).containsExactly("login")
    }

    @Test
    fun `navigate back clears cache then delegates to host navigation gateway`() = runTest(dispatcher) {
        val events = mutableListOf<String>()
        val viewModel = BridgeViewModel(
            hostNavigationGateway = FakeHostNavigationGateway(
                onNavigateBack = { events += "back" },
            ),
            reactRootViewCacheGateway = FakeReactRootViewCacheGateway(
                onClearComponentCache = { componentName -> events += "clear:$componentName" },
            ),
        )

        viewModel.sendIntent(BridgeIntent.NavigateBack("profile"))
        advanceUntilIdle()

        assertThat(events).containsExactly("clear:profile", "back").inOrder()
    }

    @Test
    fun `navigate back can retain cache for reusable host pages`() = runTest(dispatcher) {
        val events = mutableListOf<String>()
        val viewModel = BridgeViewModel(
            hostNavigationGateway = FakeHostNavigationGateway(
                onNavigateBack = { events += "back" },
            ),
            reactRootViewCacheGateway = FakeReactRootViewCacheGateway(
                onClearComponentCache = { componentName -> events += "clear:$componentName" },
            ),
        )

        viewModel.sendIntent(
            BridgeIntent.NavigateBack(
                componentName = "Novel",
                cachePolicy = BridgeComponentCachePolicy.RETAIN_COMPONENT_CACHE,
            ),
        )
        advanceUntilIdle()

        assertThat(events).containsExactly("back")
    }

    private class FakeHostNavigationGateway(
        private val onNavigateToRoute: (String) -> Unit = {},
        private val onNavigateBack: () -> Unit = {},
    ) : HostNavigationGateway {

        override fun navigateToRoute(route: String) = onNavigateToRoute(route)

        override fun navigateBack() = onNavigateBack()

        override fun navigateToWritePage() = Unit

        override fun navigateToBookManage() = Unit

        override fun navigateToSearch(query: String) = Unit

        override fun navigateToBecomeWriter() = Unit

        override fun navigateToAIPage() = Unit

        override fun navigateToReader(bookId: String, chapterId: String?) = Unit

        override fun navigateToRecommendBook() = Unit

        override fun navigateToMyReservation() = Unit

        override fun navigateToMemberCenter() = Unit

        override fun navigateToViewedUsers() = Unit

        override fun navigateToFeedbackHelp() = Unit

        override fun navigateToQuestionList() = Unit

        override fun navigateToQuestionDetail() = Unit

        override fun navigateToWriteReview(bookId: String?, rating: Int?) = Unit

        override fun navigateToReviewDetail(commentData: String) = Unit
    }

    private class FakeReactRootViewCacheGateway(
        private val onClearComponentCache: (String) -> Unit = {},
        private val onClearAllComponentCache: () -> Unit = {},
    ) : ReactRootViewCacheGateway {

        override fun clearComponentCache(componentName: String) {
            onClearComponentCache(componentName)
        }

        override fun clearAllComponentCache() {
            onClearAllComponentCache()
        }
    }
}
