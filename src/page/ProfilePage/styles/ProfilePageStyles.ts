import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../design-system/novelDesign';
import { wp, fp, sp } from '../../../utils/theme/dimensions';
import { NovelColors } from '../../../utils/theme/colors';
import { typography } from '../../../utils/theme/typography';
import { PAGE_WIDTH } from '../utils/constants';

export const createHomePageStyles = (colors: NovelColors) => {
  const novelDesign = createNovelDesignUI(colors);

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.canvas,
      paddingHorizontal: wp(novelDesign.space['200']),
    },

    scrollView: {
      flex: 1,
    },

    scrollContent: {
      paddingBottom: wp(100),
    },

    topBar: {
      flexDirection: 'row',
      justifyContent: 'flex-end',
      alignItems: 'center',
      paddingTop: wp(novelDesign.space['200']),
      paddingBottom: wp(novelDesign.space['100']),
      gap: wp(novelDesign.space['150']),
    },
    topBarButton: {
      width: sp(40),
      height: sp(40),
      borderRadius: sp(novelDesign.radius.full),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      backgroundColor: novelDesign.color.bg.surface,
      alignItems: 'center',
      justifyContent: 'center',
    },

    loginBar: {
      flexDirection: 'row',
      alignItems: 'center',
      backgroundColor: novelDesign.color.bg.surface,
      borderColor: novelDesign.color.border.subtle,
      borderRadius: sp(novelDesign.radius.xl),
      borderWidth: 1,
      marginTop: wp(novelDesign.space['100']),
      paddingHorizontal: wp(novelDesign.space['200']),
      paddingVertical: wp(novelDesign.space['200']),
      gap: wp(15),
    },
    avatar: {
      width: sp(50),
      height: sp(50),
      borderRadius: sp(25),
      overflow: 'hidden',
    },
    avatarImage: {
      width: '100%',
      height: '100%',
      backgroundColor: novelDesign.color.bg.elevated,
      borderRadius: sp(25),
    },
    defaultAvatar: {
      width: '100%',
      height: '100%',
      backgroundColor: novelDesign.color.bg.elevated,
      borderRadius: sp(25),
    },
    loginButton: {
      flex: 1,
    },
    loginText: {
      ...typography.bodyMedium,
      color: novelDesign.color.text.primary,
    },

    scrollableContainer: {
      width: PAGE_WIDTH,
      alignItems: 'center',
      backgroundColor: novelDesign.color.bg.surface,
      borderColor: novelDesign.color.border.subtle,
      borderRadius: sp(novelDesign.radius.xl),
      borderWidth: 1,
      marginVertical: wp(10),
      paddingBottom: wp(novelDesign.space['200']),
    },
    scrollArea: {
      width: PAGE_WIDTH,
      borderRadius: sp(novelDesign.radius.xl),
      overflow: 'hidden',
      paddingTop: wp(novelDesign.space['200']),
    },
    page: {
      paddingHorizontal: wp(20),
      width: PAGE_WIDTH,
    },

    firstPageIcons: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      justifyContent: 'space-around',
      gap: wp(10),
      marginBottom: wp(10),
    },
    gridContainer: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      justifyContent: 'space-around',
      gap: wp(10),
    },
    lastPageContainer: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      justifyContent: 'space-around',
      gap: wp(10),
    },
    iconItem: {
      width: wp(60),
      alignItems: 'center',
      marginBottom: wp(15),
    },
    iconText: {
      ...typography.labelSmall,
      color: novelDesign.color.text.primary,
      textAlign: 'center',
      lineHeight: fp(16),
      marginTop: wp(5),
    },

    pageIndicator: {
      flexDirection: 'row',
      justifyContent: 'center',
      gap: wp(8),
    },
    dot: {
      width: sp(3.5),
      height: sp(3.5),
      borderRadius: sp(3.5),
      backgroundColor: novelDesign.color.brand.primary,
    },

    advertisement: {
      flexDirection: 'row',
      alignItems: 'center',
      backgroundColor: novelDesign.color.bg.elevated,
      borderColor: novelDesign.color.border.subtle,
      borderRadius: sp(novelDesign.radius.lg),
      borderWidth: 1,
      padding: wp(novelDesign.space['200']),
      gap: wp(10),
    },
    adBookCover: {
      width: wp(30),
      height: wp(20),
      backgroundColor: novelDesign.color.brand.primary,
      borderRadius: sp(5),
    },
    adContent: {
      flex: 1,
      gap: wp(5),
    },
    adTitle: {
      ...typography.labelLarge,
      fontWeight: '600',
      color: novelDesign.color.text.primary,
      lineHeight: fp(18),
    },
    adAuthor: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
      lineHeight: fp(16),
      marginBottom: wp(5),
    },
    continueReading: {
      paddingHorizontal: wp(10),
      paddingVertical: wp(5),
    },
    continueText: {
      ...typography.labelSmall,
      color: novelDesign.color.brand.primary,
      fontWeight: '500',
    },

    bottomBox: {
      width: PAGE_WIDTH,
      height: wp(200),
      backgroundColor: novelDesign.color.bg.surface,
      borderColor: novelDesign.color.border.subtle,
      borderRadius: sp(novelDesign.radius.xl),
      borderWidth: 1,
      padding: wp(novelDesign.space['200']),
      alignSelf: 'center',
      marginVertical: wp(10),
    },
    balanceRow: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: wp(20),
    },
    balanceText: {
      ...typography.labelLarge,
      color: novelDesign.color.text.primary,
      fontWeight: '500',
    },
    withdrawButton: {
      paddingHorizontal: wp(10),
      paddingVertical: wp(5),
    },
    withdrawText: {
      ...typography.labelSmall,
      color: novelDesign.color.brand.primary,
      fontWeight: '500',
    },
    bottomAd: {
      flex: 1,
      justifyContent: 'center',
    },

    waterfallGrid: {
      flexDirection: 'row',
      gap: wp(15),
    },
    waterfallColumn: {
      flex: 1,
    },
    waterfallBookItem: {
      backgroundColor: novelDesign.color.bg.surface,
      borderColor: novelDesign.color.border.subtle,
      borderRadius: sp(novelDesign.radius.lg),
      borderWidth: 1,
      marginBottom: wp(10),
      shadowColor: novelDesign.color.text.primary,
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.08,
      shadowRadius: 6,
      elevation: 2,
      overflow: 'hidden',
    },
    waterfallBookCover: {
      width: '100%',
      backgroundColor: novelDesign.color.bg.elevated,
    },
    waterfallCoverImage: {
      width: '100%',
      height: '100%',
    },
    waterfallPlaceholderCover: {
      width: '100%',
      height: '100%',
      backgroundColor: novelDesign.color.brand.accent,
      justifyContent: 'center',
      alignItems: 'center',
    },
    waterfallPlaceholderText: {
      fontSize: fp(12),
      color: novelDesign.color.text.primary,
      opacity: 0.6,
    },
    waterfallBookInfo: {
      padding: wp(10),
    },
    waterfallBookTitle: {
      ...typography.labelLarge,
      fontWeight: '600',
      color: novelDesign.color.text.primary,
      lineHeight: fp(18),
      marginBottom: wp(5),
    },
    waterfallBookAuthor: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
      lineHeight: fp(16),
      marginBottom: wp(5),
    },
    waterfallBookDescription: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
      lineHeight: fp(16),
      marginBottom: wp(5),
    },

    emptyContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: wp(50),
    },
    emptyText: {
      ...typography.bodyMedium,
      color: novelDesign.color.text.primary,
      opacity: 0.6,
    },

    waterfallLoadingContainer: {
      flexDirection: 'row',
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: wp(20),
      gap: wp(8),
    },
    waterfallLoadingText: {
      ...typography.labelLarge,
      color: novelDesign.color.text.secondary,
    },
    waterfallEndLine: {
      width: wp(30),
      height: wp(1),
      backgroundColor: novelDesign.color.border.subtle,
    },
    waterfallEndText: {
      ...typography.labelSmall,
      color: novelDesign.color.text.secondary,
      opacity: 0.7,
      marginHorizontal: wp(10),
    },

    refreshIndicator: {
      backgroundColor: novelDesign.color.bg.elevated,
      paddingVertical: wp(15),
      paddingHorizontal: wp(20),
      borderBottomWidth: 1,
      borderBottomColor: novelDesign.color.border.subtle,
      marginBottom: wp(10),
    },
    refreshContent: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
    },
    loadingSpinner: {
      marginRight: wp(10),
    },
    spinnerText: {
      fontSize: fp(14),
      color: novelDesign.color.text.primary,
      fontWeight: '500',
    },
    refreshText: {
      fontSize: fp(14),
      color: novelDesign.color.text.primary,
      fontWeight: '500',
    },

    bottomLoadingContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: wp(20),
    },
    loadingText: {
      fontSize: fp(14),
      color: novelDesign.color.text.secondary,
      fontWeight: '500',
    },
  });
};
