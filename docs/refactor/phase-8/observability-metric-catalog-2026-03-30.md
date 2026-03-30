# Phase 8 Observability Metric Catalog

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 4 / Phase 8`
- 当前结论：`已固定仓库内 observability 指标目录与当前宿主入口`

## 目的
- 把当前已经散落在 startup、network trace、bridge、welfare/WebView 中的局部观测能力收成统一目录。
- 明确“仓库里现在真的有些什么指标”和“还没有什么平台能力”，避免把 Phase 8 写成愿景。

## 指标目录
### 启动
| Area | Metric | Source | Current Host |
| --- | --- | --- | --- |
| Startup | `process_start` | `StartupPerformanceMonitor.startProcessMonitoring()` | `android/app/src/main/java/com/novel/utils/performance/StartupPerformanceMonitor.kt` |
| Startup | `application_create` | `onApplicationCreateStart/End()` | `StartupPerformanceMonitor` |
| Startup | `first_activity` | `onFirstActivityCreate()` | `StartupPerformanceMonitor` |
| Startup | `first_frame` | `onFirstFrameDrawn()` | `StartupPerformanceMonitor` |
| Startup | `fully_loaded` | `onAppFullyLoaded()` | `StartupPerformanceMonitor` |
| Startup | `componentMetrics` | `recordComponentInitTime()` | `StartupPerformanceMonitor` |
| Startup | `memoryUsage` | `getCurrentMemoryMetrics()` | `StartupPerformanceMonitor` |

### Welfare / WebView
| Area | Metric | Source | Current Host |
| --- | --- | --- | --- |
| Welfare | `pageLoadTime` | `recordPageLoadComplete()` | `android/feature-welfare/src/main/java/com/novel/page/welfare/utils/WelfarePerformanceMonitor.kt` |
| Welfare | `firstContentfulPaint` | `recordFirstContentfulPaint()` | `WelfarePerformanceMonitor` |
| Welfare | `timeToInteractive` | `recordTimeToInteractive()` | `WelfarePerformanceMonitor` |
| Welfare | `memoryUsage` | `startMemoryMonitoring()` | `WelfarePerformanceMonitor` |
| Welfare | `networkLatency` | `recordNetworkLatency()` | `WelfarePerformanceMonitor` |
| Welfare | `errorCount` | `recordError()` | `WelfarePerformanceMonitor` |
| Welfare | `retryCount` | `recordRetry()` | `WelfarePerformanceMonitor` |
| Welfare | `webview_load` | `recordWebViewLoadStart/Complete()` | `WelfarePerformanceMonitor` |

### Network / Trace
| Area | Metric | Source | Current Host |
| --- | --- | --- | --- |
| Network | `X-Request-Id` | `RequestIdInterceptor.REQUEST_ID_HEADER` | `android/core-network/src/main/java/com/novel/utils/network/interceptor/RequestIdInterceptor.kt` |
| Network | `X-Trace-Id` | `RequestIdInterceptor.TRACE_ID_HEADER` | `RequestIdInterceptor` |
| Network | `legacy dispatch trace log` | `NetworkTraceLogHelper.formatLegacyDispatch()` | `android/core-network/src/main/java/com/novel/utils/network/NetworkTraceLogHelper.kt` |
| Network | `okhttp dispatch trace log` | `NetworkTraceLogHelper.formatOkHttpDispatch()` | `NetworkTraceLogHelper` |
| Bridge Network | `bridge dispatch trace log` | `NetworkTraceLogHelper.formatBridgeDispatch()` | `NavigationBridgeNetworkGateway` |

### Bridge / Error
| Area | Metric | Source | Current Host |
| --- | --- | --- | --- |
| Bridge | `TIMEOUT_ERROR` mapping | `BridgePromiseErrorMapper.map()` | `android/core-bridge/src/main/java/com/novel/rn/bridge/BridgePromiseErrorMapper.kt` |
| Bridge | `defaultCode + AppError message` mapping | `BridgePromiseErrorMapper.map()` | `BridgePromiseErrorMapper` |
| Error | `AppError.Timeout / Network / Serialization / Unexpected` | `AppError.fromThrowable()` | `android/core-common/src/main/java/com/novel/core/result/AppError.kt` |

## 当前证据入口
- 启动与性能：
  - `docs/refactor/phase-6/performance-budget-summary.md`
  - `docs/refactor/evidence/phase6-startup-logcat-2026-03-27.txt`
- Welfare / WebView：
  - `docs/refactor/evidence/welfare-webview-multisample-2026-03-28.txt`
  - `docs/refactor/evidence/welfare-webview-path-matrix-logcat-2026-03-28.txt`
- Request / trace：
  - `android/core-network/src/test/java/com/novel/utils/network/interceptor/RequestIdInterceptorTest.kt`
  - `android/core-network/src/test/java/com/novel/utils/network/NetworkTraceLogHelperTest.kt`
- Bridge / AppError：
  - `android/core-bridge/src/test/java/com/novel/rn/bridge/BridgePromiseErrorMapperTest.kt`
  - `android/core-common/src/test/java/com/novel/core/result/AppErrorTest.kt`

## 当前缺口
- 当前没有统一的 Crash 平台接入。
- 当前没有统一的 ANR 平台接入。
- 当前没有统一的 dashboard / alerting 平台。
- 当前没有“所有指标都统一进单一 SDK”的实现。

## 使用规则
- 新增 metric 前，先判断它属于：
  - Startup
  - Welfare / WebView
  - Network / Trace
  - Bridge / Error
  - 其它新域
- 若是新域，先补目录文档，再补实现。
- 若只是扩展现有域，不要新起一套命名方式。

## 主要引用
- `docs/refactor/phases/phase-8-observability-rollout-governance.md`
- `docs/refactor/tracking/phase-7-8-validation-board.md`
