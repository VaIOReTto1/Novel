# Smoke Run - android - core-pages

- Scenario: Home / Login / Search / Reader Android smoke suite
- Command: `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.home.HomeSmokeTest,com.novel.page.login.LoginSmokeTest,com.novel.page.search.SearchSmokeTest,com.novel.page.read.viewmodel.ReaderSmokeTest`
- Route / Page: `home`, `login`, `search`, `reader`
- Device / API: `DN2101 / Android 13`
- Network: `adb over tcpip (192.168.8.130:5555)`
- Build Variant: `debugAndroidTest`
- Expected: 四条核心 Android 路径 smoke 用例全部通过，且不依赖 Hilt Activity 容器
- Actual: `connectedDebugAndroidTest` 成功，4 个 smoke tests 全部通过
- Evidence Files:
  - `android/app/build/reports/androidTests/connected/debug/index.html`
  - `android/app/build/outputs/androidTest-results/connected/`
- Result: `green`

## Notes
- `HomeSmokeTest` 采用 `HomePageSkeleton()` 作为稳定 smoke 入口。
- `LoginSmokeTest` 采用 `LoginPageSkeleton()` 作为稳定 smoke 入口。
- `ReaderSmokeTest` 采用 `NoAnimationContainer()` 作为无 Hilt 依赖的内容容器入口。
- `SearchSmokeTest` 继续使用 `SearchPageContent()` 作为 Compose 层 smoke 入口。
