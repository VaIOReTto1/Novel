package com.novel.rn.host

import android.os.Handler
import android.os.Looper
import com.novel.utils.NavViewModel

interface HostNavigationGateway {
    fun navigateToRoute(route: String)
    fun navigateBack()
    fun navigateToWritePage()
    fun navigateToBookManage()
    fun navigateToSearch(query: String)
    fun navigateToBecomeWriter()
    fun navigateToAIPage()
    fun navigateToReader(bookId: String, chapterId: String? = null)
    fun navigateToRecommendBook()
    fun navigateToMyReservation()
    fun navigateToMemberCenter()
    fun navigateToViewedUsers()
    fun navigateToFeedbackHelp()
    fun navigateToQuestionList()
    fun navigateToQuestionDetail()
    fun navigateToWriteReview(bookId: String?, rating: Int?)
    fun navigateToReviewDetail(commentData: String)
}

class DefaultHostNavigationGateway(
    private val dispatchToHost: ((() -> Unit) -> Unit) = { block ->
        Handler(Looper.getMainLooper()).post(block)
    },
    private val navigateToRouteAction: (String) -> Unit = { route ->
        NavViewModel.navController.value?.navigate(route)
    },
    private val navigateBackAction: () -> Unit = {
        NavViewModel.navController.value?.popBackStack()
    },
    private val navigateToWritePageAction: () -> Unit = NavViewModel::navigateToWritePage,
    private val navigateToBookManageAction: () -> Unit = NavViewModel::navigateToBookManage,
    private val navigateToSearchAction: (String) -> Unit = NavViewModel::navigateToSearch,
    private val navigateToBecomeWriterAction: () -> Unit = NavViewModel::navigateToBecomeWriter,
    private val navigateToAIPageAction: () -> Unit = NavViewModel::navigateToAIPage,
    private val navigateToReaderAction: (String, String?) -> Unit = NavViewModel::navigateToReader,
    private val navigateToRecommendBookAction: () -> Unit = NavViewModel::navigateToRecommendBook,
    private val navigateToMyReservationAction: () -> Unit = NavViewModel::navigateToMyReservation,
    private val navigateToMemberCenterAction: () -> Unit = NavViewModel::navigateToMemberCenter,
    private val navigateToViewedUsersAction: () -> Unit = NavViewModel::navigateToViewedUsers,
    private val navigateToFeedbackHelpAction: () -> Unit = NavViewModel::navigateToFeedbackHelp,
    private val navigateToQuestionListAction: () -> Unit = NavViewModel::navigateToQuestionList,
    private val navigateToQuestionDetailAction: () -> Unit = NavViewModel::navigateToQuestionDetail,
    private val navigateToWriteReviewAction: (String?, Int?) -> Unit = NavViewModel::navigateToWriteReview,
    private val navigateToReviewDetailAction: (String) -> Unit = NavViewModel::navigateToReviewDetail,
) : HostNavigationGateway {

    override fun navigateToRoute(route: String) {
        dispatchToHost { navigateToRouteAction(route) }
    }

    override fun navigateBack() {
        dispatchToHost(navigateBackAction)
    }

    override fun navigateToWritePage() {
        dispatchToHost(navigateToWritePageAction)
    }

    override fun navigateToBookManage() {
        dispatchToHost(navigateToBookManageAction)
    }

    override fun navigateToSearch(query: String) {
        dispatchToHost { navigateToSearchAction(query) }
    }

    override fun navigateToBecomeWriter() {
        dispatchToHost(navigateToBecomeWriterAction)
    }

    override fun navigateToAIPage() {
        dispatchToHost(navigateToAIPageAction)
    }

    override fun navigateToReader(bookId: String, chapterId: String?) {
        dispatchToHost { navigateToReaderAction(bookId, chapterId) }
    }

    override fun navigateToRecommendBook() {
        dispatchToHost(navigateToRecommendBookAction)
    }

    override fun navigateToMyReservation() {
        dispatchToHost(navigateToMyReservationAction)
    }

    override fun navigateToMemberCenter() {
        dispatchToHost(navigateToMemberCenterAction)
    }

    override fun navigateToViewedUsers() {
        dispatchToHost(navigateToViewedUsersAction)
    }

    override fun navigateToFeedbackHelp() {
        dispatchToHost(navigateToFeedbackHelpAction)
    }

    override fun navigateToQuestionList() {
        dispatchToHost(navigateToQuestionListAction)
    }

    override fun navigateToQuestionDetail() {
        dispatchToHost(navigateToQuestionDetailAction)
    }

    override fun navigateToWriteReview(bookId: String?, rating: Int?) {
        dispatchToHost { navigateToWriteReviewAction(bookId, rating) }
    }

    override fun navigateToReviewDetail(commentData: String) {
        dispatchToHost { navigateToReviewDetailAction(commentData) }
    }
}
