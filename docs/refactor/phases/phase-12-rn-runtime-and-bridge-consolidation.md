# Phase 12 - RN 运行时与桥接收口

## 目标
- 把 RN 侧的启动初始化、bridge 包装、native event、页面注册和返回策略收成统一入口层。

## 范围
- `App.tsx`
- `index.js`
- `src/utils/appInit.ts`
- `src/utils/bridge/**`
- `src/utils/nativeEventListener.ts`
- `src/utils/theme/themeStore.ts`
- 页面中的 `NativeModules` / `DeviceEventEmitter` / `BackHandler` 直连收口

## 非目标
- 不按页面域全面重排目录
- 不修改对外 bridge / route / component 注册语义
- 不进行 RN -> Compose 迁移

## 当前仓库入口基线
- `appInit.ts` 目前混合：
  - app init
  - 主题同步
  - 用户预加载
  - 设置预加载
  - 页面状态缓存
  - RN 页面组件导入注册
- `NavigationBridge` / `UserBridge` 已存在包装层，但很多页面与 store 仍直接触原生原语。
- `themeStore` 和 `nativeEventListener` 当前同时承担 event hub 职责。

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P12.1 | 固定 RN runtime coordinator 宿主 | `appInit` 职责拆分边界清楚 |
| P12.2 | 固定 bridge gateway 宿主 | `NavigationBridge` / `UserBridge` 与原生原语边界清楚 |
| P12.3 | 固定 event hub 宿主 | `DeviceEventEmitter` 使用面可追溯 |
| P12.4 | 固定 back navigation policy | 页面返回策略统一 |
| P12.5 | 输出 Phase 12 closeout 宿主 | 运行时入口层关闭 |

## 交付物
- `rn-runtime-coordinator-2026-03-31.md`
- `rn-bridge-gateway-2026-03-31.md`
- `rn-event-hub-2026-03-31.md`
- `rn-back-navigation-policy-2026-03-31.md`

## 当前状态
- `planned`
