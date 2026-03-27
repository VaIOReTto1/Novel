# Phase 6 Device Evidence Addendum - 2026-03-28

## 摘要
- 日期：`2026-03-28`
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建：`debug / com.novel / versionName=1.0.2`
- 目的：关闭前一轮仍残留的两条真机证据缺口：
  - Search `LOAD_MORE`
  - Reader `flip`

## 一、Search LOAD_MORE
### 命令
```powershell
adb shell am start -S -n com.novel/.ComposeMainActivity `
  --es debug_route "search_result?query=我" `
  --es debug_search_page_size 5
```

### 实际结果
- `INITIAL_ENTRY`
  - `query=我`
  - `page=1`
  - `resultCount=5`
  - `hasMore=true`
  - `durationMs=90`
- `LOAD_MORE`
  - `page=2`
  - `resultCount=5`
  - `hasMore=true`
  - `durationMs=51`
- `LOAD_MORE`
  - `page=3`
  - `resultCount=5`
  - `hasMore=true`
  - `durationMs=75`

### 结论
- `LOAD_MORE` 现在已有当天真机样本。
- 前一轮缺口已从“无法制造分页场景”收敛为“场景已可控且已有真机证据”。

### 证据
- `docs/refactor/evidence/search-load-more-logcat-2026-03-28.txt`
- `docs/refactor/evidence/search-page-size-sweep-2026-03-27.txt`
- `docs/refactor/phase-6/search-load-more-debug-scenario-2026-03-27.md`

## 二、Reader flip
### 命令
```powershell
adb shell am start -S -n com.novel/.ComposeMainActivity `
  --es debug_route "reader/1334318497132552192?chapterId=1334318500051787776" `
  --es debug_reader_auto_flip next
```

### 实际结果
- `init`
  - `durationMs=383`
  - `budgetMs=1200`
  - `budgetStatus=within`
- `flip`
  - `phase=start action=flip`
  - `source=page_flip_intent`
  - `direction=NEXT`
  - `mode=PAGECURL`
  - `durationMs=72`
  - `budgetMs=250`
  - `budgetStatus=within`
  - `outcome=virtual_page_rebuild`
  - `virtualIndex=2`

### 结论
- Reader `flip` 现在已有当天真机样本。
- 前一轮“只能靠人工 swipe，抓不到可信 trace”的缺口已关闭。

### 证据
- `docs/refactor/evidence/reader-flip-logcat-2026-03-28.txt`
- `docs/refactor/phase-6/reader-flip-debug-scenario-2026-03-27.md`

## 总结
- 截至 `2026-03-28`：
  - Search `LOAD_MORE` 真机样本已补齐
  - Reader `flip` 真机样本已补齐
- `Phase 6` 仍剩的主要 backlog 已不再是“缺关键动作样本”，而是：
  - 更系统的 benchmark / budget diff
  - Welfare / WebView 更深复盘
  - 数据库 / 缓存收益量化
