package com.novel.utils

import android.content.Context
import android.content.SharedPreferences
import com.novel.page.component.LaunchDialogType
import kotlin.random.Random

/**
 * 弹窗启动管理器
 *
 * 负责管理应用启动时的弹窗显示逻辑：
 * - 0~0.3 概率显示版本升级提醒
 * - 0.3~0.6 概率显示签到赠金提示
 * - 其他概率不显示弹窗
 */
class DialogLaunchManager private constructor(context: Context) {

    companion object {
        private const val TAG = "DialogLaunchManager"
        private const val PREFS_NAME = "dialog_launch_prefs"
        private const val KEY_LAST_DIALOG_SHOW_TIME = "last_dialog_show_time"
        private const val KEY_DIALOG_SHOW_COUNT = "dialog_show_count"

        @Volatile
        private var INSTANCE: DialogLaunchManager? = null

        fun getInstance(context: Context): DialogLaunchManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DialogLaunchManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * 获取需要显示的弹窗类型
     * 根据随机概率决定显示哪个弹窗
     *
     * @return 需要显示的弹窗类型，null表示不显示弹窗
     */
    fun getDialogTypeToShow(): LaunchDialogType? {
        val now = System.currentTimeMillis()
        val lastShowTime = prefs.getLong(KEY_LAST_DIALOG_SHOW_TIME, 0)
        val showCount = prefs.getInt(KEY_DIALOG_SHOW_COUNT, 0)

        // 可选：添加一些限制条件，比如每天只显示一次
        val oneDayInMillis = 24 * 60 * 60 * 1000L
        if (now - lastShowTime < oneDayInMillis) return null

        // 生成 0.0 到 1.0 之间的随机数
        val randomValue = Random.nextFloat()

        TimberLogger.d(TAG, "随机值: $randomValue, 显示次数: $showCount")

        val dialogType = when {
            randomValue < 0.3f -> {
                TimberLogger.d(TAG, "概率命中：显示版本升级提醒")
                LaunchDialogType.UPDATE_TIP
            }

            randomValue < 0.7f -> {
                TimberLogger.d(TAG, "概率命中：显示签到赠金提示")
                LaunchDialogType.SIGNIN_BONUS
            }

            else -> {
                TimberLogger.d(TAG, "概率命中：不显示弹窗")
                null
            }
        }

        // 记录显示状态
        if (dialogType != null) {
            markDialogShown()
        }

        return dialogType
    }

    /**
     * 标记弹窗已显示
     */
    private fun markDialogShown() {
        val now = System.currentTimeMillis()
        val showCount = prefs.getInt(KEY_DIALOG_SHOW_COUNT, 0)

        prefs.edit()
            .putLong(KEY_LAST_DIALOG_SHOW_TIME, now)
            .putInt(KEY_DIALOG_SHOW_COUNT, showCount + 1)
            .apply()

        TimberLogger.d(TAG, "记录弹窗显示时间: $now")
    }

    /**
     * 清除弹窗显示记录（用于测试）
     */
    fun clearDialogHistory() {
        prefs.edit().clear().apply()
        TimberLogger.d(TAG, "清除弹窗历史记录")
    }

    /**
     * 获取弹窗显示统计信息
     */
    fun getDialogStats(): DialogStats {
        return DialogStats(
            lastShowTime = prefs.getLong(KEY_LAST_DIALOG_SHOW_TIME, 0),
            showCount = prefs.getInt(KEY_DIALOG_SHOW_COUNT, 0)
        )
    }
}

/**
 * 弹窗显示统计信息
 */
data class DialogStats(
    val lastShowTime: Long,
    val showCount: Int
)