import React, { useEffect, useCallback, useMemo } from 'react';
import { View, ScrollView } from 'react-native';
import { useHistoryStore } from './store/historyStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { useRefreshLogic } from './hooks/useRefreshLogic';
import { useHistoryAnimations } from './hooks/useHistoryAnimations';
import { createHistoryPageStyles } from './styles/HistoryPageStyles';
import { HISTORY_TABS } from './utils/constants';
import { HistoryItem } from './types';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  TopBar,
  TabsArea,
  RefreshIndicator,
  ContentArea,
} from './components';
import {
  bootstrapScrollBoxHistoryPage,
  createScrollBoxHistoryPageHandlers,
} from './domain/scrollboxHistoryPageModel';

const HistoryPage: React.FC = () => {
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
    refreshLogic.pullDistance,
  );

  useEffect(() => {
    bootstrapScrollBoxHistoryPage({
      loadHistoryItems,
    });
  }, [loadHistoryItems]);

  const handlers = useMemo(
    () =>
      createScrollBoxHistoryPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('HistoryPageComponent'),
        setSelectedTab,
        setViewType,
      }),
    [setSelectedTab, setViewType],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBackPress();
      return true;
    });
  }, [handlers]);

  const handleSearchPress = useCallback(() => {
    console.log('History page search pressed');
  }, []);

  const handleItemPress = useCallback((item: HistoryItem) => {
    console.log('History item pressed:', item.title);
  }, []);

  useEffect(() => {
    loadHistoryItems(true);
  }, [selectedTab, loadHistoryItems]);

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        onBackPress={handlers.handleBackPress}
        onSearchPress={handleSearchPress}
      />

      <TabsArea
        styles={styles}
        tabs={HISTORY_TABS}
        selectedTab={selectedTab}
        onTabPress={(tabId) => {
          const tabType = HISTORY_TABS.find(tab => tab.id === tabId)?.type || 'all';
          handlers.handleTabPress(tabType);
        }}
        viewType={viewType}
        onViewTypeChange={handlers.handleViewTypeChange}
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
