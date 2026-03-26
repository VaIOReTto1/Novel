<!-- generated, do not edit by hand -->
<!-- source-head: 813536c609d3969c3d03630c2241e9f216909a78 -->
# Workspace Snapshot

## Git State
- Branch: `main`
- Head: `813536c`

## Recent Commits
- 813536c 迁移NovelKeyChain到core-storage
- 03b57b6 抽离RN模块注册表
- b6007c7 引入宿主导航与缓存网关
- 06cd6fd 抽离应用启动生命周期上报器
- 96f6ac5 抽离应用启动编排器
- 004d43e 抽离ReactRootView缓存注册表
- f0be13b 同步WaveD宿主根收口文档
- e6aad3d 压薄ComposeMainActivity宿主入口

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
- Phase status: in_progress
- Latest closeout: 2026-03-21
- Stage summary: Stage 3 = Phase 5-6 = in_progress（2026-03-24 reopen 持续推进；2026-03-21 closeout 作为历史 checkpoint 保留）
- Effective date: see-stage-summary
