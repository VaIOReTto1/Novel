# Phase 8 Rollout And Rollback Playbook

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 4 / Phase 8`
- 当前结论：`已固定当前仓库可执行的 rollout / rollback playbook`

## 当前真实 rollout 模式
- 当前仓库没有远程配置平台。
- 当前仓库没有 canary 发布平台。
- 当前仓库的真实 rollout 单位仍是：
  - 原子提交
  - 文档留痕
  - 验证命令
  - `git revert`

## 标准流程
1. 确定原子主题。
2. 写入对应 phase / tracking 文档。
3. 补验证入口与 evidence。
4. 在 `rollback-index.md` 登记 `Rollback ID`、commit、one-click revert 命令。
5. 用中文 commit message 提交。
6. 若阶段状态变化，再同步 harness 层。

## 回退路径优先级
### 1. Git revert
- 当前默认首选。
- 入口：
  - `docs/refactor/tracking/rollback-index.md`

### 2. Runtime flag / kill switch
- 仅适用于已经存在 registry 的场景：
  - `enableBridgeErrorMapper`
  - `enableBridgeSharedScopes`
  - `enableSettingsDataStorePilot`

### 3. 文档 closeout 降级
- 若某阶段 closeout 后发现 control-plane 口径不成立，先降级 authority，再决定是否回退代码。
- 当前 repo 已有样本：
  - `2026-03-21` 的历史 checkpoint
  - `2026-03-26` 的 reopen closeout

## 发布前检查
- 是否已有 `Rollback ID`
- 是否已有 one-click revert 命令
- 是否已有最小 postcheck
- 是否已补 decision log / validation board
- 若触达阶段状态，是否已同步 harness

## 不允许伪装的能力
- 不允许把“本地文档化 rollback”写成“线上灰度系统”。
- 不允许把“BuildConfig 默认值”写成“远程 kill switch”。
- 不允许跳过 rollback-index 就声称改动可回退。

## 当前 repo 里的 canary 替代物
- sampled build canary
- device evidence canary
- bridge contract / smoke canary
- staged closeout / reopen canary

## 主要引用
- `docs/refactor/tracking/rollback-index.md`
- `docs/refactor/tracking/atomic-commit-guide.md`
- `docs/harness/references/verification.md`
