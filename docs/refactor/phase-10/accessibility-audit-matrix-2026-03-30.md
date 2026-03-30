# Accessibility Audit Matrix

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 10`
- 当前结论：`已固定无障碍审计宿主矩阵骨架`

## 检查项
- Compose semantics
- TalkBack
- 可点击区域
- 对比度
- 字体缩放
- Reader 大字号极限验证

## 当前页面入口
| Surface | Current Anchor | Current Fact | Gap |
| --- | --- | --- | --- |
| Welfare / WebView | `WelfareAccessibilityHelper` | 已有 WebView 无障碍配置与状态监听入口 | 尚未形成页面审计矩阵 |
| Login | `TitleSection` | 已有标题 `contentDescription` | 仅局部覆盖 |
| Home | `HomeTopBar` | 搜索与分类按钮已有部分描述 | 缺统一点击区域 / 对比度 / 字体缩放审计 |
| Reader | blueprint backlog | 蓝图明确要求大字号极限验证 | 当前无宿主文档 |
| RN Host Pages | stage backlog | 当前无集中无障碍清单 | 仍是缺口 |

## 默认通过阈值
- 每个页面至少有：
  - 关键操作的可读描述
  - 核心点击目标不依赖纯图像语义
  - 字体缩放下不出现主要信息不可见
- Reader 单独追加：
  - 大字号极限验证
