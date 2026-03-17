package com.novel.utils.network.api.front

import com.google.gson.GsonBuilder
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.core.result.AppError
import com.novel.core.result.DataResult
import com.novel.utils.network.ApiService
import com.novel.utils.network.ImmutableListTypeAdapterFactory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class SearchServiceTest {

    private val gson = GsonBuilder()
        .registerTypeAdapterFactory(ImmutableListTypeAdapterFactory())
        .create()

    @Test
    fun searchBooksBlocking_buildsExpectedRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":{
                    "pageNum":2,
                    "pageSize":20,
                    "total":88,
                    "pages":5,
                    "list":[
                      {
                        "id":11,
                        "categoryId":7,
                        "categoryName":"都市",
                        "picUrl":"https://example.com/search.png",
                        "bookName":"Search NetworkFacade Book",
                        "authorId":9,
                        "authorName":"Search Author",
                        "bookDesc":"Search service sample",
                        "bookStatus":1,
                        "visitCount":99,
                        "wordCount":12345,
                        "commentCount":5,
                        "firstChapterId":1,
                        "lastChapterId":2,
                        "lastChapterName":"最新章",
                        "updateTime":"2026-03-16 10:00:00"
                      }
                    ]
                  }
                }
            """.trimIndent()
        )
        val service = SearchService(gson, facade)

        val result = service.searchBooksBlocking(
            request = SearchService.SearchRequest(
                keyword = "都市",
                workDirection = 1,
                categoryId = 7,
                sort = "updateTime",
                pageNum = 2,
                pageSize = 20
            )
        )

        assertEquals(ApiService.BASE_URL_FRONT, facade.lastRequest?.baseUrl)
        assertEquals("search/books", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "keyword" to "都市",
                "workDirection" to "1",
                "categoryId" to "7",
                "sort" to "updateTime",
                "pageNum" to "2",
                "pageSize" to "20"
            ),
            facade.lastRequest?.queryParams
        )
        assertEquals("0", result.code)
        assertEquals("ok", result.message)
        assertTrue(result.ok == true)
        assertEquals(2L, result.data?.pageNum)
        assertEquals(1, result.data?.list?.size)
        assertEquals("Search NetworkFacade Book", result.data?.list?.first()?.bookName)
    }

    @Test
    fun searchBooksByKeywordBlocking_buildsKeywordOnlyRequest() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "pageNum":1,
                    "pageSize":10,
                    "total":0,
                    "pages":0,
                    "list":[]
                  }
                }
            """.trimIndent()
        )
        val service = SearchService(gson, facade)

        service.searchBooksByKeywordBlocking(
            keyword = "关键字",
            pageNum = 1,
            pageSize = 10
        )

        assertEquals(
            mapOf(
                "keyword" to "关键字",
                "pageNum" to "1",
                "pageSize" to "10"
            ),
            facade.lastRequest?.queryParams
        )
    }

    @Test
    fun searchBooksResult_wrapsIoFailureAsNetworkError() = runBlocking {
        val facade = FailingNetworkFacade(IOException("offline"))
        val service = SearchService(gson, facade)

        val result = service.searchBooksResult(
            SearchService.SearchRequest(keyword = "测试")
        )

        assertTrue(result is DataResult.Failure)
        assertTrue((result as DataResult.Failure).error is AppError.Network)
    }

    @Test
    fun searchBooksResult_wrapsSuccessInDataResult() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "pageNum":1,
                    "pageSize":10,
                    "total":0,
                    "pages":0,
                    "list":[]
                  }
                }
            """.trimIndent()
        )
        val service = SearchService(gson, facade)

        val result = service.searchBooksResult(
            SearchService.SearchRequest(keyword = "测试")
        )

        assertTrue(result is DataResult.Success<*>)
        assertTrue((result as DataResult.Success<SearchService.BookSearchResponse>).value.ok == true)
    }

    private class RecordingNetworkFacade(
        private val response: String
    ) : NetworkFacade {
        var lastRequest: NetworkRequest? = null

        override suspend fun execute(request: NetworkRequest): String {
            lastRequest = request
            return response
        }
    }

    private class FailingNetworkFacade(
        private val throwable: Throwable
    ) : NetworkFacade {
        override suspend fun execute(request: NetworkRequest): String {
            throw throwable
        }
    }
}
