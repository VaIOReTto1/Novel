# Phase 5 模块验证矩阵 - 2026-03-21

## 目标
- 固定当前 `Phase 5` 模块图的可执行验证矩阵。
- 为 `V5-05` 提供直接命令证据。

## 模块级命令
| 区域 | 命令 | 结果 |
| --- | --- | --- |
| Core common | `android/gradlew.bat :core-common:testDebugUnitTest` | `pass` |
| Core bridge contract | `android/gradlew.bat :core-bridge-contract:testDebugUnitTest` | `pass` |
| Core storage | `android/gradlew.bat :core-storage:testDebugUnitTest` | `pass` |
| Core network | `android/gradlew.bat :core-network:testDebugUnitTest` | `pass` |
| Feature welfare | `android/gradlew.bat :feature-welfare:compileDebugKotlin` | `pass` |
| Feature home | `android/gradlew.bat :feature-home:testDebugUnitTest` | `pass` |
| Feature search | `android/gradlew.bat :feature-search:testDebugUnitTest` | `pass` |
| Feature rn-host | `android/gradlew.bat :feature-rn-host:testDebugUnitTest` | `pass` |
| App JVM | `android/gradlew.bat :app:testDebugUnitTest` | `pass` |

## Stage 3 共享守门
| 区域 | 命令 | 结果 |
| --- | --- | --- |
| RN / Jest | `npm test -- --runInBand` | `pass` |
| Android lint | `android/gradlew.bat app:lintDebug --console=plain` | `pass` |
| Android test compile | `android/gradlew.bat app:compileDebugAndroidTestKotlin --console=plain` | `pass` |
| Macrobenchmark assemble | `android/gradlew.bat :macrobenchmark:assemble --console=plain` | `pass` |
| Install debug | `android/gradlew.bat app:installDebug` | `pass` |

## 备注
- `feature-welfare` 当前仍以 compile gate 为主，因为本轮切口都是低耦合组件/工具层，尚未形成独立 module-local JVM suite。
- `feature-home / feature-search / feature-rn-host` 当前最小切口已具备独立 module-local JVM 验证。
- 所有 Gradle 命令都由 leader 串行执行，以避免 KSP/KAPT 缓存互踩。

## 结论
- 当前 `Phase 5` 模块图已经具备可执行、可追溯的验证矩阵。
- `V5-05` 可以进入 `validated / green`。
