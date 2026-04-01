const fs = require('fs');
const path = require('path');

const repoRoot = path.resolve(__dirname, '..', '..');

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

describe('novelDesign naming guard', () => {
  test('code layer and command layer no longer use novelDesign-prefixed names', () => {
    const files = [
      ...walkFiles(path.join(repoRoot, 'src'), (filePath) => /\.(ts|tsx)$/.test(filePath)),
      ...walkFiles(
        path.join(repoRoot, 'android'),
        (filePath) =>
          /\.(kt|xml)$/.test(filePath) &&
          !toPosix(filePath).includes('/build/'),
      ),
      ...walkFiles(path.join(repoRoot, 'design-system'), (filePath) => /\.(json|ts|tsx|less)$/.test(filePath)),
      ...walkFiles(path.join(repoRoot, 'scripts'), (filePath) => /\.js$/.test(filePath)),
      path.join(repoRoot, 'package.json'),
      path.join(repoRoot, 'index.web.ts'),
      path.join(repoRoot, 'webpack.config.js'),
    ];

    const offenders = [];

    files.forEach((filePath) => {
      if (!fs.existsSync(filePath)) {
        return;
      }

      const source = fs.readFileSync(filePath, 'utf8');
      if (/stage7|Stage7|stage-7/.test(source)) {
        offenders.push(toPosix(path.relative(repoRoot, filePath)));
      }
    });

    expect(offenders).toEqual([]);
  });
});
