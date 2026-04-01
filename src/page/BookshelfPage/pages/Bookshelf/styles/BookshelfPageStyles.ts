import { StyleSheet, Dimensions } from 'react-native';
import { createNovelDesignUI } from '../../../../../design-system/novelDesign';
import { wp, fp, sp } from '../../../../../utils/theme/dimensions';
import { NovelColors } from '../../../../../utils/theme/colors';

const { width: screenWidth } = Dimensions.get('window');

export const createBookshelfPageStyles = (colors: NovelColors) => {
  const novelDesign = createNovelDesignUI(colors);

  return StyleSheet.create({
  // 容器样式
  container: {
    flex: 1,
    backgroundColor: novelDesign.color.bg.canvas,
  },

  contentContainer: {
    flex: 1,
  },

  scrollView: {
    flex: 1,
  },

  scrollContent: {
    paddingBottom: wp(100),
  },

  // 标签页样式
  tabsContainer: {
    backgroundColor: novelDesign.color.bg.surface,
    paddingHorizontal: wp(20),
    paddingVertical: wp(12),
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
  },

  tabsRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },

  tabsLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },

  tabsScroll: {
    flexDirection: 'row',
    alignItems: 'center',
  },

  tab: {
    paddingHorizontal: wp(16),
    paddingVertical: wp(8),
    marginRight: wp(12),
    borderRadius: sp(16),
    backgroundColor: 'transparent',
  },

  activeTab: {
    backgroundColor: colors.novelMain,
  },

  tabText: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },

  activeTabText: {
    color: colors.novelBackground,
    fontWeight: '600',
  },

  tabsRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(12),
  },

  viewTypeButton: {
    padding: wp(8),
    borderRadius: sp(6),
    backgroundColor: 'transparent',
  },

  activeViewTypeButton: {
    backgroundColor: colors.novelMain,
  },

  sortButton: {
    padding: wp(8),
    borderRadius: sp(6),
    backgroundColor: 'transparent',
  },

  editButton: {
    padding: wp(8),
    borderRadius: sp(6),
    backgroundColor: 'transparent',
  },

  activeEditButton: {
    backgroundColor: colors.novelMain,
  },

  tabsDivider: {
    height: 1,
    backgroundColor: novelDesign.color.bg.elevated,
  },

  // 内容区域样式
  contentArea: {
    flex: 1,
  },

  // 空状态样式
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(80),
    backgroundColor: novelDesign.color.bg.canvas,
  },

  emptyIcon: {
    fontSize: fp(48),
    marginBottom: wp(16),
  },

  emptyText: {
    fontSize: fp(16),
    color: colors.novelText,
    fontWeight: '500',
    marginBottom: wp(8),
  },

  emptyDescription: {
    fontSize: fp(16),
    color: colors.novelText,
    opacity: 0.6,
    textAlign: 'center',
    lineHeight: fp(20),
  },

  // 网格视图样式
  gridContainer: {
    paddingHorizontal: wp(16),
    paddingTop: wp(16),
    paddingBottom: wp(16),
  },

  gridRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: wp(20),
  },

  gridItem: {
    backgroundColor: novelDesign.color.bg.surface,
    width: (screenWidth - wp(32) - wp(20)) / 3, // 动态计算3列布局
  },

  gridCover: {
    borderRadius: sp(12),
    width: '100%',
    height: wp(120),
    backgroundColor: novelDesign.color.bg.elevated,
  },

  gridCoverImage: {
    width: '100%',
    height: '100%',
  },

  gridPlaceholderCover: {
    width: '100%',
    height: '100%',
    backgroundColor: novelDesign.color.brand.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },

  gridPlaceholderText: {
    fontSize: fp(10),
    color: novelDesign.color.text.inverse,
    opacity: 0.8,
  },

  gridInfo: {
    padding: wp(8),
  },

  gridTitle: {
    fontSize: fp(14),
    fontWeight: '600',
    color: colors.novelText,
    lineHeight: fp(18),
    marginBottom: wp(4),
  },

  gridAuthor: {
    fontSize: fp(12),
    color: colors.novelText,
    opacity: 0.7,
    lineHeight: fp(16),
  },

  gridProgress: {
    fontSize: fp(10),
    color: novelDesign.color.brand.primary,
    marginTop: wp(2),
  },

  // 列表视图样式
  listContainer: {
    paddingHorizontal: wp(20),
    paddingTop: wp(16),
  },

  listItem: {
    flexDirection: 'row',
    backgroundColor: novelDesign.color.bg.surface,
    borderRadius: sp(12),
    padding: wp(16),
    marginBottom: wp(12),
    shadowColor: colors.novelText,
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.15,
    shadowRadius: 4,
    elevation: 3,
  },

  listCover: {
    width: wp(60),
    height: wp(80),
    borderRadius: sp(6),
    overflow: 'hidden',
    backgroundColor: novelDesign.color.bg.elevated,
  },

  listCoverImage: {
    width: '100%',
    height: '100%',
  },

  listContent: {
    flex: 1,
    marginLeft: wp(12),
    justifyContent: 'space-between',
  },

  listHeader: {
    flex: 1,
  },

  listTitle: {
    fontSize: fp(16),
    fontWeight: '600',
    color: colors.novelText,
    lineHeight: fp(20),
    marginBottom: wp(4),
  },

  listAuthor: {
    fontSize: fp(14),
    color: colors.novelText,
    opacity: 0.7,
    lineHeight: fp(16),
    marginBottom: wp(4),
  },

  listMeta: {
    fontSize: fp(12),
    color: colors.novelText,
    opacity: 0.6,
    lineHeight: fp(14),
  },

  listFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: wp(8),
  },

  listProgress: {
    fontSize: fp(12),
    color: novelDesign.color.brand.primary,
    fontWeight: '500',
  },

  listStatus: {
    fontSize: fp(12),
    color: colors.novelText,
    opacity: 0.6,
  },

  // 瀑布流样式
  waterfallContainer: {
    flex: 1,
    paddingHorizontal: wp(12),
    paddingTop: wp(16),
  },

  waterfallGrid: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },

  waterfallColumn: {
    flex: 1,
  },

  // 列表分隔符
  listSeparator: {
    height: wp(1),
    backgroundColor: novelDesign.color.border.subtle,
    marginHorizontal: wp(16),
    opacity: 0.3,
  },

  // 空状态增强样式
  emptyTitle: {
    fontSize: fp(16),
    color: colors.novelText,
    textAlign: 'center',
    marginBottom: wp(8),
    opacity: 0.7,
  },

  emptyAction: {
    paddingHorizontal: wp(24),
    paddingVertical: wp(12),
    backgroundColor: novelDesign.color.brand.primary,
    borderRadius: sp(8),
  },

  emptyActionText: {
    fontSize: fp(14),
    color: novelDesign.color.text.inverse,
    fontWeight: '500',
  },

  waterfallBookItem: {
    backgroundColor: novelDesign.color.bg.surface,
    borderRadius: sp(12),
    marginBottom: wp(16),
  },

  // 新的waterfall布局样式
  waterfallItemContainer: {
    marginBottom: wp(16),
    position: 'relative',        // 新增：让子元素 absolute 基于此
  },

  waterfallCoverContainer: {
    position: 'absolute',        // 新增
    top: 0,                      // 顶部贴齐父容器
    left: wp(24),                // 保持和原来一样的左侧间距
    zIndex: 2,                   // 确保盖在信息卡之上
    borderRadius: sp(8),
    backgroundColor: colors.novelBookBackground,
    overflow: 'hidden',
    width: wp(100),              // 保留原来固定宽度
    aspectRatio: 0.65,           // 保留原来高宽比
  },

  waterfallBookInfo: {
    height: fp(250),
    backgroundColor: colors.novelSecondaryBackground + '80',
    borderRadius: sp(12),
    padding: wp(12),
    marginHorizontal: wp(3),
    marginTop: wp(80),           // 根据封面高度（~wp(90)）调整，让它正好露出底部
    paddingTop: wp(80),
    zIndex: 1,                   // 放到封面下方
  },

  waterfallCoverImage: {
    width: '100%',
    height: '100%',
  },

  waterfallPlaceholderCover: {
    width: '100%',
    height: '100%',
    backgroundColor: colors.novelMain,
    justifyContent: 'center',
    alignItems: 'center',
  },

  waterfallPlaceholderText: {
    fontSize: fp(12),
    color: colors.novelBackground,
    opacity: 0.8,
  },

  waterfallTitleRow: {
    height: wp(50),
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
  },

  waterfallMenuButton: {
    padding: wp(4),
    marginLeft: wp(8),
  },

  waterfallCurrentChapter: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    opacity: 0.8,
    paddingTop: wp(8),
    marginBottom: wp(6),
  },

  waterfallDescription: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    opacity: 0.8,
    lineHeight: fp(16),
    marginBottom: wp(4),
  },

  menuButtonText: {
    fontSize: fp(16),
    color: colors.novelTextGray,
    opacity: 0.8,
  },

  waterfallBookTitle: {
    fontSize: fp(15),
    fontWeight: '600',
    color: colors.novelText,
    lineHeight: fp(20),
  },

  waterfallBookAuthor: {
    fontSize: fp(13),
    color: colors.novelText,
    opacity: 0.7,
    lineHeight: fp(18),
    marginBottom: wp(6),
  },

  waterfallBookDescription: {
    fontSize: fp(12),
    color: colors.novelTextGray + '80',
    opacity: 0.6,
    lineHeight: fp(16),
    marginBottom: wp(5),
  },

  waterfallBookProgress: {
    fontSize: fp(12),
    color: colors.novelMain,
    fontWeight: '500',
  },

  // BookItem 组件样式
  listBookItem: {
    flexDirection: 'row',
    backgroundColor: 'transparent',
    marginBottom: wp(12),
  },

  gridBookItem: {
    width: (screenWidth - wp(32) - wp(20)) / 3,
    marginBottom: wp(16),
  },

  listBookCover: {
    width: wp(70),
    height: wp(95),
    borderRadius: sp(8),
    overflow: 'hidden',
    backgroundColor: colors.novelDivider,
  },

  gridBookCover: {
    borderRadius: sp(8),
    width: '100%',
    aspectRatio: 0.75,
    backgroundColor: colors.novelDivider,
  },

  listBookInfo: {
    flex: 1,
    marginLeft: wp(12),
    justifyContent: 'space-between',
  },

  listBookHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: wp(4),
  },

  listBookTitleContainer: {
    justifyContent: 'center',
  },

  listBookTitle: {
    marginTop: wp(10),
  },

  listMenuButton: {
    marginTop: wp(5),
    marginLeft: wp(8),
  },

  listCurrentChapter: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    opacity: 0.8,
  },

  listLastUpdate: {
    fontSize: fp(11),
    color: colors.novelTextGray,
    opacity: 0.8,
  },

  gridBookInfo: {
  },

  gridBookContent: {
    flex: 1,
    padding: wp(2),
    height: wp(50),
  },

  gridMenuButton: {
    padding: wp(4),
    marginLeft: wp(8),
  },

  gridChapterRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: wp(2),
  },

  gridCurrentChapter: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    opacity: 0.8,
  },

  selectedBookItem: {
    borderWidth: 2,
    borderColor: colors.novelMain,
  },

  bookCoverContainer: {
    position: 'relative',
  },

  // 标签样式
  bookTag: {
    position: 'absolute',
    top: wp(6),
    right: wp(6),
    paddingHorizontal: wp(6),
    paddingVertical: wp(2),
    backgroundColor: novelDesign.color.brand.primary,
    borderRadius: sp(2),
    zIndex: 1,
  },

  bookTagText: {
    fontSize: fp(7),
    color: novelDesign.color.text.inverse,
    fontWeight: '500',
  },

  checkboxText: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: 'bold',
  },

  progressContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    height: wp(3),
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },

  progressBar: {
    height: '100%',
    backgroundColor: colors.novelMain,
  },

  bookTitle: {
    fontSize: fp(12),
    fontWeight: '600',
    color: colors.novelText,
    marginBottom: wp(6),
  },

  bookAuthor: {
    fontSize: fp(13),
    color: colors.novelText,
    opacity: 0.7,
    lineHeight: fp(18),
    marginBottom: wp(6),
  },

  bookDescription: {
    fontSize: fp(12),
    color: colors.novelText,
    opacity: 0.6,
    lineHeight: fp(16),
    marginBottom: wp(4),
  },

  lastReadTime: {
    fontSize: fp(10),
    color: colors.novelMain,
    fontWeight: '500',
  },

  // 编辑模式样式
  selectionOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: sp(8),
  },

  selectionCheckbox: {
    width: sp(24),
    height: sp(24),
    borderRadius: sp(12),
    backgroundColor: colors.novelBackground,
    borderWidth: 2,
    borderColor: colors.novelMain,
    justifyContent: 'center',
    alignItems: 'center',
  },

  selectedCheckbox: {
    backgroundColor: colors.novelMain,
  },

  checkboxIcon: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: 'bold',
  },

  // 编辑工具栏样式
  toolbar: {
    backgroundColor: novelDesign.color.bg.surface,
    borderTopWidth: 1,
    borderTopColor: novelDesign.color.border.subtle,
    paddingHorizontal: wp(20),
    paddingVertical: wp(16),
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-end',
  },

  toolbarButton: {
    paddingHorizontal: wp(16),
    paddingVertical: wp(8),
    borderRadius: sp(6),
    backgroundColor: novelDesign.color.brand.primary,
  },

  toolbarButtonText: {
    fontSize: fp(14),
    color: novelDesign.color.text.inverse,
    fontWeight: '500',
  },

  editToolbar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: novelDesign.color.bg.surface,
    borderTopWidth: 1,
    borderTopColor: novelDesign.color.border.subtle,
    paddingHorizontal: wp(20),
    paddingVertical: wp(16),
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },

  editToolbarLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(16),
  },

  editToolbarCenter: {
    flex: 1,
    alignItems: 'center',
  },

  editToolbarRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(16),
  },

  editToolbarButton: {
    paddingHorizontal: wp(16),
    paddingVertical: wp(8),
    borderRadius: sp(6),
    backgroundColor: 'transparent',
  },

  editToolbarButtonPrimary: {
    backgroundColor: colors.novelMain,
  },

  editToolbarButtonDanger: {
    backgroundColor: colors.novelError || '#FF4444',
  },

  editToolbarButtonText: {
    fontSize: fp(14),
    color: novelDesign.color.text.primary,
    fontWeight: '500',
  },

  editToolbarButtonTextPrimary: {
    color: colors.novelBackground,
  },

  editToolbarSelectedText: {
    fontSize: fp(14),
    color: novelDesign.color.text.primary,
    fontWeight: '500',
  },

  selectionCount: {
    fontSize: fp(14),
    color: novelDesign.color.text.primary,
    fontWeight: '500',
  },

  editActionButton: {
    paddingHorizontal: wp(16),
    paddingVertical: wp(8),
    borderRadius: sp(6),
    backgroundColor: novelDesign.color.brand.primary,
  },

  editActionButtonText: {
    fontSize: fp(14),
    color: novelDesign.color.text.inverse,
    fontWeight: '500',
  },

  deleteButton: {
    backgroundColor: novelDesign.color.status.danger,
  },

  deleteButtonText: {
    color: novelDesign.color.text.inverse,
  },

  disabledButton: {
    backgroundColor: colors.novelDivider,
    opacity: 0.5,
  },

  disabledButtonText: {
    color: colors.novelText,
    opacity: 0.5,
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

  loadingSpinner: {
    marginRight: wp(10),
  },

  spinnerText: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },

  refreshText: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },

  // 推荐流样式
  recommendationSection: {
    backgroundColor: colors.novelBackground,
    marginTop: wp(16),
    paddingTop: wp(20),
  },

  recommendationContainer: {
    backgroundColor: colors.novelBackground,
    paddingBottom: wp(20),
  },

  recommendationHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: wp(20),
    marginBottom: wp(16),
  },

  recommendationTitle: {
    fontSize: fp(16),
    fontWeight: '400',
    color: colors.novelTextGray + '50',
  },

  recommendationMore: {
    fontSize: fp(14),
    color: colors.novelMain,
    fontWeight: '500',
  },

  recommendationContent: {
    paddingHorizontal: wp(20),
  },

  recommendationGrid: {
    flexDirection: 'row',
    gap: wp(15),
  },

  recommendationColumn: {
    flex: 1,
  },

  // 新的推荐流item样式
  recommendationItem: {
    backgroundColor: colors.novelSecondaryBackground + '80',
    borderRadius: sp(12),
    marginBottom: wp(16),
    overflow: 'hidden',
  },

  recommendationItemCover: {
    width: '100%',
    aspectRatio: 0.75,
    backgroundColor: colors.novelDivider,
  },

  recommendationItemInfo: {
    padding: wp(12),
  },

  recommendationItemTitle: {
    fontSize: fp(14),
    fontWeight: '600',
    color: colors.novelText,
    lineHeight: fp(18),
    marginBottom: wp(6),
  },

  recommendationItemDescription: {
    fontSize: fp(12),
    color: colors.novelText,
    opacity: 0.7,
    lineHeight: fp(16),
    marginBottom: wp(8),
  },

  recommendationItemTag: {
    alignSelf: 'flex-start',
    paddingHorizontal: wp(8),
    paddingVertical: wp(4),
    backgroundColor: colors.novelMain,
    borderRadius: sp(4),
  },

  recommendationItemTagText: {
    fontSize: fp(10),
    color: colors.novelBackground,
    fontWeight: '500',
  },

  // 视图切换器样式
  viewSwitcher: {
    flexDirection: 'row',
    backgroundColor: colors.novelBackground,
    borderRadius: sp(8),
    padding: wp(4),
    marginHorizontal: wp(16),
    marginBottom: wp(12),
  },

  verticalDivider: {
    width: 1,
    marginHorizontal: wp(4),
    alignSelf: 'center',
    height: wp(10),
    backgroundColor: colors.novelDivider,
  },

  viewOption: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: wp(8),
    paddingHorizontal: wp(12),
    borderRadius: sp(6),
    gap: wp(6),
  },

  activeViewOption: {
    backgroundColor: colors.novelMain,
  },

  disabledViewOption: {
    opacity: 0.5,
  },

  viewOptionIcon: {
    fontSize: fp(16),
    color: colors.novelText,
    opacity: 0.7,
  },

  activeViewOptionIcon: {
    color: colors.novelBackground,
    opacity: 1,
  },

  viewOptionLabel: {
    fontSize: fp(14),
    color: colors.novelText,
    opacity: 0.7,
  },

  activeViewOptionLabel: {
    color: colors.novelBackground,
    opacity: 1,
    fontWeight: '500',
  },

  // 加载更多指示器样式
  loadMoreContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(20),
    gap: wp(8),
  },

  loadMoreText: {
    fontSize: fp(14),
    color: colors.novelText,
    opacity: 0.6,
  },

  loadMoreErrorText: {
    fontSize: fp(14),
    color: colors.novelError || '#FF4444',
    textAlign: 'center',
    marginBottom: wp(12),
  },

  retryButton: {
    paddingHorizontal: wp(20),
    paddingVertical: wp(10),
    backgroundColor: colors.novelMain,
    borderRadius: sp(6),
    alignSelf: 'center',
  },

  retryButtonText: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: '500',
  },

  loadMoreSpinner: {
    marginRight: wp(8),
  },

  endLine: {
    width: wp(30),
    height: wp(1),
    backgroundColor: colors.novelDivider,
  },

  endText: {
    fontSize: fp(12),
    color: colors.novelText,
    opacity: 0.5,
    marginHorizontal: wp(10),
  },

  // TopBar样式
  topBar: {
    flexDirection: 'row',
    backgroundColor: colors.novelBackground,
    paddingHorizontal: wp(16),
    paddingVertical: wp(12),
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
    justifyContent: 'space-around',
  },

  topBarButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: wp(8),
    paddingHorizontal: wp(12),
    borderRadius: sp(6),
    gap: wp(6),
  },

  activeTopBarButton: {
    backgroundColor: colors.novelMain,
  },

  topBarButtonIcon: {
    fontSize: fp(16),
    color: colors.novelText,
    opacity: 0.7,
  },

  activeTopBarButtonIcon: {
    color: colors.novelBackground,
    opacity: 1,
  },

  topBarButtonLabel: {
    fontSize: fp(14),
    color: colors.novelText,
    opacity: 0.7,
  },

  activeTopBarButtonLabel: {
    color: colors.novelBackground,
    opacity: 1,
    fontWeight: '500',
  },

  // 图片占位符样式
  imagePlaceholder: {
    width: '100%',
    height: wp(240),
    backgroundColor: colors.novelDivider,
    justifyContent: 'center',
    alignItems: 'center',
  },

  placeholderIcon: {
    fontSize: fp(24),
    color: colors.novelText,
    opacity: 0.3,
    marginBottom: wp(4),
  },

  placeholderText: {
    fontSize: fp(12),
    color: colors.novelText,
    opacity: 0.5,
    textAlign: 'center',
  },

  // 统一滚动视图样式
  unifiedScrollContainer: {
    flex: 1,
    backgroundColor: colors.novelBackground,
  },

  bookshelfContentWrapper: {
    backgroundColor: colors.novelBackground,
  },

  recommendationWrapper: {
    backgroundColor: colors.novelBackground,
    marginTop: wp(20),
    paddingTop: wp(20),
    borderTopLeftRadius: sp(16),
    borderTopRightRadius: sp(16),
  },

  // 优化后的TopBar样式
  topBarContainer: {
    flexDirection: 'row',
    backgroundColor: colors.novelBackground,
    paddingHorizontal: wp(16),
    paddingVertical: wp(4),
    alignItems: 'center',
    justifyContent: 'space-between',
  },

  topBarLeft: {
    flex: 1,
    alignItems: 'flex-start',
  },

  topBarCenter: {
    flex: 2,
    flexDirection: 'row',
    justifyContent: 'center',
    gap: wp(8),
  },

  topBarRight: {
    flex: 1,
    alignItems: 'flex-end',
  },

  topBarActionsRow: {
    flexDirection: 'row',
    gap: wp(8),
  },

  adBanner: {
    width: wp(181),
    height: wp(30),
    backgroundColor: colors.novelDivider,
    borderRadius: sp(6),
    justifyContent: 'center',
    alignItems: 'center',
  },

  adBannerText: {
    fontSize: fp(12),
    color: colors.novelText,
    fontWeight: '500',
  },

  topBarActionButton: {
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: sp(6),
  },

  activeTopBarActionIcon: {
    color: colors.novelMain,
    opacity: 1,
  },

  topBarActionLabel: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '800',
    textAlign: 'center',
  },

  topBarActionLabelDisabled: {
    opacity: 0.3,
  },

  activeTopBarActionLabel: {
    color: colors.novelMain,
    opacity: 1,
    fontWeight: '500',
  },
});
};
