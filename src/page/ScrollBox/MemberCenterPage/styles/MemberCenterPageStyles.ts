import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

// VIP 专用颜色定义
export const VIP_COLORS = {
  // 普通会员 – 奶油蜜桃
  memberGradient: ['#FDC2A3', '#FEEDE2', '#FFF9F5'],

  // SVIP – 柔雾珊瑚
  svipGradient: ['#FFB5B5', '#FFE4E7', '#FFFAFA'],

  // 免广告VIP – 冰薄荷
  adfreeGradient: ['#A9E8DF', '#D4F7F1', '#F7FFFC'],
  // 价格突出色
  priceHighlight: '#ff4444',
  // VIP金色
  vipGold: '#ffd700',
  // 特权蓝色
  privilegeBlue: '#3491ff',
};

// 根据VIP类型获取主题色
export const getVIPThemeColors = (cardType: string) => {
  switch (cardType) {
    case 'member':
      return {
        primary: '#ff6b35',
        light: '#ff8c42',
        gradient: VIP_COLORS.memberGradient,
      };
    case 'svip':
      return {
        primary: '#ff6b6b',
        light: '#ff8e8e',
        gradient: VIP_COLORS.svipGradient,
      };
    case 'adfree':
      return {
        primary: '#4ecdc4',
        light: '#7fcdcd',
        gradient: VIP_COLORS.adfreeGradient,
      };
    default:
      return {
        primary: '#ff6b35',
        light: '#ff8c42',
        gradient: ['#ff6b35', '#ff8c42', '#ffa726'],
      };
  }
};

export const createMemberCenterPageStyles = (colors: NovelColors, currentCardType: string = 'member') => {
  const themeColors = getVIPThemeColors(currentCardType);

  return StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.novelBackground,
  },

  // 顶部Bar样式 - 透明背景
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: wp(20),
    paddingVertical: wp(16),
    backgroundColor: colors.novelBackground,
    minHeight: wp(56),
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    zIndex: 10,
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
  title: {
    ...typography.bodyMedium,
    fontSize: fp(18),
    color: colors.novelText,
    fontWeight: '600',
    textAlign: 'center',
  },
  menuButton: {
    justifyContent: 'center',
    alignItems: 'center',
    minWidth: wp(32),
    minHeight: wp(32),
  },
  menuIcon: {
    fontSize: fp(24),
    color: colors.novelText,
    fontWeight: '400',
  },

  // 右侧下拉菜单
  menuDropdown: {
    position: 'absolute',
    top: wp(50),
    right: wp(20),
    backgroundColor: colors.novelBackground,
    borderRadius: sp(8),
    paddingVertical: wp(8),
    minWidth: wp(120),
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 5,
    zIndex: 20,
  },
  menuItem: {
    paddingHorizontal: wp(16),
    paddingVertical: wp(12),
    flexDirection: 'row',
    alignItems: 'center',
  },
  menuItemText: {
    fontSize: fp(14),
    color: colors.novelText,
    marginLeft: wp(8),
  },

  // ScrollView 样式
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingTop: wp(56), // 为TopBar留出空间
    paddingBottom: wp(120), // 为底部按钮留出空间
  },

  // VIP卡片轮播区域 - 无限循环轮播优化
  cardCarouselContainer: {
    marginTop: wp(20),
    marginBottom: wp(10),
  },
  cardCarousel: {
    paddingHorizontal: wp(0),
  },
  cardWrapper: {
    width: wp(220),            // ← 确保与 CARD_WIDTH 一致
    height: wp(160),
    marginHorizontal: 0,       // 移除margin，由FlatList的paddingHorizontal处理居中
    position: 'relative',
    overflow: 'visible',        // 让侧卡超出时不被裁切，支持3D效果
    justifyContent: 'center',   // 垂直居中
    alignItems: 'center',       // 水平居中
  },

  // 3D卡片效果
  cardContainer: {
    width: '100%',
    height: '100%',
    perspective: 1000,
  },
  card: {
    width: '100%',
    height: '100%',
    borderRadius: sp(16),
    padding: wp(20),
    justifyContent: 'space-between',
  },
  cardActive: {
    transform: [{ scale: 1.05 }, { translateY: -wp(5) }],
  },
  cardInactive: {
    transform: [{ scale: 0.95 }, { translateY: wp(5) }],
    opacity: 0.8,
  },
  cardTitle: {
    fontSize: fp(24),
    color: colors.novelBackground,
    fontWeight: '700',
  },
  cardSubtitle: {
    fontSize: fp(14),
    color: 'rgba(255,255,255,0.9)',
    marginTop: wp(4),
  },
  cardBottomIcon: {
    alignSelf: 'flex-end',
    width: wp(40),
    height: wp(40),
    borderRadius: wp(20),
    backgroundColor: 'rgba(255,255,255,0.2)',
    justifyContent: 'center',
    alignItems: 'center',
  },

  // 卡片指示器 - 动态主题色
  cardIndicatorContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: wp(16),
  },
  cardIndicator: {
    width: wp(4),
    height: wp(4),
    borderRadius: wp(4),
    backgroundColor: colors.novelTextGray + '40',
    marginHorizontal: wp(4),
  },
  cardIndicatorActive: {
    backgroundColor: themeColors.primary + 'aa',
    width: wp(4),
    borderRadius: wp(4),
  },

  // 权益展示区域
  benefitsContainer: {
    paddingHorizontal: wp(16),
    marginBottom: wp(24),
  },
  benefitsTitle: {
    fontSize: fp(18),
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(16),
    textAlign: 'left',
  },
  benefitsList: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  benefitItem: {
    width: '25%',
    alignItems: 'center',
    marginBottom: wp(16),
  },
  benefitIcon: {
    width: wp(52),
    height: wp(52),
    borderRadius: wp(26),
    backgroundColor: colors.novelSecondaryBackground,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: wp(8),
  },
  benefitIconText: {
    fontSize: fp(20),
  },
  benefitTitle: {
    fontSize: fp(12),
    color: colors.novelText,
    textAlign: 'center',
    fontWeight: '400',
  },
  benefitDescription: {
    fontSize: fp(10),
    color: colors.novelTextGray,
    textAlign: 'center',
    marginTop: wp(2),
  },

  // 价格套餐区域 - 动态主题色
  priceContainer: {
    paddingHorizontal: wp(16),
    marginBottom: wp(24),
  },
  pricePackagesRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: wp(8),
  },
  pricePackage: {
    flex: 1,
    backgroundColor: colors.novelSecondaryBackground,
    borderRadius: sp(12),
    padding: wp(16),
    alignItems: 'center',
    minHeight: wp(120),
  },
  pricePackageSelected: {
  },
  pricePackageRecommended: {
    position: 'relative',
  },
  recommendedBadge: {
    position: 'absolute',
    top: -wp(8),
    left: wp(8),
    right: wp(8),
    backgroundColor: themeColors.primary,
    borderRadius: sp(12),
    paddingVertical: wp(4),
    paddingHorizontal: wp(8),
    alignItems: 'center',
  },
  recommendedText: {
    fontSize: fp(10),
    color: colors.novelBackground,
    fontWeight: '600',
  },
  priceDuration: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
    textAlign: 'center',
    marginBottom: wp(8),
  },
  priceValue: {
    fontSize: fp(22),
    color: themeColors.primary,
    fontWeight: '700',
    textAlign: 'center',
  },
  priceOriginal: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    textDecorationLine: 'line-through',
    textAlign: 'center',
    marginTop: wp(2),
  },

  // 权益对比表格
  comparisonContainer: {
    paddingHorizontal: wp(16),
    marginBottom: wp(24),
  },
  comparisonTitle: {
    fontSize: fp(18),
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(16),
    textAlign: 'left',
  },
  comparisonTable: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(12),
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: colors.novelDivider,
  },
  comparisonHeader: {
    flexDirection: 'row',
    backgroundColor: colors.novelSecondaryBackground,
  },
  comparisonHeaderCell: {
    flex: 1,
    alignItems: 'center',
  },
  comparisonHeaderCellHighlight: {
    backgroundColor: themeColors.primary + '30',
  },
  comparisonHeaderText: {

    paddingVertical: wp(12),
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '600',
  },
  comparisonRow: {
    flexDirection: 'row',
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
  },
  comparisonCell: {
    flex: 1,
    alignItems: 'center',
    paddingHorizontal: wp(4),
  },
  comparisonCellHighlight: {
    backgroundColor: themeColors.primary + '08',
  },
  comparisonCellText: {
    fontSize: fp(12),
    paddingVertical: wp(12),
    color: colors.novelText,
    textAlign: 'center',
  },
  comparisonHighlight: {
    color: themeColors.primary,
    fontWeight: '600',
  },

  // VIP推荐区域
  recommendationContainer: {
    paddingHorizontal: wp(16),
    marginBottom: wp(24),
  },
  recommendationTitle: {
    fontSize: fp(18),
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(16),
  },
  recommendationGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  recommendationItem: {
    width: '48%',
    marginBottom: wp(16),
  },
  recommendationCover: {
    width: '100%',
    backgroundColor: colors.novelBookBackground,
    aspectRatio: 0.75,
    borderRadius: sp(8),
    marginBottom: wp(8),
  },
  recommendationItemTitle: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
    marginBottom: wp(4),
  },
  recommendationDescription: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    lineHeight: fp(16),
  },

  // 任务卡片区域
  taskCardsGradient: {
    borderRadius: wp(12),
    paddingVertical: wp(24),
    marginBottom: wp(14),
    paddingHorizontal: wp(12),
    marginHorizontal: wp(16),
  },

  taskCardsTitle: {
    fontSize: sp(18),
    fontWeight: '700',
    color: '#222',
    textAlign: 'left',
  },

  /* 顶部横向分割线（可选） */
  taskCardsDivider: {
    height: 1,
    backgroundColor: 'rgba(0,0,0,0.05)',
    marginTop: wp(10),
  },

  /* 列表本身 */
  taskCardsList: {
    flexDirection: 'column',
  },

  /* ------------ 单条任务 ------------- */
  taskCardItem: {
    paddingTop: wp(14),
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },

  /* 左侧文案区 */
  taskCardInfo: {
    flexShrink: 1,
  },
  taskCardTitle: {
    fontSize: sp(16),
    fontWeight: '600',
    color: '#111',
    marginBottom: wp(4),
  },

  /* 奖励整行 */
  taskCardRewardRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: wp(4),
  },

  /* 橙色标签 badge */
  taskCardBadge: {
    fontSize: sp(9),
    color: themeColors.primary,
    backgroundColor: themeColors.primary + '10',
    paddingHorizontal: wp(6),
    paddingVertical: wp(2),
    borderRadius: wp(3),
    marginRight: wp(8),
  },

  /* 灰色说明 reward */
  taskCardRewardText: {
    fontSize: sp(12),
    color: colors.novelTextGray + 'a0',
  },

  /* 右侧按钮 */
  taskCardButton: {
    width: wp(88),
    height: wp(32),
    borderRadius: wp(16),
    backgroundColor: 'rgba(0,0,0,0.05)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  taskCardButtonText: {
    fontSize: sp(14),
    color: colors.novelTextGray,
  },
  taskCardButtonCompleted: {
    backgroundColor: 'rgba(0,0,0,0.01)',
  },
  taskCardButtonTextCompleted: {
    color: colors.novelText,
    fontWeight: '600',
  },

  // 底部购买区域 - 动态主题色
  bottomPurchaseContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: colors.novelBackground,
    borderTopWidth: 1,
    borderTopColor: colors.novelDivider,
    paddingHorizontal: wp(16),
    paddingTop: wp(16),
    paddingBottom: wp(16),
    zIndex: 10,
  },
  purchaseButton: {
    backgroundColor: themeColors.primary,
    borderRadius: sp(25),
    paddingVertical: wp(10),
    alignItems: 'center',
    marginBottom: wp(12),
    shadowColor: themeColors.primary,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 5,
  },
  purchaseButtonText: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: '600',
  },
  purchaseButtonSubtext: {
    fontSize: fp(8),
    color: colors.novelBackground,
    opacity: 0.8,
    marginTop: wp(2),
  },
  agreementContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    flexWrap: 'wrap',
  },
  agreementText: {
    fontSize: fp(12),
    color: colors.novelTextGray,
  },
  agreementLink: {
    fontSize: fp(12),
    color: themeColors.primary,
    textDecorationLine: 'underline',
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
