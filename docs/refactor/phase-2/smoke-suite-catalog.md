# Phase 2 Smoke Suite Catalog

## 目标
- 为 `V2-05` 建立可执行的核心路径 smoke 套件。
- 覆盖首页、登录、搜索、阅读器、设置五条主路径。
- 断言只保留“可渲染、可进入、关键结构存在”一级，优先降低 flake。

## 当前 smoke 覆盖
| Path | Layer | Test File | Command |
| --- | --- | --- | --- |
| 首页 | Android Compose | `android/app/src/androidTest/java/com/novel/page/home/HomeSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.home.HomeSmokeTest` |
| 登录 | Android Compose | `android/app/src/androidTest/java/com/novel/page/login/LoginSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.login.LoginSmokeTest` |
| 搜索 | Android Compose | `android/app/src/androidTest/java/com/novel/page/search/SearchSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.search.SearchSmokeTest` |
| 阅读器 | Android Compose | `android/app/src/androidTest/java/com/novel/page/read/viewmodel/ReaderSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.read.viewmodel.ReaderSmokeTest` |
| 设置 | RN Jest render | `__tests__/smoke/SettingsPage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx` |

## 当前稳定入口说明
- `HomeSmokeTest`
  - 使用 `HomePageSkeleton()` 作为稳定 smoke 入口，避免直接依赖 Hilt Activity 容器
- `LoginSmokeTest`
  - 使用 `LoginPageSkeleton()` 作为稳定 smoke 入口，优先验证登录页主结构可渲染
- `ReaderSmokeTest`
  - 使用 `NoAnimationContainer()` 作为无 Hilt 依赖的阅读器内容容器入口
- `SearchSmokeTest`
  - 使用 `SearchPageContent()` 作为搜索页核心 Compose 内容 smoke
- `SettingsPage.smoke.test.tsx`
  - 使用 RN render smoke，验证设置页主结构、初始化链路与核心 section 文案

## 推荐执行方式
- RN smoke
```bash
npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx
```

- Android smoke
```bash
cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.home.HomeSmokeTest,com.novel.page.login.LoginSmokeTest,com.novel.page.search.SearchSmokeTest,com.novel.page.read.viewmodel.ReaderSmokeTest
```

## 后续扩展
- 将 smoke 命令接入 `.github/workflows/`，作为 `V2-06` 的 Android smoke job。
- 为 RN 页面继续补充:
  - `BookshelfPageComponent`
  - `HistoryPageComponent`
  - `CategoryPageComponent`
- 为 Android smoke 增加截图/录屏归档模板，支撑 `V2-08` 证据标准化。
