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

## 当前状态
- `planned`
