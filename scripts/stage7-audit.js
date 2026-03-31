#!/usr/bin/env node

const fs = require('fs');
const os = require('os');
const path = require('path');

const DEFAULT_OUTPUT_DIR = path.join(
  'docs',
  'refactor',
  'phase-15',
);

const toPosix = (value) => value.split(path.sep).join('/');

const ensureDir = (dirPath) => {
  fs.mkdirSync(dirPath, { recursive: true });
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

const readText = (repoRoot, relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

const writeText = (absolutePath, content) => {
  ensureDir(path.dirname(absolutePath));
  fs.writeFileSync(absolutePath, content, 'utf8');
};

const writeJson = (absolutePath, data) => {
  writeText(absolutePath, `${JSON.stringify(data, null, 2)}\n`);
};

const relativeRepoPath = (repoRoot, absolutePath) =>
  toPosix(path.relative(repoRoot, absolutePath));

const unique = (items) => [...new Set(items.filter(Boolean))];

const slugify = (value) =>
  value
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .replace(/[^A-Za-z0-9]+/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-|-$/g, '')
    .toLowerCase();

const componentNameFromFile = (absolutePath) =>
  path.basename(absolutePath).replace(/\.(ts|tsx|kt)$/, '');

const detectAssetSources = (source) => {
  const tags = [];

  if (/react-native-vector-icons\/([A-Za-z0-9]+)/.test(source)) {
    tags.push('react-native-vector-icons');
  }
  if (/\.svg['"]/.test(source) || /assets\/image\/.*\.svg/.test(source)) {
    tags.push('local-svg');
  }
  if (/NovelImageView|ProgressiveImageView|Image\(/.test(source)) {
    tags.push('image');
  }
  if (/ThemeSwitcher|theme/i.test(source)) {
    tags.push('theme-aware');
  }

  return unique(tags);
};

const inferComponentCategory = (relativePath) => {
  const baseName = path.basename(relativePath, path.extname(relativePath));

  if (/TopBar|Header|Nav|TabBar|BackButton/i.test(baseName)) {
    return 'navigation';
  }
  if (/Modal|Dialog/i.test(baseName)) {
    return 'dialog';
  }
  if (/Sheet|BottomSheet/i.test(baseName)) {
    return 'sheet';
  }
  if (/Loading|Skeleton|Spinner/i.test(baseName)) {
    return 'loading';
  }
  if (/Empty|Placeholder/i.test(baseName)) {
    return 'empty';
  }
  if (/List|Grid|Waterfall/i.test(baseName)) {
    return 'list';
  }
  if (/Card|Item|Tile|Row/i.test(baseName)) {
    return 'item';
  }
  if (/Input|Form|Search|Picker|Switch/i.test(baseName)) {
    return 'form';
  }
  if (/Button|Toolbar|Action|Filter|Purchase/i.test(baseName)) {
    return 'action';
  }
  if (/Image|Avatar|Icon/i.test(baseName)) {
    return 'media';
  }

  return 'layout';
};

const sampleNeighborComponents = (repoRoot, absolutePath) => {
  const pageDir = path.dirname(absolutePath);
  const candidateDirs = [
    path.join(pageDir, 'components'),
    path.join(pageDir, 'component'),
    path.join(pageDir, 'components', 'items'),
    path.join(path.dirname(pageDir), 'components'),
    path.join(pageDir, 'skeleton'),
  ];
  const componentNames = [];

  candidateDirs.forEach((dirPath) => {
    if (!fs.existsSync(dirPath)) {
      return;
    }
    walkFiles(
      dirPath,
      (filePath) => /\.(ts|tsx|kt)$/.test(filePath),
      componentNames,
    );
  });

  return unique(
    componentNames
      .map((filePath) => componentNameFromFile(filePath))
      .sort(),
  ).slice(0, 8);
};

const createSurface = ({
  surfaceId,
  platform,
  entryType,
  hostType,
  parentSurface = '',
  keyStates = ['default'],
  keyComponents = [],
  assetSources = [],
  sourcePaths = [],
}) => ({
  surface_id: surfaceId,
  platform,
  entry_type: entryType,
  host_type: hostType,
  parent_surface: parentSurface,
  key_states: keyStates,
  key_components: keyComponents,
  asset_sources: assetSources,
  a11y_status: 'pending-audit',
  rtl_status: 'pending-audit',
  dark_mode_status: 'pending-audit',
  figma_frame_id: '',
  visual_regression_case_id: `${surfaceId}-vr`,
  implementation_status: 'active',
  source_paths: sourcePaths,
});

const collectRnHostSurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) => filePath.endsWith('Component.tsx'),
  );

  return files
    .map((filePath) => {
      const source = fs.readFileSync(filePath, 'utf8');
      const registrationMatch = source.match(
        /AppRegistry\.registerComponent\('([^']+)'/,
      );
      if (!registrationMatch) {
        return null;
      }

      const registeredName = registrationMatch[1];
      const surfaceId = `rn-host-${slugify(registeredName)}`;

      return createSurface({
        surfaceId,
        platform: 'react-native',
        entryType: 'component-registration',
        hostType: 'rn-host',
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativeRepoPath(repoRoot, filePath)],
      });
    })
    .filter(Boolean)
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const collectRnRootSurfaces = (repoRoot) => {
  const appSource = readText(repoRoot, 'App.tsx');
  const profileSource = readText(repoRoot, 'src/page/ProfilePage/ProfilePage.tsx');

  return [
    createSurface({
      surfaceId: 'rn-root-profile-page',
      platform: 'react-native',
      entryType: 'app-root',
      hostType: 'rn-root',
      keyComponents: sampleNeighborComponents(
        repoRoot,
        path.join(repoRoot, 'src/page/ProfilePage/ProfilePage.tsx'),
      ),
      assetSources: unique([
        ...detectAssetSources(appSource),
        ...detectAssetSources(profileSource),
      ]),
      sourcePaths: ['App.tsx', 'src/page/ProfilePage/ProfilePage.tsx'],
    }),
  ];
};

const nestedPageSurfaceId = (relativePath) => {
  const match = relativePath.match(
    /^src\/page\/([^/]+)\/pages\/([^/]+)\/([^/]+)\.tsx$/,
  );

  if (!match) {
    return `rn-nested-${slugify(relativePath.replace(/^src\/page\//, '').replace(/\.tsx$/, ''))}`;
  }

  const [, rootPage, folderName] = match;
  const normalizedRoot = rootPage.replace(/Page$/, '');
  return `rn-nested-${slugify(`${normalizedRoot}-${folderName}-page`)}`;
};

const collectRnNestedSurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) =>
      /\.tsx$/.test(filePath) &&
      filePath.includes(`${path.sep}pages${path.sep}`) &&
      filePath.endsWith('Page.tsx'),
  );

  return files
    .map((filePath) => {
      const relativePath = relativeRepoPath(repoRoot, filePath);
      const source = fs.readFileSync(filePath, 'utf8');
      const parentSurface = relativePath.startsWith('src/page/BookshelfPage/pages/')
        ? 'rn-host-bookshelf-page-component'
        : '';

      return createSurface({
        surfaceId: nestedPageSurfaceId(relativePath),
        platform: 'react-native',
        entryType: 'nested-page',
        hostType: 'rn-nested',
        parentSurface,
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativePath],
      });
    })
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const collectAndroidNativeSurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'android', 'app', 'src', 'main', 'java', 'com', 'novel', 'page'),
    (filePath) =>
      filePath.endsWith('Page.kt') &&
      !filePath.includes(`${path.sep}component${path.sep}`) &&
      !filePath.includes(`${path.sep}components${path.sep}`) &&
      !filePath.includes(`${path.sep}skeleton${path.sep}`),
  );

  return files
    .map((filePath) => {
      const source = fs.readFileSync(filePath, 'utf8');
      const baseName = componentNameFromFile(filePath).replace(/Page$/, '');

      return createSurface({
        surfaceId: `android-native-${slugify(`${baseName} Page`)}`,
        platform: 'android',
        entryType: 'native-screen',
        hostType: 'android-native',
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativeRepoPath(repoRoot, filePath)],
      });
    })
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const collectAndroidShellSurfaces = (repoRoot) => {
  const shellFiles = [
    'android/app/src/main/java/com/novel/MainActivity.kt',
    'android/app/src/main/java/com/novel/ComposeMainActivity.kt',
    'android/app/src/main/java/com/novel/page/MainPageHostComponents.kt',
    'android/app/src/main/java/com/novel/rn/ReactNativePage.kt',
    'android/feature-rn-host/src/main/java/com/novel/rn/ReactNativePageContent.kt',
  ];

  return shellFiles
    .filter((relativePath) => fs.existsSync(path.join(repoRoot, relativePath)))
    .map((relativePath) => {
      const source = readText(repoRoot, relativePath);
      const fileLabel = componentNameFromFile(relativePath);

      return createSurface({
        surfaceId: `android-shell-${slugify(fileLabel)}`,
        platform: 'android',
        entryType: 'shell',
        hostType: 'android-shell',
        keyComponents: [],
        assetSources: detectAssetSources(source),
        sourcePaths: [relativePath],
      });
    });
};

const collectAndroidOverlaySurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'android', 'app', 'src', 'main', 'java', 'com', 'novel', 'page'),
    (filePath) =>
      /(Skeleton|BottomSheet|Panel|Dialog)\.kt$/.test(filePath),
  );

  return files
    .map((filePath) => {
      const source = fs.readFileSync(filePath, 'utf8');
      const baseName = componentNameFromFile(filePath);

      return createSurface({
        surfaceId: `android-overlay-${slugify(baseName)}`,
        platform: 'android',
        entryType: 'overlay',
        hostType: 'android-overlay',
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativeRepoPath(repoRoot, filePath)],
      });
    })
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const buildSurfaceInventory = (repoRoot) => {
  const surfaces = [
    ...collectRnRootSurfaces(repoRoot),
    ...collectRnHostSurfaces(repoRoot),
    ...collectRnNestedSurfaces(repoRoot),
    ...collectAndroidNativeSurfaces(repoRoot),
    ...collectAndroidShellSurfaces(repoRoot),
    ...collectAndroidOverlaySurfaces(repoRoot),
  ];

  return surfaces.sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const buildComponentCatalog = (repoRoot) => {
  const componentFiles = walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) =>
      /\.(ts|tsx)$/.test(filePath) &&
      filePath.includes(`${path.sep}components${path.sep}`),
  );

  const entries = componentFiles
    .map((filePath) => {
      const relativePath = relativeRepoPath(repoRoot, filePath);
      const source = fs.readFileSync(filePath, 'utf8');
      return {
        path: relativePath,
        name: componentNameFromFile(filePath),
        platform: 'react-native',
        category: inferComponentCategory(relativePath),
        asset_sources: detectAssetSources(source),
      };
    })
    .sort((left, right) => left.path.localeCompare(right.path));

  const summary = entries.reduce(
    (acc, entry) => {
      acc.rn_component_count += 1;
      acc.category_counts[entry.category] = (acc.category_counts[entry.category] || 0) + 1;
      return acc;
    },
    { rn_component_count: 0, category_counts: {} },
  );

  return { summary, entries };
};

const buildAssetInventory = (repoRoot) => {
  const svgAssets = walkFiles(
    path.join(repoRoot, 'assets', 'image'),
    (filePath) => filePath.endsWith('.svg'),
  );
  const fontAssets = walkFiles(
    path.join(repoRoot, 'assets', 'fonts'),
    () => true,
  );
  const rnFiles = walkFiles(
    path.join(repoRoot, 'src'),
    (filePath) => /\.(ts|tsx|js|jsx)$/.test(filePath),
  );
  const families = new Set();

  rnFiles.forEach((filePath) => {
    const source = fs.readFileSync(filePath, 'utf8');
    const pattern = /react-native-vector-icons\/([A-Za-z0-9]+)/g;
    let match;
    while ((match = pattern.exec(source)) !== null) {
      families.add(match[1]);
    }
  });

  return {
    summary: {
      local_svg_count: svgAssets.length,
      font_asset_count: fontAssets.length,
    },
    local_svg_assets: svgAssets.map((filePath) => relativeRepoPath(repoRoot, filePath)).sort(),
    font_assets: fontAssets.map((filePath) => relativeRepoPath(repoRoot, filePath)).sort(),
    react_native_vector_icon_families: [...families].sort(),
  };
};

const parseRegisteredComponentNames = (repoRoot) => {
  const registryPath = path.join(repoRoot, 'src', 'utils', 'runtime', 'componentRegistry.ts');
  const source = fs.readFileSync(registryPath, 'utf8');
  return [...source.matchAll(/'([A-Za-z0-9]+Component)'/g)].map((match) => match[1]).sort();
};

const parseDiscoveredComponentNames = (repoRoot) =>
  walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) => filePath.endsWith('Component.tsx'),
  )
    .map((filePath) => fs.readFileSync(filePath, 'utf8').match(/AppRegistry\.registerComponent\('([^']+)'/)?.[1])
    .filter(Boolean)
    .sort();

const buildFigmaFrameMap = (surfaces) =>
  surfaces.map((surface) => {
    let figmaPage = '00-现状审计/Android Shell & Overlays';

    if (surface.host_type === 'rn-root') {
      figmaPage = '00-现状审计/RN Root';
    } else if (surface.host_type === 'rn-host') {
      figmaPage = '00-现状审计/RN Host Pages';
    } else if (surface.host_type === 'rn-nested') {
      figmaPage = '00-现状审计/RN Nested Pages';
    } else if (surface.host_type === 'android-native') {
      figmaPage = '00-现状审计/Android Native Pages';
    }

    return {
      surface_id: surface.surface_id,
      figma_page: figmaPage,
      figma_frame_name: surface.surface_id,
      figma_frame_id: '',
      mapping_status: 'unmapped',
    };
  });

const parseRNSmokeTests = (repoRoot) =>
  walkFiles(
    path.join(repoRoot, '__tests__', 'smoke'),
    (filePath) => filePath.endsWith('.smoke.test.tsx'),
  )
    .map((filePath) => path.basename(filePath))
    .sort();

const parseSmokeCatalogMentions = (repoRoot) => {
  const source = readText(repoRoot, 'docs/refactor/phase-2/smoke-suite-catalog.md');
  return unique(
    [
      ...source.matchAll(/([A-Za-z0-9]+\.smoke\.test\.tsx)/g),
      ...source.matchAll(/([A-Za-z0-9]+SmokeTest\.kt)/g),
    ].map((match) => match[1]),
  ).sort();
};

const buildGovernanceDriftReport = (repoRoot, surfaces, figmaFrameMap) => {
  const registryNames = parseRegisteredComponentNames(repoRoot);
  const discoveredNames = parseDiscoveredComponentNames(repoRoot);
  const missingFromRegistry = discoveredNames.filter((name) => !registryNames.includes(name));
  const extraInRegistry = registryNames.filter((name) => !discoveredNames.includes(name));
  const rnSmokeTests = parseRNSmokeTests(repoRoot);
  const smokeCatalogMentions = parseSmokeCatalogMentions(repoRoot);
  const missingFromCatalog = rnSmokeTests.filter((name) => !smokeCatalogMentions.includes(name));

  return [
    '# Stage 7 Governance Drift Report',
    '',
    '## Summary',
    `- Surface count: ${surfaces.length}`,
    `- Registry drift: ${missingFromRegistry.length === 0 && extraInRegistry.length === 0 ? 'none' : 'present'}`,
    `- RN smoke tests: ${rnSmokeTests.length}`,
    `- Missing smoke catalog entries: ${missingFromCatalog.length}`,
    `- Unmapped figma frames: ${figmaFrameMap.filter((item) => !item.figma_frame_id).length}`,
    '',
    '## Registry drift',
    `- Missing from registry: ${missingFromRegistry.length ? missingFromRegistry.join(', ') : 'none'}`,
    `- Extra in registry: ${extraInRegistry.length ? extraInRegistry.join(', ') : 'none'}`,
    '',
    '## RN smoke tests',
    ...rnSmokeTests.map((name) => `- ${name}`),
    '',
    '## Smoke catalog drift',
    `- Catalog mentions: ${smokeCatalogMentions.length ? smokeCatalogMentions.join(', ') : 'none'}`,
    `- Missing from catalog: ${missingFromCatalog.length ? missingFromCatalog.join(', ') : 'none'}`,
    '',
    '## Figma mapping status',
    `- Unmapped surfaces: ${figmaFrameMap.filter((item) => !item.figma_frame_id).length}`,
    '',
  ].join('\n');
};

const resolveOutputDir = (repoRoot, outputDir) =>
  path.isAbsolute(outputDir)
    ? outputDir
    : path.join(repoRoot, outputDir);

const generateAuditArtifacts = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputDir = DEFAULT_OUTPUT_DIR,
} = {}) => {
  const resolvedOutputDir = resolveOutputDir(repoRoot, outputDir);
  ensureDir(resolvedOutputDir);

  const surfaceInventory = buildSurfaceInventory(repoRoot);
  const componentCatalog = buildComponentCatalog(repoRoot);
  const assetInventory = buildAssetInventory(repoRoot);
  const figmaFrameMap = buildFigmaFrameMap(surfaceInventory);
  const governanceDriftReport = buildGovernanceDriftReport(
    repoRoot,
    surfaceInventory,
    figmaFrameMap,
  );

  const outputs = {
    surfaceInventoryPath: path.join(resolvedOutputDir, 'surface-inventory.json'),
    componentCatalogPath: path.join(resolvedOutputDir, 'component-catalog.json'),
    assetInventoryPath: path.join(resolvedOutputDir, 'asset-inventory.json'),
    figmaFrameMapPath: path.join(resolvedOutputDir, 'figma-frame-map.json'),
    governanceDriftReportPath: path.join(resolvedOutputDir, 'governance-drift-report.md'),
  };

  writeJson(outputs.surfaceInventoryPath, surfaceInventory);
  writeJson(outputs.componentCatalogPath, componentCatalog);
  writeJson(outputs.assetInventoryPath, assetInventory);
  writeJson(outputs.figmaFrameMapPath, figmaFrameMap);
  writeText(outputs.governanceDriftReportPath, `${governanceDriftReport}\n`);

  return outputs;
};

const checkAuditArtifacts = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputDir = DEFAULT_OUTPUT_DIR,
} = {}) => {
  const resolvedOutputDir = resolveOutputDir(repoRoot, outputDir);
  const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-check-'));

  try {
    const freshOutputs = generateAuditArtifacts({ repoRoot, outputDir: tempDir });
    const expectedFiles = {
      surfaceInventoryPath: path.join(resolvedOutputDir, 'surface-inventory.json'),
      componentCatalogPath: path.join(resolvedOutputDir, 'component-catalog.json'),
      assetInventoryPath: path.join(resolvedOutputDir, 'asset-inventory.json'),
      figmaFrameMapPath: path.join(resolvedOutputDir, 'figma-frame-map.json'),
      governanceDriftReportPath: path.join(resolvedOutputDir, 'governance-drift-report.md'),
    };

    const missing = Object.values(expectedFiles).filter((filePath) => !fs.existsSync(filePath));
    if (missing.length > 0) {
      return {
        ok: false,
        message: `Missing audit artifact(s): ${missing.join(', ')}`,
      };
    }

    const mismatched = Object.keys(expectedFiles).filter((key) => {
      const currentContent = fs.readFileSync(expectedFiles[key], 'utf8');
      const freshContent = fs.readFileSync(freshOutputs[key], 'utf8');
      return currentContent !== freshContent;
    });

    if (mismatched.length > 0) {
      return {
        ok: false,
        message: `Audit artifacts are stale: ${mismatched.join(', ')}`,
      };
    }

    return {
      ok: true,
      message: 'Stage 7 audit artifacts are up to date.',
    };
  } finally {
    fs.rmSync(tempDir, { recursive: true, force: true });
  }
};

const main = () => {
  const command = process.argv[2] || 'generate';
  const repoRoot = path.resolve(__dirname, '..');

  if (command === 'generate') {
    const outputs = generateAuditArtifacts({ repoRoot });
    console.log(`Stage 7 audit artifacts written to ${path.dirname(outputs.surfaceInventoryPath)}`);
    return;
  }

  if (command === 'check') {
    const result = checkAuditArtifacts({ repoRoot });
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
  buildAssetInventory,
  buildComponentCatalog,
  buildGovernanceDriftReport,
  buildSurfaceInventory,
  checkAuditArtifacts,
  generateAuditArtifacts,
};

if (require.main === module) {
  main();
}
