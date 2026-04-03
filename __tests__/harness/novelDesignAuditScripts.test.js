const fs = require('fs');
const os = require('os');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const novelDesignAudit = require('../../scripts/novel-design-audit.js');

const readJson = (filePath) => JSON.parse(fs.readFileSync(filePath, 'utf8'));

describe('novelDesign audit scripts', () => {
  test('generates Phase 15 audit artifacts with expected files', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-audit-'));

    try {
      const outputs = novelDesignAudit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });

      expect(Object.keys(outputs).sort()).toEqual([
        'assetInventoryPath',
        'componentCatalogPath',
        'componentVisualSpecsPath',
        'figmaFrameMapPath',
        'figmaSyncQueuePath',
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
      const { surfaceInventoryPath } = novelDesignAudit.generateAuditArtifacts({
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
      const { componentCatalogPath } = novelDesignAudit.generateAuditArtifacts({
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
      const { surfaceInventoryPath, surfaceVisualSpecsPath } = novelDesignAudit.generateAuditArtifacts({
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
            viewport: expect.objectContaining({
              device_frame: expect.any(String),
              scroll_direction: expect.any(String),
            }),
            frame_anatomy: expect.any(Array),
            primary_blocks: expect.any(Array),
            visual_density: expect.objectContaining({
              density: expect.any(String),
            }),
            state_panels: expect.any(Array),
            asset_profile: expect.objectContaining({
              icon_sources: expect.any(Array),
            }),
            interaction_chrome: expect.any(Array),
          }),
          target_visual_plan: expect.objectContaining({
            direction: 'literary-editorial',
            layout_strategy: expect.any(String),
            layout_blueprint: expect.objectContaining({
              primary_flow: expect.any(String),
            }),
            section_recipes: expect.any(Array),
            spacing_rhythm: expect.objectContaining({
              page_gutter: expect.any(String),
            }),
            shape_language: expect.objectContaining({
              cards: expect.any(String),
            }),
            typography_roles: expect.objectContaining({
              title: expect.any(String),
            }),
            motion_notes: expect.objectContaining({
              page_enter: expect.any(String),
            }),
            dark_a11y_rtl: expect.objectContaining({
              dark_mode: expect.any(String),
            }),
            style_keywords: expect.any(Array),
          }),
          implementation_progress: expect.objectContaining({
            shell_reskinned: true,
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
      const { componentCatalogPath, componentVisualSpecsPath } = novelDesignAudit.generateAuditArtifacts({
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
            anatomy: expect.any(Array),
            size_rules: expect.objectContaining({
              min_touch_target: expect.any(String),
            }),
            text_hierarchy: expect.objectContaining({
              title: expect.any(String),
            }),
            container_style: expect.objectContaining({
              background: expect.any(String),
            }),
            interaction_states: expect.any(Array),
          }),
          target_visual_plan: expect.objectContaining({
            component_recipe: expect.any(String),
            slot_structure: expect.any(Array),
            recipe_binding: expect.any(String),
            state_matrix: expect.any(Array),
            token_binding: expect.any(Array),
            platform_adaptation: expect.any(Array),
            style_keywords: expect.any(Array),
          }),
          implementation_progress: expect.objectContaining({
            novel_design_ready: true,
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
      const { assetInventoryPath } = novelDesignAudit.generateAuditArtifacts({
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
      const { governanceDriftReportPath } = novelDesignAudit.generateAuditArtifacts({
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
      expect(report).toContain('Detailed surface fields');
      expect(report).toContain('Detailed component fields');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('figma frame mapping emits sync metadata and queue entries for deferred Figma writeback', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-figma-map-'));

    try {
      const { figmaFrameMapPath, figmaSyncQueuePath } = novelDesignAudit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const frameMap = readJson(figmaFrameMapPath);
      const syncQueue = readJson(figmaSyncQueuePath);

      expect(frameMap[0]).toEqual(
        expect.objectContaining({
          frame_type: expect.any(String),
          source_kind: expect.any(String),
          sync_status: expect.any(String),
          target_frame_name_light: expect.any(String),
          target_frame_name_dark: expect.any(String),
        }),
      );
      expect(syncQueue[0]).toEqual(
        expect.objectContaining({
          source_id: expect.any(String),
          source_kind: expect.any(String),
          sync_status: expect.any(String),
        }),
      );
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('figma frame mapping preserves previously backfilled frame ids', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-figma-map-preserve-'));

    try {
      const phase15Dir = path.join(tempDir, 'docs', 'refactor', 'phase-15');
      fs.mkdirSync(phase15Dir, { recursive: true });
      fs.writeFileSync(
        path.join(phase15Dir, 'figma-frame-map.json'),
        JSON.stringify(
          [
            {
              surface_id: 'rn-root-profile-page',
              figma_page: '00-现状审计/RN Root',
              figma_frame_name: 'rn-root-profile-page',
              figma_frame_id: '123:456',
              mapping_status: 'mapped',
              source_kind: 'surface',
              frame_type: 'root-audit-frame',
              sync_status: 'synced',
              audit_frame_name: 'rn-root-profile-page',
              target_frame_name_light: 'rn-root-profile-page/light',
              target_frame_name_dark: 'rn-root-profile-page/dark',
              annotation_frame_name: 'rn-root-profile-page/annotation',
            },
          ],
          null,
          2,
        ),
        'utf8',
      );

      const { figmaFrameMapPath } = novelDesignAudit.generateAuditArtifacts({
        repoRoot,
        outputDir: phase15Dir,
      });
      const frameMap = readJson(figmaFrameMapPath);
      const profile = frameMap.find((entry) => entry.surface_id === 'rn-root-profile-page');

      expect(profile.figma_frame_id).toBe('123:456');
      expect(profile.mapping_status).toBe('mapped');
      expect(profile.sync_status).toBe('synced');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('check audit artifacts accepts repo state with backfilled figma frame ids', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-check-preserve-'));

    try {
      const phase15Dir = path.join(tempDir, 'docs', 'refactor', 'phase-15');
      fs.mkdirSync(phase15Dir, { recursive: true });

      novelDesignAudit.generateAuditArtifacts({
        repoRoot,
        outputDir: phase15Dir,
      });

      const figmaFrameMapPath = path.join(phase15Dir, 'figma-frame-map.json');
      const frameMap = readJson(figmaFrameMapPath);
      const updatedFrameMap = frameMap.map((entry) =>
        entry.surface_id === 'rn-root-profile-page'
          ? {
              ...entry,
              figma_frame_id: '123:456',
              mapping_status: 'mapped',
              sync_status: 'synced',
            }
          : entry,
      );
      fs.writeFileSync(figmaFrameMapPath, `${JSON.stringify(updatedFrameMap, null, 2)}\n`, 'utf8');
      novelDesignAudit.generateAuditArtifacts({
        repoRoot,
        outputDir: phase15Dir,
      });

      const result = novelDesignAudit.checkAuditArtifacts({
        repoRoot,
        outputDir: phase15Dir,
      });

      expect(result).toEqual({
        ok: true,
        message: 'Novel design audit artifacts are up to date.',
      });
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('visual planning summary reports every surface and component as planned entries', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-visual-summary-'));

    try {
      const { visualPlanningSummaryPath } = novelDesignAudit.generateAuditArtifacts({
        repoRoot,
        outputDir: tempDir,
      });
      const summary = fs.readFileSync(visualPlanningSummaryPath, 'utf8');

      expect(summary).toContain('Surface visual specs');
      expect(summary).toContain('Component visual specs');
      expect(summary).toContain('Current look recorded');
      expect(summary).toContain('Target look planned');
      expect(summary).toContain('Shell reskinned');
      expect(summary).toContain('Detailed current fields');
      expect(summary).toContain('Detailed target fields');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
