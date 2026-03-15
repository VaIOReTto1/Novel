package com.novel.testing

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object FixtureLoader {

    fun loadText(path: String): String {
        val classLoader = requireNotNull(FixtureLoader::class.java.classLoader) {
            "Fixture class loader is unavailable"
        }
        val stream = classLoader.getResourceAsStream(path)
            ?: error("Fixture not found: $path")
        return stream.use { input ->
            BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).readText()
        }
    }
}
