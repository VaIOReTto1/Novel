# Phase 0-2 验证看板

## 使用规则
- 每条记录必须填写 `Expected / Evidence / Actual / Status / Result Analysis / Owner / Validator / Validated On`。
- `Status` 仅允许使用：
  - `planned`
  - `in_progress`
  - `blocked`
  - `ready_for_validation`
  - `validated`
  - `not_met`
  - `deferred`
- `Result Analysis` 仅允许使用：
  - `green`
  - `yellow`
  - `red`
- 任意 `red` 存在时，对应阶段不得关闭。

## Phase 0
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V0-01 | Phase 0 | 核心路径矩阵完整 | 首页、书详情、阅读器、登录、搜索、福利、设置、分类、书架、我的、作者、AI 页面无遗漏 | `docs/refactor/phase-0/core-path-matrix.md` | 已输出 12 条核心路径，覆盖 Compose、RN Host、Root RN、Bridge 扩展页 | `validated` | `green` | 待指定 | 待指定 | 2026-03-14 |
| V0-02 | Phase 0 | 资产清单具备代码定位 | route、Bridge、storage、schema、config 具备路径级引用 | `docs/refactor/phase-0/asset-inventory.md` | 已盘点 Native routes、RN 组件、Bridge 模块、存储、数据库与构建配置，并附代码定位 | `validated` | `green` | 待指定 | 待指定 | 2026-03-14 |
| V0-03 | Phase 0 | 基线数据可复现 | 指标、设备、环境、轮次记录完整 | `docs/refactor/phase-0/device-matrix-and-measurement-protocol.md`, `docs/refactor/phase-0/baseline-snapshot.md`, `docs/refactor/phase-0/dynamic-baseline-run-2026-03-14.md`, `docs/refactor/evidence/profile-page-current-2026-03-15.png`, `docs/refactor/evidence/bookshelf-page-from-home-2026-03-15.png` | 已建立设备矩阵、静态基线，并在 `DN2101 / Android 13` 上完成冷启动、首页滚动、本地构建时长、阅读器正文翻页、“我的”页视觉/滚动样本，以及冷启动后首次进入“书架”页的 RN 代表首开样本；`profile` 根宿主页白屏保留为高风险遗留 | `validated` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V0-04 | Phase 0 | 风险与禁区清单完整 | 风险分级明确，禁区可执行 | `docs/refactor/phase-0/risk-register-and-no-go-zones.md` | 已输出 blocker/high/medium/low 风险图谱与禁区清单，可直接用于 Phase 1 门禁 | `validated` | `green` | 待指定 | 待指定 | 2026-03-14 |
| V0-05 | Phase 0 | 测试数据方案与 kill switch 定稿 | 可直接进入 Phase 2 与后续灰度/回滚设计 | `docs/refactor/phase-0/stable-test-data-plan.md`, `docs/refactor/phase-0/kill-switch-minimal-plan.md` | 已输出 fixture/fake data 分层建议与最小 kill switch 范围、形态和适用场景 | `validated` | `green` | 待指定 | 待指定 | 2026-03-14 |
| V0-06 | Phase 0 | Phase 1 进入条件明确 | blocker、关闭标准和进入标准客观可判断 | `docs/refactor/phase-0/phase-1-entry-criteria.md` | 已输出 Phase 1 放行门禁、允许遗留项范围与 blocker 条件 | `validated` | `green` | 待指定 | 待指定 | 2026-03-14 |

## Phase 1
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1-01 | Phase 1 | release 构建成功并有产物报告 | 构建与产物可复现 | `android/app/build.gradle`, `android/app/proguard-rules.pro`, `android/app/build/outputs/apk/release/output-metadata.json`, `android/app/build/outputs/apk/release/app-release.apk`, `android/app/build/outputs/mapping/release/mapping.txt` | 已完成 release 生产化脚手架并通过 `assembleRelease`；当前产出 `app-release.apk`，版本 `1.0.2 (4)`，APK 大小约 `103.5 MB`，mapping 文件已生成 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-02 | Phase 1 | release 路径无硬编码 `http://` | endpoint 配置收口完成 | `android/app/build.gradle`, `android/app/src/main/java/com/novel/utils/network/ApiService.kt`, `android/app/src/main/java/com/novel/page/component/NovelCachedImageView.kt` | 已将后端 API 与图片 host 从 Kotlin 代码收口到 `BuildConfig`，并支持通过 `NOVEL_API_BASE_HOST` Gradle 属性或环境变量注入；当前 fallback 已限制为 debug，本地已通过 `assembleDebug` 与 `assembleRelease` 验证 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-03 | Phase 1 | release manifest 权限矩阵通过审查 | 权限最小化完成 | `android/app/src/main/AndroidManifest.xml`, `android/app/src/main/java/com/novel/utils/PhoneInfoUtil.kt`, `docs/refactor/phase-1/permission-matrix.md` | 已完成权限收口：移除 `READ_PRIVILEGED_PHONE_STATE`，将 `READ_PHONE_STATE` 限定到 Android 12 及以下，并补齐 manifest 与运行时逻辑映射文档 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-04 | Phase 1 | release 不全局开放 cleartext | 网络安全策略生效 | `android/app/src/main/AndroidManifest.xml`, `android/app/src/main/res/xml/network_security_config.xml`, `android/app/src/debug/AndroidManifest.xml`, `android/app/src/debug/res/xml/network_security_config.xml` | 已将主资源 cleartext 默认收紧为关闭，debug 资源单独保留放开策略，并对白名单主机 `47.110.147.60` 与 `lin.yyyai.xyz` 显式放行；本地已通过 `assembleRelease` 验证 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-05 | Phase 1 | schema 导出与 migration 策略完备 | 数据库发布路径可信 | `android/app/build.gradle`, `android/app/src/main/java/com/novel/utils/dao/NovelDatabase.kt`, `android/app/src/main/java/com/novel/di/DatabaseModule.kt`, `android/app/schemas/com.novel.utils.dao.NovelDatabase/4.json`, `docs/refactor/phase-1/database-migration-strategy.md` | 已完成 schema 导出、debug-only destructive migration 与 release 发布策略文档；当前版本 `4.json` schema 已生成 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-06 | Phase 1 | 迁移演练矩阵完成至少一轮 | 升级风险可评估 | `docs/refactor/phase-1/migration-rehearsal-matrix.md`, `docs/refactor/phase-1/migration-rehearsal-round-1-2026-03-15.md` | 已补数据库、SharedPreferences、KeyChain 三类资产的迁移演练矩阵与执行模板，并完成一轮“重装保留数据”实操验证，确认数据库文件与关键偏好字段保留 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-07 | Phase 1 | WebView 安全策略代码与文档一致 | 域名、SSL、外链、mixed content 规则明确 | `android/app/src/main/java/com/novel/page/welfare/component/WebViewComponent.kt`, `android/app/src/main/java/com/novel/page/welfare/utils/WelfareWebSecurityConfig.kt`, `android/app/src/main/java/com/novel/page/welfare/WelfarePage.kt`, `android/app/src/main/java/com/novel/page/welfare/viewmodel/WelfareMvi.kt`, `android/app/src/main/res/xml/network_security_config.xml`, `android/app/build.gradle`, `docs/refactor/evidence/welfare-bing-2026-03-15.png` | 已将福利页默认测试页切换为 `https://www.bing.com/`，补充 `bing.com/www.bing.com/cn.bing.com` 白名单，并完成真机验证：页面实际跳转到 `https://cn.bing.com/` 且 `OnPageFinished` 成功触发 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-08 | Phase 1 | signing、环境注入、依赖验证可追溯 | 发布与供应链基础完备 | `android/app/build.gradle`, `.gitignore`, `android/gradle/verification-metadata.xml`, `docs/refactor/phase-1/release-signing-and-env.md` | 已具备 release signing 脚手架、API host 环境注入入口、Gradle 依赖校验元数据和配套说明文档；`assembleRelease` 已验证通过 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V1-09 | Phase 1 | benchmark 目标、版本、环境明确 | 性能基线可信 | `android/macrobenchmark/build.gradle`, `docs/refactor/phase-1/benchmark-environment.md`, `android/gradle/verification-metadata.xml` | 已固定 `uiautomator` 版本为 `2.3.0`，补充 benchmark 模块的依赖、构建形态、执行命令与限制说明，并通过 `:macrobenchmark:assemble` 构建验证 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |

## Phase 2
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V2-01 | Phase 2 | `src/test` 已启用并纳入构建 | JVM test 基础设施完成 | `android/app/build.gradle`, `android/app/src/test/java/` | 已新增 `testImplementation` 依赖并创建 `src/test/java` 首批测试文件 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V2-02 | Phase 2 | 首批 JVM tests 稳定运行 | reducer/usecase/repository 可验证 | `android/app/src/test/java/com/novel/utils/security/SecurityConfigTest.kt`, `android/app/src/test/java/com/novel/utils/FormattingUtilsTest.kt`, `android/app/build/test-results/testDebugUnitTest/` | 已新增首批纯逻辑 JVM 测试，并通过 `app:testDebugUnitTest` 验证 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V2-03 | Phase 2 | fixtures/fake data 可复现 | 测试数据稳定且可共享 | `android/app/src/test/resources/fixtures/`, `android/app/src/test/java/com/novel/testing/`, `docs/refactor/phase-2/fixture-and-fake-data-catalog.md`, `android/app/build/test-results/testDebugUnitTest/` | 已建立阅读历史、用户偏好和首页推荐 fixture，并提供 `FixtureCatalog` 与 `FakeReaderHistorySource`；已通过 `app:testDebugUnitTest` 验证 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V2-04 | Phase 2 | Bridge contract tests 覆盖关键协议 | Native/RN 协议回归可发现 | `__tests__/bridge/UserBridge.contract.test.ts`, `__tests__/bridge/NavigationBridge.contract.test.ts`, `__tests__/bridge/NativeBridgeEventContracts.test.ts`, `__tests__/fixtures/bridge/`, `docs/refactor/phase-2/bridge-contract-catalog.md` | 已建立 UserBridge/NavigationBridge Promise 合同、ThemeChanged/WritePageSelectionMenuAction 事件合同与 Native source contract 测试，并通过 `npm test -- --runInBand --runTestsByPath __tests__/bridge/UserBridge.contract.test.ts __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/NativeBridgeEventContracts.test.ts` 验证 | `ready_for_validation` | `green` | 待指定 | 待指定 | 2026-03-15 |
| V2-05 | Phase 2 | smoke 覆盖关键页面 | 首页、登录、搜索、阅读器、设置具备自动验证 | `android/app/src/androidTest/java/com/novel/page/home/HomeSmokeTest.kt`, `android/app/src/androidTest/java/com/novel/page/login/LoginSmokeTest.kt`, `android/app/src/androidTest/java/com/novel/page/search/SearchSmokeTest.kt`, `android/app/src/androidTest/java/com/novel/page/read/viewmodel/ReaderSmokeTest.kt`, `__tests__/smoke/SettingsPage.smoke.test.tsx`, `docs/refactor/phase-2/smoke-suite-catalog.md` | 已补齐首页/登录/搜索/阅读器 Android smoke 与设置 RN smoke；`npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx` 与 `./gradlew.bat app:compileDebugAndroidTestKotlin` 已通过，但 `connectedDebugAndroidTest` 因当前 shell 的 ADB 未发现设备而未完成 | `blocked` | `yellow` | 待指定 | 待指定 | 2026-03-15 |
| V2-06 | Phase 2 | CI 包含 assemble、detekt、lint、JVM tests、RN lint/test | 最小自动化门禁在线 | 待补充 | 待补充 | `planned` | `yellow` | 待指定 | 待指定 | - |
| V2-07 | Phase 2 | PR 有阻断型门禁，不再只有 label workflow | 不合格提交无法直接通过 | 待补充 | 待补充 | `planned` | `yellow` | 待指定 | 待指定 | - |
| V2-08 | Phase 2 | size diff、benchmark diff、截图/录屏证据归档格式固定 | 验证证据统一可追溯 | 待补充 | 待补充 | `planned` | `yellow` | 待指定 | 待指定 | - |
| V2-09 | Phase 2 | flake 规则可执行且有责任人 | 门禁具备长期维护能力 | 待补充 | 待补充 | `planned` | `yellow` | 待指定 | 待指定 | - |
