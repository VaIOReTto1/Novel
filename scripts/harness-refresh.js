#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const ROOT = path.resolve(__dirname, '..');
const SNAPSHOT_PATH = path.join(
  ROOT,
  'docs',
  'harness',
  'generated',
  'workspace-snapshot.md',
);

const readText = (relativePath) =>
  fs.readFileSync(path.join(ROOT, relativePath), 'utf8');

const ensureDir = (dirPath) => {
  fs.mkdirSync(dirPath, { recursive: true });
};

const writeText = (absolutePath, content) => {
  ensureDir(path.dirname(absolutePath));
  fs.writeFileSync(absolutePath, content, 'utf8');
};

const run = (command) =>
  execSync(command, {
    cwd: ROOT,
    encoding: 'utf8',
    stdio: ['ignore', 'pipe', 'pipe'],
  }).trim();

const unique = (items) => [...new Set(items.filter(Boolean))];

const capture = (text, pattern, fallback = 'unknown') => {
  const match = text.match(pattern);
  return match ? match[1].trim() : fallback;
};

const walkFiles = (startPath, predicate, acc = []) => {
  if (!fs.existsSync(startPath)) {
    return acc;
  }

  for (const entry of fs.readdirSync(startPath, { withFileTypes: true })) {
    const nextPath = path.join(startPath, entry.name);
    if (entry.isDirectory()) {
      walkFiles(nextPath, predicate, acc);
      continue;
    }
    if (predicate(nextPath)) {
      acc.push(nextPath);
    }
  }

  return acc;
};

const getGitState = () => {
  const head = run('git rev-parse HEAD');
  return {
    branch: run('git branch --show-current'),
    head,
    shortHead: run('git rev-parse --short HEAD'),
    recentCommits: run('git log --oneline -n 8').split(/\r?\n/),
  };
};

const getAndroidModules = () => {
  const settingsGradle = readText('android/settings.gradle');
  const modules = [];

  for (const line of settingsGradle.split(/\r?\n/)) {
    const match = line.match(/^include\s+['"](:[^'"]+)['"]/);
    if (match) {
      modules.push(match[1]);
    }
  }

  return modules;
};

const getRnRegistrations = () => {
  const appJson = JSON.parse(readText('app.json'));
  const sourceFiles = [
    path.join(ROOT, 'index.js'),
    ...walkFiles(path.join(ROOT, 'src'), (filePath) =>
      /\.(js|jsx|ts|tsx)$/.test(filePath),
    ),
  ];
  const explicitComponents = new Set();

  for (const filePath of sourceFiles) {
    const source = fs.readFileSync(filePath, 'utf8');
    const pattern = /AppRegistry\.registerComponent\(\s*['"]([^'"]+)['"]/g;
    let match;
    while ((match = pattern.exec(source)) !== null) {
      explicitComponents.add(match[1]);
    }
  }

  return {
    rootAppName: appJson.name,
    explicitComponents: [...explicitComponents].sort(),
  };
};

const getVerificationCommands = () => {
  const packageJson = JSON.parse(readText('package.json'));
  const workflow = readText('.github/workflows/quality-gates.yml');
  const commands = [];

  for (const [name, command] of Object.entries(packageJson.scripts || {})) {
    commands.push(`npm run ${name} -> ${command}`);
  }

  const workflowLines = workflow.split(/\r?\n/);
  let inScriptBlock = false;
  let scriptIndent = 0;

  for (const line of workflowLines) {
    const trimmed = line.trim();
    const indent = line.length - line.trimStart().length;

    if (inScriptBlock) {
      if (trimmed && indent > scriptIndent) {
        commands.push(trimmed);
        continue;
      }
      if (trimmed && indent <= scriptIndent) {
        inScriptBlock = false;
      }
    }

    if (trimmed.startsWith('run: ')) {
      commands.push(trimmed.slice(5).trim());
    }

    if (trimmed === 'script: |') {
      inScriptBlock = true;
      scriptIndent = indent;
    }
  }

  return unique(commands);
};

const getRefactorSummary = () => {
  const refactorReadme = readText('docs/refactor/README.md');
  const stageSummary = readText('docs/refactor/stage-3-closeout-summary.md');

  const currentPhase = capture(refactorReadme, /当前阶段：`([^`]+)`/);
  const phaseStatus = capture(refactorReadme, /阶段状态：`([^`]+)`/);
  const latestCloseout = capture(
    refactorReadme,
    /最近结论：`([^`]+)`/,
    'see-control-panel',
  );
  const stageLabel = capture(stageSummary, /阶段：`([^`]+)`/, 'Stage 3');
  const stageStatus = capture(stageSummary, /当前状态：`([^`]+)`/);

  return {
    currentPhase,
    phaseStatus,
    latestCloseout,
    stageSummaryLine: `${stageLabel} = ${stageStatus}`,
    effectiveDate: 'see-stage-summary',
  };
};

const renderSnapshot = () => {
  const gitState = getGitState();
  const modules = getAndroidModules();
  const registrations = getRnRegistrations();
  const commands = getVerificationCommands();
  const refactor = getRefactorSummary();

  return [
    '<!-- generated, do not edit by hand -->',
    `<!-- source-head: ${gitState.head} -->`,
    '# Workspace Snapshot',
    '',
    '## Git State',
    `- Branch: \`${gitState.branch}\``,
    `- Head: \`${gitState.shortHead}\``,
    '',
    '## Recent Commits',
    ...gitState.recentCommits.map((commit) => `- ${commit}`),
    '',
    '## Android Modules',
    ...modules.map((moduleName) => `- ${moduleName}`),
    '',
    '## RN Registrations',
    `- Root app: ${registrations.rootAppName} (registered through \`appName\` in \`index.js\`)`,
    ...registrations.explicitComponents.map(
      (componentName) => `- ${componentName}`,
    ),
    '',
    '## Verification And Build Commands',
    ...commands.map((command) => `- ${command}`),
    '',
    '## Refactor Summary',
    `- Current phase: ${refactor.currentPhase}`,
    `- Phase status: ${refactor.phaseStatus}`,
    `- Latest closeout: ${refactor.latestCloseout}`,
    `- Stage summary: ${refactor.stageSummaryLine}`,
    `- Effective date: ${refactor.effectiveDate}`,
    '',
  ].join('\n');
};

writeText(SNAPSHOT_PATH, renderSnapshot());
console.log(`Harness snapshot written to ${SNAPSHOT_PATH}`);
