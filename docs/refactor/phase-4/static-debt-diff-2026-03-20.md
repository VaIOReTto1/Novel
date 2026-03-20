# Phase 4 Static Debt Diff - 2026-03-20

## Summary
- 基线来源：`docs/refactor/tracking/stage-2-static-debt-baseline.md`
- 当前执行节点：`Phase 4 / Wave 4 / W4-Q01 + W4-Q02`
- 结论：
  - touched-files 维度当前未发现新增 detekt 报告命中
  - repo 级静态债仍未达到 `Stage 2` 关闭阈值

## Current Metrics
| Metric | Baseline | Current | Delta | Target |
| --- | --- | --- | --- | --- |
| `RN lint errors` | `89` | `89` | `0` | `<= 72` |
| `RN lint warnings` | `1219` | `1219` | `0` | n/a |
| `detekt weighted issues` | `2260` | `2224` | `-36` | `<= 1921` |

## Command Evidence
- `npm run lint`
  - 结果：`89 errors / 1219 warnings`
  - 说明：当前 repo 级 RN lint 与基线持平，尚未达到阶段目标
- `android/gradlew.bat app:detekt`
  - 结果：`2224 weighted issues`
  - 说明：相较基线 `2260` 已收敛 `36`，但仍高于阶段目标 `1921`

## Interpretation
- `V4-06` 当前仍只能保持 `yellow`
- 造成 `V4-06` 未关闭的主要原因不是本轮 touched files 新增问题，而是 repo 级历史债规模仍过大
- 若维持当前阈值不变，想把 `2224` 继续压到 `<= 1921`，还需要额外清理至少 `303` weighted issues

## Recommendation
- 继续保留：
  - touched-files `0` 新增问题
  - repo 级静态债差值文档化
- 若要把 `V4-06` 切到 `green`，需要明确选择其一：
  - 继续执行大规模 detekt / ESLint 历史债清理
  - 或在阶段关闭前调整 repo 级目标与关闭口径
