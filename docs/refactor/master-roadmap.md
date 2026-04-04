# Novel 重构总路线图

## 当前权威状态
- `Stage 3 = validated`
- `Stage 4 = validated`
- `Stage 5 = validated`
- `Stage 6 = validated`
- `Stage 7 = validated`
- `Phase 15 = validated`
- `Phase 16 = validated`
- `Phase 17 = validated`
- `Phase 18 = validated`
- `Stage 7 technical status = validated`

## 项目现状
- Android 主线重构已经完成到 `Stage 5`，当前稳定在 `app + core-* + feature-* + macrobenchmark` 模块图。
- RN 主线重构已在 `Stage 6` 完成第一轮 runtime、page-domain 与 contract/maintainability 收口。
- 当前默认主线切换到 `Stage 7`，目标是把视觉系统、资产治理、Token、展示基建和页面重皮肤沉淀为长期治理层。
- 当前 repo、验证门禁、官方 Figma 宿主证据与三方签核已全部闭环；后续如需继续推进，应通过 reopen 或新 Stage 进入。

## Stage 摘要
| Stage | Phase | 主题 | 当前状态 |
| --- | --- | --- | --- |
| Stage 1 | Phase 0-2 | 基线、发布安全、质量门禁 | `validated` |
| Stage 2 | Phase 3-4 | 基础设施与边界收口 | `validated` |
| Stage 3 | Phase 5-6 | 模块化深化与性能治理 | `validated` |
| Stage 4 | Phase 7-8 | 包体积 / 构建效率 / observability | `validated` |
| Stage 5 | Phase 9-11 | 运行硬化、合规、供应链、维护性 | `validated` |
| Stage 6 | Phase 12-14 | RN runtime、page-domain、contract/maintainability | `validated` |
| Stage 7 | Phase 15-18 | 视觉系统、资产治理、Token、展示与回归门禁 | `validated` |

## 当前权威入口
- [README.md](./README.md)
- [stage-7-phase-15-18-plan.md](./stage-7-phase-15-18-plan.md)
- [phase-15-18-validation-board.md](./tracking/phase-15-18-validation-board.md)
- [decision-log.md](./tracking/decision-log.md)
- [rollback-index.md](./tracking/rollback-index.md)
