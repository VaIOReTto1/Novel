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

      {/* 使用display样式来隐藏/显示页面，避免重新挂载 */}
      <View style={[styles.pageContainer, currentTab === 'history' ? styles.pageVisible : styles.pageHidden]}>
        <HistoryPage />
      </View>
      <View style={[styles.pageContainer, currentTab === 'bookshelf' ? styles.pageVisible : styles.pageHidden]}>
        <BookshelfPage />
      </View>
      <View style={[styles.pageContainer, currentTab === 'watchlist' ? styles.pageVisible : styles.pageHidden]}>
        <WatchlistPage />
      </View>
      <View style={[styles.pageContainer, currentTab === 'community' ? styles.pageVisible : styles.pageHidden]}>
        <CommunityPage />
      </View>
    </View>
  );
});

console.log('[BookshelfPage] Component loaded');
