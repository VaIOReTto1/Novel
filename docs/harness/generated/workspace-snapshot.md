<!-- generated, do not edit by hand -->
<!-- source-head: 81ec19bc0d286e15da81bd9d2c994e25837ec18f -->
# Workspace Snapshot

## Git State
- Branch: `main`
- Head: `81ec19b`

## Recent Commits
- 81ec19b Merge pull request #5 from VaIOReTto1/codex-wave2-search-rn-host
- 3883383 补齐福利页多次采样矩阵
- 37d8f0b 继续收敛首页首帧延后UI负担
- 4658202 补齐Phase6量化矩阵与编译阻塞留痕
- 8af7ee5 补齐搜索与阅读器真机取证缺口
- 9f6c025 补齐搜索分页调试取证支架
- 738d432 同步Phase6剩余优化治理入口
- 44f5184 显式化RN宿主页缓存返回策略

## Android Modules
- :app
- :core-common
- :core-ui
- :core-bridge
- :core-bridge-contract
- :core-storage
- :core-network
- :feature-home
- :feature-book
- :feature-login
- :feature-search
- :feature-reader
- :feature-rn-host
- :feature-welfare
- :macrobenchmark

## RN Registrations
- Root app: Novel (registered through `appName` in `index.js`)
- AIWriteAssistantComponent
- BecomeWriterPageComponent
- BookManagePageComponent
- BookshelfPageComponent
- CategoryPageComponent
- CommentPageComponent
- FeedbackHelpMainPageComponent
- HelpSupportPageComponent
- HistoryPageComponent
- MemberCenterPageComponent
- MessagePageComponent
- MyReservationPageComponent
- PrivacyPolicyPageComponent
- QuestionDetailPageComponent
- QuestionListPageComponent
- RecommendBookPageComponent
- ReviewDetailPageComponent
- SettingsPageComponent
- TimedSwitchPageComponent
- ViewedUsersPageComponent
- WritePageComponent
- WriteReviewPageComponent

## Verification And Build Commands
- npm run android -> react-native run-android --active-arch-only
- npm run ios -> react-native run-ios
- npm run lint -> eslint .
- npm run start -> react-native start
- npm run web -> webpack serve --config webpack.config.js
- npm run test -> jest
- npm run harness:refresh -> node scripts/harness-refresh.js
- npm run harness:check -> node scripts/harness-check.js
- npm run version:show -> node scripts/version-sync.js show
- npm run version:patch -> node scripts/version-sync.js patch
- npm run version:minor -> node scripts/version-sync.js minor
- npm run version:major -> node scripts/version-sync.js major
- npm run version:sync -> node scripts/version-sync.js sync
- npm run build:android -> npm run version:patch && cd android && ./gradlew assembleRelease
- npm run build:android:bundle -> npm run version:patch && cd android && ./gradlew bundleRelease
- npm run harness:check
- npm run harness:refresh
- git diff --exit-code -- docs/harness/generated/workspace-snapshot.md
- corepack enable
- yarn install --frozen-lockfile
- npm test -- --runInBand
- chmod +x android/gradlew
- ./gradlew app:testDebugUnitTest app:lintDebug app:compileDebugAndroidTestKotlin :macrobenchmark:assemble
- cd android
- ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.home.HomeSmokeTest,com.novel.page.login.LoginSmokeTest,com.novel.page.search.SearchSmokeTest,com.novel.page.read.viewmodel.ReaderSmokeTest
- npm run lint
- ./gradlew app:detekt

## Refactor Summary
- Current phase: Phase 5
- Phase status: validated
- Latest closeout: see-control-panel
- Stage summary: Stage 3 = validated
- Effective date: see-stage-summary
