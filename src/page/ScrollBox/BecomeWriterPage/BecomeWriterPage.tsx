import React, { useEffect, useCallback } from 'react';
import { View, ScrollView, NativeModules, Text } from 'react-native';
import { useBecomeWriterStore } from './store/becomeWriterStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { createBecomeWriterPageStyles } from './styles/BecomeWriterPageStyles';
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

const { NavigationBridge } = NativeModules;

const BecomeWriterPage: React.FC = () => {
  // 使用Zustand store
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
    handleBecomeTomatoWriter,
    hideWelcomeModal,
    setAgreementChecked,
    handleImmediateRegister,
    handleMorePress,
    works,
    isAuthor,
  } = useBecomeWriterStore();

  const colors = useNovelColors();
  const styles = createBecomeWriterPageStyles(colors);

  // Android硬件返回按钮处理（使用 require 以规避类型问题）
  useEffect(() => {
    const RN = require('react-native');
    const BH = RN && RN.BackHandler;
    if (!(BH && BH.addEventListener)) {
      return;
    }
    const sub = BH.addEventListener('hardwareBackPress', () => {
      console.log('[BecomeWriterPage] Android硬件返回按钮被按下');
      if (NavigationBridge?.navigateBack) {
        NavigationBridge.navigateBack('BecomeWriterPageComponent');
      }
      return true;
    });
    return () => {
      try {
        if (sub && typeof sub.remove === 'function') {
          sub.remove();
        }
      } catch (e) {}
    };
  }, []);

  // 初始化数据
  useEffect(() => {
    const initializeData = async () => {
      try {
        console.log('[BecomeWriterPage] 开始初始化数据');
        await loadInitialData();
        console.log('[BecomeWriterPage] 数据初始化完成');
      } catch (e) {
        console.error('[BecomeWriterPage] 初始化失败:', e);
      }
    };

    initializeData();
  }, [loadInitialData]);

  // 返回按钮点击
  const handleBackPress = useCallback(() => {
    console.log('BecomeWriter page back pressed');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('BecomeWriterPageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  }, []);

  // 数据统计 Tab 切换
  const handleDataTabChange = useCallback((tab: 'novel' | 'short') => {
    console.log('Data tab changed to:', tab);
    setSelectedDataTab(tab);
  }, [setSelectedDataTab]);

  // 作家专属 Tab 切换
  const handleAuthorTabChange = useCallback((tab: 'benefits' | 'road' | 'platform') => {
    console.log('Author tab changed to:', tab);
    setSelectedAuthorTab(tab);
  }, [setSelectedAuthorTab]);

  // 创作活动 Tab 切换
  const handleActivityTabChange = useCallback((tab: 'novel' | 'short') => {
    console.log('Activity tab changed to:', tab);
    setSelectedActivityTab(tab);
  }, [setSelectedActivityTab]);
  // 成为番茄作家按钮点击
  const handleBecomeTomatoWriterPress = useCallback(() => {
    console.log('Become tomato writer button pressed');
    handleBecomeTomatoWriter();
  }, [handleBecomeTomatoWriter]);
  
  // AI入口
  const handleAIPress = useCallback(() => {
    NavigationBridge?.navigateToAIPage?.();
  }, []);

  // 更多按钮点击处理
  const handleActivityMorePress = useCallback(() => {
    handleMorePress('创作活动');
  }, [handleMorePress]);

  const handleCourseMorePress = useCallback(() => {
    handleMorePress('作家课堂');
  }, [handleMorePress]);

  // 协议链接点击
  const handleAgreementPress = useCallback(() => {
    console.log('Agreement link pressed');
    // 可接入统一Toast，这里先用console
  }, []);

  // 作家作品列表：优先原生获取，无则使用 mock
  useEffect(() => {
    const fetchAuthorWorks = async () => {
      if (!isAuthor) { return; }
      try {
        const res = await (require('../../../utils/bridge/NavigationBridge').default).getAuthorBooks(1, 50);
        const list = Array.isArray(res?.list) ? res.list : [];
        if (list.length > 0) {
          const worksMapped = list.map((it: any) => ({
            id: String(it.id ?? Date.now()),
            title: String(it.bookName ?? '未命名作品'),
            words: Number(it.wordCount ?? 0),
          }));
          useBecomeWriterStore.getState().setWorks?.(worksMapped);
        } else {
          // fallback mock
          useBecomeWriterStore.getState().setWorks?.([
            { id: 'm1', title: '我的新书', words: 0 },
          ]);
        }
      } catch (e) {
        useBecomeWriterStore.getState().setWorks?.([
          { id: 'm1', title: '我的新书', words: 0 },
        ]);
      }
    };
    fetchAuthorWorks();
  }, [isAuthor]);

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
      <TopBar styles={styles} onBackPress={handleBackPress} showAI={isAuthor} onAIPress={handleAIPress} />

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        bounces={true}
      >
        {/* 用户信息 & 公告 */}
        <UserSection
          styles={styles}
          userInfo={userInfo}
          announcement={announcement}
        />

        {/* 作品数据 */}
        <DataStatsSection
          styles={styles}
          selectedTab={selectedDataTab}
          dataStats={dataStats}
          isExpanded={isDataStatsExpanded}
          onTabChange={handleDataTabChange}
          onToggleExpanded={toggleDataStatsExpanded}
        works={works}
        onPressWork={() => {
          if (NavigationBridge?.navigateToBookManage) {
            NavigationBridge.navigateToBookManage();
          }
        }}
        onCreateChapter={() => NavigationBridge?.navigateToWritePage?.()}
          isAuthor={isAuthor}
        />

        {/* 作家专属 */}
        <AuthorExclusiveSection
          styles={styles}
          selectedTab={selectedAuthorTab}
          benefits={benefits}
          roadTimeline={roadTimeline}
          platforms={platforms}
          onTabChange={handleAuthorTabChange}
        />

        {/* 版权改编 */}
        <CopyrightSection
          styles={styles}
          copyrightWorks={copyrightWorks}
        />

        {/* 创作活动 */}
        <CreativeActivitySection
          styles={styles}
          selectedTab={selectedActivityTab}
          activities={activities}
          onTabChange={handleActivityTabChange}
          onMorePress={handleActivityMorePress}
        />

        {/* 作家课堂 */}
        <WriterClassroomSection
          styles={styles}
          courses={courses}
          onMorePress={handleCourseMorePress}
        />
      </ScrollView>

      {/* 底部固定按钮 */}
      {!isAuthor && (
        <BottomButton
          styles={styles}
          onPress={handleImmediateRegister}
        />
      )}

      {/* 欢迎弹窗 */}
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
