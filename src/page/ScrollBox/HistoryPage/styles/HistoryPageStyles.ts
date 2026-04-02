import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createHistoryPageStyles = (colors: NovelColors) => {
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
    searchButton: {
      justifyContent: 'center',
      alignItems: 'center',
      minWidth: wp(32),
      minHeight: wp(32),
    },

    tabsContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      paddingHorizontal: wp(12),
      paddingVertical: wp(6),
      backgroundColor: novelDesign.color.bg.surface,
    },
    tabsScrollArea: {
      flex: 1,
    },
    tabsScroll: {
      flexDirection: 'row',
      paddingRight: wp(4),
    },
    tab: {
      paddingHorizontal: wp(12),
      paddingVertical: wp(6),
      alignItems: 'center',
    },
    activeTab: {},
    tabText: {
      ...typography.labelLarge,
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

    viewControls: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: wp(6),
    },
    viewToggleButton: {
      paddingLeft: wp(12),
      paddingVertical: wp(6),
      alignItems: 'center',
    },
    viewToggleText: {
      ...typography.labelLarge,
      color: novelDesign.color.text.primary,
      fontWeight: '600',
    },
    verticalDivider: {
      width: 0.5,
      height: wp(20),
      backgroundColor: novelDesign.color.border.subtle,
    },
    staticButton: {
      paddingRight: wp(12),
      paddingVertical: wp(6),
      alignItems: 'center',
    },
    staticButtonText: {
      ...typography.labelLarge,
      color: novelDesign.color.text.primary,
      fontWeight: '600',
    },

    contentContainer: {
      flex: 1,
      paddingHorizontal: wp(16),
    },
    scrollView: {
      flex: 1,
    },
    scrollContent: {
      paddingBottom: wp(32),
    },

    gridContainer: {
      paddingTop: wp(12),
    },
    gridColumns: {
      flexDirection: 'row',
      justifyContent: 'space-between',
    },
    gridColumnLeft: {
      flex: 1,
      marginRight: 4,
    },
    gridColumnMiddle: {
      flex: 1,
      marginHorizontal: 2,
    },
    gridColumnRight: {
      flex: 1,
      marginLeft: 4,
    },
    gridColumnItem: {
      marginBottom: 12,
    },
    gridRow: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      marginBottom: wp(12),
    },
    gridItem: {
      width: (wp(350) - wp(32) - wp(12)) / 3,
    },
    gridCover: {
      width: '100%',
      height: wp(135),
      borderRadius: sp(8),
      overflow: 'hidden',
      backgroundColor: novelDesign.color.bg.elevated,
    },
    gridCoverImage: {
      width: '100%',
      height: '100%',
    },
    gridInfo: {
      paddingTop: wp(10),
    },
    gridTitle: {
      fontSize: wp(14),
      fontWeight: '600',
      color: novelDesign.color.text.primary,
      numberOfLines: 2,
      ellipsizeMode: 'tail',
      lineHeight: fp(18),
      marginBottom: wp(4),
    },
    gridAuthor: {
      paddingTop: wp(8),
      fontSize: wp(10),
      color: novelDesign.color.text.secondary,
      numberOfLines: 1,
      marginBottom: wp(4),
    },
    gridProgress: {
      ...typography.labelSmall,
      color: novelDesign.color.brand.primary,
      fontSize: fp(11),
    },

    listContainer: {
      paddingTop: wp(12),
    },
    listItem: {
      flexDirection: 'row',
      padding: wp(12),
    },
    listCover: {
      width: wp(65),
      height: wp(87),
      borderRadius: sp(6),
      backgroundColor: novelDesign.color.bg.elevated,
      marginRight: wp(12),
    },
    listCoverImage: {
      width: '100%',
      height: '100%',
      borderRadius: sp(6),
    },
  });
};
