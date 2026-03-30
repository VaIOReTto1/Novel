import React, { useEffect, useCallback, useRef, useState } from 'react';
import {
  View,
  RefreshControl,
  Animated,
} from 'react-native';
import { useNovelColors } from '../../../utils/theme/colors';
import { NavigationBridge } from '../../../utils/bridge/NavigationBridge';
import { useCommentStore } from './store/commentStore';
import { createCommentPageStyles } from './styles/CommentPageStyles';
import { TopBar } from './components/TopBar';
import { CommentList } from './components/CommentList';
import { RatingSection } from './components/RatingSection';
import { CategorySection } from './components/CategorySection';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';

// 自定义Hook：刷新逻辑
const useRefresh = () => {
  const { refreshComments } = useCommentStore();

  const onRefresh = useCallback(async () => {
    await refreshComments();
  }, [refreshComments]);

  return { onRefresh };
};

// 自定义Hook：动画
const useAnimations = () => {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const scaleAnim = useRef(new Animated.Value(0.95)).current;

  useEffect(() => {
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 300,
        useNativeDriver: true,
      }),
      Animated.spring(scaleAnim, {
        toValue: 1,
        tension: 100,
        friction: 8,
        useNativeDriver: true,
      }),
    ]).start();
  }, [fadeAnim, scaleAnim]);

  return { fadeAnim, scaleAnim };
};

interface CommentPageProps {
  bookId: string;
  bookInfo?: {
    bookId: string;
    bookName: string;
    authorName: string;
    picUrl: string;
  };
}

const CommentPage: React.FC<CommentPageProps> = ({ bookId, bookInfo }) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  const { loadComments, loadMoreComments, isRefreshing, reset } = useCommentStore();
  const { onRefresh } = useRefresh();
  const { fadeAnim, scaleAnim } = useAnimations();
  const [, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');

  // 初始化数据加载
  useEffect(() => {
    console.log('[CommentPage] 页面初始化，bookId:', bookId);
    if (bookId) {
      loadComments(bookId);
    }

    // 页面卸载时重置状态
    return () => {
      reset();
    };
  }, [bookId, loadComments, reset]);

  // Android硬件返回按钮处理
  useEffect(() => {
    return registerHardwareBackHandler(() => {
      console.log('[CommentPage] Android硬件返回按钮被按下');
      NavigationBridge.navigateBack('CommentPageComponent');
      return true; // 阻止默认行为
    });
  }, []);

  // 返回按钮处理
  const handleBackPress = useCallback(() => {
    console.log('[CommentPage] 用户点击返回按钮');
    NavigationBridge.navigateBack('CommentPageComponent');
  }, []);

  // 加载更多评论
  const handleLoadMore = useCallback(() => {
    loadMoreComments();
  }, [loadMoreComments]);

  const handleSearch = useCallback((query: string) => {
    setSearchQuery(query);
    // TODO: 实现搜索逻辑
    console.log('搜索评论:', query);
  }, []);

  const handleCategoryChange = useCallback((category: string) => {
    setSelectedCategory(category);
    // TODO: 实现分类筛选逻辑
    console.log('切换分类:', category);
  }, []);

  const handleWriteReview = useCallback(() => {
    // 导航到发表评论页面
    NavigationBridge.navigateToWriteReview(bookId);
  }, [bookId]);

  return (
    <Animated.View style={[
      styles.container,
      {
        opacity: fadeAnim,
        transform: [{ scale: scaleAnim }],
      },
    ]}>
      <TopBar onBackPress={handleBackPress} onSearch={handleSearch} />

      <CommentList
        onEndReached={handleLoadMore}
        refreshControl={
          <RefreshControl
            refreshing={isRefreshing}
            onRefresh={onRefresh}
            colors={[colors.novelMain]}
            tintColor={colors.novelMain}
            progressBackgroundColor={colors.novelBackground}
          />
        }
        ListHeaderComponent={
          <View>
            <RatingSection onWriteReview={handleWriteReview} bookId={bookId} />
            <CategorySection
              selectedCategory={selectedCategory}
              onCategoryChange={handleCategoryChange}
            />
          </View>
        }
        bookInfo={bookInfo}
      />
    </Animated.View>
  );
};

export default CommentPage;
export { CommentPage };
