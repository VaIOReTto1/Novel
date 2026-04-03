import React, { useEffect, useCallback } from 'react';
import { Alert, View, ScrollView, Text } from 'react-native';
import { useRecommendBookStore } from './store/recommendBookStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createRecommendBookPageStyles } from './styles/RecommendBookPageStyles';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  TopBar,
  UserSection,
  DataStatsSection,
  CreativeServiceSection,
  CreativeTaskSection,
} from './components';
import {
  bootstrapRecommendBookPage,
  createRecommendBookPageHandlers,
} from './domain/recommendBookPageModel';

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

  const handlers = React.useMemo(
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

  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar styles={styles} onBackPress={handlers.handleBackPress} />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.container}>
        <TopBar styles={styles} onBackPress={handlers.handleBackPress} />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>{`加载失败: ${error}`}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <TopBar styles={styles} onBackPress={handlers.handleBackPress} />

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        <UserSection styles={styles} userInfo={userInfo} />

        <DataStatsSection
          styles={styles}
          dataStats={dataStats}
          onWithdrawPress={handlers.handleWithdrawPress}
        />

        <CreativeServiceSection
          styles={styles}
          services={services}
          onServicePress={handlers.handleServiceItemPress}
        />

        <CreativeTaskSection
          styles={styles}
          selectedTab={selectedTaskTab}
          tasks={tasks}
          onTabChange={handlers.handleTaskTabChange}
          onTaskPress={handlers.handleTaskItemPress}
          onViewAllPress={handlers.handleViewAllTasksPress}
        />
      </ScrollView>
    </View>
  );
};

export default RecommendBookPage;
