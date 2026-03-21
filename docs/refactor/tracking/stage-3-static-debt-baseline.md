# 第三阶段静态债基线

## 目标
- 以 `Stage 2` 关闭时的静态债结果作为 `Stage 3` 唯一基线
- 明确第三阶段的静态债目标是“零回退”，而不是再次把清债变成主线

## 基线采集时间
- 日期：`2026-03-21`
- 来源：`Stage 2 closeout`

## 基线命令
- `npx eslint . -f json`
- `android/gradlew.bat app:detekt`

## 当前基线
| Item | Result |
| --- | --- |
| RN lint errors | `0` |
| RN lint warnings | `953` |
| detekt weighted issues | `1901` |

## Stage 3 规则
- touched files：
  - ESLint error = `0`
  - detekt issue = `0`
- repo 级：
  - `RN lint errors` 必须持续保持 `0`
  - `detekt weighted issues` 不得高于 `1901`

## 说明
- `Stage 3` 的主线是模块化与性能治理，不是再次展开大规模静态债冲刺
- 若后续为了模块化或性能专项自然带来静态债下降，可以记为增益，但不是本阶段主关闭目标
