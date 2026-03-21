# Phase 6 性能预算摘要

| 区域 | 指标 | 当前值 | 预算 / 目标 | 状态 | 证据 |
| --- | --- | --- | --- | --- | --- |
| 启动 | `timeToInitialDisplayMs` 中位数 | `654.4 ms`（`startupNoCompilation`） | `DN2101` debug no-compilation `<= 700 ms` | `green` | `startup-benchmark-run-2026-03-21.md` |
| 启动 | `timeToInitialDisplayMs` 中位数 | `663.8 ms`（`startup`） | 默认绿色套件 `<= 700 ms` | `green` | `startup-benchmark-run-2026-03-21.md` |
| 首页滚动 | `frameDurationCpuMs P95` | `20.9 ms` | `<= 24 ms` | `green` | `scroll-benchmark-run-2026-03-21.md` |
| 首页滚动 | `frameOverrunMs P95` | `7.4 ms` | `<= 10 ms` | `green` | `scroll-benchmark-run-2026-03-21.md` |
| 搜索结果页 | route jump -> success log | `~945 ms` | `<= 1200 ms` | `green` | `search-performance-baseline-2026-03-21.md` |
| Reader 初始化 | route jump -> first ReaderPage load log | `~266 ms` | `<= 500 ms` | `green` | `reader-performance-baseline-2026-03-21.md` |
| Reader 翻页 | direct flip sample | 当前运行缺失 | 后续若要升级成硬门禁，需补 `FlipPageUseCase` 直接样本 | `documented gap` | `reader-performance-baseline-2026-03-21.md` |
| Reader 设置 | direct settings update sample | 当前运行缺失 | 后续若要升级成硬门禁，需补 `UpdateSettingsUseCase` 直接样本 | `documented gap` | `reader-performance-baseline-2026-03-21.md` |
| Welfare 初始化 | `InitializeWelfarePageUseCase` | `313-555 ms` | `<= 600 ms` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| Welfare WebView | WebView load completion | `266 ms` | `<= 400 ms` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| RN Host Profile | route jump -> RN context ready | `~2.08 s` | `<= 2.5 s` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| RN Host Settings | route jump -> settings load success | `~3.00 s` | `<= 3.5 s` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| RN Host AI | route jump -> RN context ready | `~3.77 s` | `<= 4.0 s` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| Baseline Profile | connected generation | 受设备 compile 阻塞 | 要么生成成功，要么保持“可复现 blocker + remediation path” | `accepted blocker` | `baseline-profile-run-2026-03-21.md` |
| 设备 compile | `cmd package compile` on `com.novel` and `com.android.settings` | 两个包都失败 | 需在第二设备上复验，才能升级 compiled-mode 门禁 | `accepted blocker` | `device-compile-blocker-2026-03-21.md` |
| Bridge 契约 | 运行时语义 | 依靠 smoke + contract + host rerun evidence 保持绿色 | 不允许 route / payload 漂移 | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
