# Phase 15 - 现状审计与控制面

## 目标
- 为 Stage 7 建立唯一权威控制面、机器可对账清单、Figma 审计页和事实对账门禁。

## 范围
- 页面与组件视觉盘点
- 资产基线盘点
- Figma 审计页与 frame map
- governance drift report
- refactor / harness 导航切换

## 当前执行项
- `surface-inventory`
- `component-catalog`
- `asset-inventory`
- `governance-drift-report`
- `figma-frame-map`
- `stage-7-surface-audit` 校验脚本

## 关键规则
- `App -> ProfilePage` 必须作为 RN Root 单独盘点，不得只看 `componentRegistry.ts`。
- `BookshelfPageComponent` 内部的 `History / Bookshelf / Watchlist / Community` 必须拆开盘点。
- Android 壳层、Skeleton、BottomSheet、Panel、Dialog 必须单独成面。
- 事实对账以脚本输出为准，不以手工目录或历史 catalog 为准。

## 当前状态
- `in_progress`
