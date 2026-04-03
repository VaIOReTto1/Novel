const fs = require('fs');
const os = require('os');
const path = require('path');
const finalize = require('../../scripts/stage7-finalize.js');

describe('stage7 finalize text updates', () => {
  test('updates key Stage 7 control-plane docs to validated in a temp repo', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'stage7-finalize-'));

    try {
      const files = {
        'docs/refactor/README.md': [
          '- 当前阶段：`Phase 15`',
          '- 阶段状态：`in_progress`',
          '- 当前 Stage：`Stage 7 = Phase 15-18`',
          '- Stage 状态：`in_progress`',
          '- 当前技术收口状态：`ready_for_closeout_except_signoff`',
          '- 最新生效切换：`2026-03-31 Stage 7 activation`',
          '- `Stage 7 = in_progress`',
        ].join('\n'),
        'docs/refactor/master-roadmap.md': [
          '- `Stage 7 = in_progress`',
          '- `Stage 7 technical status = ready_for_closeout_except_signoff`',
          '- 当前 repo、验证门禁与官方 Figma 宿主证据已闭环，剩余最终切换条件只差设计 / 产品 / QA 真人签核。',
          '| Stage 7 | Phase 15-18 | 视觉系统、资产治理、Token、展示与回归门禁 | `in_progress` |',
        ].join('\n'),
        'docs/harness/current-focus.md': [
          '- 当前权威结论：`Stage 7 = in_progress`，`Phase 15-18 = validated`',
          '- 当前 active refactor 主线仍为 `Stage 7 / Phase 15-18`',
          '- repo 侧 code、tests、Android gate、audit/control-plane 与官方宿主证据已闭环，当前重点切到设计 / 产品 / QA 三方签核落盘',
          '- Stage 7 的真实剩余 blocker 已从 Figma 页面证据补齐收缩为设计 / 产品 / QA 三方真人签核尚未落盘',
          '- 设计 / 产品 / QA 的签核包已准备，但真人签核记录尚未落盘',
        ].join('\n'),
        'docs/refactor/stage-7-closeout-summary.md': [
          '- `Stage 7 = in_progress`',
          '- 当前状态：`ready_for_closeout_except_signoff`',
          '- 当前真正阻塞 Stage 7 正式关闭的项已经收缩到一类：',
          '  - 设计 / 产品 / QA 三方签核未落盘',
          '- 因此当前最准确状态不是“待补 Figma 证据”，而是“closeout 宿主、证据包与技术基线已闭环，待人工签核完成最终切换”。',
          '- 原 Stage 7 Figma 文件 `iYUJgiIKNxjt78XHujzS4b` 对当前账号不可继续用作正式宿主；当前已切换到新宿主 `7YaJPjyzLvGLfVPTkUx0Tf`，并完成 `51` 个 audit frame 的初始回填。',
          '- 三方评审按本轮口径属于“待签核，不阻塞技术完成”，但会继续阻塞 `Stage 7 = validated` 的最终切换。',
          '- 执行设计 / 产品 / QA 待签核评审包，并在 [stage-7-signoff-record.md](./stage-7-signoff-record.md) 中补录真人签核记录。',
          '- 随后切换：',
          '  - `Phase 15-18 = validated`',
          '  - `Stage 7 = validated`',
        ].join('\n'),
        'docs/refactor/stage-7-closeout-review-packet.md': [
          '- 当前技术状态：`ready_for_closeout_except_signoff`',
          '- 评审口径：设计 / 产品 / QA 待人工签核，不阻塞 repo 内技术完成',
          '- 设计 / 产品 / QA 三方签核仍待人工落盘。',
          '1. 基于当前官方宿主、closeout 包与 [stage-7-signoff-record.md](./stage-7-signoff-record.md) 执行设计 / 产品 / QA 三方签核',
          '2. 由设计 / 产品 / QA 分别在签核记录宿主中补录 reviewer、date、decision、notes',
          '3. 切换 `Stage 7 = validated`',
        ].join('\n'),
        'docs/refactor/tracking/phase-15-18-validation-board.md': '- `Stage 7`: `in_progress`',
        'docs/refactor/tracking/decision-log.md': '| 日期 | 阶段 | 类型 | 决策 | 原因 | 影响 | 后续动作 |\n| --- | --- | --- | --- | --- | --- | --- |\n',
        'docs/harness/session-log.md': '| Date | Theme | Summary | Evidence |\n| --- | --- | --- | --- |\n',
      };

      Object.entries(files).forEach(([relativePath, content]) => {
        const absolutePath = path.join(tempDir, relativePath);
        fs.mkdirSync(path.dirname(absolutePath), { recursive: true });
        fs.writeFileSync(absolutePath, `${content}\n`, 'utf8');
      });

      finalize.finalizeText(tempDir, '2026-04-05');

      const readme = fs.readFileSync(path.join(tempDir, 'docs/refactor/README.md'), 'utf8');
      const summary = fs.readFileSync(path.join(tempDir, 'docs/refactor/stage-7-closeout-summary.md'), 'utf8');
      const packet = fs.readFileSync(path.join(tempDir, 'docs/refactor/stage-7-closeout-review-packet.md'), 'utf8');
      const board = fs.readFileSync(path.join(tempDir, 'docs/refactor/tracking/phase-15-18-validation-board.md'), 'utf8');

      expect(readme).toContain('- 当前技术收口状态：`validated`');
      expect(readme).toContain('- `Stage 7 = validated`');
      expect(summary).toContain('- `Stage 7 = validated`');
      expect(summary).toContain('- 当前状态：`validated`');
      expect(packet).toContain('- 当前技术状态：`validated`');
      expect(packet).toContain('- none');
      expect(board).toContain('- `Stage 7`: `validated`');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
