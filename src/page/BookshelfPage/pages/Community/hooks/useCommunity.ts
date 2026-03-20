import { useCallback, useEffect } from 'react';
import { useCommunityStore } from '../store/communityStore';
import { CommunitySortType } from '../types';

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

  // 评论
  const handleComment = useCallback((postId: string) => {
    commentPost(postId);
    // TODO: 跳转到评论页面
  }, [commentPost]);

  // 分享
  const handleShare = useCallback((postId: string) => {
    sharePost(postId);
    // TODO: 调用分享功能
  }, [sharePost]);

  // 更多操作
  const handleMore = useCallback((postId: string) => {
    // TODO: 显示更多操作菜单
    console.log('More actions for post:', postId);
  }, []);

  // 用户点击
  const handleUserPress = useCallback((userId: string) => {
    // TODO: 跳转到用户页面
    console.log('User pressed:', userId);
  }, []);

  // 订阅用户
  const handleSubscribe = useCallback((userId: string) => {
    // TODO: 实现订阅功能
    console.log('Subscribe user:', userId);
  }, []);

  // 搜索
  const handleSearch = useCallback(() => {
    // TODO: 跳转到搜索页面
    console.log('Search pressed');
  }, []);

  // 通知
  const handleNotification = useCallback(() => {
    // TODO: 跳转到通知页面
    console.log('Notification pressed');
  }, []);

  // 发布
  const handlePublish = useCallback(() => {
    // TODO: 跳转到发布页面
    console.log('Publish pressed');
  }, []);

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
