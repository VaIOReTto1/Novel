package com.novel.utils.network

import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import kotlinx.collections.immutable.ImmutableList
import org.junit.Test

class ImmutableListTypeAdapterFactoryTest {

    @Test
    fun fromJson_deserializesImmutableList() {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(ImmutableListTypeAdapterFactory())
            .create()

        val result = gson.fromJson("""{"items":["alpha","beta"]}""", TestModel::class.java)

        assertThat(result.items).isInstanceOf(ImmutableList::class.java)
        assertThat(result.items).containsExactly("alpha", "beta").inOrder()
    }

    private data class TestModel(
        val items: ImmutableList<String>,
    )
}
