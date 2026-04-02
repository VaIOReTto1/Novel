import { StyleSheet, Dimensions } from 'react-native';

import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { wp, fp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

const { height: screenHeight } = Dimensions.get('window');

export const createMessagePageStyles = (colors: NovelColors) => {
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
    title: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      color: novelDesign.color.text.primary,
      fontWeight: '600',
      textAlign: 'center',
    },
    markAllButton: {
      justifyContent: 'center',
      alignItems: 'center',
      minWidth: wp(32),
      minHeight: wp(32),
    },
    markAllIcon: {
      fontSize: 20,
    },

    mainMessagesSection: {
      backgroundColor: novelDesign.color.bg.surface,
    },
    messageItem: {
      flexDirection: 'row',
      alignItems: 'flex-start',
      paddingHorizontal: wp(16),
      paddingVertical: wp(12),
      borderBottomWidth: 1,
      borderBottomColor: novelDesign.color.border.subtle,
      backgroundColor: novelDesign.color.bg.surface,
    },
    messageIcon: {
      width: wp(40),
      height: wp(40),
      borderRadius: wp(20),
      backgroundColor: novelDesign.color.brand.primary,
      justifyContent: 'center',
      alignItems: 'center',
      marginTop: wp(4),
      marginRight: wp(12),
    },
    messageIconText: {
      fontSize: fp(18),
    },
    messageContent: {
      flex: 1,
      marginRight: wp(12),
    },
    messageHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: wp(4),
    },
    messageTitle: {
      ...typography.bodyMedium,
      color: novelDesign.color.text.primary,
      fontWeight: '500',
      fontSize: fp(15),
    },
    messageTime: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
      fontSize: fp(12),
    },
    messageBody: {
      ...typography.labelLarge,
      color: novelDesign.color.text.secondary,
      lineHeight: fp(18),
      marginBottom: wp(4),
    },
    notificationBadge: {
      width: wp(8),
      height: wp(8),
      borderRadius: wp(4),
      backgroundColor: novelDesign.color.status.danger,
      alignSelf: 'center',
    },
    messageBodyRow: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
    },

    divider: {
      height: wp(8),
      backgroundColor: novelDesign.color.bg.elevated,
    },

    tabsContainer: {
      backgroundColor: novelDesign.color.bg.surface,
      paddingHorizontal: wp(12),
      paddingVertical: wp(12),
      borderTopWidth: 0.5,
      borderTopColor: novelDesign.color.border.subtle,
      borderBottomWidth: 0.5,
      borderBottomColor: novelDesign.color.border.subtle,
    },
    tabsSectionContainer: {
      backgroundColor: 'transparent',
    },
    tabsScroll: {
      flexDirection: 'row',
    },
    tab: {
      marginRight: wp(20),
      minWidth: wp(60),
      alignItems: 'center',
    },
    activeTab: {},
    tabText: {
      fontSize: wp(16),
      color: novelDesign.color.text.secondary,
      fontWeight: '400',
    },
    activeTabText: {
      color: novelDesign.color.text.primary,
      fontWeight: '600',
    },
    tabsDivider: {
      height: 0.5,
      backgroundColor: novelDesign.color.border.subtle,
    },

    contentContainer: {
      flex: 1,
    },
    scrollView: {
      flex: 1,
    },
    scrollContent: {
      paddingBottom: wp(32),
      minHeight: screenHeight * 1.2,
    },

    emptyContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: wp(80),
      paddingHorizontal: wp(40),
    },
    emptyImage: {
      width: wp(120),
      height: wp(120),
      marginBottom: wp(16),
      borderRadius: wp(60),
      backgroundColor: novelDesign.color.bg.elevated,
      justifyContent: 'center',
      alignItems: 'center',
    },
    emptyImageText: {
      fontSize: fp(60),
      opacity: 0.3,
    },
    emptyText: {
      ...typography.bodyMedium,
      color: novelDesign.color.text.secondary,
      textAlign: 'center',
      lineHeight: fp(22),
    },

    loadingContainer: {
      flexDirection: 'row',
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: wp(20),
      gap: wp(8),
    },
    loadingText: {
      ...typography.labelLarge,
      color: novelDesign.color.text.secondary,
    },
  });
};
