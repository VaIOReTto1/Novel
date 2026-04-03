const fs = require('fs');
const os = require('os');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const closeoutReadiness = require('../../scripts/stage7-closeout-readiness.js');

describe('stage7 closeout readiness', () => {
  test('generates a readiness report with current blockers and gate evidence', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'stage7-closeout-'));

    try {
      const outputPath = closeoutReadiness.generateStage7CloseoutReadiness({
        repoRoot,
        outputPath: path.join(tempDir, 'stage-7-closeout-readiness.md'),
      });

      expect(fs.existsSync(outputPath)).toBe(true);
      const report = fs.readFileSync(outputPath, 'utf8');

      expect(report).toContain('# Stage 7 Closeout Readiness');
      expect(report).toContain('Technical gates');
      expect(report).toContain('Full Jest');
      expect(report).toContain('Android shared gate');
      expect(report).toContain('RN smoke tests: 16');
      expect(report).toContain('Figma frame map');
      expect(report).toContain('Unmapped surfaces: 0');
      expect(report).toContain('External blockers');
      expect(report).toContain('Design / Product / QA signoff remains pending');
      expect(report).toContain('Overall status: ready_for_closeout_except_figma_and_signoff');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('check fails when the readiness report is stale', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'stage7-closeout-check-'));

    try {
      const outputPath = path.join(tempDir, 'stage-7-closeout-readiness.md');
      closeoutReadiness.generateStage7CloseoutReadiness({
        repoRoot,
        outputPath,
      });
      fs.writeFileSync(outputPath, '# stale\n', 'utf8');

      const result = closeoutReadiness.checkStage7CloseoutReadiness({
        repoRoot,
        outputPath,
      });

      expect(result.ok).toBe(false);
      expect(result.message).toContain('stale');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
