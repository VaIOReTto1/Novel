# Phase 4 宿主页挂载与风险验证矩阵

## 目标
- 为 `V4-05` 提供正式、可追溯的宿主页验证矩阵。
- 让后续执行者在不改变 Route 语义的前提下，明确 `ReactNativePage`、`NavigationUtil`、`Bridge` 初始化时序需要验证什么。

## 风险范围
- `profile-host / RN Host` 页面挂载
- `ReactRootView` 缓存复用
- React 上下文初始化时序
- Theme 初始注入与事件同步
- 白屏与降级路径

## 宿主页链路
| 链路环节 | 文件 | 当前职责 | 主要风险 |
| --- | --- | --- | --- |
| Route 入口分发 | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt` | Compose Route 到 RN 页面容器的统一入口 | Route 数量多，宿主页入口分散 |
| RN 容器 | `android/app/src/main/java/com/novel/rn/ReactNativePage.kt` | ReactRootView 获取、Theme 初始 props、上下文监听、返回销毁 | 白屏、Root 缓存、Theme 注入时序 |
| Application Root 缓存 | `android/app/src/main/java/com/novel/MainApplication.kt` | `getOrCreateReactRootView()`、按需初始化网络服务 | Root 复用与按需初始化时序 |
| Bridge 出口 | `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt` | Route/Query/Host/Theme 混合出口 | 初始化时序与职责混杂 |
| Theme 事件生产者 | `android/app/src/main/java/com/novel/ui/theme/ThemeManager.kt`, `android/app/src/main/java/com/novel/rn/settings/SettingsViewModel.kt` | `ThemeChanged` 事件与当前主题状态同步 | 初始主题与运行时主题不一致 |

## 重点验证场景
| ID | 场景 | 入口 | 期望 | 风险等级 |
| --- | --- | --- | --- | --- |
| H-01 | 冷启动后首次进入 `profile` | `main -> profile` | Root 成功挂载，无白屏，Theme 初始 props 正确 | high |
| H-02 | 冷启动后首次进入 `settings` | `main -> settings` | Root 成功挂载，无白屏，Settings MVI 与 RN 宿主页正常初始化 | high |
| H-03 | 从 Native 页面进入写作或 AI 页面 | `becomewriter` / `writepage` / `aipage` | Bridge 出口与宿主页链路均正常，参数透传稳定 | high |
| H-04 | 连续进入多个 RN Host 页面 | `profile -> settings -> history -> message` | Root 缓存与销毁策略可预期，无异常复用 | medium |
| H-05 | Theme 切换后 RN 页面恢复 | 任一 RN Host 页面 | `ThemeChanged` 事件正确触发，初始 theme 与运行时 theme 一致 | high |
| H-06 | 返回销毁场景 | `destroyOnBack = true` 页面 | 返回时缓存销毁，重新进入时重新挂载成功 | medium |
| H-07 | ReactContext 未就绪场景 | 首开 RN Host 页面 | 加载态后正常进入，不出现永久 loading 或白屏 | high |

## 证据要求
### 必备证据
- 路由链路说明
- 触发命令或操作路径
- 设备/环境标签
- Build Variant
- 结果截图或日志片段归档路径
- 是否命中降级路径

### 推荐证据文件
- `docs/refactor/evidence/profile-host-first-open-<date>.png`
- `docs/refactor/evidence/settings-host-first-open-<date>.png`
- `docs/refactor/evidence/rn-host-theme-switch-<date>.png`
- `docs/refactor/phase-4/host-risk-run-<scenario>-<date>.md`

## 验证命令建议
- Android smoke：
  - `android/gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=...`
- RN contract / Jest：
  - `npm test -- --runInBand`
- 宿主页专项日志采集：
  - `adb logcat -d`

## 关闭条件
- 至少覆盖：
  - `profile`
  - `settings`
  - 一个作者/AI 宿主页场景
- 白屏风险、Theme 初始注入、Bridge 初始化时序均有正向证据
- 若存在降级路径，必须明确：
  - 触发条件
  - 用户可见表现
  - 是否阻塞 Phase 4 关闭

## 下一步建议
- `Wave 1 / Atomic Theme 04`
  - 输出四个超大类的原子拆分清单
- `Wave 2 / Atomic Theme 01`
  - 按本矩阵开始真实宿主页验证取证
