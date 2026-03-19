# Rollback Index

## 目标
- 固定 `Phase 4+` 每个原子主题的一键回滚入口。
- 避免后续出现“改动可解释但不可快速回退”的情况。

## 使用规则
- 仅 `LeaderAgent` 可写。
- 每个原子主题在进入提交阶段前，必须先分配 `Rollback ID`。
- 若无法给出 `One-Click Command`，该主题不得进入提交阶段。

## 字段定义
| Rollback ID | Phase | Wave | Atomic Theme | Owner Agent | Commit SHA | One-Click Command | Precheck | Postcheck | Status | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

## 当前记录
| Rollback ID | Phase | Wave | Atomic Theme | Owner Agent | Commit SHA | One-Click Command | Precheck | Postcheck | Status | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `RB-P4-W2-A01-20260319-01` | Phase 4 | Wave 2 | 建立 `NavigationBridgeFacade` 兼容壳 | `BridgeFacadeSplitAgent` | `5092915` | `git revert --no-edit 5092915` | `git status --short` | `./gradlew.bat app:testDebugUnitTest --tests "com.novel.rn.bridge.facade.NavigationBridgeFacadeTest"` | `active` | 当前仅收口登录/设置/返回三条最小导航出口 |
| `RB-P4-W2-A02-20260319-01` | Phase 4 | Wave 2 | 抽离 `NavigationRouteDelegate` 最小实现 | `BridgeFacadeSplitAgent` | `acbcfb7` | `git revert --no-edit acbcfb7` | `git status --short` | `./gradlew.bat app:testDebugUnitTest --tests "com.novel.rn.bridge.delegate.NavigationRouteDelegateTest"` | `active` | 当前先收口五条纯 route 跳转，不改 route 语义 |
| `RB-P4-W2-A03-20260319-01` | Phase 4 | Wave 2 | 抽离 `NavigationQueryDelegate` 最小实现 | `BridgeFacadeSplitAgent` | `4743b7c` | `git revert --no-edit 4743b7c` | `git status --short` | `./gradlew.bat app:testDebugUnitTest --tests "com.novel.rn.bridge.delegate.NavigationQueryDelegateTest"` | `active` | 当前先收口桥接状态与主题状态三条低风险查询出口 |
| `RB-P4-W2-A04-20260319-01` | Phase 4 | Wave 2 | 抽离 `NavigationHostDelegate` 最小实现 | `BridgeFacadeSplitAgent` | `673cd4f` | `git revert --no-edit 673cd4f` | `git status --short` | `./gradlew.bat app:testDebugUnitTest --tests "com.novel.rn.bridge.delegate.NavigationHostDelegateTest"` | `active` | 当前先收口组件注册、路由通知、缓存清理四条宿主页相关出口 |
| `RB-P4-W2-A05-20260319-01` | Phase 4 | Wave 2 | 抽离 `SelectionMenuDelegate` 最小实现 | `BridgeFacadeSplitAgent` | `c3a2bd1` | `git revert --no-edit c3a2bd1` | `git status --short` | `./gradlew.bat app:testDebugUnitTest --tests "com.novel.rn.bridge.delegate.SelectionMenuDelegateTest"` | `active` | 当前先收口 Selection Menu 的 action 解析与事件 payload 组装 |
| `RB-P4-W2-A06-20260319-01` | Phase 4 | Wave 2 | 抽离 `NavigationContentQueryDelegate` 最小实现 | `BridgeFacadeSplitAgent` | `99967fb` | `git revert --no-edit 99967fb` | `git status --short` | `./gradlew.bat app:testDebugUnitTest --tests "com.novel.rn.bridge.delegate.NavigationContentQueryDelegateTest"` | `active` | 当前先收口 6 个读操作的 payload 组装层，不改变 Promise 语义 |
| `RB-P4-W2-A07-20260320-01` | Phase 4 | Wave 2 | 抽离 `NavigationAuthorDelegate` 最小实现 | `BridgeFacadeSplitAgent` | `46ab116` | `git revert --no-edit 46ab116` | `git status --short` | `./gradlew.bat app:testDebugUnitTest --tests "com.novel.rn.bridge.delegate.NavigationAuthorDelegateTest"` | `active` | 当前先收口成为作家、写作入口、作品管理和注册请求构造语义 |
| `RB-P4-W2-A10-20260320-01` | Phase 4 | Wave 2 | 宿主页验证准备与证据模板 | `HostRiskQualityAgent` | `pending-after-commit` | `git revert --no-edit <commit-sha>` | `git status --short` | `rg -n "待补充|TBD|TODO|待填写" docs/refactor/phase-4/host-risk-run-*-template.md docs/refactor/phase-4/host-risk-evidence-checklist.md` | `active` | 当前先固定 `profile/settings/author-ai` 三类宿主页验证模板与 checklist |
