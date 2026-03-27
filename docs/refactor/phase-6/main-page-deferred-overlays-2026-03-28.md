# Main Page Deferred Overlays - 2026-03-28

## 状态
- 生效日期：`2026-03-28`
- 关联阶段：`Phase 6 / Startup 首帧继续收敛`
- 当前结论：`主页面首帧上的非关键 overlay 已延后到首帧后`

## 目的
- 把 `MainPage` 首帧上的非关键 UI 负担再压一层：
  - 短剧 toast
  - 启动弹窗判定
- 这部分不属于“用户打开 app 第一帧必须看到”的内容，适合延后到首帧后再揭示。

## 当前实现
- 新增：
  - `MainPageStartupUiCoordinator`
- 当前策略：
  - 初始状态不立即显示短剧 toast
  - 初始状态不立即读取 `DialogLaunchManager`
  - 在首帧后再 reveal deferred UI

## 当前代码入口
- `android/app/src/main/java/com/novel/page/MainPage.kt`
- `android/app/src/main/java/com/novel/page/MainPageStartupUiCoordinator.kt`
- `android/app/src/test/java/com/novel/page/MainPageStartupUiCoordinatorTest.kt`

## 当前验证
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :app:testDebugUnitTest --tests com.novel.page.MainPageStartupUiCoordinatorTest`
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :app:compileDebugKotlin`

## 当前限制
- 当前代码已落地，但 `2026-03-28` 无线 adb 在安装最新 debug APK 时发生：
  - `failed to read copy response: EOF`
  - `已建立的连接被主机中的软件中止`
- 因此这条优化的真机前后对比样本尚未补到 `perf-multisample` 中。

## 当前结论
- 这条“继续收敛首帧”的代码优化已经真正落地。
- 剩下阻塞的是：
  - 将最新 debug 包稳定装到 `DN2101`
  - 再做前后对比量化
