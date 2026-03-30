import { Alert, Share } from 'react-native';
import { useEffect, useMemo } from 'react';
import { useCommunityStore } from '../store/communityStore';
import NavigationBridge from '../../../../../utils/bridge/NavigationBridge';
import { createCommunityActionHandlers } from './communityHandlers';
import {
  bootstrapCommunityPage,
  createCommunityPageHandlers,
} from '../domain/communityPageModel';

export const useCommunity = () => {
  const {
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

  const filteredPosts = getFilteredPosts();
  const sortedPosts = getSortedPosts(filteredPosts);

  useEffect(() => {
    bootstrapCommunityPage({
      loadPosts,
    });
  }, [loadPosts]);

  const communityPageHandlers = useMemo(
    () =>
      createCommunityPageHandlers({
        loading,
        hasMore,
        setActiveTab,
        setSelectedCategory,
        setSelectedCircle,
        setSortType,
        refreshPosts,
        loadMorePosts,
        likePosts,
        clearError,
        loadPosts,
      }),
    [
      clearError,
      hasMore,
      likePosts,
      loadMorePosts,
      loadPosts,
      loading,
      refreshPosts,
      setActiveTab,
      setSelectedCategory,
      setSelectedCircle,
      setSortType,
    ],
  );

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

  return {
    posts: sortedPosts,
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
    handleTabChange: communityPageHandlers.handleTabChange,
    handleCategoryChange: communityPageHandlers.handleCategoryChange,
    handleCircleChange: communityPageHandlers.handleCircleChange,
    handleSortChange: communityPageHandlers.handleSortChange,
    handleRefresh: communityPageHandlers.handleRefresh,
    handleLoadMore: communityPageHandlers.handleLoadMore,
    handleLike: communityPageHandlers.handleLike,
    handleComment,
    handleShare,
    handleMore,
    handleUserPress,
    handleSubscribe,
    handleSearch,
    handleNotification,
    handlePublish,
    handleRetry: communityPageHandlers.handleRetry,
  };
};
