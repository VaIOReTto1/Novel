import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createViewedUsersPageStyles = (colors: NovelColors) => {
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

    tabsContainer: {
      flexDirection: 'row',
      backgroundColor: novelDesign.color.bg.surface,
      borderBottomWidth: 1,
      borderBottomColor: novelDesign.color.border.subtle,
      paddingHorizontal: wp(16),
    },
    tab: {
      flex: 1,
      paddingVertical: wp(8),
      alignItems: 'center',
      position: 'relative',
    },
    tabText: {
      fontSize: fp(16),
      color: novelDesign.color.text.secondary,
    },
    activeTabText: {
      color: novelDesign.color.text.primary,
      fontWeight: '600',
    },

    scrollView: {
      flex: 1,
    },
    scrollContent: {
      paddingBottom: wp(20),
    },

    emptyContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: wp(80),
      paddingHorizontal: wp(20),
    },
    emptyIcon: {
      width: wp(180),
      height: wp(180),
      backgroundColor: novelDesign.color.bg.elevated,
      borderRadius: wp(90),
      justifyContent: 'center',
      alignItems: 'center',
      marginBottom: wp(20),
    },
    emptyIconText: {
      fontSize: fp(60),
    },
    emptyTitle: {
      fontSize: fp(14),
      color: novelDesign.color.text.secondary,
      marginBottom: wp(20),
    },
    emptyButton: {
      backgroundColor: novelDesign.color.bg.surface,
      paddingHorizontal: wp(20),
      paddingVertical: wp(10),
      borderRadius: sp(22),
    },
    emptyButtonText: {
      fontSize: fp(12),
      color: novelDesign.color.brand.primary,
      fontWeight: '600',
    },

    usersList: {
      paddingHorizontal: wp(16),
    },
    userItem: {
      flexDirection: 'row',
      alignItems: 'center',
      paddingVertical: wp(14),
    },
    lastUserItem: {
      borderBottomWidth: 0,
    },

    avatarContainer: {
      position: 'relative',
      marginRight: wp(12),
    },
    avatar: {
      width: wp(48),
      height: wp(48),
      borderRadius: wp(24),
      backgroundColor: novelDesign.color.bg.elevated,
    },
    vBadge: {
      position: 'absolute',
      right: -2,
      bottom: -2,
      width: wp(18),
      height: wp(18),
      backgroundColor: '#1e90ff',
      borderRadius: wp(9),
      borderWidth: wp(1.5),
      borderColor: novelDesign.color.bg.surface,
      justifyContent: 'center',
      alignItems: 'center',
    },
    vBadgeText: {
      color: novelDesign.color.text.inverse,
      fontSize: fp(8),
      fontWeight: '600',
    },

    userInfo: {
      flex: 1,
      minWidth: 0,
    },
    userNameRow: {
      flexDirection: 'row',
      alignItems: 'center',
      marginBottom: wp(2),
    },
    userName: {
      fontSize: fp(15),
      color: novelDesign.color.text.primary,
      fontWeight: '500',
    },
    userTag: {
      marginLeft: wp(4),
      marginRight: wp(2),
      paddingHorizontal: wp(4),
      paddingVertical: wp(1),
      borderRadius: sp(3),
      justifyContent: 'center',
      alignItems: 'center',
    },
    tagsContainer: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      alignItems: 'center',
      marginLeft: wp(4),
    },
    userTagText: {
      fontSize: fp(8),
      color: novelDesign.color.text.inverse,
      fontWeight: '600',
    },
    userDescription: {
      fontSize: fp(10),
      color: novelDesign.color.text.secondary,
      marginTop: wp(4),
    },

    followButton: {
      paddingHorizontal: wp(8),
      paddingVertical: wp(4),
      borderRadius: sp(22),
      backgroundColor: novelDesign.color.bg.surface,
      flexDirection: 'row',
      alignItems: 'center',
      minWidth: wp(70),
      justifyContent: 'center',
    },
    followedButton: {
      backgroundColor: novelDesign.color.bg.surface,
    },
    followButtonText: {
      fontSize: fp(14),
      color: novelDesign.color.brand.primary,
      fontWeight: '600',
    },
    followedButtonText: {
      fontSize: fp(14),
      color: novelDesign.color.brand.primary,
      fontWeight: '600',
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
