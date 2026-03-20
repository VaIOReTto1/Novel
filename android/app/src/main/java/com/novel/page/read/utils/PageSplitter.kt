package com.novel.page.read.utils

import com.novel.utils.TimberLogger
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.utils.HtmlTextUtil
import com.novel.utils.wdp
import java.util.concurrent.ConcurrentHashMap

/**
 * 高性能文本分页工具 —— **v3.1 (2025‑06‑30)**
 *
 * ## 本次更新：支持「首页标题高度」精准扣除
 * 首页默认在内容上方绘制章节名（字体 size +4sp，底部 16dp 间距）。v3.0 仅粗略减 2 行，
 * 大字号设备或长标题易溢出。现依据 **标题实际像素高度 + 间距** 动态计算首页可用行数。
 *
 * ### 改动要点
 * 1. 计算标题行高：`titleFontPx = (fontSize +4).sp` → `titleLineHeight = titleFontPx * 1.5`。
 * 2. 计算标题下边距：`16.dp` → `titleBottomPx`。
 * 3. `firstPageMaxLines = ((availH - titleHeight - titleBottomPx) / lineHeight)`。
 * 4. 其余算法保持不变，对后续页无影响。成本约 +0.02 ms。
 */
class PageSplitter private constructor() {

    companion object {
        private const val TAG = ReaderLogTags.PAGE_SPLITTER
        private const val LINE_SPACING_MULTIPLIER = 1.5f
        private const val MAX_BACKTRACK = 2 // 行首/行尾回退上限

        // ————————————————— 禁则字符 —————————————————
        val HEAD_PROHIBITED_FLAGS = BooleanArray(65536)
        val TAIL_PROHIBITED_FLAGS = BooleanArray(65536)

        private val HEAD_PROHIBITED_CHARS = charArrayOf(
            '，','。','、','；','：','！','？','）','】','》','』','」','”','’','﹀','﹂'
        )
        private val TAIL_PROHIBITED_CHARS = charArrayOf(
            '（','【','《','『','「','“','‘','〔','￥'
        )

        init {
            for (c in HEAD_PROHIBITED_CHARS) HEAD_PROHIBITED_FLAGS[c.code] = true
            for (c in TAIL_PROHIBITED_CHARS) TAIL_PROHIBITED_FLAGS[c.code] = true
        }

        @JvmStatic
        fun Char.isHeadProhibited(): Boolean = HEAD_PROHIBITED_FLAGS[this.code]
        @JvmStatic
        fun Char.isTailProhibited(): Boolean = TAIL_PROHIBITED_FLAGS[this.code]

        // ————————————————— TextMetrics 缓存 —————————————————
        private data class Metrics(val avgCharW: Float, val lineHeight: Float)
        private val metricsCache = ConcurrentHashMap<Int /*fontPx*/, Metrics>()

        private fun obtainMetrics(fontPx: Int): Metrics =
            metricsCache.getOrPut(fontPx) {
                val paint = android.text.TextPaint()
                paint.isAntiAlias = true
                paint.textSize = fontPx.toFloat()
                val avg = paint.measureText("测")
                Metrics(avg, fontPx * LINE_SPACING_MULTIPLIER)
            }

        // ————————————————— StringBuilder 池 —————————————————
        private val pageBuilderPool = object : ThreadLocal<StringBuilder>() {
            override fun initialValue(): StringBuilder = StringBuilder(2048)
        }
        private val lineBuilderPool = object : ThreadLocal<StringBuilder>() {
            override fun initialValue(): StringBuilder = StringBuilder(128)
        }

        /**
         * 将章节内容按当前屏幕尺寸 & 阅读设置进行分页。
         */
        fun splitContent(
            content: String,
            containerSize: IntSize,
            readerSettings: ReaderSettings,
            density: Density
        ): List<String> {
            val t0 = System.nanoTime()

            // -------- 1. 参数校验 --------
            if (content.isBlank() || containerSize.width <= 0 || containerSize.height <= 0) {
                TimberLogger.w(TAG, "参数无效，返回原始内容")
                return listOf(content)
            }

            // -------- 2. HTML → 纯文本 --------
            val plain = HtmlTextUtil.cleanHtml(content).trim()

            // -------- 3. 可用宽高 --------
            val hPaddingPx = with(density) { 32.wdp.toPx() }
            val vPaddingPx = with(density) { 20.wdp.toPx() }
            val navFontPx = with(density) { 10.sp.toPx() }
            val navHTop = navFontPx * LINE_SPACING_MULTIPLIER + with(density) { 12.wdp.toPx() }
            val navHBottom = navFontPx * LINE_SPACING_MULTIPLIER + with(density) { 3.wdp.toPx() }
            val availW = containerSize.width - hPaddingPx
            val availH = containerSize.height - vPaddingPx - navHTop - navHBottom

            // -------- 4. 获取度量 --------
            val fontPx = with(density) { readerSettings.fontSize.sp.toPx().toInt() }
            val metrics = obtainMetrics(fontPx)
            val maxCharsPerLine = (availW / metrics.avgCharW).toInt().coerceAtLeast(1)
            val baseLinesPerPage = (availH / metrics.lineHeight).toInt().coerceAtLeast(1)

            // -------- 4.1 首页标题高度扣除 --------
            val titleFontPx = with(density) { (readerSettings.fontSize + 4).sp.toPx() }
            val titleLineHeight = titleFontPx * LINE_SPACING_MULTIPLIER
            val titleBottomPx = with(density) { 16.wdp.toPx() }
            val firstPageMaxLines = ((availH - titleLineHeight - titleBottomPx) / metrics.lineHeight).toInt().coerceAtLeast(1)

            // -------- 5. 分页核心 --------
            val pages = mutableListOf<String>()
            val pageBuilder = pageBuilderPool.get()!!.apply { setLength(0) }
            val lineBuilder = lineBuilderPool.get()!!.apply { setLength(0) }

            var currentLineCount = 0
            var currentPageLimit = firstPageMaxLines
            var isFirstPage = true

            fun flushLine(forceSavePage: Boolean = false) {
                if (lineBuilder.isEmpty()) {
                    // 如果纯 pageBuilder 有内容且强制保存
                    if (forceSavePage && pageBuilder.isNotEmpty()) {
                        pages.add(pageBuilder.trimEnd().toString())
                        pageBuilder.setLength(0)
                    }
                    return
                }
                // 行尾禁则：回退不超过 MAX_BACKTRACK
                var endIdx = lineBuilder.length
                var back = 0
                while (back < MAX_BACKTRACK && endIdx > 0 && lineBuilder[endIdx - 1].isTailProhibited()) {
                    endIdx--; back++
                }
                pageBuilder.append(lineBuilder, 0, endIdx).append('\n')
                if (endIdx < lineBuilder.length) {
                    lineBuilder.delete(0, endIdx)
                } else lineBuilder.setLength(0)
                currentLineCount++

                if (currentLineCount >= currentPageLimit || forceSavePage) {
                    pages.add(pageBuilder.trimEnd().toString())
                    pageBuilder.setLength(0)
                    currentLineCount = 0
                    if (isFirstPage) {
                        isFirstPage = false
                        currentPageLimit = baseLinesPerPage
                    }
                }
            }

            val rawLines = plain.splitToSequence('\n')
            for (raw in rawLines) {
                if (raw.isBlank()) {
                    flushLine()
                    continue
                }
                var idx = 0
                val len = raw.length
                while (idx < len) {
                    var tentativeEnd = (idx + maxCharsPerLine).coerceAtMost(len)

                    // 行首禁则：最多回退 MAX_BACKTRACK
                    var back = 0
                    while (back < MAX_BACKTRACK && tentativeEnd < len && raw[tentativeEnd].isHeadProhibited() && tentativeEnd > idx) {
                        tentativeEnd--; back++
                    }
                    // 行尾禁则：最多回退 MAX_BACKTRACK
                    back = 0
                    while (back < MAX_BACKTRACK && tentativeEnd > idx && raw[tentativeEnd - 1].isTailProhibited()) {
                        tentativeEnd--; back++
                    }
                    lineBuilder.append(raw, idx, tentativeEnd)
                    flushLine()
                    idx = tentativeEnd
                }
            }
            if (lineBuilder.isNotEmpty() || pageBuilder.isNotEmpty()) flushLine(true)
            if (pageBuilder.isNotEmpty()) {
                pages.add(pageBuilder.trimEnd().toString())
            }

            val t1 = System.nanoTime()
            TimberLogger.d(TAG, "分页完成：${pages.size}页，用时${(t1 - t0) / 1_000_000}ms")

            return if (pages.isEmpty()) listOf(plain) else pages
        }
    }
}
