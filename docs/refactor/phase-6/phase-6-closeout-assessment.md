# Phase 6 关闭评审与签字确认报告

## 摘要
- 阶段：`Phase 6 - 性能专项与基准治理`
- 关闭状态：`validated`
- 评审目标：确认 `Phase 6` 已在 `Phase 5` 验证后的模块边界上建立启动、滚动、搜索、Reader、Welfare/WebView、RN Host / Bridge 的可追溯性能证据与预算入口，并为 `Phase 7` 固定进入条件。
- 发布结论：`允许关闭`

## 评审范围
- `docs/refactor/phases/phase-6-performance-governance.md`
- `docs/refactor/phase-6/stage-3-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/startup-benchmark-run-2026-03-21.md`
- `docs/refactor/phase-6/scroll-benchmark-run-2026-03-21.md`
- `docs/refactor/phase-6/baseline-profile-run-2026-03-21.md`
- `docs/refactor/phase-6/device-compile-blocker-2026-03-21.md`
- `docs/refactor/phase-6/search-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/reader-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/performance-budget-summary.md`
- `docs/refactor/phase-6/phase-6-optimization-opportunity-catalog.md`
- `docs/refactor/blueprint-v2-phase-3-6-gap-analysis.md`
- `docs/refactor/tracking/phase-5-6-validation-board.md`
- `docs/refactor/stage-3-closeout-summary.md`

## 评审方法
### 第一轮主审
- 逐项复核 `V6-01 ~ V6-06` 是否都有正式 evidence 文档。
- 检查启动 / 滚动 benchmark、搜索 / Reader / WebView / RN Host 日志样本和预算摘要是否互相可追溯。

### 第二轮主审
- 二次逐字检查：
  - 是否仍把 `DN2101` 的设备 compile 阻塞项误写成仓库回归。
  - 是否把尚未取得的 Reader flip / settings direct sample 伪装成已测量结果。
  - 是否已经把 `Phase 7` 入口固定在包体积、依赖与构建效率治理。

### 作者闭环
- 作者统一以“证据落盘 + 已接受阻塞项声明 + 阶段状态切换 + 优化欠账留痕”完成闭环。

## V6-01 ~ V6-06 结论
| ID | 结论 | 关键依据 | 关闭判断 |
| --- | --- | --- | --- |
| V6-01 | Stage 3 性能 baseline 已形成可追溯入口 | `stage-3-performance-baseline-2026-03-21.md` 与全部专项 evidence 文档 | `green` |
| V6-02 | 启动预算成立，baseline profile 阻塞项已被客观固化 | startup benchmark 通过、baseline profile run 阻塞、direct device compile blocker | `green` |
| V6-03 | 首页 / 搜索 / Reader 关键链路均已有正式 baseline 文档 | scroll benchmark、search log sample、reader baseline 文档 | `green` |
| V6-04 | Welfare / WebView / Bridge 已形成聚合专项文档 | welfare log sample、RN Host rerun、bridge contract / RN smoke | `green` |
| V6-05 | 性能观测模板已闭环 | `performance-budget-summary.md` 与统一 run-doc 模板 | `green` |
| V6-06 | Stage 3 关闭总结与 Phase 7 进入条件已明确 | `stage-3-closeout-summary.md`、`phase-7-size-dependency-build-governance.md` | `green` |

## 代码与文档产出摘要
### Benchmark 主线
- 默认绿色套件现已固定为：
  - `ExampleStartupBenchmark`
  - `ScrollPerformanceBenchmark`
- 编译型探针被拆出到：
  - `StartupCompilationProbeBenchmark`
  - `ScrollCompilationProbeBenchmark`
- 因此日常 baseline 不会再被 `DN2101` 的设备 compile 噪声整套打红。

### Phase 6 证据包
- 已落盘：
  - 启动 benchmark 文档
  - 滚动 benchmark 文档
  - baseline profile 运行记录
  - direct device compile 阻塞项文档
  - search / reader / welfare-webview-host 专项文档
  - 统一 `performance-budget-summary.md`

### 阶段收尾
- `Phase 6` 门禁文档、看板、控制面板与 `Stage 3 关闭总结` 已切换到关闭口径。
- `Phase 7` 入口文档已补齐，避免关闭后出现“状态已切换但无权威宿主文档”的问题。

## 与原始蓝图的差异
- 详见：
  - `docs/refactor/blueprint-v2-phase-3-6-gap-analysis.md`
- 就 `Phase 6` 而言，最关键的差异是：
  - 本轮完成的是 `baseline + evidence + budget + blocker 固化`
  - 不是原始蓝图意义上的“系统性优化全部完成”
- 因此：
  - `Phase 6 validated` 成立
  - 但“已经做完所有性能优化”这一说法不成立

## 未完成优化的承接说明
- 详见：
  - `docs/refactor/phase-6/phase-6-optimization-opportunity-catalog.md`
- 当前仍明确存在后续可继续推进的优化机会，主要集中在：
  - 启动 warm / cold path 的进一步分流、compiled-mode 二设备复验与收益核算
  - Reader flip / settings 动作级样本与进一步热点治理
  - Welfare / WebView 更深层 macrobenchmark 与重复上报治理
  - 搜索结果页 benchmark 化与分页 / 筛选热点治理
  - RN Host 生命周期、批量调用与线程切换进一步优化
- 这些项应进入后续性能专项待办池，而不应在文档叙述中被算作“已完成”。

## Closeout 后继续推进的优化
- 以下提交均发生在 `Phase 6 validated` 之后，用于继续消化 backlog 与追平仓库事实；它们不重开 `Phase 6` 状态：
  - `bad7f5f` `补齐网络请求追踪头`
    - 在 `NetworkModule` 中接入 `RequestIdInterceptor`，为 `OkHttp` 请求默认注入 `X-Request-Id / X-Trace-Id`。
    - 影响：`Phase 3` 的 request / trace id 能力应从“未实现”修正为“部分实现”。
  - `c9f1f8e` `收敛阅读器初始化与分页刷新`
    - 通过 `ReaderStartupCoordinator` 和 `ReaderSettingsRefreshCoordinator` 去除重复初始化与过宽的分页刷新触发。
    - 影响：Reader 不再只是“拿到 init baseline”，而是已经落了首轮真实优化。
  - `收敛阅读器恢复提示与动作探针`
    - 通过 `ReaderRestoreHintCoordinator` 把恢复提示改为在恢复入口真正 ready 后显示，并为 `init / flip / settings_update` 补上轻量性能 probe。
    - 影响：Reader 已从“只有 init 样本”推进到“动作级 probe 已接通，后续可围绕日志继续做预算化取证”。
  - `666cc72` `收敛福利页初始化路径`
    - 通过 `WelfarePageBootstrapCoordinator` 收敛 preload / performance monitor / `InitializePage` 的首次 bootstrap。
    - 影响：Welfare / WebView 不能再继续按“存在重复初始化嫌疑但尚未处理”描述。
  - `f679aa6` `延后搜索分类筛选加载`
    - 通过 `SearchCategoryFilterLoadCoordinator` 将 `loadCategoryFilters()` 延后到真正需要时执行。
    - 影响：Search 首开结果页的非必要负担已完成第一轮治理。
  - `bda823f` `补发RN主题同步`
    - 通过 `ReactNativeThemeSyncCoordinator` 在 RN context 就绪后补发主题同步。
    - 影响：`ThemeChanged` 不应再被描述为“context 未就绪时永久跳过”。
  - `28ec92d` `延后非关键启动初始化`
    - 通过 `StartupDeferredInitializationCoordinator` 把 `RetrofitClient`、`SettingsUtils` 等初始化移到首帧后。
    - 影响：启动专项已从纯测量进入第一轮真实减负。
  - `955d9d0` `恢复首页分类栏首页入口`
    - 在 `android/app` 内恢复首页本地 special filter 的唯一展示语义为 `首页`，并兼容旧值 `推荐`。
    - 影响：这是 closeout 后继续收口的 app 层兼容修正，不改变 `Phase 5/6` 状态。
  - `bb75095` `延后RN预热到首帧后`
    - 通过 `ReactNativePrewarmCoordinator` 调整 RN 预热时机，避免在首屏创建期无条件预热。
    - 影响：启动 / RN Host 文档应按“已延后到首帧后”描述，而非“仍无条件预热”。
  - `463323c` `接通福利页WebView性能埋点`
    - 通过 `WelfareWebPerformanceCoordinator` 在 `WebViewComponent` 中接通 `FCP / TTI` 记录。
    - 影响：Welfare / WebView 文档应从“埋点代码存在但未接线”修正为“已接线，仍缺更深层治理”。

## 修订记录
| 日期 | 修订项 | 影响文档 | 结果 |
| --- | --- | --- | --- |
| 2026-03-21 | 将默认 startup/scroll benchmark 从 compiled-mode 噪声中拆出 | `android/macrobenchmark/**` | 完成 |
| 2026-03-21 | 补齐 Phase 6 正式 evidence package | `docs/refactor/phase-6/**`, `docs/refactor/evidence/**` | 完成 |
| 2026-03-21 | 关闭 `Phase 6` 并切换 `Phase 7 planned` | `README.md`, `phase-5-6-validation-board.md`, `stage-3-closeout-summary.md`, `decision-log.md` | 完成 |
| 2026-03-22 | 继续推进 closeout 后原子优化并追平文档事实 | `android/app/**`, `docs/refactor/**` | 完成 |

## 残余风险
- `DN2101` 的 `cmd package compile` 仍不可用；这已被降级为已接受的环境阻塞项，而不是 `com.novel` 回归。
- Reader 当前仍缺少可重复的：
  - flip action 直接数值样本
  - settings update 直接数值样本
- Search、Welfare/WebView、RN Host 仍缺更深层 benchmark / hotspot 级治理结论。
- 数据库索引收益、`FTS4` 复盘与缓存清理收益复盘仍未完成。

## 发布质量结论
- 结论：`通过`
- 说明：
  - `V6-01 ~ V6-06` 已全部闭环
  - 基线、预算、阻塞项和残余风险都已客观留痕
  - `Phase 7` 入口已固定，不需要重新解释 `Stage 3` 结论
  - 但本轮不应被表述为“性能优化已经做完”

## 签字确认
- `Author`：当前重构实施者 / signed / 2026-03-21
- `Primary Reviewer`：文档主审查者 / signed / 2026-03-21
- `Final Approver`：阶段门禁批准者 / signed / 2026-03-21
