const fs = require('fs');
const os = require('os');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');
const tokenBuild = require('../../scripts/stage7-token-build.js');

describe('stage7 token build', () => {
  test('generates style-dictionary, less, RN and Android token artifacts', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-tokens-'));

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
        }),
        space: expect.any(Object),
        radius: expect.any(Object),
        motion: expect.any(Object),
        typography: expect.any(Object),
      }),
    );

    expect(tokens.color.bg.canvas.light).toMatch(/^#/);
    expect(tokens.color.bg.canvas.dark).toMatch(/^#/);
    expect(tokens.typography.title.hero.size).toBeGreaterThan(20);
  });

  test('generated RN tokens expose typed theme objects and typography roles', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-rn-token-'));

    try {
      const { reactNativePath } = tokenBuild.generateTokenArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const source = fs.readFileSync(reactNativePath, 'utf8');

      expect(source).toContain('export const stage7LightTheme');
      expect(source).toContain('export const stage7DarkTheme');
      expect(source).toContain('title:');
      expect(source).toContain('reader:');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });

  test('generated Android artifacts expose XML resources and Compose tokens', () => {
    const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-android-token-'));

    try {
      const outputs = tokenBuild.generateTokenArtifacts({
        repoRoot,
        outputRoot: tempDir,
      });
      const xml = fs.readFileSync(outputs.androidXmlPath, 'utf8');
      const compose = fs.readFileSync(outputs.androidComposePath, 'utf8');

      expect(xml).toContain('<resources>');
      expect(xml).toContain('stage7_color_bg_canvas_light');
      expect(xml).toContain('stage7_space_100');
      expect(compose).toContain('object Stage7Tokens');
      expect(compose).toContain('val LightColors');
      expect(compose).toContain('val DarkColors');
    } finally {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  });
});
