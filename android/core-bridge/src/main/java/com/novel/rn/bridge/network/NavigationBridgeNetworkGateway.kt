package com.novel.rn.bridge.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.novel.core.logging.CoreLogger
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.network.NetworkTraceLogHelper
import com.novel.utils.network.interceptor.RequestIdInterceptor

data class HomeBooksBridgeItem(
    val type: Int,
    val bookId: String,
    val picUrl: String?,
    val bookName: String?,
    val authorName: String?,
    val bookDesc: String?
)

data class HomeBooksBridgeResponse(
    val code: String?,
    val message: String?,
    val ok: Boolean,
    val data: List<HomeBooksBridgeItem>
)

data class BookCategoryBridgeItem(
    val id: String,
    val name: String?
)

data class BookCategoryBridgeResponse(
    val ok: Boolean,
    val data: List<BookCategoryBridgeItem>
)

data class SearchBooksBridgeItem(
    val id: String,
    val bookName: String?,
    val authorName: String?,
    val picUrl: String?,
    val bookDesc: String?
)

data class SearchBooksBridgeResponse(
    val ok: Boolean,
    val pageNum: Long?,
    val pageSize: Long?,
    val total: Long?,
    val pages: Long?,
    val list: List<SearchBooksBridgeItem>
)

data class AuthorBookBridgeItem(
    val id: String,
    val bookName: String?,
    val authorName: String?,
    val picUrl: String?,
    val wordCount: Double,
    val bookDesc: String?,
    val categoryId: String,
    val categoryName: String?
)

data class AuthorBooksBridgeResponse(
    val code: String?,
    val message: String?,
    val ok: Boolean,
    val list: List<AuthorBookBridgeItem>
)

class NavigationBridgeNetworkGateway(
    private val networkFacade: NetworkFacade,
    private val frontBaseUrl: String,
    private val authorBaseUrl: String
) {

    suspend fun getHomeBooksHighPriority(): HomeBooksBridgeResponse {
        val json = executeGet(
            endpoint = "home/books"
        )
        val code = json.optNullableString("code")
        val message = json.optNullableString("message")
        val ok = json.optBoolean("ok")
        val items = mutableListOf<HomeBooksBridgeItem>()
        val dataJsonArr = json.optJsonArray("data")

        if (dataJsonArr != null) {
            for (index in 0 until dataJsonArr.size()) {
                val item = dataJsonArr[index]?.takeIf { it.isJsonObject }?.asJsonObject ?: continue
                items += HomeBooksBridgeItem(
                    type = item.optInt("type"),
                    bookId = item.optLong("bookId").toString(),
                    picUrl = item.optNullableString("picUrl"),
                    bookName = item.optNullableString("bookName"),
                    authorName = item.optNullableString("authorName"),
                    bookDesc = item.optNullableString("bookDesc")
                )
            }
        }

        return HomeBooksBridgeResponse(
            code = code,
            message = message,
            ok = ok,
            data = items
        )
    }

    suspend fun getBookCategories(workDirection: Int): BookCategoryBridgeResponse {
        val json = executeGet(
            endpoint = "book/category/list",
            queryParams = mapOf("workDirection" to workDirection.toString())
        )
        val items = mutableListOf<BookCategoryBridgeItem>()
        val dataJsonArr = json.optJsonArray("data")

        if (dataJsonArr != null) {
            for (index in 0 until dataJsonArr.size()) {
                val item = dataJsonArr[index]?.takeIf { it.isJsonObject }?.asJsonObject ?: continue
                items += BookCategoryBridgeItem(
                    id = item.optLong("id").toString(),
                    name = item.optNullableString("name")
                )
            }
        }

        return BookCategoryBridgeResponse(
            ok = json.optBoolean("ok"),
            data = items
        )
    }

    suspend fun searchBooks(
        workDirection: Int,
        categoryId: Int,
        pageNum: Int,
        pageSize: Int
    ): SearchBooksBridgeResponse {
        val queryParams = linkedMapOf(
            "pageNum" to pageNum.toString(),
            "pageSize" to pageSize.toString(),
            "workDirection" to workDirection.toString()
        ).apply {
            if (categoryId > 0) {
                put("categoryId", categoryId.toString())
            }
        }
        val json = executeGet(
            endpoint = "search/books",
            queryParams = queryParams
        )
        val dataObject = json.optJsonObject("data")
        val items = mutableListOf<SearchBooksBridgeItem>()
        val listJsonArr = dataObject?.optJsonArray("list")

        if (listJsonArr != null) {
            for (index in 0 until listJsonArr.size()) {
                val item = listJsonArr[index]?.takeIf { it.isJsonObject }?.asJsonObject ?: continue
                items += SearchBooksBridgeItem(
                    id = item.optLong("id").toString(),
                    bookName = item.optNullableString("bookName"),
                    authorName = item.optNullableString("authorName"),
                    picUrl = item.optNullableString("picUrl"),
                    bookDesc = item.optNullableString("bookDesc")
                )
            }
        }

        return SearchBooksBridgeResponse(
            ok = json.optBoolean("ok"),
            pageNum = dataObject?.optNullableLong("pageNum"),
            pageSize = dataObject?.optNullableLong("pageSize"),
            total = dataObject?.optNullableLong("total"),
            pages = dataObject?.optNullableLong("pages"),
            list = items
        )
    }

    suspend fun getAuthorBooks(pageNum: Int, pageSize: Int): AuthorBooksBridgeResponse {
        val json = executeGet(
            baseUrl = authorBaseUrl,
            endpoint = "books",
            queryParams = mapOf(
                "pageNum" to pageNum.toString(),
                "pageSize" to pageSize.toString()
            )
        )
        val dataObject = json.optJsonObject("data")
        val items = mutableListOf<AuthorBookBridgeItem>()
        val listJsonArr = dataObject?.optJsonArray("list")

        if (listJsonArr != null) {
            for (index in 0 until listJsonArr.size()) {
                val item = listJsonArr[index]?.takeIf { it.isJsonObject }?.asJsonObject ?: continue
                items += AuthorBookBridgeItem(
                    id = item.optLong("id").toString(),
                    bookName = item.optNullableString("bookName"),
                    authorName = item.optNullableString("authorName"),
                    picUrl = item.optNullableString("picUrl"),
                    wordCount = item.optInt("wordCount").toDouble(),
                    bookDesc = item.optNullableString("bookDesc"),
                    categoryId = item.optLong("categoryId").toString(),
                    categoryName = item.optNullableString("categoryName")
                )
            }
        }

        return AuthorBooksBridgeResponse(
            code = json.optNullableString("code"),
            message = json.optNullableString("message"),
            ok = json.optBoolean("ok"),
            list = items
        )
    }

    private suspend fun executeGet(
        baseUrl: String = frontBaseUrl,
        endpoint: String,
        queryParams: Map<String, String> = emptyMap()
    ): JsonObject {
        val request = NetworkRequest(
            baseUrl = baseUrl,
            endpoint = endpoint,
            method = NetworkRequestMethod.GET,
            queryParams = queryParams,
            headers = RequestIdInterceptor.ensureTraceHeaders(mapOf("Accept" to "*/*"))
        )
        CoreLogger.d("NavigationBridgeNetworkGateway", NetworkTraceLogHelper.formatBridgeDispatch(request))
        val response = networkFacade.execute(request)

        return JsonParser.parseString(response).asJsonObject
    }
}

private fun JsonObject.optJsonArray(key: String): JsonArray? =
    get(key)?.takeUnless { it.isJsonNull }?.takeIf { it.isJsonArray }?.asJsonArray

private fun JsonObject.optJsonObject(key: String): JsonObject? =
    get(key)?.takeUnless { it.isJsonNull }?.takeIf { it.isJsonObject }?.asJsonObject

private fun JsonObject.optNullableString(key: String): String? =
    get(key)?.takeUnless { it.isJsonNull }?.asString

private fun JsonObject.optBoolean(key: String): Boolean =
    get(key)?.takeUnless { it.isJsonNull }?.asBoolean ?: false

private fun JsonObject.optInt(key: String): Int =
    get(key)?.takeUnless { it.isJsonNull }?.asInt ?: 0

private fun JsonObject.optLong(key: String): Long =
    get(key)?.takeUnless { it.isJsonNull }?.asLong ?: 0L

private fun JsonObject.optNullableLong(key: String): Long? =
    get(key)?.takeUnless { it.isJsonNull }?.asLong
