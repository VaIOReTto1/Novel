import { Alert, Share } from 'react-native';
import { useCallback, useEffect } from 'react';
import { useCommunityStore } from '../store/communityStore';
import { CommunitySortType } from '../types';
import NavigationBridge from '../../../../../utils/bridge/NavigationBridge';
import { createCommunityActionHandlers } from './communityHandlers';

export const useCommunity = () => {
  const {
    // State
    tabs,
    categories,
    circles,
    activeTab,
    selectedCategory,
    selectedCircle,
    sortType,
    loading,
    refreshing,
    hasMore,
    error,

    // Actions
    loadPosts,
    refreshPosts,
    loadMorePosts,
    setActiveTab,
    setSelectedCategory,
    setSelectedCircle,
    setSortType,
    likePosts,
    commentPost,
    sharePost,
    clearError,
    getFilteredPosts,
    getSortedPosts,
  } = useCommunityStore();

  // 获取过滤和排序后的帖子
  const filteredPosts = getFilteredPosts();
  const sortedPosts = getSortedPosts(filteredPosts);

  // 初始化加载
  useEffect(() => {
    loadPosts();
  }, [loadPosts]);

  // 切换标签页
  const handleTabChange = useCallback((tabId: string) => {
    setActiveTab(tabId);
  }, [setActiveTab]);

  // 切换分类
  const handleCategoryChange = useCallback((categoryId: string) => {
    setSelectedCategory(categoryId);
  }, [setSelectedCategory]);

  // 切换圈子
  const handleCircleChange = useCallback((circleId: string) => {
    setSelectedCircle(circleId);
  }, [setSelectedCircle]);

  // 切换排序
  const handleSortChange = useCallback((sort: CommunitySortType) => {
    setSortType(sort);
  }, [setSortType]);

  // 刷新
  const handleRefresh = useCallback(() => {
    refreshPosts();
  }, [refreshPosts]);

  // 加载更多
  const handleLoadMore = useCallback(() => {
    if (!loading && hasMore) {
      loadMorePosts();
    }
  }, [loading, hasMore, loadMorePosts]);

  // 点赞
  const handleLike = useCallback((postId: string) => {
    likePosts(postId);
  }, [likePosts]);

  const {
    handleComment,
    handleShare,
    handleMore,
    handleUserPress,
    handleSubscribe,
    handleSearch,
    handleNotification,
    handlePublish,
  } = createCommunityActionHandlers({
    posts: sortedPosts,
    commentPost,
    sharePost,
    navigateToReviewDetail: NavigationBridge.navigateToReviewDetail,
    navigateToSearch: (query: string) => NavigationBridge.navigateToSearch(query),
    navigateToMessage: NavigationBridge.navigateToMessage,
    navigateToWritePage: NavigationBridge.navigateToWritePage,
    share: (content) => Share.share(content),
    alert: (title, message, buttons) => Alert.alert(title, message, buttons),
  });

  // 重试
  const handleRetry = useCallback(() => {
    clearError();
    loadPosts();
  }, [clearError, loadPosts]);

  return {
    // Data
    posts: sortedPosts,
    tabs,
    categories,
    circles,

    // State
    activeTab,
    selectedCategory,
    selectedCircle,
    sortType,
    loading,
    refreshing,
    hasMore,
    error,

    // Handlers
    handleTabChange,
    handleCategoryChange,
    handleCircleChange,
    handleSortChange,
    handleRefresh,
    handleLoadMore,
    handleLike,
    handleComment,
    handleShare,
    handleMore,
    handleUserPress,
    handleSubscribe,
    handleSearch,
    handleNotification,
    handlePublish,
    handleRetry,
  };
};
