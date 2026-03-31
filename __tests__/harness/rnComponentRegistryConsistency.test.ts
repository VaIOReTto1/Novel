import fs from 'fs';
import path from 'path';

const SRC_PAGE_ROOT = path.resolve(__dirname, '../../src/page');
const COMPONENT_REGISTRY_FILE = path.resolve(
  __dirname,
  '../../src/utils/runtime/componentRegistry.ts',
);

const collectComponentFiles = (dir: string): string[] => {
  const entries = fs.readdirSync(dir, { withFileTypes: true });

  return entries.flatMap((entry) => {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      return collectComponentFiles(fullPath);
    }
    return entry.name.endsWith('Component.tsx') ? [fullPath] : [];
  });
};

const extractRegisteredName = (filePath: string): string | null => {
  const content = fs.readFileSync(filePath, 'utf8');
  const match = content.match(/AppRegistry\.registerComponent\('([^']+)'/);
  return match ? match[1] : null;
};

const extractRegistryNames = (): string[] => {
  const content = fs.readFileSync(COMPONENT_REGISTRY_FILE, 'utf8');
  const names = [...content.matchAll(/'([A-Za-z0-9]+Component)'/g)].map(
    (match) => match[1],
  );
  return names;
};

describe('RN component registry consistency', () => {
  const componentFiles = collectComponentFiles(SRC_PAGE_ROOT);
  const discoveredNames = componentFiles
    .map(extractRegisteredName)
    .filter((value): value is string => Boolean(value))
    .sort();
  const registeredNames = extractRegistryNames().sort();

  test('every page component registration is listed in runtime component registry', () => {
    expect(registeredNames).toEqual(discoveredNames);
  });

  test('runtime component registry has unique component names', () => {
    expect(new Set(extractRegistryNames()).size).toBe(extractRegistryNames().length);
  });
});
