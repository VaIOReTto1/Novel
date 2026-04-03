import React, { useEffect, useCallback, useMemo } from 'react';
import { View, ScrollView, Text } from 'react-native';
import { useMyReservationStore } from './store/myReservationStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createMyReservationPageStyles } from './styles/MyReservationPageStyles';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  TopBar,
  SubTabsSection,
  ReservationGrid,
  EmptyState,
} from './components';
import {
  bootstrapMyReservationPage,
  createMyReservationPageHandlers,
  getMyReservationContentState,
} from './domain/myReservationPageModel';

const MyReservationPage: React.FC = () => {
  const {
    loading,
    error,
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

  useEffect(() => {
    bootstrapMyReservationPage({
      loadInitialData,
    });
  }, [loadInitialData]);

  const handlers = useMemo(
    () =>
      createMyReservationPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('MyReservationPageComponent'),
        setSelectedTab,
        setSelectedSubTab,
        handleReserveItem,
        handleItemPress,
      }),
    [handleItemPress, handleReserveItem, setSelectedSubTab, setSelectedTab],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBackPress();
      return true;
    });
  }, [handlers]);

  const contentState = getMyReservationContentState({
    selectedTab,
    selectedSubTab,
    newReservations,
    myOnlineReservations,
    myOfflineReservations,
    emptyStates,
  });

  if (loading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          selectedTab={selectedTab}
          onTabChange={handlers.handleMainTabChange}
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
          selectedTab={selectedTab}
          onTabChange={handlers.handleMainTabChange}
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
        selectedTab={selectedTab}
        onTabChange={handlers.handleMainTabChange}
        onBackPress={handlers.handleBackPress}
      />

      {selectedTab === 'mine' && (
        <SubTabsSection
          styles={styles}
          selectedTab={selectedSubTab}
          onlineCount={myOnlineReservations.length}
          offlineCount={myOfflineReservations.length}
          onTabChange={handlers.handleSubTabChange}
        />
      )}

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        {contentState.emptyData ? (
          <EmptyState styles={styles} data={contentState.emptyData} />
        ) : (
          <ReservationGrid
            styles={styles}
            items={contentState.items}
            onItemPress={handlers.handleItemPress}
            onReservePress={handlers.handleReservePress}
          />
        )}
      </ScrollView>
    </View>
  );
};

export default MyReservationPage;
