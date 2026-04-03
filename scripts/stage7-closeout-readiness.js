#!/usr/bin/env node

const fs = require('fs');
const os = require('os');
const path = require('path');

const DEFAULT_OUTPUT_PATH = path.join(
  'docs',
  'refactor',
  'phase-18',
  'stage-7-closeout-readiness.md',
);

const readText = (repoRoot, relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

const parseJson = (repoRoot, relativePath) =>
  JSON.parse(readText(repoRoot, relativePath));

const ensureDir = (dirPath) => {
  fs.mkdirSync(dirPath, { recursive: true });
};

const writeText = (absolutePath, content) => {
  ensureDir(path.dirname(absolutePath));
  fs.writeFileSync(absolutePath, content, 'utf8');
};

const resolveOutputPath = (repoRoot, outputPath) =>
  path.isAbsolute(outputPath) ? outputPath : path.join(repoRoot, outputPath);

const capture = (text, pattern, fallback = 'unknown') => {
  const match = text.match(pattern);
  return match ? match[1].trim() : fallback;
};

const captureSignoffField = (text, sectionTitle, label, fallback = 'unknown') => {
  const escapedTitle = sectionTitle.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const escapedLabel = label.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const pattern = new RegExp(
    `## ${escapedTitle}[\\s\\S]*?- ${escapedLabel}:\\s*` + '`?([^\\n`]+)`?',
  );
  return capture(text, pattern, fallback);
};

const collectSmokeTests = (repoRoot) => {
  const smokeDir = path.join(repoRoot, '__tests__', 'smoke');
  if (!fs.existsSync(smokeDir)) {
    return [];
  }

  return fs
    .readdirSync(smokeDir)
    .filter((name) => name.endsWith('.smoke.test.tsx'))
    .sort();
};

const buildReadinessModel = (repoRoot) => {
  const validationBoard = readText(
    repoRoot,
    'docs/refactor/tracking/phase-15-18-validation-board.md',
  );
  const stageSummary = readText(
    repoRoot,
    'docs/refactor/stage-7-closeout-summary.md',
  );
  const signoffRecord = readText(
    repoRoot,
    'docs/refactor/stage-7-signoff-record.md',
  );
  const figmaFrameMap = parseJson(repoRoot, 'docs/refactor/phase-15/figma-frame-map.json');
  const copyrightLedger = parseJson(
    repoRoot,
    'design-system/assets/copyright-ledger.json',
  );
  const smokeTests = collectSmokeTests(repoRoot);
  const externalBlockers = [];

  if (
    stageSummary.includes('亮色稿') ||
    stageSummary.includes('暗色稿') ||
    stageSummary.includes('标注稿') ||
    stageSummary.includes('组件映射')
  ) {
    externalBlockers.push(
      'Light / dark / annotation / component-mapping evidence in the official Figma host is still incomplete',
    );
  }

  if (stageSummary.includes('三方评审') || stageSummary.includes('待签核')) {
    externalBlockers.push('Design / Product / QA signoff remains pending');
  }

  const signoff = {
    design: {
      reviewer: captureSignoffField(signoffRecord, 'Design Signoff', 'Reviewer', 'pending'),
      date: captureSignoffField(signoffRecord, 'Design Signoff', 'Date', 'pending'),
      decision: captureSignoffField(signoffRecord, 'Design Signoff', 'Decision', 'pending'),
    },
    product: {
      reviewer: captureSignoffField(signoffRecord, 'Product Signoff', 'Reviewer', 'pending'),
      date: captureSignoffField(signoffRecord, 'Product Signoff', 'Date', 'pending'),
      decision: captureSignoffField(signoffRecord, 'Product Signoff', 'Decision', 'pending'),
    },
    qa: {
      reviewer: captureSignoffField(signoffRecord, 'QA Signoff', 'Reviewer', 'pending'),
      date: captureSignoffField(signoffRecord, 'QA Signoff', 'Date', 'pending'),
      decision: captureSignoffField(signoffRecord, 'QA Signoff', 'Decision', 'pending'),
    },
  };

  return {
    overallStatus: capture(stageSummary, /当前状态：`([^`]+)`/),
    latestUpdate: capture(validationBoard, /最新更新：`([^`]+)`/),
    technicalGates: {
      fullJest:
        validationBoard.includes('108` 个 suites / `260` 个 tests 全绿') ||
        validationBoard.includes('108` 个 suites / `260` 个 tests'),
      androidSharedGate: validationBoard.includes(
        'app:testDebugUnitTest app:lintDebug app:compileDebugAndroidTestKotlin :macrobenchmark:assemble',
      ),
      smokeCatalogDriftNone: validationBoard.includes('smoke catalog drift 为 `none`'),
    },
    smoke: {
      count: smokeTests.length,
      names: smokeTests,
    },
    figma: {
      totalSurfaces: figmaFrameMap.length,
      unmappedSurfaces: figmaFrameMap.filter((entry) => !entry.figma_frame_id).length,
    },
    assets: {
      copyrightLedgerEntries: Array.isArray(copyrightLedger.entries)
        ? copyrightLedger.entries.length
        : 0,
    },
    signoff,
    externalBlockers,
  };
};

const renderReadinessReport = (model) => [
  '# Stage 7 Closeout Readiness',
  '',
  '## Summary',
  `- Overall status: ${model.overallStatus}`,
  `- Latest update: ${model.latestUpdate}`,
  '',
  '## Technical gates',
  `- Full Jest: ${model.technicalGates.fullJest ? 'pass' : 'missing'}`,
  `- Android shared gate: ${model.technicalGates.androidSharedGate ? 'pass' : 'missing'}`,
  `- Smoke catalog drift: ${model.technicalGates.smokeCatalogDriftNone ? 'none' : 'present'}`,
  '',
  '## Smoke coverage',
  `- RN smoke tests: ${model.smoke.count}`,
  ...model.smoke.names.map((name) => `- ${name}`),
  '',
  '## Asset governance',
  `- Copyright ledger entries: ${model.assets.copyrightLedgerEntries}`,
  '',
  '## Signoff status',
  `- Design: ${model.signoff.design.decision} (reviewer: ${model.signoff.design.reviewer}, date: ${model.signoff.design.date})`,
  `- Product: ${model.signoff.product.decision} (reviewer: ${model.signoff.product.reviewer}, date: ${model.signoff.product.date})`,
  `- QA: ${model.signoff.qa.decision} (reviewer: ${model.signoff.qa.reviewer}, date: ${model.signoff.qa.date})`,
  '',
  '## Figma frame map',
  `- Total surfaces: ${model.figma.totalSurfaces}`,
  `- Unmapped surfaces: ${model.figma.unmappedSurfaces}`,
  '',
  '## External blockers',
  ...(model.externalBlockers.length
    ? model.externalBlockers.map((item) => `- ${item}`)
    : ['- none']),
  '',
].join('\n') + '\n';

const generateStage7CloseoutReadiness = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputPath = DEFAULT_OUTPUT_PATH,
} = {}) => {
  const resolvedOutputPath = resolveOutputPath(repoRoot, outputPath);
  writeText(resolvedOutputPath, renderReadinessReport(buildReadinessModel(repoRoot)));
  return resolvedOutputPath;
};

const checkStage7CloseoutReadiness = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputPath = DEFAULT_OUTPUT_PATH,
} = {}) => {
  const resolvedOutputPath = resolveOutputPath(repoRoot, outputPath);
  if (!fs.existsSync(resolvedOutputPath)) {
    return {
      ok: false,
      message: `Missing readiness report: ${resolvedOutputPath}`,
    };
  }

  const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'stage7-closeout-readiness-'));
  try {
    const freshOutputPath = path.join(tempDir, 'stage-7-closeout-readiness.md');
    generateStage7CloseoutReadiness({
      repoRoot,
      outputPath: freshOutputPath,
    });

    const currentContent = fs.readFileSync(resolvedOutputPath, 'utf8');
    const freshContent = fs.readFileSync(freshOutputPath, 'utf8');
    if (currentContent !== freshContent) {
      return {
        ok: false,
        message: 'Stage 7 closeout readiness report is stale.',
      };
    }

    return {
      ok: true,
      message: 'Stage 7 closeout readiness report is up to date.',
    };
  } finally {
    fs.rmSync(tempDir, { recursive: true, force: true });
  }
};

const main = () => {
  const command = process.argv[2] || 'generate';
  const repoRoot = path.resolve(__dirname, '..');

  if (command === 'generate') {
    const outputPath = generateStage7CloseoutReadiness({ repoRoot });
    console.log(`Stage 7 closeout readiness written to ${outputPath}`);
    return;
  }

  if (command === 'check') {
    const result = checkStage7CloseoutReadiness({ repoRoot });
    if (!result.ok) {
      console.error(result.message);
      process.exit(1);
    }
    console.log(result.message);
    return;
  }

  console.error(`Unknown command: ${command}`);
  process.exit(1);
};

module.exports = {
  buildReadinessModel,
  checkStage7CloseoutReadiness,
  generateStage7CloseoutReadiness,
  renderReadinessReport,
};

if (require.main === module) {
  main();
}
