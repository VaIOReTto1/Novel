# Phase 7 Build Efficiency Baseline And Config Cache

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 4 / Phase 7`
- 当前结论：`已固定第一轮 clean / incremental baseline，并明确 configuration cache 当前阻塞口径`

## 目的
- 把 `Phase 7` 的 build efficiency 治理从“感觉慢”升级为有 clean / incremental 差值的当前基线。
- 明确 `org.gradle.configuration-cache=false` 目前到底是“完全不可用”还是“存在已知问题但局部可试跑”。

## 当前证据
- `docs/refactor/evidence/phase7-build-efficiency-baseline-2026-03-30.json`
- `docs/refactor/evidence/phase7-config-cache-probe-run1-2026-03-30.txt`
- `docs/refactor/evidence/phase7-config-cache-probe-run2-2026-03-30.txt`
- `docs/refactor/evidence/phase7-config-cache-summary-2026-03-30.json`
- `android/gradle.properties`

## 采样命令
- clean / incremental baseline：
  - `./gradlew.bat clean app:testDebugUnitTest --no-daemon --console=plain`
  - `./gradlew.bat app:testDebugUnitTest --no-daemon --console=plain`
  - `./gradlew.bat clean app:assembleRelease --no-daemon --console=plain`
  - `./gradlew.bat app:assembleRelease --no-daemon --console=plain`
- configuration cache canary：
  - `./gradlew.bat app:testDebugUnitTest --configuration-cache --configuration-cache-problems=warn --no-daemon --console=plain`
  - 第二次同命令复跑，用于确认是否复用

## 当前基线
| Task | Clean | Incremental | Delta |
| --- | --- | --- | --- |
| `app:testDebugUnitTest` | `321.00 s` | `48.03 s` | `-272.97 s` |
| `app:assembleRelease` | `614.65 s` | `53.66 s` | `-560.99 s` |

## 当前解释
- 当前热路径已经很明确：
  - `app:assembleRelease` 是最大的 clean build 成本中心
  - `app:testDebugUnitTest` 是当前日常验证里最重的单任务之一
- clean / incremental 差距都很大，说明现有构建缓存与增量链路是有效的，但 clean path 成本仍然偏高。

## Configuration Cache 当前结论
- `android/gradle.properties` 仍保持：
  - `org.gradle.configuration-cache=false`
- 当前 canary 结果：
  - 第一次 `app:testDebugUnitTest --configuration-cache`：
    - `BUILD SUCCESSFUL`
    - `Configuration cache entry stored with 1 problem`
  - 第二次同命令：
    - `BUILD SUCCESSFUL`
    - `Configuration cache entry reused`
- 当前已知问题来自：
  - `..\node_modules\react-native-reanimated\android\build.gradle`
  - `line 80`
  - external process：
    - `node --print require.resolve('react-native/package.json')`

## 当前口径
- `configuration-cache=false` 不是因为 sampled task 完全不可运行。
- 当前更准确的结论是：
  - sampled task 已经能存储并复用 configuration cache
  - 但仍带着 `react-native-reanimated` 的已知问题
  - 还没有完成对其它高风险任务的广覆盖验证，因此当前不直接切全仓默认开启

## 后续动作
1. 若要在 `Phase 8` 或后续阶段考虑全仓开启 configuration cache，先补更多任务的 canary 矩阵。
2. 若要继续压 build 时间，优先围绕 `app:assembleRelease` 的 clean path 热点展开，而不是盲目先动 incremental 参数。

## 主要引用
- `docs/refactor/phases/phase-7-size-dependency-build-governance.md`
- `docs/refactor/tracking/phase-7-8-validation-board.md`
- `docs/refactor/evidence/phase7-build-efficiency-baseline-2026-03-30.json`
- `docs/refactor/evidence/phase7-config-cache-summary-2026-03-30.json`
