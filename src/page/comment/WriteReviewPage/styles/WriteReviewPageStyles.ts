import { StyleSheet } from 'react-native';

import {
  createNovelDesignComponentRecipes,
  createNovelDesignLayoutRecipes,
  createNovelDesignSurfaceSpec,
  createNovelDesignUI,
} from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp } from '../../../../utils/theme/dimensions';

export const createWriteReviewPageStyles = (colors: NovelColors) => {
  const ui = createNovelDesignUI(colors);
  const layout = createNovelDesignLayoutRecipes(ui);
  const components = createNovelDesignComponentRecipes(ui);
  const surfaceRecipe = createNovelDesignSurfaceSpec(
    'rn-host-write-review-page-component',
    ui,
  );

  return StyleSheet.create({
    container: {
      ...layout.screenShell,
    },
    topBar: {
      ...layout.topBar,
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      borderBottomColor: ui.color.border.subtle,
    },
    closeButton: {
      ...components.quietIconButton,
    },
    closeIcon: {
      fontSize: fp(18),
      color: ui.color.text.primary,
      fontWeight: '600',
    },
    titleContainer: {
      flex: 1,
      alignItems: 'center',
      justifyContent: 'center',
      paddingHorizontal: ui.spacePx('100'),
    },
    topBarTitle: {
      color: ui.color.text.primary,
      fontSize: fp(17),
      fontWeight: '700',
    },
    submitButton: {
      ...components.primaryActionPill,
      minWidth: wp(72),
      minHeight: ui.metrics.buttonHeightMd,
    },
    submitButtonText: {
      fontSize: fp(12),
      fontWeight: '700',
      color: ui.color.text.inverse,
    },
    scrollContainer: {
      flex: 1,
    },
    contentContainer: {
      paddingHorizontal: surfaceRecipe.spacingRhythm.pageGutter,
      paddingTop: surfaceRecipe.spacingRhythm.sectionGap,
      paddingBottom: ui.spacePx('500'),
      gap: surfaceRecipe.spacingRhythm.sectionGap,
    },
    ratingContainer: {
      ...layout.paperCard,
      alignItems: 'center',
      gap: ui.spacePx('100'),
    },
    sectionTitle: {
      ...components.sectionTitleText,
      textAlign: 'center',
    },
    starContainer: {
      flexDirection: 'row',
      justifyContent: 'center',
      gap: ui.spacePx('050'),
    },
    starButton: {
      ...components.quietIconButton,
      width: wp(44),
      height: wp(44),
      backgroundColor: ui.alpha(ui.color.brand.primary, 0.08),
      borderColor: ui.alpha(ui.color.brand.primary, 0.12),
    },
    starButtonSelected: {
      backgroundColor: ui.color.interaction.selected,
      borderColor: ui.color.border.focus,
      transform: [{ scale: 1.05 }],
    },
    starText: {
      fontSize: fp(24),
      color: ui.color.text.secondary,
    },
    starTextSelected: {
      color: ui.color.brand.primary,
    },
    ratingLabel: {
      color: ui.color.brand.primary,
      fontSize: fp(13),
      fontWeight: '700',
    },
    formContainer: {
      ...layout.paperCard,
      gap: ui.spacePx('150'),
    },
    divider: {
      height: 1,
      backgroundColor: ui.color.border.subtle,
    },
    inputSection: {
      gap: ui.spacePx('100'),
    },
    inputHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
    },
    charCount: {
      color: ui.color.text.secondary,
      fontSize: fp(12),
    },
    charCountError: {
      color: ui.color.status.danger,
    },
    titleInput: {
      ...components.composerInput,
      minHeight: wp(48),
      backgroundColor: ui.color.bg.surface,
    },
    contentInput: {
      ...components.composerInput,
      minHeight: wp(180),
      maxHeight: wp(260),
      backgroundColor: ui.color.bg.surface,
      textAlignVertical: 'top',
    },
    inputError: {
      borderColor: ui.color.status.danger,
    },
    errorText: {
      color: ui.color.status.danger,
      fontSize: fp(12),
    },
    submitContainer: {
      ...layout.anchoredComposer,
      position: 'absolute',
      left: 0,
      right: 0,
      bottom: 0,
    },
    tipsContainer: {
      ...layout.raisedCard,
      overflow: 'hidden',
      borderRadius: ui.radiusPx('lg'),
    },
    tipsHeader: {
      ...layout.paperCard,
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      paddingVertical: ui.spacePx('150'),
    },
    tipsTitle: {
      color: ui.color.text.primary,
      fontSize: fp(14),
      fontWeight: '700',
    },
    tipsToggle: {
      color: ui.color.text.secondary,
      fontSize: fp(12),
    },
    tipsToggleExpanded: {
      transform: [{ rotate: '180deg' }],
    },
    tipsContent: {
      paddingHorizontal: ui.metrics.cardPadding,
      paddingBottom: ui.metrics.cardPadding,
      backgroundColor: ui.color.bg.elevated,
    },
    tipItem: {
      flexDirection: 'row',
      alignItems: 'flex-start',
      marginBottom: ui.spacePx('100'),
    },
    tipBullet: {
      color: ui.color.brand.primary,
      fontSize: fp(14),
      marginRight: ui.spacePx('100'),
      marginTop: 2,
    },
    tipText: {
      flex: 1,
      color: ui.color.text.secondary,
      fontSize: fp(13),
      lineHeight: fp(20),
    },
  });
};
