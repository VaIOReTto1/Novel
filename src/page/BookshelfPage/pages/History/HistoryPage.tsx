import React, { useEffect, useCallback, useMemo, useRef } from 'react';
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
import {
  bootstrapBookshelfHistoryPage,
  createHistoryPageHandlers,
} from './domain/historyPageModel';

const HistoryPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createHistoryPageStyles(colors);

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

  const tabsData = useMemo(() => getTabsData(), []);

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

  const { spinStyle } = useHistoryAnimations(
    isRefreshing,
    isPullingDown,
    pullDistance,
  );

  const skipCurrentTabReloadRef = useRef(true);

  useEffect(() => {
    bootstrapBookshelfHistoryPage({
      loadHistoryItems,
    });
  }, [loadHistoryItems]);

  const historyHandlers = useMemo(
    () =>
      createHistoryPageHandlers({
        setCurrentTab,
        clearSelection,
        setEditing,
        removeSelectedItems,
        toggleShelfStatus,
      }),
    [clearSelection, removeSelectedItems, setCurrentTab, setEditing, toggleShelfStatus],
  );

  const handleTabPress = useCallback((tabId: string) => {
    historyHandlers.handleTabPress(tabId as 'all' | 'book' | 'listening' | 'drama');
  }, [historyHandlers]);

  const handleViewTypeChange = useCallback((type: 'grid' | 'list') => {
    console.log('View type changed to:', type);
    requestAnimationFrame(() => {
      setViewType(type);
    });
  }, [setViewType]);

  const handleEditToggle = useCallback(() => {
    historyHandlers.handleEditToggle(isEditing);
  }, [historyHandlers, isEditing]);

  const handleItemPress = useCallback((item: HistoryItem) => {
    if (isEditing) {
      toggleItemSelection(item.id);
      return;
    }
    console.log('History item pressed:', item.title);
  }, [isEditing, toggleItemSelection]);

  const handleItemSelect = useCallback((itemId: string) => {
    toggleItemSelection(itemId);
  }, [toggleItemSelection]);

  const handleSelectAll = useCallback(() => {
    console.log('Select all items');
    selectAllItems();
  }, [selectAllItems]);

  const handleRemoveSelected = useCallback(() => {
    console.log('Remove selected items:', selectedItems.length);
    historyHandlers.handleRemoveSelected();
  }, [historyHandlers, selectedItems.length]);

  const handleAddToShelf = useCallback(async (item: HistoryItem) => {
    console.log('Add to shelf:', item.title);
    await historyHandlers.handleAddToShelf(item);
  }, [historyHandlers]);

  useEffect(() => {
    if (skipCurrentTabReloadRef.current) {
      skipCurrentTabReloadRef.current = false;
      return;
    }
    loadHistoryItems();
  }, [currentTab, loadHistoryItems]);

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
