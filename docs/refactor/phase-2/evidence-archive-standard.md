# Phase 2 Evidence Archive Standard

## 目标
- 固定 Phase 2 之后所有质量证据的命名、目录、字段和归档方式。
- 避免后续出现“跑过了但没有证据”“截图存在但无法追溯命令/设备/版本”的情况。

## 统一目录
- 截图与录屏统一放在 `docs/refactor/evidence/`
- 文本化结论与 diff 摘要统一放在 `docs/refactor/phase-2/`

## 文件命名规范
- screenshot:
  - `{feature}-{scenario}-{yyyy-mm-dd}.png`
- screen-recording:
  - `{feature}-{scenario}-{yyyy-mm-dd}.mp4`
- benchmark diff:
  - `benchmark-diff-{scenario}-{yyyy-mm-dd}.md`
- size diff:
  - `size-diff-{artifact}-{yyyy-mm-dd}.md`
- smoke evidence:
  - `smoke-run-{platform}-{scenario}-{yyyy-mm-dd}.md`

## 每类证据必填字段

### 1. Benchmark diff
- `Scenario`
- `Command`
- `Build Variant`
- `Device / API`
- `Baseline Source`
- `Candidate Source`
- `Metric`
- `Baseline`
- `Actual`
- `Delta`
- `Threshold`
- `Result`
- `Analysis`

### 2. Size diff
- `Artifact`
- `Command`
- `Build Variant`
- `Baseline Artifact`
- `Candidate Artifact`
- `Baseline Size`
- `Actual Size`
- `Delta`
- `Threshold`
- `Result`
- `Analysis`

### 3. Smoke screenshot / recording
- `Scenario`
- `Command`
- `Route / Page`
- `Device / API`
- `Network`
- `Build Variant`
- `Expected`
- `Actual`
- `Evidence Files`
- `Result`
- `Notes`

## Markdown 模板

### Benchmark diff template
```md
# Benchmark Diff - {scenario}

- Scenario:
- Command:
- Build Variant:
- Device / API:
- Baseline Source:
- Candidate Source:

| Metric | Baseline | Actual | Delta | Threshold | Result |
| --- | --- | --- | --- | --- | --- |
| cold-start |  |  |  |  |  |
| frame-time |  |  |  |  |  |
| reader-page-turn |  |  |  |  |  |

## Analysis
- 
```

### Size diff template
```md
# Size Diff - {artifact}

- Artifact:
- Command:
- Build Variant:
- Baseline Artifact:
- Candidate Artifact:

| Metric | Baseline | Actual | Delta | Threshold | Result |
| --- | --- | --- | --- | --- | --- |
| file-size |  |  |  |  |  |

## Analysis
- 
```

### Smoke evidence template
```md
# Smoke Run - {platform} - {scenario}

- Scenario:
- Command:
- Route / Page:
- Device / API:
- Network:
- Build Variant:
- Expected:
- Actual:
- Evidence Files:
- Result:

## Notes
- 
```

## 使用规则
- 任何进入 `phase-0-2-validation-board.md` 的证据，必须至少能反查到:
  - 命令
  - 构建类型
  - 设备或执行环境
  - 时间
  - 对应文件路径
- 图片/录屏不能单独存在，必须配套一份 markdown 说明文件。
- 若结果为 `yellow` 或 `red`，必须附 `Analysis`，不能只贴截图。

## 与看板的关系
- `V2-08` 关闭前，后续新增 benchmark / size / smoke 证据必须遵守本标准。
- 若后续模板调整，必须同步更新：
  - `docs/refactor/tracking/phase-0-2-validation-board.md`
  - `docs/refactor/tracking/decision-log.md`
  - 受影响的历史证据文件
