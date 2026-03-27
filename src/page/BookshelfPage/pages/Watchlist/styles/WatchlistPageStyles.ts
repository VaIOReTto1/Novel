import { StyleSheet } from 'react-native';
import { wp, hp, fp, sp } from '../../../../../utils/theme/dimensions';
import { NovelColors } from '../../../../../utils/theme';
import { screenWidth } from '../../../../../page/ProfilePage/utils/constants';

export const createWatchlistPageStyles = (colors: NovelColors) => StyleSheet.create({
  // 容器样式
  container: {
    flex: 1,
    backgroundColor: colors.novelBackground,
  },

  // TopBar 样式
  topBarContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: wp(2),
    paddingHorizontal: wp(16),
    backgroundColor: colors.novelBackground,
  },

  topBarLeft: {
    flex: 1,
  },

  topBarCenter: {
    flex: 2,
  },

  topBarRight: {
    flex: 1,
    alignItems: 'flex-end',
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
    paddingHorizontal: wp(12),
  },

  topBarActionLabel: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },

  activeTopBarActionLabel: {
    color: colors.novelMain,
  },

  // 编辑工具栏样式
  editToolbar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: wp(16),
    paddingVertical: hp(12),
    backgroundColor: colors.novelSecondaryBackground,
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
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

  editToolbarText: {
    fontSize: fp(14),
    color: colors.novelText,
    marginLeft: wp(8),
  },

  editToolbarButton: {
    paddingHorizontal: wp(12),
    paddingVertical: hp(6),
    borderRadius: wp(6),
  },

  editToolbarButtonText: {
    fontSize: fp(14),
    fontWeight: '500',
    color: colors.novelText,
  },

  editToolbarCenter: {
    flex: 1,
    alignItems: 'center',
  },

  editToolbarSelectedText: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },

  selectAllButton: {
    backgroundColor: colors.novelMain,
  },

  selectAllButtonText: {
    color: colors.novelBackground,
  },

  deleteButton: {
    backgroundColor: colors.novelError,
  },

  deleteButtonText: {
    color: colors.novelBackground,
  },

  disabledButton: {
    backgroundColor: colors.novelLightGray,
  },

  disabledButtonText: {
    color: colors.novelTextGray,
    opacity: 0.5,
  },

  // 网格视图样式
  gridContainer: {
    paddingHorizontal: wp(12),
    paddingTop: wp(8),
  },

  gridItem: {
    backgroundColor: colors.novelBackground,
    borderRadius: wp(8),
    width: (screenWidth - wp(32) - wp(20)) / 3,
  },

  selectedGridItem: {
    borderWidth: 2,
    borderColor: colors.novelMain,
  },

  gridItemImageContainer: {
    position: 'relative',
  },

  gridItemImage: {
    width: '100%',
    aspectRatio: 3 / 4,
    borderRadius: wp(8),
    backgroundColor: colors.novelLightGray,
  },

  gridItemContent: {
    padding: wp(8),
  },

  gridItemTitle: {
    fontSize: fp(13),
    fontWeight: '600',
    color: colors.novelText,
    lineHeight: fp(16),
    marginBottom: hp(4),
  },

  gridItemSubtitle: {
    fontSize: fp(11),
    color: colors.novelTextGray,
    lineHeight: fp(14),
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

  updateBadge: {
    position: 'absolute',
    top: wp(6),
    right: wp(6),
    backgroundColor: colors.novelError,
    paddingHorizontal: wp(6),
    paddingVertical: wp(2),
    borderRadius: wp(8),
  },

  updateBadgeText: {
    fontSize: fp(10),
    color: colors.novelBackground,
    fontWeight: '500',
  },

  // 剧集封面容器
  dramaCoverContainer: {
    position: 'relative',
  },

  gridDramaCover: {
    width: '100%',
    aspectRatio: 3 / 4, // 剧集封面比例
    backgroundColor: colors.novelLightGray,
  },

  // 剧集信息样式
  gridDramaInfo: {
    padding: wp(8),
  },

  gridDramaContent: {
    marginBottom: hp(8),
  },

  dramaTitle: {
    fontSize: fp(14),
    fontWeight: '600',
    color: colors.novelText,
    lineHeight: fp(18),
  },

  gridChapterRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },

  gridCurrentEpisode: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    flex: 1,
  },

  gridMenuButton: {
    padding: wp(4),
  },

  menuButtonText: {
    fontSize: fp(16),
    color: colors.novelTextGray,
  },

  // 列表视图样式
  listDramaItem: {
    flexDirection: 'row',
    padding: wp(12),
    backgroundColor: colors.novelBackground,
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
  },

  listDramaCover: {
    width: wp(60),
    height: wp(80),
    borderRadius: wp(6),
    backgroundColor: colors.novelLightGray,
    marginRight: wp(12),
  },

  listDramaInfo: {
    flex: 1,
  },

  listDramaHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: hp(4),
  },

  listDramaTitleContainer: {
    flex: 1,
    marginRight: wp(8),
  },

  listMenuButton: {
    padding: wp(4),
  },

  listCurrentEpisode: {
    fontSize: fp(12),
    color: colors.novelMain,
    marginBottom: hp(2),
  },

  listLastUpdate: {
    fontSize: fp(11),
    color: colors.novelTextGray,
  },

  // 瀑布流样式
  waterfallItemContainer: {
    flex: 1,
    margin: wp(4),
    backgroundColor: colors.novelBackground,
    borderRadius: wp(8),
    overflow: 'hidden',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },

  waterfallCoverContainer: {
    width: '100%',
  },

  waterfallDramaCover: {
    width: '100%',
    aspectRatio: 3 / 4,
    backgroundColor: colors.novelLightGray,
  },

  waterfallDramaInfo: {
    padding: wp(8),
  },

  waterfallTitleRow: {
    marginBottom: hp(6),
  },

  waterfallDramaTitle: {
    fontSize: fp(13),
    fontWeight: '600',
    color: colors.novelText,
    lineHeight: fp(16),
  },

  waterfallCurrentEpisode: {
    fontSize: fp(11),
    color: colors.novelMain,
    flex: 1,
  },

  waterfallMenuButton: {
    padding: wp(2),
  },

  waterfallDescription: {
    fontSize: fp(11),
    color: colors.novelTextGray,
    lineHeight: fp(14),
    marginTop: hp(4),
  },

  // 标签样式
  dramaTag: {
    position: 'absolute',
    top: wp(6),
    left: wp(6),
    backgroundColor: colors.novelMain,
    paddingHorizontal: wp(6),
    paddingVertical: wp(2),
    borderRadius: wp(4),
  },

  dramaTagText: {
    fontSize: fp(10),
    color: colors.novelBackground,
    fontWeight: '500',
  },

  // 选择状态样式
  selectedDramaItem: {
    backgroundColor: colors.novelChipBackground,
    borderWidth: 2,
    borderColor: colors.novelMain,
  },

  selectionOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    justifyContent: 'center',
    alignItems: 'center',
  },

  selectionCheckbox: {
    width: wp(24),
    height: wp(24),
    borderRadius: wp(12),
    borderWidth: 2,
    borderColor: colors.novelText,
    backgroundColor: 'transparent',
    justifyContent: 'center',
    alignItems: 'center',
  },

  selectedCheckbox: {
    backgroundColor: colors.novelMain,
    borderColor: colors.novelMain,
  },

  checkboxIcon: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: 'bold',
  },

  // 占位符样式
  imagePlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: colors.novelLightGray,
  },

  placeholderText: {
    fontSize: fp(12),
    color: colors.novelTextGray,
  },

  // 加载更多样式
  loadMoreContainer: {
    padding: wp(16),
    alignItems: 'center',
  },

  loadMoreText: {
    fontSize: fp(14),
    color: colors.novelTextGray,
    marginTop: hp(8),
  },

  loadingFooter: {
    paddingVertical: hp(16),
    alignItems: 'center',
  },

  loadingText: {
    fontSize: fp(14),
    color: colors.novelTextGray,
  },

  // 空状态样式
  emptyStateContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: wp(32),
  },

  emptyStateIcon: {
    fontSize: fp(64),
    marginBottom: hp(16),
  },

  emptyStateTitle: {
    fontSize: fp(18),
    fontWeight: '600',
    color: colors.novelText,
    marginBottom: hp(8),
    textAlign: 'center',
  },

  emptyStateDescription: {
    fontSize: fp(14),
    color: colors.novelTextGray,
    textAlign: 'center',
    lineHeight: fp(20),
    marginBottom: hp(24),
  },

  emptyStateButton: {
    backgroundColor: colors.novelMain,
    paddingHorizontal: wp(24),
    paddingVertical: hp(12),
    borderRadius: wp(8),
  },

  emptyStateButtonText: {
    fontSize: fp(14),
    color: colors.novelBackground,
    fontWeight: '600',
  },

  // 推荐区域样式
  recommendationSection: {
    backgroundColor: colors.novelBackground,
    marginTop: hp(8),
  },

  recommendationHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: wp(16),
    paddingVertical: hp(12),
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
  },

  recommendationTitle: {
    fontSize: fp(16),
    fontWeight: '600',
    color: colors.novelText,
  },

  recommendationMore: {
    fontSize: fp(14),
    color: colors.novelMain,
  },
});
