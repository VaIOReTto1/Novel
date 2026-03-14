# Phase 0 - Phase 1 进入条件

## 目标
- 将 `Phase 1` 的进入标准从“感觉准备好了”改为“可客观判断的放行门禁”。

## 必须满足的进入条件
| 编号 | 条件 | 判断方式 |
| --- | --- | --- |
| G1 | 核心路径矩阵完成 | `docs/refactor/phase-0/core-path-matrix.md` 已覆盖首页、书详情、阅读器、登录、搜索、福利、设置、分类、书架、我的、作者、AI |
| G2 | 资产清单完成 | `docs/refactor/phase-0/asset-inventory.md` 已包含 route、RN 组件、Bridge、存储、数据库、构建配置 |
| G3 | 测量协议完成 | `docs/refactor/phase-0/device-matrix-and-measurement-protocol.md` 已定义设备矩阵、环境、轮次、记录模板 |
| G4 | 第一轮基线快照完成 | `docs/refactor/phase-0/baseline-snapshot.md` 已记录仓库内静态基线，动态指标待补采但需明确标记 |
| G5 | 风险与禁区清单完成 | `docs/refactor/phase-0/risk-register-and-no-go-zones.md` 已输出 blocker/high/medium/low 风险与禁区 |
| G6 | 稳定测试数据方案完成 | `docs/refactor/phase-0/stable-test-data-plan.md` 已定稿 |
| G7 | kill switch 最小方案完成 | `docs/refactor/phase-0/kill-switch-minimal-plan.md` 已定稿 |
| G8 | 验证看板同步完成 | `phase-0-2-validation-board.md` 中 `V0-01 ~ V0-06` 已有当前状态和证据 |

## 允许带着遗留项进入 Phase 1 的条件
- 仅允许以下类型遗留：
  - 真机动态性能指标仍待补采，但测量协议和静态基线已存在。
  - Owner/Reviewer/Validator 尚未最终指派，但不影响文档、基线和风险判断。
- 所有遗留项必须：
  - 被标记为 `high` 或以下风险。
  - 写入 `decision-log.md`。
  - 不影响 `Phase 1` 的发布、安全、合规治理判断。

## 不允许进入 Phase 1 的 blocker
- `V0-01` 或 `V0-02` 未完成。
- 风险图谱未明确 blocker 项。
- kill switch 方案缺失。
- 资产清单无法定位到关键代码路径。
- 验证看板未同步，导致阶段状态不可追溯。

## 当前结论
- 从文档角度，`Phase 1` 的放行门禁已经可以客观判断。
- 真机动态性能采集仍应继续推进，但不阻塞 `Phase 1` 的准备与治理工作。
