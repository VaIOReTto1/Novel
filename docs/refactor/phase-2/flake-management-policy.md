# Phase 2 Flake Management Policy

## 目标
- 为 smoke、benchmark、CI job 提供统一的 flake 判定与处理规则。
- 防止出现“反复重跑赌成功”或“偶发失败没人跟进”的情况。

## 适用范围
- `android-smoke`
- `rn-tests`
- `android-quality`
- `android-detekt-observe`
- `rn-lint-observe`
- 后续 benchmark diff / smoke diff / emulator job

## 术语定义
- `flake`
  - 同一 commit、同一命令、同一环境下，失败原因无法稳定复现，重跑后结果不一致。
- `real regression`
  - 同一 commit 在重复执行中稳定失败，或能稳定指向真实代码/配置问题。
- `infra issue`
  - ADB、emulator、network、runner capacity、artifact upload、timeout 等环境问题。

## 判定规则
- 第一次失败时，必须先分类：
  - `test logic`
  - `infra issue`
  - `unknown`
- 不允许直接手动重跑超过 `1` 次而不记录。
- 满足以下任一条件，可标记为 `flake`：
  - 同一命令在不改代码情况下二次执行通过
  - 失败堆栈指向超时、设备离线、emulator boot、ADB session、artifact upload 等基础设施波动
  - 失败点在不同 run 之间不一致

## 重试策略
- RN/Jest:
  - 最多自动重试 `1` 次
  - 第二次仍失败，按真实问题处理
- Android smoke:
  - 最多自动重试 `1` 次
  - 若失败原因为设备/ADB/emulator boot，可额外允许一次基础设施重试
- Benchmark:
  - 最多自动重试 `2` 次
  - 若三次结果波动超过阈值，直接标记为 `flake investigation required`

## 阻断策略
- blocking job:
  - 失败后只允许按本策略进行有限重试
  - 不允许靠无限 rerun 解除阻断
- observe job:
  - 可以不阻断合并，但必须记录到证据或决策日志
  - 连续 `3` 次失败必须升级处理

## 升级规则
- 同一 job 在 `7` 天内出现 `>= 3` 次 flake：
  - 必须创建专项修复项
  - 必须指定 `Owner`
  - 必须写入 `decision-log.md`
- 同一 smoke 场景在 `14` 天内持续 flake：
  - 不允许升级为 blocking
- 同一 benchmark 场景在 `14` 天内持续 flake：
  - 不允许作为性能回退阻断依据

## 责任角色
- `Owner`
  - 负责定位 flake 根因、补证据、推动收敛
- `Reviewer`
  - 负责确认 flake 分类是否合理，避免把真实回归误判为 flake
- `Validator`
  - 负责批准隔离、恢复 blocking、关闭问题

## 必填记录
- 每次 flake 记录至少包含：
  - `Job`
  - `Scenario`
  - `Command`
  - `Commit SHA`
  - `Environment`
  - `First Failure`
  - `Retry Result`
  - `Classification`
  - `Owner`
  - `Next Action`

## 隔离规则
- 允许临时隔离的前提：
  - 已记录证据
  - 已指定 Owner
  - 已写明恢复条件
- 不允许：
  - 口头约定隔离
  - 不记录原因直接 `continue-on-error`
  - 永久观察态不回收

## 与看板的关系
- 若 flake 仍无法归类，`V2-09` 不得关闭。
- 若 observe job 连续波动，必须同步更新：
  - `docs/refactor/tracking/decision-log.md`
  - 对应 Phase 2 文档
  - 对应证据归档文件
