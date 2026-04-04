import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';

export const createRecommendBookPageStyles = (colors: NovelColors) => {
  const novelDesign = createNovelDesignUI(colors);

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.canvas,
    },

    topBar: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(20),
      paddingVertical: wp(16),
      minHeight: wp(56),
      backgroundColor: novelDesign.color.bg.canvas,
    },
    backButton: {
      width: wp(36),
      height: wp(36),
      alignItems: 'center',
      justifyContent: 'center',
      borderRadius: sp(18),
      backgroundColor: novelDesign.color.bg.surface,
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
    },
    backArrow: {
      fontSize: fp(18),
      color: novelDesign.color.text.primary,
      fontWeight: '500',
    },
    titleContainer: {
      flex: 1,
      alignItems: 'center',
      justifyContent: 'center',
    },
    title: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      fontWeight: '600',
      color: novelDesign.color.text.primary,
      textAlign: 'center',
    },
    topBarSpacer: {
      width: wp(36),
      height: wp(36),
    },

    scrollView: {
      flex: 1,
    },
    scrollContent: {
      paddingHorizontal: wp(20),
      paddingBottom: wp(28),
      gap: wp(16),
    },

    overviewCard: {
      position: 'relative',
      overflow: 'hidden',
      backgroundColor: novelDesign.color.bg.surface,
      borderRadius: sp(28),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      padding: wp(20),
      gap: wp(16),
      shadowColor: novelDesign.color.brand.primary,
      shadowOffset: { width: 0, height: 10 },
      shadowOpacity: 0.16,
      shadowRadius: 22,
      elevation: 6,
    },
    overviewAura: {
      position: 'absolute',
      top: -wp(34),
      right: -wp(20),
      width: wp(132),
      height: wp(132),
      borderRadius: wp(66),
      backgroundColor: novelDesign.color.interaction.selected,
      opacity: 0.6,
    },
    overviewHeader: {
      flexDirection: 'row',
      alignItems: 'flex-start',
      gap: wp(12),
    },
    avatarBadge: {
      width: wp(48),
      height: wp(48),
      borderRadius: wp(24),
      backgroundColor: novelDesign.color.bg.elevated,
      alignItems: 'center',
      justifyContent: 'center',
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
    },
    avatarBadgeText: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      fontWeight: '700',
      color: novelDesign.color.text.primary,
    },
    overviewHeaderContent: {
      flex: 1,
      gap: wp(4),
    },
    overviewEyebrow: {
      ...typography.labelSmall,
      color: novelDesign.color.brand.secondary,
    },
    overviewName: {
      ...typography.bodyMedium,
      fontSize: fp(22),
      lineHeight: fp(28),
      fontWeight: '700',
      color: novelDesign.color.text.primary,
    },
    overviewMeta: {
      ...typography.labelLarge,
      color: novelDesign.color.text.secondary,
    },
    overviewValueBadge: {
      alignItems: 'flex-end',
      gap: wp(4),
      paddingHorizontal: wp(12),
      paddingVertical: wp(10),
      borderRadius: sp(18),
      backgroundColor: novelDesign.color.bg.elevated,
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
    },
    overviewValueLabel: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
    },
    overviewValueText: {
      ...typography.bodyMedium,
      fontSize: fp(16),
      fontWeight: '700',
      color: novelDesign.color.brand.primary,
    },

    kpiRow: {
      flexDirection: 'row',
      gap: wp(10),
    },
    kpiCard: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.elevated,
      borderRadius: sp(20),
      paddingHorizontal: wp(12),
      paddingVertical: wp(14),
      gap: wp(8),
    },
    kpiLabel: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
    },
    kpiValue: {
      ...typography.bodyMedium,
      fontSize: fp(20),
      fontWeight: '700',
      color: novelDesign.color.text.primary,
    },
    kpiHint: {
      ...typography.labelSmall,
      color: novelDesign.color.brand.secondary,
    },

    insightCard: {
      backgroundColor: novelDesign.color.bg.canvas,
      borderRadius: sp(20),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      paddingHorizontal: wp(14),
      paddingVertical: wp(14),
      gap: wp(10),
    },
    insightHeader: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: wp(8),
    },
    insightTitle: {
      ...typography.bodyMedium,
      fontSize: fp(15),
      fontWeight: '600',
      color: novelDesign.color.text.primary,
    },
    insightAccent: {
      ...typography.labelSmall,
      color: novelDesign.color.brand.primary,
    },
    insightText: {
      ...typography.labelLarge,
      lineHeight: fp(20),
      color: novelDesign.color.text.secondary,
    },
    insightAction: {
      alignSelf: 'flex-start',
      paddingHorizontal: wp(12),
      paddingVertical: wp(8),
      borderRadius: sp(16),
      backgroundColor: novelDesign.color.bg.elevated,
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
    },
    insightActionText: {
      ...typography.labelSmall,
      fontWeight: '600',
      color: novelDesign.color.text.primary,
    },

    toolSection: {
      gap: wp(10),
    },
    toolSectionTitle: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
    },
    toolsGrid: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      justifyContent: 'space-between',
      gap: wp(10),
    },
    toolCard: {
      width: '48%',
      minHeight: wp(82),
      backgroundColor: novelDesign.color.bg.surface,
      borderRadius: sp(16),
      paddingHorizontal: wp(12),
      paddingVertical: wp(10),
      gap: wp(4),
    },
    toolCardTitle: {
      ...typography.bodyMedium,
      fontSize: fp(14),
      fontWeight: '600',
      color: novelDesign.color.text.primary,
    },
    toolCardDescription: {
      ...typography.labelSmall,
      lineHeight: fp(16),
      color: novelDesign.color.text.secondary,
    },

    taskQueueCard: {
      backgroundColor: novelDesign.color.bg.surface,
      borderRadius: sp(24),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      padding: wp(18),
      gap: wp(14),
    },
    sectionHeader: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
    },
    sectionTitle: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      fontWeight: '600',
      color: novelDesign.color.text.primary,
    },
    moreLink: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
    },
    subtabsContainer: {
      flexDirection: 'row',
      gap: wp(10),
    },
    subtab: {
      minWidth: wp(56),
      paddingHorizontal: wp(14),
      paddingVertical: wp(8),
      borderRadius: sp(16),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      backgroundColor: novelDesign.color.bg.elevated,
      alignItems: 'center',
      justifyContent: 'center',
    },
    activeSubtab: {
      backgroundColor: novelDesign.color.brand.primary,
      borderColor: novelDesign.color.brand.primary,
    },
    subtabText: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
    },
    activeSubtabText: {
      color: novelDesign.color.text.inverse,
      fontWeight: '600',
    },

    primaryTaskCard: {
      backgroundColor: novelDesign.color.bg.surface,
      borderRadius: sp(24),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      padding: wp(16),
      gap: wp(16),
      shadowColor: novelDesign.color.brand.primary,
      shadowOffset: { width: 0, height: 8 },
      shadowOpacity: 0.08,
      shadowRadius: 18,
      elevation: 4,
    },
    primaryTaskContent: {
      gap: wp(12),
    },
    primaryTaskTitle: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      fontWeight: '700',
      color: novelDesign.color.text.primary,
    },
    primaryTaskDescription: {
      ...typography.labelLarge,
      lineHeight: fp(20),
      color: novelDesign.color.text.secondary,
    },
    taskMetricRow: {
      flexDirection: 'row',
      gap: wp(10),
    },
    taskMetricBlock: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.canvas,
      borderRadius: sp(16),
      paddingHorizontal: wp(12),
      paddingVertical: wp(10),
      gap: wp(4),
    },
    taskMetricLabel: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
    },
    taskMetricValue: {
      ...typography.labelLarge,
      fontWeight: '600',
      color: novelDesign.color.text.primary,
    },
    taskMetricValueAccent: {
      ...typography.labelLarge,
      fontWeight: '700',
      color: novelDesign.color.brand.primary,
    },
    taskTags: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      gap: wp(8),
    },
    taskTag: {
      borderRadius: sp(12),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      backgroundColor: novelDesign.color.bg.elevated,
      paddingHorizontal: wp(8),
      paddingVertical: wp(4),
    },
    taskTagText: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
    },
    taskActionButton: {
      alignSelf: 'flex-start',
      minWidth: wp(92),
      borderRadius: sp(16),
      backgroundColor: novelDesign.color.brand.primary,
      alignItems: 'center',
      justifyContent: 'center',
      paddingHorizontal: wp(14),
      paddingVertical: wp(9),
    },
    taskActionButtonText: {
      ...typography.labelSmall,
      fontWeight: '700',
      color: novelDesign.color.text.inverse,
    },

    taskCoverCard: {
      width: '100%',
      minHeight: wp(116),
      borderRadius: sp(22),
      paddingHorizontal: wp(14),
      paddingVertical: wp(14),
      justifyContent: 'space-between',
      borderWidth: 1,
      borderColor: 'rgba(255, 255, 255, 0.36)',
    },
    taskCoverCardCompact: {
      width: wp(72),
      minHeight: wp(88),
      paddingHorizontal: wp(10),
      paddingVertical: wp(10),
      borderRadius: sp(14),
    },
    taskCoverHeader: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: wp(8),
    },
    taskCoverSand: {
      backgroundColor: colors.novelBookBackground,
    },
    taskCoverSunrise: {
      backgroundColor: novelDesign.color.interaction.selected,
    },
    taskCoverSage: {
      backgroundColor: novelDesign.color.reader.emphasis,
    },
    taskCoverInk: {
      backgroundColor: novelDesign.color.reader.chrome,
    },
    taskCoverType: {
      ...typography.labelSmall,
      color: novelDesign.color.brand.secondary,
    },
    taskCoverBadge: {
      borderRadius: sp(999),
      backgroundColor: 'rgba(255, 253, 252, 0.72)',
      paddingHorizontal: wp(8),
      paddingVertical: wp(4),
    },
    taskCoverBadgeText: {
      ...typography.labelSmall,
      color: novelDesign.color.text.primary,
    },
    taskCoverFooter: {
      gap: wp(6),
    },
    taskCoverLabel: {
      ...typography.bodyMedium,
      fontSize: fp(22),
      lineHeight: fp(28),
      fontWeight: '700',
      color: novelDesign.color.text.primary,
    },
    taskCoverSupport: {
      ...typography.labelSmall,
      lineHeight: fp(18),
      color: novelDesign.color.text.secondary,
    },

    secondaryTaskList: {
      gap: wp(12),
    },
    secondaryTaskCard: {
      flexDirection: 'row',
      gap: wp(12),
      backgroundColor: novelDesign.color.bg.canvas,
      borderRadius: sp(20),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      padding: wp(12),
    },
    secondaryTaskContent: {
      flex: 1,
      gap: wp(6),
    },
    secondaryTaskTitle: {
      ...typography.bodyMedium,
      fontSize: fp(15),
      fontWeight: '600',
      color: novelDesign.color.text.primary,
    },
    secondaryTaskDescription: {
      ...typography.labelSmall,
      lineHeight: fp(18),
      color: novelDesign.color.text.secondary,
    },
    secondaryTaskFooter: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: wp(10),
    },
    secondaryTaskValue: {
      ...typography.labelLarge,
      fontWeight: '700',
      color: novelDesign.color.brand.primary,
    },
    secondaryTaskAction: {
      borderRadius: sp(14),
      backgroundColor: novelDesign.color.bg.surface,
      paddingHorizontal: wp(10),
      paddingVertical: wp(7),
    },
    secondaryTaskActionText: {
      ...typography.labelSmall,
      fontWeight: '600',
      color: novelDesign.color.text.primary,
    },

    emptyState: {
      borderRadius: sp(18),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      backgroundColor: novelDesign.color.bg.canvas,
      paddingVertical: wp(28),
      paddingHorizontal: wp(16),
      alignItems: 'center',
      justifyContent: 'center',
    },
    emptyStateText: {
      ...typography.labelLarge,
      color: novelDesign.color.text.secondary,
      textAlign: 'center',
    },

    loadingContainer: {
      flex: 1,
      alignItems: 'center',
      justifyContent: 'center',
      padding: wp(20),
    },
    loadingText: {
      ...typography.bodyMedium,
      color: novelDesign.color.text.secondary,
    },
  });
};
