# Phase 5 模块验证矩阵 - 2026-03-23

## 目标
- 固定 reopened `Phase 5` 深化后的最新可执行验证矩阵。
- 为 `V5-10` 的重新关闭准备当前命令证据。

## 模块级命令
| 区域 | 命令 | 结果 |
| --- | --- | --- |
| Core common | `android/gradlew.bat :core-common:testDebugUnitTest` | `pass` |
| Core ui | `android/gradlew.bat :core-ui:testDebugUnitTest` | `pass` |
| Core network | `android/gradlew.bat :core-network:testDebugUnitTest` | `pass` |
| Core bridge | `android/gradlew.bat :core-bridge:testDebugUnitTest` | `pass` |
| Core bridge contract | `android/gradlew.bat :core-bridge-contract:testDebugUnitTest` | `pass` |
| Core storage | `android/gradlew.bat :core-storage:testDebugUnitTest` | `pass` |
| Feature home | `android/gradlew.bat :feature-home:testDebugUnitTest` | `pass` |
| Feature search | `android/gradlew.bat :feature-search:testDebugUnitTest` | `pass` |
| Feature welfare | `android/gradlew.bat :feature-welfare:testDebugUnitTest` | `pass` |
| Feature rn-host | `android/gradlew.bat :feature-rn-host:testDebugUnitTest` | `pass` |
| Feature book | `android/gradlew.bat :feature-book:testDebugUnitTest` | `pass` |
| Feature login | `android/gradlew.bat :feature-login:testDebugUnitTest` | `pass` |
| Feature reader | `android/gradlew.bat :feature-reader:testDebugUnitTest` | `pass` |
| App JVM | `android/gradlew.bat :app:testDebugUnitTest` | `pass` |

## Stage 3 共享守门
| 区域 | 命令 | 结果 |
| --- | --- | --- |
| RN / Jest | `npm test -- --runInBand` | `pass` |
| Android lint | `android/gradlew.bat app:lintDebug` | `pass` |
| Android test compile | `android/gradlew.bat app:compileDebugAndroidTestKotlin` | `pass` |
| Macrobenchmark assemble | `android/gradlew.bat :macrobenchmark:assemble` | `pass` |

## 备注
- `feature-book / feature-login / feature-reader` 当前已具备模块级 JVM 验证，但仍只迁入首批纯逻辑层，不应误判为完整功能层已全部出 `app`。
- `feature-welfare` 当前已从 compile gate 进到 module-local JVM suite，说明其内部协调层已经足够独立。
- 依赖校验在新增 `feature-book / feature-login / feature-reader` 时触发了 `verification-metadata.xml` 增量更新，当前已重新跑通。

## 结论
- reopened `Phase 5` 的当前模块图已经形成第二版可执行验证矩阵。
- 关闭前只剩 closeout 口径与 rollback/index 文档统一收尾。
