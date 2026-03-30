import fs from 'fs';
import path from 'path';

const SRC_ROOT = path.resolve(__dirname, '../../src');
const ALLOWED_NATIVE_MODULE_FILES = new Set([
  path.normalize('utils/bridge/NavigationBridge.ts'),
  path.normalize('utils/bridge/SettingsBridge.ts'),
  path.normalize('utils/bridge/UserBridge.ts'),
]);
const ALLOWED_EVENT_EMITTER_FILES = new Set([
  path.normalize('utils/runtime/eventHub.ts'),
]);
const ALLOWED_BACK_HANDLER_FILES = new Set([
  path.normalize('utils/runtime/backNavigation.ts'),
]);

const SOURCE_FILE_PATTERN = /\.(ts|tsx|js|jsx)$/;

const collectSourceFiles = (dir: string): string[] => {
  const entries = fs.readdirSync(dir, { withFileTypes: true });

  return entries.flatMap((entry) => {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      return collectSourceFiles(fullPath);
    }
    return SOURCE_FILE_PATTERN.test(entry.name) ? [fullPath] : [];
  });
};

describe('RN raw primitive boundaries', () => {
  const sourceFiles = collectSourceFiles(SRC_ROOT);

  test('NativeModules only appears in bridge wrappers', () => {
    const offenders = sourceFiles
      .map((filePath) => {
        const relativePath = path.relative(SRC_ROOT, filePath);
        if (ALLOWED_NATIVE_MODULE_FILES.has(path.normalize(relativePath))) {
          return null;
        }

        const content = fs.readFileSync(filePath, 'utf8');
        return /\bNativeModules\b/.test(content) ? relativePath : null;
      })
      .filter(Boolean);

    expect(offenders).toEqual([]);
  });

  test('DeviceEventEmitter only appears in runtime event hub', () => {
    const offenders = sourceFiles
      .map((filePath) => {
        const relativePath = path.relative(SRC_ROOT, filePath);
        if (ALLOWED_EVENT_EMITTER_FILES.has(path.normalize(relativePath))) {
          return null;
        }

        const content = fs.readFileSync(filePath, 'utf8');
        return /\bDeviceEventEmitter\b/.test(content) ? relativePath : null;
      })
      .filter(Boolean);

    expect(offenders).toEqual([]);
  });

  test('BackHandler only appears in runtime back-navigation wrapper', () => {
    const offenders = sourceFiles
      .map((filePath) => {
        const relativePath = path.relative(SRC_ROOT, filePath);
        if (ALLOWED_BACK_HANDLER_FILES.has(path.normalize(relativePath))) {
          return null;
        }

        const content = fs.readFileSync(filePath, 'utf8');
        return /\bBackHandler\b/.test(content) ? relativePath : null;
      })
      .filter(Boolean);

    expect(offenders).toEqual([]);
  });
});
