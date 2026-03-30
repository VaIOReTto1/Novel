# Phase 13 - RN 页面域重构

## 目标
- 按页面域收口 `store / hooks / components / types / styles` 的边界，避免 RN 继续以“每页一套小架构”的方式发散。

## 范围
- `Profile + app root preload`
- `Settings + TimeSwitch + privacy/help`
- `Bookshelf / History / Watchlist / Community`
- `Comment / ReviewDetail / WriteReview`
- `Writer / AIWriteAssistant / BookManage`
- `ScrollBox heavy pages`

## 非目标
- 不在一个提交里重排多个页面域
- 不把运行时桥接问题带回页面域阶段
- 不重建视觉设计系统

## 当前仓库入口基线
- 当前 page/store/hook/component 数量级已经足够支持分域治理。
- `BookshelfPage`、`SettingsPage`、`Writer` 相关域内部仍存在多子页、多 store、多 hooks 的嵌套结构。
- `ProfilePage` 仍兼具 app root preload 和页面展示职责，是第一优先域。

## 优先域顺序
1. `Profile + app root preload`
2. `Settings + TimeSwitch + privacy/help`
3. `Bookshelf / History / Watchlist / Community`
4. `Comment / ReviewDetail / WriteReview`
5. `Writer / AIWriteAssistant / BookManage`
6. `ScrollBox heavy pages`

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P13.1 | 固定 RN domain guide | 每类页面域默认边界成立 |
| P13.2 | 固定 Profile / Settings 第一批域边界 | 首批高耦合入口域收口 |
| P13.3 | 固定 Bookshelf / Comment 中段域边界 | 多子页多 store 域收口 |
| P13.4 | 固定 Writer / heavy pages 后段域边界 | 长尾页面域进入同一规则 |
| P13.5 | 输出 Phase 13 closeout 宿主 | 页面域边界关闭 |

## 交付物
- `rn-domain-guide-2026-03-31.md`

## 当前状态
- `planned`
