# Phase 17 - 资产治理、共享基元与展示基建

## 目标
- 把图标、图片、插画、版权台账和共享基元收拢为受控资产系统，并建立双端展示基建。

## 范围
- Iconify 本地同步
- Picsum 占位图组件
- Pexels 采集与版权台账
- unDraw 主题化插画同步
- RN Storybook Web
- Android Showcase

## 关键规则
- 不允许运行时直接请求 Iconify CDN。
- Pexels 图片必须先采集落库再消费。
- 视觉回归与展示环境中的 Picsum 必须固定 `seed`。
- 版权信息必须同时出现在 UI 叠层和 ledger 中。

## 当前产物入口
- [stage7.asset-providers.json](../../../design-system/source/stage7.asset-providers.json)
- [icon-manifest.json](../../../design-system/assets/icon-manifest.json)
- [stage7IconRegistry.ts](../../../src/design-system/icons/generated/stage7IconRegistry.ts)
- [Stage7Icon.tsx](../../../src/design-system/icons/Stage7Icon.tsx)
- [PlaceholderImage.tsx](../../../src/design-system/media/PlaceholderImage.tsx)
- [PexelsCreditOverlay.tsx](../../../src/design-system/media/PexelsCreditOverlay.tsx)
- [media-manifest.json](../../../design-system/assets/media-manifest.json)
- [illustration-manifest.json](../../../design-system/assets/illustration-manifest.json)
- [copyright-ledger.json](../../../design-system/assets/copyright-ledger.json)
- [asset-governance-report.md](../phase-17/asset-governance-report.md)

## 当前状态
- `in_progress`
