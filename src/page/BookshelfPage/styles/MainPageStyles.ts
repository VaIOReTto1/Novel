import { StyleSheet } from 'react-native';
import { wp, fp } from '../../../utils/theme/dimensions';
import { NovelColors } from '../../../utils/theme/colors';

export const createMainPageStyles = (colors: NovelColors) => {
  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors.novelBackground,
    },

    // TabBar样式
    tabBarContainer: {
      backgroundColor: colors.novelBackground,
      paddingHorizontal: wp(14),
      paddingVertical: wp(8),
    },
    tabsContainer: {
      flexDirection: 'row',
      alignItems: 'flex-end',
    },
    tab: {
      marginRight: wp(18),
    },
    activeTab: {
    },
    tabText: {
      fontSize: fp(16),
      color: colors.novelTextGray,
      fontWeight: '400',
    },
    activeTabText: {
      fontSize: fp(18),
      color: colors.novelText,
      fontWeight: '600',
    },

    // 内容区域
    contentContainer: {
      flex: 1,
    },

    // 页面容器 - 用于避免组件重新挂载
    pageContainer: {
      flex: 1,
    },
    pageVisible: {
      display: 'flex',
    },
    pageHidden: {
      display: 'none',
    },

    // 加载状态
    loadingContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },
    loadingText: {
      fontSize: fp(16),
      color: colors.novelTextGray,
      marginTop: wp(16),
    },

    // 错误状态
    errorContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingHorizontal: wp(40),
    },
    errorIcon: {
      fontSize: fp(48),
      marginBottom: wp(16),
    },
    errorText: {
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '600',
      marginBottom: wp(8),
      textAlign: 'center',
    },
    errorDescription: {
      fontSize: fp(14),
      color: colors.novelTextGray,
      textAlign: 'center',
      lineHeight: fp(20),
    },
  });
};
