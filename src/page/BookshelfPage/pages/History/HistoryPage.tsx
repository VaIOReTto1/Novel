import React, { useEffect, useCallback, useMemo } from 'react';
import { View, Text, ScrollView } from 'react-native';
import { createHistoryPageStyles } from './styles/HistoryPageStyles';
import { useHistoryStore, getTabsData } from './store/historyStore';
import { HistoryItem } from './types';
import {
  TabsArea,
  ContentArea,
  EditToolbar,
  RefreshIndicator,
} from './components';
import { useNovelColors } from '../../../../utils/theme/colors';
import { useRefreshLogic } from './hooks/useRefreshLogic';
import { useHistoryAnimations } from './hooks/useHistoryAnimations';
import { PULL_THRESHOLD } from './utils/constants';

const HistoryPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createHistoryPageStyles(colors);

  // 使用Zustand store
  const {
    historyItems,
    currentTab,
    viewType,
    isEditing,
    selectedItems,
    isLoading,
    error,
    hasMore,
    isRefreshing,
    setCurrentTab,
    setViewType,
    setEditing,
    toggleItemSelection,
    selectAllItems,
    clearSelection,
    removeSelectedItems,
    loadHistoryItems,
    refreshHistory,
    loadMoreHistory,
    toggleShelfStatus,
  } = useHistoryStore();

  // 获取Tab数据
  const tabsData = useMemo(() => getTabsData(), []);

  // 下拉刷新逻辑
  const {
    isPullingDown,
    pullDistance,
    handleRefresh: onRefresh,
    handleLoadMore: onLoadMore,
    handleScroll,
  } = useRefreshLogic({
    isRefreshing,
    loading: isLoading,
    hasMore,
    refreshHistory,
    loadMoreHistory,
  });

  // 下拉刷新动画
  const { spinStyle } = useHistoryAnimations(
    isRefreshing,
    isPullingDown,
    pullDistance
  );

  // 初始化数据
  useEffect(() => {
    const initializeData = async () => {
      try {
        console.log('[HistoryPage] 开始初始化数据');
        await loadHistoryItems();
        console.log('[HistoryPage] 数据初始化完成');
      } catch (error) {
        console.error('[HistoryPage] 初始化失败:', error);
      }
    };

    initializeData();
  }, [loadHistoryItems]);

  // Tab切换
  const handleTabPress = useCallback((tabId: string) => {
    console.log('Tab changed to:', tabId);
    setCurrentTab(tabId as 'all' | 'book' | 'listening' | 'drama');
    clearSelection();
    // 重新加载数据
    loadHistoryItems();
  }, [setCurrentTab, clearSelection, loadHistoryItems]);

  // 视图类型切换 - 优化性能，避免频繁重新渲染
  const handleViewTypeChange = useCallback((type: 'grid' | 'list') => {
    console.log('View type changed to:', type);
    // 使用requestAnimationFrame延迟状态更新，提升切换流畅度
    requestAnimationFrame(() => {
      setViewType(type);
    });
  }, [setViewType]);

  // 排序类型切换
  // 编辑模式切换
  const handleEditToggle = useCallback(() => {
    const newEditingState = !isEditing;
    console.log('Edit mode toggled to:', newEditingState);
    setEditing(newEditingState);
    if (newEditingState === false) {
      clearSelection();
    }
  }, [isEditing, setEditing, clearSelection]);

  // 历史项点击
  const handleItemPress = useCallback((item: HistoryItem) => {
    if (isEditing) {
      // 编辑模式下切换选择状态
      toggleItemSelection(item.id);
    } else {
      // 正常模式下跳转到阅读页面
      console.log('History item pressed:', item.title);
      // 这里可以导航到阅读器或详情页
      // 根据item.type判断跳转到不同的页面
    }
  }, [isEditing, toggleItemSelection]);

  // 历史项选择（编辑模式）
  const handleItemSelect = useCallback((itemId: string) => {
    toggleItemSelection(itemId);
  }, [toggleItemSelection]);

  // 全选
  const handleSelectAll = useCallback(() => {
    console.log('Select all items');
    selectAllItems();
  }, [selectAllItems]);

  // 删除选中的项目
  const handleRemoveSelected = useCallback(() => {
    console.log('Remove selected items:', selectedItems.length);
    removeSelectedItems();
    setEditing(false);
  }, [removeSelectedItems, setEditing, selectedItems.length]);

  // 加入书架
  const handleAddToShelf = useCallback(async (item: HistoryItem) => {
    console.log('Add to shelf:', item.title);
    try {
      await toggleShelfStatus(item);
    } catch (error) {
      console.error('Failed to toggle shelf status:', error);
    }
  }, [toggleShelfStatus]);

  // 注意：handleRefresh和handleLoadMore现在由useRefreshLogic hook提供

  // 当tab改变时重新加载数据
  useEffect(() => {
    loadHistoryItems();
  }, [currentTab, loadHistoryItems]);

  // 错误状态
  if (error) {
    return (
      <View style={styles.container}>
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>加载失败: {error}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <TabsArea
        styles={styles}
        tabs={tabsData}
        selectedTab={currentTab}
        onTabPress={handleTabPress}
        viewType={viewType}
        onViewTypeChange={handleViewTypeChange}
      />
      
      <View style={styles.tabsDivider} />
      
      <View style={styles.contentContainer}>
        {(isPullingDown || isRefreshing) && (
          <RefreshIndicator
            styles={styles}
            isPullingDown={isPullingDown}
            isRefreshing={isRefreshing}
            pullDistance={pullDistance}
            threshold={PULL_THRESHOLD}
            spinStyle={spinStyle}
          />
        )}
        
        <ScrollView
          style={styles.scrollView}
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
          bounces={true}
          alwaysBounceVertical={true}
          scrollEnabled={true}
          onScroll={handleScroll}
          scrollEventThrottle={16}
        >
          <ContentArea
            styles={styles}
            historyItems={historyItems}
            viewType={viewType}
            isEditing={isEditing}
            selectedItems={selectedItems}
            onItemPress={handleItemPress}
            onItemSelect={handleItemSelect}
            onAddToShelf={handleAddToShelf}
            loading={isLoading}
            error={error}
            refreshing={isRefreshing}
            onRefresh={onRefresh}
            onLoadMore={onLoadMore}
          />
        </ScrollView>
      </View>
      
      {isEditing && (
        <EditToolbar
          styles={styles}
          selectedCount={selectedItems.length}
          onSelectAll={handleSelectAll}
          onRemove={handleRemoveSelected}
          onCancel={handleEditToggle}
          visible={isEditing}
        />
      )}
    </View>
  );
};

export default HistoryPage;

console.log('[HistoryPage] Component loaded');
