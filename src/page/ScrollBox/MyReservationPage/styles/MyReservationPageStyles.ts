import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createMyReservationPageStyles = (colors: NovelColors) => StyleSheet.create({
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

  // TopBar中的Tab样式
  topBarTabsContainer: {
    flexDirection: 'row',
  },
  topBarTab: {
    paddingHorizontal: wp(8),
    alignItems: 'center',
  },
  activeTopBarTab: {
    backgroundColor: colors.novelBackground,
    shadowColor: colors.novelText,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  topBarTabText: {
    fontSize: fp(17),
    color: colors.novelTextGray,
    fontWeight: '500',
  },
  activeTopBarTabText: {
    color: colors.novelText,
    fontWeight: '600',
  },

  // ScrollView 样式
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: wp(20),
  },

  // 通用卡片样式
  section: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(8),
    padding: wp(16),
    marginHorizontal: wp(16),
    marginBottom: wp(16),
  },

  // 子Tab样式（已上线/待上线）
  subTabsContainer: {
    flexDirection: 'row',
    gap: wp(12),
    paddingHorizontal: wp(16),
    paddingVertical: wp(12),
    backgroundColor: colors.novelBackground,
  },
  subTab: {
    paddingHorizontal: wp(10),
    paddingVertical: wp(6),
    borderRadius: sp(5),
    backgroundColor: colors.novelSecondaryBackground + '80',
  },
  activeSubTab: {
    backgroundColor: colors.novelMain + '20',
    borderColor: colors.novelMain,
  },
  subTabText: {
    fontSize: fp(12),
    color: colors.novelText,
  },
  activeSubTabText: {
    color: colors.novelMain,
    fontWeight: '600',
  },

  // 网格布局样式
  gridContainer: {
    paddingHorizontal: wp(16),
  },
  gridRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: wp(8),
  },
  gridItem: {
    width: '49%',
    aspectRatio: 0.58,
    borderRadius: sp(12),
    overflow: 'hidden',
    backgroundColor: colors.novelSecondaryBackground,
    position: 'relative',
  },

  // 预约卡片样式
  reservationCard: {
    position: 'relative',
    width: '100%',
    height: '100%',
  },
  cardImage: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    width: '100%',
    height: '100%',
  },
  cardBadge: {
    position: 'absolute',
    top: wp(8),
    right: wp(8),
    backgroundColor: colors.novelMain,
    paddingHorizontal: wp(6),
    paddingVertical: wp(2),
    borderRadius: sp(4),
  },
  cardBadgeText: {
    fontSize: fp(10),
    color: colors.novelBackground,
    fontWeight: '500',
  },
  cardGradient: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    height: '60%',
  },
  cardContent: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    padding: wp(16),
    zIndex: 1,
  },
  cardTitle: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: '500',
    marginBottom: wp(4),
    maxLength: 1,
    ellipsizeMode: 'tail',
  },
  cardSubtitle: {
    fontSize: fp(10),
    color: colors.novelBackground + '80',
    marginBottom: wp(12),
  },
  cardButton: {
    width: '100%',
    paddingVertical: wp(5),
    backgroundColor: colors.novelBackground + '0e',
    borderWidth: 1,
    borderColor: colors.novelBackground + '28',
    borderRadius: sp(24),
    alignItems: 'center',
  },
  cardButtonText: {
    fontSize: fp(12),
    color: colors.novelBackground,
    fontWeight: '500',
  },
  reservedButton: {
    backgroundColor: colors.novelTextGray + '40',
    borderColor: colors.novelTextGray + '60',
  },
  reservedButtonText: {
    color: colors.novelBackground,
  },

  // 空状态样式
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(80),
    paddingHorizontal: wp(20),
  },
  emptyIcon: {
    width: wp(160),
    height: wp(160),
    backgroundColor: colors.novelSecondaryBackground,
    borderRadius: wp(80),
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: wp(24),
  },
  emptyImageAsset: {
    width: 80,
    height: 80,
  },
  emptyIconText: {
    fontSize: fp(48),
  },
  emptyTitle: {
    fontSize: fp(14),
    color: colors.novelTextGray,
    textAlign: 'center',
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
