# Phase 5 Build-Time Baseline And Diff Entrypoints

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 5 reopen closeout 后治理补齐`
- 当前结论：`已形成 build-time baseline 与 diff 入口`

## 目的
- 把 `Phase 5` 原本只停留在验证矩阵里的构建命令，升级成可复用的 build-time 治理入口。
- 固定后续对比时必须使用的命令、工作目录和非增量口径，避免把本地 daemon / incremental 噪声误当成结果。

## 当前基线
- 记录日期：`2026-03-27`
- 分支：`codex-wave2-search-rn-host`
- 记录方式：`android/gradlew.bat --no-daemon -Pkotlin.incremental=false -Pkapt.incremental.apt=false`
- 工作目录：`d:/program/Novel/android`
- 原始证据：`docs/refactor/evidence/build-time-baseline-2026-03-27.json`

| Entry Point | Wall Clock |
| --- | --- |
| `app:testDebugUnitTest` | `288.88 s` |
| `app:lintDebug` | `84.29 s` |
| `app:compileDebugAndroidTestKotlin` | `63.07 s` |
| `:macrobenchmark:assemble` | `45.81 s` |
| `:feature-search:testDebugUnitTest` | `47.31 s` |

## 固定入口
### Repo / CI 主入口
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" app:testDebugUnitTest`
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" app:lintDebug`
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" app:compileDebugAndroidTestKotlin`
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :macrobenchmark:assemble`

### 模块级 diff 入口
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :feature-search:testDebugUnitTest`
- 若变更触达其它高风险模块，替换为对应 `:feature-*` / `:core-*` 单测入口，但仍保持同一非增量口径。

## 对比规则
1. 先记录变更前基线，再记录变更后样本。
2. 两次都必须在 `android/` 目录执行，且都带：
   - `--no-daemon`
   - `-Pkotlin.incremental=false`
   - `-Pkapt.incremental.apt=false`
3. 两次都要在任务之间执行 `gradlew --stop`，避免 daemon 热态污染结果。
4. 若结果差异超过 `10%`，需要在 PR / closeout / addendum 中解释。
5. 若结果差异超过 `20%`，默认升级为 build governance 风险，不能只写一句“本地偶发抖动”。

## 何时必须补 diff
- 修改 `settings.gradle`、模块依赖、build logic、公共注解处理配置。
- 让 `:app` 重新依赖新的 feature / core 模块。
- 新增大体量 Kotlin 源集、KSP / KAPT / Lint 规则或 instrumentation 依赖。

## 当前解释
- `app:testDebugUnitTest` 明显长于其他入口，说明 `:app` 仍是集成级验证与依赖收敛的主要成本中心。
- `:feature-search:testDebugUnitTest` 的单模块入口已足够轻，适合作为后续“模块化仍然值得”的对比样本。
- 这份工件属于 `Phase 5` 的治理补齐，不等于 `Phase 7` 的完整 build-efficiency 专项。

## 与 Phase 7 的边界
- 本文档解决的是：`Phase 5` 现在就有可追溯 build-time 入口。
- `Phase 7` 仍负责：
  - build hot path 深挖
  - artifact diff
  - dependency diff
  - clean / incremental 更完整的体系化治理

## 主要引用
- `docs/refactor/phase-5/phase-5-closeout-assessment.md`
- `docs/refactor/phase-5/module-verification-matrix-2026-03-26.md`
- `docs/refactor/phases/phase-7-size-dependency-build-governance.md`
- `docs/refactor/evidence/build-time-baseline-2026-03-27.json`
