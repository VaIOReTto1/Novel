# Current Focus

## 当前状态
- 当前分支：`main`
- 当前权威结论：`Stage 4 = in_progress`，`Phase 7 = validated`，`Phase 8 = planned`
- 当前 refactor 控制面板以 `docs/refactor/README.md` 为准
- 当前 Android 模块图已经稳定为 `app + core-* + feature-* + macrobenchmark`

## 最近完成
- `Stage 3 = validated`，继续以 `2026-03-26` reopen closeout 为最终收口事实
- `2026-03-28` 已正式完成 `Stage 4` 控制面切主线
- `2026-03-30` 已完成 `Phase 7` closeout，关闭 Stage 4 前半段的 size / dependency / build efficiency 治理
- `Phase 8` 已保留为下一主线，当前保持 `planned`

## 默认下一主线
- 默认下一主线：推进 `Phase 8` 的 observability / rollout / ADR 入口建设
- 第一落点：固定 observability 指标目录，统一启动、Bridge、WebView、缓存、权限等口径
- 第二落点：固定 feature flag / kill switch registry，收清默认值、owner 与回退用途
- 第三落点：固定 rollout / rollback playbook 与 ADR / reviewer / owner 机制
- `Phase 7` 已关闭，不再作为当前执行主线

## Blockers / Known Drift
- 根 `README.md` 的技术版本和成熟度描述仍可能落后于当前代码与当前 refactor 文档
- `android/gradle/libs.versions.toml` 仍缺失，依赖版本继续分散在多个 Gradle 脚本中
- `org.gradle.configuration-cache=false` 当前已经有 sampled task canary 结论，但尚未覆盖所有高风险任务
- 仓库已有本地 `RefactorFeatureFlags` 与局部性能 monitor，但尚无统一 Crash / ANR / 灰度平台
- `docs/harness/generated/workspace-snapshot.md` 在模块图、验证命令、harness 脚本或 workflow 输入变化后必须重新生成
- `.trae/rules/project_rules.md` 现在只是 shim，不再承担项目事实说明

## Primary Source Refs
- [docs/refactor/README.md](../refactor/README.md)
- [docs/refactor/stage-4-phase-7-8-plan.md](../refactor/stage-4-phase-7-8-plan.md)
- [docs/refactor/phase-7/phase-7-closeout-assessment.md](../refactor/phase-7/phase-7-closeout-assessment.md)
- [docs/refactor/phases/phase-8-observability-rollout-governance.md](../refactor/phases/phase-8-observability-rollout-governance.md)
- [docs/refactor/tracking/phase-7-8-validation-board.md](../refactor/tracking/phase-7-8-validation-board.md)
- [docs/refactor/tracking/decision-log.md](../refactor/tracking/decision-log.md)

## Last Reviewed
- `2026-03-30` by Phase 7 closeout
