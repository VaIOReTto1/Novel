# Phase 4 BridgeFacade 与 Delegate 映射表

## 目标
- 为 `Wave 1 / Atomic Theme 02` 提供 `BridgeFacade` 的唯一拆分参考。
- 在不改变 route / event / payload / Promise 语义的前提下，明确 `NavigationBridgeModule` 应如何拆成 facade + delegates。

## 当前问题
- `NavigationBridgeModule` 既承担 RN 可见桥接出口，又承担：
  - 导航路由
  - 宿主页缓存管理
  - 读取型查询
  - AI 与作者操作
  - 主题/设置/Promise 协调
  - Selection Menu 事件发射
- 这导致：
  - 协议兼容点无法单独验证
  - 宿主页风险与纯导航耦合
  - `BridgeFacade` 无法成为唯一出口

## 目标结构
### BridgeFacade
- 建议职责：
  - 保留 RN 对外入口名称与签名兼容
  - 只做参数接收、delegate 路由、统一错误映射、兼容壳保留
- 建议位置：
  - `com.novel.rn.bridge.facade.NavigationBridgeFacade`

### Delegate 切分
| Delegate | 目标职责 | 当前来源方法 | 默认 Owner |
| --- | --- | --- | --- |
| `SelectionMenuDelegate` | Android 选择菜单挂载与事件发射 | `attachSelectionMenu`, `detachSelectionMenu` | `BridgeFacadeSplitAgent` |
| `NavigationRouteDelegate` | 纯导航类路由跳转 | 所有 `navigateTo*`, `goToLogin`, `navigateBack` | `BridgeFacadeSplitAgent` |
| `NavigationQueryDelegate` | 查询类 Promise / Callback 桥接 | `getHomeBooksHighPriority`, `getReadingHistory`, `getAuthorStatus`, `getAuthorBooks`, `getBookCategories`, `searchBooks`, `getBridgeStatus`, `getCurrentActualTheme`, `getCurrentNightMode` | `BridgeFacadeSplitAgent` |
| `NavigationAiDelegate` | AI 相关 Promise 桥接 | `aiPolish`, `aiExpand`, `aiCondense`, `aiContinue` | `BridgeFacadeSplitAgent` |
| `NavigationAuthorDelegate` | 作者注册与写作入口相关能力 | `registerAuthor`, `navigateToBecomeWriter*`, `navigateToWritePage`, `navigateToBookManage` | `BridgeFacadeSplitAgent` |
| `NavigationHostDelegate` | 组件缓存、组件注册、Route 通知 | `clearComponentCache`, `clearAllComponentCache`, `registerComponent`, `notifyRouteChanged` | `HostRiskQualityAgent` |
| `NavigationThemeDelegate` | 主题与 Settings Promise 协调 | `changeTheme`, `observeEffectForPromise` | `BridgeFacadeSplitAgent` |

## 兼容规则
### 不允许变化
- `NavigationBridge` 模块名不变
- 所有 RN 侧调用方法名不变
- Promise resolve/reject 字段语义不变
- DeviceEventEmitter 事件名不变：
  - `ThemeChanged`
  - `WritePageSelectionMenuAction`

### 允许的内部变化
- `NavigationBridgeModule` 可退化为 facade 壳
- 具体实现可下沉到 delegates
- 错误映射统一经过现有 `BridgePromiseErrorMapper`
- route 跳转内部可通过 delegate 转发，但对外 route 语义保持不变

## 宿主页链路事实表
| 环节 | 当前文件 | 风险点 | Phase 4 处理方式 |
| --- | --- | --- | --- |
| Route 分发 | `NavigationUtil.kt` | RN 页面入口分散，宿主页链路长 | 只做链路梳理，不改 route 语义 |
| RN 页面容器 | `ReactNativePage.kt` | 宿主页缓存、Theme 注入、上下文就绪时序 | 作为 `NavigationHostDelegate` 的事实来源 |
| Bridge 出口 | `NavigationBridgeModule.kt` | 桥接职责混杂 | 收口到 `BridgeFacade` |
| Theme 事件 | `ThemeManager.kt`, `SettingsViewModel.kt` | 事件生产者分散 | 只补兼容映射，不改事件语义 |

## 推荐落地顺序
1. 先新增 facade / delegate 文档映射，不动代码行为
2. 再把 `NavigationBridgeModule` 拆成：
  - facade 壳
  - `NavigationRouteDelegate`
  - `NavigationQueryDelegate`
3. 再拆：
  - `NavigationHostDelegate`
  - `SelectionMenuDelegate`
4. 最后收口：
  - `NavigationAiDelegate`
  - `NavigationThemeDelegate`
  - `NavigationAuthorDelegate`

## 推荐验证项映射
| 验证项 | 证据 |
| --- | --- |
| V4-03 | `BridgeFacade` 接口草图、delegate 映射表、旧协议兼容表、Bridge contract 回归 |
| V4-05 | `ReactNativePage` / `NavigationUtil` / host 挂载链、初始化时序、白屏/降级路径 |

## 下一步建议
- `Wave 1 / Atomic Theme 03`
  - 输出宿主页挂载与风险验证矩阵
- `Wave 2 / Atomic Theme 01`
  - 先做 `NavigationBridgeModule` facade 壳与 `NavigationRouteDelegate` 的代码级最小拆分
