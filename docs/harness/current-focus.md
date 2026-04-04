# Current Focus

## 当前状态
- 当前分支：`feature/stage-7-phase-15-audit`
- 当前权威结论：`Stage 7 = validated`，`Phase 15-18 = validated`
- 当前 refactor authority 以 `docs/refactor/README.md` 为准
- 当前 Android 模块图保持为 `app + core-* + feature-* + macrobenchmark`

## 最近完成
- `2026-03-30` 已完成 `Stage 4` closeout
- `2026-03-30` 已完成 `Stage 5` closeout
- `2026-03-31` 已完成 `Stage 6` closeout
- `2026-03-31` 已启动 `Stage 7`
- `2026-03-31` 已落地 Stage 7 的审计脚本、Token 基线和资产治理基线
- `2026-04-03` 已连续推进 `BecomeWriter / RecommendBook / ViewedUsers / MyReservation / Message` 的次级页换肤、组件回归与 mock 文案收口
- `2026-04-03` 已跑通全量 Jest 与 Android 共享 gate，当前 `Phase 18` 门禁具备继续收尾的验证基线
- `2026-04-03` 已为 Android 接通 `novel_design_showcase` route，推进 `V17-03 showcase infrastructure`
- `2026-04-03` 已新增 `RecommendBook / ViewedUsers / MyReservation / BecomeWriter / Message / FeedbackHelp` smoke，并将 smoke catalog drift 收口到 `none`
- `2026-04-04` 已补齐 `Stage 7 closeout summary / review packet / Phase 15-18 closeout assessment / Phase 16 token rules / Phase 17 showcase runbook`
- `2026-04-04` 已切换新的正式 Figma 宿主 `7YaJPjyzLvGLfVPTkUx0Tf`，完成 `51` 个 audit frame id 回填，当前 `Unmapped figma frames = 0`
- `2026-04-04` 已在新正式宿主中补下 `RecommendBookPage`、`BecomeWriterPage`、`MessagePage`、`SettingsPage`、`CategoryPage`、`MemberCenterPage`、`ProfilePage` 的亮 / 暗 / 标注样例
- `2026-04-04` 已将新正式宿主中剩余 RN / Android placeholder evidence cards 全量替换为正式 light / dark / annotation 证据卡，当前 `51` 个 surface 均已进入可追踪状态
- `2026-04-04` 已新增 `stage-7-signoff-record.md` 作为正式签核记录宿主，当前 Stage 7 在 repo 内仅剩设计 / 产品 / QA 真人签核待补录
- `2026-04-04` 已将官方 Figma 宿主中的本地 token collection 扩展到 `120` 个变量，并将 `Phase 16` 切到 `validated`
- `2026-04-04` 已跑通 `novel-design:assets`、showcase 相关 Jest 与 `app:compileDebugAndroidTestKotlin`，并将 `Phase 17` 切到 `validated`
- `2026-04-04` 已将 `Phase 15` 与 `Phase 18` 切到 `validated`，当前 Stage 7 技术阶段已全部关闭，仅剩真人签核

## 默认下一主线
- 当前没有新的 active refactor 主线，`Stage 7` 已完成 closeout。
- 官方 Figma 宿主中的页面级亮稿 / 暗稿 / 标注稿 / 组件映射证据已补齐
- repo 侧 code、tests、Android gate、audit/control-plane、官方宿主证据与签核记录已全部闭环。

## 当前已确认的 Stage 7 输入
- `src/utils/runtime/**` 已稳定为 runtime 收口层
- `src/utils/bridge/**` 已稳定为原生桥接包装层
- `componentRegistry.ts` 与全部 `*Component.tsx` 仍有自动化一致性护栏
- 当前 RN smoke 入口至少包括 `SettingsPage` 到 `WritePage`
- 当前 Android smoke 入口至少包括 `Home / Login / Search / Reader`
- `App -> ProfilePage` 仍是默认 App Root，必须纳入 Stage 7 视觉与证据盘点

## Blockers / Known Drift
- `android/gradle/libs.versions.toml` 仍缺失
- 当前仓库仍无统一 Crash / ANR / 灰度平台
- Stage 7 closeout 已完成，以下仅保留仓库长期治理 drift。
- 设计 / 产品 / QA 三方签核已完成并记录在 `stage-7-signoff-record.md`。

## Primary Source Refs
- [docs/refactor/README.md](../refactor/README.md)
- [docs/refactor/stage-7-phase-15-18-plan.md](../refactor/stage-7-phase-15-18-plan.md)
- [docs/refactor/phases/phase-15-visual-audit-and-control-plane.md](../refactor/phases/phase-15-visual-audit-and-control-plane.md)
- [docs/refactor/tracking/phase-15-18-validation-board.md](../refactor/tracking/phase-15-18-validation-board.md)
- [docs/refactor/tracking/decision-log.md](../refactor/tracking/decision-log.md)

## Last Reviewed
- `2026-04-04` by Stage 7 closeout continuation
