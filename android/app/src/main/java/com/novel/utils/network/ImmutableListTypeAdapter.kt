package com.novel.utils.network

import androidx.compose.runtime.Stable
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * ImmutableList的Gson TypeAdapter
 * 
 * 解决Gson无法直接序列化/反序列化ImmutableList的问题
 * 在反序列化时将ArrayList转换为ImmutableList
 */
class ImmutableListTypeAdapterFactory : TypeAdapterFactory {
    
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType != ImmutableList::class.java) {
            return null
        }
        
        val elementType = if (type.type is ParameterizedType) {
            (type.type as ParameterizedType).actualTypeArguments[0]
        } else {
            Any::class.java
        }
        
        val elementAdapter = gson.getAdapter(TypeToken.get(elementType))
        
        @Suppress("UNCHECKED_CAST")
        return ImmutableListTypeAdapter(elementAdapter) as TypeAdapter<T>
    }
}

@Stable
class ImmutableListTypeAdapter<T>(
    @Stable
    private val elementAdapter: TypeAdapter<T>
) : TypeAdapter<ImmutableList<T>>() {
    
    override fun write(out: JsonWriter, value: ImmutableList<T>?) {
        if (value == null) {
            out.nullValue()
            return
        }
        
        out.beginArray()
        for (element in value) {
            elementAdapter.write(out, element)
        }
        out.endArray()
    }
    
    override fun read(`in`: JsonReader): ImmutableList<T>? {
        if (`in`.peek() == com.google.gson.stream.JsonToken.NULL) {
            `in`.nextNull()
            return null
        }
        
        val list = mutableListOf<T>()
        `in`.beginArray()
        while (`in`.hasNext()) {
            list.add(elementAdapter.read(`in`))
        }
        `in`.endArray()
        
        return list.toImmutableList()
    }
}
