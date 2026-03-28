# Current Focus

## 当前状态
- 当前分支：`main`
- 当前权威结论：`Stage 4 = in_progress`，`Phase 7 = in_progress`，`Phase 8 = planned`
- 当前 refactor 控制面板以 `docs/refactor/README.md` 为准
- 当前 Android 模块图已经稳定为 `app + core-* + feature-* + macrobenchmark`

## 最近完成
- `Stage 3 = validated`，继续以 `2026-03-26` reopen closeout 为最终收口事实
- `2026-03-28` 已正式完成 `Stage 4` 控制面切主线
- `Phase 7` 已正式成为当前执行 phase
- `Phase 8` 已补齐宿主文档与验证入口，当前保持 `planned`

## 默认下一主线
- 默认下一主线：推进 `Phase 7` 的包体积、依赖与构建效率治理
- 第一落点：固定 size baseline 与 artifact diff 入口，覆盖 AAB / APK / fonts / JS-native assets
- 第二落点：固定 Gradle / npm dependency inventory，并显式收口 version catalog / BOM 路线
- 第三落点：建立 clean / incremental build baseline，并明确 `configuration-cache=false` 的阻塞原因
- `Phase 8` 暂不并行打开，只保留为 Stage 4 后半段 queued phase

## Blockers / Known Drift
- 根 `README.md` 的技术版本和成熟度描述仍可能落后于当前代码与当前 refactor 文档
- `android/gradle/libs.versions.toml` 仍缺失，依赖版本继续分散在多个 Gradle 脚本中
- `org.gradle.configuration-cache=false` 仍未形成显式阻塞结论
- 仓库已有本地 `RefactorFeatureFlags` 与局部性能 monitor，但尚无统一 Crash / ANR / 灰度平台
- `docs/harness/generated/workspace-snapshot.md` 在模块图、验证命令、harness 脚本或 workflow 输入变化后必须重新生成
- `.trae/rules/project_rules.md` 现在只是 shim，不再承担项目事实说明

## Primary Source Refs
- [docs/refactor/README.md](../refactor/README.md)
- [docs/refactor/stage-4-phase-7-8-plan.md](../refactor/stage-4-phase-7-8-plan.md)
- [docs/refactor/phases/phase-7-size-dependency-build-governance.md](../refactor/phases/phase-7-size-dependency-build-governance.md)
- [docs/refactor/phases/phase-8-observability-rollout-governance.md](../refactor/phases/phase-8-observability-rollout-governance.md)
- [docs/refactor/tracking/phase-7-8-validation-board.md](../refactor/tracking/phase-7-8-validation-board.md)
- [docs/refactor/tracking/decision-log.md](../refactor/tracking/decision-log.md)

## Last Reviewed
- `2026-03-28` by Stage 4 control-plane cutover
