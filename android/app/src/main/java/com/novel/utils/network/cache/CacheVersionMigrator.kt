package com.novel.utils.network.cache

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.novel.utils.TimberLogger
import java.io.File

internal class CacheVersionMigrator(
    private val cacheDir: File,
    private val gson: Gson,
    private val currentCacheVersion: Int,
    private val clearAllCache: () -> Unit,
) {

    fun handleMigration() {
        try {
            val versionFile = File(cacheDir, VERSION_FILE_NAME)
            val currentVersion = if (versionFile.exists()) {
                versionFile.readText().toIntOrNull() ?: LEGACY_CACHE_VERSION
            } else {
                LEGACY_CACHE_VERSION
            }

            if (currentVersion < currentCacheVersion) {
                TimberLogger.d(
                    TAG,
                    "检测到缓存版本升级: ${currentVersion} -> ${currentCacheVersion}，开始迁移",
                )
                migrateCacheVersion(currentVersion, currentCacheVersion)
                versionFile.writeText(currentCacheVersion.toString())
                TimberLogger.d(TAG, "缓存版本迁移完成")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "缓存版本迁移失败，清理所有缓存", e)
            clearAllCache()
        }
    }

    private fun migrateCacheVersion(fromVersion: Int, toVersion: Int) {
        when {
            fromVersion < 2 -> {
                TimberLogger.d(TAG, "迁移缓存从版本${fromVersion}到版本${toVersion}，清理不兼容缓存")
                cacheDir.listFiles()?.forEach { file ->
                    if (file.name != VERSION_FILE_NAME) {
                        try {
                            val content = file.readText()
                            gson.fromJson(content, object : TypeToken<CacheEntry<Any?>>() {}.type)
                        } catch (_: Exception) {
                            TimberLogger.d(TAG, "删除不兼容缓存文件: ${file.name}")
                            file.delete()
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private const val TAG = "NetworkCacheManager"
        private const val LEGACY_CACHE_VERSION = 1
        private const val VERSION_FILE_NAME = "cache_version.txt"
    }
}
