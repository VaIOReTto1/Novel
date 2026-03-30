# Phase 14 - RN 契约、质量与可维护性

## 目标
- 把 RN 侧的 contract、registry、mock/fallback、命名与状态模型统一成长期治理入口。

## 范围
- bridge contract tests / smoke
- RN component registry consistency
- mock / fallback / fail-closed catalog
- naming / directory / state model guide

## 非目标
- 不重新定义 Native contract
- 不直接承诺所有 heavy pages 都切到真实数据源
- 不重开 `Stage 5` 的 Android 治理主题

## 当前仓库入口基线
- 已有：
  - `__tests__/bridge/**`
  - `__tests__/smoke/SettingsPage.smoke.test.tsx`
  - `index.js` + 多个 `*Component.tsx` 页面注册入口
  - 多处仍在使用 mock / fallback 数据的 RN heavy pages
- 当前仍缺：
  - RN component registry consistency 宿主
  - RN 专属 mock / fallback backlog 宿主
  - RN 命名 / 目录 / 状态模型专项指南

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P14.1 | 固定 RN contract quality 宿主 | bridge / smoke / fixture 的当前护栏清晰 |
| P14.2 | 固定 RN component registry consistency | 页面注册名和 owner 一致性可追溯 |
| P14.3 | 固定 RN mock / fallback catalog | 数据质量 debt 不再散点漂移 |
| P14.4 | 固定 RN maintainability guide | 命名 / 目录 / 状态模型规则清楚 |
| P14.5 | 输出 Phase 14 closeout 宿主 | Stage 6 长期治理层关闭 |

## 交付物
- `rn-contract-quality-host-2026-03-31.md`
- `rn-component-registry-consistency-2026-03-31.md`
- `rn-mock-fallback-catalog-2026-03-31.md`

## 当前状态
- `planned`
