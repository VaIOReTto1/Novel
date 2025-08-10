import React, { useCallback, useEffect } from 'react';
import { View, ScrollView, BackHandler } from 'react-native';
import { useNovelColors } from '../../../utils/theme';
import { createFeedbackHelpPageStyles } from './styles/FeedbackHelpPageStyles';
import { useFeedbackHelpStore } from './store/feedbackHelpStore';
import {
  TopBar,
  UserSection,
  ConsultSection,
  FrequentQuestions,
  ContactSection,
} from './components';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';

export const FeedbackHelpMainPage: React.FC = React.memo(() => {
  const colors = useNovelColors();
  const styles = createFeedbackHelpPageStyles(colors);

  const {
    consultCategories,
    frequentQuestions,
    isLoading,
    error,
    selectCategory,
    selectQuestion,
    resetToMain,
  } = useFeedbackHelpStore();

  // 处理分类点击 - 导航到问题列表页
  const handleCategoryPress = useCallback((categoryId: string) => {
    console.log('[FeedbackHelpMainPage] Category selected:', categoryId);
    selectCategory(categoryId);
    // 导航到问题列表页面
    NavigationBridge.navigateToQuestionList();
  }, [selectCategory]);

  // 处理问题点击 - 导航到问题详情页
  const handleQuestionPress = useCallback((questionId: string) => {
    console.log('[FeedbackHelpMainPage] Question selected:', questionId);
    selectQuestion(questionId);
    // 导航到问题详情页面
    NavigationBridge.navigateToQuestionDetail();
  }, [selectQuestion]);

  // 处理返回操作
  const handleBack = useCallback(() => {
    NavigationBridge.navigateBack('FeedbackHelpMainPageComponent');
  }, []);

  // 处理搜索
  const handleSearch = useCallback(() => {
    console.log('[FeedbackHelpMainPage] Search triggered');
    // TODO: 实现搜索功能
  }, []);

  // 处理联系客服
  const handleContactPress = useCallback(() => {
    console.log('[FeedbackHelpMainPage] Contact pressed');
    // TODO: 实现联系客服功能
  }, []);

  // 初始化时重置状态
  useEffect(() => {
    resetToMain();
  }, [resetToMain]);

  // Android硬件返回按钮处理
  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      console.log('[FeedbackHelpMainPage] Android硬件返回按钮被按下');
      NavigationBridge.navigateBack('FeedbackHelpMainPageComponent');
      return true; // 阻止默认行为
    });

    return () => backHandler.remove();
  }, []);

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        title="客服中心"
        onBack={handleBack}
        showSearch={true}
        onSearch={handleSearch}
        pageType="main"
        searchPlaceholder="听书"
      />
      
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <UserSection styles={styles} />
        
        <ConsultSection
          styles={styles}
          categories={consultCategories}
          onCategoryPress={handleCategoryPress}
        />
        
        <FrequentQuestions
          styles={styles}
          questions={frequentQuestions}
          onQuestionPress={handleQuestionPress}
        />
      </ScrollView>

        <ContactSection
          styles={styles}
          onContactPress={handleContactPress}
        />
    </View>
  );
});

console.log('[FeedbackHelpMainPage] Component loaded');