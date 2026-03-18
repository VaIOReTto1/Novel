# 双人交叉评审模板

## 使用规则
- 本模板适用于：
  - phase closeout 评审
  - 主文档改版评审
  - 协作策略评审
- 必须采用以下角色：
  - `Author`
  - `Primary Reviewer`
  - `Final Approver`
- `Primary Reviewer` 必须完成两轮独立检查。
- 不允许在最终报告中保留任何未闭环模板占位词、临时注记或执行期再决定标记。

## Report Header
- Document Under Review: `<path>`
- Review Level: `<release-quality | stage-closeout | policy-review>`
- Review Date: `<YYYY-MM-DD>`
- Verdict: `<pass | pass_with_risk | blocked>`

## Review Scope
- `<documents in scope>`

## Review Method
### Primary Reviewer Pass 1
- 盲审，不读取作者解释。
- 检查：
  - 状态一致性
  - 路径与引用正确性
  - 模板占位
  - 术语统一
  - 风险与回滚口径

### Primary Reviewer Pass 2
- 第二轮逐字逐句检查。
- 强制确认：
  - 是否有遗漏项
  - 是否有矛盾项
  - 是否有假阳性项

### Author Diff Reconciliation
- 每条发现项都必须给出处置：
  - `accepted`
  - `rejected with reason`
  - `deferred with risk`

### Reviewer Closure Check
- 未闭环项必须进入 `Residual Risks`。

### Final Approver Sign-off Gate
- 只有当以下条件同时成立时才可签字：
  - 无 blocker
  - 无未解释 high 风险
  - 文档无模板占位
  - 主状态一致

## Pass 1 Findings
| ID | Severity | Finding | Disposition |
| --- | --- | --- | --- |
| `<P1-01>` | `<low/medium/high/blocker>` | `<finding>` | `<accepted/rejected/deferred>` |

## Pass 2 Findings
| ID | Severity | Finding | Disposition |
| --- | --- | --- | --- |
| `<P2-01>` | `<low/medium/high/blocker>` | `<finding>` | `<accepted/rejected/deferred>` |

## Author Disposition
| Finding ID | Result | Reason |
| --- | --- | --- |
| `<P1-01>` | `<accepted/rejected/deferred>` | `<reason>` |

## Revision Log
| 日期 | 修订项 | 结果 |
| --- | --- | --- |
| `<YYYY-MM-DD>` | `<revision>` | `<done/not_done>` |

## Residual Risks
- `<risk-1>`
- `<risk-2>`

## Release Quality Verdict
- 结论：`<pass | pass_with_risk | blocked>`
- 说明：
  - `<reason-1>`
  - `<reason-2>`

## Sign-off
- `Author`: `<role-or-name>` / `<signed|pending>` / `<date>`
- `Primary Reviewer`: `<role-or-name>` / `<signed|pending>` / `<date>`
- `Final Approver`: `<role-or-name>` / `<signed|pending>` / `<date>`
