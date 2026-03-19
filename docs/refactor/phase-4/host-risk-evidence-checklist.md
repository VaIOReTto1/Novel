# Phase 4 Host Risk Evidence Checklist

## 目标
- 把 `W2-A10` 的宿主页验证准备工作变成固定 checklist。
- 确保 `profile`、`settings`、作者/AI 场景的采证在真机可用时可直接执行。

## 必做场景
- `profile`
- `settings`
- `becomewriter` 或 `aipage`

## 每个场景必填字段
- `Command`
- `Route / Page`
- `Device / API`
- `Network`
- `Build Variant`
- `Expected`
- `Actual`
- `Evidence Files`
- `Result`

## 必采证据
- 截图至少 1 张
- 必要时补 `adb logcat -d`
- 如出现降级路径：
  - 明确触发条件
  - 明确用户可见表现
  - 明确是否阻塞 `V4-05`

## 与看板映射
- `V4-05`
  - 只要模板与 checklist 完整，可进入 `in_progress`
  - 只有形成 `profile/settings/author-ai` 中至少 2 个正向证据时，才允许推进到 `ready_for_validation`
