# Phase 13 - RN 页面域重构

## 目标
- 按页面域收口 `store / hooks / components / types / styles` 的边界。
- 让页面组件本身不再继续承担 preload、section 构造和 domain orchestration。

## 范围
- `Profile + app root preload`
- `Settings + TimeSwitch + privacy/help`
- `Bookshelf / History / Watchlist / Community`
- `Comment / ReviewDetail / WriteReview`
- `Writer / AIWriteAssistant / BookManage`
- `ScrollBox heavy pages`

## 非目标
- 不重开 `Phase 12` 的 runtime / bridge consolidation
- 不变更对外 `route`、bridge payload、`componentName`
- 不做全仓一次性目录重排

## 当前进展
| ID | Task | 当前结果 |
| --- | --- | --- |
| P13.1 | 固定 RN domain guide | 宿主已存在，后续继续作为边界准绳 |
| P13.2 | 固定 Profile / Settings 第一批域边界 | 已建立 `profileBootstrap` 与 `settingsPageModel`，并将页面委派到域层 helper |
| P13.3 | 固定 Bookshelf / Comment 中段域边界 | 待执行 |
| P13.4 | 固定 Writer / heavy pages 后段域边界 | 待执行 |
| P13.5 | 输出 Phase 13 closeout 宿主 | 待执行 |

## 当前证据入口
- `docs/refactor/phase-13/profile-settings-domain-wave-2026-03-31.md`
- `docs/refactor/phase-13/rn-domain-guide-2026-03-31.md`
- `__tests__/domains/profileBootstrap.test.ts`
- `__tests__/domains/settingsPageModel.test.ts`
- `__tests__/smoke/SettingsPage.smoke.test.tsx`

## 当前状态
- `in_progress`
