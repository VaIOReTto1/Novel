import { useCallback, useRef } from 'react';
import { useBookshelfStore, useRecommendationStore } from '../store/bookshelfStore';

export const useRefreshLogic = () => {
  const {
    isRefreshing,
    refreshBookshelfItems,
    loadMoreBookshelfItems,
    hasMore,
  } = useBookshelfStore();

  const {
    loadMoreRecommendations,
    refreshRecommendations,
  } = useRecommendationStore();

  const refreshTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  // 刷新书架数据
  const onRefresh = useCallback(async () => {
    try {
      await Promise.all([
        refreshBookshelfItems(),
        refreshRecommendations(),
      ]);
    } catch (error) {
      console.error('Refresh failed:', error);
    }
  }, [refreshBookshelfItems, refreshRecommendations]);

  // 加载更多书架数据
  const onLoadMore = useCallback(async () => {
    if (!hasMore) {return;}

    try {
      await loadMoreBookshelfItems();
    } catch (error) {
      console.error('Load more failed:', error);
    }
  }, [loadMoreBookshelfItems, hasMore]);

  // 加载更多推荐数据
  const onLoadMoreRecommendations = useCallback(async () => {
    try {
      await loadMoreRecommendations();
    } catch (error) {
      console.error('Load more recommendations failed:', error);
    }
  }, [loadMoreRecommendations]);

  // 防抖刷新
  const debouncedRefresh = useCallback(() => {
    if (refreshTimeoutRef.current) {
      clearTimeout(refreshTimeoutRef.current);
    }

    refreshTimeoutRef.current = setTimeout(() => {
      onRefresh();
    }, 300);
  }, [onRefresh]);

  return {
    isRefreshing,
    onRefresh,
    onLoadMore,
    onLoadMoreRecommendations,
    debouncedRefresh,
  };
};
