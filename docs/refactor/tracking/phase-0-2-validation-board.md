# Phase 0-2 验证看板

## 使用规则
- 每条记录都必须填写 `Expected / Evidence / Actual / Status / Result Analysis / Owner / Validator / Validated On`。
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
| V0-01 | Phase 0 | 核心路径矩阵完整 | 首页、书详情、阅读器、登录、搜索、福利、设置、分类、书架、我的、作者、AI 页面无遗漏 | `docs/refactor/phase-0/core-path-matrix.md` | 已输出 12 条核心路径，覆盖 Compose、RN Host、Root RN、Bridge 扩展页 | validated | green | 待指定 | 待指定 | 2026-03-14 |
| V0-02 | Phase 0 | 资产清单具备代码定位 | route、Bridge、storage、schema、config 具备路径级引用 | `docs/refactor/phase-0/asset-inventory.md` | 已盘点 Native routes、RN 组件、Bridge 模块、存储、数据库与构建配置，并附代码定位 | validated | green | 待指定 | 待指定 | 2026-03-14 |
| V0-03 | Phase 0 | 基线数据可复现 | 指标、设备、环境、轮次记录完整 | `docs/refactor/phase-0/device-matrix-and-measurement-protocol.md`, `docs/refactor/phase-0/baseline-snapshot.md`, `docs/refactor/phase-0/dynamic-baseline-run-2026-03-14.md`, `docs/refactor/evidence/profile-page-current-2026-03-15.png`, `docs/refactor/evidence/bookshelf-page-from-home-2026-03-15.png` | 已建立设备矩阵、静态基线，并在 `DN2101 / Android 13` 上完成冷启动、首页滚动、本地构建时长、阅读器正文翻页、“我的”页视觉/滚动样本，以及冷启动后首次进入“书架”页的 RN 代表首开样本；`profile` 根宿主页白屏保留为高风险遗留 | validated | green | 待指定 | 待指定 | 2026-03-15 |
| V0-04 | Phase 0 | 风险与禁区清单完整 | 风险分级明确，禁区可执行 | `docs/refactor/phase-0/risk-register-and-no-go-zones.md` | 已输出 blocker/high/medium/low 风险图谱与禁区清单，可直接用于 Phase 1 门禁 | validated | green | 待指定 | 待指定 | 2026-03-14 |
| V0-05 | Phase 0 | 测试数据方案与 kill switch 定稿 | 可直接进入 Phase 2 与后续灰度/回滚设计 | `docs/refactor/phase-0/stable-test-data-plan.md`, `docs/refactor/phase-0/kill-switch-minimal-plan.md` | 已输出 fixture/fake data 分层建议与最小 kill switch 范围、形态和适用场景 | validated | green | 待指定 | 待指定 | 2026-03-14 |
| V0-06 | Phase 0 | Phase 1 进入条件明确 | blocker、关闭标准和进入标准客观可判断 | `docs/refactor/phase-0/phase-1-entry-criteria.md` | 已输出 Phase 1 放行门禁、允许遗留项范围与 blocker 条件 | validated | green | 待指定 | 待指定 | 2026-03-14 |

## Phase 1
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V1-01 | Phase 1 | release 构建成功并有产物报告 | 构建与产物可复现 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V1-02 | Phase 1 | release 路径无硬编码 `http://` | endpoint 配置收口完成 | `android/app/build.gradle`, `android/app/src/main/java/com/novel/utils/network/ApiService.kt`, `android/app/src/main/java/com/novel/page/component/NovelCachedImageView.kt` | 已将后端 API 与图片 host 从 Kotlin 代码收口到 `BuildConfig`，为后续替换安全地址与环境注入做准备 | in_progress | yellow | 待指定 | 待指定 | 2026-03-15 |
| V1-03 | Phase 1 | release manifest 权限矩阵通过审查 | 权限最小化完成 | `android/app/src/main/AndroidManifest.xml` | 已启动权限最小化收口，第一步移除 `READ_PRIVILEGED_PHONE_STATE` | in_progress | yellow | 待指定 | 待指定 | 2026-03-15 |
| V1-04 | Phase 1 | release 不全局开放 cleartext | 网络安全策略生效 | `android/app/src/main/AndroidManifest.xml`, `android/app/src/main/res/xml/network_security_config.xml`, `android/app/src/debug/AndroidManifest.xml`, `android/app/src/debug/res/xml/network_security_config.xml` | 已将主资源 cleartext 默认收紧为关闭，debug 资源单独保留放开策略 | in_progress | yellow | 待指定 | 待指定 | 2026-03-15 |
| V1-05 | Phase 1 | schema 导出与 migration 策略完备 | 数据库发布路径可信 | `android/app/build.gradle`, `android/app/src/main/java/com/novel/utils/dao/NovelDatabase.kt`, `android/app/src/main/java/com/novel/di/DatabaseModule.kt`, `android/app/schemas/com.novel.utils.dao.NovelDatabase/4.json` | 已启动 Room 发布路径治理：开启 schema 导出，并将 destructive migration 限制为 debug 路径；当前版本 `4.json` schema 已生成 | in_progress | yellow | 待指定 | 待指定 | 2026-03-15 |
| V1-06 | Phase 1 | 迁移演练矩阵完成至少一轮 | 升级风险可评估 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V1-07 | Phase 1 | WebView 安全策略代码与文档一致 | 域名、SSL、外链、mixed content 规则明确 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V1-08 | Phase 1 | signing、环境注入、依赖验证可追溯 | 发布与供应链基础完备 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V1-09 | Phase 1 | benchmark 目标、版本、环境明确 | 性能基线可信 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |

## Phase 2
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V2-01 | Phase 2 | `src/test` 已启用并纳入构建 | JVM test 基础设施完成 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-02 | Phase 2 | 首批 JVM tests 稳定运行 | reducer/usecase/repository 可验证 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-03 | Phase 2 | fixtures/fake data 可复现 | 测试数据稳定且可共享 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-04 | Phase 2 | Bridge contract tests 覆盖关键协议 | Native/RN 协议回归可发现 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-05 | Phase 2 | smoke 覆盖关键页面 | 首页、登录、搜索、阅读器、设置具备自动验证 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-06 | Phase 2 | CI 覆盖最小门禁集合 | assemble、detekt、lint、tests、smoke 在线 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-07 | Phase 2 | PR 存在阻断型门禁 | 不合格提交不可直接通过 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-08 | Phase 2 | diff 与证据归档格式固定 | size、benchmark、截图/录屏可追溯 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
| V2-09 | Phase 2 | flake 规则可执行且有责任人 | 门禁长期可维护 | 待补充 | 待补充 | planned | yellow | 待指定 | 待指定 | - |
