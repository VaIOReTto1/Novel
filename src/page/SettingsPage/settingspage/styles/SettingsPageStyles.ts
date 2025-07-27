import { StyleSheet } from 'react-native';
import { wp, fp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

/**
 * 创建设置页面样式
 * @param colors 当前主题下的颜色配置
 * @returns StyleSheet 样式表
 */
export const createSettingsPageStyles = (colors: NovelColors) => {
  // 已不再需要独立的 isDarkMode 变量，颜色直接从主题中获取

  // 动态颜色配置（使用全局主题色）
  const switchActiveColor = colors.novelMain; // 开关激活色
  const switchInactiveColor = colors.novelDivider; // 开关未激活背景
  const switchThumbColor = colors.novelSecondaryBackground; // 开关滑块颜色
  const pressedBackgroundColor = colors.novelChipBackground; // 列表项按压背景
  const dangerBackgroundColor = colors.novelChipBackground; // 危险操作背景
  const dangerBorderColor = colors.novelError; // 危险操作边框
  const dangerTextColor = colors.novelError; // 危险文本颜色

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

    // === 设置项样式 ===
    settingRow: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(20),
      paddingVertical: wp(16),
      backgroundColor: colors.novelSecondaryBackground,
      borderBottomWidth: 0.5,
      borderBottomColor: colors.novelDivider,
      minHeight: wp(56),
    },

    settingRowPressed: {
      backgroundColor: pressedBackgroundColor,
    },

    settingLeft: {
      flex: 1,
      flexDirection: 'row',
      alignItems: 'center',
    },

    settingIcon: {
      marginRight: wp(15),
      justifyContent: 'center',
      alignItems: 'center',
    },

    settingTitle: {
      ...typography.bodyMedium,
      fontSize: fp(16),
      color: colors.novelText,
      flex: 1,
      fontWeight: '400',
    },

    settingSubtitle: {
      ...typography.labelSmall,
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginTop: wp(2),
    },

    settingRight: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'flex-end',
    },

    settingValue: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginRight: wp(8),
      textAlign: 'right',
    },

    arrow: {
      fontSize: fp(18),
      color: colors.novelTextGray,
      fontWeight: '300',
    },

    // === 开关组件样式 ===
    switch: {
      transform: [{ scale: 0.9 }],
    },

    // === 主题切换按钮样式 ===
    themeToggleButton: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingHorizontal: wp(12),
      paddingVertical: wp(6),
      borderRadius: wp(16),
      backgroundColor: colors.novelChipBackground,
      borderWidth: 1,
      borderColor: colors.novelDivider,
      minWidth: wp(60),
    },

    themeToggleText: {
      fontSize: fp(18),
      textAlign: 'center',
    },

    // === 分组标题样式 ===
    sectionHeader: {
      paddingHorizontal: wp(20),
      paddingTop: wp(20),
      paddingBottom: wp(10),
      backgroundColor: colors.novelBackground,
    },

    sectionTitle: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelTextGray,
      fontWeight: '600',
      textTransform: 'uppercase',
      letterSpacing: 0.5,
    },

    // === 缓存大小显示样式 ===
    cacheSize: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelMain,
      marginRight: wp(8),
      fontWeight: '500',
    },

    // === 字体大小控制样式 ===
    fontSizeContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
    },

    fontSizeButton: {
      width: wp(32),
      height: wp(32),
      borderRadius: wp(16),
      backgroundColor: colors.novelMain,
      justifyContent: 'center',
      alignItems: 'center',
      marginHorizontal: wp(5),
      elevation: 2,
      shadowColor: colors.novelText,
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.1,
      shadowRadius: 2,
    },

    fontSizeButtonDisabled: {
      backgroundColor: colors.novelTextGray,
      opacity: 0.5,
      elevation: 0,
      shadowOpacity: 0,
    },

    fontSizeButtonText: {
      color: colors.novelSecondaryBackground,
      fontSize: fp(16),
      fontWeight: '600',
    },

    fontSizeDisplay: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelText,
      minWidth: wp(40),
      textAlign: 'center',
      marginHorizontal: wp(8),
      fontWeight: '500',
    },

    // === 时间选择器样式 ===
    timePickerContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: wp(20),
    },

    timePickerText: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelMain,
      marginHorizontal: wp(4),
      fontWeight: '500',
    },

    // === 状态样式 ===
    emptyContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: wp(50),
    },

    emptyText: {
      ...typography.bodyMedium,
      color: colors.novelTextGray,
      textAlign: 'center',
    },

    loadingContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: wp(20),
    },

    loadingText: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginLeft: wp(8),
    },

    // === 特殊状态样式 ===
    dangerRow: {
      backgroundColor: dangerBackgroundColor,
      borderBottomColor: dangerBorderColor,
    },

    dangerTitle: {
      color: dangerTextColor,
      fontWeight: '500',
    },

    disabledRow: {
      opacity: 0.6,
    },

    disabledTitle: {
      color: colors.novelTextGray,
    },

    // === 开关样式扩展 ===
    customSwitch: {
      backgroundColor: switchInactiveColor,
      borderRadius: 15,
      width: 50,
      height: 30,
      justifyContent: 'center',
      paddingHorizontal: 2,
      elevation: 1,
      shadowColor: colors.novelText,
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.1,
      shadowRadius: 1,
    },

    customSwitchActive: {
      backgroundColor: switchActiveColor,
    },

    customSwitchThumb: {
      width: 26,
      height: 26,
      borderRadius: 13,
      backgroundColor: switchThumbColor,
      elevation: 2,
      shadowColor: colors.novelText,
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.2,
      shadowRadius: 2,
    },

    customSwitchThumbActive: {
      alignSelf: 'flex-end',
    },

    customSwitchThumbInactive: {
      alignSelf: 'flex-start',
    },

    // === 时间选择器模态框样式 ===
    modalOverlay: {
      position: 'absolute',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      flex: 1,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      justifyContent: 'flex-end',
      zIndex: 1000,
    },

    modalContainer: {
      backgroundColor: colors.novelSecondaryBackground,
      borderTopLeftRadius: wp(20),
      borderTopRightRadius: wp(20),
      maxHeight: '80%',
    },

    modalHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      paddingHorizontal: wp(20),
      paddingVertical: wp(16),
      borderBottomWidth: 0.5,
      borderBottomColor: colors.novelDivider,
    },

    modalTitle: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      color: colors.novelText,
      fontWeight: '600',
    },

    modalCancelText: {
      ...typography.labelLarge,
      fontSize: fp(16),
      color: colors.novelTextGray,
    },

    modalConfirmText: {
      ...typography.labelLarge,
      fontSize: fp(16),
      color: colors.novelMain,
      fontWeight: '600',
    },

    modalContent: {
      padding: wp(20),
    },

    timeSection: {
      marginBottom: wp(30),
    },

    timeSectionTitle: {
      ...typography.bodyMedium,
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '600',
      marginBottom: wp(15),
    },

    timeColumn: {
      alignItems: 'center',
      marginHorizontal: wp(20),
    },

    timeLabel: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginBottom: wp(10),
    },

    timeScrollView: {
      maxHeight: wp(120),
      width: wp(60),
    },

    timeItem: {
      paddingVertical: wp(8),
      paddingHorizontal: wp(12),
      borderRadius: wp(8),
      marginVertical: wp(2),
      alignItems: 'center',
    },

    timeItemSelected: {
      backgroundColor: colors.novelMain,
    },

    timeItemText: {
      ...typography.labelLarge,
      fontSize: fp(16),
      color: colors.novelText,
    },

    timeItemTextSelected: {
      color: colors.novelSecondaryBackground,
      fontWeight: '600',
    },

    timeSeparator: {
      ...typography.bodyMedium,
      fontSize: fp(24),
      color: colors.novelText,
      fontWeight: '600',
    },

    timeHint: {
      marginTop: wp(20),
      padding: wp(15),
      backgroundColor: colors.novelChipBackground,
      borderRadius: wp(10),
    },

    timeHintText: {
      ...typography.labelLarge,
      fontSize: fp(14),
      color: colors.novelTextGray,
      textAlign: 'center',
      lineHeight: fp(20),
    },

    // === 定时切换页面样式 ===
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

    settingsContainer: {
      backgroundColor: colors.novelSecondaryBackground,
    },

    // ===== 退出登录弹窗样式 =====
    logoutModalOverlay: {
      position: 'absolute',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000,
    },
    logoutModalContainer: {
      backgroundColor: colors.novelBackground,
      borderRadius: wp(16),
      marginHorizontal: wp(32),
      paddingTop: wp(24),
      paddingBottom: wp(20),
      paddingHorizontal: wp(24),
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.25,
      shadowRadius: 8,
      elevation: 10,
      position: 'relative',
    },
    logoutModalTitle: {
      marginTop: wp(8),
      fontSize: fp(18),
      fontWeight: 'bold',
      color: colors.novelText,
      textAlign: 'center',
      marginBottom: wp(16),
    },
    logoutModalDescription: {
      fontSize: fp(14),
      color: colors.novelText,
      textAlign: 'center',
      lineHeight: fp(20),
      marginBottom: wp(25),
    },
    logoutModalButtonContainer: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      gap: wp(12),
    },
    logoutModalButton: {
      flex: 1,
      paddingVertical: wp(12),
      borderRadius: wp(24),
      alignItems: 'center',
    },
    logoutModalCancelButton: {
      backgroundColor: colors.novelChipBackground,
      borderWidth: 1,
      borderColor: colors.novelDivider,
    },
    logoutModalConfirmButton: {
      backgroundColor: colors.novelMain,
    },
    logoutModalCancelButtonText: {
      color: colors.novelText,
      fontSize: fp(16),
      fontWeight: '500',
    },
    logoutModalConfirmButtonText: {
      color: colors.novelBackground,
      fontSize: fp(16),
      fontWeight: 'bold',
    },
  });
};
