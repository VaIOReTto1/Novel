import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createFeedbackHelpPageStyles = (colors: NovelColors) => {
  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors.novelSecondaryBackground,
    },

    // 顶部Bar样式
    topBar: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(20),
      paddingVertical: wp(16),
      backgroundColor: colors.novelBackground,
      minHeight: wp(56),
    },
    backButton: {
      justifyContent: 'center',
      alignItems: 'center',
      minWidth: wp(32),
      minHeight: wp(32),
    },
    backArrow: {
      fontSize: fp(24),
      color: colors.novelText,
      fontWeight: '300',
    },
    titleContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },
    title: {
      ...typography.bodyMedium,
      fontSize: fp(18),
      color: colors.novelText,
      fontWeight: '600',
      textAlign: 'center',
    },
    searchButton: {
      justifyContent: 'center',
      alignItems: 'center',
      minWidth: wp(32),
      minHeight: wp(32),
    },
    searchIcon: {
      fontSize: fp(12),
      color: colors.novelText,
    },

    // 搜索容器样式
    searchContainer: {
      flex: 1,
      paddingHorizontal: wp(12),
    },
    searchInputContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      backgroundColor: '#F5F5F5', // 写死的搜索框背景色
      borderRadius: wp(20),
      paddingHorizontal: wp(12),
      paddingVertical: wp(0),
      minHeight: wp(36),
    },
    searchInput: {
      alignItems: 'center',
      fontSize: fp(12),
      lineHeight: fp(12),
      color: colors.novelText,
      marginLeft: wp(8),
      paddingVertical: 0,
    },
    searchPlaceholder: {
      color: '#999999', // 写死的占位符颜色
    },

    // 历史按钮样式
    historyButton: {
      justifyContent: 'center',
      alignItems: 'center',
      minWidth: wp(32),
      minHeight: wp(32),
    },
    historyIcon: {
      fontSize: fp(18),
      color: colors.novelText,
    },

    // ScrollView 样式
    scrollView: {
      flex: 1,
    },
    scrollContent: {
      paddingBottom: wp(20),
    },

    // 用户信息区域
    userSection: {
      paddingHorizontal: wp(20),
      paddingVertical: wp(24),
      backgroundColor: colors.novelSecondaryBackground,
    },
    userGreeting: {
      fontSize: fp(24),
      color: colors.novelText,
      fontWeight: '600',
      marginBottom: wp(8),
    },
    userSubtitle: {
      fontSize: fp(14),
      color: colors.novelTextGray,
      lineHeight: fp(20),
    },

    // 咨询场景区域
    consultSection: {
      backgroundColor: colors.novelBackground,
      marginHorizontal: wp(14),
      borderRadius: wp(12),
      padding: wp(12),
      marginBottom: wp(24),
    },
    consultHeader: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: wp(16),
    },
    consultTitle: {
      fontSize: fp(18),
      color: colors.novelText,
      fontWeight: '600',
    },
    moreButton: {
      flexDirection: 'row',
      alignItems: 'center',
    },
    moreText: {
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginRight: wp(4),
    },
    moreIcon: {
      fontSize: fp(16),
      color: colors.novelTextGray,
    },

    // 咨询场景网格
    consultGrid: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      justifyContent: 'space-between',
    },
    consultItem: {
      width: '48%',
      marginBottom: wp(16),
      borderRadius: sp(16),
      overflow: 'hidden',
    },
    consultGradient: {
      flex: 1,
      padding: wp(16),
      justifyContent: 'space-between',
    },
    consultItemHeader: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: wp(12),
    },
    consultItemTitle: {
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '600',
    },
    consultItemContent: {
      flex: 1,
    },
    consultItemText: {
      fontSize: fp(12),
      color: colors.novelTextGray,
      lineHeight: fp(18),
      marginBottom: wp(4),
    },
    consultItemBottom: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'flex-end',
    },
    consultIcon: {
      fontSize: fp(24),
    },
    consultArrow: {
      fontSize: fp(16),
      color: colors.novelTextGray,
    },

    // 大家都在问区域
    frequentSection: {
      backgroundColor: colors.novelBackground,
      marginHorizontal: wp(14),
      borderRadius: wp(12),
      padding: wp(12),
    },
    frequentTitleContainer: {
      marginBottom: wp(16),
    },
    frequentTitle: {
      fontSize: fp(16),
      color: colors.novelMain,
      fontWeight: '600',
      marginBottom: wp(6),
    },
    frequentTitleUnderline: {
      marginLeft: wp(8),
      height: wp(2),
      backgroundColor: colors.novelMain,
      width: wp(60),
      borderRadius: wp(1),
    },
    frequentList: {
      gap: wp(12),
    },
    frequentItem: {
      flexDirection: 'row',
      alignItems: 'center',
      padding: wp(2),
    },
    frequentNumberText: {
      marginRight: wp(8),
      fontSize: fp(16),
      fontWeight: '600',
    },
    frequentContent: {
      flex: 1,
      marginRight: wp(12),
    },
    frequentQuestionTitle: {
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '500',
    },
    frequentArrow: {
      fontSize: fp(16),
      color: colors.novelTextGray,
    },

    // 底部联系区域
    contactSection: {
      paddingHorizontal: wp(20),
      paddingBottom: wp(24),
      backgroundColor: colors.novelBackground,
      borderTopWidth: 1,
      borderTopColor: colors.novelDivider,
    },
    contactButton: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: wp(8),
    },
    contactButtonIcon: {
      fontSize: fp(18),
      color: colors.novelText,
      marginRight: wp(8),
    },
    contactButtonText: {
      fontSize: fp(16),
      color:  colors.novelText,
      fontWeight: '500',
    },
    contactInfo: {
      alignItems: 'center',
    },
    contactInfoText: {
      fontSize: fp(8),
      color: colors.novelTextGray,
      textAlign: 'center',
    },

    // 问题列表页样式
    questionListContainer: {
      flex: 1,
      backgroundColor: colors.novelBackground,
    },
    questionListContent: {
      flex: 1,
      flexDirection: 'row',
    },

    // 左侧分类筛选样式
    categoryFilterContainer: {
      width: wp(100),
      backgroundColor: colors.novelSecondaryBackground,
      borderRightWidth: 1,
      borderRightColor: colors.novelDivider,
    },
    categoryFilterList: {
      flex: 1,
      paddingVertical: wp(8),
    },
    categoryFilterItem: {
      flexDirection: 'column',
      alignItems: 'center',
      paddingVertical: wp(12),
      paddingHorizontal: wp(8),
      marginHorizontal: wp(4),
      marginVertical: wp(2),
      borderRadius: wp(8),
    },
    categoryFilterItemActive: {
      backgroundColor: colors.novelMain + '15',
    },
    categoryFilterIcon: {
      fontSize: fp(20),
      marginBottom: wp(4),
    },
    categoryFilterIconActive: {
      // 可以添加激活状态的图标样式
    },
    categoryFilterText: {
      fontSize: fp(10),
      color: colors.novelTextGray,
      textAlign: 'center',
      fontWeight: '500',
    },
    categoryFilterTextActive: {
      color: colors.novelMain,
      fontWeight: '600',
    },

    // 右侧问题列表样式
    questionListRight: {
      flex: 1,
    },
    categoryHeader: {
      backgroundColor: colors.novelSecondaryBackground,
      paddingHorizontal: wp(20),
      paddingVertical: wp(16),
      borderBottomWidth: 1,
      borderBottomColor: colors.novelDivider,
    },
    categoryTitle: {
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '600',
    },
    categorySubtitle: {
      fontSize: fp(12),
      color: colors.novelTextGray,
      marginTop: wp(4),
    },
    questionsList: {
      paddingHorizontal: wp(16),
      paddingTop: wp(8),
    },
    questionItem: {
      backgroundColor: colors.novelBackground,
      paddingVertical: wp(16),
      paddingHorizontal: wp(4),
      marginBottom: wp(8),
      borderBottomWidth: 1,
      borderBottomColor: colors.novelDivider + '20',
    },
    questionItemContent: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
    },
    questionItemIcon: {
      width: wp(40),
      height: wp(40),
      borderRadius: wp(20),
      backgroundColor: colors.novelSecondaryBackground,
      justifyContent: 'center',
      alignItems: 'center',
      marginRight: wp(12),
    },
    questionItemIconText: {
      fontSize: fp(18),
    },
    questionItemLeft: {
      flex: 1,
      marginRight: wp(12),
    },
    questionItemTitle: {
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '500',
      lineHeight: fp(22),
    },
    questionItemMeta: {
      flexDirection: 'row',
      alignItems: 'center',
      marginTop: wp(8),
    },
    questionItemTag: {
      backgroundColor: '#FF995D' + '15',
      borderRadius: sp(4),
      paddingHorizontal: wp(6),
      paddingVertical: wp(2),
      marginRight: wp(8),
    },
    questionItemTagText: {
      fontSize: fp(10),
      color: '#FF995D',
    },
    questionItemViews: {
      fontSize: fp(11),
      color: colors.novelTextGray,
    },
    questionItemArrow: {
      fontSize: fp(18),
      color: colors.novelTextGray,
    },

    // 问题详情页样式
    detailContainer: {
      flex: 1,
      backgroundColor: colors.novelBackground,
    },
    detailHeader: {
      backgroundColor: colors.novelSecondaryBackground,
      paddingHorizontal: wp(20),
      paddingVertical: wp(20),
      borderBottomWidth: 1,
      borderBottomColor: colors.novelDivider,
    },
    detailTitle: {
      fontSize: fp(18),
      color: colors.novelText,
      fontWeight: '600',
      lineHeight: fp(26),
      marginBottom: wp(8),
    },
    detailMeta: {
      flexDirection: 'row',
      alignItems: 'center',
      flexWrap: 'wrap',
    },
    detailTag: {
      backgroundColor: '#FF995D' + '15',
      borderRadius: sp(4),
      paddingHorizontal: wp(8),
      paddingVertical: wp(4),
      marginRight: wp(8),
      marginBottom: wp(8),
    },
    detailTagText: {
      fontSize: fp(11),
      color: '#FF995D',
    },
    detailContent: {
      padding: wp(20),
    },
    detailText: {
      fontSize: fp(14),
      color: colors.novelText,
      lineHeight: fp(22),
      marginBottom: wp(20),
    },

    // 相关问题区域
    relatedSection: {
      marginTop: wp(24),
      marginBottom: wp(20),
    },
    relatedTitle: {
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '600',
      marginBottom: wp(12),
    },
    relatedItem: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      backgroundColor: colors.novelSecondaryBackground,
      borderRadius: sp(8),
      padding: wp(12),
      marginBottom: wp(8),
      borderWidth: 1,
      borderColor: colors.novelDivider + '30',
    },
    relatedItemText: {
      fontSize: fp(14),
      color: '#FF995D',
      flex: 1,
    },
    relatedItemArrow: {
      fontSize: fp(16),
      color: colors.novelTextGray,
    },

    // 解决状态按钮
    resolutionSection: {
      paddingHorizontal: wp(20),
      paddingVertical: wp(16),
      borderTopWidth: 1,
      borderTopColor: colors.novelDivider,
    },
    resolutionButtons: {
      flexDirection: 'row',
      gap: wp(12),
    },
    resolutionButton: {
      flex: 1,
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      paddingVertical: wp(12),
      borderRadius: sp(25),
      borderWidth: 1,
    },
    resolvedButton: {
      backgroundColor: '#4CAF50' + '15',
      borderColor: '#4CAF50' + '30',
    },
    unresolvedButton: {
      backgroundColor: colors.novelSecondaryBackground,
      borderColor: colors.novelDivider,
    },
    resolutionButtonIcon: {
      fontSize: fp(16),
      marginRight: wp(6),
    },
    resolvedButtonIcon: {
      color: '#4CAF50',
    },
    unresolvedButtonIcon: {
      color: colors.novelTextGray,
    },
    resolutionButtonText: {
      fontSize: fp(14),
      fontWeight: '500',
    },
    resolvedButtonText: {
      color: '#4CAF50',
    },
    unresolvedButtonText: {
      color: colors.novelTextGray,
    },

    // 空状态样式
    emptyState: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      padding: wp(40),
    },
    emptyStateIcon: {
      fontSize: fp(48),
      marginBottom: wp(16),
    },
    emptyStateTitle: {
      fontSize: fp(16),
      color: colors.novelText,
      fontWeight: '600',
      marginBottom: wp(8),
      textAlign: 'center',
    },
    emptyStateDescription: {
      fontSize: fp(14),
      color: colors.novelTextGray,
      textAlign: 'center',
      lineHeight: fp(20),
    },

    // 加载状态样式
    loadingContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      padding: wp(20),
    },
    loadingText: {
      ...typography.bodyMedium,
      color: colors.novelTextGray,
      marginTop: wp(8),
    },
  });
};
