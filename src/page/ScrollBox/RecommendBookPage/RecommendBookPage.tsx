import React, { useEffect, useCallback } from 'react';
import { View, ScrollView, NativeModules, Text } from 'react-native';
import { useRecommendBookStore } from './store/recommendBookStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createRecommendBookPageStyles } from './styles/RecommendBookPageStyles';
import {
  TopBar,
  UserSection,
  DataStatsSection,
  CreativeServiceSection,
  CreativeTaskSection,
} from './components';

const { NavigationBridge } = NativeModules;

const RecommendBookPage: React.FC = () => {
  // 使用Zustand store
  const {
    loading,
    error,
    userInfo,
    dataStats,
    services,
    selectedTaskTab,
    tasks,
    loadInitialData,
    setSelectedTaskTab,
    handleViewAllTasks,
    handleServicePress,
    handleTaskPress,
  } = useRecommendBookStore();

  const colors = useNovelColors();
  const styles = createRecommendBookPageStyles(colors);

  // 初始化数据
  useEffect(() => {
    const initializeData = async () => {
      try {
        console.log('[RecommendBookPage] 开始初始化数据');
        await loadInitialData();
        console.log('[RecommendBookPage] 数据初始化完成');
      } catch (error) {
        console.error('[RecommendBookPage] 初始化失败:', error);
      }
    };

    initializeData();
  }, [loadInitialData]);

  // 返回按钮点击
  const handleBackPress = useCallback(() => {
    console.log('RecommendBook page back pressed');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('RecommendBookPageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  }, []);

  // 创作任务 Tab 切换
  const handleTaskTabChange = useCallback((tab: 'recommend' | 'book') => {
    console.log('Task tab changed to:', tab);
    setSelectedTaskTab(tab);
  }, [setSelectedTaskTab]);

  // 提现按钮点击
  const handleWithdrawPress = useCallback(() => {
    console.log('Withdraw button pressed');
    alert('提现功能开发中...');
  }, []);

  // 查看更多任务
  const handleViewAllTasksPress = useCallback(() => {
    console.log('View all tasks pressed');
    handleViewAllTasks();
  }, [handleViewAllTasks]);

  // 服务点击
  const handleServiceItemPress = useCallback((serviceId: string) => {
    console.log('Service pressed:', serviceId);
    handleServicePress(serviceId);
  }, [handleServicePress]);

  // 任务点击
  const handleTaskItemPress = useCallback((taskId: string) => {
    console.log('Task pressed:', taskId);
    handleTaskPress(taskId);
  }, [handleTaskPress]);

  // 加载状态
  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar styles={styles} onBackPress={handleBackPress} />
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
        <TopBar styles={styles} onBackPress={handleBackPress} />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载失败: {error}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <TopBar styles={styles} onBackPress={handleBackPress} />

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        {/* 用户信息 */}
        <UserSection
          styles={styles}
          userInfo={userInfo}
        />

        {/* 数据统计 */}
        <DataStatsSection
          styles={styles}
          dataStats={dataStats}
          onWithdrawPress={handleWithdrawPress}
        />

        {/* 创作服务 */}
        <CreativeServiceSection
          styles={styles}
          services={services}
          onServicePress={handleServiceItemPress}
        />

        {/* 创作任务 */}
        <CreativeTaskSection
          styles={styles}
          selectedTab={selectedTaskTab}
          tasks={tasks}
          onTabChange={handleTaskTabChange}
          onTaskPress={handleTaskItemPress}
          onViewAllPress={handleViewAllTasksPress}
        />
      </ScrollView>
    </View>
  );
};

export default RecommendBookPage; 