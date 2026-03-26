package com.novel.utils

import androidx.compose.runtime.Stable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.novel.page.component.FlipBookAnimationController
import com.novel.page.search.component.SearchRankingItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@Stable
object NavViewModel : ViewModel() {
    val navController = MutableLiveData<NavHostController?>()

    private const val debounceTimeoutMs = 1_000L
    private const val backCommand = "__BACK__"

    private val navCommands = MutableSharedFlow<String>(extraBufferCapacity = 16)
    private var currentBookInfo: Pair<String, Boolean>? = null
    private var flipBookController: FlipBookAnimationController? = null

    init {
        viewModelScope.launch {
            navCommands
                .debounce(debounceTimeoutMs)
                .collect { command ->
                    navController.value?.let { controller ->
                        if (command == backCommand) {
                            controller.popBackStack()
                            TimberLogger.d("NavViewModel", "Debounced popBackStack()")
                        } else {
                            controller.navigate(command)
                            TimberLogger.d("NavViewModel", "Debounced navigate($command)")
                        }
                    }
                }
        }
    }

    fun setFlipBookController(controller: FlipBookAnimationController?) {
        flipBookController = controller
    }

    fun currentFlipBookController(): FlipBookAnimationController? = flipBookController

    fun navigateToSearch(query: String = "") {
        val route = if (query.isNotBlank()) "search?query=$query" else "search"
        navigateDebounced(route)
    }

    fun navigateToSearchResult(query: String) {
        navigateDebounced("search_result?query=$query")
    }

    fun navigateToFullRanking(
        rankingType: String,
        rankingItems: List<SearchRankingItem>,
    ) {
        val encodedData = encodeRankingData(rankingItems)
        navigateDebounced("full_ranking/$rankingType/$encodedData")
    }

    fun navigateToBookDetail(bookId: String, fromRank: Boolean = false) {
        currentBookInfo = bookId to fromRank
        navigateDebounced("book_detail/$bookId?fromRank=$fromRank")
    }

    fun navigateToReader(bookId: String, chapterId: String? = null) {
        val route = if (chapterId != null) {
            "reader/$bookId?chapterId=$chapterId"
        } else {
            "reader/$bookId"
        }
        navigateDebounced(route)
    }

    fun navigateToRoute(route: String) {
        navigateDebounced(route)
    }

    fun navigateToHistory() = navigateDebounced("history")

    fun navigateToMessage() = navigateDebounced("message")

    fun navigateToBecomeWriter() = navigateDebounced("becomewriter")

    fun navigateToRecommendBook() = navigateDebounced("recommendbook")

    fun navigateToViewedUsers() = navigateDebounced("viewedusers")

    fun navigateToMyReservation() = navigateDebounced("myreservation")

    fun navigateToMemberCenter() = navigateDebounced("membercenter")

    fun navigateToWritePage() = navigateDebounced("writepage")

    fun navigateToAIPage() = navigateDebounced("aipage")

    fun navigateToBookManage() = navigateDebounced("bookmanage")

    fun navigateToFeedbackHelp() = navigateDebounced("feedbackhelp")

    fun navigateToQuestionList() = navigateDebounced("questionlist")

    fun navigateToQuestionDetail() = navigateDebounced("questiondetail")

    fun navigateToCommentPage(bookId: String) {
        val encodedData = encodeCommentData("{\"bookId\":\"$bookId\"}")
        navigateDebounced("comment/$encodedData")
    }

    fun navigateToCommentPageWithBookInfo(
        bookId: String,
        bookName: String,
        authorName: String,
        picUrl: String,
    ) {
        val bookData =
            """{"bookId":"$bookId","bookName":"$bookName","authorName":"$authorName","picUrl":"$picUrl"}"""
        navigateDebounced("comment/${encodeCommentData(bookData)}")
    }

    fun navigateToWriteReview(bookId: String? = null, rating: Int? = null) {
        val route = when {
            bookId != null && rating != null -> "writereview/$bookId?rating=$rating"
            bookId != null -> "writereview/$bookId"
            else -> "writereview"
        }
        navigateDebounced(route)
    }

    fun navigateToReviewDetail(commentData: String) {
        navigateDebounced("reviewdetail/${encodeCommentData(commentData)}")
    }

    fun navigateBack() {
        navigateDebounced(backCommand)
    }

    fun decodeRankingData(encodedData: String): List<SearchRankingItem> {
        return try {
            val decodedUrl = java.net.URLDecoder.decode(encodedData, "UTF-8")
            val bytes = android.util.Base64.decode(
                decodedUrl,
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP,
            )
            String(bytes)
                .split("|")
                .mapNotNull { entry ->
                    val parts = entry.split(',')
                    if (parts.size < 4) {
                        null
                    } else {
                        SearchRankingItem(
                            id = parts[0].toLongOrNull() ?: 0L,
                            title = parts[1],
                            author = parts[2],
                            rank = parts[3].toIntOrNull() ?: 0,
                        )
                    }
                }
        } catch (error: Exception) {
            TimberLogger.e("NavViewModel", "Failed to decode ranking data", error)
            emptyList()
        }
    }

    fun decodeCommentData(encodedData: String): String = decodePayload(encodedData, "comment")

    fun decodeBookData(encodedData: String): String = decodePayload(encodedData, "book")

    private fun navigateDebounced(route: String) {
        navCommands.tryEmit(route)
    }

    private fun encodeRankingData(items: List<SearchRankingItem>): String {
        return try {
            val csv = items.joinToString("|") { "${it.id},${it.title},${it.author},${it.rank}" }
            val base64 = android.util.Base64.encodeToString(
                csv.toByteArray(),
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP,
            )
            java.net.URLEncoder.encode(base64, "UTF-8")
        } catch (error: Exception) {
            TimberLogger.e("NavViewModel", "Failed to encode ranking data", error)
            ""
        }
    }

    private fun encodeCommentData(commentData: String): String {
        return try {
            val base64 = android.util.Base64.encodeToString(
                commentData.toByteArray(Charsets.UTF_8),
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP,
            )
            java.net.URLEncoder.encode(base64, "UTF-8")
        } catch (error: Exception) {
            TimberLogger.e("NavViewModel", "Failed to encode comment data", error)
            ""
        }
    }

    private fun decodePayload(encodedData: String, payloadName: String): String {
        return try {
            val decodedUrl = java.net.URLDecoder.decode(encodedData, "UTF-8")
            val bytes = android.util.Base64.decode(
                decodedUrl,
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP,
            )
            String(bytes, Charsets.UTF_8)
        } catch (error: Exception) {
            TimberLogger.e("NavViewModel", "Failed to decode $payloadName data", error)
            ""
        }
    }
}
