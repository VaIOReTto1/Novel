import { StyleSheet } from 'react-native';

import { resolveNovelDesignTheme } from '../../../design-system/tokens/resolveNovelDesignTheme';
import { NovelColors } from '../../../utils/theme/colors';
import { wp, fp, sp } from '../../../utils/theme/dimensions';

export const createCategoryPageStyles = (colors: NovelColors) => {
  const novelDesign = resolveNovelDesignTheme(colors.novelBackground);

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.canvas,
    },

    topTabs: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: wp(novelDesign.space['150']),
      gap: wp(36),
      backgroundColor: novelDesign.color.bg.surface,
      borderBottomWidth: 1,
      borderBottomColor: novelDesign.color.border.subtle,
    },

    topTabText: {
      fontSize: fp(16),
      color: novelDesign.color.text.secondary,
      fontWeight: '500',
    },

    topTabActive: {
      color: novelDesign.color.text.primary,
      fontWeight: '600',
      fontSize: fp(18),
    },

    body: {
      flex: 1,
      flexDirection: 'row',
    },

    sidebar: {
      width: wp(100),
      flexShrink: 0,
      flexGrow: 0,
      backgroundColor: novelDesign.color.bg.surface,
      borderRightWidth: 1,
      borderRightColor: novelDesign.color.border.subtle,
    },

    sidebarItem: {
      paddingVertical: wp(18),
      paddingHorizontal: wp(12),
    },

    sidebarItemActive: {
      fontWeight: '600',
      color: novelDesign.color.brand.primary,
    },

    sidebarText: {
      fontSize: fp(14),
      color: novelDesign.color.text.primary,
      textAlign: 'center',
    },

    list: {
      flex: 1,
      borderTopLeftRadius: sp(novelDesign.radius.lg),
      backgroundColor: novelDesign.color.bg.surface,
    },

    gridContainer: {
      paddingHorizontal: wp(12),
      paddingVertical: wp(12),
      backgroundColor: novelDesign.color.bg.surface,
    },

    gridItem: {
      flex: 1,
      margin: wp(6),
      backgroundColor: novelDesign.color.bg.surface,
    },

    card: {
      borderRadius: sp(novelDesign.radius.lg),
      borderWidth: 1,
      borderColor: novelDesign.color.border.subtle,
      backgroundColor: novelDesign.color.bg.surface,
      overflow: 'hidden',
    },

    cover: {
      width: '100%',
      aspectRatio: 0.75,
      backgroundColor: novelDesign.color.bg.elevated,
      borderRadius: sp(novelDesign.radius.lg),
    },

    titleBox: {
      paddingVertical: wp(6),
      paddingHorizontal: wp(6),
    },

    title: {
      fontSize: fp(12),
      color: novelDesign.color.text.primary,
      fontWeight: '600',
      textAlign: 'center',
    },

    gridItemWrapper: {
      padding: wp(8),
    },

    loadingBox: {
      paddingVertical: wp(24),
    },

    loadingContainer: {
      flex: 1,
      flexDirection: 'row',
      justifyContent: 'center',
      alignItems: 'center',
    },

    loadingText: {
      textAlign: 'center',
      opacity: 0.6,
    },

    columnWrapperStart: {
      justifyContent: 'flex-start',
      flexWrap: 'wrap',
    },

    listFooterSpacer: {
      paddingBottom: wp(12),
    },

    footerCenter: {
      alignItems: 'center',
    },

    footerRow: {
      flexDirection: 'row',
      alignItems: 'center',
    },

    endLine: {
      width: wp(30),
      height: wp(1),
      backgroundColor: novelDesign.color.border.subtle,
      marginHorizontal: wp(6),
    },

    endText: {
      fontSize: fp(12),
      color: novelDesign.color.text.secondary,
    },
  });
};
