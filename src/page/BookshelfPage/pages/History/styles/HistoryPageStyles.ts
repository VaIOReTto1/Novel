import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../../utils/theme/dimensions';
import { typography } from '../../../../../utils/theme/typography';
import { NovelColors } from '../../../../../utils/theme/colors';

export const createHistoryPageStyles = (colors: NovelColors) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.novelBackground,
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
    paddingHorizontal: wp(14),
    paddingVertical: wp(5),
    borderRadius: sp(4),
    marginRight: wp(12),
    backgroundColor: colors.novelDivider + 'a0',
    alignItems: 'center',
  },
  activeTab: {
    backgroundColor: colors.novelMain + '30',
  },
  tabText: {
    fontSize: fp(15),
    color: colors.novelTextGray,
    fontWeight: '400',
  },
  activeTabText: {
    color: colors.novelMain,
    fontWeight: '600',
  },

  // 视图控制样式
  viewControls: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(6),
  },
  viewToggleButton: {
    paddingLeft: wp(12),
    alignItems: 'center',
  },
  viewToggleText: {
    fontSize: fp(15),
    color: colors.novelText,
    fontWeight: '800',
  },
  verticalDivider: {
    width: 1,
    marginHorizontal: wp(4),
    alignSelf: 'center',
    height: wp(10),
    backgroundColor: colors.novelDivider,
  },
  staticButton: {
    paddingRight: wp(12),
    alignItems: 'center',
  },
  staticButtonText: {
    fontSize: fp(15),
    color: colors.novelText,
    fontWeight: '800',
  },

  // 内容容器样式
  contentContainer: {
    flex: 1,
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: wp(32),
  },

  // 网格视图样式
  gridContainer: {
    paddingHorizontal: wp(12),
    paddingTop: wp(12),
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
  gridAddToShelfButton: {
    marginTop: wp(6),
    paddingHorizontal: wp(12),
    paddingVertical: wp(4),
    borderRadius: sp(20),
    backgroundColor:colors.novelDivider + 'a0',
    alignItems: 'center',
  },
  gridAddToShelfText: {
    fontSize: fp(10),
    color: colors.novelMain,
    fontWeight: '500',
  },
  gridAddedToShelfText: {
    color: colors.novelTextGray,
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
  listContent: {
    flex: 1,
  },
  listTitle: {
    marginTop: wp(14),
    fontSize: wp(16),
    fontWeight: '600',
    color: colors.novelText,
    numberOfLines: 1,
    lineHeight: fp(18),
    marginBottom: wp(18),
  },
  listMeta: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  listFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  listChapter: {
    fontSize: fp(11),
    color: colors.novelTextGray + 'a0',
    flex: 1,
  },
  addToShelfButton: {
    paddingHorizontal: wp(12),
    paddingVertical: wp(4),
    borderRadius: sp(20),
    backgroundColor:colors.novelDivider + 'a0',
  },
  addToShelfText: {
    fontSize: fp(11),
    color: colors.novelMain,
    fontWeight: '500',
  },
  addedToShelfText: {
    color: colors.novelTextGray,
  },

  // 编辑模式样式
  editModeOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.1)',
    zIndex: 1,
  },
  selectionCheckbox: {
    position: 'absolute',
    top: wp(8),
    right: wp(8),
    width: wp(20),
    height: wp(20),
    borderRadius: wp(10),
    backgroundColor: colors.novelBackground,
    borderWidth: 1,
    borderColor: colors.novelDivider,
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 2,
  },
  selectedCheckbox: {
    backgroundColor: colors.novelMain,
    borderColor: colors.novelMain,
  },
  checkboxIcon: {
    fontSize: fp(12),
    color: colors.novelBackground,
  },

  // 空状态样式
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

  // 加载状态样式
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

  // 编辑工具栏样式
  editToolbar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: colors.novelBackground,
    borderTopWidth: 1,
    borderTopColor: colors.novelDivider,
    paddingHorizontal: wp(16),
    paddingVertical: wp(12),
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  editToolbarLeft: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  editToolbarRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(16),
  },
  editToolbarButton: {
    paddingHorizontal: wp(12),
    paddingVertical: wp(8),
  },
  editToolbarButtonText: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },
  editToolbarDeleteButton: {
    backgroundColor: colors.novelError,
    borderRadius: sp(6),
    paddingHorizontal: wp(16),
    paddingVertical: wp(8),
  },
  editToolbarDeleteText: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: '500',
  },
  selectedCountText: {
    fontSize: fp(14),
    color: colors.novelTextGray,
  },
});
