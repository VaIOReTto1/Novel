package com.novel.rn.settings.usecase

import android.content.Context
import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.rn.settings.SettingsUtils
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.TimberLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 导出用户数据UseCase
 * 
 * 功能：
 * - 导出用户设置数据
 * - 生成备份文件
 * - 数据打包压缩
 */
@Stable
class ExportUserDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsUtils: SettingsUtils,
    private val novelUserDefaults: NovelUserDefaults
) : BaseUseCase<ExportUserDataUseCase.ExportParams, ExportUserDataUseCase.ExportResult>() {

    companion object {
        private const val TAG = "ExportUserDataUseCase"
        private const val EXPORT_FILE_NAME = "novel_settings_backup"
    }

    data class ExportParams(
        val includeSettings: Boolean = true,
        val includeCache: Boolean = false,
        val exportPath: String? = null
    )

    @Serializable
    @Stable
    data class UserDataBackup(
        val exportTime: String,
        val appVersion: String,
        val settings: SettingsBackup,
        val metadata: Map<String, String>
    )

    @Serializable
    @Stable
    data class SettingsBackup(
        val themeMode: String,
        val followSystemTheme: Boolean,
        val autoNightMode: Boolean,
        val nightModeStartTime: String,
        val nightModeEndTime: String,
        val customSettings: Map<String, String>
    )

    data class ExportResult(
        val success: Boolean,
        val filePath: String,
        val fileSize: String,
        val message: String
    )

    override suspend fun execute(parameters: ExportParams): ExportResult {
        TimberLogger.d(TAG, "开始导出用户数据")
        
        try {
            val settingsBackup = SettingsBackup(
                themeMode = novelUserDefaults.getString("night_mode") ?: "auto",
                followSystemTheme = settingsUtils.isFollowSystemTheme(),
                autoNightMode = settingsUtils.isAutoNightModeEnabled(),
                nightModeStartTime = settingsUtils.getNightModeStartTime(),
                nightModeEndTime = settingsUtils.getNightModeEndTime(),
                customSettings = getAllCustomSettings()
            )
            
            val userDataBackup = UserDataBackup(
                exportTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                appVersion = getAppVersion(),
                settings = settingsBackup,
                metadata = mapOf(
                    "platform" to "Android",
                    "exportType" to "Settings"
                )
            )
            
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(userDataBackup)
            
            val exportDir = parameters.exportPath?.let { File(it) } 
                ?: File(context.getExternalFilesDir(null), "exports")
            
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${EXPORT_FILE_NAME}_$timestamp.json"
            val exportFile = File(exportDir, fileName)
            
            exportFile.writeText(jsonString)
            
            val fileSize = settingsUtils.formatCacheSize(exportFile.length())
            
            val result = ExportResult(
                success = true,
                filePath = exportFile.absolutePath,
                fileSize = fileSize,
                message = "用户数据导出成功"
            )
            
            TimberLogger.d(TAG, "用户数据导出成功: ${exportFile.absolutePath}, 大小: $fileSize")
            return result
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "导出用户数据失败", e)
            
            return ExportResult(
                success = false,
                filePath = "",
                fileSize = "0B",
                message = "导出失败: ${e.message}"
            )
        }
    }
    
    private fun getAllCustomSettings(): Map<String, String> {
        return try {
            mapOf(
                "lastBackupTime" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                "settingsVersion" to "1.0"
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取自定义设置失败", e)
            emptyMap()
        }
    }
    
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            // 根据系统版本，安全地获取版本号
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION") // 忽略旧版 versionCode 的“已过时”警告
                packageInfo.versionCode.toLong()
            }
            "${packageInfo.versionName} ($versionCode)"
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取应用版本失败", e)
            "Unknown"
        }
    }
} 