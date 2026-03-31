# 第七阶段重构计划（Stage 7 = Phase 15-18）

## 当前状态
- `in_progress`

## 阶段定义
- `Stage 7 = Phase 15 + Phase 16 + Phase 17 + Phase 18`
- 目标是在不改变现有功能逻辑、路由语义、Bridge 契约和页面行为的前提下，建立跨 Android / React Native 的视觉系统与资产治理闭环。

## 阶段约束
- 不复用历史 `Stage 4 / Phase 7` 的命名和结论。
- 先建立控制面、盘点与 Token 真源，再推进资产同步和页面重皮肤。
- 所有页面、组件、状态面、资产、Figma frame 都必须可被脚本和文档双向追踪。
- 当前仓库已知的既有 bridge 契约测试失败不属于 Stage 7 引入变更；Stage 7 新改动必须用新增验证与现有门禁分别证明正确性。

## Phase 15
- 主题：现状审计与控制面
- 范围：
  - 建立 `surface-inventory`、`component-catalog`、`asset-inventory`、`governance-drift-report`
  - 建立 Figma `00-现状审计` 页组与 `figma-frame-map`
  - 建立注册页、目录扫描、smoke、catalog、Figma frame 的事实对账脚本
- 关闭条件：
  - 没有孤儿页面、孤儿组件、孤儿状态面
  - 没有 catalog 漂移
  - Stage 7 控制面与 harness 导航完成切换

## Phase 16
- 主题：Figma 基础系统与 Token 真源
- 范围：
  - 建立 Figma Variables 与 Components
  - 建立语义色彩、字体、间距、圆角、阴影、动效曲线
  - 建立 `Figma Variables -> JSON -> Style Dictionary -> LESS / RN / Android` 导出链路
- 关闭条件：
  - 语义 Token 全量落地
  - 多端导出产物一致
  - 暗黑模式、无障碍、RTL 规则成文

## Phase 17
- 主题：资产治理、共享基元与展示基建
- 范围：
  - Iconify 本地同步
  - Picsum 占位图组件、Pexels 版权台账、unDraw 主题化插画同步
  - RN Storybook Web 与 Android Showcase
  - 图标、图片、插画、版权叠层的共享基元
- 关闭条件：
  - 资产 manifest、版权 ledger、同步脚本齐备
  - 双端展示基建可运行
  - 不再新增未受控的本地图标/图片入口

## Phase 18
- 主题：页面重皮肤、回归门禁与收尾
- 范围：
  - 壳层、高频入口、核心内容页、评论/创作线、ScrollBox 次级页分波次换肤
  - Figma 高保真、标注稿、组件映射、视觉回归基线
  - 设计、产品、QA 三方评审与 closeout
- 关闭条件：
  - 关键页面视觉回归稳定
  - 无障碍、版权、性能门禁达到约束
  - Stage 7 closeout 文档与报告齐全

## 当前执行顺序
1. `Phase 15` 控制面和机器清单
2. `Phase 16` Token 真源与导出链路
3. `Phase 17` 资产同步与展示基建
4. `Phase 18` 页面重皮肤与收尾

## 当前权威链接
- [Phase 15 宿主文档](./phases/phase-15-visual-audit-and-control-plane.md)
- [Phase 16 宿主文档](./phases/phase-16-figma-foundations-and-token-source.md)
- [Phase 17 宿主文档](./phases/phase-17-asset-governance-and-shared-primitives.md)
- [Phase 18 宿主文档](./phases/phase-18-visual-rollout-and-quality-gates.md)
- [Phase 15-18 验证看板](./tracking/phase-15-18-validation-board.md)
