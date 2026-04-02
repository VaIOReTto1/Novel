import { StyleSheet } from 'react-native';

import {
  createNovelDesignComponentRecipes,
  createNovelDesignLayoutRecipes,
  createNovelDesignSurfaceSpec,
  createNovelDesignUI,
} from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp } from '../../../../utils/theme/dimensions';

export const createWritePageStyles = (colors: NovelColors) => {
  const ui = createNovelDesignUI(colors);
  const layout = createNovelDesignLayoutRecipes(ui);
  const components = createNovelDesignComponentRecipes(ui);
  const surfaceRecipe = createNovelDesignSurfaceSpec(
    'rn-host-write-page-component',
    ui,
  );

  return StyleSheet.create({
    container: {
      ...layout.screenShell,
    },
    topBar: {
      ...layout.topBar,
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
    },
    navBtn: {
      ...components.quietIconButton,
    },
    navIcon: {
      fontSize: fp(22),
      color: ui.color.text.primary,
    },
    toolbar: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: ui.spacePx('100'),
    },
    toolBtn: {
      ...components.quietIconButton,
    },
    toolIcon: {
      fontSize: fp(16),
      color: ui.color.text.primary,
    },
    publishBtn: {
      ...components.primaryActionPill,
    },
    publishText: {
      color: ui.color.text.inverse,
      fontWeight: '700',
      fontSize: fp(13),
    },
    editor: {
      flex: 1,
    },
    editorScrollContent: {
      paddingHorizontal: surfaceRecipe.spacingRhythm.pageGutter,
      paddingTop: surfaceRecipe.spacingRhythm.sectionGap,
      paddingBottom: ui.spacePx('500'),
      gap: surfaceRecipe.spacingRhythm.sectionGap,
    },
    editorSheet: {
      ...layout.paperCard,
      minHeight: wp(560),
      gap: surfaceRecipe.spacingRhythm.blockGap,
    },
    titleInput: {
      fontSize: fp(22),
      lineHeight: fp(30),
      color: ui.color.text.primary,
      fontWeight: '700',
      paddingVertical: 0,
    },
    contentInput: {
      minHeight: wp(460),
      fontSize: fp(15),
      lineHeight: fp(28),
      color: ui.color.text.primary,
      textAlignVertical: 'top',
      paddingVertical: 0,
    },
    welcomePanel: {
      ...layout.raisedCard,
      gap: ui.spacePx('150'),
    },
    welcomeHeader: {
      flexDirection: 'row',
      alignItems: 'flex-start',
      justifyContent: 'space-between',
      gap: ui.spacePx('100'),
    },
    welcomeTitle: {
      color: ui.color.text.primary,
      fontSize: fp(18),
      lineHeight: fp(24),
      fontWeight: '700',
      flex: 1,
    },
    welcomeRow: {
      flexDirection: 'row',
      gap: ui.spacePx('100'),
    },
    welcomeItem: {
      flex: 1,
      ...layout.paperCard,
      paddingHorizontal: ui.metrics.compactCardPadding,
      paddingVertical: ui.metrics.compactCardPadding,
      gap: ui.spacePx('050'),
    },
    welcomeBadge: {
      width: wp(28),
      height: wp(28),
      borderRadius: wp(14),
      backgroundColor: ui.alpha(ui.color.brand.primary, 0.14),
      alignItems: 'center',
      justifyContent: 'center',
    },
    welcomeBadgeText: {
      color: ui.color.brand.primary,
      fontSize: fp(11),
      fontWeight: '700',
    },
    welcomeItemTitle: {
      color: ui.color.text.primary,
      fontSize: fp(13),
      fontWeight: '700',
    },
    welcomeSub: {
      color: ui.color.text.secondary,
      fontSize: fp(11),
      lineHeight: fp(16),
    },
    closeBtn: {
      ...components.quietIconButton,
      width: wp(30),
      height: wp(30),
    },
    closeText: {
      color: ui.color.text.secondary,
      fontSize: fp(12),
      fontWeight: '700',
    },
    volumeBar: {
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
    selectionToolbarBackdrop: {
      position: 'absolute',
      left: 0,
      right: 0,
      top: 0,
      bottom: 0,
    },
    selectionToolbarContainer: {
      ...layout.floatingPanel,
      flexDirection: 'row',
      justifyContent: 'space-around',
      paddingHorizontal: ui.spacePx('100'),
      paddingVertical: ui.spacePx('100'),
      elevation: 8,
      shadowColor: ui.alpha(ui.color.text.primary, 0.18),
      shadowOpacity: 0.18,
      shadowRadius: 10,
    },
    toolbarBtnText: {
      fontSize: fp(12),
      color: ui.color.text.primary,
      fontWeight: '700',
    },
    toolbarBtnTextSecondary: {
      fontSize: fp(12),
      color: ui.color.text.secondary,
      fontWeight: '600',
    },
    overlayMask: {
      position: 'absolute',
      left: 0,
      right: 0,
      top: 0,
      bottom: 0,
      backgroundColor: ui.alpha(ui.color.text.primary, 0.32),
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
    },
    modalContainer: {
      ...layout.paperCard,
      width: '84%',
      gap: ui.spacePx('100'),
    },
    modalHint: {
      color: ui.color.text.primary,
      fontSize: fp(14),
      lineHeight: fp(20),
    },
    modalInput: {
      ...components.composerInput,
      borderColor: ui.color.border.subtle,
    },
    modalActions: {
      flexDirection: 'row',
      justifyContent: 'flex-end',
      alignItems: 'center',
      gap: ui.spacePx('100'),
    },
    modalCancel: {
      color: ui.color.text.secondary,
      fontWeight: '600',
    },
    modalOk: {
      color: ui.color.brand.primary,
      fontWeight: '700',
    },
  });
};
