import { StyleSheet } from 'react-native';
import { createNovelDesignUI } from '../../../../../design-system/novelDesign';
import { NovelColors } from '../../../../../utils/theme';
import { wp, hp, fp } from '../../../../../utils/theme';

export const createCommunityPageStyles = (colors: NovelColors) => {
  const novelDesign = createNovelDesignUI(colors);
  const { color } = novelDesign;
  const brandPrimary = color.brand.primary;
  const danger = color.status.danger;
  const textPrimary = color.text.primary;
  const textSecondary = color.text.secondary;
  const surface = color.bg.surface;
  const canvas = color.bg.canvas;
  const elevated = color.bg.elevated;
  const borderSubtle = color.border.subtle;
  const borderStrong = color.border.strong;

  return StyleSheet.create({
  // 主容器
  container: {
    flex: 1,
    backgroundColor: canvas,
  },

  // 顶部导航栏
  topBar: {
    backgroundColor: surface,
    paddingHorizontal: wp(16),
    paddingVertical: hp(12),
    borderBottomWidth: 1,
    borderBottomColor: borderSubtle,
  },

  topBarContent: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },

  topBarTitle: {
    fontSize: fp(18),
    fontWeight: '600',
    color: textPrimary,
  },

  topBarActions: {
    flexDirection: 'row',
    alignItems: 'center',
  },

  topBarButton: {
    padding: wp(8),
    marginLeft: wp(8),
  },

  topBarButtonText: {
    fontSize: fp(14),
    color: brandPrimary,
  },

  // 标签页
  tabContainer: {
    backgroundColor: surface,
    paddingHorizontal: wp(16),
    paddingVertical: hp(8),
    borderBottomWidth: 1,
    borderBottomColor: borderSubtle,
  },

  tabScrollView: {
    flexGrow: 0,
  },

  tabItem: {
    paddingHorizontal: wp(16),
    paddingVertical: hp(8),
    marginRight: wp(16),
    borderRadius: wp(16),
    backgroundColor: 'transparent',
  },

  activeTabItem: {
    backgroundColor: elevated,
  },

  tabText: {
    fontSize: fp(14),
    color: textSecondary,
    fontWeight: '500',
  },

  activeTabText: {
    color: brandPrimary,
    fontWeight: '600',
  },

  // 帖子列表
  postList: {
    flex: 1,
    backgroundColor: novelDesign.color.bg.canvas,
  },

  // 顶部分割线
  topDivider: {
    height: hp(8),
    backgroundColor: novelDesign.color.bg.elevated,
  },

  // 热门评论标题
  hotCommentHeader: {
    paddingHorizontal: wp(16),
    paddingVertical: hp(12),
    backgroundColor: novelDesign.color.bg.surface,
  },

  hotCommentTitle: {
    fontSize: fp(16),
    fontWeight: '600',
    color: textPrimary,
  },

  postItem: {
    backgroundColor: novelDesign.color.bg.surface,
    paddingHorizontal: wp(16),
    paddingVertical: hp(16),
  },

  postHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: hp(12),
  },

  // 头部内容区域
  headerContent: {
    flex: 1,
    marginLeft: wp(12),
  },

  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
  },

  sourceText: {
    fontSize: fp(12),
    color: textSecondary,
    marginLeft: wp(8),
  },

  // 头部操作按钮区域
  headerActions: {
    flexDirection: 'row',
    alignItems: 'center',
  },

  subscribeButton: {
    backgroundColor: brandPrimary,
    paddingHorizontal: wp(12),
    paddingVertical: hp(6),
    borderRadius: wp(12),
    marginRight: wp(8),
  },

  subscribeButtonText: {
    fontSize: fp(12),
    color: color.text.inverse,
    fontWeight: '500',
  },

  // 底部分割线
  bottomDivider: {
    height: 1,
    backgroundColor: borderSubtle,
    marginHorizontal: wp(16),
  },

  userAvatar: {
    width: wp(40),
    height: wp(40),
    borderRadius: wp(20),
    backgroundColor: elevated,
    marginRight: wp(12),
  },

  userInfo: {
    flex: 1,
  },

  userName: {
    fontSize: fp(14),
    fontWeight: '600',
    color: textPrimary,
    marginBottom: hp(2),
  },

  userLevel: {
    fontSize: fp(12),
    color: textSecondary,
  },

  publishTime: {
    fontSize: fp(12),
    color: textSecondary,
  },

  postTitle: {
    fontSize: fp(16),
    fontWeight: 'bold',
    color: textPrimary,
    marginBottom: hp(8),
  },

  postContent: {
    fontSize: fp(14),
    lineHeight: fp(20),
    color: textPrimary,
    marginBottom: hp(12),
  },

  postImages: {
    marginBottom: hp(12),
  },

  postImage: {
    width: wp(120),
    height: wp(120),
    borderRadius: wp(8),
    backgroundColor: elevated,
    marginRight: wp(8),
  },

  // 操作栏样式
  postActions: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around',
    paddingTop: hp(12),
  },

  actionButton: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: hp(4),
    paddingHorizontal: wp(8),
  },

  actionIcon: {
    fontSize: fp(16),
    color: textSecondary,
    marginRight: wp(4),
  },

  likedIcon: {
    color: danger,
  },

  actionText: {
    fontSize: fp(12),
    color: textSecondary,
  },

  likedText: {
    color: brandPrimary,
  },

  moreButton: {
    padding: wp(8),
  },

  moreIcon: {
    fontSize: fp(16),
    color: textSecondary,
  },

  // 热门标签
  hotBadge: {
    position: 'absolute',
    top: hp(8),
    right: wp(16),
    backgroundColor: danger,
    paddingHorizontal: wp(6),
    paddingVertical: hp(2),
    borderRadius: wp(4),
  },

  hotBadgeText: {
    fontSize: fp(10),
    color: color.text.inverse,
    fontWeight: '500',
  },

  // 置顶标签
  topBadge: {
    position: 'absolute',
    top: hp(8),
    right: wp(16),
    backgroundColor: brandPrimary,
    paddingHorizontal: wp(6),
    paddingVertical: hp(2),
    borderRadius: wp(4),
  },

  topBadgeText: {
    fontSize: fp(10),
    color: color.text.inverse,
    fontWeight: '500',
  },

  // 空状态
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: wp(32),
  },

  emptyIcon: {
    fontSize: fp(64),
    color: borderStrong,
    marginBottom: hp(16),
  },

  emptyTitle: {
    fontSize: fp(18),
    fontWeight: '600',
    color: textPrimary,
    marginBottom: hp(8),
    textAlign: 'center',
  },

  emptyDescription: {
    fontSize: fp(14),
    color: textSecondary,
    textAlign: 'center',
    lineHeight: fp(20),
    marginBottom: hp(24),
  },

  emptyButton: {
    backgroundColor: brandPrimary,
    paddingHorizontal: wp(24),
    paddingVertical: hp(12),
    borderRadius: wp(8),
  },

  emptyButtonText: {
    fontSize: fp(14),
    color: color.text.inverse,
    fontWeight: '600',
  },

  // 加载状态
  loadingContainer: {
    paddingVertical: hp(16),
    alignItems: 'center',
  },

  loadingText: {
    fontSize: fp(14),
    color: textSecondary,
    marginTop: hp(8),
  },

  // 错误状态
  errorContainer: {
    paddingVertical: hp(16),
    alignItems: 'center',
  },

  errorText: {
    fontSize: fp(14),
    color: danger,
    textAlign: 'center',
  },

  retryButton: {
    marginTop: hp(8),
    paddingHorizontal: wp(16),
    paddingVertical: hp(8),
    backgroundColor: brandPrimary,
    borderRadius: wp(4),
  },

  retryButtonText: {
    fontSize: fp(12),
    color: color.text.inverse,
    fontWeight: '500',
  },

  // 发布按钮
  floatingButton: {
    position: 'absolute',
    bottom: hp(24),
    right: wp(24),
    width: wp(56),
    height: wp(56),
    borderRadius: wp(28),
    backgroundColor: brandPrimary,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 4,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
  },

  floatingButtonIcon: {
    fontSize: fp(24),
    color: color.text.inverse,
  },

  // 筛选栏
  filterContainer: {
    backgroundColor: surface,
    paddingHorizontal: wp(16),
    paddingVertical: hp(8),
    borderBottomWidth: 1,
    borderBottomColor: borderSubtle,
  },

  filterScrollView: {
    flexGrow: 0,
  },

  filterSpacer: {
    width: 16,
  },

  filterItem: {
    paddingHorizontal: wp(12),
    paddingVertical: hp(6),
    marginRight: wp(8),
    borderRadius: wp(12),
    backgroundColor: surface,
    borderWidth: 1,
    borderColor: borderSubtle,
  },

  activeFilterItem: {
    backgroundColor: elevated,
    borderColor: brandPrimary,
  },

  filterText: {
    fontSize: fp(12),
    color: textSecondary,
  },

  activeFilterText: {
    color: brandPrimary,
    fontWeight: '500',
  },

  // 圈子相关样式
  circleTabContainer: {
    backgroundColor: surface,
    paddingVertical: hp(16),
    borderBottomWidth: 1,
    borderBottomColor: borderSubtle,
  },

  circleTabHeader: {
    paddingHorizontal: wp(16),
    marginBottom: hp(12),
  },

  circleTabTitle: {
    fontSize: fp(16),
    fontWeight: '600',
    color: textPrimary,
  },

  circleScrollView: {
    paddingHorizontal: wp(16),
  },

  circleItem: {
    alignItems: 'center',
    marginRight: wp(16),
    width: wp(60),
  },

  circleIconContainer: {
    width: wp(50),
    height: wp(50),
    borderRadius: wp(25),
    backgroundColor: elevated,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: hp(8),
    borderWidth: 2,
    borderColor: 'transparent',
  },

  activeCircleIconContainer: {
    borderColor: brandPrimary,
    backgroundColor: elevated,
  },

  circleIcon: {
    fontSize: fp(24),
  },

  circleName: {
    fontSize: fp(12),
    color: textSecondary,
    textAlign: 'center',
    fontWeight: '500',
  },

  activeCircleName: {
    color: brandPrimary,
    fontWeight: '600',
  },

  allCircleItem: {
    alignItems: 'center',
    marginRight: wp(16),
    width: wp(60),
  },

  allCircleIconContainer: {
    width: wp(50),
    height: wp(50),
    borderRadius: wp(25),
    backgroundColor: elevated,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: hp(8),
    borderWidth: 2,
    borderColor: 'transparent',
  },

  activeAllCircleIconContainer: {
    borderColor: brandPrimary,
    backgroundColor: brandPrimary,
  },

  allCircleIcon: {
    fontSize: fp(20),
    color: textSecondary,
  },

  activeAllCircleIcon: {
    color: color.text.inverse,
  },

  allCircleName: {
    fontSize: fp(12),
    color: textSecondary,
    textAlign: 'center',
    fontWeight: '500',
  },

  activeAllCircleName: {
    color: brandPrimary,
    fontWeight: '600',
  },
});
};
