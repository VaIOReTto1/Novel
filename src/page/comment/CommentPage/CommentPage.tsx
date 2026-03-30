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
import {
  bootstrapCommentPage,
  createCommentPageHandlers,
} from './domain/commentPageModel';

const useRefresh = () => {
  const { refreshComments } = useCommentStore();

  const onRefresh = useCallback(async () => {
    await refreshComments();
  }, [refreshComments]);

  return { onRefresh };
};

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

  const handlers = React.useMemo(
    () =>
      createCommentPageHandlers({
        bookId,
        navigateBack: () => NavigationBridge.navigateBack('CommentPageComponent'),
        navigateToWriteReview: (nextBookId) => NavigationBridge.navigateToWriteReview(nextBookId),
        loadMoreComments,
        setSearchQuery,
        setSelectedCategory,
      }),
    [bookId, loadMoreComments],
  );

  useEffect(() => {
    let cleanup = () => undefined;

    bootstrapCommentPage({
      bookId,
      loadComments,
      reset,
    }).then((nextCleanup) => {
      cleanup = nextCleanup;
    });

    return () => cleanup();
  }, [bookId, loadComments, reset]);

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      NavigationBridge.navigateBack('CommentPageComponent');
      return true;
    });
  }, []);

  const handleBackPress = useCallback(() => {
    handlers.handleBackPress();
  }, [handlers]);

  const handleLoadMore = useCallback(() => {
    handlers.handleLoadMore();
  }, [handlers]);

  const handleSearch = useCallback((query: string) => {
    handlers.handleSearch(query);
  }, [handlers]);

  const handleCategoryChange = useCallback((category: string) => {
    handlers.handleCategoryChange(category);
  }, [handlers]);

  const handleWriteReview = useCallback(() => {
    handlers.handleWriteReview();
  }, [handlers]);

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
