package com.novel.page.welfare.usecase

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InitializeWelfarePageUseCaseTest {

    @Test
    fun execute_returnsDefaultTitleAndUrl() = runBlocking {
        val result = InitializeWelfarePageUseCase().invoke(Unit)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.title).isEqualTo("福利页面 - Bing")
        assertThat(result.defaultUrl).isEqualTo("https://www.bing.com/")
    }
}
