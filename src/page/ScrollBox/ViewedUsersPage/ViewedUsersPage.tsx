import React, { useEffect, useCallback } from 'react';
import { View, NativeModules, Text, BackHandler } from 'react-native';
import { useViewedUsersStore } from './store/viewedUsersStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createViewedUsersPageStyles } from './styles/ViewedUsersPageStyles';
import {
  TopBar,
  TabsSection,
  EmptyState,
  UsersList,
} from './components';

const { NavigationBridge } = NativeModules;

const ViewedUsersPage: React.FC = () => {
  // 使用Zustand store
  const {
    loading,
    error,
    userInfo,
    selectedTab,
    viewedUsers,
    recommendUsers,
    followingUsers,
    fansUsers,
    emptyStates,
    loadInitialData,
    setSelectedTab,
    handleFollowUser,
    handleViewMoreRecommend,
    handleUserPress,
  } = useViewedUsersStore();

  const colors = useNovelColors();
  const styles = createViewedUsersPageStyles(colors);

  // 初始化数据
  useEffect(() => {
    const initializeData = async () => {
      try {
        console.log('[ViewedUsersPage] 开始初始化数据');
        await loadInitialData();
        console.log('[ViewedUsersPage] 数据初始化完成');
      } catch (error) {
        console.error('[ViewedUsersPage] 初始化失败:', error);
      }
    };

    initializeData();
  }, [loadInitialData]);

  // 返回按钮点击
  const handleBackPress = useCallback(() => {
    console.log('ViewedUsers page back pressed');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('ViewedUsersPageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  }, []);

  // Android硬件返回按钮处理
  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      console.log('[ViewedUsersPage] Android硬件返回按钮被按下');
      if (NavigationBridge?.navigateBack) {
        NavigationBridge.navigateBack('ViewedUsersPageComponent');
      }
      return true; // 阻止默认行为
    });

    return () => backHandler.remove();
  }, []);

  // Tab切换
  const handleTabChange = useCallback((tab: 'viewed' | 'recommend' | 'following' | 'fans') => {
    console.log('Tab changed to:', tab);
    setSelectedTab(tab);
  }, [setSelectedTab]);

  // 关注用户
  const handleFollowPress = useCallback((userId: string) => {
    console.log('Follow user:', userId);
    handleFollowUser(userId);
  }, [handleFollowUser]);

  // 用户点击
  const handleUserItemPress = useCallback((userId: string) => {
    console.log('User pressed:', userId);
    handleUserPress(userId);
  }, [handleUserPress]);

  // 查看更多推荐按钮点击
  const handleViewMorePress = useCallback(() => {
    console.log('View more recommend pressed');
    handleViewMoreRecommend();
  }, [handleViewMoreRecommend]);

  // 获取当前Tab的用户数据
  const getCurrentUsers = () => {
    switch (selectedTab) {
      case 'viewed':
        return viewedUsers;
      case 'recommend':
        return recommendUsers;
      case 'following':
        return followingUsers;
      case 'fans':
        return fansUsers;
      default:
        return [];
    }
  };

  // 渲染内容区域
  const renderContent = () => {
    const currentUsers = getCurrentUsers();
    const isEmpty = currentUsers.length === 0;

    if (isEmpty) {
      const emptyData = emptyStates[selectedTab];
      return (
        <EmptyState
          styles={styles}
          data={emptyData}
          onButtonPress={selectedTab === 'viewed' ? handleViewMorePress : undefined}
        />
      );
    }

    return (
      <UsersList
        styles={styles}
        users={currentUsers}
        onUserPress={handleUserItemPress}
        onFollowPress={handleFollowPress}
      />
    );
  };

  // 加载状态
  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          userName={userInfo?.name || ''}
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
          userName={userInfo?.name || ''}
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
      {/* 顶部导航 */}
      <TopBar
        styles={styles}
        userName={userInfo?.name || '安国的尹锋'}
        onBackPress={handleBackPress}
      />

      {/* Tab导航 */}
      <TabsSection
        styles={styles}
        selectedTab={selectedTab}
        onTabChange={handleTabChange}
      />

      {/* 内容区域 */}
      {renderContent()}
    </View>
  );
};

export default ViewedUsersPage;
