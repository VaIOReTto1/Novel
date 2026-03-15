# Phase 2 PR Gate And Ownership Matrix

## 目标
- 明确 PR 级别的 blocking / observe 门禁划分。
- 固定 `Owner / Reviewer / Validator` 的默认责任归属，避免出现“门禁挂了但没人负责”。

## 当前 PR 门禁

### Blocking jobs
- `rn-tests`
  - 命令: `npm test -- --runInBand`
- `android-quality`
  - 命令:
    - `./gradlew app:testDebugUnitTest`
    - `./gradlew app:lintDebug`
    - `./gradlew app:compileDebugAndroidTestKotlin`
    - `./gradlew :macrobenchmark:assemble`

### Observe jobs
- `android-smoke`
  - 说明: 已具备真机/CI emulator 执行路径，但仍处于波动收敛期
- `rn-lint-observe`
  - 说明: 存在历史 RN lint debt，先在线暴露
- `android-detekt-observe`
  - 说明: 已能真实扫描，但存在历史 detekt debt

## 合并规则
- PR 不允许在 blocking jobs 失败时直接合并
- observe jobs 失败不会立即阻断，但必须：
  - 在 PR 评论或对应 evidence 中留痕
  - 若属于持续问题，写入 `decision-log.md`
  - 按 `flake-management-policy.md` 执行重试与升级

## 默认责任角色
- `Owner`
  - 默认由当前改动实施者承担
  - 在 PR 语境下等价于 `PR Author / 当前值守实施者`
- `Reviewer`
  - 默认由对应模块评审者承担
  - 在本轮默认语义为 `Android / RN 模块评审者`
- `Validator`
  - 默认由阶段门禁批准者承担
  - 在本轮默认语义为 `质量门禁批准者 / 阶段关闭批准者`

## Phase 2 默认映射
| Role | Default Mapping | Scope |
| --- | --- | --- |
| Owner | 当前重构实施者 | 执行、修复、补证据 |
| Reviewer | 模块代码评审者 | 审核方案与改动合理性 |
| Validator | 阶段门禁批准者 | 批准阶段关闭或延期 |

## 使用规则
- 新增 blocking job 前，必须先完成：
  - 本地可执行验证
  - 失败场景归类
  - 证据归档路径确认
- observe job 升级为 blocking 前，必须满足：
  - 最近一段周期内无高频 flake
  - 有明确 Owner
  - 有回退方案

## 与其他文档的关系
- CI job 定义:
  - `docs/refactor/phase-2/ci-workflow-catalog.md`
- flake 规则:
  - `docs/refactor/phase-2/flake-management-policy.md`
- 证据归档:
  - `docs/refactor/phase-2/evidence-archive-standard.md`
