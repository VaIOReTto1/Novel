package com.novel.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 设备编译链专项探针。
 *
 * 这些用例允许失败，并且失败本身就是 Phase 6 的 blocker 证据。
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupCompilationProbeBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupBaselineProfile() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Partial()
    ) {
        waitForHomeReady()
    }

    @Test
    fun startupFullCompilation() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.Full()
    ) {
        waitForHomeReady()
    }
}

@RunWith(AndroidJUnit4::class)
@LargeTest
class ScrollCompilationProbeBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollingPerformanceBaselineProfile() = benchmarkRule.measureRepeated(
        packageName = "com.novel",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.Partial()
    ) {
        performHomeScrollActions()
    }
}
