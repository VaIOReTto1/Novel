# Phase 0-2 验证看板

## 使用规则
- 每条记录必须填写 `Expected / Evidence / Actual / Status / Result Analysis / Owner / Validator / Validated On`
- `Status` 仅允许：
  - `planned`
  - `in_progress`
  - `blocked`
  - `ready_for_validation`
  - `validated`
  - `not_met`
  - `deferred`
- `Result Analysis` 仅允许：
  - `green`
  - `yellow`
  - `red`

## Phase 0
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V0-01 | Phase 0 | 核心路径矩阵完整 | 12 条核心路径无遗漏 | `docs/refactor/phase-0/core-path-matrix.md` | 已完成首页、书详情、阅读器、登录、搜索、福利、设置、分类、书架、我的、作者、AI 路径矩阵 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-14 |
| V0-02 | Phase 0 | 资产清单具备代码定位 | route / bridge / storage / schema / config 可追溯 | `docs/refactor/phase-0/asset-inventory.md` | 已完成 Native routes、RN 组件、Bridge 模块、存储、数据库与构建配置盘点 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-14 |
| V0-03 | Phase 0 | 基线数据可复现 | 指标、设备、环境、轮次记录完整 | `docs/refactor/phase-0/device-matrix-and-measurement-protocol.md`, `docs/refactor/phase-0/baseline-snapshot.md`, `docs/refactor/phase-0/dynamic-baseline-run-2026-03-14.md` | 已建立静态/动态基线并补齐首页、阅读器、RN 代表样本证据 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V0-04 | Phase 0 | 风险与禁区清单完整 | 风险分级明确且禁区可执行 | `docs/refactor/phase-0/risk-register-and-no-go-zones.md` | blocker/high/medium/low 风险与禁区已固化 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-14 |
| V0-05 | Phase 0 | 测试数据方案与 kill switch 定稿 | 可直接支撑 Phase 2 与后续灰度/回滚 | `docs/refactor/phase-0/stable-test-data-plan.md`, `docs/refactor/phase-0/kill-switch-minimal-plan.md` | 已完成 fixture/fake data 与最小 kill switch 方案 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-14 |
| V0-06 | Phase 0 | Phase 1 进入条件明确 | blocker 与进入标准可客观判断 | `docs/refactor/phase-0/phase-1-entry-criteria.md` | 已形成客观进入条件和 blocker 判定标准 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-14 |

## Phase 1
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1-01 | Phase 1 | release 构建成功并有产物报告 | 构建与产物可复现 | `android/app/build.gradle`, `android/app/build/outputs/apk/release/`, `android/app/build/outputs/mapping/release/` | `assembleRelease` 通过并产出正式 APK 与 mapping | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-02 | Phase 1 | release 路径无硬编码 `http://` | endpoint 配置收口完成 | `android/app/build.gradle`, `android/app/src/main/java/com/novel/utils/network/ApiService.kt`, `android/app/src/main/java/com/novel/page/component/NovelCachedImageView.kt` | API 与图片 host 已收口到 `BuildConfig` 并支持环境注入 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-03 | Phase 1 | release manifest 权限矩阵通过审查 | 权限最小化完成 | `android/app/src/main/AndroidManifest.xml`, `docs/refactor/phase-1/permission-matrix.md` | 不合规权限已移除或收紧，权限矩阵已成文 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-04 | Phase 1 | cleartext 未对 release 全局开放 | 网络安全策略生效 | `android/app/src/main/res/xml/network_security_config.xml`, `android/app/src/debug/res/xml/network_security_config.xml` | 主资源默认收紧，debug 独立放开 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-05 | Phase 1 | schema 导出与 migration 策略完备 | 数据库发布路径可信 | `android/app/schemas/`, `docs/refactor/phase-1/database-migration-strategy.md` | schema 导出开启，destructive migration 仅保留 debug | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-06 | Phase 1 | 迁移演练矩阵至少完成一轮 | 升级风险可评估 | `docs/refactor/phase-1/migration-rehearsal-matrix.md`, `docs/refactor/phase-1/migration-rehearsal-round-1-2026-03-15.md` | 已完成首轮重装保留数据实操演练 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-07 | Phase 1 | WebView 安全策略代码与文档一致 | 域名、SSL、外链、mixed content 规则明确 | `docs/refactor/evidence/welfare-bing-2026-03-15.png`, `docs/refactor/phase-1/benchmark-environment.md` | 福利 WebView 白名单与真机验证已完成 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-08 | Phase 1 | signing、环境注入、依赖验证可追溯 | 发布与供应链基础完备 | `android/gradle/verification-metadata.xml`, `docs/refactor/phase-1/release-signing-and-env.md` | signing/env/dependency verification 已落地 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V1-09 | Phase 1 | benchmark 目标、版本、环境明确 | 性能基线可信 | `android/macrobenchmark/build.gradle`, `docs/refactor/phase-1/benchmark-environment.md` | benchmark 模块已固定版本并通过 assemble 验证 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |

## Phase 2
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V2-01 | Phase 2 | `src/test` 已启用并纳入构建 | JVM test 基础设施完成 | `android/app/build.gradle`, `android/app/src/test/java/` | 已建立正式 `src/test` 与依赖基础 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V2-02 | Phase 2 | 首批 JVM tests 稳定运行 | reducer/usecase/repository 可验证 | `android/app/build/test-results/testDebugUnitTest/` | 首批 JVM tests 已通过 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V2-03 | Phase 2 | fixtures/fake data 可复现 | 测试数据稳定且可共享 | `docs/refactor/phase-2/fixture-and-fake-data-catalog.md` | fixture/fake data 基础层已完成 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V2-04 | Phase 2 | Bridge contract tests 覆盖关键协议 | Native/RN 协议回归可发现 | `docs/refactor/phase-2/bridge-contract-catalog.md` | Bridge contract tests 已建立并通过 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V2-05 | Phase 2 | smoke 覆盖关键页面 | 首页、登录、搜索、阅读器、设置具备自动验证 | `docs/refactor/phase-2/smoke-suite-catalog.md`, `docs/refactor/phase-2/smoke-run-android-core-2026-03-16.md`, `docs/refactor/phase-2/smoke-run-rn-settings-2026-03-16.md` | Android / RN 核心 smoke 已完成并补齐真机与本地证据 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-16 |
| V2-06 | Phase 2 | CI 包含 assemble、detekt、lint、JVM tests、RN lint/test | 最小自动化门禁在线 | `.github/workflows/quality-gates.yml`, `docs/refactor/phase-2/ci-workflow-catalog.md`, `docs/refactor/phase-2/pr-gate-and-ownership-matrix.md` | 已建立 blocking + observe 质量 workflow，并验证 blocking 命令基线可执行 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-16 |
| V2-07 | Phase 2 | PR 有阻断型门禁，不再只有 label workflow | 不合格提交无法直接通过 | `.github/workflows/quality-gates.yml`, `.github/workflows/label.yml`, `docs/refactor/phase-2/pr-gate-and-ownership-matrix.md` | PR 已具备 blocking jobs，不再只有 label workflow | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-16 |
| V2-08 | Phase 2 | size diff、benchmark diff、截图/录屏证据归档格式固定 | 验证证据统一可追溯 | `docs/refactor/phase-2/evidence-archive-standard.md`, `docs/refactor/evidence/` | 证据目录、模板、字段标准已固定 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-15 |
| V2-09 | Phase 2 | flake 规则可执行且有责任人 | 门禁具备长期维护能力 | `docs/refactor/phase-2/flake-management-policy.md`, `docs/refactor/phase-2/pr-gate-and-ownership-matrix.md` | flake 规则与默认责任矩阵已固定 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | 2026-03-16 |
