# Rollback Index

## 当前 Stage 4 文档切主线
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-STAGE4-DOCS-20260328-01` | `340bc23` | Stage 4 控制面切换与 Phase 7-8 文档骨架建立 | `git revert --no-edit 340bc23` | `npm run harness:check` |
| `RB-P7-SHRINK-20260330-01` | `d113f44` | Phase 7 第一轮 icon font shrink | `git revert --no-edit d113f44` | `android/gradlew.bat clean app:assembleRelease app:bundleRelease --no-daemon --console=plain` |
| `RB-P7-BUILD-20260330-01` | `eca2a36` | Phase 7 build baseline 与 configuration cache canary | `git revert --no-edit eca2a36` | `android/gradlew.bat app:testDebugUnitTest --configuration-cache --configuration-cache-problems=warn --no-daemon --console=plain` |
| `RB-P7-CLOSEOUT-20260330-01` | `77c4967` | Phase 7 closeout 与 Phase 8 入口切换 | `git revert --no-edit 77c4967` | `npm run harness:check` |
| `RB-P8-GOVERNANCE-20260330-01` | `241989b` | Phase 8 治理工件与 Stage 4 closeout | `git revert --no-edit 241989b` | `npm run harness:check` |
| `RB-STAGE5-DOCS-20260330-01` | `3ed6c6f` | Stage 5 控制面、Phase 9-11 宿主与公共治理宿主建立 | `git revert --no-edit 3ed6c6f` | `npm run harness:check` |
| `RB-P9-DOCS-20260330-01` | `fa8afa7` | Phase 9 运行可靠性与业务连续性宿主建立及关闭 | `git revert --no-edit fa8afa7` | `npm run harness:check` |
| `RB-P10-P11-STAGE5-20260330-01` | `945511d` | Phase 10/11 治理工件与 Stage 5 closeout | `git revert --no-edit 945511d` | `npm run harness:check` |
| `RB-STAGE6-DOCS-20260331-01` | `4ca01b5` | Stage 6 控制面、Phase 12-14 宿主与 RN 治理宿主建立 | `git revert --no-edit 4ca01b5` | `npm run harness:check` |

## 当前 reopen 原子提交
| Rollback ID | Commit SHA | Atomic Theme | One-Click Command | Postcheck |
| --- | --- | --- | --- | --- |
| `RB-P5-R1-20260326-01` | `6e39db8` | 收口 RN 宿主根与桥接状态层 | `git revert --no-edit 6e39db8` | `android/gradlew.bat :feature-rn-host:testDebugUnitTest :app:compileDebugKotlin :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |
| `RB-P5-R2-20260326-01` | `41a5ba8` | 迁移搜索根状态机到 `feature-search` | `git revert --no-edit 41a5ba8` | `android/gradlew.bat :feature-search:testDebugUnitTest :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |
| `RB-P5-R2-20260326-02` | `6c0d662` | 迁移登录根状态机到 `feature-login` | `git revert --no-edit 6c0d662` | `android/gradlew.bat :feature-login:testDebugUnitTest :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |
| `RB-P5-R2-20260326-03` | `6799388` | 迁移书籍详情根状态机到 `feature-book` | `git revert --no-edit 6799388` | `android/gradlew.bat :feature-book:testDebugUnitTest :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |
| `RB-P5-R3-20260326-01` | `f8a5d7c` | 迁移 Reader 设置协调件到 `feature-reader` | `git revert --no-edit f8a5d7c` | `android/gradlew.bat :feature-reader:testDebugUnitTest :app:compileDebugKotlin --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |
| `RB-P5-R3-20260326-02` | `ff71292` | 迁移首页根状态机到 `feature-home` | `git revert --no-edit ff71292` | `android/gradlew.bat :feature-home:testDebugUnitTest :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |
| `RB-P5-R3-20260326-03` | `5a5c81c` | 迁移阅读器根状态机到 `feature-reader` | `git revert --no-edit 5a5c81c` | `android/gradlew.bat :feature-reader:testDebugUnitTest :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |
| `RB-P5-R4-20260326-01` | `bb8349e` | 收口 app 宿主薄包装层 | `git revert --no-edit bb8349e` | `android/gradlew.bat :feature-rn-host:testDebugUnitTest :app:compileDebugKotlin :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` |

## 说明
- reopen 之前的 Phase 4 / Phase 5 历史 rollback 记录仍可在 git 历史中追溯，但当前 closeout 权威索引以本页为准。
