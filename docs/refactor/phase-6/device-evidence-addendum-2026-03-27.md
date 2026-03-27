# Phase 6 Device Evidence Addendum - 2026-03-27

## 摘要
- 日期：`2026-03-27`
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建：`debug / com.novel / versionName=1.0.2`
- 目标：补齐 `Phase 6` 在 `2026-03-27` 这轮优化后的设备侧样本，不把结论只留在本机单测或文档推断里。

## 一、Startup
### 命令
- `adb shell am force-stop com.novel`
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity`
- `adb logcat -d | Select-String "StartupPerformanceMonitor|ComposeMainActivity: trigger=|NavigationSetup|Displayed com.novel/.ComposeMainActivity"`

### 实际结果
- `ActivityTaskManager` 显示 `ComposeMainActivity`：`1.751s`
- `StartupPerformanceMonitor`
  - `Application onCreate = 29ms`
  - `首帧绘制 = 1019ms`
  - `完全加载 = 1086ms`
  - `ThemeManager = 5ms`
  - `SoLoader = 23ms`
- `ComposeMainActivityFirstFrameCoordinator`
  - 首帧后记录到 `trigger=prewarm_after_first_frame reactContextPath=COLD_OPEN`
  - 紧接着记录到 `trigger=create_react_context_in_background reactContextPath=COLD_OPEN`

### 结论
- 首帧后 gate-driven prewarm 已在真机上留下清晰样本。
- 当前仍保留“首帧渲染时间较长”的监控建议，因此 Startup 线是“样本补齐 + 优化继续可做”，不是“已经没有空间”。

### 证据
- `docs/refactor/evidence/phase6-startup-logcat-2026-03-27.txt`

## 二、Search
### 命令
- `adb shell am force-stop com.novel`
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route "search_result?query=的"`
- `adb shell input tap 220 380`
- `adb shell input tap 975 380`
- `adb shell input tap 220 920`
- `adb shell input tap 720 2120`
- `adb logcat -d | Select-String "SearchResultViewModel: phase=finish action=search|NavigationSetup"`

### 实际结果
- `INITIAL_ENTRY`
  - `query=的`
  - `durationMs=82`
  - `resultCount=15`
  - `hasMore=false`
- `CATEGORY_SWITCH`
  - `durationMs=62`
  - `resultCount=1`
  - `hasMore=false`
- `FILTER_APPLY`
  - `durationMs=253`
  - `resultCount=0`
  - `hasMore=false`

### 分页探针
- 额外对 `的 / 天 / 王 / 都市` 四组 query 做了真实样本探针。
- 四组样本都返回 `hasMore=false`，因此今天没有拿到可信的 `LOAD_MORE` 设备样本。
- 当前这条缺口来自真实样本集返回，而不是 trace 未接通。

### 结论
- Search 热点动作现在已经有：
  - 首开
  - 分类切换
  - 筛选应用
  的设备样本。
- `LOAD_MORE` 仍是残余缺口，需要后续换 query / 数据集或补专用探针。

### 证据
- `docs/refactor/evidence/search-hot-actions-logcat-2026-03-27.txt`
- `docs/refactor/evidence/search-load-more-probe-2026-03-27.txt`

## 三、Welfare / WebView
### 命令
- `adb shell am force-stop com.novel`
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity`
- `adb shell input tap 540 2140`
- `adb shell input tap 110 2140`
- `adb shell input tap 540 2140`
- `adb logcat -d | Select-String "WelfarePage|WelfarePerformanceMonitor|WebViewComponent|InitializeWelfarePageUseCase|WelfareViewModel"`

### 首开样本
- `InitializeWelfarePageUseCase` 耗时：`320ms`
- `页面加载完成`：`156ms`
- `https://cn.bing.com/`
  - `TTI = 100ms`
  - `WebView load = 131ms`
  - `FCP = 345ms`

### 复开 / 回退复用样本
- 日志明确出现：
  - `开始保存WebView状态`
  - `WebView状态已恢复`
  - `传入URL与当前URL相同，无需重新加载`
- 复开后样本：
  - `页面加载完成 = 0ms`
  - `TTI = 452ms`
  - `WebView load = 113ms`
  - `FCP = 691ms`

### 结论
- Welfare 当前已经不只是“有埋点”，而是有一条完整的：
  - 首开
  - 返回首页
  - 再进福利
  的设备样本链。
- WebView state restore 与 reopen 语义已可追溯，但 FCP / TTI 仍存在样本波动，后续仍值得继续收敛。

### 证据
- `docs/refactor/evidence/welfare-webview-performance-logcat-2026-03-27.txt`

## 四、RN Host
### 命令
#### `COLD_OPEN`
- `adb shell am force-stop com.novel`
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route profile`

#### `OPEN / REUSED`
- `adb shell am force-stop com.novel`
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity`
- 预热完成后：
  - 点击 `书架`
  - 返回 `首页`
  - 再点 `书架`
  - 再点 `我的`

### 实际结果
#### `COLD_OPEN`
- `component=Novel reactRootViewPath=COLD_OPEN reactContextPath=COLD_OPEN`
- 约 `4.56s` 后出现：
  - `React context ready, start application: Novel`
  - `theme synced to RN: light for Novel`

#### `OPEN`
- `component=BookshelfPageComponent reactRootViewPath=OPEN reactContextPath=ALREADY_READY`
- `component=Novel reactRootViewPath=OPEN reactContextPath=ALREADY_READY`
- 两条路径都记录到：
  - `start theme sync to RN`
  - `theme synced to RN: light`

#### `REUSED`
- 二次进入 `书架` 时记录到：
  - `component=BookshelfPageComponent reactRootViewPath=REUSED reactContextPath=ALREADY_READY`

### 结论
- `COLD_OPEN / OPEN / REUSED` 现在已经有当天设备样本，而不是只有单测与设计意图。
- `ThemeManager` 实际主题优先同步给 RN 的规则，也在同一批样本里留下了 `theme synced to RN: light` 的运行时证据。
- 由于 `debug_route` 只在 `onCreate` 读取，本轮 `OPEN / REUSED` 使用主页面内导航而不是重复 `am start`，这是当前正确取证方式。

### 证据
- `docs/refactor/evidence/rn-host-path-logcat-2026-03-27.txt`

## 五、Reader
### 命令
- `adb shell am force-stop com.novel`
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route "reader/1334318497132552192?chapterId=1334318500051787776"`
- `adb shell input swipe 900 1200 180 1200 300`
- `adb logcat -d | Select-String "ReaderPerfProbe|ReaderPage|ReaderViewModel"`

### 实际结果
- `settings_update`
  - `durationMs=5`
  - `budgetMs=400`
  - `budgetStatus=within`
- `init`
  - `durationMs=820`
  - `budgetMs=1200`
  - `budgetStatus=within`
- 本次 swipe 仍未抓到可信的 `flip` 直接样本。

### 结论
- Reader 至少已经有了当天设备侧的：
  - `settings_update`
  - `init`
  样本。
- `flip` 仍是残余缺口，需要后续更稳定的交互脚本或 debug-only probe。

### 证据
- `docs/refactor/evidence/reader-performance-logcat-2026-03-27.txt`

## 总结
- 本轮新增设备样本已覆盖：
  - Startup 关键路径
  - Search 首开 / 分类切换 / 筛选应用
  - Welfare / WebView 首开与复开
  - RN Host `COLD_OPEN / OPEN / REUSED`
  - Reader `init / settings_update`
- 当前仍未关闭的设备侧缺口：
  - Search `LOAD_MORE`
  - Reader `flip`
- 这两项都应继续保留在 `Phase 6` backlog，而不是误转交给 `Phase 7`。
