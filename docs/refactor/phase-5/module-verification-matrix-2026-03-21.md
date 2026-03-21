# Phase 5 Module Verification Matrix - 2026-03-21

## Goal
- Freeze the executable verification matrix for the current Phase 5 module graph.
- Provide direct command evidence for `V5-05`.

## Module-Level Commands
| Area | Command | Result |
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

## Stage 3 Shared Gates
| Area | Command | Result |
| --- | --- | --- |
| RN / Jest | `npm test -- --runInBand` | `pass` |
| Android lint | `android/gradlew.bat app:lintDebug --console=plain` | `pass` |
| Android test compile | `android/gradlew.bat app:compileDebugAndroidTestKotlin --console=plain` | `pass` |
| Macrobenchmark assemble | `android/gradlew.bat :macrobenchmark:assemble --console=plain` | `pass` |
| Install debug | `android/gradlew.bat app:installDebug` | `pass` |

## Notes
- `feature-welfare` 当前仍以 compile gate 为主，因为本轮切口都是低耦合组件/工具层，尚未形成独立 module-local JVM suite。
- `feature-home / feature-search / feature-rn-host` 当前最小切口已具备独立 module-local JVM 验证。
- All Gradle commands were executed serially by the leader to avoid KSP/KAPT cache interference.

## Conclusion
- The current Phase 5 module graph has an executable and traceable verification matrix.
- `V5-05` can enter `validated / green`.
