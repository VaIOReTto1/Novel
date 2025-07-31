import React, { useCallback, useMemo } from 'react';
import { View, Text } from 'react-native';
import { MainTabBar } from './components/MainTabBar';
import { useMainBookshelfStore, getMainTabs } from './store/mainStore';
import { createMainPageStyles } from './styles/MainPageStyles';
import { useNovelColors } from '../../utils/theme';
import HistoryPage from './pages/History/HistoryPage';
import { BookshelfPage } from './pages/Bookshelf/BookshelfPage';
import { WatchlistPage } from './pages/Watchlist/WatchlistPage';
import CommunityPage from './pages/Community/CommunityPage';

export const MainPage: React.FC = React.memo(() => {
  const colors = useNovelColors();
  const styles = createMainPageStyles(colors);

  const {
    currentTab,
    isLoading,
    error,
    setCurrentTab,
  } = useMainBookshelfStore();

  // 获取Tab数据
  const tabs = useMemo(() => getMainTabs(), []);

  // 处理Tab切换
  const handleTabPress = useCallback((tabId: string) => {
    console.log('[BookshelfPage] Tab selected:', tabId);
    setCurrentTab(tabId);
  }, [setCurrentTab]);

  // 错误状态
  if (error) {
    return (
      <View style={styles.container}>
        <View style={styles.errorContainer}>
          <Text style={styles.errorIcon}>❌</Text>
          <Text style={styles.errorText}>加载失败</Text>
          <Text style={styles.errorDescription}>{error}</Text>
        </View>
      </View>
    );
  }

  // 加载状态
  if (isLoading) {
    return (
      <View style={styles.container}>
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <MainTabBar
        styles={styles}
        tabs={tabs}
        selectedTab={currentTab}
        onTabPress={handleTabPress}
      />

      {/* 根据当前Tab显示对应页面 - 使用条件渲染替代isActive */}
      {currentTab === 'history' && <HistoryPage />}
      {currentTab === 'bookshelf' && <BookshelfPage />}
      {currentTab === 'watchlist' && <WatchlistPage />}
      {currentTab === 'community' && <CommunityPage />}
    </View>
  );
});

console.log('[BookshelfPage] Component loaded');