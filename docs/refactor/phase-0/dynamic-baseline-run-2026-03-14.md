# Phase 0 - 动态基线采样记录（2026-03-14）

## 1. 采样环境
- Git 提交：`4bf41f0` 之后的 Phase 0 文档阶段，应用安装时工作树干净
- 设备厂商：`OnePlus`
- 设备型号：`DN2101`
- Android 版本：`13`
- 屏幕分辨率：`1080x2400`
- 屏幕密度：`480`
- 连接方式：`adb connect 192.168.8.115:5555`
- 安装包：`android/app/build/outputs/apk/debug/app-debug.apk`
- 启动 Activity：`com.novel/.ComposeMainActivity`

## 2. 冷启动 5 次采样

### 采样命令
```powershell
1..5 | ForEach-Object {
  adb -s 192.168.8.115:5555 shell am start -S -W com.novel/.ComposeMainActivity
}
```

### 结果
| 轮次 | LaunchState | TotalTime(ms) | WaitTime(ms) |
| --- | --- | ---: | ---: |
| 1 | COLD | 1181 | 1187 |
| 2 | COLD | 1161 | 1168 |
| 3 | COLD | 1519 | 1523 |
| 4 | COLD | 1106 | 1111 |
| 5 | COLD | 1240 | 1246 |

### 汇总
- TotalTime 中位数：`1181 ms`
- TotalTime 平均值：`1241.4 ms`
- 最小值：`1106 ms`
- 最大值：`1519 ms`

## 3. 启动后内存快照

### 采样命令
```powershell
adb -s 192.168.8.115:5555 shell dumpsys meminfo com.novel
```

### 关键结果
- Top Activity：`com.novel/.ComposeMainActivity`
- `TOTAL PSS`：`211935 KB`
- `TOTAL RSS`：`347728 KB`
- `Java Heap`：`89560 KB`
- `Native Heap`：`27384 KB`
- `Graphics`：`20036 KB`

## 4. 首页滚动粗基线

### 采样命令
```powershell
adb -s 192.168.8.115:5555 shell dumpsys gfxinfo com.novel reset
1..3 | ForEach-Object {
  adb -s 192.168.8.115:5555 shell input swipe 540 1900 540 700 300
}
adb -s 192.168.8.115:5555 shell dumpsys gfxinfo com.novel
```

### 采用的有效统计块
- 以 reset 后的第二段统计为准

### 结果
- `Total frames rendered`：`419`
- `Janky frames`：`0 (0.00%)`
- `Janky frames (legacy)`：`26 (6.21%)`
- `50th percentile`：`14 ms`
- `90th percentile`：`16 ms`
- `95th percentile`：`18 ms`
- `99th percentile`：`22 ms`
- `Number High input latency`：`800`
- `Number Frame deadline missed`：`0`

## 5. 可脚本化路径验证结果

### 已验证
- 首页可通过 `uiautomator dump` 稳定导出层级。
- 首页第一本书可通过固定坐标点击进入书详情页。
- 书详情页主要文本内容可稳定导出层级。
- 从首页底部“继续观看”区域点击后，可通过日志确认进入 `ReaderPage`，当前落在阅读器虚拟第一页 `BookDetailPage`。

### 暂未闭环
- 阅读器虽然已确认可进入，但自动化尚未稳定把虚拟第一页推进到正文 `ContentPage`，翻页路径仍待补采。
- RN “我的”页已经在真机上视觉渲染出实际内容，但 `uiautomator dump` 只能拿到宿主容器，尚未形成可稳定识别的元素级自动化采样路径，因此 RN 首开基线暂未闭环。

### RN 调试链路附加证据
- 通过 `adb logcat -d` 抓到多条：
  - `ReactNativeJNI: Failed to connect to localhost/127.0.0.1:8081`
- 即使补充执行了：
  - `adb reverse tcp:8081 tcp:8081`
- 当前判断是：
  - RN 页面可以视觉渲染。
  - 但无线真机 + 当前调试链路下，UIAutomator 无法稳定拿到 RN 页面内部元素，且 ReactNativeJNI 存在持续 websocket 连接失败日志。
  - 因此本轮不将 RN 首开时间记为有效自动化基线。

### RN 直达路由复验
- 已通过 `debug_route=profile` 直达 RN 根宿主页：
  - `am start -W` 冷启动耗时约 `1576 ms`
  - 日志可见 `React上下文就绪，启动应用: Novel`
  - 但等待 `22s` 后截图仍为白屏
- 已通过 `debug_route=settings` 直达 RN Host 页面：
  - 等待 `18s` 后截图仍为白屏
- 结论：
  - 当前无线真机 + debug 组合下，RN 路由具备启动与上下文初始化日志，但视觉渲染不稳定
  - `RN 首开动态基线` 仍不能作为有效完成项关闭

### RN 补充排查结论
- 本机 `Metro` 状态：
  - `http://127.0.0.1:8081/status -> packager-status:running`
- 设备缓存 bundle：
  - `/data/data/com.novel/files/BridgeReactNativeDevBundle.js`
  - 大小约 `9.8 MB`
  - 本地检索可见 `Novel`、`SettingsPageComponent`、`BookshelfPageComponent` 等注册信息
- 运行时错误：
  - 当前 `logcat` 未抓到明确 `ReactNativeJS` fatal / `Invariant Violation` / `TypeError` 级别错误
- 综合判断：
  - RN 白屏并非简单的“Metro 未启动”或“bundle 未注册组件”问题
  - 更可能是当前无线真机 debug 运行时与宿主渲染链路的稳定性问题

### RN “我的”页面当前视觉基线
- 当前设备状态下抓取到“我的”页面视觉截图：
  - `docs/refactor/evidence/profile-page-current-2026-03-15.png`
- 页面中可见内容包括：
  - 顶部登录/注册占位
  - `我的消息`、`成为作家`、`浏览历史`、`排行榜中心`
  - 两个“继续阅读”卡片
  - 底部 LogBox 提示 `Open debugger to view warnings.`

### RN “我的”页面滚动粗基线

#### 采样方式
- 在当前已展示的“我的”页面执行 3 轮上下滑动
- 使用 `dumpsys gfxinfo com.novel reset` 后采样

#### 结果
- 有效统计块：
  - `Total frames rendered`: `312`
  - `Janky frames`: `29 (9.29%)`
  - `50th percentile`: `21 ms`
  - `90th percentile`: `25 ms`
  - `95th percentile`: `26 ms`
  - `99th percentile`: `36 ms`

#### 当前内存快照
- “我的”页当前驻留时：
  - `TOTAL PSS`: `389690 KB`
  - `TOTAL RSS`: `538764 KB`
  - `Native Heap`: `161728 KB`
  - `Java Heap`: `81556 KB`

### 阅读器链路附加证据
- 通过 `adb logcat -d` 已确认以下日志：
  - `ReaderViewModel: 分页完成后写入页数缓存: 总页数=578`
  - `PageCurlFlipContainer: virtualPages: [BookDetailPage, ContentPage(...)] , virtualPageIndex: 0`
- 结论：
  - “继续观看”路径已经进入 `ReaderPage`
  - 当前停在阅读器的虚拟书详情页，不是普通书详情路由页
  - 翻页进入正文页的自动化手势仍需继续补采

### Debug 路由与语义标识附加证据
- 已新增仅 `debug` 生效的路由注入能力，可通过以下命令直达阅读器：
```powershell
adb shell am start -S -W -n com.novel/.ComposeMainActivity --es debug_route "reader/1334318497132552192?chapterId=1334318500051787776"
```
- 已进一步使用第二章 `chapterId=1334318502731948032` 直达正文页，绕开虚拟书详情页。
- 通过 `uiautomator dump` 已确认阅读器根节点可见：
  - `content-desc="阅读页面"`
- 通过第二章直达路径，已确认正文页可见内容：
  - 章节标题：`第一章 想要个孩子？`
  - 页码：`8 / 578`
- 结论：
  - 阅读器入口不再依赖首页/书详情点击路径
  - 后续正文翻页与 Smoke 可直接复用该 debug 路由

### 阅读器正文翻页首轮采样

#### 采样命令
```powershell
adb shell am start -S -W -n com.novel/.ComposeMainActivity --es debug_route "reader/1334318497132552192?chapterId=1334318502731948032"
adb shell input swipe 900 1400 220 1400 300
```

#### 页码结果
| 轮次 | 页码 |
| --- | --- |
| 初始页 | `8 / 578` |
| 翻页 1 次后 | `9 / 578` |
| 翻页 2 次后 | `10 / 578` |
| 翻页 3 次后 | `11 / 578` |

#### `gfxinfo` 粗结果
- 以 reset 后统计块为参考：
  - `Total frames rendered`: `31`
  - `Janky frames`: `30 (96.77%)`
  - `50th percentile`: `150 ms`
  - `90th percentile`: `200 ms`
  - `95th percentile`: `300 ms`
  - `99th percentile`: `350 ms`

#### 阅读器内存快照
- `TOTAL PSS`: `316213 KB`
- `TOTAL RSS`: `462076 KB`
- `Java Heap`: `64772 KB`
- `Native Heap`: `92792 KB`
- `Graphics`: `50616 KB`

## 6. 本地构建时长

### 采样命令
```powershell
$duration = Measure-Command { Set-Location android; .\gradlew.bat app:assembleDebug | Out-Host }
```

### 结果
- `app:assembleDebug` 首轮耗时：`62.68 s`
- 构建结果：`BUILD SUCCESSFUL`

## 7. 当前结论
- 本轮已经拿到真实设备上的冷启动、内存和首页滚动粗基线，可作为 Phase 0 的第一轮动态证据。
- 构建时长也已补齐首轮数据。
- 阅读器正文页已可通过 debug 路由稳定进入，并完成首轮翻页、`gfxinfo` 与内存采样。
- RN 当前页面视觉与滚动基线已补齐，但 `RN 首开动态基线` 仍未稳定闭环。
- 在这些指标补齐之前，`V0-03` 仍应保持 `in_progress`，不能直接关闭。
