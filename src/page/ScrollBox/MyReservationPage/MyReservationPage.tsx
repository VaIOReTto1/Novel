import React, { useEffect, useCallback } from 'react';
import { View, ScrollView, NativeModules, Text, BackHandler } from 'react-native';
import { useMyReservationStore } from './store/myReservationStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createMyReservationPageStyles } from './styles/MyReservationPageStyles';
import {
  TopBar,
  SubTabsSection,
  ReservationGrid,
  EmptyState,
} from './components';

const { NavigationBridge } = NativeModules;

const MyReservationPage: React.FC = () => {
  // 使用Zustand store
  const {
    loading,
    error,
    userInfo,
    selectedTab,
    selectedSubTab,
    newReservations,
    myOnlineReservations,
    myOfflineReservations,
    emptyStates,
    loadInitialData,
    setSelectedTab,
    setSelectedSubTab,
    handleReserveItem,
    handleItemPress,
  } = useMyReservationStore();

  const colors = useNovelColors();
  const styles = createMyReservationPageStyles(colors);

  // 初始化数据
  useEffect(() => {
    const initializeData = async () => {
      try {
        console.log('[MyReservationPage] 开始初始化数据');
        await loadInitialData();
        console.log('[MyReservationPage] 数据初始化完成');
      } catch (error) {
        console.error('[MyReservationPage] 初始化失败:', error);
      }
    };

    initializeData();
  }, [loadInitialData]);

  // 返回按钮点击
  const handleBackPress = useCallback(() => {
    console.log('MyReservation page back pressed');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('MyReservationPageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  }, []);

  // Android硬件返回按钮处理
  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      console.log('[MyReservationPage] Android硬件返回按钮被按下');
      if (NavigationBridge?.navigateBack) {
        NavigationBridge.navigateBack('MyReservationPageComponent');
      }
      return true; // 阻止默认行为
    });

    return () => backHandler.remove();
  }, []);

  // 主Tab切换
  const handleMainTabChange = useCallback((tab: 'new' | 'mine') => {
    console.log('Main tab changed to:', tab);
    setSelectedTab(tab);
  }, [setSelectedTab]);

  // 子Tab切换
  const handleSubTabChange = useCallback((tab: 'online' | 'offline') => {
    console.log('Sub tab changed to:', tab);
    setSelectedSubTab(tab);
  }, [setSelectedSubTab]);

  // 预约按钮点击
  const handleReservePress = useCallback((itemId: string) => {
    console.log('Reserve item:', itemId);
    handleReserveItem(itemId);
  }, [handleReserveItem]);

  // 项目点击
  const handleItemItemPress = useCallback((itemId: string) => {
    console.log('Item pressed:', itemId);
    handleItemPress(itemId);
  }, [handleItemPress]);

  // 渲染内容区域
  const renderContent = () => {
    if (selectedTab === 'new') {
      // 新剧预约
      return (
        <ReservationGrid
          styles={styles}
          items={newReservations}
          onItemPress={handleItemItemPress}
          onReservePress={handleReservePress}
        />
      );
    } else {
      // 我的预约
      const currentItems = selectedSubTab === 'online' ? myOnlineReservations : myOfflineReservations;
      const isEmpty = currentItems.length === 0;

      if (isEmpty) {
        const emptyData = emptyStates[selectedSubTab];
        return <EmptyState styles={styles} data={emptyData} />;
      }

      return (
        <ReservationGrid
          styles={styles}
          items={currentItems}
          onItemPress={handleItemItemPress}
          onReservePress={handleReservePress}
        />
      );
    }
  };

  // 加载状态
  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar 
          styles={styles} 
          selectedTab={selectedTab}
          onTabChange={handleMainTabChange}
          onBackPress={handleBackPress} 
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      </View>
    );
  }

  // 错误状态
  if (error) {
    return (
      <View style={styles.container}>
        <TopBar 
          styles={styles} 
          selectedTab={selectedTab}
          onTabChange={handleMainTabChange}
          onBackPress={handleBackPress} 
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载失败: {error}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* 顶部导航（包含Tab切换） */}
      <TopBar 
        styles={styles} 
        selectedTab={selectedTab}
        onTabChange={handleMainTabChange}
        onBackPress={handleBackPress} 
      />

      {/* 我的预约的子Tab */}
      {selectedTab === 'mine' && (
        <SubTabsSection
          styles={styles}
          selectedTab={selectedSubTab}
          onlineCount={myOnlineReservations.length}
          offlineCount={myOfflineReservations.length}
          onTabChange={handleSubTabChange}
        />
      )}

      {/* 内容区域 */}
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        {renderContent()}
      </ScrollView>
    </View>
  );
};

export default MyReservationPage;