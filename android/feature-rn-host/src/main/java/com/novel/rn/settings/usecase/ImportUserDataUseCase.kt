package com.novel.rn.settings.usecase

import android.content.Context
import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.rn.settings.SettingsUtils
import com.novel.ui.theme.ThemeManager
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.core.logging.CoreLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

/**
 * 导入用户数据UseCase
 * 
 * 功能：
 * - 导入用户设置数据
 * - 验证备份文件
 * - 恢复用户配置
 */
@Stable
class ImportUserDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsUtils: SettingsUtils,
    private val themeManager: ThemeManager,
    private val novelUserDefaults: NovelUserDefaults
) : BaseUseCase<ImportUserDataUseCase.ImportParams, ImportUserDataUseCase.ImportResult>() {

    companion object {
        private const val TAG = "ImportUserDataUseCase"
    }

    data class ImportParams(
        val filePath: String,
        val overwriteExisting: Boolean = true,
        val validateOnly: Boolean = false // 仅验证文件有效性
    )

    data class ImportResult(
        val success: Boolean,
        val message: String,
        val importedSettings: Int = 0,
        val skippedSettings: Int = 0,
        val backupInfo: BackupInfo? = null
    )

    data class BackupInfo(
        val exportTime: String,
        val appVersion: String,
        val platform: String,
        val settingsCount: Int
    )

    override suspend fun execute(parameters: ImportParams): ImportResult {
        CoreLogger.d(TAG, "开始导入用户数据: ${parameters.filePath}")
        
        try {
            val importFile = File(parameters.filePath)
            if (!importFile.exists()) {
                return ImportResult(
                    success = false,
                    message = "备份文件不存在: ${parameters.filePath}"
                )
            }
            
            // 读取并解析文件
            val jsonString = importFile.readText()
            val json = Json { ignoreUnknownKeys = true }
            
            val userDataBackup = try {
                json.decodeFromString<ExportUserDataUseCase.UserDataBackup>(jsonString)
            } catch (e: SerializationException) {
                CoreLogger.e(TAG, "解析备份文件失败", e)
                return ImportResult(
                    success = false,
                    message = "备份文件格式错误: ${e.message}"
                )
            }
            
            val backupInfo = BackupInfo(
                exportTime = userDataBackup.exportTime,
                appVersion = userDataBackup.appVersion,
                platform = userDataBackup.metadata["platform"] ?: "Unknown",
                settingsCount = countSettings(userDataBackup.settings)
            )
            
            // 如果只是验证，直接返回
            if (parameters.validateOnly) {
                return ImportResult(
                    success = true,
                    message = "备份文件验证成功",
                    backupInfo = backupInfo
                )
            }
            
            // 执行导入
            var importedCount = 0
            var skippedCount = 0
            
            val settings = userDataBackup.settings
            
            // 导入主题设置
            try {
                if (parameters.overwriteExisting || !hasExistingThemeSettings()) {
                    settingsUtils.setNightMode(settings.themeMode)
                    settingsUtils.setFollowSystemTheme(settings.followSystemTheme)
                    settingsUtils.setAutoNightMode(settings.autoNightMode)
                    settingsUtils.setNightModeTime(settings.nightModeStartTime, settings.nightModeEndTime)
                    
                    // 应用主题设置
                    themeManager.setThemeMode(settings.themeMode)
                    
                    importedCount += 4 // 主题相关的4个设置项
                    CoreLogger.d(TAG, "主题设置导入成功")
                } else {
                    skippedCount += 4
                    CoreLogger.d(TAG, "跳过主题设置（已存在且不覆盖）")
                }
            } catch (e: Exception) {
                CoreLogger.e(TAG, "导入主题设置失败", e)
                skippedCount += 4
            }
            
            // 导入自定义设置
            settings.customSettings.forEach { (key, value) ->
                try {
                    if (parameters.overwriteExisting || novelUserDefaults.getString(key) == null) {
                        novelUserDefaults.setString(key, value)
                        importedCount++
                    } else {
                        skippedCount++
                    }
                } catch (e: Exception) {
                    CoreLogger.e(TAG, "导入自定义设置失败: $key", e)
                    skippedCount++
                }
            }
            
            val result = ImportResult(
                success = true,
                message = "用户数据导入完成",
                importedSettings = importedCount,
                skippedSettings = skippedCount,
                backupInfo = backupInfo
            )
            
            CoreLogger.d(TAG, "用户数据导入成功: 导入${importedCount}项，跳过${skippedCount}项")
            return result
            
        } catch (e: Exception) {
            CoreLogger.e(TAG, "导入用户数据失败", e)
            
            return ImportResult(
                success = false,
                message = "导入失败: ${e.message}"
            )
        }
    }
    
    private fun hasExistingThemeSettings(): Boolean {
        return novelUserDefaults.getString("night_mode") != null
    }
    
    private fun countSettings(settings: ExportUserDataUseCase.SettingsBackup): Int {
        return 4 + settings.customSettings.size // 4个主题设置 + 自定义设置
    }
} 

