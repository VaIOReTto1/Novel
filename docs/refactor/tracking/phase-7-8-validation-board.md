# Phase 7-8 验证看板

## 当前状态
- `Stage 4`: `in_progress`
- `Phase 7`: `in_progress`
- `Phase 8`: `planned`
- 最新更新：`2026-03-28`

## Phase 7
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V7-01 | size baseline 与 artifact diff 入口 | APK / AAB / fonts / JS-native assets 组成可追溯 | 已由 `size-baseline-and-artifact-entrypoints-2026-03-28.md` 与 `phase7-release-artifact-inventory-2026-03-28.json` 固定首批基线：`AAB 72.60 MiB`、`APK 98.93 MiB`、`JS bundle 2.33 MiB`、字体 `3.71 MiB / 19` 文件 | `validated` | `green` |
| V7-02 | Gradle / npm dependency inventory | 版本分散、重复依赖、catalog/BOM 路线明确 | 已由 `dependency-inventory-and-governance-2026-03-28.md`、npm top-level JSON 与 `releaseRuntimeClasspath` 样本确认：无 version catalog、Kotlin/Hilt/Compose 版本来源分散、npm 侧 `31` 个 ranged 声明 | `validated` | `green` |
| V7-03 | 第一轮低风险 size shrink | 不改行为前提下取得可证实体积收益 | 待开展 | `in_progress` | `pending` |
| V7-04 | clean / incremental build baseline | build hot path、config cache 阻塞原因明确 | 待开展 | `in_progress` | `pending` |
| V7-05 | Phase 7 closeout 与 Phase 8 入口 | Stage 4 后半段进入条件清晰 | 待开展 | `in_progress` | `pending` |

## Phase 8
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V8-01 | observability 指标目录 | 启动、Bridge、WebView、缓存、权限等指标口径固定 | 尚未启动 | `planned` | `queued` |
| V8-02 | feature flag / kill switch registry | 开关默认值、owner、回退用途明确 | 尚未启动 | `planned` | `queued` |
| V8-03 | rollout / rollback playbook | canary / rollback 步骤可执行 | 尚未启动 | `planned` | `queued` |
| V8-04 | ADR / reviewer / owner 机制 | 关键变更有制度化审查入口 | 尚未启动 | `planned` | `queued` |
| V8-05 | Stage 4 closeout 入口 | 第四阶段收尾与长期维护入口清晰 | 尚未启动 | `planned` | `queued` |

## 当前权威入口
- [Stage 4 计划](../stage-4-phase-7-8-plan.md)
- [Phase 7 宿主文档](../phases/phase-7-size-dependency-build-governance.md)
- [Phase 8 宿主文档](../phases/phase-8-observability-rollout-governance.md)
- [Stage 3 closeout summary](../stage-3-closeout-summary.md)
