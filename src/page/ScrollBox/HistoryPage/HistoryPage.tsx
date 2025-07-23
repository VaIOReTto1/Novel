import React, { useEffect, useCallback } from 'react';
import { View, ScrollView, NativeModules } from 'react-native';
import { useHistoryStore } from './store/historyStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { useRefreshLogic } from './hooks/useRefreshLogic';
import { useHistoryAnimations } from './hooks/useHistoryAnimations';
import { createHistoryPageStyles } from './styles/HistoryPageStyles';
import { HISTORY_TABS } from './utils/constants';
import { HistoryItem } from './types';
import {
  TopBar,
  TabsArea,
  RefreshIndicator,
  ContentArea,
} from './components';

const { NavigationBridge } = NativeModules;

const HistoryPage: React.FC = () => {
  // 使用Zustand store
  const {
    historyItems,
    loading,
    isRefreshing,
    hasMore,
    selectedTab,
    viewType,
    loadHistoryItems,
    refreshHistory,
    loadMoreHistory,
    setSelectedTab,
    setViewType,
  } = useHistoryStore();

  const colors = useNovelColors();
  const styles = createHistoryPageStyles(colors);

  // 使用自定义hooks
  const refreshLogic = useRefreshLogic({
    isRefreshing,
    loading,
    hasMore,
    refreshHistory,
    loadMoreHistory,
  });

  const animations = useHistoryAnimations(
    isRefreshing,
    refreshLogic.isPullingDown,
    refreshLogic.pullDistance
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

  // 返回按钮点击
  const handleBackPress = useCallback(() => {
    console.log('History page back pressed');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('HistoryPageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  }, []);

  // 搜索按钮点击
  const handleSearchPress = useCallback(() => {
    console.log('History page search pressed');
    // 这里可以实现搜索功能
  }, []);

  // Tab切换
  const handleTabPress = useCallback((tabId: string) => {
    console.log('Tab changed to:', tabId);
    const tabType = HISTORY_TABS.find(tab => tab.id === tabId)?.type || 'all';
    setSelectedTab(tabType);
  }, [setSelectedTab]);

  // 视图类型切换 - 优化性能，避免频繁重新渲染
  const handleViewTypeChange = useCallback((type: 'grid' | 'list') => {
    console.log('View type changed to:', type);
    // 使用requestAnimationFrame延迟状态更新，提升切换流畅度
    requestAnimationFrame(() => {
      setViewType(type);
    });
  }, [setViewType]);

  // 历史项点击
  const handleItemPress = useCallback((item: HistoryItem) => {
    console.log('History item pressed:', item.title);
    // 这里可以导航到阅读器或详情页
    // 根据item.type判断跳转到不同的页面
  }, []);

  // 当tab改变时重新加载数据
  useEffect(() => {
    loadHistoryItems(true);
  }, [selectedTab, loadHistoryItems]);

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        onBackPress={handleBackPress}
        onSearchPress={handleSearchPress}
      />

      <TabsArea
        styles={styles}
        tabs={HISTORY_TABS}
        selectedTab={selectedTab}
        onTabPress={handleTabPress}
        viewType={viewType}
        onViewTypeChange={handleViewTypeChange}
      />

      <View style={styles.contentContainer}>
        <ScrollView
          style={styles.scrollView}
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
          onScroll={refreshLogic.handleScroll}
          scrollEventThrottle={16}
          bounces={true}
          alwaysBounceVertical={true}
          scrollEnabled={true}
        >
          <RefreshIndicator
            styles={styles}
            isPullingDown={refreshLogic.isPullingDown}
            isRefreshing={isRefreshing}
            pullDistance={refreshLogic.pullDistance}
            threshold={refreshLogic.PULL_THRESHOLD}
            spinStyle={animations.spinStyle}
          />

          <ContentArea
            styles={styles}
            items={historyItems}
            viewType={viewType}
            loading={loading}
            hasMore={hasMore}
            onItemPress={handleItemPress}
          />
        </ScrollView>
      </View>
    </View>
  );
};

export default HistoryPage;