import { useCallback } from 'react';
import { useWatchlistStore, useDramaRecommendationStore } from '../store/watchlistStore';

export const useRefreshLogic = () => {
  const {
    isRefreshing,
    refreshWatchlist,
    loadMoreWatchlist,
  } = useWatchlistStore();

  const {
    loadMoreRecommendations,
  } = useDramaRecommendationStore();

  const onRefresh = useCallback(async () => {
    await refreshWatchlist();
  }, [refreshWatchlist]);

  const onLoadMore = useCallback(async () => {
    await loadMoreWatchlist();
  }, [loadMoreWatchlist]);

  const onLoadMoreRecommendations = useCallback(async () => {
    await loadMoreRecommendations();
  }, [loadMoreRecommendations]);

  return {
    isRefreshing,
    onRefresh,
    onLoadMore,
    onLoadMoreRecommendations,
  };
};
