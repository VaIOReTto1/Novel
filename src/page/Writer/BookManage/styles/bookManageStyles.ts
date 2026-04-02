import { StyleSheet } from 'react-native';

import {
  createNovelDesignComponentRecipes,
  createNovelDesignLayoutRecipes,
  createNovelDesignSurfaceSpec,
  createNovelDesignUI,
} from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp } from '../../../../utils/theme/dimensions';

export const createBookManageStyles = (colors: NovelColors) => {
  const ui = createNovelDesignUI(colors);
  const layout = createNovelDesignLayoutRecipes(ui);
  const components = createNovelDesignComponentRecipes(ui);
  const surfaceRecipe = createNovelDesignSurfaceSpec(
    'rn-host-book-manage-page-component',
    ui,
  );

  return StyleSheet.create({
    container: {
      ...layout.screenShell,
    },
    header: {
      ...layout.topBar,
      flexDirection: 'row',
      alignItems: 'center',
    },
    headerAction: {
      ...components.quietIconButton,
    },
    back: {
      fontSize: fp(20),
      color: ui.color.text.primary,
    },
    title: {
      marginLeft: wp(12),
      color: ui.color.text.primary,
      fontSize: fp(17),
      fontWeight: '700',
    },
    contentScroll: {
      paddingHorizontal: surfaceRecipe.spacingRhythm.pageGutter,
      paddingTop: surfaceRecipe.spacingRhythm.sectionGap,
      paddingBottom: ui.spacePx('500'),
      gap: surfaceRecipe.spacingRhythm.sectionGap,
    },
    banner: {
      paddingBottom: surfaceRecipe.spacingRhythm.compactGap,
    },
    bannerCard: {
      ...layout.paperCard,
      flexDirection: 'row',
      alignItems: 'center',
      gap: surfaceRecipe.spacingRhythm.blockGap,
    },
    cover: {
      width: ui.metrics.writerCover.width,
      height: ui.metrics.writerCover.height,
      borderRadius: sp(12),
      backgroundColor: ui.color.bg.elevated,
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
    },
    bannerInfo: {
      flex: 1,
      gap: ui.spacePx('050'),
    },
    bookTitle: {
      color: ui.color.text.primary,
      fontSize: fp(20),
      lineHeight: fp(28),
      fontWeight: '700',
    },
    author: {
      color: ui.color.text.secondary,
      fontSize: fp(13),
    },
    status: {
      color: ui.color.brand.primary,
      fontSize: fp(12),
      fontWeight: '700',
    },
    draft: {
      ...layout.raisedCard,
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      gap: surfaceRecipe.spacingRhythm.blockGap,
    },
    draftText: {
      flex: 1,
      color: ui.color.text.primary,
      fontSize: fp(13),
      lineHeight: fp(20),
    },
    draftAction: {
      color: ui.color.brand.primary,
      fontWeight: '700',
      fontSize: fp(13),
    },
    sectionHeaderRow: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginTop: surfaceRecipe.spacingRhythm.compactGap,
      marginBottom: ui.spacePx('100'),
    },
    sectionTitle: {
      color: ui.color.text.primary,
      fontWeight: '700',
      fontSize: fp(16),
    },
    chapterList: {
      gap: ui.spacePx('100'),
    },
    chapterCard: {
      ...layout.paperCard,
      gap: ui.spacePx('050'),
    },
    chapterTitle: {
      color: ui.color.text.primary,
      fontSize: fp(15),
      fontWeight: '700',
    },
    chapterMeta: {
      color: ui.color.text.secondary,
      marginTop: wp(2),
      fontSize: fp(12),
    },
    volume: {
      alignSelf: 'flex-start',
      paddingHorizontal: wp(12),
      paddingVertical: wp(6),
      borderRadius: ui.metrics.chipRadius,
      backgroundColor: ui.alpha(ui.color.brand.primary, 0.12),
      borderWidth: 1,
      borderColor: ui.alpha(ui.color.brand.primary, 0.18),
    },
    volumeText: {
      color: ui.color.brand.secondary,
      fontSize: fp(12),
      fontWeight: '700',
    },
    emptyWrap: {
      ...layout.raisedCard,
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: ui.spacePx('400'),
    },
    emptyCard: {
      alignItems: 'center',
      gap: ui.spacePx('050'),
    },
    emptyText: {
      color: ui.color.text.secondary,
      marginTop: wp(8),
      fontSize: fp(13),
      textAlign: 'center',
    },
    footer: {
      ...layout.anchoredComposer,
      borderTopWidth: 1,
      borderTopColor: ui.color.border.subtle,
    },
    createBtn: {
      ...components.primaryActionPill,
      minHeight: ui.metrics.buttonHeightLg,
    },
    createText: {
      color: ui.color.text.inverse,
      fontWeight: '700',
      fontSize: fp(14),
    },
    tip: {
      textAlign: 'center',
      marginTop: wp(8),
      color: ui.color.text.secondary,
      fontSize: fp(11),
      lineHeight: fp(16),
    },
  });
};
