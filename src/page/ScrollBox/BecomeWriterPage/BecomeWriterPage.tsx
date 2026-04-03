import React, { useEffect, useMemo } from 'react';
import { View, ScrollView, Text } from 'react-native';
import { useBecomeWriterStore } from './store/becomeWriterStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createBecomeWriterPageStyles } from './styles/BecomeWriterPageStyles';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  TopBar,
  UserSection,
  DataStatsSection,
  AuthorExclusiveSection,
  CopyrightSection,
  CreativeActivitySection,
  WriterClassroomSection,
  BottomButton,
  WelcomeModal,
} from './components';
import {
  bootstrapBecomeWriterPage,
  createBecomeWriterPageHandlers,
} from './domain/becomeWriterPageModel';

const BecomeWriterPage: React.FC = () => {
  const {
    loading,
    error,
    userInfo,
    announcement,
    selectedDataTab,
    dataStats,
    isDataStatsExpanded,
    selectedAuthorTab,
    benefits,
    roadTimeline,
    platforms,
    copyrightWorks,
    selectedActivityTab,
    activities,
    courses,
    showWelcomeModal,
    isAgreementChecked,
    loadInitialData,
    setSelectedDataTab,
    setSelectedAuthorTab,
    setSelectedActivityTab,
    toggleDataStatsExpanded,
    hideWelcomeModal,
    setAgreementChecked,
    handleImmediateRegister,
    handleMorePress,
    works,
    isAuthor,
  } = useBecomeWriterStore();

  const colors = useNovelColors();
  const styles = createBecomeWriterPageStyles(colors);

  const handlers = useMemo(
    () =>
      createBecomeWriterPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('BecomeWriterPageComponent'),
        setSelectedDataTab,
        setSelectedAuthorTab,
        setSelectedActivityTab,
        navigateToAIPage: () => NavigationBridge.navigateToAIPage?.(),
        handleMorePress,
        navigateToBookManage: () => NavigationBridge.navigateToBookManage?.(),
        navigateToWritePage: () => NavigationBridge.navigateToWritePage?.(),
      }),
    [
      handleMorePress,
      setSelectedActivityTab,
      setSelectedAuthorTab,
      setSelectedDataTab,
    ],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBackPress();
      return true;
    });
  }, [handlers]);

  useEffect(() => {
    bootstrapBecomeWriterPage({
      isAuthor,
      loadInitialData,
      getAuthorBooks: NavigationBridge.getAuthorBooks,
      setWorks: (nextWorks) => useBecomeWriterStore.getState().setWorks?.(nextWorks),
    });
  }, [isAuthor, loadInitialData]);

  const handleAgreementPress = React.useCallback(() => {
    console.log('Agreement link pressed');
  }, []);

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
      <TopBar
        styles={styles}
        onBackPress={handlers.handleBackPress}
        showAI={isAuthor}
        onAIPress={handlers.handleAIPress}
      />

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        <UserSection
          styles={styles}
          userInfo={userInfo}
          announcement={announcement}
        />

        <DataStatsSection
          styles={styles}
          selectedTab={selectedDataTab}
          dataStats={dataStats}
          isExpanded={isDataStatsExpanded}
          onTabChange={handlers.handleDataTabChange}
          onToggleExpanded={toggleDataStatsExpanded}
          works={works}
          onPressWork={handlers.handlePressWork}
          onCreateChapter={handlers.handleCreateChapter}
          isAuthor={isAuthor}
        />

        <AuthorExclusiveSection
          styles={styles}
          selectedTab={selectedAuthorTab}
          benefits={benefits}
          roadTimeline={roadTimeline}
          platforms={platforms}
          onTabChange={handlers.handleAuthorTabChange}
        />

        <CopyrightSection
          styles={styles}
          copyrightWorks={copyrightWorks}
        />

        <CreativeActivitySection
          styles={styles}
          selectedTab={selectedActivityTab}
          activities={activities}
          onTabChange={handlers.handleActivityTabChange}
          onMorePress={handlers.handleActivityMorePress}
        />

        <WriterClassroomSection
          styles={styles}
          courses={courses}
          onMorePress={handlers.handleCourseMorePress}
        />
      </ScrollView>

      {!isAuthor && (
        <BottomButton
          styles={styles}
          onPress={handleImmediateRegister}
        />
      )}

      {!isAuthor && (
        <WelcomeModal
          styles={styles}
          visible={showWelcomeModal}
          isAgreementChecked={isAgreementChecked}
          onClose={hideWelcomeModal}
          onRegister={handleImmediateRegister}
          onAgreementChange={setAgreementChecked}
          onAgreementPress={handleAgreementPress}
        />
      )}
    </View>
  );
};

export default BecomeWriterPage;
