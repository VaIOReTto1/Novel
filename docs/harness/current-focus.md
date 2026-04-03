# Current Focus

## 当前状态
- 当前分支：`feature/stage-7-phase-15-audit`
- 当前权威结论：`Stage 7 = in_progress`，`Phase 15-18 = in_progress`
- 当前 refactor authority 以 `docs/refactor/README.md` 为准
- 当前 Android 模块图保持为 `app + core-* + feature-* + macrobenchmark`

## 最近完成
- `2026-03-30` 已完成 `Stage 4` closeout
- `2026-03-30` 已完成 `Stage 5` closeout
- `2026-03-31` 已完成 `Stage 6` closeout
- `2026-03-31` 已启动 `Stage 7`
- `2026-03-31` 已落地 Stage 7 的审计脚本、Token 基线和资产治理基线
- `2026-04-03` 已继续推进 ScrollBox 次级页换肤，补齐 `BecomeWriter / RecommendBook / ViewedUsers / MyReservation / Message` 的组件回归与 mock 文案收口
- `2026-04-03` 已跑通全量 Jest 与 Android 共享 gate，当前换肤波次具备继续推进 `Phase 18` 门禁收尾的验证基线
- `2026-04-03` 已为 Android 侧 `NovelDesignShowcaseScreen` 接通 `novel_design_showcase` 路由，推进 `V17-03 showcase infrastructure`
- `2026-04-03` 已新增 `RecommendBook / ViewedUsers / MyReservation / BecomeWriter / Message / FeedbackHelp` smoke，并将 smoke catalog drift 收口到 `none`

## 默认下一主线
- 当前 active refactor 主线为 `Stage 7 / Phase 15`
- 默认先推进控制面、机器清单、Figma 审计页与事实对账脚本
- 当前已并行落地 Token 真源和资产治理脚手架，后续继续进入展示基建与页面重皮肤

## 当前已确认的 Stage 7 输入
- `src/utils/runtime/**` 已稳定为 runtime 收口层
- `src/utils/bridge/**` 已稳定为原生桥接包装层
- `componentRegistry.ts` 与全部 `*Component.tsx` 仍有自动化一致性护栏
- 当前 RN smoke 入口至少包括 `SettingsPage` 与 `WritePage`
- 当前 Android smoke 入口至少包括 `Home / Login / Search / Reader`
- `App -> ProfilePage` 仍是默认 App Root，必须纳入 Stage 7 盘点

## Blockers / Known Drift
- Root `README.md` 仍可能滞后于当前 refactor authority
- `android/gradle/libs.versions.toml` 仍缺失
- 当前仓库仍无统一 Crash / ANR / 灰度平台
- Figma frame map 仍待继续回填，视觉稿/标注稿/组件映射尚未形成 Stage 7 closeout 所需证据

## Primary Source Refs
- [docs/refactor/README.md](../refactor/README.md)
- [docs/refactor/stage-7-phase-15-18-plan.md](../refactor/stage-7-phase-15-18-plan.md)
- [docs/refactor/phases/phase-15-visual-audit-and-control-plane.md](../refactor/phases/phase-15-visual-audit-and-control-plane.md)
- [docs/refactor/tracking/phase-15-18-validation-board.md](../refactor/tracking/phase-15-18-validation-board.md)
- [docs/refactor/tracking/decision-log.md](../refactor/tracking/decision-log.md)

## Last Reviewed
- `2026-04-03` by Stage 7 scrollbox rollout
