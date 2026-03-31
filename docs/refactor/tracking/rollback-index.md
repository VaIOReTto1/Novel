# Rollback Index

## Stage 6 代码提交
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-P12-CODE-20260331-01` | `5a0b632` | Phase 12 运行时、bridge gateway、event hub、back navigation 与页面直连入口收口 | `git revert --no-edit 5a0b632` | `npm test -- --runInBand __tests__/runtime/backNavigation.test.ts __tests__/runtime/eventHub.test.ts __tests__/runtime/runtimeCoordinator.test.ts __tests__/runtime/rawPrimitivesBoundary.test.ts __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/UserBridge.contract.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` |
| `RB-P13-WAVE1-20260331-01` | `20b02c4` | Phase 13 首波 Profile / Settings 域收口 | `git revert --no-edit 20b02c4` | `npm test -- --runInBand __tests__/domains/profileBootstrap.test.ts __tests__/domains/settingsPageModel.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` |
| `RB-P13-WAVE2-20260331-01` | `852f175` | Phase 13 第二波 History / Watchlist 域收口 | `git revert --no-edit 852f175` | `npm test -- --runInBand __tests__/domains/bookshelfHistoryPageModel.test.ts __tests__/domains/watchlistPageModel.test.ts __tests__/stores/historyStores.mock-closure.test.ts` |
| `RB-P13-WAVE3-20260331-01` | `8ffa9cd` | Phase 13 第三波 Bookshelf 域收口 | `git revert --no-edit 8ffa9cd` | `npm test -- --runInBand __tests__/domains/bookshelfPageModel.test.ts __tests__/domains/bookshelfHistoryPageModel.test.ts __tests__/domains/watchlistPageModel.test.ts __tests__/stores/historyStores.mock-closure.test.ts` |
| `RB-P13-WAVE4-20260331-01` | `17ac26b` | Phase 13 第四波评论域页面收口 | `git revert --no-edit 17ac26b` | `npm test -- --runInBand __tests__/domains/commentPageModel.test.ts __tests__/domains/reviewDetailPageModel.test.ts __tests__/domains/writeReviewPageModel.test.ts __tests__/stores/commentStore.mock-closure.test.ts` |
| `RB-P13-WAVE5-20260331-01` | `aaf9069` | Phase 13 第五波 Writer 域收口 | `git revert --no-edit aaf9069` | `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts` |
| `RB-P13-WAVE5-20260331-02` | `24d49b9` | Phase 13 第五波 Writer 页面接线深化 | `git revert --no-edit 24d49b9` | `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-P13-WAVE6-20260331-01` | `fbfc7d9` | Phase 13 第六波 Community 与 heavy pages 收口 | `git revert --no-edit fbfc7d9` | `npm test -- --runInBand __tests__/domains/communityPageModel.test.ts __tests__/community/communityHandlers.test.ts __tests__/domains/recommendBookPageModel.test.ts __tests__/domains/memberCenterPageModel.test.ts __tests__/domains/becomeWriterPageModel.test.ts` |
| `RB-P13-WRITE-DEEP-20260331-01` | `0eb4639` | WritePage 更深层收口 | `git revert --no-edit 0eb4639` | `npm test -- --runInBand __tests__/domains/writePageModel.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-P13-WAVE7-20260331-01` | `540bde2` | Phase 13 第七波剩余长页收口 | `git revert --no-edit 540bde2` | `npm test -- --runInBand __tests__/domains/messagePageModel.test.ts __tests__/domains/myReservationPageModel.test.ts __tests__/domains/viewedUsersPageModel.test.ts __tests__/domains/scrollboxHistoryPageModel.test.ts __tests__/domains/feedbackHelpPageModel.test.ts` |
| `RB-P14-REGISTRY-20260331-01` | `e846ae5` | Phase 14 registry consistency 护栏 | `git revert --no-edit e846ae5` | `npm test -- --runInBand __tests__/harness/rnComponentRegistryConsistency.test.ts` |

## Stage 6 authority / harness 提交
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-P12-AUTH-20260331-01` | `2b750ac` | Phase 12 closeout、Stage 6 状态切换与 harness 导航同步 | `git revert --no-edit 2b750ac` | `npm run harness:check` |
| `RB-P12-ROLLBACK-20260331-01` | `975c132` | Phase 12 控制面切换的回滚索引补记 | `git revert --no-edit 975c132` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-01` | `4dd9777` | Phase 13 状态切换、validation board 与 wave 1 记录同步 | `git revert --no-edit 4dd9777` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-01` | `4018c6f` | Phase 13 启动后的 harness 导航同步 | `git revert --no-edit 4018c6f` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-01` | `3a687fe` | Phase 13 启动的回滚索引补记 | `git revert --no-edit 3a687fe` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-02` | `deb061e` | Phase 13 中段页面域收口进展补记 | `git revert --no-edit deb061e` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-02` | `0f4aaef` | Phase 13 中段收口后的 harness 导航同步 | `git revert --no-edit 0f4aaef` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-02` | `5c0ee65` | Phase 13 中段推进的回滚索引补记 | `git revert --no-edit 5c0ee65` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-03` | `b9af348` | Phase 13 后段 Writer 域收口进展补记 | `git revert --no-edit b9af348` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-03` | `b5b0021` | Phase 13 后段 Writer 收口后的 harness 导航同步 | `git revert --no-edit b5b0021` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-03` | `727c83a` | Phase 13 后段推进的回滚索引补记 | `git revert --no-edit 727c83a` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-04` | `32f17f3` | Phase 13 第五波 Writer 页面接线进展补记 | `git revert --no-edit 32f17f3` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-04` | `8f175ee` | Phase 13 第五波接线的回滚索引补记 | `git revert --no-edit 8f175ee` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-05` | `deae664` | Phase 13 第六波 heavy pages 进展补记 | `git revert --no-edit deae664` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-04` | `2b6b1dc` | Phase 13 第六波后的 harness 导航同步 | `git revert --no-edit 2b6b1dc` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-05` | `31bcb01` | Phase 13 第六波推进的回滚索引补记 | `git revert --no-edit 31bcb01` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-06` | `8de4626` | WritePage 更深层收口进展补记 | `git revert --no-edit 8de4626` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-05` | `889c7ab` | WritePage 更深层收口后的 harness 导航同步 | `git revert --no-edit 889c7ab` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-06` | `cd0f37b` | WritePage 更深层收口的回滚索引补记 | `git revert --no-edit cd0f37b` | `npm run harness:check` |

## 说明
- 当前 closeout 与 rollback authority 以本页为准。
