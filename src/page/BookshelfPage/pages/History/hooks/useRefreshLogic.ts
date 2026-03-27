import { useState, useCallback, useEffect } from 'react';
import { PULL_THRESHOLD } from '../utils/constants';

interface UseRefreshLogicProps {
  isRefreshing: boolean;
  loading: boolean;
  hasMore: boolean;
  refreshHistory: () => Promise<void>;
  loadMoreHistory: () => Promise<void>;
}

export const useRefreshLogic = ({
  isRefreshing,
  loading,
  hasMore,
  refreshHistory,
  loadMoreHistory,
}: UseRefreshLogicProps) => {
  const [isPullingDown, setIsPullingDown] = useState(false);
  const [pullDistance, setPullDistance] = useState(0);
  const [hasTriggeredRefresh, setHasTriggeredRefresh] = useState(false);

  // 下拉刷新
  const handleRefresh = useCallback(async () => {
    console.log('触发下拉刷新');
    try {
      await refreshHistory();
      console.log('下拉刷新完成');
    } catch (error) {
      console.error('刷新失败:', error);
    }
  }, [refreshHistory]);

  // 上拉加载更多
  const handleLoadMore = useCallback(async () => {
    if (hasMore && !loading) {
      console.log('触发上拉加载更多');
      try {
        await loadMoreHistory();
        console.log('加载更多完成');
      } catch (error) {
        console.error('加载更多失败:', error);
      }
    }
  }, [hasMore, loading, loadMoreHistory]);

  // 处理滚动事件
  const handleScroll = useCallback((event: any) => {
    const { layoutMeasurement, contentOffset, contentSize } = event.nativeEvent;
    const paddingToBottom = 50;

    // 检查是否到达底部
    const isCloseToBottom = layoutMeasurement.height + contentOffset.y >=
      contentSize.height - paddingToBottom;

    if (isCloseToBottom) {
      handleLoadMore();
    }

    // 检查下拉距离
    if (contentOffset.y < 0) {
      const distance = Math.abs(contentOffset.y);
      setPullDistance(distance);

      if (distance > 10) {
        setIsPullingDown(true);
      }

      // 触发刷新
      if (distance > PULL_THRESHOLD && !hasTriggeredRefresh && !isRefreshing) {
        setHasTriggeredRefresh(true);
        handleRefresh();
      }
    } else {
      if (isPullingDown) {
        setIsPullingDown(false);
        setPullDistance(0);
      }
    }
  }, [handleLoadMore, isPullingDown, hasTriggeredRefresh, isRefreshing, handleRefresh]);

  // 重置刷新状态
  useEffect(() => {
    if (!isRefreshing) {
      setHasTriggeredRefresh(false);
      setIsPullingDown(false);
      setPullDistance(0);
    }
  }, [isRefreshing]);

  return {
    isPullingDown,
    pullDistance,
    handleScroll,
    handleRefresh,
    handleLoadMore,
    PULL_THRESHOLD,
  };
};
