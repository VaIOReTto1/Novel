package com.novel.utils.network.api.author.ai

import androidx.compose.runtime.Stable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.novel.core.network.LegacyApiServiceAdapter
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.core.network.legacy.DefaultLegacyApiExecutor
import com.novel.core.result.AppError
import com.novel.core.result.DataResult
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService
import com.novel.utils.network.ApiService.BASE_URL_AI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 作家后台-AI模块（客户端）
 *
 * 与服务端 `AuthorAiController` 契约保持一致：
 * - /expand : ratio 为百分比，例如 150 表示将文本扩展到约 150%（允许±10%）
 * - /condense : ratio 作为“分母”，例如 2 表示约 1/2（内部折算为百分比，允许±10%）
 * - /continue : length 为续写目标字数（近似，允许±20%）
 * - /polish : 无额外参数，仅做表达优化，不改变事实与叙述视角
 *
 * 返回体：后端为 `RestResp<String>`，本地映射为 [AiResponse]，其中 `data` 字段即为 AI 处理后的纯文本结果。
 */
@Singleton
class AiService @Inject constructor(
    private val networkFacade: NetworkFacade
) {

    constructor() : this(LegacyApiServiceAdapter(DefaultLegacyApiExecutor))
    
    // region 数据结构
    /**
     * 服务端通用响应体映射。
     * - `data`：AI 处理后的纯文本结果（服务端已做哨兵标记解析与清洗）。
     */
    @Stable
    data class AiResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: String?,
        @SerializedName("ok") val ok: Boolean?
    )

    /**
     * 润色请求：不改变原意与事实，仅做表达与可读性优化。
     */
    @Stable
    data class PolishRequest(
        val text: String
    )

    /**
     * 扩写请求：`ratio` 为百分比，例如 150 表示约 150%（允许±10%）。
     */
    @Stable
    data class ExpandRequest(
        val text: String,
        val ratio: Int
    )

    /**
     * 续写请求：`length` 为续写目标字数（近似，允许±20%）。
     */
    @Stable
    data class ContinueRequest(
        val text: String,
        val length: Int
    )

    /**
     * 缩写请求：`ratio` 作为“分母”，例如 2 表示约 1/2（内部折算为百分比，允许±10%）。
     */
    @Stable
    data class CondenseRequest(
        val text: String,
        val ratio: Int
    )
    // endregion

    // region 网络请求方法
    
    /**
     * AI 润色接口。
     * - 不改变原意与事实，仅做表达优化。
     * @param text 需要润色的文本
     */
    private fun polishText(
        text: String,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AiService", "开始 polishText()，文本长度：${text.length}")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse(
                    endpoint = "polish",
                    queryParams = mapOf("text" to text)
                )
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    /**
     * AI 扩写接口。
     * - `ratio` 为百分比，例如 150 表示将文本扩展到约 150%（允许±10%）。
     * @param text 需要扩写的文本
     * @param ratio 扩写比例（百分比）
     */
    private fun expandText(
        text: String,
        ratio: Int,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AiService", "开始 expandText()，文本长度：${text.length}，扩写比例：$ratio")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse(
                    endpoint = "expand",
                    queryParams = mapOf(
                        "text" to text,
                        "ratio" to ratio.toString()
                    )
                )
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    /**
     * AI 续写接口。
     * - `length` 为续写目标字数（近似，允许±20%）。
     * @param text 需要续写的文本
     * @param length 续写长度（目标字数）
     */
    private fun continueText(
        text: String,
        length: Int,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AiService", "开始 continueText()，文本长度：${text.length}，续写长度：$length")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse(
                    endpoint = "continue",
                    queryParams = mapOf(
                        "text" to text,
                        "length" to length.toString()
                    )
                )
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    /**
     * AI 缩写接口。
     * - `ratio` 作为“分母”，例如 2 表示约 1/2（内部折算为百分比，允许±10%）。
     * @param text 需要缩写的文本
     * @param ratio 缩写比例（分母）
     */
    private fun condenseText(
        text: String,
        ratio: Int,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AiService", "开始 condenseText()，文本长度：${text.length}，缩写比例：$ratio")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse(
                    endpoint = "condense",
                    queryParams = mapOf(
                        "text" to text,
                        "ratio" to ratio.toString()
                    )
                )
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    // endregion

    // region 便捷方法
    
    /**
     * AI润色接口（使用请求对象）
     */
    fun polishText(
        request: PolishRequest,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        polishText(request.text, callback)
    }

    /**
     * AI扩写接口（使用请求对象）
     */
    fun expandText(
        request: ExpandRequest,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        expandText(request.text, request.ratio, callback)
    }

    /**
     * AI续写接口（使用请求对象）
     */
    fun continueText(
        request: ContinueRequest,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        continueText(request.text, request.length, callback)
    }

    /**
     * AI缩写接口（使用请求对象）
     */
    fun condenseText(
        request: CondenseRequest,
        callback: (AiResponse?, Throwable?) -> Unit
    ) {
        condenseText(request.text, request.ratio, callback)
    }

    // endregion

    // region 协程版本
    suspend fun polishTextBlocking(text: String): AiResponse {
        return suspendCancellableCoroutine { cont ->
            polishText(text) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun polishTextBlocking(request: PolishRequest): AiResponse {
        return polishTextBlocking(request.text)
    }

    suspend fun polishTextResult(text: String): DataResult<AiResponse> =
        runResulting { polishTextBlocking(text) }

    suspend fun expandTextBlocking(text: String, ratio: Int): AiResponse {
        return suspendCancellableCoroutine { cont ->
            expandText(text, ratio) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun expandTextBlocking(request: ExpandRequest): AiResponse {
        return expandTextBlocking(request.text, request.ratio)
    }

    suspend fun continueTextBlocking(text: String, length: Int): AiResponse {
        return suspendCancellableCoroutine { cont ->
            continueText(text, length) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun continueTextBlocking(request: ContinueRequest): AiResponse {
        return continueTextBlocking(request.text, request.length)
    }

    suspend fun condenseTextBlocking(text: String, ratio: Int): AiResponse {
        return suspendCancellableCoroutine { cont ->
            condenseText(text, ratio) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun condenseTextBlocking(request: CondenseRequest): AiResponse {
        return condenseTextBlocking(request.text, request.ratio)
    }

    // endregion

    private suspend fun requestAndParse(
        endpoint: String,
        queryParams: Map<String, String>
    ): AiResponse {
        val response = networkFacade.execute(
            NetworkRequest(
                baseUrl = BASE_URL_AI,
                endpoint = endpoint,
                method = NetworkRequestMethod.POST_QUERY,
                queryParams = queryParams,
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            )
        )

        return Gson().fromJson(response, AiResponse::class.java)
    }

    private suspend fun <T> runResulting(block: suspend () -> T): DataResult<T> =
        try {
            DataResult.Success(block())
        } catch (throwable: Throwable) {
            TimberLogger.e("AiService", "DataResult request failed", throwable)
            DataResult.Failure(AppError.fromThrowable(throwable))
        }
    // endregion
}
