# Rollback Index

## Stage 6 代码提交
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-P12-CODE-20260331-01` | `5a0b632` | Phase 12 运行时、bridge gateway、event hub �?back navigation 收口 | `git revert --no-edit 5a0b632` | `npm test -- --runInBand __tests__/runtime/backNavigation.test.ts __tests__/runtime/eventHub.test.ts __tests__/runtime/runtimeCoordinator.test.ts __tests__/runtime/rawPrimitivesBoundary.test.ts __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/UserBridge.contract.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` |
| `RB-P13-WAVE1-20260331-01` | `20b02c4` | Phase 13 第一�?Profile / Settings 域收�?| `git revert --no-edit 20b02c4` | `npm test -- --runInBand __tests__/domains/profileBootstrap.test.ts __tests__/domains/settingsPageModel.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` |
| `RB-P13-WAVE2-20260331-01` | `852f175` | Phase 13 第二�?History / Watchlist 域收�?| `git revert --no-edit 852f175` | `npm test -- --runInBand __tests__/domains/bookshelfHistoryPageModel.test.ts __tests__/domains/watchlistPageModel.test.ts __tests__/stores/historyStores.mock-closure.test.ts` |
| `RB-P13-WAVE3-20260331-01` | `8ffa9cd` | Phase 13 第三�?Bookshelf 域收�?| `git revert --no-edit 8ffa9cd` | `npm test -- --runInBand __tests__/domains/bookshelfPageModel.test.ts __tests__/domains/bookshelfHistoryPageModel.test.ts __tests__/domains/watchlistPageModel.test.ts __tests__/stores/historyStores.mock-closure.test.ts` |
| `RB-P13-WAVE4-20260331-01` | `17ac26b` | Phase 13 第四波评论域页面收口 | `git revert --no-edit 17ac26b` | `npm test -- --runInBand __tests__/domains/commentPageModel.test.ts __tests__/domains/reviewDetailPageModel.test.ts __tests__/domains/writeReviewPageModel.test.ts __tests__/stores/commentStore.mock-closure.test.ts` |
| `RB-P13-WAVE5-20260331-01` | `aaf9069` | Phase 13 第五�?Writer 域收�?| `git revert --no-edit aaf9069` | `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts` |
| `RB-P13-WAVE5-20260331-02` | `24d49b9` | Phase 13 第五�?Writer 页面接线深化 | `git revert --no-edit 24d49b9` | `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-P13-WAVE6-20260331-01` | `fbfc7d9` | Phase 13 第六�?Community �?heavy pages 收口 | `git revert --no-edit fbfc7d9` | `npm test -- --runInBand __tests__/domains/communityPageModel.test.ts __tests__/community/communityHandlers.test.ts __tests__/domains/recommendBookPageModel.test.ts __tests__/domains/memberCenterPageModel.test.ts __tests__/domains/becomeWriterPageModel.test.ts` |
| `RB-P13-WRITE-DEEP-20260331-01` | `0eb4639` | WritePage 更深层收�?| `git revert --no-edit 0eb4639` | `npm test -- --runInBand __tests__/domains/writePageModel.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-P13-WAVE7-20260331-01` | `540bde2` | Phase 13 第七波剩余长页收�?| `git revert --no-edit 540bde2` | `npm test -- --runInBand __tests__/domains/messagePageModel.test.ts __tests__/domains/myReservationPageModel.test.ts __tests__/domains/viewedUsersPageModel.test.ts __tests__/domains/scrollboxHistoryPageModel.test.ts __tests__/domains/feedbackHelpPageModel.test.ts` |
| `RB-P14-REGISTRY-20260331-01` | `e846ae5` | Phase 14 registry consistency 护栏 | `git revert --no-edit e846ae5` | `npm test -- --runInBand __tests__/harness/rnComponentRegistryConsistency.test.ts` |
| `RB-STAGE6-CLOSEOUT-20260331-01` | `7d31458` | 关闭 Phase 13 / Phase 14 并完�?Stage 6 收口 | `git revert --no-edit 7d31458` | `npm run harness:check` |

## Stage 6 authority / harness 提交
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-P12-AUTH-20260331-01` | `2b750ac` | Phase 12 closeout、Stage 6 状态切换与 harness 导航同步 | `git revert --no-edit 2b750ac` | `npm run harness:check` |
| `RB-P12-ROLLBACK-20260331-01` | `975c132` | Phase 12 控制面切换的回滚索引补记 | `git revert --no-edit 975c132` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-01` | `4dd9777` | Phase 13 状态切换、validation board �?wave 1 记录同步 | `git revert --no-edit 4dd9777` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-01` | `4018c6f` | Phase 13 启动后的 harness 导航同步 | `git revert --no-edit 4018c6f` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-01` | `3a687fe` | Phase 13 启动的回滚索引补�?| `git revert --no-edit 3a687fe` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-02` | `deb061e` | Phase 13 中段页面域收口进展补�?| `git revert --no-edit deb061e` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-02` | `0f4aaef` | Phase 13 中段收口后的 harness 导航同步 | `git revert --no-edit 0f4aaef` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-02` | `5c0ee65` | Phase 13 中段推进的回滚索引补�?| `git revert --no-edit 5c0ee65` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-03` | `b9af348` | Phase 13 后段 Writer 域收口进展补�?| `git revert --no-edit b9af348` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-03` | `b5b0021` | Phase 13 后段 Writer 收口后的 harness 导航同步 | `git revert --no-edit b5b0021` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-03` | `727c83a` | Phase 13 后段推进的回滚索引补�?| `git revert --no-edit 727c83a` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-04` | `32f17f3` | Phase 13 第五�?Writer 页面接线进展补记 | `git revert --no-edit 32f17f3` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-04` | `8f175ee` | Phase 13 第五波接线的回滚索引补记 | `git revert --no-edit 8f175ee` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-05` | `deae664` | Phase 13 第六�?heavy pages 进展补记 | `git revert --no-edit deae664` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-04` | `2b6b1dc` | Phase 13 第六波后�?harness 导航同步 | `git revert --no-edit 2b6b1dc` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-05` | `31bcb01` | Phase 13 第六波推进的回滚索引补记 | `git revert --no-edit 31bcb01` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-06` | `8de4626` | WritePage 更深层收口进展补�?| `git revert --no-edit 8de4626` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-05` | `889c7ab` | WritePage 更深层收口后�?harness 导航同步 | `git revert --no-edit 889c7ab` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-06` | `cd0f37b` | WritePage 更深层收口的回滚索引补记 | `git revert --no-edit cd0f37b` | `npm run harness:check` |
| `RB-STAGE6-HARNESS-20260331-01` | `b1501e2` | Stage 6 closeout 后的 harness 导航同步 | `git revert --no-edit b1501e2` | `npm run harness:check` |

## Stage 7 authority / harness 提交
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-STAGE7-AUTH-20260331-01` | `534407b` | 启动 Stage 7 控制面与 harness 导航 | `git revert --no-edit 534407b` | `npm run harness:check` |

## Stage 7 code / artifact 提交
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-STAGE7-P15-20260331-01` | `814fe85` | Phase 15 审计脚本、盘点产物与事实对账门禁 | `git revert --no-edit 814fe85` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AuditScripts.test.js && npm run novel-design:audit:check && npm run harness:check` |
| `RB-STAGE7-P16P17-20260331-01` | `868b861` | Phase 16-17 Token 真源、资产治理脚手架与生成物 | `git revert --no-edit 868b861` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7TokenBuild.test.js __tests__/harness/stage7AssetsScripts.test.js && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-CHECKS-20260331-01` | `c7690a6` | 修正 Stage 7 token / asset check 为只读校�?| `git revert --no-edit c7690a6` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7TokenBuild.test.js __tests__/harness/stage7AssetsScripts.test.js && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-ASSET-UI-20260401-01` | `09e6221` | 接入 Stage 7 图标注册表与媒体基础组件 | `git revert --no-edit 09e6221` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AssetsScripts.test.js __tests__/design-system/IconComponent.stage7.test.tsx __tests__/design-system/PlaceholderImage.stage7.test.tsx __tests__/design-system/PexelsCreditOverlay.stage7.test.tsx && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-SHOWCASE-20260401-01` | `e64186d` | 补充 Stage 7 �?RN 展示基建骨架 | `git revert --no-edit e64186d` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/Stage7Showcase.test.tsx __tests__/design-system/IconComponent.stage7.test.tsx __tests__/design-system/PlaceholderImage.stage7.test.tsx __tests__/design-system/PexelsCreditOverlay.stage7.test.tsx && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-ANDROID-SHOWCASE-20260401-01` | `5d5fe2e` | 补上 Stage 7 �?Android 展示基建骨架 | `git revert --no-edit 5d5fe2e` | `cd android && ./gradlew :core-ui:testDebugUnitTest --tests com.novel.ui.showcase.Stage7ShowcaseModelTest --stacktrace --console=plain && npm run harness:check` |
| `RB-STAGE7-WEB-SHOWCASE-20260401-01` | `a16c3c9` | 修�?Stage 7 展示�?web 入口�?shim 链路 | `git revert --no-edit a16c3c9` | `npm test -- --runInBand --runTestsByPath __tests__/web/webEntryConfig.test.ts __tests__/web/webShims.test.ts __tests__/design-system/Stage7Showcase.test.tsx && npx webpack --config webpack.config.js && npm run harness:check` |
| `RB-STAGE7-VISUAL-BRIEFS-20260401-01` | `ed30a96` | 补全 Stage 7 页面与组件的视觉简�?| `git revert --no-edit ed30a96` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AuditScripts.test.js && npm run novel-design:audit:check && npm run harness:check` |
| `RB-STAGE7-FIGMA-SEED-20260401-01` | `90142be` | 同步 Stage 7 �?Figma 逐项卡片落地 | `git revert --no-edit 90142be` | `npm run harness:check` |
| `RB-STAGE7-WAVE1-PROFILE-20260401-01` | `8e02f52` | 启动 Stage 7 首批页面壳层换肤（ProfilePage�?| `git revert --no-edit 8e02f52` | `npm test -- --runInBand --runTestsByPath __tests__/App.test.tsx __tests__/design-system/resolveNovelDesignTheme.test.ts __tests__/design-system/ProfilePageStyles.stage7.test.ts __tests__/design-system/ProfileTopBar.stage7.test.tsx` |
| `RB-STAGE7-AUDIT-ANDROID-20260401-01` | `4de4cd5` | 扩展 Stage 7 组件审计�?Android | `git revert --no-edit 4de4cd5` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AuditScripts.test.js && npm run novel-design:audit:check && npm run harness:check` |
| `RB-STAGE7-WAVE1-SETTINGS-20260401-01` | `89f7327` | 推进 Stage 7 �?SettingsPage 壳层换肤 | `git revert --no-edit 89f7327` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx __tests__/design-system/SettingsPageStyles.stage7.test.ts` |
| `RB-STAGE7-WAVE1-CATEGORY-20260401-01` | `1f46827` | 推进 Stage 7 �?CategoryPage 壳层换肤 | `git revert --no-edit 1f46827` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CategoryPageStyles.stage7.test.ts` |
| `RB-STAGE7-WAVE1-BOOKSHELF-20260401-01` | `018cea7` | 推进 Stage 7 �?BookshelfPage 壳层换肤 | `git revert --no-edit 018cea7` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfMainPageStyles.stage7.test.ts __tests__/smoke/BookshelfPage.smoke.test.tsx` |
| `RB-STAGE7-WAVE1-MEMBERCENTER-20260401-01` | `2b2d2d7` | 推进 Stage 7 �?MemberCenterPage 壳层换肤 | `git revert --no-edit 2b2d2d7` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/MemberCenterPageStyles.stage7.test.ts __tests__/smoke/MemberCenterPage.smoke.test.tsx` |
| `RB-STAGE7-WAVE1-WRITEPAGE-20260401-01` | `6ef5171` | 推进 Stage 7 �?WritePage 壳层换肤 | `git revert --no-edit 6ef5171` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/WritePageStyles.novelDesign.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-STAGE7-NOVELDESIGN-20260401-01` | `402ead4` | 将代码层 Stage 7 命名统一迁移�?novelDesign | `git revert --no-edit 402ead4` | `npm test -- --runInBand --runTestsByPath __tests__/harness/novelDesignNaming.test.js __tests__/harness/novelDesignAuditScripts.test.js __tests__/harness/novelDesignTokenBuild.test.js __tests__/harness/novelDesignAssetsScripts.test.js && npm run novel-design:audit:check && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check && npx webpack --config webpack.config.js` |
| `RB-STAGE7-NOVELDESIGN-20260401-02` | `7e7b66f` | 清理 Stage 7 遗留测试命名并同步快�?| `git revert --no-edit 7e7b66f` | `npm test -- --runInBand --runTestsByPath __tests__/harness/novelDesignNaming.test.js __tests__/harness/novelDesignAuditScripts.test.js __tests__/harness/novelDesignTokenBuild.test.js __tests__/harness/novelDesignAssetsScripts.test.js && npm run harness:check` |

| `RB-STAGE7-WRITER-NOVELDESIGN-20260401-01` | `b8aeaea` | ͳһ novelDesign ȫ�����ò��ƽ� WritePage / AIWriteAssistant / BookManagePage д���߻��� | `git revert --no-edit b8aeaea` | `npm test -- --runInBand --runTestsByPath __tests__/harness/novelDesignAuditScripts.test.js __tests__/harness/novelDesignNaming.test.js __tests__/harness/novelDesignTokenBuild.test.js __tests__/harness/novelDesignAssetsScripts.test.js __tests__/design-system/NovelDesignUI.test.ts __tests__/design-system/WritePageStyles.novelDesign.test.ts __tests__/design-system/AIWriteAssistantStyles.novelDesign.test.ts __tests__/design-system/BookManageStyles.novelDesign.test.ts __tests__/smoke/WritePage.smoke.test.tsx __tests__/smoke/AIWriteAssistant.smoke.test.tsx __tests__/smoke/BookManagePage.smoke.test.tsx && npm run novel-design:audit:check && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check && npx webpack --config webpack.config.js` |

| `RB-STAGE7-NOVELDESIGN-ENTRY-20260401-01` | `ee556a0` | �������ѻ���ҳ��ͳһ���� NovelDesignUI | `git revert --no-edit ee556a0` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/ProfilePageStyles.novelDesign.test.ts __tests__/design-system/SettingsPageStyles.novelDesign.test.ts __tests__/design-system/CategoryPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfMainPageStyles.novelDesign.test.ts __tests__/design-system/MemberCenterPageStyles.novelDesign.test.ts __tests__/design-system/CommentPageStyles.novelDesign.test.ts __tests__/design-system/ReviewDetailPageStyles.novelDesign.test.ts __tests__/design-system/WriteReviewPageStyles.novelDesign.test.ts __tests__/design-system/WritePageStyles.novelDesign.test.ts __tests__/design-system/NovelDesignUI.test.ts` |

| `RB-STAGE7-BOOKSHELF-NESTED-20260401-01` | `2050e4a` | �ƽ���� History / Watchlist ��Ƕҳ�� novelDesign ���� | `git revert --no-edit 2050e4a` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/HistoryPageStyles.novelDesign.test.ts __tests__/design-system/WatchlistPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-BOOKSHELF-CORE-20260401-01` | `184a3ce` | �ƽ���� Bookshelf / Community ��Ƕҳ�� novelDesign ���� | `git revert --no-edit 184a3ce` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-BOOKSHELF-COMPONENTS-20260402-01` | `b777920` | �ƽ���������� novelDesign ͼ�������̬�տ� | `git revert --no-edit b777920` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityComponents.novelDesign.test.tsx __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-COMMUNITY-STYLE-20260402-01` | `dee099c` | ���� Community ��ʽ�ں˵� novelDesign �տ� | `git revert --no-edit dee099c` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/CommunityComponents.novelDesign.test.tsx __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-BOOKSHELF-SHELL-20260402-01` | `0f71c4f` | �ƽ� Bookshelf ��̬������ǲ�� novelDesign �տ� | `git revert --no-edit 0f71c4f` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-COMMUNITY-FEED-20260402-01` | `70d789d` | �ƽ� Community Feed �� Bookshelf �������� novelDesign �տ� | `git revert --no-edit 70d789d` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/CommunityFeedComponents.novelDesign.test.tsx __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-SHELL-GUARDS-20260402-01` | `758765d` | ϸ�������������ʽ�˵� novelDesign ���� | `git revert --no-edit 758765d` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfEditToolbar.novelDesign.test.tsx __tests__/design-system/CommunityPostList.novelDesign.test.tsx` |

| `RB-STAGE7-TEXT-TOKENS-20260402-01` | `8b57e55` | ��������������ı���� novelDesign ����ɫ | `git revert --no-edit 8b57e55` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/CommunityPageStyles.novelDesign.test.ts` |

| `RB-STAGE7-STYLE-CORE-20260402-01` | `ff18b71` | ����ѹƽ�����������ʽ�˵ľ�����ɫֵ | `git revert --no-edit ff18b71` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/design-system/CommunityComponents.novelDesign.test.tsx __tests__/design-system/BookshelfEditToolbar.novelDesign.test.tsx __tests__/design-system/CommunityPostList.novelDesign.test.tsx __tests__/design-system/CommunityFeedComponents.novelDesign.test.tsx` |

## 说明
- 当前 closeout �?rollback authority 以本页为准�?