# Phase 15-18 验证看板

## 当前状态
- `Stage 7`: `in_progress`
- `Phase 15`: `in_progress`
- `Phase 16`: `planned`
- `Phase 17`: `planned`
- `Phase 18`: `planned`
- 最新更新：`2026-03-31`

## Phase 15
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V15-01 | Stage 7 authority cutover | `README / roadmap / stage plan / harness` 一致切到 Stage 7 | 当前已建立 Stage 7 authority 宿主与导航入口 | `in_progress` | `yellow` |
| V15-02 | surface inventory | RN Root / RN Host / RN Nested / Android Native / Android Shell 全覆盖 | 已由 `scripts/stage7-audit.js` 生成 `surface-inventory.json`，当前统计 `51` 个 surface，仍待补 Figma frame id | `in_progress` | `yellow` |
| V15-03 | component catalog | RN / Android 共享组件按类别收口 | 已生成 `component-catalog.json`，当前已收口 `128` 个 RN component 文件，后续需补 Android 共享基元聚类深化 | `in_progress` | `yellow` |
| V15-04 | asset inventory | 图标 / 图片 / 插画 / 字体入口可追踪 | 已生成 `asset-inventory.json`，当前已纳入本地 SVG、字体与 `react-native-vector-icons` family 基线 | `in_progress` | `yellow` |
| V15-05 | governance drift report | catalog / smoke / registry / Figma frame 漂移可发现 | 已生成 `governance-drift-report.md`，当前 registry drift 为 `none`，Figma mapping 仍全部待补 | `in_progress` | `yellow` |

## Phase 16
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V16-01 | Figma foundations | 语义色彩、排版、间距、圆角、阴影、动效建立真源 | 已建立 `stage7.tokens.json` 作为 repo 内语义 Token 真源草案，后续需与 Figma Variables 双向对齐 | `in_progress` | `yellow` |
| V16-02 | token export chain | `Figma -> JSON -> Style Dictionary -> LESS/RN/Android` 可跑通 | 已生成 `style-dictionary.tokens.json`、`tokens.less`、RN/Android token 产物，并接入质量门禁检查 | `in_progress` | `yellow` |
| V16-03 | dark / a11y / RTL rules | 暗黑、无障碍、RTL 规则成文并可映射到 Token | 当前 Token 已包含 light/dark 主题与 motion/typography 语义层，后续仍需补 RTL 与无障碍细则文档化 | `in_progress` | `yellow` |

## Phase 17
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V17-01 | Iconify governance | 图标命名、同步脚本、双端映射齐备 | 已生成 `icon-manifest.json` 与 RN `stage7IconRegistry.ts`，当前先以 legacy 本地 SVG 与 vector family 基线收口，后续继续替换为 Iconify 语义名 | `in_progress` | `yellow` |
| V17-02 | media governance | Picsum / Pexels / unDraw 资产与版权闭环 | 已生成 `media-manifest.json`、`illustration-manifest.json` 与 `copyright-ledger.json`，当前为 provider 规则与空账本基线 | `in_progress` | `yellow` |
| V17-03 | showcase infrastructure | RN Storybook 与 Android Showcase 可运行 | 已有 RN `Stage7Showcase.tsx` 与 web 显式入口开关，Android Showcase 与正式 Storybook 仍待补齐 | `in_progress` | `yellow` |

## Phase 18
| ID | Item | Expected | Actual | Status | Result |
| --- | --- | --- | --- | --- | --- |
| V18-01 | page rollout wave 1 | 壳层与高频入口换肤完成 | 待执行 | `planned` | `gray` |
| V18-02 | page rollout wave 2-4 | 核心内容页、评论创作线、次级页面完成 | 待执行 | `planned` | `gray` |
| V18-03 | visual regression and gates | 像素、无障碍、版权、性能门禁通过 | 待执行 | `planned` | `gray` |
