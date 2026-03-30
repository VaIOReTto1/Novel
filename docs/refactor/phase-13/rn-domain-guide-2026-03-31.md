# RN Domain Guide

## 状态
- 记录日期：`2026-03-31`
- 关联阶段：`Stage 6 / Phase 13`
- 当前结论：`已固定 RN 页面域治理宿主`

## 当前页面域
1. `Profile + app root preload`
2. `Settings + TimeSwitch + privacy/help`
3. `Bookshelf / History / Watchlist / Community`
4. `Comment / ReviewDetail / WriteReview`
5. `Writer / AIWriteAssistant / BookManage`
6. `ScrollBox heavy pages`

## 当前问题
- 各域的 `store / hooks / components / types / styles` 颗粒度不统一
- 部分页域同时承担 bridge 调用、运行时职责和 UI 组织

## 收口目标
- 每个页面域形成清晰入口
- 每个域内部的 store / hook / component 边界可追溯
