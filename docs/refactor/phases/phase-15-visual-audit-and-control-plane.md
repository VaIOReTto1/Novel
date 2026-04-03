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
- `surface-visual-specs`
- `component-catalog`
- `component-visual-specs`
- `asset-inventory`
- `governance-drift-report`
- `figma-frame-map`
- `visual-planning-summary`
- `stage-7-surface-audit` 校验脚本

## 当前产物入口
- [surface-inventory.json](../phase-15/surface-inventory.json)
- [surface-visual-specs.json](../phase-15/surface-visual-specs.json)
- [component-catalog.json](../phase-15/component-catalog.json)
- [component-visual-specs.json](../phase-15/component-visual-specs.json)
- [asset-inventory.json](../phase-15/asset-inventory.json)
- [figma-frame-map.json](../phase-15/figma-frame-map.json)
- [governance-drift-report.md](../phase-15/governance-drift-report.md)
- [visual-planning-summary.md](../phase-15/visual-planning-summary.md)
- [Stage 7 Figma file](https://www.figma.com/design/7YaJPjyzLvGLfVPTkUx0Tf)

## 关键规则
- `App -> ProfilePage` 必须作为 RN Root 单独盘点，不得只看 `componentRegistry.ts`。
- `BookshelfPageComponent` 内部的 `History / Bookshelf / Watchlist / Community` 必须拆开盘点。
- Android 壳层、Skeleton、BottomSheet、Panel、Dialog 必须单独成面。
- 事实对账以脚本输出为准，不以手工目录或历史 catalog 为准。
- 每一个 surface 和每一个 component 都必须同时拥有 `current_look_recorded` 与 `target_look_planned` 描述，不允许只记“现状”或只记“目标”。
- Figma 文件中的 `00-现状审计`、`02-组件规范`、`03-页面-亮色`、`04-页面-暗色` 必须承载逐项卡片，不能只保留空页面骨架。

## 当前状态
- `in_progress`
