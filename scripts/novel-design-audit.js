#!/usr/bin/env node

const fs = require('fs');
const os = require('os');
const path = require('path');

const DEFAULT_OUTPUT_DIR = path.join(
  'docs',
  'refactor',
  'phase-15',
);

const toPosix = (value) => value.split(path.sep).join('/');

const ensureDir = (dirPath) => {
  fs.mkdirSync(dirPath, { recursive: true });
};

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

const readText = (repoRoot, relativePath) =>
  fs.readFileSync(path.join(repoRoot, relativePath), 'utf8');

const writeText = (absolutePath, content) => {
  ensureDir(path.dirname(absolutePath));
  fs.writeFileSync(absolutePath, content, 'utf8');
};

const writeJson = (absolutePath, data) => {
  writeText(absolutePath, `${JSON.stringify(data, null, 2)}\n`);
};

const relativeRepoPath = (repoRoot, absolutePath) =>
  toPosix(path.relative(repoRoot, absolutePath));

const unique = (items) => [...new Set(items.filter(Boolean))];

const slugify = (value) =>
  value
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .replace(/[^A-Za-z0-9]+/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-|-$/g, '')
    .toLowerCase();

const componentNameFromFile = (absolutePath) =>
  path.basename(absolutePath).replace(/\.(ts|tsx|kt)$/, '');

const detectAssetSources = (source) => {
  const tags = [];

  if (/react-native-vector-icons\/([A-Za-z0-9]+)/.test(source)) {
    tags.push('react-native-vector-icons');
  }
  if (/\.svg['"]/.test(source) || /assets\/image\/.*\.svg/.test(source)) {
    tags.push('local-svg');
  }
  if (/NovelImageView|ProgressiveImageView|Image\(/.test(source)) {
    tags.push('image');
  }
  if (/ThemeSwitcher|theme/i.test(source)) {
    tags.push('theme-aware');
  }

  return unique(tags);
};

const inferComponentCategory = (relativePath) => {
  const baseName = path.basename(relativePath, path.extname(relativePath));

  if (/Showcase/i.test(baseName)) {
    return 'showcase';
  }
  if (/TopBar|Header|Nav|TabBar|BackButton/i.test(baseName)) {
    return 'navigation';
  }
  if (/Modal|Dialog/i.test(baseName)) {
    return 'dialog';
  }
  if (/Sheet|BottomSheet/i.test(baseName)) {
    return 'sheet';
  }
  if (/Loading|Skeleton|Spinner/i.test(baseName)) {
    return 'loading';
  }
  if (/Empty|Placeholder/i.test(baseName)) {
    return 'empty';
  }
  if (/List|Grid|Waterfall/i.test(baseName)) {
    return 'list';
  }
  if (/Card|Item|Tile|Row/i.test(baseName)) {
    return 'item';
  }
  if (/Input|Form|Search|Picker|Switch|TextField/i.test(baseName)) {
    return 'form';
  }
  if (/Button|Toolbar|Action|Filter|Purchase/i.test(baseName)) {
    return 'action';
  }
  if (/Image|Avatar|Icon/i.test(baseName)) {
    return 'media';
  }

  return 'layout';
};

const collectAndroidComponentEntries = (repoRoot) => {
  const androidRoots = [
    path.join(repoRoot, 'android', 'core-ui', 'src', 'main', 'java', 'com', 'novel', 'page', 'component'),
    path.join(repoRoot, 'android', 'app', 'src', 'main', 'java', 'com', 'novel', 'page'),
    path.join(repoRoot, 'android', 'core-ui', 'src', 'main', 'java', 'com', 'novel', 'ui', 'showcase'),
  ];
  const files = [];

  androidRoots.forEach((rootPath) => {
    walkFiles(
      rootPath,
      (filePath) =>
        filePath.endsWith('.kt') &&
        (
          filePath.includes(`${path.sep}component${path.sep}`) ||
          filePath.includes(`${path.sep}components${path.sep}`) ||
          filePath.includes(`${path.sep}skeleton${path.sep}`) ||
          filePath.includes(`${path.sep}showcase${path.sep}`)
        ),
      files,
    );
  });

  return unique(files).map((filePath) => {
    const relativePath = relativeRepoPath(repoRoot, filePath);
    const source = fs.readFileSync(filePath, 'utf8');
    return {
      path: relativePath,
      name: componentNameFromFile(filePath),
      platform: 'android',
      category: inferComponentCategory(relativePath),
      asset_sources: detectAssetSources(source),
    };
  });
};

const sampleNeighborComponents = (repoRoot, absolutePath) => {
  const pageDir = path.dirname(absolutePath);
  const candidateDirs = [
    path.join(pageDir, 'components'),
    path.join(pageDir, 'component'),
    path.join(pageDir, 'components', 'items'),
    path.join(path.dirname(pageDir), 'components'),
    path.join(pageDir, 'skeleton'),
  ];
  const componentNames = [];

  candidateDirs.forEach((dirPath) => {
    if (!fs.existsSync(dirPath)) {
      return;
    }
    walkFiles(
      dirPath,
      (filePath) => /\.(ts|tsx|kt)$/.test(filePath),
      componentNames,
    );
  });

  return unique(
    componentNames
      .map((filePath) => componentNameFromFile(filePath))
      .sort(),
  ).slice(0, 8);
};

const createSurface = ({
  surfaceId,
  platform,
  entryType,
  hostType,
  parentSurface = '',
  keyStates = ['default'],
  keyComponents = [],
  assetSources = [],
  sourcePaths = [],
}) => ({
  surface_id: surfaceId,
  platform,
  entry_type: entryType,
  host_type: hostType,
  parent_surface: parentSurface,
  key_states: keyStates,
  key_components: keyComponents,
  asset_sources: assetSources,
  a11y_status: 'pending-audit',
  rtl_status: 'pending-audit',
  dark_mode_status: 'pending-audit',
  figma_frame_id: '',
  visual_regression_case_id: `${surfaceId}-vr`,
  implementation_status: 'active',
  source_paths: sourcePaths,
});

const collectRnHostSurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) => filePath.endsWith('Component.tsx'),
  );

  return files
    .map((filePath) => {
      const source = fs.readFileSync(filePath, 'utf8');
      const registrationMatch = source.match(
        /AppRegistry\.registerComponent\('([^']+)'/,
      );
      if (!registrationMatch) {
        return null;
      }

      const registeredName = registrationMatch[1];
      const surfaceId = `rn-host-${slugify(registeredName)}`;

      return createSurface({
        surfaceId,
        platform: 'react-native',
        entryType: 'component-registration',
        hostType: 'rn-host',
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativeRepoPath(repoRoot, filePath)],
      });
    })
    .filter(Boolean)
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const collectRnRootSurfaces = (repoRoot) => {
  const appSource = readText(repoRoot, 'App.tsx');
  const profileSource = readText(repoRoot, 'src/page/ProfilePage/ProfilePage.tsx');

  return [
    createSurface({
      surfaceId: 'rn-root-profile-page',
      platform: 'react-native',
      entryType: 'app-root',
      hostType: 'rn-root',
      keyComponents: sampleNeighborComponents(
        repoRoot,
        path.join(repoRoot, 'src/page/ProfilePage/ProfilePage.tsx'),
      ),
      assetSources: unique([
        ...detectAssetSources(appSource),
        ...detectAssetSources(profileSource),
      ]),
      sourcePaths: ['App.tsx', 'src/page/ProfilePage/ProfilePage.tsx'],
    }),
  ];
};

const nestedPageSurfaceId = (relativePath) => {
  const match = relativePath.match(
    /^src\/page\/([^/]+)\/pages\/([^/]+)\/([^/]+)\.tsx$/,
  );

  if (!match) {
    return `rn-nested-${slugify(relativePath.replace(/^src\/page\//, '').replace(/\.tsx$/, ''))}`;
  }

  const [, rootPage, folderName] = match;
  const normalizedRoot = rootPage.replace(/Page$/, '');
  return `rn-nested-${slugify(`${normalizedRoot}-${folderName}-page`)}`;
};

const collectRnNestedSurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) =>
      /\.tsx$/.test(filePath) &&
      filePath.includes(`${path.sep}pages${path.sep}`) &&
      filePath.endsWith('Page.tsx'),
  );

  return files
    .map((filePath) => {
      const relativePath = relativeRepoPath(repoRoot, filePath);
      const source = fs.readFileSync(filePath, 'utf8');
      const parentSurface = relativePath.startsWith('src/page/BookshelfPage/pages/')
        ? 'rn-host-bookshelf-page-component'
        : '';

      return createSurface({
        surfaceId: nestedPageSurfaceId(relativePath),
        platform: 'react-native',
        entryType: 'nested-page',
        hostType: 'rn-nested',
        parentSurface,
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativePath],
      });
    })
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const collectAndroidNativeSurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'android', 'app', 'src', 'main', 'java', 'com', 'novel', 'page'),
    (filePath) =>
      filePath.endsWith('Page.kt') &&
      !filePath.includes(`${path.sep}component${path.sep}`) &&
      !filePath.includes(`${path.sep}components${path.sep}`) &&
      !filePath.includes(`${path.sep}skeleton${path.sep}`),
  );

  return files
    .map((filePath) => {
      const source = fs.readFileSync(filePath, 'utf8');
      const baseName = componentNameFromFile(filePath).replace(/Page$/, '');

      return createSurface({
        surfaceId: `android-native-${slugify(`${baseName} Page`)}`,
        platform: 'android',
        entryType: 'native-screen',
        hostType: 'android-native',
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativeRepoPath(repoRoot, filePath)],
      });
    })
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const collectAndroidShellSurfaces = (repoRoot) => {
  const shellFiles = [
    'android/app/src/main/java/com/novel/MainActivity.kt',
    'android/app/src/main/java/com/novel/ComposeMainActivity.kt',
    'android/app/src/main/java/com/novel/page/MainPageHostComponents.kt',
    'android/app/src/main/java/com/novel/rn/ReactNativePage.kt',
    'android/feature-rn-host/src/main/java/com/novel/rn/ReactNativePageContent.kt',
  ];

  return shellFiles
    .filter((relativePath) => fs.existsSync(path.join(repoRoot, relativePath)))
    .map((relativePath) => {
      const source = readText(repoRoot, relativePath);
      const fileLabel = componentNameFromFile(relativePath);

      return createSurface({
        surfaceId: `android-shell-${slugify(fileLabel)}`,
        platform: 'android',
        entryType: 'shell',
        hostType: 'android-shell',
        keyComponents: [],
        assetSources: detectAssetSources(source),
        sourcePaths: [relativePath],
      });
    });
};

const collectAndroidOverlaySurfaces = (repoRoot) => {
  const files = walkFiles(
    path.join(repoRoot, 'android', 'app', 'src', 'main', 'java', 'com', 'novel', 'page'),
    (filePath) =>
      /(Skeleton|BottomSheet|Panel|Dialog)\.kt$/.test(filePath),
  );

  return files
    .map((filePath) => {
      const source = fs.readFileSync(filePath, 'utf8');
      const baseName = componentNameFromFile(filePath);

      return createSurface({
        surfaceId: `android-overlay-${slugify(baseName)}`,
        platform: 'android',
        entryType: 'overlay',
        hostType: 'android-overlay',
        keyComponents: sampleNeighborComponents(repoRoot, filePath),
        assetSources: detectAssetSources(source),
        sourcePaths: [relativeRepoPath(repoRoot, filePath)],
      });
    })
    .sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const buildSurfaceInventory = (repoRoot) => {
  const surfaces = [
    ...collectRnRootSurfaces(repoRoot),
    ...collectRnHostSurfaces(repoRoot),
    ...collectRnNestedSurfaces(repoRoot),
    ...collectAndroidNativeSurfaces(repoRoot),
    ...collectAndroidShellSurfaces(repoRoot),
    ...collectAndroidOverlaySurfaces(repoRoot),
  ];

  return surfaces.sort((left, right) => left.surface_id.localeCompare(right.surface_id));
};

const determineSurfaceCluster = (surface) => {
  const joined = `${surface.surface_id} ${surface.source_paths.join(' ')}`.toLowerCase();

  if (surface.surface_id === 'rn-root-profile-page') {
    return 'profile-root';
  }
  if (joined.includes('bookshelf-page-component')) {
    return 'bookshelf-shell';
  }
  if (joined.includes('bookshelf-')) {
    return 'bookshelf-content';
  }
  if (joined.includes('settings')) {
    return 'settings-stack';
  }
  if (joined.includes('category')) {
    return 'category-browser';
  }
  if (joined.includes('comment') || joined.includes('review')) {
    return 'comment-review';
  }
  if (joined.includes('write') || joined.includes('bookmanage') || joined.includes('aiwriteassistant')) {
    return 'writer-tooling';
  }
  if (joined.includes('main-page-host-components') || joined.includes('react-native-page-content') || joined.includes('react-native-page')) {
    return 'android-host-shell';
  }
  if (joined.includes('main-page')) {
    return 'android-main-shell';
  }
  if (joined.includes('home')) {
    return 'android-home-discovery';
  }
  if (joined.includes('login')) {
    return 'auth-single-column';
  }
  if (joined.includes('search-result')) {
    return 'android-search-results';
  }
  if (joined.includes('search-page') || joined.includes('full-ranking')) {
    return 'android-search-discovery';
  }
  if (joined.includes('book-detail')) {
    return 'android-book-detail';
  }
  if (joined.includes('reader')) {
    return 'immersive-reader';
  }
  if (joined.includes('welfare')) {
    return 'welfare-webview';
  }
  if (surface.host_type === 'android-overlay') {
    if (joined.includes('skeleton')) {
      return 'overlay-skeleton';
    }
    if (joined.includes('bottom-sheet')) {
      return 'overlay-bottom-sheet';
    }
    if (joined.includes('panel')) {
      return 'overlay-panel';
    }
    return 'overlay-dialog';
  }

  return 'utility-detail';
};

const SURFACE_CLUSTER_PLANS = {
  'profile-root': {
    current: {
      layout: 'scrollable profile shell with top bar, identity header, icon rail, balances and waterfall feed',
      chrome: 'icon-led utility chrome with direct theme toggles and lightweight section separation',
      content_pattern: 'dashboard-like profile summary followed by card/list content',
    },
    target: {
      layout_strategy: 'editorial-profile-dashboard with stronger vertical rhythm and inset group cards',
      component_recipe: 'editorial-profile-shell',
      style_keywords: ['literary', 'warm-neutral', 'editorial-dashboard'],
    },
  },
  'bookshelf-shell': {
    current: {
      layout: 'host shell with top tab bar and nested pages toggled in place',
      chrome: 'utilitarian tab strip and full-width content region',
      content_pattern: 'multi-tab content container for bookshelf, history, watchlist and community',
    },
    target: {
      layout_strategy: 'editorial-tab-shell with inset navigation rail and sectioned content body',
      component_recipe: 'editorial-tab-shell',
      style_keywords: ['library', 'modular', 'paper-tabs'],
    },
  },
  'bookshelf-content': {
    current: {
      layout: 'content-heavy bookshelf surfaces using grids, lists, top bars and empty/loading states',
      chrome: 'top-bar-first navigation with dense card/list presentation',
      content_pattern: 'collection browsing, filtering and feed-style stacking',
    },
    target: {
      layout_strategy: 'editorial-library-grid with calmer spacing and modular cards',
      component_recipe: 'editorial-library-content',
      style_keywords: ['collection', 'shelf-cards', 'quiet-density'],
    },
  },
  'settings-stack': {
    current: {
      layout: 'stacked grouped settings rows with modals and hardware-back glue',
      chrome: 'plain top bar and utility sections with switches and rows',
      content_pattern: 'settings groups, policy/help routes and switch-heavy controls',
    },
    target: {
      layout_strategy: 'editorial-settings-groups with panelized sections and explicit sublabels',
      component_recipe: 'editorial-settings-panel',
      style_keywords: ['grouped-panels', 'utility', 'warm-controls'],
    },
  },
  'category-browser': {
    current: {
      layout: 'sidebar plus top tabs plus book grid',
      chrome: 'category navigation prioritized over immersive content',
      content_pattern: 'taxonomy navigation and cover-driven result grid',
    },
    target: {
      layout_strategy: 'editorial-catalog-browser with stronger hierarchy between taxonomy and content',
      component_recipe: 'editorial-catalog-grid',
      style_keywords: ['catalog', 'taxonomy', 'cover-grid'],
    },
  },
  'comment-review': {
    current: {
      layout: 'review and comment pages with top bars, rating blocks, lists and reply sheets',
      chrome: 'action icons and dense comment rows',
      content_pattern: 'threaded community content with detail, reply and compose states',
    },
    target: {
      layout_strategy: 'editorial-review-thread with calmer cards and stronger reading hierarchy',
      component_recipe: 'editorial-review-stack',
      style_keywords: ['review-cards', 'threaded', 'community-reading'],
    },
  },
  'writer-tooling': {
    current: {
      layout: 'toolbar-led creation pages with editors, prompts, assistant blocks and modals',
      chrome: 'utility-first creation chrome with mixed icon families',
      content_pattern: 'compose, assist, manage drafts and modal parameter flows',
    },
    target: {
      layout_strategy: 'editorial-creation-workbench with paper surfaces and compact action rails',
      component_recipe: 'editorial-writer-workbench',
      style_keywords: ['creation', 'tooling', 'paper-editor'],
    },
  },
  'android-host-shell': {
    current: {
      layout: 'native shell around React Native surfaces with loading bridge and cached root views',
      chrome: 'host-level background and navigation orchestration',
      content_pattern: 'container-only host for embedded RN screens',
    },
    target: {
      layout_strategy: 'quiet-host-shell that minimizes chrome and frames embedded content cleanly',
      component_recipe: 'host-shell',
      style_keywords: ['embedded', 'minimal-chrome', 'bridge-safe'],
    },
  },
  'android-main-shell': {
    current: {
      layout: 'pager-based native shell with bottom navigation and overlay stack',
      chrome: 'bottom-app-bar navigation and full-screen pager',
      content_pattern: 'tab shell mixing native pages and RN hosts',
    },
    target: {
      layout_strategy: 'editorial-main-shell with calmer bottom chrome and stronger page transitions',
      component_recipe: 'editorial-main-shell',
      style_keywords: ['bottom-nav', 'host-shell', 'reader-brand'],
    },
  },
  'android-home-discovery': {
    current: {
      layout: 'compose discovery page with top bar, ranking, recommendation and media-heavy grids',
      chrome: 'top discovery bar and stacked sections',
      content_pattern: 'home discovery feed with rank and recommendation modules',
    },
    target: {
      layout_strategy: 'editorial-discovery-home with magazine-like section pacing',
      component_recipe: 'editorial-discovery',
      style_keywords: ['magazine', 'discovery', 'modular-sections'],
    },
  },
  'auth-single-column': {
    current: {
      layout: 'single-column auth page with hero title, inputs, agreement and actions',
      chrome: 'minimal auth chrome focused on task completion',
      content_pattern: 'form-first authentication flow',
    },
    target: {
      layout_strategy: 'editorial-auth-panel with warmer framing and stronger trust cues',
      component_recipe: 'editorial-auth',
      style_keywords: ['auth', 'trust', 'warm-form'],
    },
  },
  'android-search-discovery': {
    current: {
      layout: 'search top bar with history, ranking and filter affordances',
      chrome: 'search-led chrome with stacked results modules',
      content_pattern: 'search discovery and ranking exploration',
    },
    target: {
      layout_strategy: 'editorial-search-discovery with structured prompts and refined ranking blocks',
      component_recipe: 'editorial-search',
      style_keywords: ['search', 'ranking', 'structured-discovery'],
    },
  },
  'android-search-results': {
    current: {
      layout: 'result list with filters and infinite scrolling',
      chrome: 'search header plus utility filters',
      content_pattern: 'cover-and-metadata search result cards',
    },
    target: {
      layout_strategy: 'editorial-search-results with denser metadata hierarchy and calmer card rhythm',
      component_recipe: 'editorial-search-results',
      style_keywords: ['results', 'metadata', 'filter-pills'],
    },
  },
  'android-book-detail': {
    current: {
      layout: 'detail screen with cover, stats, actions, reviews and expandable description',
      chrome: 'book-first hero and stacked supporting sections',
      content_pattern: 'book presentation with actions and community proof',
    },
    target: {
      layout_strategy: 'editorial-book-detail with hero cover stage and annotated metadata bands',
      component_recipe: 'editorial-book-detail',
      style_keywords: ['detail-hero', 'metadata', 'review-proof'],
    },
  },
  'immersive-reader': {
    current: {
      layout: 'immersive reader with reading canvas, page-flip modes and settings/chapters overlays',
      chrome: 'minimal reader chrome revealed through overlays',
      content_pattern: 'text-first reading flow with contextual panels',
    },
    target: {
      layout_strategy: 'immersive-reader with paper-toned canvas and quieter controls',
      component_recipe: 'immersive-reader',
      style_keywords: ['reader', 'paper-canvas', 'immersive'],
    },
  },
  'welfare-webview': {
    current: {
      layout: 'native wrapper around welfare/webview content with accessibility helpers',
      chrome: 'hosted browser-like shell',
      content_pattern: 'web content framed inside native surface',
    },
    target: {
      layout_strategy: 'editorial-webview-shell that better frames external content without stealing focus',
      component_recipe: 'webview-shell',
      style_keywords: ['webview', 'framed-content', 'minimal-host'],
    },
  },
  'overlay-skeleton': {
    current: {
      layout: 'skeleton placeholders mirroring major screen blocks',
      chrome: 'placeholder-only support surface',
      content_pattern: 'loading state approximation of destination content',
    },
    target: {
      layout_strategy: 'editorial-skeleton with warm shimmer and structural rhythm',
      component_recipe: 'skeleton-loading',
      style_keywords: ['skeleton', 'warm-shimmer', 'structural-loading'],
    },
  },
  'overlay-bottom-sheet': {
    current: {
      layout: 'bottom-sheet action and metadata surfaces',
      chrome: 'utility sheet chrome with close affordance',
      content_pattern: 'temporary contextual detail and actions',
    },
    target: {
      layout_strategy: 'editorial-bottom-sheet with paper panel hierarchy and quieter controls',
      component_recipe: 'dialog-sheet',
      style_keywords: ['bottom-sheet', 'paper-panel', 'contextual'],
    },
  },
  'overlay-panel': {
    current: {
      layout: 'floating side or settings panels tied to reader/search flows',
      chrome: 'control-heavy panel chrome',
      content_pattern: 'temporary controls and selection lists',
    },
    target: {
      layout_strategy: 'editorial-control-panel with grouped toggles and soft elevation',
      component_recipe: 'control-panel',
      style_keywords: ['panel', 'controls', 'soft-elevation'],
    },
  },
  'overlay-dialog': {
    current: {
      layout: 'modal dialog or transient overlay with focused actions',
      chrome: 'scrim plus centered action surface',
      content_pattern: 'interruptive confirmation or launch experience',
    },
    target: {
      layout_strategy: 'editorial-dialog with warmer cards and clear action hierarchy',
      component_recipe: 'dialog-overlay',
      style_keywords: ['dialog', 'confirm', 'warm-scrim'],
    },
  },
  'utility-detail': {
    current: {
      layout: 'single-purpose page with top bar and one dominant content block',
      chrome: 'utility navigation with straightforward content stack',
      content_pattern: 'detail/help/message/history style utility surface',
    },
    target: {
      layout_strategy: 'editorial-utility-detail with stronger section grouping and quieter chrome',
      component_recipe: 'utility-detail',
      style_keywords: ['utility', 'single-purpose', 'grouped-detail'],
    },
  },
};

const RESKINNED_SURFACES = new Set([
  'rn-root-profile-page',
  'android-native-book-detail-page',
  'android-native-main-page',
  'android-native-reader-page',
  'android-native-welfare-page',
  'android-shell-compose-main-activity',
  'android-shell-main-activity',
  'android-shell-main-page-host-components',
  'android-shell-react-native-page',
  'android-shell-react-native-page-content',
  'android-overlay-book-description-bottom-sheet',
  'android-overlay-chapter-list-panel',
  'android-overlay-home-rank-panel',
  'android-overlay-reader-settings-panel',
  'android-overlay-search-filter-bottom-sheet',
  'android-native-login-page',
  'android-native-home-page',
  'android-native-full-ranking-page',
  'android-native-search-page',
  'android-native-search-result-page',
  'android-overlay-full-ranking-page-skeleton',
  'android-overlay-home-page-skeleton',
  'android-overlay-login-page-skeleton',
  'android-overlay-search-page-skeleton',
  'android-overlay-search-result-page-skeleton',
  'rn-host-become-writer-page-component',
  'rn-host-settings-page-component',
  'rn-host-timed-switch-page-component',
  'rn-host-category-page-component',
  'rn-host-bookshelf-page-component',
  'rn-host-member-center-page-component',
  'rn-host-comment-page-component',
  'rn-host-feedback-help-main-page-component',
  'rn-host-help-support-page-component',
  'rn-host-history-page-component',
  'rn-host-message-page-component',
  'rn-host-my-reservation-page-component',
  'rn-host-privacy-policy-page-component',
  'rn-host-question-detail-page-component',
  'rn-host-question-list-page-component',
  'rn-host-recommend-book-page-component',
  'rn-host-review-detail-page-component',
  'rn-host-write-review-page-component',
  'rn-host-write-page-component',
  'rn-host-viewed-users-page-component',
  'rn-host-aiwrite-assistant-component',
  'rn-host-book-manage-page-component',
  'rn-nested-bookshelf-bookshelf-page',
  'rn-nested-bookshelf-community-page',
  'rn-nested-bookshelf-history-page',
  'rn-nested-bookshelf-watchlist-page',
]);

const NOVEL_DESIGN_READY_COMPONENTS = new Set([
  'android/app/src/main/java/com/novel/page/MainPageHostComponents.kt',
  'android/app/src/main/java/com/novel/page/book/components/AuthorSection.kt',
  'android/app/src/main/java/com/novel/page/book/components/BookActionSection.kt',
  'android/app/src/main/java/com/novel/page/book/components/BookCoverSection.kt',
  'android/app/src/main/java/com/novel/page/book/components/BookDescriptionSection.kt',
  'android/app/src/main/java/com/novel/page/book/components/BookDescriptionBottomSheet.kt',
  'android/app/src/main/java/com/novel/page/book/components/BookReviewsSection.kt',
  'android/app/src/main/java/com/novel/page/book/components/BookStatsSection.kt',
  'android/app/src/main/java/com/novel/page/book/components/BookTitleSection.kt',
  'android/app/src/main/java/com/novel/page/component/BackButton.kt',
  'android/core-ui/src/main/java/com/novel/page/component/NovelButton.kt',
  'android/core-ui/src/main/java/com/novel/page/component/NovelDivider.kt',
  'android/core-ui/src/main/java/com/novel/page/component/NovelText.kt',
  'android/core-ui/src/main/java/com/novel/page/component/NovelTextField.kt',
  'android/app/src/main/java/com/novel/page/login/component/ActionButtons.kt',
  'android/app/src/main/java/com/novel/page/login/component/AppBar.kt',
  'android/app/src/main/java/com/novel/page/login/component/AgreementSection.kt',
  'android/app/src/main/java/com/novel/page/login/component/InputSection.kt',
  'android/app/src/main/java/com/novel/page/login/component/OperatorSection.kt',
  'android/app/src/main/java/com/novel/page/login/component/PhoneSection.kt',
  'android/app/src/main/java/com/novel/page/login/component/TitleSection.kt',
  'android/app/src/main/java/com/novel/page/login/skeleton/LoginPageSkeleton.kt',
  'android/app/src/main/java/com/novel/page/home/component/HomeFilterBar.kt',
  'android/app/src/main/java/com/novel/page/home/component/HomeRankPanel.kt',
  'android/app/src/main/java/com/novel/page/home/component/HomeTopBar.kt',
  'android/app/src/main/java/com/novel/page/home/skeleton/HomePageSkeleton.kt',
  'android/app/src/main/java/com/novel/page/search/FullRankingPage.kt',
  'android/app/src/main/java/com/novel/page/search/component/HistoryGrid.kt',
  'android/app/src/main/java/com/novel/page/search/component/RankingList.kt',
  'android/app/src/main/java/com/novel/page/search/component/SearchFilterChip.kt',
  'android/app/src/main/java/com/novel/page/search/component/SearchFilterBottomSheet.kt',
  'android/app/src/main/java/com/novel/page/search/component/SearchResultItem.kt',
  'android/app/src/main/java/com/novel/page/search/component/SearchTopBar.kt',
  'android/app/src/main/java/com/novel/page/search/skeleton/FullRankingPageSkeleton.kt',
  'android/app/src/main/java/com/novel/page/search/skeleton/SearchPageSkeleton.kt',
  'android/app/src/main/java/com/novel/page/search/skeleton/SearchResultPageSkeleton.kt',
  'android/app/src/main/java/com/novel/page/read/components/ChapterListPanel.kt',
  'android/app/src/main/java/com/novel/page/read/components/ReaderSettingsPanel.kt',
  'android/feature-rn-host/src/main/java/com/novel/rn/ReactNativePageContent.kt',
  'android/feature-welfare/src/main/java/com/novel/page/welfare/WelfarePageContent.kt',
  'src/page/BookshelfPage/pages/Bookshelf/components/BookItem.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/EmptyState.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/LoadMoreIndicator.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/RecommendationFlow.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/TopBar.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/UnifiedScrollView.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/ViewSwitcher.tsx',
  'src/page/BookshelfPage/components/MainTabBar.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/EditToolbar.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/GridView.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/ListView.tsx',
  'src/page/BookshelfPage/pages/Bookshelf/components/WaterfallGrid.tsx',
  'src/page/BookshelfPage/pages/Community/components/FilterBar.tsx',
  'src/page/BookshelfPage/pages/Community/components/FloatingButton.tsx',
  'src/page/BookshelfPage/pages/Community/components/LoadingIndicator.tsx',
  'src/page/BookshelfPage/pages/Community/components/EmptyState.tsx',
  'src/page/BookshelfPage/pages/Community/components/PostItem.tsx',
  'src/page/BookshelfPage/pages/Community/components/PostList.tsx',
  'src/page/BookshelfPage/pages/Community/components/TabBar.tsx',
  'src/page/BookshelfPage/pages/Community/components/TopBar.tsx',
  'src/page/BookshelfPage/pages/History/components/TabBar.tsx',
  'src/page/BookshelfPage/pages/History/components/EditToolbar.tsx',
  'src/page/BookshelfPage/pages/History/components/HistoryItem.tsx',
  'src/page/BookshelfPage/pages/History/components/RefreshIndicator.tsx',
  'src/page/BookshelfPage/pages/Watchlist/components/TopBar.tsx',
  'src/page/BookshelfPage/pages/Watchlist/components/EditToolbar.tsx',
  'src/page/BookshelfPage/pages/Watchlist/components/EmptyState.tsx',
  'src/page/BookshelfPage/pages/Watchlist/components/WatchlistGrid.tsx',
  'src/page/CategoryPage/components/BookGrid.tsx',
  'src/page/CategoryPage/components/Sidebar.tsx',
  'src/page/CategoryPage/components/TopTabs.tsx',
  'src/page/comment/CommentPage/components/CategorySection.tsx',
  'src/page/comment/CommentPage/components/CommentList.tsx',
  'src/page/comment/CommentPage/components/RatingSection.tsx',
  'src/page/comment/CommentPage/components/RefreshIndicator.tsx',
  'src/page/comment/CommentPage/components/TopBar.tsx',
  'src/page/comment/ReviewDetailPage/components/CommentList.tsx',
  'src/page/comment/ReviewDetailPage/components/RefreshIndicator.tsx',
  'src/page/comment/ReviewDetailPage/components/TopBar.tsx',
  'src/page/comment/ReviewDetailPage/components/RepliesSheet.tsx',
  'src/page/comment/ReviewDetailPage/components/ReviewContent.tsx',
  'src/page/comment/WriteReviewPage/components/index.ts',
  'src/page/comment/WriteReviewPage/components/TopBar.tsx',
  'src/page/comment/WriteReviewPage/components/RatingInput.tsx',
  'src/page/comment/WriteReviewPage/components/ReviewForm.tsx',
  'src/page/ProfilePage/components/BottomBox.tsx',
  'src/page/ProfilePage/components/BookItem.tsx',
  'src/page/ProfilePage/components/LoginBar.tsx',
  'src/page/ProfilePage/components/LoadMoreIndicator.tsx',
  'src/page/ProfilePage/components/RefreshIndicator.tsx',
  'src/page/ProfilePage/components/ScrollableArea.tsx',
  'src/page/ProfilePage/components/WaterfallGrid.tsx',
  'src/page/SettingsPage/settingspage/components/index.ts',
  'src/page/SettingsPage/settingspage/components/SettingRow.tsx',
  'src/page/SettingsPage/settingspage/components/ThemeSwitcher.tsx',
  'src/page/ScrollBox/BecomeWriterPage/styles/BecomeWriterPageStyles.ts',
  'src/page/ScrollBox/RecommendBookPage/styles/RecommendBookPageStyles.ts',
  'src/page/Writer/AIWriteAssistant/components/ActionBar.tsx',
  'src/page/Writer/AIWriteAssistant/components/Header.tsx',
  'src/page/Writer/AIWriteAssistant/components/IdeaSelector.tsx',
  'src/page/Writer/AIWriteAssistant/components/InputBar.tsx',
  'src/page/Writer/AIWriteAssistant/components/IntroExample.tsx',
  'src/page/Writer/AIWriteAssistant/components/Suggestions.tsx',
  'src/page/Writer/AIWriteAssistant/components/ThinkingBlock.tsx',
  'src/page/ScrollBox/MyReservationPage/styles/MyReservationPageStyles.ts',
  'src/page/ScrollBox/ViewedUsersPage/styles/ViewedUsersPageStyles.ts',
  'src/page/Writer/BookManage/components/Banner.tsx',
  'src/page/Writer/BookManage/components/ChapterSection.tsx',
  'src/page/Writer/BookManage/components/DraftBar.tsx',
  'src/page/Writer/BookManage/components/EmptyChapter.tsx',
  'src/page/Writer/BookManage/components/Footer.tsx',
  'src/page/Writer/BookManage/components/Header.tsx',
  'src/page/ProfilePage/components/TopBar.tsx',
  'src/page/ProfilePage/styles/ProfilePageStyles.ts',
  'src/page/ScrollBox/FeedbackHelpPage/styles/FeedbackHelpPageStyles.ts',
  'src/page/ScrollBox/HistoryPage/styles/HistoryPageStyles.ts',
  'src/page/ScrollBox/MessagePage/components/EmptyState.tsx',
  'src/page/ScrollBox/MessagePage/components/LoadMoreIndicator.tsx',
  'src/page/ScrollBox/MessagePage/components/MainMessagesSection.tsx',
  'src/page/ScrollBox/MessagePage/components/MessageItem.tsx',
  'src/page/ScrollBox/MessagePage/components/TabsArea.tsx',
  'src/page/ScrollBox/MessagePage/components/TopBar.tsx',
  'src/page/ScrollBox/MessagePage/styles/MessagePageStyles.ts',
  'src/page/ScrollBox/MyReservationPage/styles/MyReservationPageStyles.ts',
  'src/page/ScrollBox/ViewedUsersPage/styles/ViewedUsersPageStyles.ts',
  'src/page/SettingsPage/helpsupportPage/styles/HelpSupportPageStyles.ts',
  'src/page/SettingsPage/privacypolicyPage/styles/PrivacyPolicyPageStyles.ts',
  'src/page/SettingsPage/settingspage/styles/SettingsPageStyles.ts',
  'src/page/CategoryPage/styles/CategoryPageStyles.ts',
  'src/page/BookshelfPage/styles/MainPageStyles.ts',
  'src/page/ScrollBox/MemberCenterPage/styles/MemberCenterPageStyles.ts',
  'src/page/comment/CommentPage/styles/CommentPageStyles.ts',
  'src/page/comment/ReviewDetailPage/styles/ReviewDetailPageStyles.ts',
  'src/page/comment/WriteReviewPage/styles/WriteReviewPageStyles.ts',
  'src/page/Writer/WritePage/styles/WritePageStyles.ts',
  'src/design-system/tokens/novelDesignTokens.ts',
  'src/design-system/tokens/resolveNovelDesignTheme.ts',
  'src/design-system/icons/NovelDesignIcon.tsx',
  'src/design-system/icons/generated/novelDesignIconRegistry.ts',
  'src/design-system/showcase/NovelDesignShowcase.tsx',
  'android/core-ui/src/main/java/com/novel/ui/theme/NovelDesignTokens.kt',
  'android/core-ui/src/main/res/values/novel_design_tokens.xml',
  'android/core-ui/src/main/java/com/novel/ui/showcase/NovelDesignShowcaseScreen.kt',
  'android/core-ui/src/main/java/com/novel/ui/showcase/NovelDesignShowcaseModel.kt',
]);

const SURFACE_VISUAL_OVERRIDES = {
  'rn-host-become-writer-page-component': {
    current_visual_summary: {
      layout: 'editorial creator-growth page with profile strip, announcement rail, benefit modules and fixed bottom CTA',
      chrome: 'creator onboarding chrome balancing utility top bar, segmented tabs and modal registration flow',
      content_pattern: 'benefit rows, timeline, platform logos, activities, courses and registration prompt',
      key_states: ['default', 'tab-switch', 'modal-open', 'loading'],
      key_components: ['TopBar', 'WelcomeModal', 'BenefitsList', 'Timeline', 'BottomButton'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial creator-growth surface with paper cards, elevated support modules and stronger CTA framing',
      component_recipe: 'creator-growth-surface',
      style_keywords: ['creator-growth', 'benefit-cards', 'timeline', 'bottom-cta'],
    },
  },
  'rn-host-write-page-component': {
    current_visual_summary: {
      layout: 'fixed top bar plus full-height editor canvas with welcome panel, selection toolbar and modal overlays',
      chrome: 'utility-first writing chrome with publish button, text actions and bottom volume rail',
      content_pattern: 'title input, long-form editor, contextual toolbars, welcome shortcuts and parameter modal',
      key_states: ['default', 'welcome-panel', 'selection-toolbar', 'parameter-modal', 'loading-overlay'],
      key_components: ['TopBar', 'WelcomePanel', 'VolumeBar', 'SelectionToolbar'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-writer-workbench with quiet top chrome, paper editor and floating assist panels',
      component_recipe: 'editorial-editor-shell',
      style_keywords: ['paper-editor', 'floating-toolbars', 'editorial-writing', 'utility-chrome'],
    },
  },
  'android-native-login-page': {
    current_visual_summary: {
      layout: 'single-column auth screen with title stack, operator copy, inputs, actions and agreement block',
      chrome: 'minimal auth chrome framed by centered content and low-noise backgrounds',
      content_pattern: 'login/register dual-mode form with validation, captcha and agreement actions',
      key_states: ['loading', 'login-mode', 'register-mode', 'validation-error'],
      key_components: ['LoginAppBar', 'TitleSection', 'InputSection', 'ActionButtons', 'AgreementSection'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-auth panel with paper background, restrained accent buttons and stronger form grouping',
      component_recipe: 'editorial-auth-surface',
      style_keywords: ['auth-panel', 'form-stack', 'paper-surface', 'warm-accent'],
    },
  },
  'android-native-home-page': {
    current_visual_summary: {
      layout: 'discovery home with top search bar, filter rail, ranking shelf and recommendation waterfall feed',
      chrome: 'reader-home chrome with stacked discovery modules and pull-to-refresh shell',
      content_pattern: 'search entry, category filters, rank panel, recommendation feed and loading footer',
      key_states: ['loading', 'recommend-mode', 'rank-switch', 'refreshing', 'load-more'],
      key_components: ['HomeTopBar', 'HomeFilterBar', 'HomeRankPanel', 'HomeRecommendGrid'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-discovery home with calmer paper modules and clearer section pacing',
      component_recipe: 'editorial-discovery-home',
      style_keywords: ['discovery-home', 'rank-shelf', 'recommend-waterfall', 'paper-modules'],
    },
  },
  'android-native-full-ranking-page': {
    current_visual_summary: {
      layout: 'collapsing top bar over a single-column ranking list with prominent ranking numerals',
      chrome: 'utility ranking chrome with animated title/subtitle collapse and stacked list rows',
      content_pattern: 'ranking rows, author metadata and hot-search hint labels',
      key_states: ['loading', 'collapsing-header', 'ranking-list'],
      key_components: ['FullRankingPage', 'RankingNumber', 'FullRankingPageSkeleton'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-ranking list with quieter collapsing chrome and paper row rhythm',
      component_recipe: 'editorial-ranking-surface',
      style_keywords: ['ranking-list', 'collapsing-header', 'paper-rows', 'metadata-hints'],
    },
  },
  'android-native-search-page': {
    current_visual_summary: {
      layout: 'search shell with top bar, history chips and multiple ranking shelves',
      chrome: 'search-first chrome with compact top input and stacked discovery modules',
      content_pattern: 'history list, ranking carousels and search discovery entry points',
      key_states: ['loading', 'history-expanded', 'default-discovery'],
      key_components: ['SearchTopBar', 'SearchHistorySection', 'HistoryGrid', 'RankingSection'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-search discovery with paper search rail, softer history chips and curated ranking cards',
      component_recipe: 'editorial-search-discovery-surface',
      style_keywords: ['search-discovery', 'history-chips', 'ranking-cards', 'paper-shell'],
    },
  },
  'android-native-search-result-page': {
    current_visual_summary: {
      layout: 'search top bar plus category filter row and vertically stacked result cards',
      chrome: 'utility search chrome with inline filters and incremental loading footer',
      content_pattern: 'result cards with cover, metadata, chips, empty state and pagination feedback',
      key_states: ['loading', 'loading-more', 'empty-state', 'filter-sheet-open'],
      key_components: ['SearchTopBar', 'CategoryFilterChip', 'SearchResultItem', 'SearchFilterBottomSheet'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-search results with paper cards, softer chips and clearer empty-state hierarchy',
      component_recipe: 'editorial-search-results-surface',
      style_keywords: ['result-cards', 'filter-chips', 'empty-state', 'search-shell'],
    },
  },
  'android-overlay-login-page-skeleton': {
    current_visual_summary: {
      layout: 'login skeleton mirroring auth hierarchy with bars, fields and buttons',
      chrome: 'placeholder-only auth surface',
      content_pattern: 'loading approximation of login/register panel',
      key_states: ['loading'],
      key_components: ['LoginPageSkeleton'],
    },
    target_visual_plan: {
      layout_strategy: 'warm auth skeleton with softer shimmer and stable form geometry',
      component_recipe: 'auth-skeleton',
      style_keywords: ['auth-skeleton', 'warm-shimmer', 'form-placeholder'],
    },
  },
  'android-overlay-home-page-skeleton': {
    current_visual_summary: {
      layout: 'home skeleton with top bar, filter chips, ranking block and recommendation placeholders',
      chrome: 'placeholder-only home discovery shell',
      content_pattern: 'loading approximation of search, ranking and recommendation modules',
      key_states: ['loading'],
      key_components: ['HomePageSkeleton'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-home skeleton with softer search shell and staggered recommendation placeholders',
      component_recipe: 'home-skeleton',
      style_keywords: ['home-skeleton', 'discovery-placeholder', 'warm-shimmer'],
    },
  },
  'android-overlay-full-ranking-page-skeleton': {
    current_visual_summary: {
      layout: 'ranking skeleton with stacked row placeholders and header block',
      chrome: 'placeholder-only ranking shell',
      content_pattern: 'loading approximation of ranking list rows and header',
      key_states: ['loading'],
      key_components: ['FullRankingPageSkeleton'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-ranking skeleton with softer row placeholders and paper header block',
      component_recipe: 'ranking-skeleton',
      style_keywords: ['ranking-skeleton', 'row-placeholder', 'loading-header'],
    },
  },
  'android-overlay-search-page-skeleton': {
    current_visual_summary: {
      layout: 'search discovery skeleton with top bar, history chips and ranking placeholders',
      chrome: 'placeholder-only discovery shell',
      content_pattern: 'loading approximation of search discovery modules',
      key_states: ['loading'],
      key_components: ['SearchPageSkeleton'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-search skeleton with softer chips and ranking-card placeholders',
      component_recipe: 'search-skeleton',
      style_keywords: ['search-skeleton', 'history-placeholder', 'ranking-placeholder'],
    },
  },
  'android-overlay-search-result-page-skeleton': {
    current_visual_summary: {
      layout: 'search results skeleton with stacked card placeholders',
      chrome: 'placeholder-only results shell',
      content_pattern: 'loading approximation of result card list',
      key_states: ['loading'],
      key_components: ['SearchResultPageSkeleton'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-results skeleton with paper-card placeholders and softer separators',
      component_recipe: 'search-results-skeleton',
      style_keywords: ['results-skeleton', 'card-placeholder', 'loading-list'],
    },
  },
  'rn-host-history-page-component': {
    current_visual_summary: {
      layout: 'top bar plus tab strip with history content toggled between grid and list views',
      chrome: 'lightweight utility chrome around tabs, view switch and pull-to-refresh indicator',
      content_pattern: 'reading-history cards with cover, author and progress metadata in two densities',
      key_states: ['default', 'grid-view', 'list-view', 'refreshing'],
      key_components: ['TopBar', 'TabsArea', 'RefreshIndicator', 'ContentArea'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-history ledger with paper tabs, softer list rows and clearer reading progress accents',
      component_recipe: 'history-ledger-surface',
      style_keywords: ['history-ledger', 'paper-tabs', 'reading-progress', 'dual-density'],
    },
  },
  'rn-host-message-page-component': {
    current_visual_summary: {
      layout: 'top bar plus primary message blocks, secondary tabs and sticky threaded message list',
      chrome: 'utility inbox chrome with mark-all action, sticky tabs and pull-to-refresh shell',
      content_pattern: 'system message rows, tabbed secondary notifications, empty state and incremental loading footer',
      key_states: ['default', 'sticky-tabs', 'empty-state', 'refreshing', 'loading-more'],
      key_components: ['TopBar', 'MainMessagesSection', 'TabsArea', 'RefreshIndicator', 'EmptyState', 'LoadMoreIndicator'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-notification inbox with quieter chrome, paper message rows and clearer unread hierarchy',
      component_recipe: 'notification-inbox-surface',
      style_keywords: ['notification-inbox', 'sticky-tabs', 'paper-rows', 'unread-dot'],
    },
  },
  'rn-host-viewed-users-page-component': {
    current_visual_summary: {
      layout: 'top bar plus tab strip over a viewed-users relationship list with empty-state branch',
      chrome: 'quiet social utility chrome with relationship tabs and follow buttons',
      content_pattern: 'avatar rows, user tags, descriptions, follow pills and viewed/following/fans states',
      key_states: ['viewed-tab', 'following-tab', 'fans-tab', 'empty-state', 'loading'],
      key_components: ['TopBar', 'TabsSection', 'UsersList', 'EmptyState'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-relationship list with softer rows, paper follow pills and clearer social hierarchy',
      component_recipe: 'relationship-list-surface',
      style_keywords: ['social-list', 'follow-pill', 'avatar-rows', 'utility-tabs'],
    },
  },
  'rn-host-my-reservation-page-component': {
    current_visual_summary: {
      layout: 'top bar with main tabs, optional sub-tabs and reservation card grid inside a scroll shell',
      chrome: 'reservation utility chrome with two-level tab hierarchy and image-led cards',
      content_pattern: 'upcoming reservation cards, online/offline sub-filters, empty-state fallback and reservation CTA buttons',
      key_states: ['new-tab', 'mine-tab', 'sub-tab-switch', 'empty-state', 'loading'],
      key_components: ['TopBar', 'SubTabsSection', 'ReservationGrid', 'EmptyState'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-reservation gallery with paper tabs, elevated booking cards and clearer CTA layering',
      component_recipe: 'reservation-gallery-surface',
      style_keywords: ['reservation-cards', 'two-level-tabs', 'cta-gallery', 'editorial-booking'],
    },
  },
  'rn-host-feedback-help-main-page-component': {
    current_visual_summary: {
      layout: 'top bar plus search field, user greeting, consult scenario cards and FAQ list',
      chrome: 'help-center chrome balancing search, cards and frequently asked question rows',
      content_pattern: 'search entry, consult cards, highlighted FAQ titles and bottom contact section',
      key_states: ['default', 'searching', 'faq-list'],
      key_components: ['TopBar', 'SearchInput', 'ConsultCards', 'FrequentList'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-help hub with paper cards, elevated search field and clearer support hierarchy',
      component_recipe: 'help-hub-surface',
      style_keywords: ['help-hub', 'search-entry', 'consult-cards', 'faq-rows'],
    },
  },
  'rn-host-question-list-page-component': {
    current_visual_summary: {
      layout: 'utility detail page showing searchable FAQ entries in a stacked question list',
      chrome: 'light support chrome with search shell and grouped rows',
      content_pattern: 'question rows, section headings and support navigation hints',
      key_states: ['default', 'searching', 'empty-result'],
      key_components: ['TopBar', 'SearchInput', 'QuestionRows'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-faq list with quieter chrome and stronger row grouping',
      component_recipe: 'faq-list-surface',
      style_keywords: ['faq-list', 'grouped-rows', 'search-shell'],
    },
  },
  'rn-host-question-detail-page-component': {
    current_visual_summary: {
      layout: 'single-column support detail page with one dominant answer body and simple top chrome',
      chrome: 'focused help-detail chrome with minimal distraction',
      content_pattern: 'question heading, answer body and support follow-up hints',
      key_states: ['default', 'long-answer'],
      key_components: ['TopBar', 'AnswerBody'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-support detail with stronger reading hierarchy and paper document body',
      component_recipe: 'support-detail-surface',
      style_keywords: ['support-detail', 'reading-body', 'paper-document'],
    },
  },
  'rn-host-recommend-book-page-component': {
    current_visual_summary: {
      layout: 'creator utility page with profile row, stat cards, service cards and task-based earning modules',
      chrome: 'growth utility chrome with section headers, sub-tabs and two-column educational or task cards',
      content_pattern: 'stat modules, service entries, task cards, activities, courses and creator action CTA',
      key_states: ['default', 'subtab-switch', 'loading'],
      key_components: ['TopBar', 'ProfileSection', 'StatsCards', 'ServiceCards', 'TaskCards'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial creator-utility surface with elevated cards, softer service rows and clearer earning hierarchy',
      component_recipe: 'creator-utility-surface',
      style_keywords: ['creator-utility', 'earning-cards', 'service-rows', 'task-grid'],
    },
  },
  'rn-host-help-support-page-component': {
    current_visual_summary: {
      layout: 'document-style page with top bar and long-form help/support body copy',
      chrome: 'minimal legal-document chrome with a single reading column',
      content_pattern: 'section titles, descriptive paragraphs and closing notes',
      key_states: ['default', 'long-document'],
      key_components: ['TopBar', 'DocumentBody'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-help document with calmer chrome and paper reading canvas',
      component_recipe: 'help-document-surface',
      style_keywords: ['document-page', 'reading-column', 'paper-body'],
    },
  },
  'rn-host-privacy-policy-page-component': {
    current_visual_summary: {
      layout: 'document-style page with top bar and long-form privacy policy copy',
      chrome: 'minimal policy chrome around a single reading column',
      content_pattern: 'section titles, paragraphs and legal closing copy',
      key_states: ['default', 'long-document'],
      key_components: ['TopBar', 'DocumentBody'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-policy document with quieter chrome and paper reading surface',
      component_recipe: 'policy-document-surface',
      style_keywords: ['policy-document', 'reading-column', 'legal-copy'],
    },
  },
  'rn-host-aiwrite-assistant-component': {
    current_visual_summary: {
      layout: 'fixed header plus scrolling chat feed with floating idea panel, suggestion pills and anchored composer',
      chrome: 'assistant chat shell with back/menu actions, quota caption and bottom composer rail',
      content_pattern: 'intro prompt cards, assistant and user bubbles, collapsible thinking block, post-response actions and idea categories',
      key_states: ['loading', 'intro', 'assistant-response', 'thinking-expanded', 'idea-selector-open', 'sending'],
      key_components: ['Header', 'IntroExample', 'ChatRow', 'ThinkingBlock', 'ActionBar', 'Suggestions', 'IdeaSelector', 'InputBar'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-ai-assistant shell with paper chat cards, quiet fixed header and floating idea chooser',
      component_recipe: 'editorial-ai-chat',
      style_keywords: ['assistant-chat', 'paper-bubbles', 'thinking-panel', 'floating-idea-panel', 'anchored-composer'],
    },
  },
  'rn-host-book-manage-page-component': {
    current_visual_summary: {
      layout: 'single-column management shell with top bar, book banner, draft card, chapter section, volume note and bottom CTA',
      chrome: 'minimal writer management chrome focused on draft recovery and chapter creation',
      content_pattern: 'book identity block, continue-draft card, chapter list or empty state, right-aligned volume label and primary footer action',
      key_states: ['loading', 'draft-exists', 'chapter-empty', 'chapter-list'],
      key_components: ['Header', 'Banner', 'DraftBar', 'ChapterSection', 'EmptyChapter', 'Footer'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-book-management shell with paper banner card, elevated draft recovery block and tokenized chapter cards',
      component_recipe: 'editorial-book-manager',
      style_keywords: ['writer-management', 'banner-card', 'draft-recovery', 'chapter-cards', 'footer-cta'],
    },
  },
  'rn-nested-bookshelf-history-page': {
    current_visual_summary: {
      layout: 'tab-led reading-history page with toggleable grid and list modes inside the bookshelf container',
      chrome: 'lightweight tabs and view controls above densely packed cover or list rows',
      content_pattern: 'history tabs, cover grid, reading progress, add-to-shelf pills and list-mode metadata rows',
      key_states: ['tab-switch', 'grid-view', 'list-view', 'refreshing'],
      key_components: ['TabBar', 'HistoryContent', 'HistoryItem', 'EditToolbar', 'RefreshIndicator'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-history shelf with paper tabs, elevated cover cards and quieter list rows',
      component_recipe: 'bookshelf-history-surface',
      style_keywords: ['history-library', 'cover-grid', 'reading-progress', 'paper-tabs'],
    },
  },
  'rn-nested-bookshelf-watchlist-page': {
    current_visual_summary: {
      layout: 'watchlist manager with top bar, edit toolbar and three-column poster grid',
      chrome: 'compact management chrome around posters, badges and selection state',
      content_pattern: 'ad banner, edit actions, tracked drama posters, progress bars, update badges and empty-state discovery CTA',
      key_states: ['default', 'edit-mode', 'selection-state', 'empty-state', 'loading-more'],
      key_components: ['TopBar', 'EditToolbar', 'WatchlistGrid', 'EmptyState'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-watchlist shelf with quiet top chrome, paper poster cards and elevated edit rail',
      component_recipe: 'bookshelf-watchlist-surface',
      style_keywords: ['watchlist-grid', 'poster-cards', 'edit-rail', 'discovery-empty-state'],
    },
  },
  'rn-nested-bookshelf-bookshelf-page': {
    current_visual_summary: {
      layout: 'bookshelf core page with tabs, view switchers, recommendation feed and mixed grid/list/waterfall layouts',
      chrome: 'bookshelf utility chrome around sorting, editing and layout-mode toggles',
      content_pattern: 'shelf tabs, recommendation shelves, empty-state guidance and dense book cards with cover-first hierarchy',
      key_states: ['tab-switch', 'grid-view', 'list-view', 'waterfall-view', 'empty-state', 'recommendation-loading'],
      key_components: ['TopBar', 'BookItem', 'EmptyState', 'RecommendationFlow', 'ViewSwitcher', 'EditToolbar'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-bookshelf surface with calmer tabs, paper book cards and refined recommendation flow',
      component_recipe: 'bookshelf-core-surface',
      style_keywords: ['shelf-grid', 'recommendation-flow', 'paper-cards', 'utility-tabs'],
    },
  },
  'rn-nested-bookshelf-community-page': {
    current_visual_summary: {
      layout: 'community feed page with top bar, category tabs, post list and floating write action',
      chrome: 'social-reading chrome with topic filters, list dividers and utility actions',
      content_pattern: 'post cards, author metadata, subscribe actions, empty state and loading indicator within the feed',
      key_states: ['tab-switch', 'feed-loading', 'empty-state', 'floating-action'],
      key_components: ['TopBar', 'TabBar', 'FilterBar', 'PostList', 'PostItem', 'EmptyState', 'FloatingButton', 'LoadingIndicator'],
    },
    target_visual_plan: {
      layout_strategy: 'editorial-community feed with paper post cards, quieter tabs and anchored floating compose affordance',
      component_recipe: 'bookshelf-community-feed',
      style_keywords: ['community-feed', 'post-cards', 'filter-tabs', 'floating-compose'],
    },
  },
};

const COMPONENT_VISUAL_OVERRIDES = {
  'src/page/Writer/AIWriteAssistant/components/ActionBar.tsx': {
    current_visual_summary: {
      structure: 'feedback bar tucked under an assistant answer with like, dislike, copy and retry icon buttons',
      affordance: 'post-response evaluation, copy and retry actions for generated content',
    },
    target_visual_plan: {
      component_recipe: 'assistant-response-actions',
      style_keywords: ['feedback-row', 'quiet-outline-actions', 'assistant-output'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/ChatRow.tsx': {
    current_visual_summary: {
      structure: 'assistant and user message bubbles with optional thinking block, markdown body and action bar',
      affordance: 'two-way conversation stream with expandable assistant reasoning and response follow-ups',
    },
    target_visual_plan: {
      component_recipe: 'editorial-chat-bubble',
      style_keywords: ['chat-bubbles', 'thinking-stack', 'assistant-thread'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/Header.tsx': {
    current_visual_summary: {
      structure: 'fixed top bar with back button, centered title/quota stack and trailing menu action',
      affordance: 'conversation navigation and assistant usage context',
    },
    target_visual_plan: {
      component_recipe: 'editorial-assistant-header',
      style_keywords: ['fixed-header', 'quota-caption', 'quiet-chrome'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/IdeaSelector.tsx': {
    current_visual_summary: {
      structure: 'floating category panel with title row, close control and wrapped genre tiles',
      affordance: 'quick genre selection for prompt injection before sending',
    },
    target_visual_plan: {
      component_recipe: 'floating-idea-picker',
      style_keywords: ['floating-panel', 'genre-grid', 'assistant-prompting'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/InputBar.tsx': {
    current_visual_summary: {
      structure: 'bottom multi-line input field with single send button in a persistent composer bar',
      affordance: 'compose and submit assistant prompts without leaving the thread',
    },
    target_visual_plan: {
      component_recipe: 'anchored-chat-composer',
      style_keywords: ['composer-bar', 'anchored-input', 'primary-send'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/IntroExample.tsx': {
    current_visual_summary: {
      structure: 'hero intro bubble with assistant greeting, three example prompt cards and refresh action',
      affordance: 'empty-state onboarding for first prompt selection',
    },
    target_visual_plan: {
      component_recipe: 'assistant-onboarding-card',
      style_keywords: ['intro-card', 'prompt-samples', 'assistant-onboarding'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/MarkdownText.tsx': {
    current_visual_summary: {
      structure: 'markdown body renderer embedded inside assistant message bubbles',
      affordance: 'formats longer assistant answers with headings, emphasis and lists',
    },
    target_visual_plan: {
      component_recipe: 'assistant-rich-text',
      style_keywords: ['markdown-body', 'reading-answer', 'rich-text'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/Suggestions.tsx': {
    current_visual_summary: {
      structure: 'two horizontally aligned suggestion pills for deep-think and idea modes above the composer',
      affordance: 'quick mode toggles before sending a prompt',
    },
    target_visual_plan: {
      component_recipe: 'assistant-mode-pills',
      style_keywords: ['suggestion-pills', 'mode-toggle', 'bottom-utility'],
    },
  },
  'src/page/Writer/AIWriteAssistant/components/ThinkingBlock.tsx': {
    current_visual_summary: {
      structure: 'collapsible reasoning block with state label, caret toggle and quoted thinking text',
      affordance: 'reveals or hides assistant reasoning trace inline with the answer',
    },
    target_visual_plan: {
      component_recipe: 'assistant-thinking-panel',
      style_keywords: ['reasoning-panel', 'collapsible-quote', 'process-visibility'],
    },
  },
  'src/page/Writer/BookManage/components/Banner.tsx': {
    current_visual_summary: {
      structure: 'book identity banner with cover placeholder, title, author and publication status',
      affordance: 'anchors the management page around one active work',
    },
    target_visual_plan: {
      component_recipe: 'book-management-banner',
      style_keywords: ['book-banner', 'identity-card', 'writer-surface'],
    },
  },
  'src/page/Writer/BookManage/components/ChapterSection.tsx': {
    current_visual_summary: {
      structure: 'chapter section heading followed by either empty placeholder space or stacked chapter cards',
      affordance: 'organizes existing chapters and signals the next authoring step',
    },
    target_visual_plan: {
      component_recipe: 'writer-chapter-stack',
      style_keywords: ['chapter-cards', 'writer-list', 'management-stack'],
    },
  },
  'src/page/Writer/BookManage/components/DraftBar.tsx': {
    current_visual_summary: {
      structure: 'single-line recovery card with interrupted-draft copy and trailing continue action',
      affordance: 'resume the last unfinished writing session',
    },
    target_visual_plan: {
      component_recipe: 'draft-recovery-card',
      style_keywords: ['draft-recovery', 'elevated-card', 'writer-cta'],
    },
  },
  'src/page/Writer/BookManage/components/EmptyChapter.tsx': {
    current_visual_summary: {
      structure: 'centered empty-state copy block shown when the book has no chapters yet',
      affordance: 'explains that chapter creation is the next action',
    },
    target_visual_plan: {
      component_recipe: 'writer-empty-state',
      style_keywords: ['empty-state', 'paper-card', 'chapter-zero'],
    },
  },
  'src/page/Writer/BookManage/components/Footer.tsx': {
    current_visual_summary: {
      structure: 'bottom action area with one primary chapter-creation button and supporting migration tip',
      affordance: 'starts new chapter creation and explains advanced editing entry point',
    },
    target_visual_plan: {
      component_recipe: 'writer-footer-cta',
      style_keywords: ['footer-cta', 'primary-button', 'supporting-note'],
    },
  },
  'src/page/Writer/BookManage/components/Header.tsx': {
    current_visual_summary: {
      structure: 'simple top bar with back affordance and page title for chapter management',
      affordance: 'returns to the previous writer surface while keeping context visible',
    },
    target_visual_plan: {
      component_recipe: 'writer-management-header',
      style_keywords: ['top-bar', 'quiet-chrome', 'writer-context'],
    },
  },
  'src/page/Writer/WritePage/components/SelectionToolbar.tsx': {
    current_visual_summary: {
      structure: 'floating editing toolbar with selection actions for rewrite, condense and continue operations',
      affordance: 'applies inline writing transforms to selected text',
    },
    target_visual_plan: {
      component_recipe: 'editor-selection-toolbar',
      style_keywords: ['floating-toolbar', 'selection-actions', 'writer-assist'],
    },
  },
  'src/page/Writer/WritePage/components/TopBar.tsx': {
    current_visual_summary: {
      structure: 'top editor bar with navigation, utility icons and publish action',
      affordance: 'global writing actions at the start of the session',
    },
    target_visual_plan: {
      component_recipe: 'editor-header',
      style_keywords: ['editor-top-bar', 'publish-cta', 'utility-actions'],
    },
  },
  'src/page/Writer/WritePage/components/VolumeBar.tsx': {
    current_visual_summary: {
      structure: 'low-chrome volume label rail positioned near the lower edge of the editor',
      affordance: 'keeps current volume context visible while writing',
    },
    target_visual_plan: {
      component_recipe: 'editor-volume-rail',
      style_keywords: ['context-rail', 'volume-label', 'editor-footer'],
    },
  },
  'src/page/Writer/WritePage/components/WelcomePanel.tsx': {
    current_visual_summary: {
      structure: 'floating shortcut panel with helper entry points and dismiss control shown over the editor',
      affordance: 'onboards writers to key creation shortcuts before they start editing',
    },
    target_visual_plan: {
      component_recipe: 'editor-welcome-panel',
      style_keywords: ['floating-shortcuts', 'editor-onboarding', 'paper-panel'],
    },
  },
  'src/page/BookshelfPage/pages/History/components/HistoryItem.tsx': {
    current_visual_summary: {
      structure: 'history card/list row showing cover, title, metadata and reading progress',
      affordance: 'surfaces recent reading entries with re-entry and add-to-shelf cues',
    },
    target_visual_plan: {
      component_recipe: 'history-book-row',
      style_keywords: ['reading-history', 'metadata-row', 'progress-chip'],
    },
  },
  'src/page/BookshelfPage/pages/History/components/TabBar.tsx': {
    current_visual_summary: {
      structure: 'horizontal tab strip with view toggles embedded on the right edge',
      affordance: 'switches history categories and view mode without leaving the page',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-history-tabs',
      style_keywords: ['tab-strip', 'view-toggle', 'bookshelf-chrome'],
    },
  },
  'src/page/BookshelfPage/pages/History/components/EditToolbar.tsx': {
    current_visual_summary: {
      structure: 'compact toolbar for batch selection and add-to-shelf actions within history',
      affordance: 'enables multi-select management of history entries',
    },
    target_visual_plan: {
      component_recipe: 'history-edit-rail',
      style_keywords: ['batch-actions', 'edit-toolbar', 'selection-mode'],
    },
  },
  'src/page/BookshelfPage/pages/History/components/RefreshIndicator.tsx': {
    current_visual_summary: {
      structure: 'lightweight refresh indicator nested inside the history scrolling area',
      affordance: 'shows pull-to-refresh progress without replacing the list shell',
    },
    target_visual_plan: {
      component_recipe: 'history-refresh-indicator',
      style_keywords: ['refresh-state', 'inline-feedback', 'bookshelf-loading'],
    },
  },
  'src/page/BookshelfPage/pages/Watchlist/components/TopBar.tsx': {
    current_visual_summary: {
      structure: 'top bar with segmented actions and centered banner slot for watchlist promotions',
      affordance: 'switches watchlist modes and frames the page context',
    },
    target_visual_plan: {
      component_recipe: 'watchlist-top-bar',
      style_keywords: ['top-bar', 'promo-slot', 'mode-actions'],
    },
  },
  'src/page/BookshelfPage/pages/Watchlist/components/EditToolbar.tsx': {
    current_visual_summary: {
      structure: 'edit rail with select-all, delete and selected-count affordances',
      affordance: 'manages multi-select cleanup flows for tracked items',
    },
    target_visual_plan: {
      component_recipe: 'watchlist-edit-rail',
      style_keywords: ['edit-toolbar', 'selection-count', 'bulk-actions'],
    },
  },
  'src/page/BookshelfPage/pages/Watchlist/components/EmptyState.tsx': {
    current_visual_summary: {
      structure: 'empty-state block encouraging users to discover more dramas when the watchlist is blank',
      affordance: 'explains zero-state and redirects to acquisition flow',
    },
    target_visual_plan: {
      component_recipe: 'watchlist-empty-state',
      style_keywords: ['empty-state', 'discover-cta', 'zero-watchlist'],
    },
  },
  'src/page/BookshelfPage/pages/Watchlist/components/WatchlistGrid.tsx': {
    current_visual_summary: {
      structure: 'three-column poster grid with selection outline, progress bar and update badge overlays',
      affordance: 'browses and batch-manages tracked items in poster form',
    },
    target_visual_plan: {
      component_recipe: 'watchlist-poster-grid',
      style_keywords: ['poster-grid', 'selection-outline', 'progress-overlay'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/BookItem.tsx': {
    current_visual_summary: {
      structure: 'cover-first shelf card or row with title, author and progress metadata',
      affordance: 'opens shelf entries while preserving reading progress context',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-book-card',
      style_keywords: ['cover-card', 'reading-progress', 'shelf-item'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/EditToolbar.tsx': {
    current_visual_summary: {
      structure: 'batch-edit toolbar with selection count, select-all, move and delete controls',
      affordance: 'manages bookshelf entries in edit mode without leaving the current tab',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-edit-toolbar',
      style_keywords: ['batch-actions', 'selection-count', 'danger-cta'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/EmptyState.tsx': {
    current_visual_summary: {
      structure: 'bookshelf zero-state block with type-specific iconography and descriptive copy',
      affordance: 'guides users when shelf or recommendation content is empty',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-empty-state',
      style_keywords: ['empty-state', 'discovery-guidance', 'paper-zero-state'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/GridView.tsx': {
    current_visual_summary: {
      structure: 'three-column shelf grid with footer loading state and empty-state branch',
      affordance: 'browses dense shelf content with fixed card rhythm',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-grid-shell',
      style_keywords: ['three-column-grid', 'shelf-footer', 'dense-browse'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/ListView.tsx': {
    current_visual_summary: {
      structure: 'single-column shelf list with separators, footer loading block and empty-state branch',
      affordance: 'shows fuller metadata for each shelf item in list density',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-list-shell',
      style_keywords: ['list-density', 'metadata-rows', 'shelf-list'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/RecommendationFlow.tsx': {
    current_visual_summary: {
      structure: 'continuous recommendation rail embedded below shelf controls with lazy loading and mixed card sizes',
      affordance: 'extends the shelf into discovery without leaving the tab',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-recommendation-flow',
      style_keywords: ['recommendation-feed', 'mixed-cards', 'discovery-rail'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/TopBar.tsx': {
    current_visual_summary: {
      structure: 'utility top bar combining tabs, edit entry and layout toggles',
      affordance: 'switches shelf mode, sort and editing state from one shared chrome line',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-top-bar',
      style_keywords: ['utility-top-bar', 'layout-toggle', 'shelf-tabs'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/UnifiedScrollView.tsx': {
    current_visual_summary: {
      structure: 'composite scroll container that stitches shelf content, refresh control and recommendation section',
      affordance: 'keeps shelf browsing and discovery in one continuous scroll context',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-scroll-shell',
      style_keywords: ['scroll-shell', 'refresh-state', 'composite-feed'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/ViewSwitcher.tsx': {
    current_visual_summary: {
      structure: 'compact set of buttons for grid, list and alternate bookshelf layouts',
      affordance: 'changes layout density without leaving the current tab',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-view-switcher',
      style_keywords: ['layout-toggle', 'density-switch', 'utility-controls'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/WaterfallGrid.tsx': {
    current_visual_summary: {
      structure: 'two-column waterfall shelf container with uneven card heights and infinite-load footer',
      affordance: 'browses shelf items in a more editorial, staggered rhythm',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-waterfall-shell',
      style_keywords: ['waterfall-grid', 'staggered-cards', 'editorial-density'],
    },
  },
  'src/page/BookshelfPage/pages/Bookshelf/components/LoadMoreIndicator.tsx': {
    current_visual_summary: {
      structure: 'inline loading and retry feedback block rendered at the tail of shelf and recommendation lists',
      affordance: 'communicates pagination progress and retry affordance without leaving the current feed',
    },
    target_visual_plan: {
      component_recipe: 'bookshelf-load-state',
      style_keywords: ['load-more', 'retry-state', 'inline-feedback'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/EmptyState.tsx': {
    current_visual_summary: {
      structure: 'community empty-state block with illustration-like icon, copy and call-to-action',
      affordance: 'explains why no posts are visible and suggests the next action',
    },
    target_visual_plan: {
      component_recipe: 'community-empty-state',
      style_keywords: ['empty-state', 'social-zero-state', 'guided-cta'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/FilterBar.tsx': {
    current_visual_summary: {
      structure: 'horizontal filter row with selectable topics and lightweight action affordances',
      affordance: 'narrows the feed by category without leaving the list context',
    },
    target_visual_plan: {
      component_recipe: 'community-filter-row',
      style_keywords: ['filter-row', 'topic-pills', 'feed-controls'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/FloatingButton.tsx': {
    current_visual_summary: {
      structure: 'persistent floating compose button positioned over the lower edge of the community feed',
      affordance: 'creates new community content from any scroll position',
    },
    target_visual_plan: {
      component_recipe: 'community-floating-compose',
      style_keywords: ['floating-button', 'compose-cta', 'anchored-action'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/LoadingIndicator.tsx': {
    current_visual_summary: {
      structure: 'centered loading indicator used inside feed and empty slots',
      affordance: 'communicates asynchronous fetch progress without leaving the community shell',
    },
    target_visual_plan: {
      component_recipe: 'community-loading-indicator',
      style_keywords: ['loading-state', 'feed-spinner', 'inline-feedback'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/PostItem.tsx': {
    current_visual_summary: {
      structure: 'paper-like post card with author meta, title, body excerpt and action rows',
      affordance: 'reads and interacts with community posts inside the bookshelf context',
    },
    target_visual_plan: {
      component_recipe: 'community-post-card',
      style_keywords: ['post-card', 'author-meta', 'social-reading'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/PostList.tsx': {
    current_visual_summary: {
      structure: 'feed list shell responsible for refresh, empty state, loading fallback and infinite paging',
      affordance: 'orchestrates the community feed states around post cards and tabs',
    },
    target_visual_plan: {
      component_recipe: 'community-feed-shell',
      style_keywords: ['feed-shell', 'refresh-state', 'infinite-list'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/TabBar.tsx': {
    current_visual_summary: {
      structure: 'horizontal topic tab strip above the post feed',
      affordance: 'switches community feed categories in place',
    },
    target_visual_plan: {
      component_recipe: 'community-tab-bar',
      style_keywords: ['topic-tabs', 'feed-navigation', 'quiet-chrome'],
    },
  },
  'src/page/BookshelfPage/pages/Community/components/TopBar.tsx': {
    current_visual_summary: {
      structure: 'top bar with page title and trailing utility actions for the community feed',
      affordance: 'anchors the feed context and exposes global actions',
    },
    target_visual_plan: {
      component_recipe: 'community-top-bar',
      style_keywords: ['top-bar', 'feed-title', 'utility-actions'],
    },
  },
};

const buildSurfaceViewport = (surface) => ({
  device_frame: surface.platform === 'android' ? 'android-phone' : 'rn-phone',
  scroll_direction: surface.surface_id.includes('reader') ? 'mixed' : 'vertical',
  immersive: surface.surface_id.includes('reader'),
  fixed_header: !surface.surface_id.includes('overlay'),
  fixed_footer: /write|assistant|book-manage|main-page|reader/.test(surface.surface_id),
});

const buildSurfaceFrameAnatomy = (surface) => {
  const anatomy = [];

  if (!surface.surface_id.includes('overlay')) {
    anatomy.push('top-bar');
  }
  anatomy.push('primary-content-rail');
  if (/comment|review|community|bookshelf/.test(surface.surface_id)) {
    anatomy.push('secondary-filter-rail');
  }
  if (/assistant|write|book-manage/.test(surface.surface_id)) {
    anatomy.push('anchored-action-rail');
  }
  if (surface.surface_id.includes('overlay')) {
    anatomy.push('overlay-container');
  }
  if (/main-page|reader|react-native-page/.test(surface.surface_id)) {
    anatomy.push('host-or-overlay-layer');
  }

  return anatomy;
};

const buildSurfacePrimaryBlocks = (surface, summary) => {
  const leadBlock = /assistant|write/.test(surface.surface_id)
    ? 'editorial-workbench'
    : /comment|review/.test(surface.surface_id)
      ? 'review-thread'
      : /community/.test(surface.surface_id)
        ? 'community-feed'
        : /bookshelf|history|watchlist/.test(surface.surface_id)
          ? 'library-content'
          : /reader/.test(surface.surface_id)
            ? 'immersive-canvas'
            : 'stacked-content';

  return [
    {
      order: 1,
      block: leadBlock,
      width_model: 'single-column',
      cardization: /assistant|community|comment|review|book/.test(surface.surface_id)
        ? 'paper-cards'
        : 'mixed',
    },
    {
      order: 2,
      block: summary.content_pattern,
      width_model: /main-page|home|search/.test(surface.surface_id) ? 'modular-sections' : 'single-column',
      cardization: 'supporting-sections',
    },
  ];
};

const buildSurfaceStatePanels = (surface, keyStates) =>
  keyStates.map((state) => ({
    state,
    placement:
      /loading|empty|error/.test(state)
        ? 'inline-content'
        : /modal|sheet|panel/.test(state)
          ? 'overlay'
          : 'primary-flow',
    treatment:
      state === 'loading'
        ? 'structured-feedback'
        : state === 'empty'
          ? 'guided-zero-state'
          : /edit|selection/.test(state)
            ? 'selection-rail'
            : 'state-aware-section',
  }));

const buildSurfaceAssetProfile = (surface) => {
  const iconSources = surface.asset_sources.filter((source) =>
    /icon|svg|vector/i.test(source),
  );
  const imageSources = surface.asset_sources.filter((source) =>
    /image/i.test(source),
  );

  return {
    icon_sources: iconSources.length ? iconSources : ['theme-aware'],
    image_sources: imageSources,
    illustration_sources: surface.surface_id.includes('empty') ? ['undraw-sync-target'] : [],
    credit_overlay: /community|bookshelf|comment|review/.test(surface.surface_id)
      ? 'required-when-pexels'
      : 'not-primary',
  };
};

const buildSurfaceInteractionChrome = (surface) => {
  const entries = [];

  if (/bookshelf|community|history|watchlist|comment/.test(surface.surface_id)) {
    entries.push('tabs-or-filter-rail');
  }
  if (/assistant|write|book-manage/.test(surface.surface_id)) {
    entries.push('primary-action-toolbar');
  }
  if (/overlay|panel|sheet/.test(surface.surface_id)) {
    entries.push('sheet-handle-or-panel-header');
  }
  if (/main-page/.test(surface.surface_id)) {
    entries.push('bottom-navigation');
  }
  if (/reader/.test(surface.surface_id)) {
    entries.push('immersive-controls');
  }

  return entries.length ? entries : ['quiet-page-chrome'];
};

const buildSurfaceLayoutBlueprint = (surface, plan) => ({
  primary_flow: plan.target.component_recipe,
  entry_chrome: surface.host_type,
  content_axis: surface.surface_id.includes('reader') ? 'immersive-canvas' : 'stacked-sections',
  supporting_flow:
    /assistant|write/.test(surface.surface_id)
      ? 'composer-and-tooling'
      : /community|comment|review/.test(surface.surface_id)
        ? 'thread-and-filter'
        : 'supporting-metadata',
});

const buildSurfaceSectionRecipes = (surface, plan) =>
  unique([
    'page-shell',
    surface.host_type,
    plan.target.component_recipe,
    ...surface.key_components.slice(0, 4).map((component) => slugify(component)),
  ]);

const buildSurfaceVisualSpecs = (surfaces) =>
  surfaces.map((surface) => {
    const cluster = determineSurfaceCluster(surface);
    const plan = SURFACE_CLUSTER_PLANS[cluster] || SURFACE_CLUSTER_PLANS.utility-detail;
    const override = SURFACE_VISUAL_OVERRIDES[surface.surface_id] || {};
    const figmaAuditPage = surface.host_type === 'rn-root'
      ? '00-现状审计/RN Root'
      : surface.host_type === 'rn-host'
        ? '00-现状审计/RN Host Pages'
        : surface.host_type === 'rn-nested'
          ? '00-现状审计/RN Nested Pages'
          : surface.host_type === 'android-native'
            ? '00-现状审计/Android Native Pages'
            : '00-现状审计/Android Shell & Overlays';

    return {
      surface_id: surface.surface_id,
      cluster,
      platform: surface.platform,
      source_paths: surface.source_paths,
      current_visual_summary: {
        layout: override.current_visual_summary?.layout || plan.current.layout,
        chrome: override.current_visual_summary?.chrome || plan.current.chrome,
        content_pattern: override.current_visual_summary?.content_pattern || plan.current.content_pattern,
        key_states: override.current_visual_summary?.key_states || surface.key_states,
        key_components: override.current_visual_summary?.key_components || surface.key_components,
        viewport: buildSurfaceViewport(surface),
        frame_anatomy: buildSurfaceFrameAnatomy(surface),
        primary_blocks: buildSurfacePrimaryBlocks(surface, {
          content_pattern: override.current_visual_summary?.content_pattern || plan.current.content_pattern,
        }),
        visual_density: {
          density: /reader|comment|review|community/.test(surface.surface_id) ? 'dense-reading' : 'balanced',
          whitespace: /assistant|write|book-manage/.test(surface.surface_id) ? 'expanded' : 'moderate',
          crowded_points: (override.current_visual_summary?.key_components || surface.key_components).slice(0, 3),
        },
        state_panels: buildSurfaceStatePanels(
          surface,
          override.current_visual_summary?.key_states || surface.key_states,
        ),
        asset_profile: buildSurfaceAssetProfile(surface),
        interaction_chrome: buildSurfaceInteractionChrome(surface),
      },
      target_visual_plan: {
        direction: 'literary-editorial',
        layout_strategy: override.target_visual_plan?.layout_strategy || plan.target.layout_strategy,
        component_recipe: override.target_visual_plan?.component_recipe || plan.target.component_recipe,
        style_keywords: override.target_visual_plan?.style_keywords || plan.target.style_keywords,
        token_priority: [
          'color.bg.canvas',
          'color.text.primary',
          'color.brand.primary',
          'space.200',
          'radius.lg',
          'typography.title.section',
        ],
        layout_blueprint: buildSurfaceLayoutBlueprint(surface, plan),
        section_recipes: buildSurfaceSectionRecipes(surface, plan),
        spacing_rhythm: {
          page_gutter: 'space.200',
          section_gap: /assistant|write|book-manage/.test(surface.surface_id) ? 'space.300' : 'space.200',
          card_padding: 'space.200',
        },
        shape_language: {
          cards: /reader/.test(surface.surface_id) ? 'immersive-panels' : 'paper-cards',
          actions: 'rounded-pill-actions',
          overlays: /overlay|panel|sheet/.test(surface.surface_id) ? 'raised-panels' : 'integrated-overlays',
        },
        typography_roles: {
          title: 'typography.title.section',
          body: /reader/.test(surface.surface_id) ? 'typography.reader.content' : 'typography.body.md',
          meta: 'typography.meta.sm',
          eyebrow: 'typography.eyebrow.md',
        },
        motion_notes: {
          page_enter: 'motion.duration.page + motion.curve.standard',
          section_shift: 'motion.duration.fast + motion.curve.decelerate',
          overlay: 'motion.duration.sheet + motion.curve.entrance',
        },
        dark_a11y_rtl: {
          dark_mode: 'independent-token-pairing',
          contrast: '>=4.5:1 on key text',
          a11y: 'semantic-roles-and-min-44px-targets',
          rtl: /reader|comment|review/.test(surface.surface_id)
            ? 'mirror-chrome-keep-content-flow'
            : 'mirror-navigation-and-trailing-icons',
        },
        figma_targets: {
          audit_page: figmaAuditPage,
          design_page_light: '03-页面-亮色',
          design_page_dark: '04-页面-暗色',
        },
      },
      current_look_recorded: true,
      target_look_planned: true,
      implementation_progress: {
        shell_reskinned: RESKINNED_SURFACES.has(surface.surface_id),
        novel_design_ready: RESKINNED_SURFACES.has(surface.surface_id),
      },
    };
  });

const buildComponentCatalog = (repoRoot) => {
  const rnComponentFiles = walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) =>
      /\.(ts|tsx)$/.test(filePath) &&
      filePath.includes(`${path.sep}components${path.sep}`),
  );

  const rnEntries = rnComponentFiles
    .map((filePath) => {
      const relativePath = relativeRepoPath(repoRoot, filePath);
      const source = fs.readFileSync(filePath, 'utf8');
      return {
        path: relativePath,
        name: componentNameFromFile(filePath),
        platform: 'react-native',
        category: inferComponentCategory(relativePath),
        asset_sources: detectAssetSources(source),
      };
    });
  const androidEntries = collectAndroidComponentEntries(repoRoot);
  const entries = [...rnEntries, ...androidEntries]
    .sort((left, right) => left.path.localeCompare(right.path));

  const summary = entries.reduce(
    (acc, entry) => {
      if (entry.platform === 'react-native') {
        acc.rn_component_count += 1;
      } else if (entry.platform === 'android') {
        acc.android_component_count += 1;
      }
      acc.category_counts[entry.category] = (acc.category_counts[entry.category] || 0) + 1;
      return acc;
    },
    { rn_component_count: 0, android_component_count: 0, category_counts: {} },
  );

  return { summary, entries };
};

const COMPONENT_CATEGORY_PLANS = {
  navigation: {
    current: {
      structure: 'top bars, headers, nav rows and tab controls with direct icon/text actions',
      affordance: 'utility-first navigation cues with minimal hierarchy depth',
    },
    target: {
      component_recipe: 'editorial-toolbar',
      style_keywords: ['navigation', 'quiet-chrome', 'refined-actions'],
    },
  },
  dialog: {
    current: {
      structure: 'modal cards and overlays with stacked actions',
      affordance: 'interruptive confirmation and guided prompts',
    },
    target: {
      component_recipe: 'editorial-dialog',
      style_keywords: ['dialog', 'paper-surface', 'clear-actions'],
    },
  },
  sheet: {
    current: {
      structure: 'sheet-based temporary surfaces with drag/close affordances',
      affordance: 'contextual detail and action overflow',
    },
    target: {
      component_recipe: 'editorial-bottom-sheet',
      style_keywords: ['sheet', 'contextual', 'soft-elevation'],
    },
  },
  loading: {
    current: {
      structure: 'spinner, indicator or skeleton placeholders',
      affordance: 'state feedback during async work',
    },
    target: {
      component_recipe: 'editorial-loading',
      style_keywords: ['loading', 'warm-shimmer', 'structured-placeholder'],
    },
  },
  empty: {
    current: {
      structure: 'simple icon plus message empty states',
      affordance: 'fallback state explanation',
    },
    target: {
      component_recipe: 'editorial-empty-state',
      style_keywords: ['empty', 'supportive-copy', 'illustration-slot'],
    },
  },
  list: {
    current: {
      structure: 'list, grid and waterfall containers with dense item repetition',
      affordance: 'content browsing and infinite progression',
    },
    target: {
      component_recipe: 'editorial-list-shell',
      style_keywords: ['list', 'modular-cards', 'quiet-density'],
    },
  },
  item: {
    current: {
      structure: 'card, row and tile elements combining cover, text and minor actions',
      affordance: 'quick metadata scan and selection',
    },
    target: {
      component_recipe: 'editorial-card',
      style_keywords: ['card', 'paper', 'metadata-hierarchy'],
    },
  },
  form: {
    current: {
      structure: 'input, search, picker and switch controls with direct theme styling',
      affordance: 'task completion and configuration',
    },
    target: {
      component_recipe: 'editorial-form-control',
      style_keywords: ['form', 'inset-field', 'warm-borders'],
    },
  },
  action: {
    current: {
      structure: 'button, toolbar and action strips with utilitarian emphasis',
      affordance: 'high-visibility trigger surface',
    },
    target: {
      component_recipe: 'editorial-action-control',
      style_keywords: ['actions', 'pill-controls', 'brand-accent'],
    },
  },
  media: {
    current: {
      structure: 'icon, avatar and image wrappers using mixed legacy asset sources',
      affordance: 'visual signposting and cover rendering',
    },
    target: {
      component_recipe: 'asset-governed-media',
      style_keywords: ['media', 'semantic-icons', 'credit-aware'],
    },
  },
  showcase: {
    current: {
      structure: 'showcase or catalog surface presenting foundation and component samples',
      affordance: 'design-system browsing and review',
    },
    target: {
      component_recipe: 'showcase-gallery',
      style_keywords: ['showcase', 'catalog', 'review-surface'],
    },
  },
  layout: {
    current: {
      structure: 'wrapper and grouping components with low visual identity',
      affordance: 'layout scaffolding and spacing control',
    },
    target: {
      component_recipe: 'editorial-layout-shell',
      style_keywords: ['layout', 'tokenized-spacing', 'grouped-surfaces'],
    },
  },
};

const buildComponentAnatomy = (entry) => {
  if (entry.category === 'navigation') {
    return ['leading-action', 'title-slot', 'trailing-actions'];
  }
  if (entry.category === 'action') {
    return ['icon-slot', 'label-slot', 'interaction-container'];
  }
  if (entry.category === 'form') {
    return ['field-shell', 'input-slot', 'supporting-meta'];
  }
  if (entry.category === 'item') {
    return ['media-slot', 'text-stack', 'meta-row', 'action-row'];
  }
  if (entry.category === 'list') {
    return ['list-shell', 'state-slot', 'item-stack'];
  }
  return ['container', 'content-slot'];
};

const buildComponentSizeRules = (entry) => ({
  min_touch_target: entry.category === 'layout' ? 'contextual' : '44x44',
  default_height:
    entry.category === 'navigation'
      ? '56'
      : entry.category === 'action'
        ? '36-44'
        : entry.category === 'form'
          ? '44-52'
          : 'content-driven',
  icon_size: /media|navigation|action/.test(entry.category) ? '16-24' : 'contextual',
  media_ratio: entry.category === 'item' ? 'cover-first-adaptive' : 'n/a',
});

const buildComponentTargetSlots = (entry) =>
  unique([
    ...buildComponentAnatomy(entry),
    entry.category === 'action' ? 'action-pill' : 'surface-shell',
  ]);

const buildComponentVisualSpecs = (catalog) =>
  catalog.entries.map((entry) => {
    const plan = COMPONENT_CATEGORY_PLANS[entry.category] || COMPONENT_CATEGORY_PLANS.layout;
    const override = COMPONENT_VISUAL_OVERRIDES[entry.path] || {};

    return {
      path: entry.path,
      name: entry.name,
      category: entry.category,
      platform: entry.platform,
      current_visual_summary: {
        structure: override.current_visual_summary?.structure || plan.current.structure,
        affordance: override.current_visual_summary?.affordance || plan.current.affordance,
        asset_sources: override.current_visual_summary?.asset_sources || entry.asset_sources,
        anatomy: buildComponentAnatomy(entry),
        size_rules: buildComponentSizeRules(entry),
        text_hierarchy: {
          title: /navigation|item|list/.test(entry.category) ? 'title + supporting-meta' : 'label-only',
          truncation: entry.category === 'item' ? 'two-line-primary' : 'single-line-where-needed',
          line_height: entry.category === 'form' ? 'comfortable-input' : 'reading-first',
        },
        container_style: {
          background: entry.category === 'loading' ? 'transparent-or-elevated' : 'paper-surface',
          border: /action|form|item|navigation/.test(entry.category) ? 'subtle-outline' : 'contextual',
          radius: /sheet|dialog/.test(entry.category) ? 'radius.xl' : 'radius.md',
          elevation: /dialog|sheet|showcase/.test(entry.category) ? 'elevation.200' : 'surface-dependent',
          divider: entry.category === 'list' ? 'quiet-divider' : 'optional',
        },
        interaction_states: [
          'default',
          'pressed',
          ...(entry.category === 'action' || entry.category === 'form' ? ['disabled', 'focused'] : []),
          ...(entry.category === 'item' ? ['selected'] : []),
        ],
      },
      target_visual_plan: {
        direction: 'literary-editorial',
        component_recipe: override.target_visual_plan?.component_recipe || plan.target.component_recipe,
        style_keywords: override.target_visual_plan?.style_keywords || plan.target.style_keywords,
        figma_target_page: '02-组件规范',
      },
      current_look_recorded: true,
      target_look_planned: true,
      implementation_progress: {
        novel_design_ready: NOVEL_DESIGN_READY_COMPONENTS.has(entry.path),
      },
    };
  });

const enrichComponentVisualSpecs = (componentVisualSpecs) =>
  componentVisualSpecs.map((entry) => {
    const category = entry.category;
    return {
      ...entry,
      target_visual_plan: {
        ...entry.target_visual_plan,
        slot_structure: buildComponentTargetSlots(entry),
        recipe_binding: entry.target_visual_plan.component_recipe,
        state_matrix: [
          'default',
          ...(category === 'action' ? ['pressed', 'disabled'] : []),
          ...(category === 'item' ? ['selected', 'loading'] : []),
          ...(category === 'form' ? ['focused', 'error'] : []),
        ],
        token_binding: [
          'color.bg.surface',
          'color.text.primary',
          'color.text.secondary',
          'color.border.subtle',
          'space.100',
          'space.150',
          'space.200',
          'radius.md',
          ...(category === 'action' ? ['color.brand.primary', 'color.interaction.selected'] : []),
        ],
        platform_adaptation: [
          entry.platform === 'react-native' ? 'rn-flex-layout + style-sheet' : 'compose/xml-mapping',
          category === 'navigation' ? 'rtl-mirror-trailing-actions' : 'shared-slot-order',
        ],
      },
    };
  });

const buildAssetInventory = (repoRoot) => {
  const svgAssets = walkFiles(
    path.join(repoRoot, 'assets', 'image'),
    (filePath) => filePath.endsWith('.svg'),
  );
  const fontAssets = walkFiles(
    path.join(repoRoot, 'assets', 'fonts'),
    () => true,
  );
  const rnFiles = walkFiles(
    path.join(repoRoot, 'src'),
    (filePath) => /\.(ts|tsx|js|jsx)$/.test(filePath),
  );
  const families = new Set();

  rnFiles.forEach((filePath) => {
    const source = fs.readFileSync(filePath, 'utf8');
    const pattern = /react-native-vector-icons\/([A-Za-z0-9]+)/g;
    let match;
    while ((match = pattern.exec(source)) !== null) {
      families.add(match[1]);
    }
  });

  return {
    summary: {
      local_svg_count: svgAssets.length,
      font_asset_count: fontAssets.length,
    },
    local_svg_assets: svgAssets.map((filePath) => relativeRepoPath(repoRoot, filePath)).sort(),
    font_assets: fontAssets.map((filePath) => relativeRepoPath(repoRoot, filePath)).sort(),
    react_native_vector_icon_families: [...families].sort(),
  };
};

const parseRegisteredComponentNames = (repoRoot) => {
  const registryPath = path.join(repoRoot, 'src', 'utils', 'runtime', 'componentRegistry.ts');
  const source = fs.readFileSync(registryPath, 'utf8');
  return [...source.matchAll(/'([A-Za-z0-9]+Component)'/g)].map((match) => match[1]).sort();
};

const parseDiscoveredComponentNames = (repoRoot) =>
  walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) => filePath.endsWith('Component.tsx'),
  )
    .map((filePath) => fs.readFileSync(filePath, 'utf8').match(/AppRegistry\.registerComponent\('([^']+)'/)?.[1])
    .filter(Boolean)
    .sort();

const buildFigmaFrameMap = (surfaces) =>
  surfaces.map((surface) => {
    let figmaPage = '00-现状审计/Android Shell & Overlays';

    if (surface.host_type === 'rn-root') {
      figmaPage = '00-现状审计/RN Root';
    } else if (surface.host_type === 'rn-host') {
      figmaPage = '00-现状审计/RN Host Pages';
    } else if (surface.host_type === 'rn-nested') {
      figmaPage = '00-现状审计/RN Nested Pages';
    } else if (surface.host_type === 'android-native') {
      figmaPage = '00-现状审计/Android Native Pages';
    }

    return {
      surface_id: surface.surface_id,
      figma_page: figmaPage,
      figma_frame_name: surface.surface_id,
      figma_frame_id: '',
      mapping_status: 'unmapped',
    };
  });

const enrichFigmaFrameMap = (figmaFrameMap) =>
  figmaFrameMap.map((entry) => ({
    ...entry,
    source_kind: 'surface',
    frame_type: entry.surface_id.includes('overlay')
      ? 'overlay-audit-frame'
      : entry.surface_id.includes('root')
        ? 'root-audit-frame'
        : 'surface-audit-frame',
    sync_status: 'pending-figma-sync',
    audit_frame_name: entry.surface_id,
    target_frame_name_light: `${entry.surface_id}/light`,
    target_frame_name_dark: `${entry.surface_id}/dark`,
    annotation_frame_name: `${entry.surface_id}/annotation`,
  }));

const buildFigmaSyncQueue = (surfaceVisualSpecs, componentVisualSpecs) => [
  ...surfaceVisualSpecs.map((entry) => ({
    source_id: entry.surface_id,
    source_kind: 'surface',
    sync_status: 'pending-figma-sync',
    target_pages: [
      entry.target_visual_plan.figma_targets.audit_page,
      entry.target_visual_plan.figma_targets.design_page_light,
      entry.target_visual_plan.figma_targets.design_page_dark,
      '05-标注与交付',
    ],
  })),
  ...componentVisualSpecs.map((entry) => ({
    source_id: entry.path,
    source_kind: 'component',
    sync_status: 'pending-figma-sync',
    target_pages: [entry.target_visual_plan.figma_target_page, '05-标注与交付'],
  })),
];

const parseRNSmokeTests = (repoRoot) =>
  walkFiles(
    path.join(repoRoot, '__tests__', 'smoke'),
    (filePath) => filePath.endsWith('.smoke.test.tsx'),
  )
    .map((filePath) => path.basename(filePath))
    .sort();

const parseSmokeCatalogMentions = (repoRoot) => {
  const source = readText(repoRoot, 'docs/refactor/phase-2/smoke-suite-catalog.md');
  return unique(
    [
      ...source.matchAll(/([A-Za-z0-9]+\.smoke\.test\.tsx)/g),
      ...source.matchAll(/([A-Za-z0-9]+SmokeTest\.kt)/g),
    ].map((match) => match[1]),
  ).sort();
};

const buildGovernanceDriftReport = (repoRoot, surfaces, figmaFrameMap, componentVisualSpecs) => {
  const registryNames = parseRegisteredComponentNames(repoRoot);
  const discoveredNames = parseDiscoveredComponentNames(repoRoot);
  const missingFromRegistry = discoveredNames.filter((name) => !registryNames.includes(name));
  const extraInRegistry = registryNames.filter((name) => !discoveredNames.includes(name));
  const rnSmokeTests = parseRNSmokeTests(repoRoot);
  const smokeCatalogMentions = parseSmokeCatalogMentions(repoRoot);
  const missingFromCatalog = rnSmokeTests.filter((name) => !smokeCatalogMentions.includes(name));

  return [
    '# Novel Design Governance Drift Report',
    '',
    '## Summary',
    `- Surface count: ${surfaces.length}`,
    `- Registry drift: ${missingFromRegistry.length === 0 && extraInRegistry.length === 0 ? 'none' : 'present'}`,
    `- RN smoke tests: ${rnSmokeTests.length}`,
    `- Missing smoke catalog entries: ${missingFromCatalog.length}`,
    `- Unmapped figma frames: ${figmaFrameMap.filter((item) => !item.figma_frame_id).length}`,
    `- Surface visual specs coverage: ${surfaces.length}/${surfaces.length}`,
    `- Component visual specs coverage: ${componentVisualSpecs.length}/${componentVisualSpecs.length}`,
    '- Detailed surface fields: viewport, frame_anatomy, primary_blocks, visual_density, state_panels, asset_profile, interaction_chrome',
    '- Detailed component fields: anatomy, size_rules, text_hierarchy, container_style, interaction_states, slot_structure, token_binding, platform_adaptation',
    '',
    '## Registry drift',
    `- Missing from registry: ${missingFromRegistry.length ? missingFromRegistry.join(', ') : 'none'}`,
    `- Extra in registry: ${extraInRegistry.length ? extraInRegistry.join(', ') : 'none'}`,
    '',
    '## RN smoke tests',
    ...rnSmokeTests.map((name) => `- ${name}`),
    '',
    '## Smoke catalog drift',
    `- Catalog mentions: ${smokeCatalogMentions.length ? smokeCatalogMentions.join(', ') : 'none'}`,
    `- Missing from catalog: ${missingFromCatalog.length ? missingFromCatalog.join(', ') : 'none'}`,
    '',
    '## Figma mapping status',
    `- Unmapped surfaces: ${figmaFrameMap.filter((item) => !item.figma_frame_id).length}`,
    '',
  ].join('\n');
};

const buildVisualPlanningSummary = (surfaceVisualSpecs, componentVisualSpecs) => {
  const componentCategoryCounts = componentVisualSpecs.reduce((acc, entry) => {
    acc[entry.category] = (acc[entry.category] || 0) + 1;
    return acc;
  }, {});

  const surfaceClusterCounts = surfaceVisualSpecs.reduce((acc, entry) => {
    acc[entry.cluster] = (acc[entry.cluster] || 0) + 1;
    return acc;
  }, {});

  return [
    '# Novel Design Visual Planning Summary',
    '',
    '## Surface visual specs',
    `- Current look recorded: ${surfaceVisualSpecs.filter((entry) => entry.current_look_recorded).length}`,
    `- Target look planned: ${surfaceVisualSpecs.filter((entry) => entry.target_look_planned).length}`,
    `- Shell reskinned: ${surfaceVisualSpecs.filter((entry) => entry.implementation_progress?.shell_reskinned).length}`,
    '- Detailed current fields: viewport/frame_anatomy/primary_blocks/visual_density/state_panels/asset_profile/interaction_chrome',
    '- Detailed target fields: layout_blueprint/section_recipes/spacing_rhythm/shape_language/typography_roles/motion_notes/dark_a11y_rtl',
    ...Object.entries(surfaceClusterCounts).map(([cluster, count]) => `- ${cluster}: ${count}`),
    '',
    '## Component visual specs',
    `- Current look recorded: ${componentVisualSpecs.filter((entry) => entry.current_look_recorded).length}`,
    `- Target look planned: ${componentVisualSpecs.filter((entry) => entry.target_look_planned).length}`,
    `- Novel design ready: ${componentVisualSpecs.filter((entry) => entry.implementation_progress?.novel_design_ready).length}`,
    ...Object.entries(componentCategoryCounts).map(([category, count]) => `- ${category}: ${count}`),
    '',
  ].join('\n');
};

const resolveOutputDir = (repoRoot, outputDir) =>
  path.isAbsolute(outputDir)
    ? outputDir
    : path.join(repoRoot, outputDir);

const generateAuditArtifacts = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputDir = DEFAULT_OUTPUT_DIR,
} = {}) => {
  const resolvedOutputDir = resolveOutputDir(repoRoot, outputDir);
  ensureDir(resolvedOutputDir);

  const surfaceInventory = buildSurfaceInventory(repoRoot);
  const componentCatalog = buildComponentCatalog(repoRoot);
  const assetInventory = buildAssetInventory(repoRoot);
  const figmaFrameMap = enrichFigmaFrameMap(buildFigmaFrameMap(surfaceInventory));
  const surfaceVisualSpecs = buildSurfaceVisualSpecs(surfaceInventory);
  const componentVisualSpecs = enrichComponentVisualSpecs(buildComponentVisualSpecs(componentCatalog));
  const figmaSyncQueue = buildFigmaSyncQueue(surfaceVisualSpecs, componentVisualSpecs);
  const governanceDriftReport = buildGovernanceDriftReport(
    repoRoot,
    surfaceInventory,
    figmaFrameMap,
    componentVisualSpecs,
  );
  const visualPlanningSummary = buildVisualPlanningSummary(
    surfaceVisualSpecs,
    componentVisualSpecs,
  );

  const outputs = {
    surfaceInventoryPath: path.join(resolvedOutputDir, 'surface-inventory.json'),
    componentCatalogPath: path.join(resolvedOutputDir, 'component-catalog.json'),
    surfaceVisualSpecsPath: path.join(resolvedOutputDir, 'surface-visual-specs.json'),
    componentVisualSpecsPath: path.join(resolvedOutputDir, 'component-visual-specs.json'),
    assetInventoryPath: path.join(resolvedOutputDir, 'asset-inventory.json'),
    figmaFrameMapPath: path.join(resolvedOutputDir, 'figma-frame-map.json'),
    figmaSyncQueuePath: path.join(resolvedOutputDir, 'figma-sync-queue.json'),
    governanceDriftReportPath: path.join(resolvedOutputDir, 'governance-drift-report.md'),
    visualPlanningSummaryPath: path.join(resolvedOutputDir, 'visual-planning-summary.md'),
  };

  writeJson(outputs.surfaceInventoryPath, surfaceInventory);
  writeJson(outputs.componentCatalogPath, componentCatalog);
  writeJson(outputs.surfaceVisualSpecsPath, surfaceVisualSpecs);
  writeJson(outputs.componentVisualSpecsPath, componentVisualSpecs);
  writeJson(outputs.assetInventoryPath, assetInventory);
  writeJson(outputs.figmaFrameMapPath, figmaFrameMap);
  writeJson(outputs.figmaSyncQueuePath, figmaSyncQueue);
  writeText(outputs.governanceDriftReportPath, `${governanceDriftReport}\n`);
  writeText(outputs.visualPlanningSummaryPath, `${visualPlanningSummary}\n`);

  return outputs;
};

const checkAuditArtifacts = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputDir = DEFAULT_OUTPUT_DIR,
} = {}) => {
  const resolvedOutputDir = resolveOutputDir(repoRoot, outputDir);
  const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-design-check-'));

  try {
    const freshOutputs = generateAuditArtifacts({ repoRoot, outputDir: tempDir });
    const expectedFiles = {
      surfaceInventoryPath: path.join(resolvedOutputDir, 'surface-inventory.json'),
      componentCatalogPath: path.join(resolvedOutputDir, 'component-catalog.json'),
      surfaceVisualSpecsPath: path.join(resolvedOutputDir, 'surface-visual-specs.json'),
      componentVisualSpecsPath: path.join(resolvedOutputDir, 'component-visual-specs.json'),
      assetInventoryPath: path.join(resolvedOutputDir, 'asset-inventory.json'),
      figmaFrameMapPath: path.join(resolvedOutputDir, 'figma-frame-map.json'),
      figmaSyncQueuePath: path.join(resolvedOutputDir, 'figma-sync-queue.json'),
      governanceDriftReportPath: path.join(resolvedOutputDir, 'governance-drift-report.md'),
      visualPlanningSummaryPath: path.join(resolvedOutputDir, 'visual-planning-summary.md'),
    };

    const missing = Object.values(expectedFiles).filter((filePath) => !fs.existsSync(filePath));
    if (missing.length > 0) {
      return {
        ok: false,
        message: `Missing audit artifact(s): ${missing.join(', ')}`,
      };
    }

    const mismatched = Object.keys(expectedFiles).filter((key) => {
      const currentContent = fs.readFileSync(expectedFiles[key], 'utf8');
      const freshContent = fs.readFileSync(freshOutputs[key], 'utf8');
      return currentContent !== freshContent;
    });

    if (mismatched.length > 0) {
      return {
        ok: false,
        message: `Audit artifacts are stale: ${mismatched.join(', ')}`,
      };
    }

    return {
      ok: true,
      message: 'Novel design audit artifacts are up to date.',
    };
  } finally {
    fs.rmSync(tempDir, { recursive: true, force: true });
  }
};

const main = () => {
  const command = process.argv[2] || 'generate';
  const repoRoot = path.resolve(__dirname, '..');

  if (command === 'generate') {
    const outputs = generateAuditArtifacts({ repoRoot });
    console.log(`Novel design audit artifacts written to ${path.dirname(outputs.surfaceInventoryPath)}`);
    return;
  }

  if (command === 'check') {
    const result = checkAuditArtifacts({ repoRoot });
    if (!result.ok) {
      console.error(result.message);
      process.exit(1);
    }
    console.log(result.message);
    return;
  }

  console.error(`Unknown command: ${command}`);
  process.exit(1);
};

module.exports = {
  buildAssetInventory,
  buildComponentCatalog,
  buildComponentVisualSpecs,
  buildGovernanceDriftReport,
  buildSurfaceInventory,
  buildSurfaceVisualSpecs,
  checkAuditArtifacts,
  generateAuditArtifacts,
};

if (require.main === module) {
  main();
}
