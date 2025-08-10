import { useState, useCallback } from 'react';

/**
 * 刷新功能 Hook
 * 提供下拉刷新的状态管理和逻辑处理
 */
export const useRefresh = (
  onRefresh: () => Promise<void>,
  deps: React.DependencyList = []
) => {
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = useCallback(async () => {
    if (isRefreshing) return;
    
    try {
      setIsRefreshing(true);
      await onRefresh();
    } catch (error) {
      console.error('[useRefresh] 刷新失败:', error);
    } finally {
      setIsRefreshing(false);
    }
  }, [isRefreshing, onRefresh, ...deps]);

  return {
    isRefreshing,
    onRefresh: handleRefresh,
  };
};