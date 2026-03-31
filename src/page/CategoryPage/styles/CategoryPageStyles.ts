import { StyleSheet } from 'react-native';

import { resolveStage7Theme } from '../../../design-system/tokens/resolveStage7Theme';
import { NovelColors } from '../../../utils/theme/colors';
import { wp, fp, sp } from '../../../utils/theme/dimensions';

export const createCategoryPageStyles = (colors: NovelColors) => {
  const stage7 = resolveStage7Theme(colors.novelBackground);

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: stage7.color.bg.canvas,
    },

    topTabs: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: wp(stage7.space['150']),
      gap: wp(36),
      backgroundColor: stage7.color.bg.surface,
      borderBottomWidth: 1,
      borderBottomColor: stage7.color.border.subtle,
    },

    topTabText: {
      fontSize: fp(16),
      color: stage7.color.text.secondary,
      fontWeight: '500',
    },

    topTabActive: {
      color: stage7.color.text.primary,
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
      backgroundColor: stage7.color.bg.surface,
      borderRightWidth: 1,
      borderRightColor: stage7.color.border.subtle,
    },

    sidebarItem: {
      paddingVertical: wp(18),
      paddingHorizontal: wp(12),
    },

    sidebarItemActive: {
      fontWeight: '600',
      color: stage7.color.brand.primary,
    },

    sidebarText: {
      fontSize: fp(14),
      color: stage7.color.text.primary,
      textAlign: 'center',
    },

    list: {
      flex: 1,
      borderTopLeftRadius: sp(stage7.radius.lg),
      backgroundColor: stage7.color.bg.surface,
    },

    gridContainer: {
      paddingHorizontal: wp(12),
      paddingVertical: wp(12),
      backgroundColor: stage7.color.bg.surface,
    },

    gridItem: {
      flex: 1,
      margin: wp(6),
      backgroundColor: stage7.color.bg.surface,
    },

    card: {
      borderRadius: sp(stage7.radius.lg),
      borderWidth: 1,
      borderColor: stage7.color.border.subtle,
      backgroundColor: stage7.color.bg.surface,
      overflow: 'hidden',
    },

    cover: {
      width: '100%',
      aspectRatio: 0.75,
      backgroundColor: stage7.color.bg.elevated,
      borderRadius: sp(stage7.radius.lg),
    },

    titleBox: {
      paddingVertical: wp(6),
      paddingHorizontal: wp(6),
    },

    title: {
      fontSize: fp(12),
      color: stage7.color.text.primary,
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
      backgroundColor: stage7.color.border.subtle,
      marginHorizontal: wp(6),
    },

    endText: {
      fontSize: fp(12),
      color: stage7.color.text.secondary,
    },
  });
};
