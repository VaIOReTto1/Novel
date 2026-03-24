package com.novel.rn.host

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DefaultHostNavigationGatewayTest {

    private fun createGateway(
        dispatchToHost: ((() -> Unit) -> Unit) = { block -> block() },
        navigateToRouteAction: (String) -> Unit = {},
        navigateBackAction: () -> Unit = {},
        navigateToReaderAction: (String, String?) -> Unit = { _, _ -> },
    ): DefaultHostNavigationGateway {
        return DefaultHostNavigationGateway(
            dispatchToHost = dispatchToHost,
            navigateToRouteAction = navigateToRouteAction,
            navigateBackAction = navigateBackAction,
            navigateToWritePageAction = {},
            navigateToBookManageAction = {},
            navigateToSearchAction = {},
            navigateToBecomeWriterAction = {},
            navigateToAIPageAction = {},
            navigateToReaderAction = navigateToReaderAction,
            navigateToRecommendBookAction = {},
            navigateToMyReservationAction = {},
            navigateToMemberCenterAction = {},
            navigateToViewedUsersAction = {},
            navigateToFeedbackHelpAction = {},
            navigateToQuestionListAction = {},
            navigateToQuestionDetailAction = {},
            navigateToWriteReviewAction = { _, _ -> },
            navigateToReviewDetailAction = {},
        )
    }

    @Test
    fun `navigateToRoute dispatches route action`() {
        val events = mutableListOf<String>()
        val gateway = createGateway(
            dispatchToHost = { block ->
                events += "dispatch"
                block()
            },
            navigateToRouteAction = { route -> events += "route:$route" },
        )

        gateway.navigateToRoute("settings")

        assertThat(events).containsExactly("dispatch", "route:settings").inOrder()
    }

    @Test
    fun `navigateBack dispatches back action`() {
        val events = mutableListOf<String>()
        val gateway = createGateway(
            dispatchToHost = { block ->
                events += "dispatch"
                block()
            },
            navigateBackAction = { events += "back" },
        )

        gateway.navigateBack()

        assertThat(events).containsExactly("dispatch", "back").inOrder()
    }

    @Test
    fun `navigateToReader forwards chapter id`() {
        val events = mutableListOf<String>()
        val gateway = createGateway(
            navigateToReaderAction = { bookId, chapterId ->
                events += "$bookId:${chapterId ?: "null"}"
            },
        )

        gateway.navigateToReader("book-1", "chapter-2")

        assertThat(events).containsExactly("book-1:chapter-2")
    }
}
