# Phase 15-18 验证看板

## 当前状态
- `Stage 7`: `in_progress`
- `Phase 15`: `in_progress`
- `Phase 16`: `in_progress`
- `Phase 17`: `in_progress`
- `Phase 18`: `in_progress`
- 最新更新：`2026-04-04`

## Phase 15
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V15-01 | Stage 7 authority cutover | `README / roadmap / stage plan / harness` 一致切到 Stage 7 | 当前已建立 Stage 7 authority 宿主与导航入口 | `in_progress` | `yellow` |
| V15-02 | surface inventory | RN Root / RN Host / RN Nested / Android Native / Android Shell 全覆盖 | 已由 `scripts/novel-design-audit.js` 生成 `surface-inventory.json` 与 `surface-visual-specs.json`，当前统计 `51` 个 surface，且每个 surface 都已记录 current/target look；Figma `00-现状审计` / `03-页面-亮色` / `04-页面-暗色` 已种入逐项 surface 卡片，仍待回填 frame id | `in_progress` | `yellow` |
| V15-03 | component catalog | RN / Android 共享组件按类别收口 | 已生成 `component-catalog.json` 与 `component-visual-specs.json`，当前已收口 `225` 个组件条目（`143` 个 RN + `82` 个 Android），且每个 component 都已记录 current/target look；Figma `02-组件规范` 已种入逐项 component 卡片，后续继续补 Android 共享基元聚类深化 | `in_progress` | `yellow` |
| V15-04 | asset inventory | 图标 / 图片 / 插画 / 字体入口可追踪 | 已生成 `asset-inventory.json`，当前已纳入本地 SVG、字体与 `react-native-vector-icons` family 基线 | `in_progress` | `yellow` |
| V15-05 | governance drift report | catalog / smoke / registry / Figma frame 漂移可发现 | 已生成 `governance-drift-report.md` 与 `visual-planning-summary.md`，当前 registry drift 为 `none`、RN smoke tests 已扩到 `16` 条且 smoke catalog drift 为 `none`，Figma mapping 仍全部待补 | `in_progress` | `yellow` |

## Phase 16
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V16-01 | Figma foundations | 语义色彩、排版、间距、圆角、阴影、动效建立真源 | 已建立 `novel-design.tokens.json` 作为 repo 内语义 Token 真源草案，后续需与 Figma Variables 双向对齐 | `in_progress` | `yellow` |
| V16-02 | token export chain | `Figma -> JSON -> Style Dictionary -> LESS/RN/Android` 可跑通 | 已生成 `style-dictionary.tokens.json`、`tokens.less`、RN/Android token 产物，并接入质量门禁检查 | `in_progress` | `yellow` |
| V16-03 | dark / a11y / RTL rules | 暗黑、无障碍、RTL 规则成文并可映射到 Token | 当前 Token 已包含 light/dark 主题与 motion/typography 语义层，并已新增 `phase-16-token-platform-rules.md` 作为集中规则宿主；后续仍需补 Figma Variables 与平台证据的逐项对齐 | `in_progress` | `yellow` |

## Phase 17
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V17-01 | Iconify governance | 图标命名、同步脚本、双端映射齐备 | 已生成 `icon-manifest.json` 与 RN `novelDesignIconRegistry.ts`，当前先以 legacy 本地 SVG 与 vector family 基线收口，后续继续替换为 Iconify 语义名 | `in_progress` | `yellow` |
| V17-02 | media governance | Picsum / Pexels / unDraw 资产与版权闭环 | 已生成 `media-manifest.json`、`illustration-manifest.json` 与 `copyright-ledger.json`，当前已记录 showcase demo 的 `pexels-demo-showcase` 样例条目，provider 规则与 ledger 宿主均已落盘 | `in_progress` | `yellow` |
| V17-03 | showcase infrastructure | RN Storybook 与 Android Showcase 可运行 | 已有 RN `NovelDesignShowcase.tsx`、web 显式入口开关与 web shim，`npx webpack --config webpack.config.js` 已跑通；Android 已补 `NovelDesignShowcaseScreen.kt` / `NovelDesignShowcaseModel.kt` 骨架与 unit test，并已在 `NavigationUtil.kt` 接通 `novel_design_showcase` compose route；当前已补 `showcase-runbook.md` 作为团队可复现入口说明，后续再视需要升级为独立 Storybook 宿主 | `in_progress` | `yellow` |

## Phase 18
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V18-01 | page rollout wave 1 | 壳层与高频入口换肤完成 | 已落地 `ProfilePage`、`SettingsPage`、`CategoryPage`、`BookshelfPage`、`MemberCenterPage`、`CommentPage`、`ReviewDetailPage`、`WriteReviewPage`、`WritePage`、`AIWriteAssistant`、`BookManagePage`，并切入统一 `NovelDesignUI` 配置层 | `in_progress` | `yellow` |
| V18-02 | page rollout wave 2-4 | 核心内容页、评论创作线、次级页面完成 | 评论创作线与写作线持续推进中；本轮已继续收口 `BecomeWriterPage`、`RecommendBookPage`、`ViewedUsersPage`、`MyReservationPage`、`MessagePage` 的组件文案与可读 mock 数据，并补齐对应 RN 组件回归；新正式 Figma 宿主中已为 `rn-host-recommend-book-page-component` 补下首套亮/暗/标注样例；Bookshelf 四个内嵌子页与剩余次级页面仍待继续换肤 | `in_progress` | `yellow` |
| V18-03 | visual regression and gates | 像素、无障碍、版权、性能门禁通过 | 已完成 `npm test -- --runInBand` 全量 Jest 回归，结果 `108` 个 suites / `260` 个 tests 全绿；已完成 Android 共享 gate `app:testDebugUnitTest app:lintDebug app:compileDebugAndroidTestKotlin :macrobenchmark:assemble`；已新增 `stage7-closeout-readiness.md` 作为 closeout 自动 readiness 报告；视觉稿、无障碍、版权核验与性能收尾仍待继续补齐 | `in_progress` | `yellow` |
