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
 * Novel应用的启动性能测试。
 *
 * 默认套件只保留 `CompilationMode.None()` 路径，
 * 避免设备侧 `cmd package compile` blocker 让日常 baseline 全部变红。
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
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None()
    ) {
        waitForHomeReady()
    }

    @Test
    fun startupNoCompilation() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None()
    ) {
        waitForHomeReady()
    }
}

/**
 * Novel应用的首页滚动性能测试。
 *
 * 默认滚动基线同样只保留 no-compilation 路径，
 * profile/compile 相关探针单独放到 probe 套件。
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
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.None()
    ) {
        performHomeScrollActions()
    }
}

internal fun MacrobenchmarkScope.waitForHomeReady() {
    pressHome()
    startActivityAndWait()
    device.wait(Until.hasObject(By.text("推荐")), 10_000)
}

internal fun MacrobenchmarkScope.performHomeScrollActions() {
    waitForHomeReady()

    device.findObject(By.scrollable(true))?.apply {
        setGestureMargin(device.displayWidth / 10)

        repeat(3) {
            fling(Direction.DOWN)
            device.waitForIdle()
        }
        repeat(3) {
            fling(Direction.UP)
            device.waitForIdle()
        }
    }
}
