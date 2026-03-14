# Phase 0 - 资产清单

## 说明
- 本清单聚焦 Phase 0 的最小核心资产：Route、RN 组件、Bridge、存储、数据库、构建与发布配置。
- 目标不是罗列所有文件，而是锁定后续阶段最容易受影响、必须先稳定边界的对象。

## 1. Native Route 资产

### Compose Navigation Routes
来源：`android/app/src/main/java/com/novel/utils/NavigationUtil.kt`

| Route | 类型 | 承载页 |
| --- | --- | --- |
| `main` | Compose | `MainPage` |
| `login` | Compose | `LoginPage` |
| `search?query={query}` | Compose | `SearchPage` |
| `search_result?query={query}` | Compose | `SearchResultPage` |
| `full_ranking/{rankingType}/{encodedData}` | Compose | `FullRankingPage` |
| `book_detail/{bookId}?fromRank={fromRank}` | Compose | `BookDetailPage` |
| `reader/{bookId}?chapterId={chapterId}` | Compose | `ReaderPage` |
| `profile` | RN Host | `Novel` 根组件 |
| `settings` | RN Host | `SettingsPageComponent` |
| `timed_switch` | RN Host | `TimedSwitchPageComponent` |
| `privacy_policy` | RN Host | `PrivacyPolicyPageComponent` |
| `help_support` | RN Host | `HelpSupportPageComponent` |
| `history` | RN Host | `HistoryPageComponent` |
| `message` | RN Host | `MessagePageComponent` |
| `becomewriter?isAuthor={isAuthor}` | RN Host | `BecomeWriterPageComponent` |
| `writepage` | RN Host | `WritePageComponent` |
| `aipage` | RN Host | `AIWriteAssistantComponent` |
| `bookmanage` | RN Host | `BookManagePageComponent` |
| `recommendbook` | RN Host | `RecommendBookPageComponent` |
| `viewedusers` | RN Host | `ViewedUsersPageComponent` |
| `myreservation` | RN Host | `MyReservationPageComponent` |
| `membercenter` | RN Host | `MemberCenterPageComponent` |
| `feedbackhelp` | RN Host | `FeedbackHelpMainPageComponent` |
| `questionlist` | RN Host | `QuestionListPageComponent` |
| `questiondetail` | RN Host | `QuestionDetailPageComponent` |
| `comment/{bookData}` | RN Host | `CommentPageComponent` |
| `writereview/{bookId}?rating={rating}` | RN Host | `WriteReviewPageComponent` |
| `writereview` | RN Host | `WriteReviewPageComponent` |
| `reviewdetail/{commentData}` | RN Host | `ReviewDetailPageComponent` |

## 2. RN 组件注册资产

### Root 组件
来源：`index.js`, `App.tsx`

| 组件名 | 入口 | 说明 |
| --- | --- | --- |
| `Novel` | `AppRegistry.registerComponent(appName, () => App)` | RN 根应用，实际渲染 `ProfilePage` |

### 显式注册的 PageComponent
来源：`src/page/**/**Component.tsx`

| 组件名 | 文件 |
| --- | --- |
| `BookshelfPageComponent` | `src/page/BookshelfPage/BookshelfPageComponent.tsx` |
| `CategoryPageComponent` | `src/page/CategoryPage/CategoryPageComponent.tsx` |
| `CommentPageComponent` | `src/page/comment/CommentPage/CommentPageComponent.tsx` |
| `ReviewDetailPageComponent` | `src/page/comment/ReviewDetailPage/ReviewDetailPageComponent.tsx` |
| `WriteReviewPageComponent` | `src/page/comment/WriteReviewPage/WriteReviewPageComponent.tsx` |
| `BecomeWriterPageComponent` | `src/page/ScrollBox/BecomeWriterPage/BecomeWriterPageComponent.tsx` |
| `FeedbackHelpMainPageComponent` | `src/page/ScrollBox/FeedbackHelpPage/FeedbackHelpMainPageComponent.tsx` |
| `QuestionDetailPageComponent` | `src/page/ScrollBox/FeedbackHelpPage/QuestionDetailPageComponent.tsx` |
| `QuestionListPageComponent` | `src/page/ScrollBox/FeedbackHelpPage/QuestionListPageComponent.tsx` |
| `HistoryPageComponent` | `src/page/ScrollBox/HistoryPage/HistoryPageComponent.tsx` |
| `MemberCenterPageComponent` | `src/page/ScrollBox/MemberCenterPage/MemberCenterPageComponent.tsx` |
| `MessagePageComponent` | `src/page/ScrollBox/MessagePage/MessagePageComponent.tsx` |
| `MyReservationPageComponent` | `src/page/ScrollBox/MyReservationPage/MyReservationPageComponent.tsx` |
| `RecommendBookPageComponent` | `src/page/ScrollBox/RecommendBookPage/RecommendBookPageComponent.tsx` |
| `ViewedUsersPageComponent` | `src/page/ScrollBox/ViewedUsersPage/ViewedUsersPageComponent.tsx` |
| `HelpSupportPageComponent` | `src/page/SettingsPage/helpsupportPage/HelpSupportPageComponent.tsx` |
| `PrivacyPolicyPageComponent` | `src/page/SettingsPage/privacypolicyPage/PrivacyPolicyPageComponent.tsx` |
| `SettingsPageComponent` | `src/page/SettingsPage/settingspage/SettingsPageComponent.tsx` |
| `TimedSwitchPageComponent` | `src/page/SettingsPage/TimeSwitchPage/TimedSwitchPageComponent.tsx` |
| `AIWriteAssistantComponent` | `src/page/Writer/AIWriteAssistant/AIWriteAssistantComponent.tsx` |
| `BookManagePageComponent` | `src/page/Writer/BookManage/BookManagePageComponent.tsx` |
| `WritePageComponent` | `src/page/Writer/WritePage/WritePageComponent.tsx` |

## 3. Bridge 与事件资产

### Native Bridge Modules
| 模块 | 主要职责 | 关键文件 |
| --- | --- | --- |
| `NavigationBridgeModule` | 导航、缓存、历史、作者、AI、主题等混合 Bridge 能力 | `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt` |
| `UserBridgeModule` | 用户数据、登录状态、余额等 Promise 能力 | `android/app/src/main/java/com/novel/rn/bridge/UserBridgeModule.kt` |
| `SettingsBridgeModule` | 设置、主题、缓存、定时切换、登出等 Promise/Callback 能力 | `android/app/src/main/java/com/novel/rn/settings/SettingsBridgeModule.kt` |

### 已确认的关键事件名
| 事件名 | 来源 |
| --- | --- |
| `ThemeChanged` | `ThemeManager.kt`, `SettingsViewModel.kt` |
| `WritePageSelectionMenuAction` | `NavigationBridgeModule.kt` |

### 协议风险
- `NavigationBridgeModule` 暴露方法数量多、职责混杂，是后续 Bridge 协议收口的一级风险点。
- 同时存在 Promise、Callback 与 Event 三种交互模式，协议不统一。

## 4. 存储与数据库资产

### 本地存储与安全
| 资产 | 关键文件 | 当前观察 |
| --- | --- | --- |
| 用户设置 | `android/app/src/main/java/com/novel/utils/Store/UserDefaults/NovelUserDefaults.kt` | 仍是后续可迁移对象，README 中也提到未来可迁移到 DataStore |
| 安全存储 | `android/app/src/main/java/com/novel/utils/Store/NovelKeyChain/NovelKeyChain.kt` | 基于 `EncryptedSharedPreferences` |
| Token 提供 | `NovelKeyChain.kt`, `AuthInterceptor.kt` | KeyChain 提供 Token，后续需要与网络主栈统一 |

### 数据库
| 资产 | 关键文件 | 当前观察 |
| --- | --- | --- |
| Room Database | `android/app/src/main/java/com/novel/utils/dao/NovelDatabase.kt` | `version = 4`, `exportSchema = false` |
| 数据库配置 | `android/app/src/main/java/com/novel/di/DatabaseModule.kt` | 当前使用 `fallbackToDestructiveMigration()` |

## 5. 构建与发布配置资产

| 资产 | 路径 | 当前观察 |
| --- | --- | --- |
| Android 根构建 | `android/build.gradle` | `compileSdkVersion=35`, `minSdkVersion=26`, `targetSdkVersion=35` |
| App 构建 | `android/app/build.gradle` | `applicationId=com.novel`, `versionCode/versionName` 由外部版本脚本注入 |
| 版本属性 | `android/version.properties` | 当前版本 `1.0.2`, `VERSION_CODE=4` |
| 版本同步脚本 | `android/versioning.gradle`, `scripts/version-sync.js` | Android 与 package/app.json 双向同步 |
| Gradle 属性 | `android/gradle.properties` | `newArchEnabled=false`, `hermesEnabled=true` |
| Macrobenchmark | `android/macrobenchmark/build.gradle` | 已接入基准测试与 Baseline Profile，但仍需发布态可信度治理 |

## 6. 安全与发布风险资产
| 风险项 | 代码位置 | 当前观察 |
| --- | --- | --- |
| 明文流量 | `android/app/src/main/AndroidManifest.xml`, `android/app/src/main/res/xml/network_security_config.xml` | 主 manifest 仍 `usesCleartextTraffic=true` 且 `base-config cleartextTrafficPermitted=true` |
| 权限过宽 | `android/app/src/main/AndroidManifest.xml` | 仍声明 `READ_PHONE_STATE`, `READ_PHONE_NUMBERS`, `READ_PRIVILEGED_PHONE_STATE` |
| Room 迁移风险 | `DatabaseModule.kt` | 发布路径不应继续默认 destructive migration |
| Release 生产化不足 | `android/app/build.gradle` | 当前 release 路径仍需系统化收口 |

## 7. 当前结论
- Route、RN 组件、Bridge、存储、数据库、发布配置已经具备首轮清单基础，可支撑 `V0-02` 进入验证。
- `NavigationBridgeModule`、数据库迁移策略、Manifest/网络安全配置、Root RN 页面入口，是后续 Phase 1 与 Phase 3 的重点边界对象。
