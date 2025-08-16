package com.novel.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Novel应用的启动性能测试
 * 
 * 测试不同编译模式下的启动性能：
 * 1. 无编译 - 模拟首次安装后的启动
 * 2. 基准配置文件 - 使用Baseline Profile优化后的启动
 * 3. 完全AOT编译 - 全量预编译的启动性能上限
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ExampleStartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
        
        // 等待首页关键内容加载完成
        device.wait(Until.hasObject(By.text("推荐")), 10_000)
    }

    @Test
    fun startupNoCompilation() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None()
    ) {
        pressHome()
        startActivityAndWait()
        
        // 等待首页关键内容加载完成
        device.wait(Until.hasObject(By.text("推荐")), 10_000)
    }

    @Test
    fun startupBaselineProfile() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Partial()
    ) {
        pressHome()
        startActivityAndWait()
        
        // 等待首页关键内容加载完成
        device.wait(Until.hasObject(By.text("推荐")), 10_000)
    }

    @Test
    fun startupFullCompilation() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Full()
    ) {
        pressHome()
        startActivityAndWait()
        
        // 等待首页关键内容加载完成
        device.wait(Until.hasObject(By.text("推荐")), 10_000)
    }
}

/**
 * Novel应用的滚动性能测试
 * 
 * 测试首页LazyColumn滚动时的帧率表现
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ScrollPerformanceBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollingPerformance() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM
    ) {
        scrollingActions()
    }

    @Test
    fun scrollingPerformanceBaselineProfile() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.Partial()
    ) {
        scrollingActions()
    }

    private fun MacrobenchmarkScope.scrollingActions() {
        pressHome()
        startActivityAndWait()
        
        // 等待首页加载
        device.wait(Until.hasObject(By.text("推荐")), 10_000)
        
        // 找到可滚动的LazyColumn
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
    }
} 