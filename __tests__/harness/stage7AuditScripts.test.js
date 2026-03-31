const fs = require('fs');
const os = require('os');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const stage7Audit = require('../../scripts/stage7-audit.js');

const readJson = (filePath) => JSON.parse(fs.readFileSync(filePath, 'utf8'));

describe('stage7 audit scripts', () => {
  test('generates Phase 15 audit artifacts with expected files', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-audit-'));

    try {
      const outputs = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });

      expect(Object.keys(outputs).sort()).toEqual([
        'assetInventoryPath',
        'componentCatalogPath',
        'figmaFrameMapPath',
        'governanceDriftReportPath',
        'surfaceInventoryPath',
      ]);

      Object.values(outputs).forEach((artifactPath) => {
        expect(fs.existsSync(artifactPath)).toBe(true);
      });
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('surface inventory covers RN root, RN nested pages, Android native screens and shells', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-surface-'));

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

  test('component catalog groups RN components by semantic category', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-components-'));

    try {
      const { componentCatalogPath } = stage7Audit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const catalog = readJson(componentCatalogPath);
      const byPath = new Map(catalog.entries.map((entry) => [entry.path, entry]));

      expect(catalog.summary.rn_component_count).toBeGreaterThan(100);
      expect(byPath.get('src/page/ProfilePage/components/TopBar.tsx').category).toBe('navigation');
      expect(byPath.get('src/page/ScrollBox/BecomeWriterPage/components/WelcomeModal.tsx').category).toBe('dialog');
      expect(byPath.get('src/page/BookshelfPage/pages/Community/components/LoadingIndicator.tsx').category).toBe('loading');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('asset inventory captures svg fonts and vector icon families', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-assets-'));

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
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-drift-'));

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
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
