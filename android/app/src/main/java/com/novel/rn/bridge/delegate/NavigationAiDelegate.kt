package com.novel.rn.bridge.delegate

import com.novel.utils.network.api.author.ai.AiService

sealed class NavigationAiResult {
    data class Success(val data: String) : NavigationAiResult()
    data class Failure(val errorCode: String, val errorMessage: String) : NavigationAiResult()
}

class NavigationAiDelegate {

    fun buildResult(
        response: AiService.AiResponse,
        defaultErrorCode: String
    ): NavigationAiResult {
        return if (response.ok == true) {
            NavigationAiResult.Success(response.data ?: "")
        } else {
            NavigationAiResult.Failure(
                errorCode = defaultErrorCode,
                errorMessage = response.message ?: "AI 返回失败"
            )
        }
    }
}
