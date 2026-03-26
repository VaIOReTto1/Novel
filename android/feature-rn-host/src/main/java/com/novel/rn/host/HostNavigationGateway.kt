package com.novel.rn.host

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
