import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../design-system/novelDesign';
import { NovelColors } from '../../../utils/theme/colors';
import { wp, fp, sp } from '../../../utils/theme/dimensions';

export const createMainPageStyles = (colors: NovelColors) => {
  const novelDesign = createNovelDesignUI(colors);

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.canvas,
    },

    tabBarContainer: {
      backgroundColor: novelDesign.color.bg.surface,
      borderBottomWidth: 1,
      borderBottomColor: novelDesign.color.border.subtle,
      paddingHorizontal: wp(14),
      paddingTop: wp(novelDesign.space['150']),
      paddingBottom: wp(novelDesign.space['100']),
    },
    tabsContainer: {
      flexDirection: 'row',
      alignItems: 'flex-end',
    },
    tab: {
      marginRight: wp(18),
    },
    activeTab: {},
    tabText: {
      fontSize: fp(16),
      color: novelDesign.color.text.secondary,
      fontWeight: '400',
    },
    activeTabText: {
      fontSize: fp(18),
      color: novelDesign.color.text.primary,
      fontWeight: '600',
    },

    contentContainer: {
      flex: 1,
    },

    pageContainer: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.surface,
    },
    pageVisible: {
      display: 'flex',
    },
    pageHidden: {
      display: 'none',
    },

    loadingContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      backgroundColor: novelDesign.color.bg.surface,
      borderColor: novelDesign.color.border.subtle,
      borderWidth: 1,
      borderRadius: sp(novelDesign.radius.xl),
      margin: wp(novelDesign.space['200']),
    },
    loadingText: {
      fontSize: fp(16),
      color: novelDesign.color.text.secondary,
      marginTop: wp(16),
    },

    errorContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingHorizontal: wp(40),
      margin: wp(novelDesign.space['200']),
      backgroundColor: novelDesign.color.bg.surface,
      borderColor: novelDesign.color.border.subtle,
      borderWidth: 1,
      borderRadius: sp(novelDesign.radius.xl),
    },
    errorIcon: {
      fontSize: fp(48),
      marginBottom: wp(16),
    },
    errorText: {
      fontSize: fp(16),
      color: novelDesign.color.text.primary,
      fontWeight: '600',
      marginBottom: wp(8),
      textAlign: 'center',
    },
    errorDescription: {
      fontSize: fp(14),
      color: novelDesign.color.text.secondary,
      textAlign: 'center',
      lineHeight: fp(20),
    },
  });
};
