const fs = require('fs');
const os = require('os');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const stage7Audit = require('../../scripts/novel-design-audit.js');

const readJson = (filePath) => JSON.parse(fs.readFileSync(filePath, 'utf8'));

describe('stage7 audit scripts', () => {
  test('generates Phase 15 audit artifacts with expected files', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-audit-'));

    try {
      const outputs = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });

      expect(Object.keys(outputs).sort()).toEqual([
        'assetInventoryPath',
        'componentCatalogPath',
        'componentVisualSpecsPath',
        'figmaFrameMapPath',
        'governanceDriftReportPath',
        'surfaceInventoryPath',
        'surfaceVisualSpecsPath',
        'visualPlanningSummaryPath',
      ]);

      Object.values(outputs).forEach((artifactPath) => {
        expect(fs.existsSync(artifactPath)).toBe(true);
      });
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('surface inventory covers RN root, RN nested pages, Android native screens and shells', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-surface-'));

    try {
      const { surfaceInventoryPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const surfaces = readJson(surfaceInventoryPath);
      const surfaceIds = new Set(surfaces.map((surface) => surface.surface_id));

      expect(surfaceIds.has('rn-root-profile-page')).toBe(true);
      expect(surfaceIds.has('rn-host-bookshelf-page-component')).toBe(true);
      expect(surfaceIds.has('rn-nested-bookshelf-history-page')).toBe(true);
      expect(surfaceIds.has('android-native-reader-page')).toBe(true);
      expect(surfaceIds.has('android-shell-react-native-page')).toBe(true);
      expect(surfaceIds.has('android-overlay-search-filter-bottom-sheet')).toBe(true);

      surfaces.forEach((surface) => {
        expect(surface).toEqual(
          expect.objectContaining({
            surface_id: expect.any(String),
            platform: expect.any(String),
            entry_type: expect.any(String),
            host_type: expect.any(String),
            figma_frame_id: expect.any(String),
            visual_regression_case_id: expect.any(String),
            implementation_status: expect.any(String),
          }),
        );
      });
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('component catalog groups RN and Android components by semantic category', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-components-'));

    try {
      const { componentCatalogPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const catalog = readJson(componentCatalogPath);
      const byPath = new Map(catalog.entries.map((entry) => [entry.path, entry]));

      expect(catalog.summary.rn_component_count).toBeGreaterThan(100);
      expect(catalog.summary.android_component_count).toBeGreaterThan(10);
      expect(byPath.get('src/page/ProfilePage/components/TopBar.tsx').category).toBe('navigation');
      expect(byPath.get('src/page/ScrollBox/BecomeWriterPage/components/WelcomeModal.tsx').category).toBe('dialog');
      expect(byPath.get('src/page/BookshelfPage/pages/Community/components/LoadingIndicator.tsx').category).toBe('loading');
      expect(byPath.get('android/core-ui/src/main/java/com/novel/page/component/NovelButton.kt').platform).toBe('android');
      expect(byPath.get('android/core-ui/src/main/java/com/novel/page/component/NovelButton.kt').category).toBe('action');
      expect(byPath.get('android/app/src/main/java/com/novel/page/search/component/SearchFilterBottomSheet.kt').category).toBe('sheet');
      expect(byPath.get('android/core-ui/src/main/java/com/novel/ui/showcase/NovelDesignShowcaseScreen.kt').category).toBe('showcase');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('surface visual specs describe current and target look for every surface', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-surface-specs-'));

    try {
      const { surfaceInventoryPath, surfaceVisualSpecsPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const surfaces = readJson(surfaceInventoryPath);
      const specs = readJson(surfaceVisualSpecsPath);
      const specById = new Map(specs.map((entry) => [entry.surface_id, entry]));

      expect(specs).toHaveLength(surfaces.length);
      expect(specById.get('rn-root-profile-page')).toEqual(
        expect.objectContaining({
          surface_id: 'rn-root-profile-page',
          cluster: expect.any(String),
          current_visual_summary: expect.objectContaining({
            layout: expect.any(String),
            chrome: expect.any(String),
            content_pattern: expect.any(String),
          }),
          target_visual_plan: expect.objectContaining({
            direction: 'literary-editorial',
            layout_strategy: expect.any(String),
            style_keywords: expect.any(Array),
          }),
        }),
      );
      expect(specById.get('android-native-reader-page').target_visual_plan.component_recipe).toContain('immersive-reader');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('component visual specs describe current and target look for every component', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-component-specs-'));

    try {
      const { componentCatalogPath, componentVisualSpecsPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const catalog = readJson(componentCatalogPath);
      const specs = readJson(componentVisualSpecsPath);
      const specByPath = new Map(specs.map((entry) => [entry.path, entry]));

      expect(specs).toHaveLength(catalog.entries.length);
      expect(specByPath.get('src/page/ProfilePage/components/TopBar.tsx')).toEqual(
        expect.objectContaining({
          category: 'navigation',
          current_visual_summary: expect.objectContaining({
            structure: expect.any(String),
            affordance: expect.any(String),
          }),
          target_visual_plan: expect.objectContaining({
            component_recipe: expect.any(String),
            style_keywords: expect.any(Array),
          }),
        }),
      );
      expect(specByPath.get('src/page/ScrollBox/BecomeWriterPage/components/WelcomeModal.tsx').target_visual_plan.component_recipe).toContain('dialog');
      expect(specByPath.get('android/core-ui/src/main/java/com/novel/page/component/NovelTextField.kt')).toEqual(
        expect.objectContaining({
          category: 'form',
          platform: 'android',
        }),
      );
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('asset inventory captures svg fonts and vector icon families', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-assets-'));

    try {
      const { assetInventoryPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const inventory = readJson(assetInventoryPath);

      expect(inventory.summary.local_svg_count).toBeGreaterThanOrEqual(20);
      expect(inventory.summary.font_asset_count).toBeGreaterThanOrEqual(7);
      expect(inventory.react_native_vector_icon_families).toEqual(
        expect.arrayContaining(['MaterialIcons', 'Feather']),
      );
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('governance drift report captures smoke coverage and registry status', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-drift-'));

    try {
      const { governanceDriftReportPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const report = fs.readFileSync(governanceDriftReportPath, 'utf8');

      expect(report).toContain('RN smoke tests');
      expect(report).toContain('WritePage.smoke.test.tsx');
      expect(report).toContain('SettingsPage.smoke.test.tsx');
      expect(report).toContain('Registry drift');
      expect(report).toContain('none');
      expect(report).toContain('Missing from catalog: none');
      expect(report).toContain('Surface visual specs coverage');
      expect(report).toContain('Component visual specs coverage');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('visual planning summary reports every surface and component as planned entries', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-visual-summary-'));

    try {
      const { visualPlanningSummaryPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const summary = fs.readFileSync(visualPlanningSummaryPath, 'utf8');

      expect(summary).toContain('Surface visual specs');
      expect(summary).toContain('Component visual specs');
      expect(summary).toContain('Current look recorded');
      expect(summary).toContain('Target look planned');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
