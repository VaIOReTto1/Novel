package com.novel.rn.bridge.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod

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

class NavigationBridgeNetworkGateway(
    private val networkFacade: NetworkFacade,
    private val frontBaseUrl: String
) {

    suspend fun getHomeBooksHighPriority(): HomeBooksBridgeResponse {
        val response = networkFacade.execute(
            NetworkRequest(
                baseUrl = frontBaseUrl,
                endpoint = "home/books",
                method = NetworkRequestMethod.GET,
                headers = mapOf("Accept" to "*/*")
            )
        )

        val json = JsonParser.parseString(response).asJsonObject
        val code = json.optNullableString("code")
        val message = json.optNullableString("message")
        val ok = json.optBoolean("ok")
        val items = mutableListOf<HomeBooksBridgeItem>()
        val dataJsonArr = json.getAsJsonArray("data")

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
}

private fun JsonObject.optNullableString(key: String): String? =
    get(key)?.takeUnless { it.isJsonNull }?.asString

private fun JsonObject.optBoolean(key: String): Boolean =
    get(key)?.takeUnless { it.isJsonNull }?.asBoolean ?: false

private fun JsonObject.optInt(key: String): Int =
    get(key)?.takeUnless { it.isJsonNull }?.asInt ?: 0

private fun JsonObject.optLong(key: String): Long =
    get(key)?.takeUnless { it.isJsonNull }?.asLong ?: 0L
