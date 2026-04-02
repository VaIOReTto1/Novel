import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createMyReservationPageStyles = (colors: NovelColors) => {
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
      backgroundColor: novelDesign.color.bg.surface,
      minHeight: wp(56),
    },
    backButton: {
      justifyContent: 'center',
      alignItems: 'center',
      minWidth: wp(32),
      minHeight: wp(32),
    },
    backArrow: {
      fontSize: fp(40),
      color: novelDesign.color.text.primary,
      fontWeight: '300',
      lineHeight: fp(28),
    },
    titleContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },

    topBarTabsContainer: {
      flexDirection: 'row',
    },
    topBarTab: {
      paddingHorizontal: wp(8),
      alignItems: 'center',
    },
    activeTopBarTab: {
      backgroundColor: novelDesign.color.bg.surface,
      shadowColor: novelDesign.color.text.primary,
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.1,
      shadowRadius: 2,
      elevation: 2,
    },
    topBarTabText: {
      fontSize: fp(17),
      color: novelDesign.color.text.secondary,
      fontWeight: '500',
    },
    activeTopBarTabText: {
      color: novelDesign.color.text.primary,
      fontWeight: '600',
    },

    scrollView: {
      flex: 1,
    },
    scrollContent: {
      paddingBottom: wp(20),
    },

    section: {
      backgroundColor: novelDesign.color.bg.surface,
      borderRadius: sp(8),
      padding: wp(16),
      marginHorizontal: wp(16),
      marginBottom: wp(16),
    },

    subTabsContainer: {
      flexDirection: 'row',
      gap: wp(12),
      paddingHorizontal: wp(16),
      paddingVertical: wp(12),
      backgroundColor: novelDesign.color.bg.surface,
    },
    subTab: {
      paddingHorizontal: wp(10),
      paddingVertical: wp(6),
      borderRadius: sp(5),
      backgroundColor: novelDesign.color.bg.elevated,
    },
    activeSubTab: {
      backgroundColor: novelDesign.color.bg.elevated,
      borderColor: novelDesign.color.brand.primary,
    },
    subTabText: {
      fontSize: fp(12),
      color: novelDesign.color.text.primary,
    },
    activeSubTabText: {
      color: novelDesign.color.brand.primary,
      fontWeight: '600',
    },

    gridContainer: {
      paddingHorizontal: wp(16),
    },
    gridRow: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      marginBottom: wp(8),
    },
    gridItem: {
      width: '49%',
      aspectRatio: 0.58,
      borderRadius: sp(12),
      overflow: 'hidden',
      backgroundColor: novelDesign.color.bg.elevated,
      position: 'relative',
    },

    reservationCard: {
      position: 'relative',
      width: '100%',
      height: '100%',
    },
    cardImage: {
      position: 'absolute',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      width: '100%',
      height: '100%',
    },
    cardBadge: {
      position: 'absolute',
      top: wp(8),
      right: wp(8),
      backgroundColor: novelDesign.color.brand.primary,
      paddingHorizontal: wp(6),
      paddingVertical: wp(2),
      borderRadius: sp(4),
    },
    cardBadgeText: {
      fontSize: fp(10),
      color: novelDesign.color.text.inverse,
      fontWeight: '500',
    },
    cardGradient: {
      position: 'absolute',
      left: 0,
      right: 0,
      bottom: 0,
      height: '60%',
    },
    cardContent: {
      position: 'absolute',
      left: 0,
      right: 0,
      bottom: 0,
      padding: wp(16),
      zIndex: 1,
    },
    cardTitle: {
      fontSize: fp(14),
      color: novelDesign.color.text.inverse,
      fontWeight: '500',
      marginBottom: wp(4),
      maxLength: 1,
      ellipsizeMode: 'tail',
    },
    cardSubtitle: {
      fontSize: fp(10),
      color: novelDesign.color.text.inverse,
      marginBottom: wp(12),
    },
    cardButton: {
      width: '100%',
      paddingVertical: wp(5),
      backgroundColor: novelDesign.color.bg.surface,
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      borderRadius: sp(24),
      alignItems: 'center',
    },
    cardButtonText: {
      fontSize: fp(12),
      color: novelDesign.color.text.primary,
      fontWeight: '500',
    },
    reservedButton: {
      backgroundColor: novelDesign.color.bg.elevated,
      borderColor: novelDesign.color.border.subtle,
    },
    reservedButtonText: {
      color: novelDesign.color.text.inverse,
    },

    emptyContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: wp(80),
      paddingHorizontal: wp(20),
    },
    emptyIcon: {
      width: wp(160),
      height: wp(160),
      backgroundColor: novelDesign.color.bg.elevated,
      borderRadius: wp(80),
      justifyContent: 'center',
      alignItems: 'center',
      marginBottom: wp(24),
    },
    emptyImageAsset: {
      width: 80,
      height: 80,
    },
    emptyIconText: {
      fontSize: fp(48),
    },
    emptyTitle: {
      fontSize: fp(14),
      color: novelDesign.color.text.secondary,
      textAlign: 'center',
    },

    loadingContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      padding: wp(20),
    },
    loadingText: {
      ...typography.bodyMedium,
      color: novelDesign.color.text.secondary,
      marginTop: wp(8),
    },
  });
};
