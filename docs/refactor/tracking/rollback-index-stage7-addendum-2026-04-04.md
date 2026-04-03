# Rollback Index Addendum - Stage 7 Closeout Continuation

## Purpose
- This addendum records Stage 7 rollback entries created after the main `rollback-index.md` stopped receiving clean updates.
- Until the main index is normalized, treat this file as the authoritative continuation for late Stage 7 work.

## Stage 7 Late-Phase Entries
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-STAGE7-WAVE2-RECOMMENDBOOK-20260403-01` | `84ea62b` | 收口 `RecommendBookPage` 组件文案与回归覆盖 | `git revert --no-edit 84ea62b` | `npm test -- --runInBand __tests__/design-system/RecommendBookPageStyles.novelDesign.test.ts __tests__/design-system/RecommendBookComponents.novelDesign.test.tsx` |
| `RB-STAGE7-WAVE2-VIEWEDUSERS-20260403-01` | `4060923` | 收口 `ViewedUsersPage` 组件文案与可读 mock 数据 | `git revert --no-edit 4060923` | `npm test -- --runInBand __tests__/design-system/ViewedUsersComponents.novelDesign.test.tsx` |
| `RB-STAGE7-WAVE2-MYRESERVATION-20260403-01` | `53f3390` | 收口 `MyReservationPage` 组件文案与可读 mock 数据 | `git revert --no-edit 53f3390` | `npm test -- --runInBand __tests__/design-system/MyReservationComponents.novelDesign.test.tsx` |
| `RB-STAGE7-WAVE2-MESSAGE-20260403-01` | `a31f9ca` | 收口 `MessagePage` 消息数据文案与空态基线 | `git revert --no-edit a31f9ca` | `npm test -- --runInBand __tests__/design-system/MessageComponents.novelDesign.test.tsx __tests__/domains/messagePageModel.test.ts` |
| `RB-STAGE7-DOCS-ROLLING-20260403-01` | `a80c7a9` | 同步 Stage 7 滚动换肤与验证看板进度 | `git revert --no-edit a80c7a9` | `npm run harness:check` |
| `RB-STAGE7-AI-HEADER-20260403-01` | `a29fbf8` | 修正 `AIWriteAssistant` 页头标题文案 | `git revert --no-edit a29fbf8` | `npm test -- --runInBand __tests__/design-system/AIWriteAssistantHeader.novelDesign.test.tsx __tests__/smoke/AIWriteAssistant.smoke.test.tsx` |
| `RB-STAGE7-BOOKSHELF-RECO-TITLE-20260403-01` | `6ad8fe4` | 修正书架推荐区标题文案 | `git revert --no-edit 6ad8fe4` | `npm test -- --runInBand __tests__/design-system/BookshelfUnifiedScrollView.novelDesign.test.tsx` |
| `RB-STAGE7-WRITEPAGE-CONTRACT-20260403-01` | `5ac23ee` | 收口 `WritePage` 文案与 bridge / harness 契约测试 | `git revert --no-edit 5ac23ee` | `npm test -- --runInBand __tests__/bridge/NativeBridgeEventContracts.test.ts __tests__/smoke/WritePage.smoke.test.tsx __tests__/harness/harnessScripts.test.js` |
| `RB-STAGE7-SHOWCASE-ROUTE-20260403-01` | `394040c` | 接通 Android `novel_design_showcase` route | `git revert --no-edit 394040c` | `npm test -- --runInBand __tests__/harness/androidNovelDesignPages.test.js` |
| `RB-STAGE7-SHOWCASE-DOCS-20260403-01` | `0275953` | 同步展示基建接线进度到控制面 | `git revert --no-edit 0275953` | `npm run harness:check` |
| `RB-STAGE7-GATES-20260403-01` | `ec4a214` | 同步 Stage 7 全量 Jest 与 Android shared gate 进度 | `git revert --no-edit ec4a214` | `npm run harness:check` |
| `RB-STAGE7-SMOKE-EXPANSION-20260403-01` | `c356f52` | 扩展 `RecommendBook / ViewedUsers / MyReservation` smoke 覆盖 | `git revert --no-edit c356f52` | `npm test -- --runInBand __tests__/smoke/RecommendBookPage.smoke.test.tsx __tests__/smoke/ViewedUsersPage.smoke.test.tsx __tests__/smoke/MyReservationPage.smoke.test.tsx` |
| `RB-STAGE7-SMOKE-EXPANSION-20260404-01` | `7cfc671` | 补齐 `BecomeWriter / Message / FeedbackHelp` smoke 目录与 catalog 收口 | `git revert --no-edit 7cfc671` | `npm test -- --runInBand __tests__/smoke __tests__/harness/novelDesignAuditScripts.test.js` |
| `RB-STAGE7-CLOSEOUT-HOSTS-20260404-01` | `a43cb82` | 建立 Stage 7 closeout assessment / summary / review packet 宿主 | `git revert --no-edit a43cb82` | `npm run harness:check` |
| `RB-STAGE7-TOKEN-ASSET-RULES-20260404-01` | `8e96856` | 补齐 Phase 16 token 规则与 Phase 17 asset ledger 样例 | `git revert --no-edit 8e96856` | `npm test -- --runInBand __tests__/harness/novelDesignAssetsScripts.test.js && npm run novel-design:assets:check` |
| `RB-STAGE7-SHOWCASE-RUNBOOK-20260404-01` | `08ff1fd` | 补齐 showcase runbook 与 V17-03 closeout 说明 | `git revert --no-edit 08ff1fd` | `npm test -- --runInBand __tests__/harness/androidNovelDesignPages.test.js __tests__/web/webEntryConfig.test.ts __tests__/design-system/NovelDesignShowcase.test.tsx` |
| `RB-STAGE7-CLOSEOUT-READY-STATE-20260404-01` | `5787839` | 同步当前 focus 到 Stage 7 closeout prep 状态 | `git revert --no-edit 5787839` | `npm run harness:check` |
| `RB-STAGE7-CLOSEOUT-READINESS-20260404-01` | `1ced690` | 新增 `stage7:closeout` 自动 readiness 报告与检查命令 | `git revert --no-edit 1ced690` | `npm test -- --runInBand __tests__/harness/stage7CloseoutReadiness.test.js && npm run stage7:closeout:check` |

## Usage
- When preparing the final Stage 7 closeout, merge these rows conceptually into the main rollback authority.
- If the main `rollback-index.md` is later normalized to UTF-8 and edited safely, copy these rows into it verbatim.
