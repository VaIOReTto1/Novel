import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createHistoryPageStyles = (colors: NovelColors) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.novelBackground,
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
  searchButton: {
    justifyContent: 'center',
    alignItems: 'center',
    minWidth: wp(32),
    minHeight: wp(32),
  },

  // Tabs样式
  tabsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: wp(12),
    paddingVertical: wp(6),
    backgroundColor: colors.novelBackground,
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
  activeTab: {
    // 移除背景色，只保留文字样式
  },
  tabText: {
    ...typography.labelLarge,
    color: colors.novelTextGray,
    fontWeight: '400',
  },
  activeTabText: {
    color: colors.novelText,
    fontWeight: '600',
  },
  // 添加水平分割线样式
  tabsDivider: {
    height: 0.5,
    backgroundColor: colors.novelDivider,
  },

  // 视图控制样式
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
    color: colors.novelText,
    fontWeight: '600',
  },
  verticalDivider: {
    width: 0.5,
    height: wp(20),
    backgroundColor: colors.novelDivider,
  },
  staticButton: {
    paddingRight: wp(12),
    paddingVertical: wp(6),
    alignItems: 'center',
  },
  staticButtonText: {
    ...typography.labelLarge,
    color: colors.novelText,
    fontWeight: '600',
  },

  // 内容区域样式
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

  // 网格视图样式
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
    width: (wp(350) - wp(32) - wp(12)) / 3, // 计算宽度
  },
  gridCover: {
    width: '100%',
    height: wp(135),
    borderRadius: sp(8),
    overflow: 'hidden',
    backgroundColor: colors.novelDivider,
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
    color: colors.novelText,
    numberOfLines: 2,
    ellipsizeMode: 'tail',
    lineHeight: fp(18),
    marginBottom: wp(4),
  },
  gridAuthor: {
    paddingTop: wp(8),
    fontSize: wp(10),
    color: colors.novelTextGray,
    numberOfLines: 1,
    marginBottom: wp(4),
  },
  gridProgress: {
    ...typography.labelSmall,
    color: colors.novelMain,
    fontSize: fp(11),
  },

  // 列表视图样式
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
    backgroundColor: colors.novelDivider,
    marginRight: wp(12),
  },
  listCoverImage: {
    width: '100%',
    height: '100%',
    borderRadius: sp(6),
  },
  listInfo: {
    flex: 1,
  },
  listTitle: {
    paddingTop: wp(12),
    fontSize: wp(14),
    fontWeight: '600',
    color: colors.novelText,
    numberOfLines: 2,
    lineHeight: fp(18),
    marginBottom: wp(4),
  },
  listAuthor: {
    paddingTop: wp(8),
    fontSize: wp(10),
    color: colors.novelTextGray,
    numberOfLines: 1,
    marginBottom: wp(4),
  },
  listDescription: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    numberOfLines: 2,
    lineHeight: fp(16),
    marginBottom: wp(4),
  },
  listMeta: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  listProgress: {
    ...typography.labelSmall,
    color: colors.novelMain,
    fontSize: fp(11),
  },
  listTime: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    fontSize: fp(11),
  },

  // 空状态
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

  // 加载指示器样式
  loadingContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(20),
    gap: wp(8),
  },
  loadingSpinner: {
    marginRight: wp(8),
  },
  spinnerText: {
    fontSize: fp(14),
    color: colors.novelMain,
    fontWeight: '500',
  },
  loadingText: {
    ...typography.labelLarge,
    color: colors.novelTextGray,
  },
  endLine: {
    width: wp(30),
    height: wp(1),
    backgroundColor: colors.novelDivider,
  },
  endText: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    marginHorizontal: wp(10),
  },

  // 刷新指示器样式
  refreshIndicator: {
    backgroundColor: colors.novelBackground,
    paddingVertical: wp(15),
    paddingHorizontal: wp(20),
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
    marginBottom: wp(10),
  },
  refreshContent: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  refreshText: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },

  // 占位符样式
  placeholderCover: {
    width: '100%',
    height: '100%',
    backgroundColor: colors.novelDivider,
    justifyContent: 'center',
    alignItems: 'center',
  },
  placeholderText: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    opacity: 0.5,
  },
});
