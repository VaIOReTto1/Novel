# 第二阶段静态债基线

## 目标
- 固定第二阶段开始时的 `RN lint / detekt` 基线，作为后续收敛阈值的唯一参考。

## 基线采集时间
- 日期：`2026-03-16`
- 采集阶段：第一阶段结束后，第二阶段开始前

## 命令
- RN lint:
  - `npm run lint`
- Android detekt:
  - `android/gradlew app:detekt`

## 当前基线
| Item | Command | Result |
| --- | --- | --- |
| RN lint errors | `npm run lint` | `90` |
| RN lint warnings | `npm run lint` | `1216` |
| detekt weighted issues | `android/gradlew app:detekt` | `2260` |

## 阶段内约束
- touched files:
  - ESLint error = `0`
  - detekt issue = `0`
- repo 级收敛目标：
  - `RN lint` repo 级 error 数 <= `72`
  - `detekt weighted issues` <= `1921`

## 说明
- 第二阶段目标是“显著收敛”，不是“一次性全仓清零”。
- 若后续重新采集基线，必须在 `decision-log.md` 记录原因，不允许静默覆盖。
