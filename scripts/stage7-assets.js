#!/usr/bin/env node

const fs = require('fs');
const os = require('os');
const path = require('path');

const PROVIDER_SOURCE = path.join('design-system', 'source', 'stage7.asset-providers.json');

const ensureDir = (dirPath) => {
  fs.mkdirSync(dirPath, { recursive: true });
};

const writeText = (absolutePath, content) => {
  ensureDir(path.dirname(absolutePath));
  fs.writeFileSync(absolutePath, content, 'utf8');
};

const writeJson = (absolutePath, data) => {
  writeText(absolutePath, `${JSON.stringify(data, null, 2)}\n`);
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
    if (!predicate || predicate(nextPath)) {
      acc.push(nextPath);
    }
  }

  return acc;
};

const toPosix = (value) => value.split(path.sep).join('/');

const relativeRepoPath = (repoRoot, absolutePath) =>
  toPosix(path.relative(repoRoot, absolutePath));

const unique = (items) => [...new Set(items.filter(Boolean))];

const resolveOutputRoot = (repoRoot, outputRoot) =>
  path.isAbsolute(outputRoot) ? outputRoot : path.join(repoRoot, outputRoot);

const loadProviderConfig = ({ repoRoot = path.resolve(__dirname, '..') } = {}) =>
  JSON.parse(fs.readFileSync(path.join(repoRoot, PROVIDER_SOURCE), 'utf8'));

const collectVectorIconFamilies = (repoRoot) => {
  const families = new Set();
  const rnFiles = walkFiles(
    path.join(repoRoot, 'src'),
    (filePath) => /\.(ts|tsx|js|jsx)$/.test(filePath),
  );

  rnFiles.forEach((filePath) => {
    const source = fs.readFileSync(filePath, 'utf8');
    const pattern = /react-native-vector-icons\/([A-Za-z0-9]+)/g;
    let match;
    while ((match = pattern.exec(source)) !== null) {
      families.add(match[1]);
    }
  });

  return [...families].sort();
};

const buildIconManifest = (repoRoot) => {
  const svgAssets = walkFiles(
    path.join(repoRoot, 'assets', 'image'),
    (filePath) => filePath.endsWith('.svg'),
  );
  const families = collectVectorIconFamilies(repoRoot);
  const entries = [
    ...svgAssets.map((filePath) => {
      const baseName = path.basename(filePath, '.svg');
      return {
        semantic_name: `legacy.${baseName}`,
        source_type: 'legacy-local-svg',
        source: relativeRepoPath(repoRoot, filePath),
        migration_target: 'iconify',
        rtl_mirror: false,
        tint_mode: 'dynamic',
      };
    }),
    ...families.map((family) => ({
      semantic_name: `legacy.${family.toLowerCase()}`,
      source_type: 'react-native-vector-icons-family',
      source: family,
      migration_target: 'iconify',
      rtl_mirror: false,
      tint_mode: 'dynamic',
    })),
  ];

  return {
    summary: {
      local_svg_count: svgAssets.length,
      vector_icon_families: families,
    },
    entries: entries.sort((left, right) =>
      left.semantic_name.localeCompare(right.semantic_name),
    ),
  };
};

const buildMediaManifest = (providers) => ({
  providers: {
    placeholder: providers.placeholder,
    photo: providers.photo,
  },
  component_contract: {
    placeholder_props: providers.placeholder.configurable_params,
    credit_overlay_required: providers.photo.required_credit_overlay,
  },
});

const buildIllustrationManifest = (providers) => ({
  provider: providers.illustration,
  entries: [],
});

const buildCopyrightLedger = () => ({
  schema_version: '1.0.0',
  entries: [],
});

const buildAssetGovernanceReport = ({ iconManifest, providers, ledger }) => [
  '# Stage 7 Asset Governance Report',
  '',
  '## Summary',
  `- Local SVG icons: ${iconManifest.summary.local_svg_count}`,
  `- Vector icon families: ${iconManifest.summary.vector_icon_families.join(', ') || 'none'}`,
  `- Placeholder provider: ${providers.placeholder.name}`,
  `- Photo provider: ${providers.photo.name}`,
  `- Illustration provider: ${providers.illustration.name}`,
  `- Copyright ledger entries: ${ledger.entries.length}`,
  '',
  '## Migration targets',
  '- Icons migrate to `iconify` with local manifest ownership.',
  '- Placeholder images use fixed-seed `picsum` requests for deterministic review and regression.',
  '- Real photos are sourced from `pexels`, never searched at runtime, and always require credit overlay plus ledger entry.',
  '- Illustrations are sourced from `undraw` and recolored through brand tokens.',
  '',
].join('\n');

const generateAssetArtifacts = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputRoot = repoRoot,
} = {}) => {
  const resolvedRoot = resolveOutputRoot(repoRoot, outputRoot);
  const providers = loadProviderConfig({ repoRoot });
  const iconManifest = buildIconManifest(repoRoot);
  const mediaManifest = buildMediaManifest(providers);
  const illustrationManifest = buildIllustrationManifest(providers);
  const copyrightLedger = buildCopyrightLedger();
  const report = buildAssetGovernanceReport({
    iconManifest,
    providers,
    ledger: copyrightLedger,
  });

  const outputs = {
    iconManifestPath: path.join(resolvedRoot, 'design-system', 'assets', 'icon-manifest.json'),
    mediaManifestPath: path.join(resolvedRoot, 'design-system', 'assets', 'media-manifest.json'),
    illustrationManifestPath: path.join(resolvedRoot, 'design-system', 'assets', 'illustration-manifest.json'),
    copyrightLedgerPath: path.join(resolvedRoot, 'design-system', 'assets', 'copyright-ledger.json'),
    reportPath: path.join(resolvedRoot, 'docs', 'refactor', 'phase-17', 'asset-governance-report.md'),
  };

  writeJson(outputs.iconManifestPath, iconManifest);
  writeJson(outputs.mediaManifestPath, mediaManifest);
  writeJson(outputs.illustrationManifestPath, illustrationManifest);
  writeJson(outputs.copyrightLedgerPath, copyrightLedger);
  writeText(outputs.reportPath, `${report}\n`);

  return outputs;
};

const checkAssetArtifacts = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputRoot = repoRoot,
} = {}) => {
  const resolvedRoot = resolveOutputRoot(repoRoot, outputRoot);
  const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-assets-check-'));

  try {
    const fresh = generateAssetArtifacts({ repoRoot, outputRoot: tempDir });
    const current = generateAssetArtifacts({ repoRoot, outputRoot: resolvedRoot });

    const mismatched = Object.keys(current).filter((key) => {
      const currentContent = fs.readFileSync(current[key], 'utf8');
      const freshContent = fs.readFileSync(fresh[key], 'utf8');
      return currentContent !== freshContent;
    });

    if (mismatched.length > 0) {
      return {
        ok: false,
        message: `Stage 7 asset artifacts are stale: ${mismatched.join(', ')}`,
      };
    }

    return {
      ok: true,
      message: 'Stage 7 asset artifacts are up to date.',
    };
  } finally {
    fs.rmSync(tempDir, { recursive: true, force: true });
  }
};

const main = () => {
  const command = process.argv[2] || 'generate';
  const repoRoot = path.resolve(__dirname, '..');

  if (command === 'generate') {
    const outputs = generateAssetArtifacts({ repoRoot });
    console.log(`Stage 7 asset artifacts written to ${path.dirname(outputs.iconManifestPath)}`);
    return;
  }

  if (command === 'check') {
    const result = checkAssetArtifacts({ repoRoot });
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
  checkAssetArtifacts,
  generateAssetArtifacts,
  loadProviderConfig,
};

if (require.main === module) {
  main();
}
