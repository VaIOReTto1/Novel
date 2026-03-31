import { StyleSheet } from 'react-native';

import { resolveStage7Theme } from '../../../design-system/tokens/resolveStage7Theme';
import { NovelColors } from '../../../utils/theme/colors';
import { wp, fp, sp } from '../../../utils/theme/dimensions';

export const createMainPageStyles = (colors: NovelColors) => {
  const stage7 = resolveStage7Theme(colors.novelBackground);

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: stage7.color.bg.canvas,
    },

    tabBarContainer: {
      backgroundColor: stage7.color.bg.surface,
      borderBottomWidth: 1,
      borderBottomColor: stage7.color.border.subtle,
      paddingHorizontal: wp(14),
      paddingTop: wp(stage7.space['150']),
      paddingBottom: wp(stage7.space['100']),
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
      color: stage7.color.text.secondary,
      fontWeight: '400',
    },
    activeTabText: {
      fontSize: fp(18),
      color: stage7.color.text.primary,
      fontWeight: '600',
    },

    contentContainer: {
      flex: 1,
    },

    pageContainer: {
      flex: 1,
      backgroundColor: stage7.color.bg.surface,
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
      backgroundColor: stage7.color.bg.surface,
      borderColor: stage7.color.border.subtle,
      borderWidth: 1,
      borderRadius: sp(stage7.radius.xl),
      margin: wp(stage7.space['200']),
    },
    loadingText: {
      fontSize: fp(16),
      color: stage7.color.text.secondary,
      marginTop: wp(16),
    },

    errorContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingHorizontal: wp(40),
      margin: wp(stage7.space['200']),
      backgroundColor: stage7.color.bg.surface,
      borderColor: stage7.color.border.subtle,
      borderWidth: 1,
      borderRadius: sp(stage7.radius.xl),
    },
    errorIcon: {
      fontSize: fp(48),
      marginBottom: wp(16),
    },
    errorText: {
      fontSize: fp(16),
      color: stage7.color.text.primary,
      fontWeight: '600',
      marginBottom: wp(8),
      textAlign: 'center',
    },
    errorDescription: {
      fontSize: fp(14),
      color: stage7.color.text.secondary,
      textAlign: 'center',
      lineHeight: fp(20),
    },
  });
};
