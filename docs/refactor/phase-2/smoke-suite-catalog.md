# Phase 2 Smoke Suite Catalog

## 鐩爣
- 涓?`V2-05` 寤虹珛鍙墽琛岀殑鏍稿績璺緞 smoke 濂椾欢銆?- 瑕嗙洊棣栭〉銆佺櫥褰曘€佹悳绱€侀槄璇诲櫒銆佽缃€佸啓浣滈〉锛屼互鍙婂悗缁柊澧炵殑 AI 鍐欎綔鍔╂墜鍜屼綔鍝佺鐞嗛〉銆?- 鏂█鍙繚鐣欌€滃彲娓叉煋銆佸彲杩涘叆銆佸叧閿粨鏋勫瓨鍦ㄢ€濅竴绾э紝浼樺厛闄嶄綆 flake銆?
## 褰撳墠 Smoke 瑕嗙洊
| Path | Layer | Test File | Command |
| --- | --- | --- | --- |
| 棣栭〉 | Android Compose | `android/app/src/androidTest/java/com/novel/page/home/HomeSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.home.HomeSmokeTest` |
| 鐧诲綍 | Android Compose | `android/app/src/androidTest/java/com/novel/page/login/LoginSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.login.LoginSmokeTest` |
| 鎼滅储 | Android Compose | `android/app/src/androidTest/java/com/novel/page/search/SearchSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.search.SearchSmokeTest` |
| 闃呰鍣?| Android Compose | `android/app/src/androidTest/java/com/novel/page/read/viewmodel/ReaderSmokeTest.kt` | `cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.read.viewmodel.ReaderSmokeTest` |
| 璁剧疆 | RN Jest render | `__tests__/smoke/SettingsPage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx` |
| 鍐欎綔椤?| RN Jest render | `__tests__/smoke/WritePage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/WritePage.smoke.test.tsx` |
| 涔︽灦澹冲眰 | RN Jest render | `__tests__/smoke/BookshelfPage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/BookshelfPage.smoke.test.tsx` |
| 涔﹁瘎骞垮満 | RN Jest render | `__tests__/smoke/CommentPage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/CommentPage.smoke.test.tsx` |
| 涔﹁瘎璇︽儏 | RN Jest render | `__tests__/smoke/ReviewDetailPage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/ReviewDetailPage.smoke.test.tsx` |
| 浼氬憳涓績 | RN Jest render | `__tests__/smoke/MemberCenterPage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/MemberCenterPage.smoke.test.tsx` |
| AI 鍐欎綔鍔╂墜 | RN Jest render | `__tests__/smoke/AIWriteAssistant.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/AIWriteAssistant.smoke.test.tsx` |
| 浣滃搧绠＄悊 | RN Jest render | `__tests__/smoke/BookManagePage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/BookManagePage.smoke.test.tsx` |
| 涔︽灦鍐呴〉 | RN Jest render | `__tests__/smoke/BookshelfNestedPage.smoke.test.tsx` | `npm test -- --runInBand --runTestsByPath __tests__/smoke/BookshelfNestedPage.smoke.test.tsx` |

## 褰撳墠绋冲畾鍏ュ彛璇存槑
- `HomeSmokeTest`
  - 浣跨敤 `HomePageSkeleton()` 浣滀负绋冲畾 smoke 鍏ュ彛锛岄伩鍏嶇洿鎺ヤ緷璧?Hilt Activity 瀹瑰櫒銆?- `LoginSmokeTest`
  - 浣跨敤 `LoginPageSkeleton()` 浣滀负绋冲畾 smoke 鍏ュ彛锛屼紭鍏堥獙璇佺櫥褰曢〉涓荤粨鏋勫彲娓叉煋銆?- `ReaderSmokeTest`
  - 浣跨敤 `NoAnimationContainer()` 浣滀负鏃?Hilt 渚濊禆鐨勯槄璇诲櫒鍐呭瀹瑰櫒鍏ュ彛銆?- `SearchSmokeTest`
  - 浣跨敤 `SearchPageContent()` 浣滀负鎼滅储椤甸潰鏍稿績 Compose 鍐呭 smoke銆?- `SettingsPage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇佽缃〉涓荤粨鏋勩€佸垵濮嬪寲閾捐矾涓庢牳蹇?section 鏂囨銆?- `WritePage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇佸啓浣滈〉椤舵爮鍜岀紪杈戝鍣ㄥ彲鏃犲穿婧冩覆鏌撱€?- `BookshelfPage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇佷功鏋跺涓?shell 鍜屽洓涓?tab 鍏ュ彛鏂囨鍙覆鏌撱€?- `CommentPage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇佽瘎璁洪〉椤舵爮銆佽瘎鍒嗗尯銆佸垎绫诲尯涓庤瘎璁哄垪琛ㄥ３灞傚彲娓叉煋銆?- `ReviewDetailPage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇佷功璇勮鎯呴〉椤舵爮涓庤瘎璁虹嚎绋嬪３灞傚彲娓叉煋銆?- `MemberCenterPage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇佷細鍛樹腑蹇冧富瑕佸崱鐗囧尯銆佹潈鐩婂尯銆佷环鏍煎尯鍜岃喘涔版潯澹冲眰鍙覆鏌撱€?- `AIWriteAssistant.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇?AI 鍐欎綔鍔╂墜鐨勯《閮ㄥ姪鎵嬫爮銆佸簳閮ㄦā寮忔寜閽笌杈撳叆鏍忓彲娓叉煋銆?- `BookManagePage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇佷綔鍝佺鐞嗛〉鐨勪功绫?banner銆佽崏绋块€氶亾銆佺珷鑺傚尯涓庡簳閮?CTA 鍙覆鏌撱€?- `BookshelfNestedPage.smoke.test.tsx`
  - 浣跨敤 RN render smoke锛岄獙璇?Bookshelf 鍐呭祵椤典細鍒涘缓涓€浠藉叡浜牱寮忓疄渚嬪苟閫忎紶缁?TopBar銆丒ditToolbar 鍜?UnifiedScrollView銆?
## 鎺ㄨ崘鎵ц鏂瑰紡
- RN smoke
```bash
npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx __tests__/smoke/WritePage.smoke.test.tsx __tests__/smoke/AIWriteAssistant.smoke.test.tsx __tests__/smoke/BookManagePage.smoke.test.tsx
```

- 鎵╁睍 RN smoke
```bash
npm test -- --runInBand --runTestsByPath __tests__/smoke/BookshelfPage.smoke.test.tsx __tests__/smoke/BookshelfNestedPage.smoke.test.tsx __tests__/smoke/CommentPage.smoke.test.tsx __tests__/smoke/ReviewDetailPage.smoke.test.tsx __tests__/smoke/MemberCenterPage.smoke.test.tsx
```

- Android smoke
```bash
cd android && ./gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.novel.page.home.HomeSmokeTest,com.novel.page.login.LoginSmokeTest,com.novel.page.search.SearchSmokeTest,com.novel.page.read.viewmodel.ReaderSmokeTest
```

## 鍚庣画鎵╁睍
- 灏?smoke 鍛戒护鎺ュ叆 `.github/workflows/`锛屼綔涓?`V2-06` 鐨?Android smoke job銆?- 涓?RN 椤甸潰缁х画琛ュ厖锛?  - `BookshelfPageComponent`
  - `HistoryPageComponent`
  - `CategoryPageComponent`
- 涓?Android smoke 澧炲姞鎴浘/褰曞儚褰掓。妯℃澘锛屾敮鎾?`V2-08` 璇佹嵁鏍囧噯鍖栥€?

- WriteReviewPage.smoke.test.tsx`r
  - 浣跨敤 RN render smoke锛岄獙璇佸啓涔﹁瘎椤甸《閮ㄦ爮銆佽瘎鍒嗗尯鍜屽啓璇勮〃鍗曞３灞傚彲娓叉煋銆?

## Stage 7 Added RN Smoke
- `__tests__/smoke/RecommendBookPage.smoke.test.tsx`
- `__tests__/smoke/ViewedUsersPage.smoke.test.tsx`
- `__tests__/smoke/MyReservationPage.smoke.test.tsx`
