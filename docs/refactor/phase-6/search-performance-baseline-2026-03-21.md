# 搜索性能基线 - 2026-03-21

## 场景
- 通过 debug route 冷启动进入搜索结果页。
- 路由：`search_result?query=novel`

## 命令
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route "search_result?query=novel"`
- `adb logcat -d | Select-String "ComposeMainActivity|NavigationSetup|SearchResultPage|SearchResultViewModel|SearchBooksUseCase|SearchService|SearchRepository"`

## 设备 / 构建
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建变体：`debug`

## 预期
- 路由应落到 `SearchResultPage`。
- 搜索过程不应出现运行时异常。
- 第一轮样本允许是 log sample，不要求必须是完整 benchmark。

## 实际结果
- `ComposeMainActivity` 冷启动成功。
- `NavigationSetup` 在 `21:30:57.936` 跳到 `search_result?query=novel`。
- `SearchResultViewModel` 在 `21:30:58.043` 完成初始化。
- 搜索准备日志出现在 `21:30:58.335`。
- 实际搜索执行日志出现在 `21:30:58.836`。
- 搜索成功日志出现在 `21:30:58.881`。
- 当前样本可推得：
  - route jump -> search execute 约 `900 ms`
  - route jump -> first success log 约 `945 ms`
- 当前样本返回 `0` 条结果，但路由和取数路径保持健康。

## 证据
- `docs/refactor/evidence/search-result-performance-logcat-2026-03-21.txt`

## 结论
- `通过（日志样本）`

## 残余风险
- 当前证据仍是 log sample，而不是专门的搜索 benchmark。
- 搜索结果页分页仍缺专项 macrobenchmark 或 smoke-style 回归套件。
