# Stage 3 静态债基线

## 目标
- 将 `Stage 3` 当前有效的静态债结果固定为唯一基线。
- 明确本阶段的原则是“冻结历史债、阻断新增债”，而不是再次把大规模静态债清理升级为主线。

## 基线采集时间
- 日期：`2026-03-28`
- 来源：`CI gate recovery / app detekt baseline freeze`

## 基线命令
- `npx eslint . -f json`
- `android/gradlew.bat app:detekt`
- `android/gradlew.bat app:detektBaseline`

## 当前基线
| Item | Result |
| --- | --- |
| RN lint errors | `0` |
| RN lint warnings | `953` |
| detekt weighted issues | `1481` |

## Stage 3 规则
- touched files：
  - ESLint error = `0`
  - detekt issue = `0`
- repo 级：
  - `RN lint errors` 必须持续保持 `0`
  - `detekt weighted issues` 不得高于 `1481`
  - `android/app/detekt-baseline.xml` 仅用于冻结历史 detekt debt，不允许通过 `ignoreFailures`、降低规则强度或放宽阈值来获取绿色结果

## 说明
- `app:detekt` 现在保持“真实扫描 + baseline freeze”模式：历史 finding 通过 `android/app/detekt-baseline.xml` 冻结，新 finding 仍会直接失败。
- 如果后续模块化或性能治理自然带来静态债下降，应同步回写这份基线和相关 closeout 文档。
