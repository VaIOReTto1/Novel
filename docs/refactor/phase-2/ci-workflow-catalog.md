# Phase 2 CI Workflow Catalog

## 目标
- 记录当前 `Quality Gates` 工作流里 blocking 与 observe job 的真实执行方式。
- 明确哪些 job 已经是硬门禁，哪些 job 仍处于观察态但必须可执行、可留痕、可追责。

## 工作流文件
- `.github/workflows/quality-gates.yml`

## 当前 job 划分
| Job | Type | Current Mode | Commands |
| --- | --- | --- | --- |
| `rn-tests` | blocking | enabled | `npm test -- --runInBand` |
| `android-quality` | blocking | enabled | `./gradlew app:testDebugUnitTest` `<br>` `./gradlew app:lintDebug` `<br>` `./gradlew app:compileDebugAndroidTestKotlin` `<br>` `./gradlew :macrobenchmark:assemble` |
| `android-smoke` | observe | `continue-on-error: true` | `Enable KVM` `<br>` `reactivecircus/android-emulator-runner@v2 (working-directory: ./android)` `<br>` `./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=...` |
| `rn-lint-observe` | observe | `continue-on-error: true` | `npm run lint` |
| `android-detekt-observe` | observe | `continue-on-error: true` | `./gradlew app:detekt` |

## 当前策略说明
- blocking：
  - 仅接入已经能够在本地稳定复现并通过的命令。
  - `android-quality` 已拆成四个显式步骤，避免把 lint 和其他 Gradle gate 捆绑在同一条长命令里。
- observe：
  - `android-smoke` 仍属于 emulator 观察态，但已切到 KVM + `working-directory` 的 action 推荐路径。
  - `rn-lint-observe` 继续暴露历史 RN lint debt。
  - `android-detekt-observe` 保持真实扫描；当前通过 committed baseline 冻结 `1481` 个历史 weighted issues，而不是通过 `ignoreFailures` 或降低规则来变绿。

## 升级条件
- 当 `android-smoke` 在 CI emulator 上连续稳定通过后，可移除 `continue-on-error`，升级为 blocking。
- 当 RN lint 历史债务压缩到可接受范围后，可将 `rn-lint-observe` 升级为 blocking。
- 当 detekt baseline 持续收缩到可清理范围后，可将 `android-detekt-observe` 升级为 blocking。

## 证据归档
- Android quality reports
  - `android/app/build/reports`
  - `android/app/build/test-results`
  - `android/build/reports/problems`
- Smoke reports
  - `android/app/build/reports/androidTests`
  - `android/app/build/outputs/androidTest-results`
- Detekt reports
  - `android/app/build/reports/detekt`
