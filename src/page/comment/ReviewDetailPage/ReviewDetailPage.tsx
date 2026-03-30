import React, { useEffect, useState } from 'react';
import { View, ScrollView, Animated, Text, RefreshControl, TouchableOpacity, Dimensions, Easing } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { NavigationBridge } from '../../../utils/bridge/NavigationBridge';
import { TopBar } from './components/TopBar';
import { ReviewContent } from './components/ReviewContent';
import { CommentList } from './components/CommentList';
import { RepliesSheet } from './components/RepliesSheet';
import { useReviewDetailStore } from './store/reviewDetailStore';
import { useReviewDetailPageStyles } from './hooks/useReviewDetailPageStyles';
import { useRefresh, useAnimations } from './hooks';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
const { height: screenHeight } = Dimensions.get('window');
const bottomInputOverlayStyle = { zIndex: 30 } as const;
const actionButtonSpacingStyle = { marginTop: 8 } as const;

const useRefreshLogic = () => {
  const { refreshReviewDetail } = useReviewDetailStore();

  return useRefresh(async () => {
    console.log('[ReviewDetailPage] 开始刷新评论详情数据');
    await refreshReviewDetail();
  }, [refreshReviewDetail]);
};

interface ReviewDetailPageProps {
  commentData: string;
  bookInfo?: {
    bookId: string;
    bookName: string;
    authorName: string;
    picUrl: string;
  };
}

const ReviewDetailPage: React.FC<ReviewDetailPageProps> = ({ commentData, bookInfo }) => {
  const { colors, styles } = useReviewDetailPageStyles();
  const { reviewDetail, comments, loadReviewDetail, loadMoreComments, isRefreshing, isLoading, error, reset, toggleCommentLike, toggleCommentDislike } = useReviewDetailStore();
  const { isRefreshing: refreshing, onRefresh } = useRefreshLogic();
  const { fadeAnim, scaleAnim } = useAnimations();
  const [parsedCommentData, setParsedCommentData] = React.useState<any>(null);

  // 添加调试信息
  console.log('[ReviewDetailPage] 渲染状态:', {
    reviewDetail: !!reviewDetail,
    commentsCount: comments.length,
    isLoading,
    isRefreshing,
    error,
  });

  useEffect(() => {
    if (commentData) {
      try {
        const comment = JSON.parse(commentData);
        console.log('[ReviewDetailPage] 页面初始化，commentData:', comment);
        setParsedCommentData(comment);
        // 调用API加载评论详情和回复数据
        loadReviewDetail(comment.id);
      } catch (err) {
        console.error('[ReviewDetailPage] 解析评论数据失败:', err);
      }
    }

    return () => {
      console.log('[ReviewDetailPage] 页面卸载，清理状态');
      reset();
    };
  }, [commentData, loadReviewDetail, reset]);

  // Android硬件返回按钮处理
  useEffect(() => {
    return registerHardwareBackHandler(() => {
      console.log('[ReviewDetailPage] Android硬件返回按钮被按下');
      NavigationBridge.navigateBack('ReviewDetailPageComponent');
      return true; // 阻止默认行为
    });
  }, []);

  const handleBack = () => {
    console.log('[ReviewDetailPage] 用户点击返回按钮');
    NavigationBridge.navigateBack('ReviewDetailPageComponent');
  };

  const handleLoadMore = () => {
    loadMoreComments();
  };

  const handleLike = (commentId: string) => {
    toggleCommentLike(commentId);
  };

  const handleDislike = (commentId: string) => {
    toggleCommentDislike(commentId);
  };

  const handleReply = (commentId: string) => {
    // TODO: 实现回复功能
    console.log('回复评论:', commentId);
  };

  const [_commentText, _setCommentText] = useState('');
  const [_showCommentModal, setShowCommentModal] = useState(false);
  const [selectedComment, setSelectedComment] = useState<any>(null);
  const [modalAnimation] = useState(new Animated.Value(0));

  // 新的半弹窗状态与动画
  const [inputBarH, setInputBarH] = useState(0);
  const [showRepliesSheet, setShowRepliesSheet] = useState(false);

  const SHEET_TARGET_H = Math.min(screenHeight * 0.82, screenHeight - inputBarH);
  const sheetTranslateY = React.useRef(new Animated.Value(SHEET_TARGET_H)).current;
  const backdropOpacity = React.useRef(new Animated.Value(0)).current;

  const handleScroll = (event: any) => {
    const { layoutMeasurement, contentOffset, contentSize } = event.nativeEvent;
    const paddingToBottom = 20;
    if (layoutMeasurement.height + contentOffset.y >= contentSize.height - paddingToBottom) {
      handleLoadMore();
    }
  };

  const showCommentModalWithAnimation = () => {
    setShowCommentModal(true);
    Animated.timing(modalAnimation, {
      toValue: 1,
      duration: 300,
      useNativeDriver: false,
    }).start();
  };


  const handleCommentInputFocus = () => {
    showCommentModalWithAnimation();
  };

  // 新的半弹窗动画函数
  const openRepliesSheet = () => {
    setShowRepliesSheet(true);
    // 先把起点重置为收起位置（避免尺寸变化导致的错位）
    sheetTranslateY.setValue(SHEET_TARGET_H);
    Animated.parallel([
      Animated.timing(sheetTranslateY, {
        toValue: 0,
        duration: 280,
        easing: Easing.out(Easing.cubic),
        useNativeDriver: true,
      }),
      Animated.timing(backdropOpacity, {
        toValue: 0.5,
        duration: 200,
        useNativeDriver: false,
      }),
    ]).start();
  };

  const closeRepliesSheet = () => {
    Animated.parallel([
      Animated.timing(sheetTranslateY, {
        toValue: SHEET_TARGET_H,
        duration: 240,
        easing: Easing.in(Easing.cubic),
        useNativeDriver: true,
      }),
      Animated.timing(backdropOpacity, {
        toValue: 0,
        duration: 200,
        useNativeDriver: false,
      }),
    ]).start(() => setShowRepliesSheet(false));
  };

  const handleViewMoreReplies = (comment: any) => {
    setSelectedComment(comment);
    openRepliesSheet();
  };

  const renderRepliesSheet = () => {
    return (
      <RepliesSheet
        selectedComment={selectedComment}
        showRepliesSheet={showRepliesSheet}
        inputBarHeight={inputBarH}
        sheetHeight={SHEET_TARGET_H}
        sheetTranslateY={sheetTranslateY}
        backdropOpacity={backdropOpacity}
        onClose={closeRepliesSheet}
      />
    );
  };

  // 如果有错误，显示错误信息
  if (error) {
    return (
      <View style={styles.container}>
        <TopBar onBack={handleBack} bookInfo={bookInfo} />
        <View style={styles.loadingContainer}>
          <Text>加载失败: {error}</Text>
        </View>
      </View>
    );
  }

  return (
    <Animated.View
      style={[
        styles.container,
        {
          opacity: fadeAnim,
          transform: [{ scale: scaleAnim }],
        },
      ]}
    >
      <TopBar onBack={handleBack} bookInfo={bookInfo} />

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        onScroll={handleScroll}
        scrollEventThrottle={16}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            colors={[colors.novelMain]}
            progressBackgroundColor={colors.novelBackground}
            tintColor={colors.novelMain}
          />
        }
      >
        {parsedCommentData && <ReviewContent commentData={parsedCommentData} />}
        <CommentList
          onLike={handleLike}
          onDislike={handleDislike}
          onReply={handleReply}
          onViewMoreReplies={handleViewMoreReplies}
        />
      </ScrollView>

      {/* 底部输入框 */}
      <View
        style={[styles.bottomInputContainer, bottomInputOverlayStyle]}
        onLayout={(e: { nativeEvent: { layout: { height: React.SetStateAction<number>; }; }; }) => setInputBarH(e.nativeEvent.layout.height)}
      >
        <TouchableOpacity
          style={styles.commentInputBox}
          onPress={handleCommentInputFocus}
          activeOpacity={0.8}
        >
          <Text style={[styles.commentInput, { color: colors.novelTextGray }]}>
            写下你的评论...
          </Text>
        </TouchableOpacity>

        <View style={styles.commentInputActions}>
          <TouchableOpacity style={[styles.commentActionButton, actionButtonSpacingStyle]} onPress={handleLike}>
            <Icon
              name={'favorite-border'}
              size={24}
              color={colors.novelTextGray}
            />
            <Text style={styles.commentActionCount}>208</Text>
          </TouchableOpacity>

          <TouchableOpacity style={[styles.commentActionButton, actionButtonSpacingStyle]}>
            <Icon
              name="thumb-down"
              size={24}
              color={colors.novelTextGray}
            />
          </TouchableOpacity>
        </View>
      </View>

      {renderRepliesSheet()}
    </Animated.View>
  );
};

export { ReviewDetailPage };
