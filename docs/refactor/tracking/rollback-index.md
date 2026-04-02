# Rollback Index

## Stage 6 浠ｇ爜鎻愪氦
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-P12-CODE-20260331-01` | `5a0b632` | Phase 12 杩愯鏃躲�乥ridge gateway銆乪vent hub 涓?back navigation 鏀跺彛 | `git revert --no-edit 5a0b632` | `npm test -- --runInBand __tests__/runtime/backNavigation.test.ts __tests__/runtime/eventHub.test.ts __tests__/runtime/runtimeCoordinator.test.ts __tests__/runtime/rawPrimitivesBoundary.test.ts __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/UserBridge.contract.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` |
| `RB-P13-WAVE1-20260331-01` | `20b02c4` | Phase 13 绗竴娉?Profile / Settings 鍩熸敹鍙?| `git revert --no-edit 20b02c4` | `npm test -- --runInBand __tests__/domains/profileBootstrap.test.ts __tests__/domains/settingsPageModel.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` |
| `RB-P13-WAVE2-20260331-01` | `852f175` | Phase 13 绗簩娉?History / Watchlist 鍩熸敹鍙?| `git revert --no-edit 852f175` | `npm test -- --runInBand __tests__/domains/bookshelfHistoryPageModel.test.ts __tests__/domains/watchlistPageModel.test.ts __tests__/stores/historyStores.mock-closure.test.ts` |
| `RB-P13-WAVE3-20260331-01` | `8ffa9cd` | Phase 13 绗笁娉?Bookshelf 鍩熸敹鍙?| `git revert --no-edit 8ffa9cd` | `npm test -- --runInBand __tests__/domains/bookshelfPageModel.test.ts __tests__/domains/bookshelfHistoryPageModel.test.ts __tests__/domains/watchlistPageModel.test.ts __tests__/stores/historyStores.mock-closure.test.ts` |
| `RB-P13-WAVE4-20260331-01` | `17ac26b` | Phase 13 绗洓娉㈣瘎璁哄煙椤甸潰鏀跺彛 | `git revert --no-edit 17ac26b` | `npm test -- --runInBand __tests__/domains/commentPageModel.test.ts __tests__/domains/reviewDetailPageModel.test.ts __tests__/domains/writeReviewPageModel.test.ts __tests__/stores/commentStore.mock-closure.test.ts` |
| `RB-P13-WAVE5-20260331-01` | `aaf9069` | Phase 13 绗簲娉?Writer 鍩熸敹鍙?| `git revert --no-edit aaf9069` | `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts` |
| `RB-P13-WAVE5-20260331-02` | `24d49b9` | Phase 13 绗簲娉?Writer 椤甸潰鎺ョ嚎娣卞寲 | `git revert --no-edit 24d49b9` | `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-P13-WAVE6-20260331-01` | `fbfc7d9` | Phase 13 绗叚娉?Community 涓?heavy pages 鏀跺彛 | `git revert --no-edit fbfc7d9` | `npm test -- --runInBand __tests__/domains/communityPageModel.test.ts __tests__/community/communityHandlers.test.ts __tests__/domains/recommendBookPageModel.test.ts __tests__/domains/memberCenterPageModel.test.ts __tests__/domains/becomeWriterPageModel.test.ts` |
| `RB-P13-WRITE-DEEP-20260331-01` | `0eb4639` | WritePage 鏇存繁灞傛敹鍙?| `git revert --no-edit 0eb4639` | `npm test -- --runInBand __tests__/domains/writePageModel.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-P13-WAVE7-20260331-01` | `540bde2` | Phase 13 绗竷娉㈠墿浣欓暱椤垫敹鍙?| `git revert --no-edit 540bde2` | `npm test -- --runInBand __tests__/domains/messagePageModel.test.ts __tests__/domains/myReservationPageModel.test.ts __tests__/domains/viewedUsersPageModel.test.ts __tests__/domains/scrollboxHistoryPageModel.test.ts __tests__/domains/feedbackHelpPageModel.test.ts` |
| `RB-P14-REGISTRY-20260331-01` | `e846ae5` | Phase 14 registry consistency 鎶ゆ爮 | `git revert --no-edit e846ae5` | `npm test -- --runInBand __tests__/harness/rnComponentRegistryConsistency.test.ts` |
| `RB-STAGE6-CLOSEOUT-20260331-01` | `7d31458` | 鍏抽棴 Phase 13 / Phase 14 骞跺畬鎴?Stage 6 鏀跺彛 | `git revert --no-edit 7d31458` | `npm run harness:check` |

## Stage 6 authority / harness 鎻愪氦
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-P12-AUTH-20260331-01` | `2b750ac` | Phase 12 closeout銆丼tage 6 鐘舵�佸垏鎹笌 harness 瀵艰埅鍚屾 | `git revert --no-edit 2b750ac` | `npm run harness:check` |
| `RB-P12-ROLLBACK-20260331-01` | `975c132` | Phase 12 鎺у埗闈㈠垏鎹㈢殑鍥炴粴绱㈠紩琛ヨ | `git revert --no-edit 975c132` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-01` | `4dd9777` | Phase 13 鐘舵�佸垏鎹€�乿alidation board 涓?wave 1 璁板綍鍚屾 | `git revert --no-edit 4dd9777` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-01` | `4018c6f` | Phase 13 鍚姩鍚庣殑 harness 瀵艰埅鍚屾 | `git revert --no-edit 4018c6f` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-01` | `3a687fe` | Phase 13 鍚姩鐨勫洖婊氱储寮曡ˉ璁?| `git revert --no-edit 3a687fe` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-02` | `deb061e` | Phase 13 涓椤甸潰鍩熸敹鍙ｈ繘灞曡ˉ璁?| `git revert --no-edit deb061e` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-02` | `0f4aaef` | Phase 13 涓鏀跺彛鍚庣殑 harness 瀵艰埅鍚屾 | `git revert --no-edit 0f4aaef` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-02` | `5c0ee65` | Phase 13 涓鎺ㄨ繘鐨勫洖婊氱储寮曡ˉ璁?| `git revert --no-edit 5c0ee65` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-03` | `b9af348` | Phase 13 鍚庢 Writer 鍩熸敹鍙ｈ繘灞曡ˉ璁?| `git revert --no-edit b9af348` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-03` | `b5b0021` | Phase 13 鍚庢 Writer 鏀跺彛鍚庣殑 harness 瀵艰埅鍚屾 | `git revert --no-edit b5b0021` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-03` | `727c83a` | Phase 13 鍚庢鎺ㄨ繘鐨勫洖婊氱储寮曡ˉ璁?| `git revert --no-edit 727c83a` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-04` | `32f17f3` | Phase 13 绗簲娉?Writer 椤甸潰鎺ョ嚎杩涘睍琛ヨ | `git revert --no-edit 32f17f3` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-04` | `8f175ee` | Phase 13 绗簲娉㈡帴绾跨殑鍥炴粴绱㈠紩琛ヨ | `git revert --no-edit 8f175ee` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-05` | `deae664` | Phase 13 绗叚娉?heavy pages 杩涘睍琛ヨ | `git revert --no-edit deae664` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-04` | `2b6b1dc` | Phase 13 绗叚娉㈠悗鐨?harness 瀵艰埅鍚屾 | `git revert --no-edit 2b6b1dc` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-05` | `31bcb01` | Phase 13 绗叚娉㈡帹杩涚殑鍥炴粴绱㈠紩琛ヨ | `git revert --no-edit 31bcb01` | `npm run harness:check` |
| `RB-P13-AUTH-20260331-06` | `8de4626` | WritePage 鏇存繁灞傛敹鍙ｈ繘灞曡ˉ璁?| `git revert --no-edit 8de4626` | `npm run harness:check` |
| `RB-P13-HARNESS-20260331-05` | `889c7ab` | WritePage 鏇存繁灞傛敹鍙ｅ悗鐨?harness 瀵艰埅鍚屾 | `git revert --no-edit 889c7ab` | `npm run harness:check` |
| `RB-P13-ROLLBACK-20260331-06` | `cd0f37b` | WritePage 鏇存繁灞傛敹鍙ｇ殑鍥炴粴绱㈠紩琛ヨ | `git revert --no-edit cd0f37b` | `npm run harness:check` |
| `RB-STAGE6-HARNESS-20260331-01` | `b1501e2` | Stage 6 closeout 鍚庣殑 harness 瀵艰埅鍚屾 | `git revert --no-edit b1501e2` | `npm run harness:check` |

## Stage 7 authority / harness 鎻愪氦
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-STAGE7-AUTH-20260331-01` | `534407b` | 鍚姩 Stage 7 鎺у埗闈笌 harness 瀵艰埅 | `git revert --no-edit 534407b` | `npm run harness:check` |

## Stage 7 code / artifact 鎻愪氦
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-STAGE7-P15-20260331-01` | `814fe85` | Phase 15 瀹¤鑴氭湰銆佺洏鐐逛骇鐗╀笌浜嬪疄瀵硅处闂ㄧ | `git revert --no-edit 814fe85` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AuditScripts.test.js && npm run novel-design:audit:check && npm run harness:check` |
| `RB-STAGE7-P16P17-20260331-01` | `868b861` | Phase 16-17 Token 鐪熸簮銆佽祫浜ф不鐞嗚剼鎵嬫灦涓庣敓鎴愮墿 | `git revert --no-edit 868b861` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7TokenBuild.test.js __tests__/harness/stage7AssetsScripts.test.js && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-CHECKS-20260331-01` | `c7690a6` | 淇 Stage 7 token / asset check 涓哄彧璇绘牎楠?| `git revert --no-edit c7690a6` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7TokenBuild.test.js __tests__/harness/stage7AssetsScripts.test.js && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-ASSET-UI-20260401-01` | `09e6221` | 鎺ュ叆 Stage 7 鍥炬爣娉ㄥ唽琛ㄤ笌濯掍綋鍩虹缁勪欢 | `git revert --no-edit 09e6221` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AssetsScripts.test.js __tests__/design-system/IconComponent.stage7.test.tsx __tests__/design-system/PlaceholderImage.stage7.test.tsx __tests__/design-system/PexelsCreditOverlay.stage7.test.tsx && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-SHOWCASE-20260401-01` | `e64186d` | 琛ュ厖 Stage 7 鐨?RN 灞曠ず鍩哄缓楠ㄦ灦 | `git revert --no-edit e64186d` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/Stage7Showcase.test.tsx __tests__/design-system/IconComponent.stage7.test.tsx __tests__/design-system/PlaceholderImage.stage7.test.tsx __tests__/design-system/PexelsCreditOverlay.stage7.test.tsx && npm run novel-design:assets:check && npm run harness:check` |
| `RB-STAGE7-ANDROID-SHOWCASE-20260401-01` | `5d5fe2e` | 琛ヤ笂 Stage 7 鐨?Android 灞曠ず鍩哄缓楠ㄦ灦 | `git revert --no-edit 5d5fe2e` | `cd android && ./gradlew :core-ui:testDebugUnitTest --tests com.novel.ui.showcase.Stage7ShowcaseModelTest --stacktrace --console=plain && npm run harness:check` |
| `RB-STAGE7-WEB-SHOWCASE-20260401-01` | `a16c3c9` | 淇�?Stage 7 灞曠ず鐨?web 鍏ュ彛涓?shim 閾捐矾 | `git revert --no-edit a16c3c9` | `npm test -- --runInBand --runTestsByPath __tests__/web/webEntryConfig.test.ts __tests__/web/webShims.test.ts __tests__/design-system/Stage7Showcase.test.tsx && npx webpack --config webpack.config.js && npm run harness:check` |
| `RB-STAGE7-VISUAL-BRIEFS-20260401-01` | `ed30a96` | 琛ュ叏 Stage 7 椤甸潰涓庣粍浠剁殑瑙嗚绠�鎶?| `git revert --no-edit ed30a96` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AuditScripts.test.js && npm run novel-design:audit:check && npm run harness:check` |
| `RB-STAGE7-FIGMA-SEED-20260401-01` | `90142be` | 鍚屾 Stage 7 鐨?Figma 閫愰」鍗＄墖钀藉湴 | `git revert --no-edit 90142be` | `npm run harness:check` |
| `RB-STAGE7-WAVE1-PROFILE-20260401-01` | `8e02f52` | 鍚姩 Stage 7 棣栨壒椤甸潰澹冲眰鎹㈣偆锛圥rofilePage锛?| `git revert --no-edit 8e02f52` | `npm test -- --runInBand --runTestsByPath __tests__/App.test.tsx __tests__/design-system/resolveNovelDesignTheme.test.ts __tests__/design-system/ProfilePageStyles.stage7.test.ts __tests__/design-system/ProfileTopBar.stage7.test.tsx` |
| `RB-STAGE7-AUDIT-ANDROID-20260401-01` | `4de4cd5` | 鎵╁睍 Stage 7 缁勪欢瀹¤鍒?Android | `git revert --no-edit 4de4cd5` | `npm test -- --runInBand --runTestsByPath __tests__/harness/stage7AuditScripts.test.js && npm run novel-design:audit:check && npm run harness:check` |
| `RB-STAGE7-WAVE1-SETTINGS-20260401-01` | `89f7327` | 鎺ㄨ繘 Stage 7 鐨?SettingsPage 澹冲眰鎹㈣偆 | `git revert --no-edit 89f7327` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx __tests__/design-system/SettingsPageStyles.stage7.test.ts` |
| `RB-STAGE7-WAVE1-CATEGORY-20260401-01` | `1f46827` | 鎺ㄨ繘 Stage 7 鐨?CategoryPage 澹冲眰鎹㈣偆 | `git revert --no-edit 1f46827` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CategoryPageStyles.stage7.test.ts` |
| `RB-STAGE7-WAVE1-BOOKSHELF-20260401-01` | `018cea7` | 鎺ㄨ繘 Stage 7 鐨?BookshelfPage 澹冲眰鎹㈣偆 | `git revert --no-edit 018cea7` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfMainPageStyles.stage7.test.ts __tests__/smoke/BookshelfPage.smoke.test.tsx` |
| `RB-STAGE7-WAVE1-MEMBERCENTER-20260401-01` | `2b2d2d7` | 鎺ㄨ繘 Stage 7 鐨?MemberCenterPage 澹冲眰鎹㈣偆 | `git revert --no-edit 2b2d2d7` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/MemberCenterPageStyles.stage7.test.ts __tests__/smoke/MemberCenterPage.smoke.test.tsx` |
| `RB-STAGE7-WAVE1-WRITEPAGE-20260401-01` | `6ef5171` | 鎺ㄨ繘 Stage 7 鐨?WritePage 澹冲眰鎹㈣偆 | `git revert --no-edit 6ef5171` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/WritePageStyles.novelDesign.test.ts __tests__/smoke/WritePage.smoke.test.tsx` |
| `RB-STAGE7-NOVELDESIGN-20260401-01` | `402ead4` | 灏嗕唬鐮佸眰 Stage 7 鍛藉悕缁熶竴杩佺Щ涓?novelDesign | `git revert --no-edit 402ead4` | `npm test -- --runInBand --runTestsByPath __tests__/harness/novelDesignNaming.test.js __tests__/harness/novelDesignAuditScripts.test.js __tests__/harness/novelDesignTokenBuild.test.js __tests__/harness/novelDesignAssetsScripts.test.js && npm run novel-design:audit:check && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check && npx webpack --config webpack.config.js` |
| `RB-STAGE7-NOVELDESIGN-20260401-02` | `7e7b66f` | 娓呯悊 Stage 7 閬楃暀娴嬭瘯鍛藉悕骞跺悓姝ュ揩鐓?| `git revert --no-edit 7e7b66f` | `npm test -- --runInBand --runTestsByPath __tests__/harness/novelDesignNaming.test.js __tests__/harness/novelDesignAuditScripts.test.js __tests__/harness/novelDesignTokenBuild.test.js __tests__/harness/novelDesignAssetsScripts.test.js && npm run harness:check` |

| `RB-STAGE7-WRITER-NOVELDESIGN-20260401-01` | `b8aeaea` | 统一 novelDesign 全局配置并推进 WritePage / AIWriteAssistant / BookManagePage 写作线换肤 | `git revert --no-edit b8aeaea` | `npm test -- --runInBand --runTestsByPath __tests__/harness/novelDesignAuditScripts.test.js __tests__/harness/novelDesignNaming.test.js __tests__/harness/novelDesignTokenBuild.test.js __tests__/harness/novelDesignAssetsScripts.test.js __tests__/design-system/NovelDesignUI.test.ts __tests__/design-system/WritePageStyles.novelDesign.test.ts __tests__/design-system/AIWriteAssistantStyles.novelDesign.test.ts __tests__/design-system/BookManageStyles.novelDesign.test.ts __tests__/smoke/WritePage.smoke.test.tsx __tests__/smoke/AIWriteAssistant.smoke.test.tsx __tests__/smoke/BookManagePage.smoke.test.tsx && npm run novel-design:audit:check && npm run novel-design:tokens:check && npm run novel-design:assets:check && npm run harness:check && npx webpack --config webpack.config.js` |

| `RB-STAGE7-NOVELDESIGN-ENTRY-20260401-01` | `ee556a0` | 将首批已换肤页面统一接入 NovelDesignUI | `git revert --no-edit ee556a0` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/ProfilePageStyles.novelDesign.test.ts __tests__/design-system/SettingsPageStyles.novelDesign.test.ts __tests__/design-system/CategoryPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfMainPageStyles.novelDesign.test.ts __tests__/design-system/MemberCenterPageStyles.novelDesign.test.ts __tests__/design-system/CommentPageStyles.novelDesign.test.ts __tests__/design-system/ReviewDetailPageStyles.novelDesign.test.ts __tests__/design-system/WriteReviewPageStyles.novelDesign.test.ts __tests__/design-system/WritePageStyles.novelDesign.test.ts __tests__/design-system/NovelDesignUI.test.ts` |

| `RB-STAGE7-BOOKSHELF-NESTED-20260401-01` | `2050e4a` | 推进书架 History / Watchlist 内嵌页的 novelDesign 换肤 | `git revert --no-edit 2050e4a` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/HistoryPageStyles.novelDesign.test.ts __tests__/design-system/WatchlistPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-BOOKSHELF-CORE-20260401-01` | `184a3ce` | 推进书架 Bookshelf / Community 内嵌页的 novelDesign 换肤 | `git revert --no-edit 184a3ce` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-BOOKSHELF-COMPONENTS-20260402-01` | `b777920` | 推进书架组件层的 novelDesign 图标与加载态收口 | `git revert --no-edit b777920` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityComponents.novelDesign.test.tsx __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-COMMUNITY-STYLE-20260402-01` | `dee099c` | 补齐 Community 样式内核的 novelDesign 收口 | `git revert --no-edit dee099c` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/CommunityComponents.novelDesign.test.tsx __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-BOOKSHELF-SHELL-20260402-01` | `0f71c4f` | 推进 Bookshelf 空态与滚动壳层的 novelDesign 收口 | `git revert --no-edit 0f71c4f` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-COMMUNITY-FEED-20260402-01` | `70d789d` | 推进 Community Feed 与 Bookshelf 工具条的 novelDesign 收口 | `git revert --no-edit 70d789d` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/CommunityFeedComponents.novelDesign.test.tsx __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-SHELL-GUARDS-20260402-01` | `758765d` | 细化书架与社区样式核的 novelDesign 护栏 | `git revert --no-edit 758765d` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfEditToolbar.novelDesign.test.tsx __tests__/design-system/CommunityPostList.novelDesign.test.tsx` |

| `RB-STAGE7-TEXT-TOKENS-20260402-01` | `8b57e55` | 补齐书架与社区文本层的 novelDesign 语义色 | `git revert --no-edit 8b57e55` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/CommunityPageStyles.novelDesign.test.ts` |

| `RB-STAGE7-STYLE-CORE-20260402-01` | `ff18b71` | 继续压平书架与社区样式核的旧主题色值 | `git revert --no-edit ff18b71` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/CommunityPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/design-system/CommunityComponents.novelDesign.test.tsx __tests__/design-system/BookshelfEditToolbar.novelDesign.test.tsx __tests__/design-system/CommunityPostList.novelDesign.test.tsx __tests__/design-system/CommunityFeedComponents.novelDesign.test.tsx` |

| `RB-STAGE7-BOOKSHELF-STYLE-20260402-01` | `e163300` | 清空 Bookshelf 样式核的旧主题直连 | `git revert --no-edit e163300` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BookshelfNestedPageStyles.novelDesign.test.ts __tests__/design-system/BookshelfComponents.novelDesign.test.tsx __tests__/design-system/BookshelfEditToolbar.novelDesign.test.tsx` |

| `RB-STAGE7-HISTORY-MESSAGE-20260402-01` | `2161b88` | 推进 History 与 Message 页面的 novelDesign 换肤 | `git revert --no-edit 2161b88` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/ScrollBoxHistoryPageStyles.novelDesign.test.ts __tests__/design-system/MessagePageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-VIEWED-RESERVATION-20260402-01` | `2592101` | 推进 ViewedUsers 与 MyReservation 页面的 novelDesign 换肤 | `git revert --no-edit 2592101` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/ViewedUsersPageStyles.novelDesign.test.ts __tests__/design-system/MyReservationPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-AI-COPY-20260402-01` | `a4e93fe` | 清理 AI 写作助手操作栏的文案编码 | `git revert --no-edit a4e93fe` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/AIWriteAssistantStyles.novelDesign.test.ts __tests__/smoke/AIWriteAssistant.smoke.test.tsx` |

| `RB-STAGE7-HELP-LEGAL-20260402-01` | `aa49949` | 推进帮助与隐私页面的 novelDesign 换肤 | `git revert --no-edit aa49949` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/FeedbackHelpPageStyles.novelDesign.test.ts __tests__/design-system/HelpSupportPageStyles.novelDesign.test.ts __tests__/design-system/PrivacyPolicyPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-HELP-LINE-20260402-02` | `4ab20a1` | 同步帮助线页面进入 novelDesign 主线 | `git revert --no-edit 4ab20a1` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/FeedbackHelpPageStyles.novelDesign.test.ts __tests__/design-system/HelpSupportPageStyles.novelDesign.test.ts __tests__/design-system/PrivacyPolicyPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-CREATOR-AUX-20260402-01` | `e424a20` | 推进 BecomeWriter 与 RecommendBook 页面的 novelDesign 换肤 | `git revert --no-edit e424a20` | `npm test -- --runInBand --runTestsByPath __tests__/design-system/BecomeWriterPageStyles.novelDesign.test.ts __tests__/design-system/RecommendBookPageStyles.novelDesign.test.ts __tests__/harness/novelDesignAuditScripts.test.js` |

| `RB-STAGE7-ANDROID-PRIMITIVES-20260402-01` | `d901fed` | 收口 Android 共享基元到 NovelDesignTokens | `git revert --no-edit d901fed` | `cd android && .\\gradlew.bat --version` |

| `RB-STAGE7-ANDROID-LOGIN-20260402-01` | `31aa89d` | 推进 Android 登录页接入 NovelDesignTokens | `git revert --no-edit 31aa89d` | `cd android && .\\gradlew.bat --version` |

## 璇存槑
- 褰撳墠 closeout 涓?rollback authority 浠ユ湰椤典负鍑嗐�?