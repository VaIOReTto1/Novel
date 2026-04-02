const fs = require('fs');
const os = require('os');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const tokenBuild = require('../../scripts/novel-design-token-build.js');

describe('novelDesign token build', () => {
  test('generates style-dictionary, less, RN and Android token artifacts', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-tokens-'));

    try {
      const outputs = tokenBuild.generateTokenArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });

      expect(Object.keys(outputs).sort()).toEqual([
        'androidComposePath',
        'androidXmlPath',
        'lessPath',
        'reactNativePath',
        'styleDictionaryPath',
      ]);

      Object.values(outputs).forEach((artifactPath) => {
        expect(fs.existsSync(artifactPath)).toBe(true);
      });
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('exports semantic token groups for light and dark themes', () => {
    const tokens = tokenBuild.loadSourceTokens({
      repoRoot,
    });

    expect(tokens).toEqual(
      expect.objectContaining({
        color: expect.objectContaining({
          bg: expect.any(Object),
          text: expect.any(Object),
          brand: expect.any(Object),
          interaction: expect.any(Object),
          reader: expect.any(Object),
        }),
        space: expect.any(Object),
        radius: expect.any(Object),
        motion: expect.any(Object),
        typography: expect.any(Object),
      }),
    );

    expect(tokens.color.bg.canvas.light).toMatch(/^#/);
    expect(tokens.color.bg.canvas.dark).toMatch(/^#/);
    expect(tokens.color.interaction.selected.light).toMatch(/^#/);
    expect(tokens.space['700']).toBeGreaterThan(tokens.space['500']);
    expect(tokens.radius.xxl).toBeGreaterThan(tokens.radius.xl);
    expect(tokens.typography.title.hero.size).toBeGreaterThan(20);
    expect(tokens.typography.meta.sm.size).toBeLessThan(tokens.typography.body.sm.size);
    expect(tokens.typography.eyebrow.md.weight).toBeGreaterThanOrEqual(600);
  });

  test('generated RN tokens expose typed theme objects and typography roles', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-rn-token-'));

    try {
      const { reactNativePath } = tokenBuild.generateTokenArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const source = fs.readFileSync(reactNativePath, 'utf8');

      expect(source).toContain('export const novelDesignLightTheme');
      expect(source).toContain('export const novelDesignDarkTheme');
      expect(source).toContain('title:');
      expect(source).toContain('interaction:');
      expect(source).toContain('meta:');
      expect(source).toContain('reader:');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('generated Android artifacts expose XML resources and Compose tokens', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-android-token-'));

    try {
      const outputs = tokenBuild.generateTokenArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const xml = fs.readFileSync(outputs.androidXmlPath, 'utf8');
      const compose = fs.readFileSync(outputs.androidComposePath, 'utf8');

      expect(xml).toContain('<resources>');
      expect(xml).toContain('novel_design_color_bg_canvas_light');
      expect(xml).toContain('novel_design_color_interaction_selected_light');
      expect(xml).toContain('novel_design_space_100');
      expect(compose).toContain('object NovelDesignTokens');
      expect(compose).toContain('val LightColors');
      expect(compose).toContain('val DarkColors');
      expect(compose).toContain('val Radius');
      expect(compose).toContain('fun color(');
      expect(compose).toContain('fun radius(');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('check fails when token artifacts are stale', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-token-check-'));

    try {
      const outputs = tokenBuild.generateTokenArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      fs.writeFileSync(outputs.lessPath, '// stale token output\n', 'utf8');

      const result = tokenBuild.checkTokenArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });

      expect(result.ok).toBe(false);
      expect(result.message).toContain('stale');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
