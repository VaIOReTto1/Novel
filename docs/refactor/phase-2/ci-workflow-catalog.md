# Phase 2 CI Workflow Catalog

## 目标
- 把已经本地验证通过的质量命令优先接入 CI，形成真正会执行的门禁。
- 对仍处于历史债阶段的检查先提供在线观测位，避免继续依赖人工口头同步。

## 工作流文件
- `.github/workflows/quality-gates.yml`

## 当前 job 划分
| Job | Type | Current Mode | Commands |
| --- | --- | --- | --- |
| `rn-tests` | blocking | 已启用 | `npm test -- --runInBand` |
| `android-quality` | blocking | 已启用 | `./gradlew app:testDebugUnitTest app:lintDebug app:compileDebugAndroidTestKotlin :macrobenchmark:assemble` |
| `android-smoke` | observe | `continue-on-error: true` | `./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=...` |
| `rn-lint-observe` | observe | `continue-on-error: true` | `npm run lint` |
| `android-detekt-observe` | observe | `continue-on-error: true` | `./gradlew app:detekt` |

## 当前策略说明
- blocking:
  - 只接入已在本地验证通过的命令。
  - 目标是立刻替代“只有 label workflow”的空门禁状态。
- observe:
  - `android-smoke` 目前缺少当前本地 shell 可复用的设备验证，但 smoke 套件本身已落地。
  - `rn-lint-observe` 仍暴露大量历史 RN lint debt。
  - `android-detekt-observe` 已能真实扫描，但当前仍有大规模历史发现。

## 升级条件
- 当 `android-smoke` 在 CI emulator 上稳定通过后，可移除 `continue-on-error`，升级为 blocking。
- 当 RN lint 历史债务被压缩到可接受阈值后，可将 `rn-lint-observe` 升级为 blocking。
- 当 detekt 发现规模被控制到可清理范围后，可将 `android-detekt-observe` 升级为 blocking。

## 证据归档
- Android quality reports
  - `android/app/build/reports`
  - `android/app/build/test-results`
- Smoke reports
  - `android/app/build/reports/androidTests`
  - `android/app/build/outputs/androidTest-results`
- Detekt reports
  - `android/app/build/reports/detekt`
