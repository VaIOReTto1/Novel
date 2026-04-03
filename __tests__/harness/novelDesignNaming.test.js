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
  test('code layer and command layer no longer use ad-hoc stage7-prefixed names beyond sanctioned closeout entrypoints', () => {
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
    const allowedFilePaths = new Set([
      'scripts/stage7-closeout-readiness.js',
      'scripts/stage7-signoff-check.js',
    ]);
    const allowedPatterns = new Map([
      [
        'package.json',
        [
          /"stage7:closeout"/g,
          /"stage7:closeout:check"/g,
          /"stage7:signoff:check"/g,
          /scripts\/stage7-closeout-readiness\.js/g,
          /scripts\/stage7-signoff-check\.js/g,
        ],
      ],
    ]);

    files.forEach((filePath) => {
      if (!fs.existsSync(filePath)) {
        return;
      }

      const relativePath = toPosix(path.relative(repoRoot, filePath));
      if (allowedFilePaths.has(relativePath)) {
        return;
      }

      let source = fs.readFileSync(filePath, 'utf8');
      const replacements = allowedPatterns.get(relativePath) || [];
      replacements.forEach((pattern) => {
        source = source.replace(pattern, '');
      });

      if (/stage7|Stage7|stage-7/.test(source)) {
        offenders.push(relativePath);
      }
    });

    expect(offenders).toEqual([]);
  });
});
