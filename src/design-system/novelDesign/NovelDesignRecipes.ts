import { fp, sp, wp } from '../../utils/theme/dimensions';
import { NovelDesignUI } from './NovelDesignUI';

export interface NovelDesignSurfaceRecipe {
  sectionOrder: string[];
  layoutBlueprint: {
    primaryRail: string;
    supportingRail?: string;
    footerDock?: string;
  };
  sectionRecipes: Record<string, string>;
  spacingRhythm: {
    pageGutter: number;
    sectionGap: number;
    blockGap: number;
    compactGap: number;
  };
  shapeLanguage: {
    chrome: string;
    content: string;
    action: string;
    overlay: string;
  };
  typographyRoles: {
    eyebrow: string;
    title: string;
    body: string;
    meta: string;
  };
  motionNotes: {
    enter: string;
    emphasis: string;
    overlay: string;
  };
}

export interface NovelDesignComponentSpec {
  recipeBinding: string;
  slotStructure: string[];
  stateMatrix: string[];
  tokenBinding: string[];
  platformAdaptation: string[];
}

export const createNovelDesignLayoutRecipes = (ui: NovelDesignUI) => ({
  screenShell: {
    flex: 1,
    backgroundColor: ui.color.bg.canvas,
  },
  pageRail: {
    paddingHorizontal: ui.metrics.pageGutter,
  },
  topBar: {
    ...ui.presets.topBar,
  },
  paperCard: {
    ...ui.presets.paperCard,
    paddingHorizontal: ui.metrics.cardPadding,
    paddingVertical: ui.metrics.compactCardPadding,
  },
  raisedCard: {
    ...ui.presets.elevatedCard,
    paddingHorizontal: ui.metrics.cardPadding,
    paddingVertical: ui.metrics.compactCardPadding,
  },
  sectionStack: {
    gap: ui.metrics.blockGap,
  },
  listSection: {
    gap: ui.metrics.compactGap,
  },
  floatingPanel: {
    ...ui.presets.elevatedCard,
    borderRadius: ui.metrics.panelRadius,
    paddingHorizontal: ui.metrics.cardPadding,
    paddingVertical: ui.metrics.compactCardPadding,
  },
  anchoredComposer: {
    backgroundColor: ui.color.bg.surface,
    borderTopWidth: 1,
    borderTopColor: ui.color.border.subtle,
    paddingHorizontal: ui.metrics.pageGutter,
    paddingTop: ui.spacePx('100'),
    paddingBottom: ui.spacePx('150'),
  },
  floatingPrimaryAction: {
    width: wp(56),
    height: wp(56),
    borderRadius: wp(28),
    backgroundColor: ui.color.brand.primary,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: ui.alpha(ui.color.brand.secondary, 0.12),
    shadowColor: ui.alpha(ui.color.text.primary, 0.18),
    shadowOpacity: 0.18,
    shadowRadius: 14,
    shadowOffset: {
      width: 0,
      height: 6,
    },
    elevation: 10,
  },
  divider: {
    height: 1,
    backgroundColor: ui.color.border.subtle,
  },
});

export const createNovelDesignComponentRecipes = (ui: NovelDesignUI) => ({
  quietIconButton: {
    width: wp(36),
    height: wp(36),
    borderRadius: sp(18),
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: ui.color.bg.elevated,
    borderWidth: 1,
    borderColor: ui.color.border.subtle,
  },
  primaryActionPill: {
    backgroundColor: ui.color.brand.primary,
    borderRadius: ui.metrics.chipRadius,
    paddingHorizontal: wp(14),
    paddingVertical: wp(8),
    minHeight: ui.metrics.buttonHeightMd,
    alignItems: 'center',
    justifyContent: 'center',
  },
  secondaryActionPill: {
    backgroundColor: ui.color.bg.surface,
    borderWidth: 1,
    borderColor: ui.color.border.subtle,
    borderRadius: ui.metrics.chipRadius,
    paddingHorizontal: wp(12),
    paddingVertical: wp(8),
    minHeight: ui.metrics.buttonHeightMd,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  eyebrowText: {
    color: ui.color.brand.secondary,
    fontSize: fp(11),
    fontWeight: '600',
    letterSpacing: 0.3,
    textTransform: 'uppercase' as const,
  },
  sectionTitleText: {
    color: ui.color.text.primary,
    fontSize: fp(18),
    lineHeight: fp(24),
    fontWeight: '700',
  },
  titleText: {
    color: ui.color.text.primary,
    fontSize: fp(16),
    lineHeight: fp(22),
    fontWeight: '600',
  },
  bodyText: {
    color: ui.color.text.primary,
    fontSize: fp(14),
    lineHeight: fp(22),
  },
  metaText: {
    color: ui.color.text.secondary,
    fontSize: fp(12),
    lineHeight: fp(16),
  },
  composerInput: {
    ...ui.presets.inputField,
    minHeight: ui.metrics.inputMinHeight,
    maxHeight: ui.metrics.inputMaxHeight,
    paddingHorizontal: wp(12),
    paddingVertical: wp(10),
    color: ui.color.text.primary,
  },
  feedCard: {
    ...ui.presets.paperCard,
    paddingHorizontal: ui.metrics.cardPadding,
    paddingVertical: ui.metrics.cardPadding,
  },
  contentCard: {
    ...ui.presets.paperCard,
    paddingHorizontal: ui.metrics.cardPadding,
    paddingVertical: ui.metrics.compactCardPadding,
  },
  actionStrip: {
    flexDirection: 'row' as const,
    flexWrap: 'wrap' as const,
    gap: ui.spacePx('100'),
  },
  floatingActionButton: {
    ...createNovelDesignLayoutRecipes(ui).floatingPrimaryAction,
  },
});

const buildCommonSurface = (ui: NovelDesignUI): Omit<
  NovelDesignSurfaceRecipe,
  'sectionOrder' | 'layoutBlueprint' | 'sectionRecipes'
> => ({
  spacingRhythm: {
    pageGutter: ui.metrics.pageGutter,
    sectionGap: ui.metrics.sectionGap,
    blockGap: ui.metrics.blockGap,
    compactGap: ui.metrics.compactGap,
  },
  shapeLanguage: {
    chrome: 'quiet-paper-chrome',
    content: 'paper-card-stack',
    action: 'rounded-pill',
    overlay: 'raised-panel',
  },
  typographyRoles: {
    eyebrow: 'typography.eyebrow.md',
    title: 'typography.title.section',
    body: 'typography.body.md',
    meta: 'typography.meta.sm',
  },
  motionNotes: {
    enter: 'motion.duration.normal + motion.curve.standard',
    emphasis: 'motion.duration.fast + motion.curve.decelerate',
    overlay: 'motion.duration.normal + motion.curve.standard',
  },
});

export const createNovelDesignSurfaceRecipes = (
  ui: NovelDesignUI,
): Record<string, NovelDesignSurfaceRecipe> => ({
  'rn-host-ai-write-assistant-component': {
    ...buildCommonSurface(ui),
    sectionOrder: ['header', 'conversation', 'suggestions', 'ideaPanel', 'composer'],
    layoutBlueprint: {
      primaryRail: 'conversation-thread',
      footerDock: 'anchored-composer',
    },
    sectionRecipes: {
      header: 'quiet-top-bar',
      conversation: 'editorial-thread',
      suggestions: 'mode-pill-rail',
      ideaPanel: 'floating-choice-panel',
      composer: 'anchored-composer',
    },
  },
  'rn-host-write-page-component': {
    ...buildCommonSurface(ui),
    sectionOrder: ['header', 'welcomeCard', 'editorCanvas', 'selectionToolbar', 'modals'],
    layoutBlueprint: {
      primaryRail: 'editor-canvas',
      footerDock: 'selection-toolbar',
    },
    sectionRecipes: {
      header: 'quiet-top-bar',
      welcomeCard: 'writer-welcome-card',
      editorCanvas: 'paper-editor-canvas',
      selectionToolbar: 'floating-editor-toolbar',
      modals: 'floating-parameter-dialog',
    },
  },
  'rn-host-book-manage-page-component': {
    ...buildCommonSurface(ui),
    sectionOrder: ['header', 'bookBanner', 'draftRecovery', 'chapterStack', 'footer'],
    layoutBlueprint: {
      primaryRail: 'editorial-management-stack',
      footerDock: 'primary-footer-cta',
    },
    sectionRecipes: {
      header: 'quiet-top-bar',
      bookBanner: 'hero-book-card',
      draftRecovery: 'draft-status-card',
      chapterStack: 'paper-chapter-stack',
      footer: 'primary-footer-cta',
    },
  },
  'rn-host-comment-page-component': {
    ...buildCommonSurface(ui),
    sectionOrder: ['topBar', 'ratingHero', 'categoryRail', 'commentFeed'],
    layoutBlueprint: {
      primaryRail: 'review-feed',
    },
    sectionRecipes: {
      topBar: 'search-top-bar',
      ratingHero: 'rating-summary-card',
      categoryRail: 'comment-filter-rail',
      commentFeed: 'editorial-comment-feed',
    },
  },
  'rn-nested-bookshelf-community-page': {
    ...buildCommonSurface(ui),
    sectionOrder: ['topBar', 'circleTabs', 'feedHeading', 'postFeed', 'floatingCompose'],
    layoutBlueprint: {
      primaryRail: 'community-feed',
      footerDock: 'floating-compose',
    },
    sectionRecipes: {
      topBar: 'quiet-top-bar',
      circleTabs: 'community-circle-rail',
      feedHeading: 'section-heading',
      postFeed: 'editorial-feed-cards',
      floatingCompose: 'floating-compose',
    },
  },
});

export const createNovelDesignSurfaceSpec = (
  surfaceId: string,
  ui: NovelDesignUI,
): NovelDesignSurfaceRecipe => {
  const recipes = createNovelDesignSurfaceRecipes(ui);

  return (
    recipes[surfaceId] || {
      ...buildCommonSurface(ui),
      sectionOrder: ['content'],
      layoutBlueprint: {
        primaryRail: 'single-column-content',
      },
      sectionRecipes: {
        content: 'paper-content-stack',
      },
    }
  );
};

export const createNovelDesignComponentSpec = (
  componentPath: string,
  ui: NovelDesignUI,
): NovelDesignComponentSpec => {
  const commonBindings = [
    'color.bg.surface',
    'color.text.primary',
    'color.text.secondary',
    'color.border.subtle',
    'space.100',
    'space.150',
    'space.200',
    'radius.md',
    'radius.full',
  ];

  const lookup: Record<string, NovelDesignComponentSpec> = {
    'src/page/Writer/AIWriteAssistant/components/ActionBar.tsx': {
      recipeBinding: 'assistant-response-actions',
      slotStructure: ['disclaimer', 'actionGroup', 'actionPill'],
      stateMatrix: ['default', 'pressed', 'disabled'],
      tokenBinding: [...commonBindings, 'color.brand.primary', 'typography.meta.sm'],
      platformAdaptation: ['rn-wrap-actions', 'android-horizontal-action-row'],
    },
    'src/page/BookshelfPage/pages/Community/components/PostItem.tsx': {
      recipeBinding: 'community-post-card',
      slotStructure: ['authorRow', 'contentBody', 'mediaStrip', 'actionRow'],
      stateMatrix: ['default', 'liked', 'pressed'],
      tokenBinding: [...commonBindings, 'typography.title.section', 'typography.meta.sm'],
      platformAdaptation: ['rn-scrollable-media-strip', 'android-column-card'],
    },
    'src/page/comment/CommentPage/components/CommentList.tsx': {
      recipeBinding: 'editorial-comment-feed',
      slotStructure: ['authorRow', 'contentBody', 'metaRow', 'actionRow', 'ctaRow'],
      stateMatrix: ['default', 'expanded', 'liked', 'loading'],
      tokenBinding: [...commonBindings, 'color.brand.primary', 'typography.body.md'],
      platformAdaptation: ['rn-flat-list-feed', 'android-lazy-column-feed'],
    },
  };

  return (
    lookup[componentPath] || {
      recipeBinding: 'editorial-layout-shell',
      slotStructure: ['container'],
      stateMatrix: ['default'],
      tokenBinding: commonBindings,
      platformAdaptation: ['shared-layout-contract'],
    }
  );
};
