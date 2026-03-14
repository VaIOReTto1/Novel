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

### 暂未闭环
- 阅读器入口“开始阅读”按钮在本轮脚本导出中未稳定出现，尚未形成可靠自动化采样路径。
- RN “我的”页在当前 debug 测试路径下导出的仍是宿主容器，未形成可稳定识别的页面元素，因此 RN 首开基线暂未闭环。

### RN 调试链路附加证据
- 通过 `adb logcat -d` 抓到多条：
  - `ReactNativeJNI: Failed to connect to localhost/127.0.0.1:8081`
- 即使补充执行了：
  - `adb reverse tcp:8081 tcp:8081`
- 当前无线真机上的 RN 页面仍未形成稳定可见内容，因此本轮不将 RN 首开数据记为有效基线。

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
- 阅读器翻页与 RN 首开仍需继续补采。
- 在这些指标补齐之前，`V0-03` 仍应保持 `in_progress`，不能直接关闭。
