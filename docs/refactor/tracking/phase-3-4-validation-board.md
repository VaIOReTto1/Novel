# Phase 3-4 验证看板

## 使用规则
- 每条记录必须填写 `Expected / Evidence / Actual / Status / Result Analysis / Owner / Validator / Validated On`
- `Status` 允许值：
  - `planned`
  - `in_progress`
  - `blocked`
  - `ready_for_validation`
  - `validated`
  - `not_met`
  - `deferred`
- `Result Analysis` 允许值：
  - `green`
  - `yellow`
  - `red`

## Phase 3
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V3-01 | Phase 3 | 高风险生产路径主网络通路唯一化成立 | 高风险路径统一经主网络栈进入 | `docs/refactor/phase-3/legacy-network-shell-migration-matrix.md`, `android/app/src/main/java/com/novel/core/network/NetworkFacade.kt`, `android/app/src/main/java/com/novel/core/network/LegacyApiServiceAdapter.kt`, `android/app/src/test/java/com/novel/core/network/LegacyApiServiceAdapterTest.kt`, `android/app/src/main/java/com/novel/rn/bridge/network/NavigationBridgeNetworkGateway.kt`, `android/app/src/test/java/com/novel/rn/bridge/network/NavigationBridgeNetworkGatewayTest.kt`, `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt`, `android/app/src/main/java/com/novel/utils/network/api/front/HomeService.kt`, `android/app/src/test/java/com/novel/utils/network/api/front/HomeServiceTest.kt`, `android/gradlew app:testDebugUnitTest --tests "com.novel.rn.bridge.network.NavigationBridgeNetworkGatewayTest"`, `android/gradlew app:testDebugUnitTest --tests "com.novel.utils.network.api.front.HomeServiceTest"`, `android/gradlew app:testDebugUnitTest` | 已完成旧网络壳真实调用矩阵，并建立 `NetworkFacade + LegacyApiServiceAdapter` 兼容适配层；`NavigationBridgeModule` 中首页推荐、分类、搜索、作家作品列表四条高风险 Bridge 网络路径已全部通过 `NavigationBridgeNetworkGateway` 接入主网络抽象，且模块内旧 `ApiService.get()` 直连已清零。`HomeService` 的首页推荐与友情链接路径也已切入 `NetworkFacade` 并由 `HomeServiceTest` 覆盖。当前仍有 Search / User 等服务层高风险路径待迁移，因此主通路唯一化继续保持进行中 | `in_progress` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-16 |
| V3-02 | Phase 3 | 第二阶段触达范围内协程模型统一 | `runBlocking / 匿名全局 scope` 清理并统一 `DispatcherProvider` | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V3-03 | Phase 3 | `StorageFacade` 成立，SharedPreferences 仅保留兼容层 | 业务层不再新增 SharedPreferences 直连 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V3-04 | Phase 3 | `AppError` 第一批统一落地 | Home / Search / Bridge / Settings 边界错误统一映射 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V3-05 | Phase 3 | 第二阶段静态债基线建立完成 | `RN lint / detekt` 基线可追溯 | `docs/refactor/tracking/stage-2-static-debt-baseline.md`, `npm run lint`, `android/gradlew app:detekt` | 已记录 `RN lint errors=89`、`RN lint warnings=1219`、`detekt weighted issues=2260`，并固化为第二阶段唯一基线 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-16 |
| V3-06 | Phase 3 | rollback / kill switch 可执行 | 网络、Bridge、存储切换可回退 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V3-07 | Phase 3 | Phase 4 进入条件明确 | Phase 4 开始前无关键未知项 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |

## Phase 4
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V4-01 | Phase 4 | 包边界骨架稳定 | 包内逻辑模块化边界可追溯 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V4-02 | Phase 4 | 指定超大类拆分完成 | `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 完成拆分 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V4-03 | Phase 4 | `BridgeFacade` 成立且旧协议兼容 | route / event / payload 语义保持兼容 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V4-04 | Phase 4 | 第二阶段触达范围内生产 mock 清理完成 | 正式路径不再保留业务 mock 分支 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V4-05 | Phase 4 | `profile-host / RN Host` 风险验证补齐 | 宿主页挂载、白屏风险、Bridge 初始化时序证据完整 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V4-06 | Phase 4 | 第二阶段静态债目标达标 | touched files 零新增 error，repo 级债务显著收敛 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V4-07 | Phase 4 | 第二阶段关闭总结完成 | closeout 文档与证据完整 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
| V4-08 | Phase 4 | Phase 5 进入条件明确 | 后续模块化进入条件客观明确 | 待补充 | 待补充 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | - |
