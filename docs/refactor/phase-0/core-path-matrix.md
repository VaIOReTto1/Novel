# Phase 0 - 核心路径矩阵

## 说明
- 本矩阵用于定义 Phase 0 必须覆盖的核心用户路径与页面边界。
- 目标是为后续基线采集、Smoke 设计、验证看板和禁区清单提供统一引用。
- 本文基于当前仓库代码结构整理，后续如有新增核心路径，必须同步更新。

## 核心路径矩阵
| 路径 ID | 领域 | 入口 | 承载方式 | 主路径/组件 | 关键代码定位 | Phase 0 关注点 |
| --- | --- | --- | --- | --- | --- | --- |
| CP-01 | 首页 | 应用启动后默认进入 | Compose | `main -> MainPage -> HomePage` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `android/app/src/main/java/com/novel/page/MainPage.kt`, `android/app/src/main/java/com/novel/page/home/HomePage.kt` | 启动、首帧、首页滚动、推荐与榜单数据展示 |
| CP-02 | 分类 | 底部 Tab 第二项 | RN Host | `CategoryPageComponent` | `android/app/src/main/java/com/novel/page/MainPage.kt`, `src/page/CategoryPage/CategoryPageComponent.tsx` | RN 首开耗时、Native/RN 路由一致性 |
| CP-03 | 福利 | 底部 Tab 第三项 | Compose + WebView | `WelfarePage` | `android/app/src/main/java/com/novel/page/MainPage.kt`, `android/app/src/main/java/com/novel/page/welfare/WelfarePage.kt` | WebView 首屏、错误态、主题同步、安全策略 |
| CP-04 | 书架 | 底部 Tab 第四项 | RN Host | `BookshelfPageComponent` | `android/app/src/main/java/com/novel/page/MainPage.kt`, `src/page/BookshelfPage/BookshelfPageComponent.tsx` | RN 首开耗时、Bridge 数据依赖 |
| CP-05 | 我的 | 底部 Tab 第五项 | RN Root | `profile -> Novel -> App -> ProfilePage` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `android/app/src/main/java/com/novel/rn/ReactNativePage.kt`, `index.js`, `App.tsx` | Root 组件初始化、主题注入、Profile 首开路径 |
| CP-06 | 登录 | 原生导航页 | Compose | `login` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `android/app/src/main/java/com/novel/page/login/LoginPage.kt` | 表单、验证码、登录副作用、回归 Smoke |
| CP-07 | 搜索 | 原生导航页 | Compose | `search` / `search_result` / `full_ranking` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `android/app/src/main/java/com/novel/page/search/` | 搜索输入、结果页、榜单页、筛选与性能 |
| CP-08 | 书详情 | 从首页/榜单/搜索跳转 | Compose | `book_detail/{bookId}` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `android/app/src/main/java/com/novel/page/book/BookDetailPage.kt` | 详情页加载、评论、开始阅读入口 |
| CP-09 | 阅读器 | 从书详情或直接跳转 | Compose | `reader/{bookId}?chapterId={chapterId}` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `android/app/src/main/java/com/novel/page/read/ReaderPage.kt`, `android/app/src/main/java/com/novel/page/read/viewmodel/ReaderViewModel.kt` | 翻页、切章、设置、进度恢复、性能基线 |
| CP-10 | 设置 | RN 页面承载，含定时切换、隐私、帮助等 | RN Host + Native Bridge | `settings`, `timed_switch`, `privacy_policy`, `help_support` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `src/page/SettingsPage/`, `android/app/src/main/java/com/novel/rn/settings/SettingsBridgeModule.kt` | Theme/Settings Bridge 协议、缓存清理、导出导入 |
| CP-11 | 作者与 AI | 原生导航进入 RN 页面 | RN Host + Native Bridge | `becomewriter`, `writepage`, `aipage`, `bookmanage` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `src/page/Writer/`, `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt` | Author/AI Bridge、Promise 协议、性能与回退路径 |
| CP-12 | 评论与评价 | 原生导航进入 RN 页面 | RN Host | `comment`, `writereview`, `reviewdetail` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `src/page/comment/` | 评论/评价组件注册、参数传递、协议兼容性 |

## 核心链路优先级

### P0 必须覆盖
- 首页启动与首页滚动。
- 书详情进入阅读器。
- 阅读器首开、切章、翻页、设置恢复。
- 登录与搜索主流程。
- 福利页首开与错误态。
- 设置页与主题切换主流程。

### P0 次级覆盖
- 分类、书架、我的页面首开。
- 作者页、AI 页、评论页等 Bridge/RN 扩展路径。

## 当前观察
- 当前默认首入口是原生 Compose 路径 `main`，其中底部 Tab 混合承载 Compose 与 RN 页面。
- RN Root 页面并非直接对应某个 `*PageComponent`，而是通过 `Novel` 根组件加载 `App.tsx -> ProfilePage`。
- 设置、作者、AI、评论等能力存在多条 Native/RN 混合路径，后续必须优先做协议盘点而不是直接重构。
