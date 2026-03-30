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
import {
  bootstrapReviewDetailPage,
  createReviewDetailPageHandlers,
} from './domain/reviewDetailPageModel';

const { height: screenHeight } = Dimensions.get('window');
const bottomInputOverlayStyle = { zIndex: 30 } as const;
const actionButtonSpacingStyle = { marginTop: 8 } as const;

const useRefreshLogic = () => {
  const { refreshReviewDetail } = useReviewDetailStore();

  return useRefresh(async () => {
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
  const {
    reviewDetail,
    comments,
    loadReviewDetail,
    loadMoreComments,
    isRefreshing,
    isLoading,
    error,
    reset,
    toggleCommentLike,
    toggleCommentDislike,
  } = useReviewDetailStore();
  const { isRefreshing: refreshing, onRefresh } = useRefreshLogic();
  const { fadeAnim, scaleAnim } = useAnimations();
  const [parsedCommentData, setParsedCommentData] = React.useState<any>(null);
  const [_commentText, _setCommentText] = useState('');
  const [_showCommentModal, setShowCommentModal] = useState(false);
  const [selectedComment, setSelectedComment] = useState<any>(null);
  const [modalAnimation] = useState(new Animated.Value(0));
  const [inputBarH, setInputBarH] = useState(0);
  const [showRepliesSheet, setShowRepliesSheet] = useState(false);

  const sheetTargetHeight = Math.min(screenHeight * 0.82, screenHeight - inputBarH);
  const sheetTranslateY = React.useRef(new Animated.Value(sheetTargetHeight)).current;
  const backdropOpacity = React.useRef(new Animated.Value(0)).current;

  useEffect(() => {
    let cleanup = () => undefined;

    bootstrapReviewDetailPage({
      commentData,
      setParsedCommentData,
      loadReviewDetail,
      reset,
    }).then((nextCleanup) => {
      cleanup = nextCleanup;
    });

    return () => cleanup();
  }, [commentData, loadReviewDetail, reset]);

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      NavigationBridge.navigateBack('ReviewDetailPageComponent');
      return true;
    });
  }, []);

  const openRepliesSheet = () => {
    setShowRepliesSheet(true);
    sheetTranslateY.setValue(sheetTargetHeight);
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
        toValue: sheetTargetHeight,
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

  const handlers = React.useMemo(
    () =>
      createReviewDetailPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('ReviewDetailPageComponent'),
        loadMoreComments,
        toggleCommentLike,
        toggleCommentDislike,
        setSelectedComment,
        openRepliesSheet,
      }),
    [loadMoreComments, toggleCommentDislike, toggleCommentLike],
  );

  const handleScroll = (event: any) => {
    const { layoutMeasurement, contentOffset, contentSize } = event.nativeEvent;
    const paddingToBottom = 20;
    if (layoutMeasurement.height + contentOffset.y >= contentSize.height - paddingToBottom) {
      handlers.handleLoadMore();
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

  const renderRepliesSheet = () => (
    <RepliesSheet
      selectedComment={selectedComment}
      showRepliesSheet={showRepliesSheet}
      inputBarHeight={inputBarH}
      sheetHeight={sheetTargetHeight}
      sheetTranslateY={sheetTranslateY}
      backdropOpacity={backdropOpacity}
      onClose={closeRepliesSheet}
    />
  );

  if (error) {
    return (
      <View style={styles.container}>
        <TopBar onBack={handlers.handleBack} bookInfo={bookInfo} />
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
      <TopBar onBack={handlers.handleBack} bookInfo={bookInfo} />

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
          onLike={handlers.handleLike}
          onDislike={handlers.handleDislike}
          onReply={handlers.handleReply}
          onViewMoreReplies={handlers.handleViewMoreReplies}
        />
      </ScrollView>

      <View
        style={[styles.bottomInputContainer, bottomInputOverlayStyle]}
        onLayout={(e: { nativeEvent: { layout: { height: React.SetStateAction<number> } } }) =>
          setInputBarH(e.nativeEvent.layout.height)
        }
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
          <TouchableOpacity style={[styles.commentActionButton, actionButtonSpacingStyle]} onPress={() => handlers.handleLike('self')}>
            <Icon
              name={'favorite-border'}
              size={24}
              color={colors.novelTextGray}
            />
            <Text style={styles.commentActionCount}>208</Text>
          </TouchableOpacity>

          <TouchableOpacity style={[styles.commentActionButton, actionButtonSpacingStyle]} onPress={() => handlers.handleDislike('self')}>
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
