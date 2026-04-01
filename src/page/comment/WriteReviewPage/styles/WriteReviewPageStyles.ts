import { StyleSheet } from 'react-native';
import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';

export const createWriteReviewPageStyles = (colors: NovelColors) => {
  const novelDesign = createNovelDesignUI(colors);
  return StyleSheet.create({
    // 容器样式
    container: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.canvas,
    },

    // TopBar样式
    topBar: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: 12,
      paddingVertical: 4,
      backgroundColor: novelDesign.color.bg.surface,
    },

    closeButton: {
      width: 40,
      height: 40,
      alignItems: 'center',
      justifyContent: 'center',
    },

    closeIcon: {
      fontSize: 30,
      color: colors.novelText,
      fontWeight: '400',
    },

    titleContainer: {
      flex: 1,
      alignItems: 'center',
    },

    topBarTitle: {
      fontSize: 18,
      fontWeight: '500',
      color: colors.novelText,
    },

    submitButton: {
      paddingHorizontal: 16,
      paddingVertical: 4,
      backgroundColor: colors.novelMain,
      borderRadius: 16,
      alignItems: 'center',
    },

    submitButtonText: {
      fontSize: 10,
      fontWeight: '600',
      color: colors.novelBackground,
    },

    scrollContainer: {
      flex: 1,
    },

    contentContainer: {
    },

    // 评分区域
    ratingContainer: {
      backgroundColor: novelDesign.color.bg.surface,
      alignItems: 'center',
    },

    sectionTitle: {
      fontSize: 16,
      fontWeight: '500',
      color: colors.novelText,
      marginBottom: 16,
      textAlign: 'center',
    },

    starContainer: {
      flexDirection: 'row',
      justifyContent: 'center',
    },

    starButton: {
      marginHorizontal: 2,
    },

    starButtonSelected: {
      transform: [{ scale: 1.1 }],
    },

    starText: {
      fontSize: 32,
      color: colors.novelLightGray,
    },

    starTextSelected: {
      color: colors.novelMain,
    },

    ratingLabel: {
      fontSize: 14,
      color: colors.novelMain,
      marginTop: 2,
      fontWeight: '500',
    },

    // 表单区域
    formContainer: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.surface,
      paddingHorizontal: 12,
      paddingVertical: 0,
      marginBottom: 0,
    },

    divider: {
      height: 1,
      backgroundColor: colors.novelDivider,
      marginVertical: 16,
    },

    inputSection: {
      flex: 1,
    },

    inputHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: 8,
    },

    charCount: {
      fontSize: 12,
      color: colors.novelTextGray,
    },

    charCountError: {
      color: colors.novelError,
    },

    titleInput: {
      borderWidth: 0,
      borderBottomWidth: 1,
      borderBottomColor: colors.novelDivider,
      borderRadius: 0,
      paddingHorizontal: 0,
      paddingVertical: 12,
      fontSize: 16,
      color: colors.novelText,
      backgroundColor: 'transparent',
      minHeight: 44,
    },

    contentInput: {
      flex: 1,
      borderWidth: 0,
      fontSize: 16,
      color: colors.novelText,
      backgroundColor: colors.novelBackground,
      textAlignVertical: 'top',
    },

    inputError: {
      borderColor: colors.novelError,
    },

    errorText: {
      fontSize: 12,
      color: colors.novelError,
      marginTop: 4,
    },

    // 提交按钮区域
    submitContainer: {
      position: 'absolute',
      bottom: 0,
      left: 0,
      right: 0,
      backgroundColor: novelDesign.color.bg.surface,
      borderTopWidth: 1,
      borderTopColor: novelDesign.color.border.subtle,
      padding: 16,
    },

    // 提示区域
    tipsContainer: {
      backgroundColor: novelDesign.color.bg.elevated,
      borderRadius: 8,
      marginHorizontal: 16,
      marginBottom: 24,
      overflow: 'hidden',
    },

    tipsHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: 16,
      backgroundColor: colors.novelSecondaryBackground,
    },

    tipsTitle: {
      fontSize: 14,
      fontWeight: '500',
      color: colors.novelText,
    },

    tipsToggle: {
      fontSize: 12,
      color: colors.novelTextGray,
      transform: [{ rotate: '0deg' }],
    },

    tipsToggleExpanded: {
      transform: [{ rotate: '180deg' }],
    },

    tipsContent: {
      padding: 16,
      paddingTop: 0,
      backgroundColor: colors.novelSecondaryBackground,
    },

    tipItem: {
      flexDirection: 'row',
      alignItems: 'flex-start',
      marginBottom: 8,
    },

    tipBullet: {
      fontSize: 14,
      color: colors.novelMain,
      marginRight: 8,
      marginTop: 2,
    },

    tipText: {
      flex: 1,
      fontSize: 14,
      color: colors.novelTextGray,
      lineHeight: 20,
    },
  });
};
