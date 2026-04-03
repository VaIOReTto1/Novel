import React, { useEffect, useCallback, useMemo } from 'react';
import { View, Text } from 'react-native';
import { useViewedUsersStore } from './store/viewedUsersStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createViewedUsersPageStyles } from './styles/ViewedUsersPageStyles';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  TopBar,
  TabsSection,
  EmptyState,
  UsersList,
} from './components';
import {
  bootstrapViewedUsersPage,
  createViewedUsersPageHandlers,
  getViewedUsersCurrentUsers,
} from './domain/viewedUsersPageModel';

const ViewedUsersPage: React.FC = () => {
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

  useEffect(() => {
    bootstrapViewedUsersPage({
      loadInitialData,
    });
  }, [loadInitialData]);

  const handlers = useMemo(
    () =>
      createViewedUsersPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('ViewedUsersPageComponent'),
        setSelectedTab,
        handleFollowUser,
        handleViewMoreRecommend,
        handleUserPress,
      }),
    [handleFollowUser, handleUserPress, handleViewMoreRecommend, setSelectedTab],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBackPress();
      return true;
    });
  }, [handlers]);

  const currentUsers = getViewedUsersCurrentUsers({
    selectedTab,
    viewedUsers,
    recommendUsers,
    followingUsers,
    fansUsers,
  });

  const renderContent = () => {
    if (currentUsers.length === 0) {
      const emptyData = emptyStates[selectedTab];
      return (
        <EmptyState
          styles={styles}
          data={emptyData}
          onButtonPress={selectedTab === 'viewed' ? handlers.handleViewMorePress : undefined}
        />
      );
    }

    return (
      <UsersList
        styles={styles}
        users={currentUsers}
        onUserPress={handlers.handleUserItemPress}
        onFollowPress={handlers.handleFollowPress}
      />
    );
  };

  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          userName={userInfo?.name || ''}
          onBackPress={handlers.handleBackPress}
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          userName={userInfo?.name || ''}
          onBackPress={handlers.handleBackPress}
        />
        <View style={styles.loadingContainer}>
          <Text style={styles.loadingText}>{`加载失败: ${error}`}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        userName={userInfo?.name || '用户'}
        onBackPress={handlers.handleBackPress}
      />

      <TabsSection
        styles={styles}
        selectedTab={selectedTab}
        onTabChange={handlers.handleTabChange}
      />

      {renderContent()}
    </View>
  );
};

export default ViewedUsersPage;
