import { StyleSheet } from 'react-native';
import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';
import { wp, hp, sp, fp } from '../../../../utils/theme/dimensions';

/**
 * ReviewDetailPage 样式创建函数
 * 基于 NovelColors 主题系统，支持明暗模式切换
 */
export const createReviewDetailPageStyles = (colors: NovelColors) => {
  const novelDesign = createNovelDesignUI(colors);
  return StyleSheet.create({
    // 主容器
    container: {
      flex: 1,
      backgroundColor: novelDesign.color.bg.canvas,
    },

    // 滚动视图
    scrollView: {
      flex: 1,
    },

    scrollContent: {
      flexGrow: 1,
      paddingBottom: hp(100), // 增加底部边距，避免被输入框遮挡
    },

    // 顶部导航栏
    topBar: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(16),
      paddingVertical: hp(12),
      backgroundColor: novelDesign.color.bg.surface,
      borderBottomWidth: sp(1),
      borderBottomColor: novelDesign.color.border.subtle,
    },

    backButton: {
      padding: sp(8),
      borderRadius: sp(20),
    },

    backButtonText: {
      fontSize: fp(20),
      color: colors.novelText,
      fontWeight: '500',
    },

    topBarTitle: {
      fontSize: fp(18),
      fontWeight: '600',
      color: colors.novelText,
    },

    title: {
      fontSize: fp(18),
      fontWeight: '600',
      color: colors.novelText,
    },

    bookInfoContainer: {
      flex: 1,
      alignItems: 'center',
      paddingHorizontal: wp(16),
    },

    bookTitle: {
      fontSize: fp(16),
      fontWeight: '600',
      color: colors.novelText,
      textAlign: 'center',
    },

    authorName: {
      fontSize: fp(12),
      color: colors.novelTextGray,
      textAlign: 'center',
      marginTop: hp(2),
    },

    menuButton: {
      borderRadius: sp(20),
    },

    menuButtonText: {
      fontSize: fp(20),
      color: colors.novelText,
      fontWeight: '500',
    },

    placeholder: {
      width: wp(40),
    },

    // 加载状态
    loadingContainer: {
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
      paddingVertical: 40,
    },

    loadingText: {
      fontSize: fp(16),
      color: colors.novelTextGray,
    },

    // 评论详情容器
    reviewDetailContainer: {
      backgroundColor: novelDesign.color.bg.surface,
      marginHorizontal: wp(12),
      marginTop: hp(16),
      borderRadius: sp(12),
    },

    // 用户信息
    userInfoContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      marginBottom: hp(12),
    },

    avatarContainer: {
      width: sp(40),
      height: sp(40),
      borderRadius: sp(20),
      backgroundColor: colors.novelSecondaryBackground,
      justifyContent: 'center',
      alignItems: 'center',
      marginRight: wp(12),
    },

    userInfo: {
      flex: 1,
    },

    userName: {
      fontSize: fp(16),
      fontWeight: '800',
      color: colors.novelText,
      marginBottom: hp(4),
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

    ratingTimeContainer: {
      flexDirection: 'row',
      alignItems: 'center',
    },

    // 星级评分
    starsContainer: {
      flexDirection: 'row',
      marginRight: wp(8),
    },

    reviewTime: {
      fontSize: fp(12),
      color: colors.novelTextGray,
    },

    // 评论内容
    reviewTitle: {
      fontSize: fp(18),
      fontWeight: '600',
      color: colors.novelText,
      marginBottom: hp(8),
      lineHeight: fp(24),
    },

    reviewContent: {
      fontSize: fp(16),
      color: colors.novelText,
      lineHeight: fp(24),
      marginBottom: hp(16),
    },

    // 操作按钮
    actionContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingTop: hp(12),
      borderTopWidth: sp(1),
      borderTopColor: colors.novelDivider,
    },

    likeButton: {
      flexDirection: 'row',
      alignItems: 'center',
      paddingVertical: hp(8),
      paddingHorizontal: wp(12),
      borderRadius: sp(20),
      backgroundColor: colors.novelSecondaryBackground,
    },

    likeButtonActive: {
      backgroundColor: colors.novelMainLight,
    },

    likeCount: {
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginLeft: wp(4),
    },

    likeCountActive: {
      color: colors.novelBackground,
    },

    // 评论列表
    commentListContainer: {
      marginTop: hp(16),
    },

    commentListTitle: {
      fontSize: fp(18),
      fontWeight: '600',
      color: colors.novelText,
      marginLeft: wp(12),
    },

    commentItem: {
      backgroundColor: novelDesign.color.bg.surface,
      marginHorizontal: wp(12),
      marginBottom: hp(8),
      borderRadius: sp(8),
    },

    commentUserInfo: {
      flexDirection: 'row',
      alignItems: 'flex-start',
    },

    commentAvatar: {
      width: sp(32),
      height: sp(32),
      borderRadius: sp(16),
      backgroundColor: colors.novelSecondaryBackground,
      justifyContent: 'center',
      alignItems: 'center',
      marginRight: wp(8),
    },

    commentUserName: {
      fontSize: fp(14),
      fontWeight: '500',
      color: colors.novelText,
    },

    commentTime: {
      fontSize: fp(10),
      color: colors.novelTextGray,
    },

    commentContent: {
      fontSize: fp(14),
      color: colors.novelText,
      lineHeight: fp(20),
    },

    // 空状态
    emptyContainer: {
      alignItems: 'center',
      paddingVertical: hp(40),
    },

    emptyText: {
      fontSize: fp(16),
      color: colors.novelTextGray,
      marginTop: hp(8),
    },

    // 加载更多
    loadMoreContainer: {
      alignItems: 'center',
      paddingVertical: hp(20),
    },

    loadMoreText: {
      fontSize: fp(14),
      color: colors.novelTextGray,
    },

    // 评论用户详情
    commentUserDetails: {
      flex: 1,
    },

    // 评论用户名行布局（包含名字和菜单按钮）
    commentUserNameRow: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginBottom: wp(4),
    },

    commentUserNameContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      flex: 1,
    },

    // 评论菜单按钮
    commentMenuButton: {
      padding: sp(4),
      marginLeft: wp(8),
    },

    // 评论操作
    commentActions: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginTop: hp(8),
    },

    commentLeftActions: {
      flexDirection: 'row',
      alignItems: 'center',
    },

    commentRightActions: {
      flexDirection: 'row',
      alignItems: 'center',
    },

    commentActionButton: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      marginRight: wp(20),
    },

    commentActionText: {
      fontSize: fp(10),
      marginLeft: wp(4),
      color: colors.novelTextGray,
    },

    // 评论标签
    commentTag: {
      marginTop: wp(3),
      paddingHorizontal: wp(6),
      paddingVertical: hp(2),
      borderRadius: sp(2),
      marginLeft: wp(4),
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
      fontSize: fp(8),
      color: colors.novelMain,
      fontWeight: '600',
    },

    // 加载状态
    loadingFooter: {
      alignItems: 'center',
      paddingVertical: hp(16),
    },

    noMoreFooter: {
      alignItems: 'center',
      paddingVertical: hp(16),
    },

    noMoreText: {
      fontSize: fp(14),
      color: colors.novelTextGray,
    },

    emptySubText: {
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginTop: hp(4),
    },

    // 评论列表头部
    commentListHeader: {
      flexDirection: 'row',
      alignItems: 'flex-start',
    },

    commentCount: {
      fontSize: fp(12),
      fontWeight: '600',
      color: colors.novelText,
    },

    // 操作按钮
    actionButton: {
      flexDirection: 'row',
      alignItems: 'center',
      paddingVertical: hp(8),
      paddingHorizontal: wp(12),
      borderRadius: sp(20),
      backgroundColor: colors.novelSecondaryBackground,
      marginRight: wp(12),
    },

    actionText: {
      fontSize: fp(14),
      color: colors.novelTextGray,
      marginLeft: wp(4),
    },

    // 头像图片
    avatarImage: {
      width: sp(40),
      height: sp(40),
      borderRadius: sp(20),
    },

    // 书籍信息框
    bookInfoBox: {
      flexDirection: 'row',
      backgroundColor: colors.novelSecondaryBackground,
      borderRadius: sp(8),
      padding: wp(12),
      marginVertical: hp(12),
      alignItems: 'center',
    },

    bookCover: {
      backgroundColor: colors.novelBookBackground,
      width: sp(35),
      height: sp(52.5),
      borderRadius: sp(4),
      marginRight: wp(12),
    },

    bookDetails: {
      flex: 1,
    },

    bookName: {
      fontSize: fp(14),
      fontWeight: '600',
      color: colors.novelText,
      marginBottom: hp(4),
    },

    bookAuthor: {
      fontSize: fp(12),
      color: colors.novelTextGray,
    },

    // 评论输入框
    commentInputContainer: {
      flexDirection: 'row',
      backgroundColor: novelDesign.color.bg.surface,
      paddingHorizontal: wp(12),
      paddingVertical: hp(12),
      alignItems: 'flex-start',
    },

    commentInputAvatar: {
      width: sp(40),
      height: sp(40),
      borderRadius: sp(20),
      backgroundColor: colors.novelSecondaryBackground,
      justifyContent: 'center',
      alignItems: 'center',
      marginRight: wp(8),
    },

    commentInputBox: {
      flex: 1,
      flexDirection: 'row',
      alignItems: 'center',
      backgroundColor: colors.novelSecondaryBackground,
      borderRadius: sp(4),
      paddingHorizontal: wp(12),
      marginRight: hp(8),
      minHeight: sp(30),
    },

    commentInput: {
      flex: 1,
      fontSize: fp(14),
      color: colors.novelText,
      textAlignVertical: 'center',
      minHeight: sp(40),
      justifyContent: 'center',
      alignItems: 'center',
    },

    commentInputActions: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
    },

    commentActionIcon: {
      marginRight: wp(4),
    },

    commentActionCount: {
      fontSize: fp(12),
      color: colors.novelTextGray,
    },

    // 评论头像图片
    commentAvatarImage: {
      width: '100%',
      height: '100%',
      borderRadius: sp(16),
    },

    // 二级评论容器
    repliesContainer: {
      marginTop: hp(12),
      backgroundColor: colors.novelSecondaryBackground,
      borderRadius: sp(8),
      padding: wp(12),
    },

    // 二级评论项
    replyCommentItem: {
      marginBottom: hp(8),
    },

    // 简化的二级评论内容
    replyContent: {
      fontSize: fp(13),
      color: colors.novelText,
      lineHeight: fp(18),
    },

    replyUserName: {
      fontSize: fp(13),
      color: colors.novelMain,
      fontWeight: '500',
    },

    // 二级评论内容文本
    replyContentText: {
      fontSize: fp(13),
      color: colors.novelText,
    },

    // 底部输入框容器
    bottomInputContainer: {
      flexDirection: 'row',
      backgroundColor: novelDesign.color.bg.surface,
      paddingHorizontal: wp(16),
      paddingVertical: hp(12),
      alignItems: 'flex-start',
      borderTopWidth: 1,
      borderTopColor: novelDesign.color.border.subtle,
    },

    // 查看更多回复按钮
    viewMoreRepliesButton: {
      flexDirection: 'row',
      alignItems: 'center',
    },

    viewMoreRepliesText: {
      fontSize: fp(13),
      color: colors.novelMain,
      marginRight: wp(4),
    },

    repliesModalScrollView: {
      flex: 1,
      paddingHorizontal: wp(16),
    },

    replyItem: {
      paddingVertical: hp(12),
      paddingHorizontal: wp(12),
    },

    modalHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      paddingHorizontal: wp(16),
      paddingVertical: hp(16),
      borderBottomWidth: 1,
      borderBottomColor: colors.novelSecondaryBackground,
    },
    modalTitle: {
      fontSize: wp(16),
      fontWeight: 'bold',
      color: colors.novelTextGray,
    },

    modalCloseButton: {
      padding: sp(4),
    },
    // 半弹窗样式
    modalContainer: {
      flex: 1,
      justifyContent: 'flex-end',
    },

    modalBackdrop: {
      position: 'absolute',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0, // 不遮挡底部输入框
      backgroundColor: 'black',
    },

    modalBackdropTouchable: {
      flex: 1,
    },

    commentModalContent: {
      backgroundColor: colors.novelBackground,
      borderTopLeftRadius: sp(16),
      borderTopRightRadius: sp(16),
    },

    // 弹窗顶部操作栏
    modalTopBar: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(16),
      paddingVertical: hp(12),
      borderBottomWidth: 1,
      borderBottomColor: colors.novelDivider,
    },

    modalTopBarLeft: {
      flexDirection: 'row',
      alignItems: 'center',
    },

    modalTopBarRight: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      marginLeft: wp(12),
    },

    modalCloseHandle: {
      width: wp(32),
      height: hp(4),
      backgroundColor: colors.novelTextGray,
      borderRadius: sp(2),
      marginRight: wp(12),
    },

    modalTopBarTitle: {
      fontSize: fp(16),
      fontWeight: '600',
      color: colors.novelText,
    },


    modalInputContainer: {
      flexDirection: 'row',
      alignItems: 'flex-start',
      marginBottom: hp(16),
    },

    modalCommentInput: {
      minHeight: sp(80),
      maxHeight: sp(120),
      textAlignVertical: 'top',
      paddingTop: hp(8),
    },

    modalActions: {
      flexDirection: 'row',
      justifyContent: 'flex-end',
    },

    sendButton: {
      backgroundColor: colors.novelMain,
      paddingHorizontal: wp(24),
      paddingVertical: hp(8),
      borderRadius: sp(20),
    },

    sendButtonText: {
      color: 'white',
      fontSize: fp(14),
      fontWeight: 'bold',
    },

    commentInputPlaceholder: {
      alignItems: 'center',
      fontSize: fp(14),
      paddingVertical: wp(1),
      color: colors.novelTextGray,
    },

    starRating: {
      flexDirection: 'row',
      alignItems: 'center',
      marginRight: wp(8),
    },

    userMetaContainer: {
      flexDirection: 'row',
      alignItems: 'center',
      marginVertical: hp(4),
    },

    readingTimeText: {
      fontSize: fp(12),
      color: colors.novelTextGray,
    },

    // 新的半弹窗样式
    repliesSheetBackdrop: {
      position: 'absolute',
      left: 0,
      right: 0,
      top: 0,
      backgroundColor: 'black',
      zIndex: 20,
    },

    repliesSheetContainer: {
      position: 'absolute',
      left: 0,
      right: 0,
      backgroundColor: colors.novelBackground,
      borderTopLeftRadius: wp(16),
      borderTopRightRadius: wp(16),
      zIndex: 25,
      overflow: 'hidden',
    },

    repliesSheetTopBar: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(12),
      paddingVertical: hp(8),
      borderBottomWidth: 1,
      borderBottomColor: colors.novelDivider,
    },

    repliesSheetCloseButton: {
      padding: wp(6),
    },

    repliesSheetTitle: {
      fontSize: sp(16),
      color: colors.novelText,
      fontWeight: '600',
    },

    repliesSheetScrollView: {
      flex: 1,
    },

    repliesSheetScrollContent: {
      paddingBottom: wp(20),
    },

    repliesSheetDivider: {
      height: 10,
      backgroundColor: colors.novelSecondaryBackground,
      marginBottom: hp(12),
    },
  });
};
