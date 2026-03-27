# Search LOAD_MORE Debug Scenario - 2026-03-27

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 6 / Search LOAD_MORE gap unblock`
- 当前结论：`已具备内部分页 override 支架，待设备恢复后补真机样本`

## 背景
- 当前后端数据集在 `pageSize=20` 下没有给出 `pages > 1` 的高频 query。
- `search-page-size-sweep-2026-03-27.txt` 显示：
  - `query=我`
  - `total=17`
  - `pages=1`
- 这意味着当前 `LOAD_MORE` 缺口主要是数据集限制，而不是搜索链路没有分页能力。

## 新增支架
- `SearchParams` 现已支持 `pageSize`
- `SearchResultViewModel` 支持 debug-only 的 `pageSize override`
- `SearchQueryRepository` 与 `SearchResultCacheStore` 已同步尊重该 `pageSize`
- `ComposeMainActivity` 会在 `DEBUG` 构建下读取：
  - `debug_search_page_size`

## 推荐复现场景
### 命令
```powershell
adb shell am start -S -n com.novel/.ComposeMainActivity `
  --es debug_route "search_result?query=我" `
  --es debug_search_page_size 5
```

### 预期
- `query=我`
- `pageSize=5`
- 当前数据集 `total=17`
- 因此预期：
  - `pages=4`
  - 首屏 `hasMore=true`
  - 可以触发 `LOAD_MORE`

## 当前状态更新
- 这是 debug-only 的内部取证支架，不改变正式 route 或 bridge contract。
- 该 scenario 已在 `2026-03-28` 被实际用于补齐 `LOAD_MORE` 真机样本：
  - `docs/refactor/phase-6/device-evidence-addendum-2026-03-28.md`

## 主要引用
- `android/feature-search/src/main/java/com/novel/page/search/repository/SearchParams.kt`
- `android/feature-search/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt`
- `android/app/src/main/java/com/novel/page/search/repository/SearchQueryRepository.kt`
- `android/app/src/main/java/com/novel/page/search/repository/SearchResultCacheStore.kt`
- `android/app/src/main/java/com/novel/ComposeMainActivity.kt`
- `docs/refactor/evidence/search-page-size-sweep-2026-03-27.txt`
