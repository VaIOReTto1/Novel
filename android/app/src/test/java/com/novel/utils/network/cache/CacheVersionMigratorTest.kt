package com.novel.utils.network.cache

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import java.io.File
import kotlin.io.path.createTempDirectory
import org.junit.Test

class CacheVersionMigratorTest {

    private val gson = Gson()

    @Test
    fun handleMigration_writesCurrentVersionAndDeletesBrokenFiles() {
        val cacheDir = createTempDirectory("cache-version-migrator").toFile()
        val extraCacheFile = File(cacheDir, "compatible.json")
        val incompatibleCacheFile = File(cacheDir, "broken.json")

        extraCacheFile.writeText(
            gson.toJson(
                CacheEntry(
                    key = "compatible",
                    data = "ok",
                    cacheTime = 1L,
                    expiryTime = 2L,
                    cacheVersion = 1,
                )
            )
        )
        incompatibleCacheFile.writeText("{invalid json")

        CacheVersionMigrator(
            cacheDir = cacheDir,
            gson = gson,
            currentCacheVersion = 2,
            clearAllCache = {},
        ).handleMigration()

        assertThat(File(cacheDir, "cache_version.txt").readText()).isEqualTo("2")
        assertThat(incompatibleCacheFile.exists()).isFalse()
    }

    @Test
    fun handleMigration_doesNothingWhenCacheVersionIsCurrent() {
        val cacheDir = createTempDirectory("cache-version-current").toFile()
        val versionFile = File(cacheDir, "cache_version.txt")
        val cacheFile = File(cacheDir, "cached.json")
        versionFile.writeText("2")
        cacheFile.writeText("not-json-but-should-stay")

        CacheVersionMigrator(
            cacheDir = cacheDir,
            gson = gson,
            currentCacheVersion = 2,
            clearAllCache = {},
        ).handleMigration()

        assertThat(versionFile.readText()).isEqualTo("2")
        assertThat(cacheFile.exists()).isTrue()
    }

    @Test
    fun handleMigration_clearsCachesWhenVersionHandlingThrows() {
        val cacheDir = createTempDirectory("cache-version-error").toFile()
        val cacheFile = File(cacheDir, "cached.json")
        cacheFile.writeText("stale")
        File(cacheDir, "cache_version.txt").mkdir()
        var clearAllInvoked = false

        CacheVersionMigrator(
            cacheDir = cacheDir,
            gson = gson,
            currentCacheVersion = 2,
            clearAllCache = {
                clearAllInvoked = true
                cacheDir.listFiles()?.forEach { file ->
                    if (file.name != "cache_version.txt") {
                        file.deleteRecursively()
                    }
                }
            },
        ).handleMigration()

        assertThat(clearAllInvoked).isTrue()
        assertThat(cacheFile.exists()).isFalse()
    }
}
