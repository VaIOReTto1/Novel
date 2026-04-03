const fs = require('fs');
const os = require('os');
const path = require('path');
const { execFileSync } = require('child_process');

const repoRoot = path.resolve(__dirname, '..', '..');
const docsRoot = path.join(repoRoot, 'docs', 'harness');
const snapshotPath = path.join(docsRoot, 'generated', 'workspace-snapshot.md');

const runNodeScript = (relativeScriptPath) =>
  execFileSync(process.execPath, [path.join(repoRoot, relativeScriptPath)], {
    cwd: repoRoot,
    encoding: 'utf8',
    stdio: ['ignore', 'pipe', 'pipe'],
  });

const runNodeScriptInRepo = (cwd, relativeScriptPath) =>
  execFileSync(process.execPath, [path.join(cwd, relativeScriptPath)], {
    cwd,
    encoding: 'utf8',
    stdio: ['ignore', 'pipe', 'pipe'],
  });

const runGit = (args) =>
  execFileSync('git', args, {
    cwd: repoRoot,
    encoding: 'utf8',
    stdio: ['ignore', 'pipe', 'pipe'],
  }).trim();

const runGitInRepo = (cwd, args) =>
  execFileSync('git', args, {
    cwd,
    encoding: 'utf8',
    stdio: ['ignore', 'pipe', 'pipe'],
  }).trim();

const readText = (relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

const copyRepoFile = (relativePath, targetRepo) => {
  const source = path.join(repoRoot, relativePath);
  const destination = path.join(targetRepo, relativePath);
  fs.mkdirSync(path.dirname(destination), { recursive: true });
  fs.copyFileSync(source, destination);
};

const capture = (text, pattern, fallback = 'unknown') => {
  const match = text.match(pattern);
  return match ? match[1].trim() : fallback;
};

describe('harness control plane', () => {
  test('refresh generates a workspace snapshot with current repo facts', () => {
    if (fs.existsSync(snapshotPath)) {
      fs.unlinkSync(snapshotPath);
    }

    runNodeScript('scripts/harness-refresh.js');

    const snapshot = fs.readFileSync(snapshotPath, 'utf8');
    const currentBranch = runGit(['branch', '--show-current']);
    const refactorReadme = readText('docs/refactor/README.md');
    const currentPhase = capture(refactorReadme, /当前阶段：`([^`]+)`/);
    const currentPhaseStatus = capture(refactorReadme, /阶段状态：`([^`]+)`/);
    const currentStageStatus = capture(refactorReadme, /Stage 状态：`([^`]+)`/);

    expect(snapshot).toContain('generated, do not edit by hand');
    expect(snapshot).toContain(currentBranch);
    expect(snapshot).toContain(`- Current phase: ${currentPhase}`);
    expect(snapshot).toContain(`- Phase status: ${currentPhaseStatus}`);
    expect(snapshot).toContain(currentStageStatus);
    expect(snapshot).not.toContain('unknown');
    expect(snapshot).not.toContain('\n- |');
    expect(snapshot).toContain(':feature-reader');
    expect(snapshot).toContain('app:testDebugUnitTest');
    expect(snapshot).toContain('sudo udevadm control --reload-rules');
  });

  test('check passes after refresh and validates the harness structure', () => {
    runNodeScript('scripts/harness-refresh.js');

    const output = runNodeScript('scripts/harness-check.js');
    expect(output).toContain('Harness check passed');
  });

  test('snapshot remains valid across a metadata-only commit when repo facts do not change', () => {
    const tempRepo = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-harness-'));

    try {
      runGitInRepo(repoRoot, ['clone', '--quiet', '--no-hardlinks', repoRoot, tempRepo]);
      runGitInRepo(tempRepo, ['config', 'user.name', 'Harness Test']);
      runGitInRepo(tempRepo, ['config', 'user.email', 'harness-test@example.com']);
      copyRepoFile('scripts/harness-refresh.js', tempRepo);
      copyRepoFile('scripts/harness-check.js', tempRepo);

      runNodeScriptInRepo(tempRepo, 'scripts/harness-refresh.js');
      const tempSnapshotPath = path.join(
        tempRepo,
        'docs',
        'harness',
        'generated',
        'workspace-snapshot.md',
      );
      const beforeEmptyCommit = fs.readFileSync(tempSnapshotPath, 'utf8');

      runGitInRepo(tempRepo, ['commit', '--allow-empty', '-m', 'temp empty commit']);

      const checkOutput = runNodeScriptInRepo(tempRepo, 'scripts/harness-check.js');
      expect(checkOutput).toContain('Harness check passed');

      runNodeScriptInRepo(tempRepo, 'scripts/harness-refresh.js');
      const afterEmptyCommit = fs.readFileSync(tempSnapshotPath, 'utf8');

      expect(afterEmptyCommit).toBe(beforeEmptyCommit);
    } finally {
      fs.rmSync(tempRepo, { recursive: true, force: true });
    }
  });

  test('current focus and active exec plans expose current project-level routing', () => {
    const currentFocus = readText('docs/harness/current-focus.md');
    const activeIndex = readText('docs/harness/exec-plans/active/index.md');

    expect(currentFocus).toContain('## 当前状态');
    expect(currentFocus).toContain('## 默认下一主线');
    expect(currentFocus).toContain('## Primary Source Refs');
    expect(currentFocus).toContain('docs/refactor/README.md');

    expect(activeIndex).toContain('docs/refactor/master-roadmap.md');
    expect(activeIndex).toContain('harness-rollout-v2');
  });

  test('Trae shim redirects readers to the new harness entrypoints', () => {
    const shim = readText('.trae/rules/project_rules.md');

    expect(shim).toContain('AGENTS.md');
    expect(shim).toContain('docs/harness/README.md');
    expect(shim).not.toContain('Sequential Thinking');
  });

  test('harness docs require Chinese commits for each atomic change', () => {
    const agents = readText('AGENTS.md');
    const beliefs = readText('docs/harness/core-beliefs.md');

    expect(agents).toContain('atomic change');
    expect(agents).toContain('Chinese commit message');
    expect(beliefs).toContain('atomic change');
    expect(beliefs).toContain('Chinese commit message');
  });
});
