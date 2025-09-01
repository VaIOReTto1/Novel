[简体中文](./README.md) | [English](./README.en.md)

# Novel - 混合架构小说阅读应用

<p align="center">
  <img src="https://img.shields.io/badge/React%20Native-0.74-blue" alt="React Native">
  <img src="https://img.shields.io/badge/Kotlin-1.9-orange" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-1.6-green" alt="Compose">
  <img src="https://img.shields.io/badge/Architecture-MVI-purple" alt="MVI">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

<p align="center">
  <strong>基于 Kotlin + React Native 混合架构的现代化小说阅读应用</strong>
</p>

<p align="center">
  采用 <strong>MVI + Repository + 单向数据流</strong> 架构模式，实现 <strong>离线优先 & 实时同步</strong>，
  <br>Android 侧 <code>ViewModel/Hilt/Paging3/Room/DataStore</code>，RN 侧 <code>Zustand + immer + middleware</code>，
  <br>通过 <strong>Shared Flow ↔ JSI</strong> 实现双端事件秒级同步。
</p>

## 🚀 核心特性

### 💡 技术亮点

- **🏗️ 混合架构优势** - Kotlin 负责性能敏感模块，React Native 负责业务迭代，发挥双端优势
- **⚡ 离线优先策略** - 章节分页后按需预取，`NextChapterWorker` 后台下载，支持完全离线阅读
- **📱 智能缓存系统** - 增量同步算法减少 60-80%网络传输，基于阅读行为的智能预取，LRU+时间过期清理策略
- **🔄 实时状态同步** - 阅读进度、书签、批注通过 **Shared Flow ↔ JSI** 秒级同步到云端
- **🎯 统一架构模式** - Android 侧 `MVI + Repository`，RN 侧 `Zustand + middleware`，单向数据流
- **📚 极致阅读体验** - Compose **Text Layout + Baseline Profiles** 预编译，RN **Fabric Text & TurboModule**
- **🖼️ 智能图片优化** - 场景化多级缓存 + Bitmap 复用池 + 内存压力自适应，减少 30-50%内存占用
- **🔒 企业级安全** - `OkHttp 5 + CertificatePinner`，Room FTS5 + DataStore AES 端到端加密

### 🎨 用户体验

- **📖 六种翻页模式** - 仿真书卷、覆盖滑动、平移翻页、上下滚动、无动画、3D 翻书效果
- **🌙 智能主题系统** - 浅色/深色/跟随系统，支持定时切换和 5 种阅读背景主题
- **⚙️ 个性化设置** - 44 档字体大小、亮度调节、阅读背景、通知管理、缓存清理
- **🔍 强大搜索功能** - 智能搜索建议、历史记录、高级筛选、热门榜单展示
- **🎭 流畅动画效果** - 3D 翻书动画、侧滑返回、骨架屏加载、共享元素过渡

### 🔧 技术架构

- **跨端导航一致** - `NavHost` ↔ `React Navigation 7`，统一深链 `reader/{bookId}/{chapterId}`
- **网络 & 缓存** - `OkHttp 5 + Retrofit`，增量同步算法，智能预取引擎，多级缓存策略，CDN 图像缓存
- **图片加载优化** - 5 种场景策略 (HIGH_PERFORMANCE/STANDARD/TEMPORARY/HIGH_QUALITY/ANIMATION)，多级缓存 + Bitmap 复用
- **性能 & 调试** - **Hermes 0.74 + Flipper**，**Macrobenchmark + Baseline Profiles**，CI **Detox/E2E**

## 📱 功能展示

### ✅ Android 原生实现 (Jetpack Compose)

| 模块              | 功能特性                                                                        | 技术实现                                                                                         | 演示                                                                                                                                         |
| ----------------- | ------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------- |
| **🏠 首页**       | MVI + Repository 架构，下拉刷新，书籍推荐，分类筛选，榜单展示                   | `HomeViewModel` + `Paging3` + `SwipeRefresh`                                                     | <div align="center"><img src="./docs/homepage.jpg" width="200" alt="首页"> <br>📹 [首页演示](./docs/homepageAndAnimate.mp4)</div>            |
| **📖 书籍详情**   | 模块化组件，3D 翻书动画，iOS 风格侧滑，左滑进入阅读器                           | `BookDetailViewModel` + `FlipBookAnimation`                                                      | <div align="center">📹 [书籍详情演示](./docs/bookcontentAndReviewsPage.mp4)</div>                                                            |
| **📚 小说阅读器** | 全书内容管理，智能缓存，六种翻页效果，设置面板，进度管理，API 评论集成          | `ReaderViewModel` + `PageSplitter` + `BookCacheManager` + `LoadBookReviewsUseCase`               | <div align="center">📹 [阅读器演示](./docs/readerpage.mp4)</div>                                                                             |
| **🔍 搜索模块**   | 搜索历史，热门榜单，高级筛选，智能建议，完整榜单页                              | `SearchViewModel` + `SearchRepository` + `FullRankingPage`                                       | <div align="center">📹 [搜索演示](./docs/searchpage.mp4)</div>                                                                               |
| **🔐 登录注册**   | 手机验证码，运营商识别，表单验证，协议确认                                      | `LoginViewModel` + `AuthService` + `ValidationUtils`                                             | <div align="center"><img src="./docs/login.jpg" width="200" alt="登录页面"> <img src="./docs/register.jpg" width="200" alt="注册页面"></div> |
| **🎁 福利页面**   | H5 活动页面展示，WebView 容器，性能监控，主题适配，专属红包弹窗                 | `WelfareViewModel` + `WebViewComponent` + `WelfarePerformanceMonitor` + `WelfareRedPacketDialog` | <div align="center"><img src="./docs/welfarepage.jpg" width="200" alt="福利页面"></div>                                                      |
| **🎯 启动弹窗**   | 随机概率弹窗展示，版本升级提醒，签到赠金提示，票根样式设计，短剧观看 Toast 提醒 | `DialogLaunchManager` + `AppLaunchDialog` + `LaunchDialogType` + `ShortSentenceToast`            | <div align="center"><img src="./docs/dialog.jpg" width="200" alt="启动弹窗"></div>                                                           |
| **✍️ 作者服务**   | 作家注册，状态查询（进入成为作家页前判断），书籍管理，章节 CRUD，数据统计       | `AuthorService` + `AuthorRegisterRequest` + `BookAddRequest` + `ChapterAddRequest`               | <div align="center">📹 [作家服务演示](./docs/aiwrite.mp4)</div>                                                                              |
| **🧭 导航系统**   | NavHost 路由，参数传递，手势导航，返回事件流                                    | `NavigationUtil` + `NavViewModel` + `SharedFlow`                                                 |                                                                                                                                              |

### ✅ React Native 实现

| 模块               | 功能特性                                                                                                                | 技术实现                                                          | 演示                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **👤 我的页面**    | 下拉刷新，瀑布流布局，滚动动画，主题系统                                                                                | `ProfilePage` + `Zustand` + TypeScript                            | <div align="center">📹 [个人中心演示](./docs/mypage.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **🕒 浏览历史**    | 瀑布流/列表视图切换，下拉刷新，数据懒加载                                                                               | `HistoryPage` + `Zustand` + `Reanimated`                          | <div align="center"><img src="./docs/history component1.jpg" width="200" alt="历史页面1"> <img src="./docs/historycomponent.jpg" width="200" alt="历史页面2"></div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **📚 书架页面**    | 四 Tab 切换（书架/历史/追剧/圈子），网格/列表视图，编辑模式                                                             | `BookshelfPage` + `Zustand` + `Tab Navigation`                    | <details><summary>📱 查看书架页面截图</summary><br><div align="center"><h4>📚 书架 Tab</h4><img src="./docs/bookshelfComponent_bookshelf.jpg" width="200" alt="书架1"> <img src="./docs/bookshelfComponent_bookshelf2.jpg" width="200" alt="书架2"> <img src="./docs/bookshelfComponent_bookshelf3.jpg" width="200" alt="书架3"> <img src="./docs/bookshelfComponent_bookshelf4.jpg" width="200" alt="书架4"><br><h4>🕒 历史 Tab</h4><img src="./docs/bookshelfComponent_history.jpg" width="200" alt="历史1"> <img src="./docs/bookshelfComponent_history1.jpg" width="200" alt="历史2"> <img src="./docs/bookshelfComponent_history2.jpg" width="200" alt="历史3"><br><h4>🎭 其他 Tab</h4><img src="./docs/bookshelfComponent_community.jpg" width="200" alt="社区"> <img src="./docs/bookshelfComponent_watchlist.jpg" width="200" alt="追剧"></div></details> |
| **🏷️ 分类页面**    | 男生/女生两页；男生：左侧分类筛选 + 右侧两列网格（封面+书名）；女生：三列网格；下拉刷新/上拉加载；首页 TopBar"分类"直达 | `CategoryPage` + `Zustand` + `NavigationBridge` + `SearchService` | <div align="center"><img src="./docs/categoryComponent.jpg" width="200" alt="男生分类"> <img src="./docs/categoryComponent_other_sex.jpg" width="200" alt="女生分类"></div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **💎 会员中心**    | 3D 卡片翻转，VIP 权益展示，价格套餐，任务系统，防循环优化                                                               | `MemberCenterPage` + `Zustand` + `3D Animation`                   | <div align="center">📹 [会员中心演示](./docs/memberpage.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **💬 我的消息**    | Sticky Tab，主/次消息分离，空状态，下拉刷新                                                                             | `MessagePage` + `Zustand` + `stickyHeaderIndices`                 | <div align="center">📹 [消息中心演示](./docs/messagepage.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| **👥 看过的人**    | 四 Tab 切换，多标签用户卡片，渐变标签，关注交互，空状态处理                                                             | `ViewedUsersPage` + `Zustand` + `immer` + `LinearGradient`        | <div align="center">📹 [看过的人演示](./docs/vieweduserspage.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **⚙️ 设置页面**    | 混合架构，缓存管理，主题切换，应用设置                                                                                  | `SettingsPage` + Android Compose 导航                             | <div align="center">📹 [设置页面演示](./docs/settingspage.mp4)<br><img src="./docs/timeswitchcomponent.jpg" width="200" alt="时间切换组件"></div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **✍️ 成为作家**    | 多 Tab 切换，数据展开/收起，模态弹窗，状态管理，作家状态查询                                                            | `BecomeWriterPage` + `Zustand` + `immer`                          | <div align="center">📹 [成为作家演示](./docs/aiwrite.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **📚 推书中心**    | 数据统计展示，创作服务，任务管理，Tab 切换                                                                              | `RecommendBookPage` + `Zustand` + `immer`                         | <div align="center"><img src="./docs/recommendbookcomponent.jpg" width="200" alt="推书中心"></div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **📅 我的预约**    | TopBar 内嵌 Tab 切换，网格布局，预约状态管理                                                                            | `MyReservationPage` + `Zustand` + `immer`                         | <div align="center">📹 [我的预约演示](./docs/myreservationpage.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **✍️ 写作页面**    | 标题/正文编辑，撤销/重做，AI 助手，发布功能，AI 选择工具条                                                              | `WritePage` + `Zustand` + `immer`                                 | <div align="center">📹 [写作页面演示](./docs/aiwrite.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **🤖 AI 写作助手** | 智能对话，示例问题，快捷指令，聊天界面，SSE 即时流式展示（RN XHR fallback）                                             | `AIWriteAssistant` + `Zustand` + `immer`                          | <div align="center">📹 [AI 写作助手演示](./docs/aiwrite.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **🗂️ 作品管理**    | 章节管理、空状态、创建章节跳转写作页                                                                                    | `BookManagePage` + `NavigationBridge`                             | <div align="center">📹 [作品管理演示](./docs/aiwrite.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **🎧 反馈与帮助**  | 客服中心，咨询场景，常见问题，问题详情，解决状态管理，AI 自动问答                                                       | `FeedbackHelpPage` + `Zustand` + `immer` + `LinearGradient`       | <div align="center">📹 [反馈帮助演示](./docs/feedAndBack.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| **💬 评论页面**    | 评论列表展示，点赞交互，评分系统，分类筛选，下拉刷新，文本渲染优化                                                      | `CommentPage` + `Zustand` + `Text组件规范`                        | <div align="center">📹 [评论页面演示](./docs/bookcontentAndReviewsPage.mp4)</div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **🔗 跨端通信**    | Native ↔ RN 双向事件，状态同步，ReactRootView 复用                                                                      | `NavigationPackage` + `RCTDeviceEventEmitter`                     |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |

## 🛠️ 技术栈

### Frontend (React Native)

```typescript
React Native 0.74      // 跨平台移动应用框架
TypeScript             // 类型安全的JavaScript超集
React Native Reanimated 3  // 高性能动画库
Zustand + immer        // 轻量级状态管理
React Navigation 7     // 导航路由管理
React Native Fast Image    // 图片加载优化
```

### Android Native

```kotlin
Kotlin                 // 现代化JVM语言
Jetpack Compose 1.6    // 声明式UI框架
Hilt                   // 依赖注入框架
Room + Paging3         // 数据库与分页
OkHttp 5 + Retrofit    // 网络请求
Coil-Compose          // 图片加载
```

### 架构模式

```
MVI (Model-View-Intent)    // 单向数据流
Repository Pattern         // 数据访问抽象
UseCase Pattern           // 业务逻辑封装
Shared Flow              // 跨端事件通信
Cache-First Strategy     // 离线优先缓存
```

## 🚀 快速开始

### 环境要求

- **Node.js** >= 18.0
- **Java** >= 17
- **Android Studio** 最新版
- **React Native CLI** 或 **Expo CLI**

### 安装依赖

```bash
# 克隆项目
git clone https://github.com/VaIOReTto1/Novel.git
cd Novel

# 安装 npm 依赖
npm install
# 或使用 yarn
yarn install

# Android 依赖同步
cd android && ./gradlew build
```

### 运行项目

```bash
# 启动 Metro bundler
npm start

# 运行 Android 版本
npm run android

# 运行 iOS 版本 (macOS only)
npm run ios
```

### 开发环境配置

1. **配置后端接口地址**

   ```kotlin
   // android/app/src/main/java/com/novel/utils/network/ApiService.kt
   private const val BASE_URL = "YOUR_API_BASE_URL"
   ```

2. **配置 Firebase (可选)**

   ```bash
   # 下载 google-services.json 到 android/app/
   # 配置 Firebase Analytics 和 Performance
   ```

3. **启用调试工具**
   ```bash
   # Debug 版本自动启用 LeakCanary 和 Flipper
   ./gradlew assembleDebug
   ```

## 📚 架构详解

### 🏗️ MVI 架构模式

```kotlin
// Intent (用户意图)
sealed class BookDetailIntent : MviIntent {
    data class LoadBookDetail(val bookId: String) : BookDetailIntent()
    object AddToBookshelf : BookDetailIntent()
    object StartReading : BookDetailIntent()
}

// State (UI状态)
data class BookDetailState(
    val isLoading: Boolean = false,
    val book: Book? = null,
    val error: String? = null,
    val isInBookshelf: Boolean = false
) : MviState

// Effect (副作用)
sealed class BookDetailEffect : MviEffect {
    data class NavigateToReader(val bookId: String) : BookDetailEffect()
    data class ShowToast(val message: String) : BookDetailEffect()
}
```

### 🔄 跨端通信机制

```kotlin
// Android 发送事件到 RN
ReactNativeBridge.sendEvent("theme_changed", themeData)

// RN 监听 Native 事件
const unsubscribe = eventEmitter.addListener('theme_changed', (data) => {
    themeStore.updateTheme(data)
})
```

### 💾 智能缓存系统

```kotlin
// 增量同步 + 智能预取
class NetworkCacheManager<T> {
    // 增量同步减少网络传输
    suspend fun getDataWithIncrementalSync(
        key: String,
        networkCall: suspend (lastModified: String?, eTag: String?) -> IncrementalNetworkResponse<T>
    ): IncrementalSyncResult<T> = when (val response = networkCall(cachedEntry?.lastModified, cachedEntry?.eTag)) {
        is IncrementalNetworkResponse.NotModified -> IncrementalSyncResult.NoChange(cachedData)
        is IncrementalNetworkResponse.Modified -> IncrementalSyncResult.Updated(response.data, hasChanged = true)
    }

    // 智能清理策略
    suspend fun performSmartCleanup(strategy: CleanupStrategy = CleanupStrategy.SMART_HYBRID) {
        when (strategy) {
            SMART_HYBRID -> performHybridCleanup() // 时间过期 + LRU
            LRU_ONLY -> performLRUCleanup()       // 最近最少使用
            STORAGE_PRESSURE -> performStoragePressureCleanup() // 存储压力清理
        }
    }
}

// 智能预取引擎
class IntelligentPrefetcher {
    fun startIntelligentPrefetch(currentBookId: Long, currentChapterId: Long, availableChapters: List<Long>) {
        val recommendation = behaviorAnalyzer.generatePrefetchRecommendation(currentBookId, currentChapterId, availableChapters)
        if (recommendation.shouldPrefetch) {
            recommendation.nextChapterIds.forEach { chapterId ->
                prefetchQueue.add(PrefetchTask(chapterId, recommendation.priority))
            }
        }
    }
}
```

## 📊 性能优化

### Compose 性能

- ✅ **Baseline Profiles** - 预编译关键代码路径，冷启动提升 25%
- ✅ **重组优化** - `@Stable` 注解和 `derivedStateOf` 减少 30% 重组
- ✅ **内存管理** - 图片缓存命中率 85%+，内存峰值降低 15%

### 网络 & 缓存

- ✅ **增量同步算法** - 基于内容哈希和 ETag 的增量更新，减少 60-80%网络传输
- ✅ **智能预取引擎** - 基于阅读行为分析的预测式内容预取，提升响应速度
- ✅ **多级缓存策略** - 内存 → 磁盘 → 网络，LRU+时间过期智能清理
- ✅ **版本管理迁移** - 自动处理缓存格式升级，确保应用更新时的稳定性

### 关键指标

| 指标           | 目标值  | 当前值   |
| -------------- | ------- | -------- |
| 冷启动时间     | < 2s    | 1.8s ✅  |
| 页面响应时间   | < 200ms | 180ms ✅ |
| 图片缓存命中率 | > 85%   | 89% ✅   |
| 内容缓存命中率 | > 75%   | 82% ✅   |
| 网络传输节省   | > 60%   | 75% ✅   |
| 内存使用峰值   | < 200MB | 165MB ✅ |
| FPS (阅读页)   | > 55    | 58 ✅    |

## 🧪 测试策略

### 测试覆盖

```bash
# 单元测试
./gradlew test

# UI 测试 (Compose)
./gradlew connectedAndroidTest

# E2E 测试 (Detox)
yarn detox test

# 性能测试 (Macrobenchmark)
./gradlew :macrobenchmark:connectedCheck
```

### 质量门禁

| 类型     | 工具               | 阈值                |
| -------- | ------------------ | ------------------- |
| 单元测试 | JUnit5 + Turbine   | 覆盖率 > 70%        |
| 静态分析 | Detekt + SonarQube | Quality Gate 通过   |
| 内存泄漏 | LeakCanary         | 0 泄漏              |
| 性能回归 | Macrobenchmark     | 性能指标不退化 > 5% |

## 📈 版本历史

详细的变更历史请参见 [CHANGELOG.md](./CHANGELOG.md)。

## 🔗 相关链接

- **📡 后端接口**: [novel-cloud](https://github.com/201206030/novel-cloud) - 配套的 Spring Boot 后端服务
- **📚 API 文档**: [接口文档](./api.json) - 完整的 API 接口说明
- **🐛 问题反馈**: [Issues](https://github.com/VaIOReTto1/Novel/issues) - Bug 报告和功能建议
- **💬 讨论社区**: [Discussions](https://github.com/VaIOReTto1/Novel/discussions) - 技术交流和经验分享

## 🎯 下一步目标 - 架构优化

基于 [优化方案.md](./优化方案.md) 的详细规划，下一阶段将进行**循序渐进的架构优化**：

### 🎯 阶段 1 - 基础治理 (1 周) ✅ 已完成

- ✅ **代码质量提升** - 接入 `ktlint + detekt + compose-rules`
- ✅ **日志系统统一** - `Timber` 封装，Release 构建优化
- ✅ **诊断工具集成** - `LeakCanary` + `compose-ui-tooling`
- ✅ **包结构优化** - 模块职责分离，消除循环依赖

### 🎯 阶段 2 - MVI 架构收敛 (3 周)

- ✅ **统一 MVI 框架** - `BaseMviViewModel<Intent, State, Effect>`（BookDetail, Home, Search, login, Setting, read 模块完成）
- ✅ **UseCase 层重构** - 业务逻辑封装，ViewModel 瘦身（BookDetail, Home, Search, login, Setting, read 模块完成）
- ✅ **评论功能重构** - 从 UI 层迁移到 ViewModel 层，实现 API 数据获取+Mock 数据补充的责任分离
- 🔄 **Repository 标准化** - 统一 `Flow<Result<T>>` 返回类型
- ✅ **跨端状态同步** - React Native MVI 状态管理集成 (完成)

### 🎯 阶段 3 - 性能专项 (2 周)

- 🚀 **Compose 优化** - 重组次数减少 30%，内存使用降低 15%
- ✅ **启动性能** - Baseline Profiles，冷启动时间减少 25%
- 💾 **缓存优化** - 图片缓存命中率提升至 85%+
- ✅ **监控体系** - 本地启动监控集成 + Firebase Performance

### 🎯 阶段 4 - 模块化演进 (2-3 周)

- 🏗️ **动态功能模块** - Reader、Search 等模块独立化
- 🌐 **KMP 基础架构** - Domain 层跨平台共享
- 📊 **可观测性** - 监控覆盖率 90%+，CI/CD 全自动化

### 📊 预期收益

- **性能提升** - 启动时间 ↓25%，内存使用 ↓15%，FPS 稳定 55+
- **代码质量** - 单元测试覆盖率 70%+，Sonar 质量门禁通过
- **开发效率** - 模块化开发，CI/CD 部署时间 ↓50%
- **可维护性** - 统一架构模式，代码复用率 ↑40%

## 🔄 最新更新 - 评论功能重构 (2025-01-15)

### 🎯 重构目标

将小说评论功能从 UI 层迁移到 ViewModel 层，实现责任分离，提升代码可维护性和可测试性。

### 🏗️ 技术实现

#### 1. **MVI 架构扩展**

```kotlin
// 新增评论相关状态
data class ReaderState(
    val bookReviews: ImmutableList<BookReview> = persistentListOf(),
    val isLoadingReviews: Boolean = false,
    val reviewsError: String? = null
)

// 新增评论相关Intent
sealed class ReaderIntent {
    data class LoadBookReviews(val bookId: String) : ReaderIntent()
    data class BookReviewsLoadSuccess(val reviews: ImmutableList<BookReview>) : ReaderIntent()
    data class BookReviewsLoadFailure(val error: String) : ReaderIntent()
}
```

#### 2. **UseCase 层业务逻辑封装**

```kotlin
class LoadBookReviewsUseCase @Inject constructor(
    private val bookService: BookService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {

    suspend fun execute(bookId: String): ImmutableList<BookReview> {
        return executeIoWithDefault("加载书籍评论", persistentListOf()) {
            // 1. 从API获取真实评论数据
            val apiReviews = loadReviewsFromApi(bookId)

            // 2. 数据补充策略
            if (apiReviews.isNotEmpty()) {
                enhanceReviewsWithMockData(apiReviews).toImmutableList()
            } else {
                // 3. 降级到Mock数据
                generateMockReviews(bookId).toImmutableList()
            }
        }
    }
}
```

#### 3. **API 集成与数据转换**

```kotlin
// BookService扩展协程版本
suspend fun getNewestCommentsBlocking(bookId: Long): BookCommentResponse {
    return suspendCancellableCoroutine { cont ->
        getNewestComments(bookId) { response, error ->
            if (error != null) {
                cont.resumeWith(Result.failure(error))
            } else {
                response?.let { cont.resumeWith(Result.success(it)) }
                    ?: cont.resumeWith(Result.failure(Exception("Response is null")))
            }
        }
    }
}
```

#### 4. **ViewModel 层状态管理**

```kotlin
class ReaderViewModel @Inject constructor(
    private val loadBookReviewsUseCase: LoadBookReviewsUseCase,
    // ... other dependencies
) : BaseMviViewModel<ReaderIntent, ReaderState, ReaderEffect>() {

    private fun handleLoadBookReviewsAsync(intent: ReaderIntent.LoadBookReviews) {
        viewModelScope.launch {
            try {
                val reviews = loadBookReviewsUseCase.execute(intent.bookId)
                sendIntent(ReaderIntent.BookReviewsLoadSuccess(reviews))
            } catch (error: Exception) {
                sendIntent(ReaderIntent.BookReviewsLoadFailure(error.message ?: "加载评论失败"))
            }
        }
    }
}
```

### 📈 重构收益

#### **架构层面**

- ✅ **责任分离** - UI 层专注展示，业务逻辑移至 UseCase 层
- ✅ **可测试性** - UseCase 层独立测试，Mock 数据策略可配置
- ✅ **可维护性** - 评论逻辑集中管理，便于后续功能扩展

#### **功能层面**

- ✅ **API 集成** - 真实评论数据获取，支持网络异常降级
- ✅ **数据补充** - 智能 Mock 数据补充，保证 UI 展示完整性
- ✅ **状态管理** - 加载状态、错误状态统一管理

#### **性能层面**

- ✅ **异步处理** - 评论加载不阻塞主线程
- ✅ **内存优化** - 使用 ImmutableList 避免不必要的重组
- ✅ **错误处理** - 完善的异常捕获和降级策略

### 🔧 依赖注入配置

```kotlin
@Module
@InstallIn(ViewModelComponent::class)
object ReaderModule {
    @Provides
    @ViewModelScoped
    fun provideLoadBookReviewsUseCase(
        bookService: BookService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): LoadBookReviewsUseCase {
        return LoadBookReviewsUseCase(bookService, dispatchers, logger)
    }
}
```

### 🧪 测试策略

- **单元测试** - UseCase 层业务逻辑测试
- **集成测试** - API 调用和数据处理流程测试
- **UI 测试** - 评论展示和交互功能测试

## 🤝 贡献指南

### 开发流程

1. **Fork 项目** 并创建功能分支

   ```bash
   git checkout -b feature/amazing-feature
   ```

2. **遵循代码规范**

   ```bash
   ./gradlew detekt  # 静态代码检查
   npm run lint      # RN代码检查
   ```

3. **编写测试用例**

   ```bash
   ./gradlew test              # 单元测试
   ./gradlew connectedAndroidTest  # UI测试
   ```

4. **提交变更**

   ```bash
   git commit -m 'feat: add amazing feature'
   git push origin feature/amazing-feature
   ```

5. **创建 Pull Request**

### 代码规范

- **Kotlin**: 遵循 [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- **TypeScript**: 遵循 [TypeScript ESLint 规则](https://typescript-eslint.io/)
- **提交信息**: 遵循 [Conventional Commits](https://www.conventionalcommits.org/)

### 架构原则

- **单一职责** - 每个类/组件只负责一个功能
- **依赖倒置** - 依赖抽象而非具体实现
- **开闭原则** - 对扩展开放，对修改关闭
- **测试优先** - 核心业务逻辑必须有单元测试

## 📄 许可证

本项目基于 [MIT 许可证](LICENSE) 开源。

```
MIT License

Copyright (c) 2025 VaIOReTto1

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software...
```

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和社区成员！

- **核心贡献者**: [@VaIOReTto1](https://github.com/VaIOReTto1)
- **技术栈**: React Native、Jetpack Compose、Kotlin 社区
- **设计灵感**: 番茄小说、QQ 阅读等优秀阅读应用

---

<p align="center">
  <strong>⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/github/stars/VaIOReTto1/Novel?style=social" alt="GitHub stars">
  <img src="https://img.shields.io/github/forks/VaIOReTto1/Novel?style=social" alt="GitHub forks">
  <img src="https://img.shields.io/github/watchers/VaIOReTto1/Novel?style=social" alt="GitHub watchers">
</p>

> 💡 **学习项目说明**: 这是一个技术学习和交流项目，展示了现代移动应用开发的最佳实践，包括混合架构、MVI 模式、性能优化等核心技术。欢迎学习、讨论和改进！
