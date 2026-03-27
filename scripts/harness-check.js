#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const ROOT = path.resolve(__dirname, '..');
const errors = [];
const warnings = [];

const requiredFiles = [
  'AGENTS.md',
  'ARCHITECTURE.md',
  '.trae/rules/project_rules.md',
  'docs/harness/README.md',
  'docs/harness/core-beliefs.md',
  'docs/harness/current-focus.md',
  'docs/harness/session-log.md',
  'docs/harness/references/verification.md',
  'docs/harness/exec-plans/active/index.md',
  'docs/harness/exec-plans/active/2026-03-26-harness-rollout-v2.md',
  'docs/harness/exec-plans/completed/index.md',
  'docs/harness/exec-plans/tech-debt-tracker.md',
  'docs/harness/quality-score.md',
  'docs/harness/generated/workspace-snapshot.md',
];

const requiredCurrentFocusHeadings = [
  '## 当前状态',
  '## 最近完成',
  '## 默认下一主线',
  '## Blockers / Known Drift',
  '## Primary Source Refs',
  '## Last Reviewed',
];

const readText = (relativePath) =>
  fs.readFileSync(path.join(ROOT, relativePath), 'utf8');

const exists = (relativePath) => fs.existsSync(path.join(ROOT, relativePath));

const run = (command) =>
  execSync(command, {
    cwd: ROOT,
    encoding: 'utf8',
    stdio: ['ignore', 'pipe', 'pipe'],
  }).trim();

const capture = (text, pattern, fallback = 'unknown') => {
  const match = text.match(pattern);
  return match ? match[1].trim() : fallback;
};

const addError = (message) => {
  errors.push(message);
};

const addWarning = (message) => {
  warnings.push(message);
};

const walkMarkdownFiles = (startPath, acc = []) => {
  if (!fs.existsSync(startPath)) {
    return acc;
  }

  for (const entry of fs.readdirSync(startPath, { withFileTypes: true })) {
    const nextPath = path.join(startPath, entry.name);
    if (entry.isDirectory()) {
      walkMarkdownFiles(nextPath, acc);
      continue;
    }
    if (entry.isFile() && /\.md$/i.test(entry.name)) {
      acc.push(nextPath);
    }
  }

  return acc;
};

const validateRequiredFiles = () => {
  for (const filePath of requiredFiles) {
    if (!exists(filePath)) {
      addError(`Missing required file: ${filePath}`);
    }
  }
};

const validateAgentsLinks = () => {
  if (!exists('AGENTS.md')) {
    return;
  }

  const agents = readText('AGENTS.md');
  for (const requiredLink of [
    'ARCHITECTURE.md',
    'docs/harness/README.md',
    'docs/refactor/README.md',
  ]) {
    if (!agents.includes(requiredLink)) {
      addError(`AGENTS.md must link to ${requiredLink}`);
    }
  }
};

const validateCommitRule = () => {
  if (!exists('AGENTS.md') || !exists('docs/harness/core-beliefs.md')) {
    return;
  }

  const agents = readText('AGENTS.md');
  const beliefs = readText('docs/harness/core-beliefs.md');

  for (const [label, source] of [
    ['AGENTS.md', agents],
    ['docs/harness/core-beliefs.md', beliefs],
  ]) {
    if (!source.includes('atomic change')) {
      addError(`${label} must mention the atomic change rule`);
    }
    if (!source.includes('Chinese commit message')) {
      addError(`${label} must require a Chinese commit message`);
    }
  }
};

const validateCurrentFocusStructure = () => {
  if (!exists('docs/harness/current-focus.md')) {
    return;
  }

  const currentFocus = readText('docs/harness/current-focus.md');
  for (const heading of requiredCurrentFocusHeadings) {
    if (!currentFocus.includes(heading)) {
      addError(`current-focus.md is missing heading: ${heading}`);
    }
  }
};

const validateRelativeLinks = () => {
  const filesToScan = [
    path.join(ROOT, 'AGENTS.md'),
    path.join(ROOT, 'ARCHITECTURE.md'),
    path.join(ROOT, '.trae', 'rules', 'project_rules.md'),
    ...walkMarkdownFiles(path.join(ROOT, 'docs', 'harness')),
  ];

  const linkPattern = /\[[^\]]+\]\(([^)]+)\)/g;

  for (const absolutePath of filesToScan) {
    if (!fs.existsSync(absolutePath)) {
      continue;
    }

    const source = fs.readFileSync(absolutePath, 'utf8');
    let match;
    while ((match = linkPattern.exec(source)) !== null) {
      const rawLink = match[1].split('#')[0];
      if (!rawLink || /^(https?:|mailto:|#)/.test(rawLink)) {
        continue;
      }

      const resolvedPath = path.resolve(path.dirname(absolutePath), rawLink);
      const displayPath = path.relative(ROOT, absolutePath);
      if (!fs.existsSync(resolvedPath)) {
        addError(`${displayPath} contains a broken relative link: ${match[1]}`);
      }
    }
  }
};

const validatePrimarySourceRefs = () => {
  if (!exists('docs/harness/current-focus.md')) {
    return;
  }

  const currentFocus = readText('docs/harness/current-focus.md');
  const links = [...currentFocus.matchAll(/\[[^\]]+\]\(([^)]+)\)/g)];
  if (links.length === 0) {
    addError('current-focus.md must include Primary Source Refs links');
    return;
  }

  for (const [, link] of links) {
    if (/^https?:/.test(link)) {
      continue;
    }

    const target = path.resolve(
      ROOT,
      'docs',
      'harness',
      link.split('#')[0],
    );
    if (!fs.existsSync(target)) {
      addError(`current-focus.md references a missing source file: ${link}`);
    }
  }
};

const validateCurrentFocusFreshness = () => {
  if (!exists('docs/harness/current-focus.md')) {
    return;
  }

  const currentFocus = readText('docs/harness/current-focus.md');
  const refactorReadme = readText('docs/refactor/README.md');
  const currentPhase = capture(refactorReadme, /当前阶段：`([^`]+)`/);
  const currentPhaseStatus = capture(refactorReadme, /阶段状态：`([^`]+)`/);

  if (!currentFocus.includes(currentPhase)) {
    addWarning(
      `current-focus.md may be stale: it does not mention current phase ${currentPhase}`,
    );
  }

  if (!currentFocus.includes(currentPhaseStatus)) {
    addWarning(
      `current-focus.md may be stale: it does not mention current phase status ${currentPhaseStatus}`,
    );
  }
};

validateRequiredFiles();
validateAgentsLinks();
validateCommitRule();
validateCurrentFocusStructure();
validateRelativeLinks();
validatePrimarySourceRefs();
validateCurrentFocusFreshness();

if (errors.length > 0) {
  console.error('Harness check failed.');
  for (const message of errors) {
    console.error(`ERROR: ${message}`);
  }
  for (const message of warnings) {
    console.warn(`WARN: ${message}`);
  }
  process.exit(1);
}

const summary = warnings.length
  ? `Harness check passed with ${warnings.length} warning(s).`
  : 'Harness check passed.';

console.log(summary);
for (const message of warnings) {
  console.warn(`WARN: ${message}`);
}
