import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createCommentPageStyles = (colors: NovelColors) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.novelBackground,
    paddingHorizontal: wp(15),
  },

  scrollView: {
    flex: 1,
  },

  scrollContent: {
  },

  // 顶部Bar样式
  topBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: wp(10),
    paddingHorizontal: wp(5),
  },

  backButton: {
    padding: wp(5),
  },

  topBarTitle: {
    fontSize: sp(13),
    color: colors.novelText,
    fontWeight: '600',
  },

  placeholder: {
    width: wp(30),
  },

  // 搜索栏样式
  searchContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.novelSecondaryBackground + '80',
    borderRadius: sp(5),
    paddingHorizontal: wp(12),
    height: wp(36),
  },

  searchInput: {
    flex: 1,
    fontSize: sp(15),
    color: colors.novelTextGray,
    paddingVertical: 8,
  },

  searchButton: {
  },

  // 评分级别样式
  ratingSection: {
    backgroundColor: colors.novelSecondaryBackground + '80',
    paddingVertical: wp(15),
    paddingHorizontal: wp(15),
    marginBottom: wp(10),
    borderRadius: sp(12),
  },

  ratingTopRow: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    marginBottom: wp(8),
  },

  ratingBottomRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },

  ratingScoreText: {
    fontSize: sp(20),
    color: colors.novelText,
    fontWeight: 'bold',
    lineHeight: sp(20),
  },
  ratingScoreSubText: {
    fontSize: sp(10),
    flex: 1,
    color: colors.novelText,
    fontWeight: 'bold',
    marginHorizontal: wp(5),
    marginBottom: wp(1),
  },

  ratingCountText: {
    fontSize: sp(10),
    color: colors.novelTextGray,
  },

  ratingTipText: {
    fontSize: sp(10),
    color: colors.novelTextGray,
  },

  ratingSectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: wp(10),
  },

  ratingSectionTitle: {
    ...typography.labelLarge,
    color: colors.novelText,
    fontWeight: '600',
  },

  overallRating: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(5),
  },

  overallRatingText: {
    fontSize: sp(13),
    color: colors.novelMain,
    fontWeight: 'bold',
  },

  writeReviewButton: {
    backgroundColor: colors.novelMain,
    paddingHorizontal: wp(15),
    paddingVertical: wp(8),
    borderRadius: sp(16),
  },

  writeReviewText: {
    fontSize: sp(13),
    color: colors.novelBackground,
    fontWeight: '600',
  },

  // 分类标签样式
  categorySection: {
    paddingVertical: wp(15),
    marginBottom: wp(10),
    borderRadius: sp(12),
  },

  categoryTitle: {
    ...typography.labelLarge,
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(10),
  },

  categoryTabs: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: wp(8),
  },

  categoryTab: {
    paddingHorizontal: wp(12),
    paddingVertical: wp(6),
    borderRadius: sp(4),
    backgroundColor: colors.novelSecondaryBackground + '80',
  },

  categoryTabActive: {
    backgroundColor: colors.novelMain + '20',
  },

  categoryTabText: {
    fontSize: sp(13),
    color: colors.novelText,
  },

  categoryTabTextActive: {
    color: colors.novelMain,
    fontWeight: '800',
  },

  // 评论列表样式
  commentList: {
  },

  commentItem: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(12),
    marginBottom: wp(15),
  },

  commentHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: wp(10),
    justifyContent: 'space-between',
  },

  commentAvatar: {
    width: sp(40),
    height: sp(40),
    borderRadius: sp(20),
    backgroundColor: colors.novelBookBackground,
    marginRight: wp(12),
  },

  commentAvatarImage: {
    width: '100%',
    height: '100%',
    borderRadius: sp(20),
  },

  commentUserInfo: {
    flex: 1,
  },

  commentUserNameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(4),
  },

  commentTag: {
    marginTop: wp(3),
    paddingHorizontal: wp(6),
    paddingVertical: wp(2),
    borderRadius: sp(2),
    alignSelf: 'flex-start',
  },

  commentTag_first_comment: {
    backgroundColor: colors.novelMain + '20',
  },

  commentTag_true_fan: {
    backgroundColor: '#FF6B6B20',
  },

  commentTag_vip: {
    backgroundColor: '#FFD93D20',
  },

  commentTagText: {
    fontSize: sp(8),
    fontWeight: '600',
    color: colors.novelMain,
  },

  commentHeaderActions: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(8),
  },

  followButton: {
     paddingHorizontal: wp(12),
     paddingVertical: wp(4),
     backgroundColor: colors.novelSecondaryBackground + '80',
     borderRadius: wp(12),
   },
 
   followButtonText: {
     fontSize: sp(12),
     color: colors.novelMain,
     fontWeight: '500',
   },

  menuButton: {
    padding: wp(4),
  },

  commentUserName: {
    ...typography.labelLarge,
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(2),
  },

  commentTime: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
  },

  commentRating: {
    flexDirection: 'row',
    alignItems: 'center',
  },

  commentContentContainer: {
     marginBottom: wp(12),
   },

  commentContent: {
    fontSize: sp(16),
    color: colors.novelText,
    lineHeight: fp(20),
  },

  commentContentCollapsed: {
    // 样式在numberOfLines中处理
  },

  expandButton: {
     alignSelf: 'flex-start',
     marginTop: wp(4),
   },

  expandButtonInline: {
    alignSelf: 'flex-start',
    marginTop: wp(-4),
  },

  expandButtonFloating: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    backgroundColor: colors.novelBackground + 'E6',
    paddingLeft: wp(8),
  },

  expandButtonText: {
    fontSize: sp(15),
     lineHeight: fp(20),
    color: colors.novelMain,
    fontWeight: '500',
  },

  collapseButton: {
    alignSelf: 'flex-start',
  },

  commentMeta: {
     flexDirection: 'row',
     justifyContent: 'space-between',
     alignItems: 'center',
     marginBottom: wp(8),
   },

  commentRatingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(8),
  },

  readingTimeText: {
    fontSize: sp(12),
    color: colors.novelTextGray,
  },

  commentFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },

  viewCommentButton: {
    backgroundColor: colors.novelSecondaryBackground + '80',
     padding: wp(8),
     marginBottom: wp(12),
   },

  viewCommentText: {
    fontSize: sp(14),
    color: colors.novelMain,
  },

  commentDivider: {
    height: 1,
    backgroundColor: colors.novelDivider,
    marginHorizontal: -wp(16),
  },

  commentActions: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(20),
  },

  commentAction: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(5),
  },

  commentActionText: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
  },

  commentActionTextActive: {
    color: colors.novelMain,
  },

  // 星级评分样式
  starRating: {
    flexDirection: 'row',
    gap: wp(2),
  },

  // 空状态样式
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(100),
  },

  emptyText: {
    fontSize: sp(13),
    color: colors.novelTextGray,
    textAlign: 'center',
    marginBottom: wp(10),
  },

  emptySubText: {
    ...typography.bodyMedium,
    color: colors.novelTextGray,
    textAlign: 'center',
    opacity: 0.7,
  },

  // 加载状态样式
  loadingContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(20),
    gap: wp(8),
  },

  loadingText: {
    ...typography.labelLarge,
    color: colors.novelTextGray,
  },

  // 底部加载更多样式
  loadMoreContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(20),
  },

  loadMoreText: {
    ...typography.labelLarge,
    color: colors.novelTextGray,
  },

  // 结束线样式
  endContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: wp(20),
  },

  endLine: {
    width: wp(30),
    height: wp(1),
    backgroundColor: colors.novelDivider,
  },

  endText: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    opacity: 0.5,
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

  // WriteReview页面样式
  ratingContainer: {
    backgroundColor: colors.novelBackground,
    paddingVertical: wp(20),
    paddingHorizontal: wp(15),
    marginBottom: wp(15),
    borderRadius: sp(12),
    shadowColor: colors.novelText,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },

  ratingLabel: {
    ...typography.labelLarge,
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(10),
  },

  starsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(5),
    marginBottom: wp(10),
  },

  starButton: {
    padding: wp(2),
  },

  ratingText: {
    ...typography.bodyMedium,
    color: colors.novelTextGray,
  },

  inputSection: {
    backgroundColor: colors.novelBackground,
    paddingVertical: wp(15),
    paddingHorizontal: wp(15),
    marginBottom: wp(15),
    borderRadius: sp(12),
    shadowColor: colors.novelText,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },

  inputLabel: {
    ...typography.labelLarge,
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(10),
  },

  titleInput: {
    ...typography.bodyMedium,
    color: colors.novelText,
    backgroundColor: colors.novelBookBackground,
    borderRadius: sp(8),
    paddingHorizontal: wp(12),
    paddingVertical: wp(10),
    borderWidth: 1,
    borderColor: colors.novelDivider,
    marginBottom: wp(5),
  },

  contentInput: {
    ...typography.bodyMedium,
    color: colors.novelText,
    backgroundColor: colors.novelBookBackground,
    borderRadius: sp(8),
    paddingHorizontal: wp(12),
    paddingVertical: wp(10),
    borderWidth: 1,
    borderColor: colors.novelDivider,
    height: wp(120),
    marginBottom: wp(5),
  },

  charCount: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    textAlign: 'right',
  },

  submitButton: {
    backgroundColor: colors.novelMain,
    paddingHorizontal: wp(15),
    paddingVertical: wp(8),
    borderRadius: sp(16),
  },

  submitButtonDisabled: {
    backgroundColor: colors.novelTextGray,
    opacity: 0.6,
  },

  submitButtonText: {
    fontSize: sp(13),
    color: colors.novelBackground,
    fontWeight: '600',
  },

  submitButtonTextDisabled: {
    color: colors.novelBackground,
    opacity: 0.8,
  },

  tipSection: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    backgroundColor: colors.novelBookBackground,
    paddingVertical: wp(12),
    paddingHorizontal: wp(15),
    marginBottom: wp(20),
    borderRadius: sp(8),
    gap: wp(8),
  },

  tipText: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    flex: 1,
    lineHeight: fp(16),
  },

  // 评论详情页样式
  reviewDetailContainer: {
    backgroundColor: colors.novelBackground,
    paddingHorizontal: wp(4),
    paddingVertical: wp(4),
    marginBottom: wp(2),
  },
  userInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: wp(3),
  },
  userAvatar: {
    width: wp(12),
    height: wp(12),
    borderRadius: wp(6),
    marginRight: wp(3),
  },
  userDetails: {
    flex: 1,
  },
  userName: {
    fontSize: sp(14),
    color: colors.novelText,
    fontWeight: '500',
    marginBottom: wp(1),
  },
  ratingTime: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  starIcon: {
    marginRight: wp(0.5),
  },
  createTime: {
    fontSize: sp(12),
    color: colors.novelTextGray,
    marginLeft: wp(2),
  },
  reviewTitle: {
    fontSize: sp(16),
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(2),
    lineHeight: sp(22),
  },
  reviewContent: {
    fontSize: sp(14),
    color: colors.novelText,
    lineHeight: sp(20),
    marginBottom: wp(4),
  },
  actionBar: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingTop: wp(3),
    borderTopWidth: 0.5,
    borderTopColor: colors.novelDivider,
  },
  actionButton: {
    flexDirection: 'row',
    alignItems: 'center',
    marginRight: wp(6),
  },
  actionText: {
    fontSize: sp(12),
    color: colors.novelTextGray,
    marginLeft: wp(1),
  },
  actionTextActive: {
    color: colors.novelMain,
  },
  
  // 回复列表样式
  repliesSection: {
    backgroundColor: colors.novelBackground,
    paddingHorizontal: wp(4),
    paddingTop: wp(4),
  },
  repliesTitle: {
    fontSize: sp(14),
    color: colors.novelText,
    fontWeight: '500',
    marginBottom: wp(3),
  },
  replyItem: {
    flexDirection: 'row',
    paddingVertical: wp(3),
    borderBottomWidth: 0.5,
    borderBottomColor: colors.novelDivider,
  },
  replyAvatar: {
    width: wp(8),
    height: wp(8),
    borderRadius: wp(4),
    marginRight: wp(3),
  },
  replyContent: {
    flex: 1,
  },
  replyHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: wp(1),
  },
  replyUserName: {
    fontSize: sp(12),
    color: colors.novelText,
    fontWeight: '500',
  },
  replyTime: {
    fontSize: sp(11),
    color: colors.novelTextGray,
  },
  replyText: {
    fontSize: sp(13),
    color: colors.novelText,
    lineHeight: sp(18),
    marginBottom: wp(2),
  },
  replyLikeButton: {
    flexDirection: 'row',
    alignItems: 'center',
    alignSelf: 'flex-start',
  },
  replyLikeText: {
    fontSize: sp(11),
    color: colors.novelTextGray,
    marginLeft: wp(1),
  },
  replyLikeTextActive: {
    color: colors.novelMain,
  },

  // WriteReviewPage 样式
  writeReviewContainer: {
    flex: 1,
    backgroundColor: colors.novelSecondaryBackground,
  },

  writeReviewScrollContainer: {
    flex: 1,
  },

  writeReviewContentContainer: {
    padding: wp(16),
    paddingBottom: wp(100), // 为底部按钮留出空间
  },

  // 评分区域样式
  writeReviewRatingContainer: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(12),
    padding: wp(16),
    marginBottom: wp(16),
    alignItems: 'center',
    shadowColor: colors.novelText,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },

  writeReviewSectionTitle: {
    ...typography.labelLarge,
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(12),
  },

  writeReviewStarContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: wp(8),
  },

  writeReviewRatingLabel: {
    fontSize: sp(13),
    color: colors.novelTextGray,
    marginTop: wp(4),
  },

  // 表单区域样式
  writeReviewFormContainer: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(12),
    padding: wp(16),
    marginBottom: wp(16),
    shadowColor: colors.novelText,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },

  writeReviewInputSection: {
    marginBottom: wp(20),
  },

  writeReviewInputHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: wp(8),
  },

  writeReviewCharCount: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
  },

  writeReviewCharCountError: {
    color: colors.novelError,
  },

  writeReviewTitleInput: {
    ...typography.bodyMedium,
    color: colors.novelText,
    backgroundColor: colors.novelBookBackground,
    borderRadius: sp(8),
    padding: wp(12),
    borderWidth: 1,
    borderColor: colors.novelDivider,
    minHeight: wp(44),
  },

  writeReviewContentInput: {
    ...typography.bodyMedium,
    color: colors.novelText,
    backgroundColor: colors.novelBookBackground,
    borderRadius: sp(8),
    padding: wp(12),
    borderWidth: 1,
    borderColor: colors.novelDivider,
    minHeight: wp(120),
    maxHeight: wp(200),
    textAlignVertical: 'top',
  },

  writeReviewInputError: {
    borderColor: colors.novelError,
  },

  writeReviewErrorText: {
    ...typography.labelSmall,
    color: colors.novelError,
    marginTop: wp(4),
  },

  // 提交按钮区域样式
  writeReviewSubmitContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: colors.novelSecondaryBackground,
    borderTopWidth: 1,
    borderTopColor: colors.novelDivider,
    padding: wp(16),
  },

  writeReviewSubmitButton: {
    backgroundColor: colors.novelMain,
    borderRadius: sp(8),
    paddingVertical: wp(14),
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: wp(48),
  },

  writeReviewSubmitButtonDisabled: {
    backgroundColor: colors.novelTextGray,
    opacity: 0.6,
  },

  writeReviewSubmitButtonText: {
    ...typography.labelLarge,
    color: colors.novelBackground,
    fontWeight: '600',
  },

  writeReviewSubmitButtonTextDisabled: {
    color: colors.novelBackground,
  },

  writeReviewSubmitLoadingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },

  writeReviewSubmitLoadingIndicator: {
    marginRight: wp(8),
  },

  // 提示区域样式
  writeReviewTipsContainer: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(12),
    marginBottom: wp(16),
    overflow: 'hidden',
    shadowColor: colors.novelText,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },

  writeReviewTipsHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: wp(16),
    backgroundColor: colors.novelBookBackground,
  },

  writeReviewTipsTitle: {
    fontSize: sp(13),
    color: colors.novelText,
    fontWeight: '500',
  },

  writeReviewTipsToggle: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
  },

  writeReviewTipsToggleExpanded: {
    transform: [{ rotate: '180deg' }],
  },

  writeReviewTipsContent: {
    padding: wp(16),
    paddingTop: 0,
  },

  writeReviewTipItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: wp(8),
  },

  writeReviewTipBullet: {
    fontSize: sp(13),
    color: colors.novelMain,
    marginRight: wp(8),
    marginTop: wp(2),
  },

  writeReviewTipText: {
    flex: 1,
    fontSize: sp(13),
    color: colors.novelTextGray,
    lineHeight: fp(20),
  },

  // TopBar 样式补充
  titleContainer: {
    flex: 1,
    alignItems: 'center',
  },

  rightPlaceholder: {
    width: wp(30),
  },

  backArrow: {
    fontSize: sp(24),
    color: colors.novelText,
    fontWeight: 'bold',
  },
});