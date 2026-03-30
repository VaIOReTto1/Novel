<!-- generated, do not edit by hand -->
# Workspace Snapshot

## Git State
- Branch: `main`

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
- ./gradlew app:testDebugUnitTest --stacktrace --console=plain
- ./gradlew app:lintDebug --stacktrace --console=plain
- ./gradlew app:compileDebugAndroidTestKotlin --stacktrace --console=plain
- ./gradlew :macrobenchmark:assemble --stacktrace --console=plain
- echo 'KERNEL=="kvm", GROUP="kvm", MODE="0666", OPTIONS+="static_node=kvm"' | sudo tee /etc/udev/rules.d/99-kvm4all.rules
- sudo udevadm control --reload-rules
- sudo udevadm trigger --name-match=kvm
- ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.home.HomeSmokeTest,com.novel.page.login.LoginSmokeTest,com.novel.page.search.SearchSmokeTest,com.novel.page.read.viewmodel.ReaderSmokeTest --stacktrace --console=plain
- npm run lint
- ./gradlew app:detekt

## Refactor Summary
- Current phase: Phase 8
- Phase status: validated
- Latest closeout: see-control-panel
- Stage summary: Stage 4 = validated
- Effective date: 2026-03-30 Stage 4 closeout
