const fs = require('fs');
const os = require('os');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const assetScripts = require('../../scripts/novel-design-assets.js');

const readJson = (filePath) => JSON.parse(fs.readFileSync(filePath, 'utf8'));

describe('novelDesign asset scripts', () => {
  test('generates asset manifests and governance report', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-assets-'));

    try {
      const outputs = assetScripts.generateAssetArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });

      expect(Object.keys(outputs).sort()).toEqual([
        'copyrightLedgerPath',
        'iconManifestPath',
        'illustrationManifestPath',
        'mediaManifestPath',
        'reactNativeIconRegistryPath',
        'reportPath',
      ]);

      Object.values(outputs).forEach((artifactPath) => {
        expect(fs.existsSync(artifactPath)).toBe(true);
      });
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('icon manifest captures local svg inventory and migration targets', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-icons-'));

    try {
      const { iconManifestPath } = assetScripts.generateAssetArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const manifest = readJson(iconManifestPath);

      expect(manifest.summary.local_svg_count).toBeGreaterThanOrEqual(20);
      expect(manifest.summary.vector_icon_families).toEqual(
        expect.arrayContaining(['MaterialIcons', 'Feather']),
      );
      expect(
        manifest.entries.some((entry) => entry.semantic_name === 'legacy.settings'),
      ).toBe(true);
      expect(
        manifest.entries.every((entry) => entry.migration_target === 'iconify'),
      ).toBe(true);
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('media manifest encodes picsum, pexels and undraw provider rules', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-media-'));

    try {
      const outputs = assetScripts.generateAssetArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const mediaManifest = readJson(outputs.mediaManifestPath);
      const illustrationManifest = readJson(outputs.illustrationManifestPath);

      expect(mediaManifest.providers.placeholder.name).toBe('picsum');
      expect(mediaManifest.providers.placeholder.fixed_seed_required).toBe(true);
      expect(mediaManifest.providers.photo.name).toBe('pexels');
      expect(mediaManifest.providers.photo.runtime_search_allowed).toBe(false);
      expect(illustrationManifest.provider.name).toBe('undraw');
      expect(illustrationManifest.provider.theme_sync).toBe('brand-token-driven');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('copyright ledger starts with explicit schema and empty entries', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-ledger-'));

    try {
      const { copyrightLedgerPath } = assetScripts.generateAssetArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const ledger = readJson(copyrightLedgerPath);

      expect(ledger.schema_version).toBe('1.0.0');
      expect(Array.isArray(ledger.entries)).toBe(true);
      expect(ledger.entries).toHaveLength(0);
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('check fails when asset artifacts are stale', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-assets-check-'));

    try {
      const outputs = assetScripts.generateAssetArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      fs.writeFileSync(outputs.reportPath, '# stale asset report\n', 'utf8');

      const result = assetScripts.checkAssetArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });

      expect(result.ok).toBe(false);
      expect(result.message).toContain('stale');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('generated RN icon registry exposes legacy semantic names and sources', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-icon-registry-'));

    try {
      const { reactNativeIconRegistryPath } = assetScripts.generateAssetArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const source = fs.readFileSync(reactNativeIconRegistryPath, 'utf8');

      expect(source).toContain('export const novelDesignIconRegistry');
      expect(source).toContain('"legacy.settings"');
      expect(source).toContain('../../../../assets/image/settings.svg');
      expect(source).toContain('"legacy.wallet"');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
