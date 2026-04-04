const fs = require('fs');
const os = require('os');
const path = require('path');
const signoffUpdate = require('../../scripts/stage7-signoff-update.js');

describe('stage7 signoff update', () => {
  test('updates one party and preserves other sections', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'stage7-signoff-update-'));

    try {
      const signoffPath = path.join(tempDir, 'docs', 'refactor', 'stage-7-signoff-record.md');
      fs.mkdirSync(path.dirname(signoffPath), { recursive: true });
      fs.writeFileSync(
        signoffPath,
        [
          '## Design Signoff',
          '- Reviewer: `pending`',
          '- Date: `pending`',
          '- Decision: `pending`',
          '- Notes:',
          '  - 待确认设计项',
          '',
          '## Product Signoff',
          '- Reviewer: `pending`',
          '- Date: `pending`',
          '- Decision: `pending`',
          '- Notes:',
          '  - 待确认产品项',
          '',
          '## QA Signoff',
          '- Reviewer: `pending`',
          '- Date: `pending`',
          '- Decision: `pending`',
          '- Notes:',
          '  - 待确认QA项',
          '',
          '## Final Gate',
          '- Closeout decision: `pending signoff`',
          '- Decision date: `pending`',
          '',
        ].join('\n'),
        'utf8',
      );

      signoffUpdate.updateSignoffRecord({
        repoRoot: tempDir,
        party: 'design',
        reviewer: 'Designer A',
        date: '2026-04-04',
        decision: 'approved',
        notes: ['设计确认视觉系统目标达成', '组件映射可持续维护'],
      });

      const content = fs.readFileSync(signoffPath, 'utf8');
      expect(content).toContain('- Reviewer: `Designer A`');
      expect(content).toContain('- Date: `2026-04-04`');
      expect(content).toContain('- Decision: `approved`');
      expect(content).toContain('  - 设计确认视觉系统目标达成');
      expect(content).toContain('  - 组件映射可持续维护');
      expect(content).toContain('\n## Product Signoff');
      expect(content).toContain('- Reviewer: `pending`');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('can set final gate when explicitly requested', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'stage7-signoff-final-'));

    try {
      const signoffPath = path.join(tempDir, 'docs', 'refactor', 'stage-7-signoff-record.md');
      fs.mkdirSync(path.dirname(signoffPath), { recursive: true });
      fs.writeFileSync(
        signoffPath,
        [
          '## Design Signoff',
          '- Reviewer: `Designer A`',
          '- Date: `2026-04-04`',
          '- Decision: `approved`',
          '- Notes:',
          '  - done',
          '',
          '## Product Signoff',
          '- Reviewer: `PM A`',
          '- Date: `2026-04-04`',
          '- Decision: `approved-with-notes`',
          '- Notes:',
          '  - done',
          '',
          '## QA Signoff',
          '- Reviewer: `QA A`',
          '- Date: `2026-04-04`',
          '- Decision: `approved`',
          '- Notes:',
          '  - done',
          '',
          '## Final Gate',
          '- Closeout decision: `pending signoff`',
          '- Decision date: `pending`',
          '',
        ].join('\n'),
        'utf8',
      );

      signoffUpdate.updateSignoffRecord({
        repoRoot: tempDir,
        party: 'qa',
        reviewer: 'QA A',
        date: '2026-04-04',
        decision: 'approved',
        notes: ['done'],
        finalDecision: 'Stage 7 = validated',
        finalDecisionDate: '2026-04-04',
      });

      const content = fs.readFileSync(signoffPath, 'utf8');
      expect(content).toContain('- Closeout decision: `Stage 7 = validated`');
      expect(content).toContain('- Decision date: `2026-04-04`');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
