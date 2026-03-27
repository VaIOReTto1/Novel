# RN Host Root View Cache Policy - 2026-03-27

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 6 / RN Host backlog 深化`
- 当前结论：`返回时缓存语义已从隐含规则升级为显式 policy`

## 目的
- 把 RN Host 页面“返回时到底要不要清掉 root view cache”的语义，从散落在 `destroyOnBack` 布尔值和调用方约定里的隐含规则，升级成显式策略。
- 让 `COLD_OPEN / OPEN / REUSED` 这套路径语义，不只停留在 trace 上，也有实际缓存处理规则。

## 当前策略
| Policy | 含义 | 典型页面 |
| --- | --- | --- |
| `CLEAR_COMPONENT_CACHE` | 返回时清理当前组件缓存，下一次进入重新创建 root view | `SettingsPageComponent`、`TimedSwitchPageComponent` 这类 `destroyOnBack=true` 页面 |
| `RETAIN_COMPONENT_CACHE` | 返回时保留当前组件缓存，允许后续 warm / reused 复用 | `Novel`、`BookshelfPageComponent` 这类可复用宿主页 |

## 当前实现入口
- `android/core-bridge/src/main/java/com/novel/rn/bridge/BridgeMvi.kt`
  - 新增 `BridgeComponentCachePolicy`
  - `BridgeIntent.NavigateBack` 现在显式携带 `cachePolicy`
- `android/feature-rn-host/src/main/java/com/novel/rn/ReactRootViewBackNavigationPolicyCoordinator.kt`
  - 负责把 `destroyOnBack` 收敛成 `NavigateBack` intent
- `android/feature-rn-host/src/main/java/com/novel/rn/bridge/BridgeViewModel.kt`
  - 只有在 `CLEAR_COMPONENT_CACHE` 时才真正清缓存
- `android/app/src/main/java/com/novel/rn/ReactNativePage.kt`
  - 通过 policy coordinator 统一生成 back-navigation intent

## 当前意义
- 这次不是改 route、componentName 或 JS bridge API。
- 这次收口的是：返回时的 root view cache 语义终于被类型化，后续 review 和证据文档都可以引用同一套术语。

## 当前残余 debt
- 这份 policy 目前主要覆盖宿主页“返回”路径，尚未扩展成完整的 cache 生命周期总规范。
- JS 主动触发的 `NavigationBridge.navigateBack(componentName)` 仍保持现有兼容语义，不在这次变更里扩大范围。

## 主要引用
- `android/core-bridge/src/main/java/com/novel/rn/bridge/BridgeMvi.kt`
- `android/feature-rn-host/src/main/java/com/novel/rn/ReactRootViewBackNavigationPolicyCoordinator.kt`
- `android/feature-rn-host/src/main/java/com/novel/rn/bridge/BridgeViewModel.kt`
- `android/app/src/main/java/com/novel/rn/ReactNativePage.kt`
- `android/feature-rn-host/src/test/java/com/novel/rn/ReactRootViewBackNavigationPolicyCoordinatorTest.kt`
- `android/feature-rn-host/src/test/java/com/novel/rn/bridge/BridgeViewModelTest.kt`
