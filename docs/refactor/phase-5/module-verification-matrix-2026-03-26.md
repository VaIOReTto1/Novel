# Phase 5 模块验证矩阵（2026-03-26）

## 口径
- 类型：`reopen closeout`
- 状态：`validated`
- 说明：本文件是当前权威矩阵；`2026-03-21` 与 `2026-03-23` 文件仅保留历史用途。

| ID | 范围 | 命令 | 结果 |
| --- | --- | --- | --- |
| V5-RC-01 | Core + Feature 单测矩阵 | `android/gradlew.bat :core-common:testDebugUnitTest :core-ui:testDebugUnitTest :core-bridge:testDebugUnitTest :core-bridge-contract:testDebugUnitTest :core-storage:testDebugUnitTest :core-network:testDebugUnitTest :feature-home:testDebugUnitTest :feature-search:testDebugUnitTest :feature-welfare:testDebugUnitTest :feature-rn-host:testDebugUnitTest :feature-book:testDebugUnitTest :feature-login:testDebugUnitTest :feature-reader:testDebugUnitTest :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` | `pass` |
| V5-RC-02 | app Kotlin 编译 | `android/gradlew.bat :app:compileDebugKotlin --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` | `pass` |
| V5-RC-03 | app lint + AndroidTest Kotlin + macrobenchmark assemble | `android/gradlew.bat app:lintDebug app:compileDebugAndroidTestKotlin :macrobenchmark:assemble --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` | `pass` |
| V5-RC-04 | RN bridge / host Jest | `npm test -- --runInBand __tests__/bridge/NativeBridgeEventContracts.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` | `pass` |
| V5-RC-05 | host thin wrapper回归 | `android/gradlew.bat :feature-rn-host:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` | `pass` |

## 结果摘要
- `home/search/login/book/reader/rn-host` 根状态机全部已从 `app` 迁出。
- `app` 仍能完成 Kotlin 编译、JVM 单测、AndroidTest Kotlin 编译和 lint。
- reopen 期间曾出现本机 Kotlin/KSP 增量缓存抖动；最终采用 `in-process + non-incremental` 验证策略获得稳定结果。
