# Phase 1 - Benchmark 环境说明

## 目标
- 为 `V1-09` 明确 benchmark 依赖版本、目标构建形态、执行方式和运行前提。
- 让 Macrobenchmark 与 Baseline Profile 不再停留在“已接入但不够可执行”的状态。

## 当前模块
- 模块路径：`android/macrobenchmark`
- 目标应用模块：`:app`
- 主要用途：
  - 启动性能测试
  - 首页滚动性能测试
  - Baseline Profile 生成

## 已固定的关键依赖
| 依赖 | 版本 | 说明 |
| --- | --- | --- |
| `androidx.benchmark:benchmark-macro-junit4` | `1.3.2` | Macrobenchmark 主体 |
| `androidx.profileinstaller:profileinstaller` | `1.4.1` | Baseline Profile 安装器 |
| `androidx.test.uiautomator:uiautomator` | `2.3.0` | UIAutomator 依赖，已从 `<latest>` 固定为显式版本 |
| `androidx.test.ext:junit` | `1.1.5` | 测试 runner 依赖 |
| `androidx.test.espresso:espresso-core` | `3.5.1` | 基础 UI 测试能力 |

## 当前构建形态

### App 模块
- `release`
  - 已开启 `minifyEnabled`
  - 已开启 `shrinkResources`
  - 作为 benchmark 的 fallback 基准形态

### Macrobenchmark 模块
- `benchmark`
  - `debuggable true`
  - `matchingFallbacks = ['release']`
  - 目标是“以可调试方式驱动 release-like 应用”
- `release`
  - 当前仍 `debuggable true`
  - 主要用于 benchmark 模块自身执行和 profile 生成辅助

## 关键代码入口
- 启动与滚动测试：
  - `android/macrobenchmark/src/main/java/com/novel/macrobenchmark/ExampleStartupBenchmark.kt`
- Baseline Profile 生成：
  - `android/macrobenchmark/src/main/java/com/novel/macrobenchmark/BaselineProfileGenerator.kt`

## 当前覆盖的场景
- 冷启动
- 无编译/Partial/Full 编译模式启动
- 首页滚动
- 首页分类切换
- 榜单切换
- 书籍详情与阅读路径
- 搜索入口

## 执行前提
- 已安装目标应用或可由 Gradle 自动安装
- 设备在线并允许 instrumentation
- 优先使用真实设备
- 执行期间关闭明显干扰项：
  - 系统动画变更
  - 高负载后台任务
  - 电量极低模式

## 推荐命令

### 构建检查
```powershell
Set-Location android
.\gradlew.bat :macrobenchmark:assemble
```

### 连接设备执行
```powershell
Set-Location android
.\gradlew.bat :macrobenchmark:connectedCheck
```

### 生成 Baseline Profile
```powershell
Set-Location android
.\gradlew.bat :macrobenchmark:generateBaselineProfile
```

## 当前已知限制
- benchmark 脚本中的页面识别仍依赖现有首页/阅读器文本和可见元素，若 UI 结构发生变化需同步脚本。
- 当前尚未把 benchmark 结果自动写入 `docs/refactor/evidence/`。
- 尚未形成“benchmark 执行环境快照 + 输出归档”的固定模板。

## 下一步建议
- Phase 1：
  - 验证 `:macrobenchmark:assemble` 或 `:macrobenchmark:connectedCheck` 至少一轮通过
  - 把执行环境、设备和结果路径写入证据归档
- Phase 2：
  - 将 benchmark smoke 纳入 CI 或半自动执行清单
