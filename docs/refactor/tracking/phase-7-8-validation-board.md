# Phase 7-8 验证看板

## 当前状态
- `Stage 4`: `validated`
- `Phase 7`: `validated`
- `Phase 8`: `validated`
- 最新更新：`2026-03-30`

## Phase 7
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V7-01 | size baseline 与 artifact diff 入口 | APK / AAB / fonts / JS-native assets 组成可追溯 | 已由 `size-baseline-and-artifact-entrypoints-2026-03-28.md` 与 `phase7-release-artifact-inventory-2026-03-28.json` 固定首批基线：`AAB 72.60 MiB`、`APK 98.93 MiB`、`JS bundle 2.33 MiB`、字体 `3.71 MiB / 19` 文件 | `validated` | `green` |
| V7-02 | Gradle / npm dependency inventory | 版本分散、重复依赖、catalog/BOM 路线明确 | 已由 `dependency-inventory-and-governance-2026-03-28.md`、npm top-level JSON 与 `releaseRuntimeClasspath` 样本确认：无 version catalog、Kotlin/Hilt/Compose 版本来源分散、npm 侧 `31` 个 ranged 声明 | `validated` | `green` |
| V7-03 | 第一轮低风险 size shrink | 不改行为前提下取得可证实体积收益 | 已由 `first-size-shrink-vector-icon-font-prune-2026-03-30.md` 落地，限制 release icon fonts 为 `MaterialIcons + Feather`，使 `APK -1.70 MiB`、`AAB -1.70 MiB`、字体资产 `-3.32 MiB / -17` 文件 | `validated` | `green` |
| V7-04 | clean / incremental build baseline | build hot path、config cache 阻塞原因明确 | 已由 `build-efficiency-baseline-and-config-cache-2026-03-30.md` 固定基线：`app:testDebugUnitTest 321s -> 48.03s`、`app:assembleRelease 614.65s -> 53.66s`；`configuration-cache` 在 sampled task 上可复用，但仍带 `react-native-reanimated` 已知问题 | `validated` | `green` |
| V7-05 | Phase 7 closeout 与 Phase 8 入口 | Stage 4 后半段进入条件清晰 | 已由 `phase-7-closeout-assessment.md` 固定 Phase 7 关闭结论，并把 Phase 8 入口收敛到 observability / flags / rollout / ADR 四条线 | `validated` | `green` |

## Phase 8
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V8-01 | observability 指标目录 | 启动、Bridge、WebView、缓存、权限等指标口径固定 | 已由 `observability-metric-catalog-2026-03-30.md` 固定 Startup / Welfare-WebView / Network Trace / Bridge Error 的当前宿主目录，并明确当前无统一 Crash / ANR 平台 | `validated` | `green` |
| V8-02 | feature flag / kill switch registry | 开关默认值、owner、回退用途明确 | 已由 `feature-flag-and-kill-switch-registry-2026-03-30.md` 固定 `enableBridgeErrorMapper`、`enableBridgeSharedScopes`、`enableSettingsDataStorePilot` 的默认值、override key 与 consumers | `validated` | `green` |
| V8-03 | rollout / rollback playbook | canary / rollback 步骤可执行 | 已由 `rollout-and-rollback-playbook-2026-03-30.md` 固定当前 repo-local rollout / rollback 流程，并明确默认首选 `git revert + rollback-index` | `validated` | `green` |
| V8-04 | ADR / reviewer / owner 机制 | 关键变更有制度化审查入口 | 已由 `adr-reviewer-owner-governance-2026-03-30.md` 收敛 owner matrix、API surface、decision log、validation board 与 rollback 入口 | `validated` | `green` |
| V8-05 | Stage 4 closeout 入口 | 第四阶段收尾与长期维护入口清晰 | 已由 `phase-8-closeout-assessment.md` 与 `stage-4-closeout-summary.md` 固定当前关闭结论 | `validated` | `green` |

## 当前权威入口
- [Stage 4 计划](../stage-4-phase-7-8-plan.md)
- [Phase 7 宿主文档](../phases/phase-7-size-dependency-build-governance.md)
- [Phase 8 宿主文档](../phases/phase-8-observability-rollout-governance.md)
- [Stage 3 closeout summary](../stage-3-closeout-summary.md)
