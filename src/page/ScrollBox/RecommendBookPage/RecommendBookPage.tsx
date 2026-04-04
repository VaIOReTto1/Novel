import React, { useEffect, useMemo } from 'react';
import { Alert, ScrollView, Text, View } from 'react-native';

import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { useNovelColors } from '../../../utils/theme/colors';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  CreativeTaskSection,
  TopBar,
  WorkbenchOverviewSection,
} from './components';
import {
  bootstrapRecommendBookPage,
  createRecommendBookPageHandlers,
} from './domain/recommendBookPageModel';
import { createRecommendBookPageStyles } from './styles/RecommendBookPageStyles';
import { useRecommendBookStore } from './store/recommendBookStore';

const RecommendBookPage: React.FC = () => {
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

  useEffect(() => {
    bootstrapRecommendBookPage({
      loadInitialData,
    });
  }, [loadInitialData]);

  const handlers = useMemo(
    () =>
      createRecommendBookPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('RecommendBookPageComponent'),
        setSelectedTaskTab,
        handleViewAllTasks,
        handleServicePress,
        handleTaskPress,
        showWithdrawAlert: () => Alert.alert('提现功能开发中...'),
      }),
    [handleServicePress, handleTaskPress, handleViewAllTasks, setSelectedTaskTab],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBackPress();
      return true;
    });
  }, [handlers]);

  const currentTasks = tasks[selectedTaskTab];
  const focusTask = currentTasks[0] ?? null;
  const focusCount = currentTasks.length;

  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar onBackPress={handlers.handleBackPress} styles={styles} />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.container}>
        <TopBar onBackPress={handlers.handleBackPress} styles={styles} />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>{`加载失败: ${error}`}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <TopBar onBackPress={handlers.handleBackPress} styles={styles} />

      <ScrollView
        bounces
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        style={styles.scrollView}>
        <WorkbenchOverviewSection
          dataStats={dataStats}
          focusCount={focusCount}
          focusTask={focusTask}
          onFocusPress={handlers.handleTaskItemPress}
          onServicePress={handlers.handleServiceItemPress}
          services={services}
          styles={styles}
          userInfo={userInfo}
        />

        <CreativeTaskSection
          onTabChange={handlers.handleTaskTabChange}
          onTaskPress={handlers.handleTaskItemPress}
          onViewAllPress={handlers.handleViewAllTasksPress}
          selectedTab={selectedTaskTab}
          styles={styles}
          tasks={tasks}
        />
      </ScrollView>
    </View>
  );
};

export default RecommendBookPage;
