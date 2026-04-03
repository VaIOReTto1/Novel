# Stage 7 Showcase Runbook

## 目标
- 为 `V17-03 showcase infrastructure` 提供统一的可运行入口说明，避免 Android route 与 web showcase 只存在于代码实现中而无人可复现。

## Web Showcase
- 启动命令：

```bash
npm run web
```

- 访问方式：
  - 默认：`http://localhost:8080/`
  - Showcase：`http://localhost:8080/?novelDesignShowcase=1`

- 当前实现：
  - 入口开关：`src/web/webEntryConfig.ts`
  - showcase 页面：`src/design-system/showcase/NovelDesignShowcase.tsx`
  - web shim：`src/web/shims/reactNativeReanimated.ts`

## Android Showcase
- 当前 compose route：`novel_design_showcase`
- 入口 Activity：`com.novel.ComposeMainActivity`

- 调起示例：

```bash
adb shell am start -n com.novel/.ComposeMainActivity --es debug_route novel_design_showcase
```

- 当前实现：
  - route 接线：`android/app/src/main/java/com/novel/utils/NavigationUtil.kt`
  - showcase 页面：`android/core-ui/src/main/java/com/novel/ui/showcase/NovelDesignShowcaseScreen.kt`
  - showcase model：`android/core-ui/src/main/java/com/novel/ui/showcase/NovelDesignShowcaseModel.kt`

## 最小验证
- `npm test -- --runInBand __tests__/web/webEntryConfig.test.ts`
- `npm test -- --runInBand __tests__/design-system/NovelDesignShowcase.test.tsx`
- `npm test -- --runInBand __tests__/harness/androidNovelDesignPages.test.js`
- `cd android && ..\\android\\gradlew.bat app:compileDebugAndroidTestKotlin --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"`

## 当前限制
- web 侧当前是 showcase 入口，不是独立 Storybook 站点。
- Android 侧当前通过 debug route 进入，不是主导航常驻入口。
