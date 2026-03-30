# Phase 7 - 包体积、依赖与构建效率治理

## 目标
- 在 `Stage 3` 已关闭的稳定模块图基础上，建立稳定的：
  - artifact / size baseline
  - Gradle 与 npm 依赖治理入口
  - 构建效率与产物差异治理入口
- 把包体积、依赖和构建速度从“临时优化”升级为长期门禁对象。

## 范围
- APK / AAB / native libs / fonts / JS-native assets 体积盘点
- Gradle / npm 依赖冗余、重复与升级治理
- build graph、task hot path、增量构建与 clean build 对比
- artifact diff、dependency diff、build-time baseline

## 非目标
- 不重开 `Phase 6` 的性能专项
- 不借机继续扩大 `Phase 5` 的模块化范围
- 不改 UI 语义或业务功能语义

## 当前仓库入口基线
- 模块图以 `android/settings.gradle` 为准，当前已稳定为：
  - `app + core-common + core-ui + core-bridge + core-bridge-contract + core-storage + core-network + feature-home + feature-book + feature-login + feature-search + feature-reader + feature-rn-host + feature-welfare + macrobenchmark`
- `android/app/build.gradle` 已开启 release `minifyEnabled true` 与 `shrinkResources true`，Phase 7 从现有 shrink 基线继续推进，不重写 Phase 1。
- `android/gradle/verification-metadata.xml` 已存在，Gradle 依赖校验已有入口。
- `android/gradle/libs.versions.toml` 仍不存在，version catalog 尚未建立。
- 当前版本漂移已经显式存在：
  - `android/build.gradle` 与模块脚本中 Kotlin / Hilt / Compose 相关版本声明仍分散
  - Gradle / npm 两条依赖线还没有统一 inventory
- `android/gradle.properties` 仍保持 `org.gradle.configuration-cache=false`，需要在 Phase 7 形成“阻塞原因 + 是否值得开启”的显式结论。
- 包体积治理不能只盯 `res/font`，还需要覆盖：
  - `react-native-vector-icons` 字体拷贝链路
  - RN JS bundle 与 native asset 输出
  - AAB / APK 产物 diff

## 进入条件
- `Phase 6 = validated`
- `Stage 3 = validated`
- `Phase 6` 中的已接受阻塞项与残余风险已留痕，不再混入 `Phase 7` 主线

## 不承接的 Phase 6 性能债
- 以下内容不属于 `Phase 7` 主线，应留在后续性能专项待办池：
  - Reader flip / settings 动作级治理与直接样本补齐
  - 搜索结果页 benchmark 化与分页 / 筛选热点治理
  - Welfare / WebView 更深层专项与重复上报治理
  - RN Host 生命周期、warm / cold path、Bridge 批量调用与线程切换进一步优化
  - 数据库索引收益、`FTS4` 与缓存清理收益复盘
- 统一参考：
  - `docs/refactor/phase-6/phase-6-optimization-opportunity-catalog.md`
  - `docs/refactor/phase-6/database-index-and-fts-governance-2026-03-22.md`
  - `docs/refactor/phase-6/cache-cleanup-governance-2026-03-22.md`

## 协作编制
### Leader Mode
- `single leader / three helpers`

### Base Helper Count
- `3`

### Scale-Up Triggers
- 当 size / dependency / build 三条线都具备独立取证脚手架时，可短期扩容一名 helper。

### Scale-Down Triggers
- 当只剩文档收尾或单线清债时，回到最小编制。

### Agent Roster
- `SizeBudgetAgent`
  - 包体积、资源盘点、artifact diff
- `DependencyGraphAgent`
  - Gradle / npm 依赖治理
- `BuildEfficiencyAgent`
  - build hot path、task baseline、增量构建效率

### Lock Strategy
- `LOCK-SIZE-BUDGET`
- `LOCK-DEPENDENCY-GRAPH`
- `LOCK-BUILD-EFFICIENCY`

### Retry Window
- baseline / diff / build profiling：`2` 次同环境重跑
- 若两次后仍无法得到稳定结果，必须登记残余风险

### Escalation Window
- 任一优化要求改变业务功能、UI 语义、route 或 bridge payload
- 任一优化要求把 `Phase 7` 升级成新的架构重构阶段

### Leader-only Actions
- 串行执行 Gradle / npm 验证
- 统一 artifact diff / dependency diff / build-time 口径
- 同步看板、README、决策日志与关闭总结

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P7.1 | 固定 size baseline 与 artifact diff 入口 | 当前 APK/AAB/字体/JS-native assets 构成可追溯 |
| P7.2 | 固定 Gradle / npm dependency baseline | 版本分散、重复依赖与 catalog/BOM 路线明确 |
| P7.3 | 执行第一轮低风险 size shrink | 不改行为前提下取得可证实体积收益 |
| P7.4 | 执行第一轮 build efficiency 治理 | clean / incremental baseline、config cache 结论成立 |
| P7.5 | 输出 Phase 7 closeout 与 Phase 8 入口 | Stage 4 后半段入口清晰 |

## 交付物
- size baseline 文档
- dependency inventory / diff 文档
- build efficiency baseline 文档
- Phase 7 关闭总结文档

## 当前已落盘工件
- `docs/refactor/phase-7/size-baseline-and-artifact-entrypoints-2026-03-28.md`
- `docs/refactor/phase-7/dependency-inventory-and-governance-2026-03-28.md`
- `docs/refactor/phase-7/first-size-shrink-vector-icon-font-prune-2026-03-30.md`
- `docs/refactor/phase-7/build-efficiency-baseline-and-config-cache-2026-03-30.md`
- `docs/refactor/evidence/phase7-release-artifact-inventory-2026-03-28.json`
- `docs/refactor/evidence/phase7-release-artifact-inventory-2026-03-30.json`
- `docs/refactor/evidence/phase7-npm-top-level-2026-03-28.json`
- `docs/refactor/evidence/phase7-app-release-runtime-classpath-2026-03-28.txt`
- `docs/refactor/evidence/phase7-size-shrink-diff-2026-03-30.json`
- `docs/refactor/evidence/phase7-build-efficiency-baseline-2026-03-30.json`
- `docs/refactor/evidence/phase7-config-cache-summary-2026-03-30.json`

## 当前状态
- `in_progress（Stage 4 当前默认主线）`
