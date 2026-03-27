import { StyleSheet } from 'react-native';
import { wp, fp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

/**
 * 创建帮助与支持页面样式
 * @param colors 当前主题下的颜色配置
 * @returns StyleSheet 样式表
 */
export const createHelpSupportPageStyles = (colors: NovelColors) => {
  return StyleSheet.create({
    // === 容器样式 ===
    container: {
      flex: 1,
      backgroundColor: colors.novelBackground,
    },

    scrollView: {
      flex: 1,
    },

    scrollContent: {
      paddingVertical: wp(10),
    },

    // === 顶部导航栏样式 ===
    topBar: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(20),
      paddingVertical: wp(16),
      backgroundColor: colors.novelBackground,
      borderBottomWidth: 0.5,
      borderBottomColor: colors.novelDivider,
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
      color: colors.novelText,
      fontWeight: '300',
      lineHeight: fp(28),
    },

    titleContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },

    topBarTitle: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      color: colors.novelText,
      fontWeight: '600',
      textAlign: 'center',
    },

    rightPlaceholder: {
      width: wp(32),
      height: wp(32),
    },

    // === 内容样式 ===
    descriptionContainer: {
      paddingHorizontal: wp(20),
      paddingVertical: wp(20),
      backgroundColor: colors.novelBackground,
    },

    descriptionText: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelTextGray,
      lineHeight: fp(20),
    },
    descriptionSectionTitle: {
      marginTop: 16,
    },
    descriptionClosingText: {
      marginTop: 24,
    },
  });
};
