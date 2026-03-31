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
  if (/Input|Form|Search|Picker|Switch/i.test(baseName)) {
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

const buildSurfaceVisualSpecs = (surfaces) =>
  surfaces.map((surface) => {
    const cluster = determineSurfaceCluster(surface);
    const plan = SURFACE_CLUSTER_PLANS[cluster] || SURFACE_CLUSTER_PLANS.utility-detail;
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
        layout: plan.current.layout,
        chrome: plan.current.chrome,
        content_pattern: plan.current.content_pattern,
        key_states: surface.key_states,
        key_components: surface.key_components,
      },
      target_visual_plan: {
        direction: 'literary-editorial',
        layout_strategy: plan.target.layout_strategy,
        component_recipe: plan.target.component_recipe,
        style_keywords: plan.target.style_keywords,
        token_priority: [
          'color.bg.canvas',
          'color.text.primary',
          'color.brand.primary',
          'space.200',
          'radius.lg',
          'typography.title.section',
        ],
        figma_targets: {
          audit_page: figmaAuditPage,
          design_page_light: '03-页面-亮色',
          design_page_dark: '04-页面-暗色',
        },
      },
      current_look_recorded: true,
      target_look_planned: true,
    };
  });

const buildComponentCatalog = (repoRoot) => {
  const componentFiles = walkFiles(
    path.join(repoRoot, 'src', 'page'),
    (filePath) =>
      /\.(ts|tsx)$/.test(filePath) &&
      filePath.includes(`${path.sep}components${path.sep}`),
  );

  const entries = componentFiles
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
    })
    .sort((left, right) => left.path.localeCompare(right.path));

  const summary = entries.reduce(
    (acc, entry) => {
      acc.rn_component_count += 1;
      acc.category_counts[entry.category] = (acc.category_counts[entry.category] || 0) + 1;
      return acc;
    },
    { rn_component_count: 0, category_counts: {} },
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

const buildComponentVisualSpecs = (catalog) =>
  catalog.entries.map((entry) => {
    const plan = COMPONENT_CATEGORY_PLANS[entry.category] || COMPONENT_CATEGORY_PLANS.layout;

    return {
      path: entry.path,
      name: entry.name,
      category: entry.category,
      platform: entry.platform,
      current_visual_summary: {
        structure: plan.current.structure,
        affordance: plan.current.affordance,
        asset_sources: entry.asset_sources,
      },
      target_visual_plan: {
        direction: 'literary-editorial',
        component_recipe: plan.target.component_recipe,
        style_keywords: plan.target.style_keywords,
        figma_target_page: '02-组件规范',
      },
      current_look_recorded: true,
      target_look_planned: true,
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
    '# Stage 7 Governance Drift Report',
    '',
    '## Summary',
    `- Surface count: ${surfaces.length}`,
    `- Registry drift: ${missingFromRegistry.length === 0 && extraInRegistry.length === 0 ? 'none' : 'present'}`,
    `- RN smoke tests: ${rnSmokeTests.length}`,
    `- Missing smoke catalog entries: ${missingFromCatalog.length}`,
    `- Unmapped figma frames: ${figmaFrameMap.filter((item) => !item.figma_frame_id).length}`,
    `- Surface visual specs coverage: ${surfaces.length}/${surfaces.length}`,
    `- Component visual specs coverage: ${componentVisualSpecs.length}/${componentVisualSpecs.length}`,
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
    '# Stage 7 Visual Planning Summary',
    '',
    '## Surface visual specs',
    `- Current look recorded: ${surfaceVisualSpecs.filter((entry) => entry.current_look_recorded).length}`,
    `- Target look planned: ${surfaceVisualSpecs.filter((entry) => entry.target_look_planned).length}`,
    ...Object.entries(surfaceClusterCounts).map(([cluster, count]) => `- ${cluster}: ${count}`),
    '',
    '## Component visual specs',
    `- Current look recorded: ${componentVisualSpecs.filter((entry) => entry.current_look_recorded).length}`,
    `- Target look planned: ${componentVisualSpecs.filter((entry) => entry.target_look_planned).length}`,
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
  const figmaFrameMap = buildFigmaFrameMap(surfaceInventory);
  const surfaceVisualSpecs = buildSurfaceVisualSpecs(surfaceInventory);
  const componentVisualSpecs = buildComponentVisualSpecs(componentCatalog);
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
    governanceDriftReportPath: path.join(resolvedOutputDir, 'governance-drift-report.md'),
    visualPlanningSummaryPath: path.join(resolvedOutputDir, 'visual-planning-summary.md'),
  };

  writeJson(outputs.surfaceInventoryPath, surfaceInventory);
  writeJson(outputs.componentCatalogPath, componentCatalog);
  writeJson(outputs.surfaceVisualSpecsPath, surfaceVisualSpecs);
  writeJson(outputs.componentVisualSpecsPath, componentVisualSpecs);
  writeJson(outputs.assetInventoryPath, assetInventory);
  writeJson(outputs.figmaFrameMapPath, figmaFrameMap);
  writeText(outputs.governanceDriftReportPath, `${governanceDriftReport}\n`);
  writeText(outputs.visualPlanningSummaryPath, `${visualPlanningSummary}\n`);

  return outputs;
};

const checkAuditArtifacts = ({
  repoRoot = path.resolve(__dirname, '..'),
  outputDir = DEFAULT_OUTPUT_DIR,
} = {}) => {
  const resolvedOutputDir = resolveOutputDir(repoRoot, outputDir);
  const tempDir = fs.mkdtempSync(path.join(os.tmpdir(), 'novel-stage7-check-'));

  try {
    const freshOutputs = generateAuditArtifacts({ repoRoot, outputDir: tempDir });
    const expectedFiles = {
      surfaceInventoryPath: path.join(resolvedOutputDir, 'surface-inventory.json'),
      componentCatalogPath: path.join(resolvedOutputDir, 'component-catalog.json'),
      surfaceVisualSpecsPath: path.join(resolvedOutputDir, 'surface-visual-specs.json'),
      componentVisualSpecsPath: path.join(resolvedOutputDir, 'component-visual-specs.json'),
      assetInventoryPath: path.join(resolvedOutputDir, 'asset-inventory.json'),
      figmaFrameMapPath: path.join(resolvedOutputDir, 'figma-frame-map.json'),
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
      message: 'Stage 7 audit artifacts are up to date.',
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
    console.log(`Stage 7 audit artifacts written to ${path.dirname(outputs.surfaceInventoryPath)}`);
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
