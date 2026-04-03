#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const { execFileSync } = require('child_process');
const { readSignoffModel, validateSignoffModel } = require('./stage7-signoff-check.js');

const readText = (repoRoot, relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

const writeText = (repoRoot, relativePath, content) => {
  fs.writeFileSync(path.join(repoRoot, relativePath), content, 'utf8');
};

const replaceOrThrow = (text, from, to, label) => {
  if (!text.includes(from)) {
    throw new Error(`Failed to locate expected text for ${label}`);
  }
  return text.replace(from, to);
};

const finalizeText = (repoRoot, decisionDate) => {
  const updates = [
    {
      path: 'docs/refactor/README.md',
      apply: (text) => {
        let next = text;
        next = replaceOrThrow(next, '- 当前阶段：`Phase 15`', '- 当前阶段：`Stage 7 closeout`', 'README current phase');
        next = replaceOrThrow(next, '- 阶段状态：`in_progress`', '- 阶段状态：`validated`', 'README phase status');
        next = replaceOrThrow(next, '- Stage 状态：`in_progress`', '- Stage 状态：`validated`', 'README stage status');
        next = replaceOrThrow(
          next,
          '- 当前技术收口状态：`ready_for_closeout_except_signoff`',
          '- 当前技术收口状态：`validated`',
          'README technical status',
        );
        next = replaceOrThrow(
          next,
          '- 最新生效切换：`2026-03-31 Stage 7 activation`',
          `- 最新生效切换：\`${decisionDate} Stage 7 validated\``,
          'README effective date',
        );
        next = replaceOrThrow(next, '- `Stage 7 = in_progress`', '- `Stage 7 = validated`', 'README stage conclusion');
        return next;
      },
    },
    {
      path: 'docs/refactor/master-roadmap.md',
      apply: (text) => {
        let next = text;
        next = replaceOrThrow(next, '- `Stage 7 = in_progress`', '- `Stage 7 = validated`', 'roadmap stage status');
        next = replaceOrThrow(
          next,
          '- `Stage 7 technical status = ready_for_closeout_except_signoff`',
          '- `Stage 7 technical status = validated`',
          'roadmap technical status',
        );
        next = replaceOrThrow(
          next,
          '- 当前 repo、验证门禁与官方 Figma 宿主证据已闭环，剩余最终切换条件只差设计 / 产品 / QA 真人签核。',
          '- 当前 repo、验证门禁、官方 Figma 宿主证据与三方签核已全部闭环；后续如需继续推进，应通过 reopen 或新 Stage 进入。',
          'roadmap project status',
        );
        next = replaceOrThrow(
          next,
          '| Stage 7 | Phase 15-18 | 视觉系统、资产治理、Token、展示与回归门禁 | `in_progress` |',
          '| Stage 7 | Phase 15-18 | 视觉系统、资产治理、Token、展示与回归门禁 | `validated` |',
          'roadmap stage table',
        );
        return next;
      },
    },
    {
      path: 'docs/harness/current-focus.md',
      apply: (text) => {
        let next = text;
        next = replaceOrThrow(
          next,
          '- 当前权威结论：`Stage 7 = in_progress`，`Phase 15-18 = validated`',
          '- 当前权威结论：`Stage 7 = validated`，`Phase 15-18 = validated`',
          'current-focus authority',
        );
        next = replaceOrThrow(
          next,
          '- 当前 active refactor 主线仍为 `Stage 7 / Phase 15-18`',
          '- 当前没有新的 active refactor 主线，`Stage 7` 已完成 closeout。',
          'current-focus next line 1',
        );
        next = replaceOrThrow(
          next,
          '- repo 侧 code、tests、Android gate、audit/control-plane 与官方宿主证据已闭环，当前重点切到设计 / 产品 / QA 三方签核落盘',
          '- repo 侧 code、tests、Android gate、audit/control-plane、官方宿主证据与签核记录已全部闭环。',
          'current-focus next line 3',
        );
        next = replaceOrThrow(
          next,
          '- Stage 7 的真实剩余 blocker 已从 Figma 页面证据补齐收缩为设计 / 产品 / QA 三方真人签核尚未落盘',
          '- Stage 7 closeout 已完成，以下仅保留仓库长期治理 drift。',
          'current-focus blocker 1',
        );
        next = replaceOrThrow(
          next,
          '- 设计 / 产品 / QA 的签核包已准备，但真人签核记录尚未落盘',
          '- 设计 / 产品 / QA 三方签核已完成并记录在 `stage-7-signoff-record.md`。',
          'current-focus blocker 2',
        );
        return next;
      },
    },
    {
      path: 'docs/refactor/stage-7-closeout-summary.md',
      apply: (text) => {
        let next = text;
        next = replaceOrThrow(next, '- `Stage 7 = in_progress`', '- `Stage 7 = validated`', 'summary stage status');
        next = replaceOrThrow(next, '- 当前状态：`ready_for_closeout_except_signoff`', '- 当前状态：`validated`', 'summary current status');
        next = replaceOrThrow(
          next,
          '- 当前真正阻塞 Stage 7 正式关闭的项已经收缩到一类：\n  - 设计 / 产品 / QA 三方签核未落盘',
          '- 当前 Stage 7 的技术门禁、官方宿主证据与三方签核已全部闭环。',
          'summary blocker paragraph',
        );
        next = replaceOrThrow(
          next,
          '- 因此当前最准确状态不是“待补 Figma 证据”，而是“closeout 宿主、证据包与技术基线已闭环，待人工签核完成最终切换”。',
          '- 因此当前最准确状态是“Stage 7 closeout 完成，已具备 validated 权威口径”。',
          'summary final line',
        );
        next = replaceOrThrow(next, '- 原 Stage 7 Figma 文件 `iYUJgiIKNxjt78XHujzS4b` 对当前账号不可继续用作正式宿主；当前已切换到新宿主 `7YaJPjyzLvGLfVPTkUx0Tf`，并完成 `51` 个 audit frame 的初始回填。\n- 三方评审按本轮口径属于“待签核，不阻塞技术完成”，但会继续阻塞 `Stage 7 = validated` 的最终切换。', '- none', 'summary external blockers');
        next = replaceOrThrow(
          next,
          '- 执行设计 / 产品 / QA 待签核评审包，并在 [stage-7-signoff-record.md](./stage-7-signoff-record.md) 中补录真人签核记录。\n- 随后切换：\n  - `Phase 15-18 = validated`\n  - `Stage 7 = validated`',
          '- 当前没有新的 active refactor main line。\n- 后续如需继续推进视觉系统与资产治理，应通过 reopen 或新 Stage 进入。',
          'summary next steps',
        );
        return next;
      },
    },
    {
      path: 'docs/refactor/stage-7-closeout-review-packet.md',
      apply: (text) => {
        let next = text;
        next = replaceOrThrow(
          next,
          '- 当前技术状态：`ready_for_closeout_except_signoff`',
          '- 当前技术状态：`validated`',
          'review packet technical status',
        );
        next = replaceOrThrow(
          next,
          '- 评审口径：设计 / 产品 / QA 待人工签核，不阻塞 repo 内技术完成',
          '- 评审口径：设计 / 产品 / QA 三方签核已落盘，可作为 Stage 7 validated 存档包。',
          'review packet scope',
        );
        next = replaceOrThrow(next, '- 设计 / 产品 / QA 三方签核仍待人工落盘。', '- none', 'review packet blocker');
        next = replaceOrThrow(
          next,
          '1. 基于当前官方宿主、closeout 包与 [stage-7-signoff-record.md](./stage-7-signoff-record.md) 执行设计 / 产品 / QA 三方签核\n2. 由设计 / 产品 / QA 分别在签核记录宿主中补录 reviewer、date、decision、notes\n3. 切换 `Stage 7 = validated`',
          '1. 当前包作为 `Stage 7 = validated` 的归档评审入口保留。\n2. 后续如需 reopen，先更新 signoff record、validation board 与 decision log。\n3. 无新增 blocker。',
          'review packet next steps',
        );
        return next;
      },
    },
    {
      path: 'docs/refactor/tracking/phase-15-18-validation-board.md',
      apply: (text) => replaceOrThrow(
        text,
        '- `Stage 7`: `in_progress`',
        '- `Stage 7`: `validated`',
        'validation board stage status',
      ),
    },
    {
      path: 'docs/refactor/tracking/decision-log.md',
      apply: (text) => {
        const row =
          `| ${decisionDate} | Stage 7 closeout | closeout | ` +
          '在三方签核补录完成并通过 `stage7:signoff:check` 后关闭 `Stage 7` | ' +
          '当前 `Phase 15-18` 已全部 `validated`，官方宿主证据、门禁与签核记录已闭环，继续维持 `in_progress` 只会造成 authority 漂移 | ' +
          '`Stage 7 = validated`，默认 active refactor main line 清空 | 后续如需继续推进视觉系统与资产治理，走 reopen 或新 Stage |';
        if (text.includes(row)) {
          return text;
        }
        const lines = text.split('\n');
        lines.splice(2, 0, row);
        return lines.join('\n');
      },
    },
    {
      path: 'docs/harness/session-log.md',
      apply: (text) => {
        const row =
          `| ${decisionDate} | Stage 7 validated | ` +
          '设计 / 产品 / QA 三方签核补录完成，`Stage 7` 与 `Phase 15-18` 正式切换到 `validated` | ' +
          '[stage-7-signoff-record.md](../refactor/stage-7-signoff-record.md) |';
        if (text.includes(row)) {
          return text;
        }
        return `${text.trimEnd()}\n${row}\n`;
      },
    },
  ];

  for (const update of updates) {
    const current = readText(repoRoot, update.path);
    const next = update.apply(current);
    writeText(repoRoot, update.path, next);
  }
};

const main = () => {
  const repoRoot = path.resolve(__dirname, '..');
  const signoffModel = readSignoffModel({ repoRoot });
  const signoffResult = validateSignoffModel(signoffModel);
  if (!signoffResult.ok) {
    console.error(signoffResult.message);
    process.exit(1);
  }

  finalizeText(repoRoot, signoffModel.decisionDate);
  execFileSync('node', [path.join(repoRoot, 'scripts', 'stage7-closeout-readiness.js'), 'generate'], {
    stdio: 'inherit',
  });
  execFileSync('node', [path.join(repoRoot, 'scripts', 'harness-refresh.js')], {
    stdio: 'inherit',
  });
  console.log(`Stage 7 validation docs updated for ${signoffModel.decisionDate}.`);
};

module.exports = {
  finalizeText,
};

if (require.main === module) {
  main();
}
