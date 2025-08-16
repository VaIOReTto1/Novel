package com.novel.macrobenchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 更稳的 Baseline Profile 生成脚本
 * - 使用 setGestureMargin 防止系统边缘手势
 * - 使用 fling / swipe + waitForIdle 取代 Thread.sleep
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.novel"
    ) {
        // ---------- 冷启动 ----------
        pressHome()
        startActivityAndWait()

        // 等待首页完全加载
        device.wait(Until.hasObject(By.desc("首页")), 10_000)
        device.wait(Until.hasObject(By.text("推荐")), 5_000)

        // ---------- 首页滚动 ----------
        device.findObject(By.scrollable(true))?.apply {
            // 避免误触系统返回/多任务手势
            setGestureMargin(device.displayWidth / 10) // 10% 屏宽边距

            // 向下快速 fling 三次
            repeat(3) {
                fling(Direction.DOWN)
                device.waitForIdle()
            }
            // 向上回到顶部
            repeat(3) {
                fling(Direction.UP)
                device.waitForIdle()
            }
        }

        // ---------- 分类切换 ----------
        listOf("玄幻", "修真", "都市", "历史").forEach { category ->
            device.findObject(By.text(category))?.click()
            // 等待 RecyclerView 重绘完成而非 sleep
            device.waitForIdle()
        }
        device.findObject(By.text("推荐"))?.click()
        device.waitForIdle()

        // ---------- 榜单切换 ----------
        listOf("点击榜", "更新榜", "新书榜").forEach {
            device.findObject(By.text(it))?.click()
            device.waitForIdle()
        }

        // ---------- 书籍详情与阅读 ----------
        device.findObject(
            By.clickable(true).hasChild(By.clazz("android.widget.ImageView"))
        )?.let { firstBook ->
            firstBook.click()
            device.wait(Until.hasObject(By.text("开始阅读")), 5_000)

            // 详情页半屏 fling，避免一次到底触发刷新
            device.findObject(By.scrollable(true))
                ?.fling(Direction.DOWN)
            device.waitForIdle()

            device.findObject(By.text("开始阅读"))?.click()
            device.wait(Until.hasObject(By.desc("阅读页面")), 8_000)

            // 使用坐标 swipe 翻页（右→左），不依赖 sleep
            repeat(2) {
                // 25%→75% 水平 swipe
                device.swipe(
                    device.displayWidth * 3 / 4,
                    device.displayHeight / 2,
                    device.displayWidth / 4,
                    device.displayHeight / 2,
                    /*steps*/ 60
                )
                device.waitForIdle()
            }

            device.pressBack()
            device.wait(Until.hasObject(By.text("开始阅读")), 3_000)
            device.pressBack()
            device.wait(Until.hasObject(By.text("推荐")), 3_000)
        }

        // ---------- 搜索 ----------
        device.findObject(By.desc("搜索"))?.click()
        device.wait(Until.hasObject(By.clazz("android.widget.EditText")), 3_000)
        device.pressBack()

        // 等待所有动画完成
        device.waitForIdle()
    }
}
