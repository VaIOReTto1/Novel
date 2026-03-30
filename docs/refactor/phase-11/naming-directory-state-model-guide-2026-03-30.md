# Naming Directory State Model Guide

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 11`
- 当前结论：`已固定命名 / 目录 / 状态模型治理宿主`

## 当前问题
- 命名风格、目录落点、状态模型和错误文案仍分散在不同历史阶段文档与具体实现中。
- 当前已有 MVI / reducer / state adapter 等模式，但没有一份“当前仓库默认规则”。

## 计划收口项
- 命名规范：
  - 对外契约名
  - 页面组件名
  - UseCase / Repository / Gateway / Coordinator 命名
- 目录规范：
  - `app` / `core-*` / `feature-*` / `src/**` 的默认落点
- 状态模型：
  - 何时用 reducer / state adapter / effect
  - 何时允许直接 UI state
- 错误文案与用户提示：
  - 同类错误保持统一语义
