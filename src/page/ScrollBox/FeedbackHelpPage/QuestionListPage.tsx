import React, { useCallback, useEffect } from 'react';
import { View, Text } from 'react-native';
import { useNovelColors } from '../../../utils/theme';
import { createFeedbackHelpPageStyles } from './styles/FeedbackHelpPageStyles';
import { useFeedbackHelpStore } from './store/feedbackHelpStore';
import {
  TopBar,
  QuestionList,
} from './components';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';

export const QuestionListPage: React.FC = React.memo(() => {
  const colors = useNovelColors();
  const styles = createFeedbackHelpPageStyles(colors);

  const {
    selectedCategory,
    consultCategories,
    selectQuestion,
    getCategoryQuestions,
    isLoading,
    error,
  } = useFeedbackHelpStore();

  // 处理问题点击 - 导航到问题详情页
  const handleQuestionPress = useCallback((questionId: string) => {
    console.log('[QuestionListPage] Question selected:', questionId);
    selectQuestion(questionId);
    // 导航到问题详情页面
    NavigationBridge.navigateToQuestionDetail();
  }, [selectQuestion]);

  // 处理返回操作
  const handleBack = useCallback(() => {
    NavigationBridge.navigateBack('QuestionListPageComponent');
  }, []);

  // Android硬件返回按钮处理
  useEffect(() => {
    return registerHardwareBackHandler(() => {
      console.log('[QuestionListPage] Android硬件返回按钮被按下');
      NavigationBridge.navigateBack('QuestionListPageComponent');
      return true; // 阻止默认行为
    });
  }, []);

  // 处理搜索
  const handleSearch = useCallback(() => {
    console.log('[QuestionListPage] Search triggered');
    // TODO: 实现搜索功能
  }, []);

  // 获取当前页面标题
  const getPageTitle = useCallback(() => {
    if (selectedCategory) {
      const category = consultCategories.find(c => c.id === selectedCategory);
      return category?.title || '问题列表';
    }
    return '问题列表';
  }, [selectedCategory, consultCategories]);

  // 加载状态
  if (isLoading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title={getPageTitle()}
          onBack={handleBack}
          pageType="list"
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
          title={getPageTitle()}
          onBack={handleBack}
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>❌</Text>
          <Text style={styles.emptyStateTitle}>加载失败</Text>
          <Text style={styles.emptyStateDescription}>{error}</Text>
        </View>
      </View>
    );
  }

  // 如果没有选中分类，显示空状态
  if (!selectedCategory) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题列表"
          onBack={handleBack}
          pageType="list"
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>📋</Text>
          <Text style={styles.emptyStateTitle}>请选择分类</Text>
          <Text style={styles.emptyStateDescription}>
            请先选择一个咨询分类查看相关问题
          </Text>
        </View>
      </View>
    );
  }

  const categoryQuestions = getCategoryQuestions(selectedCategory);

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        title={getPageTitle()}
        onBack={handleBack}
        showSearch={true}
        onSearch={handleSearch}
        pageType="list"
      />
      <QuestionList
        styles={styles}
        questions={categoryQuestions}
        category={selectedCategory}
        onQuestionPress={handleQuestionPress}
        onBack={handleBack}
      />
    </View>
  );
});

console.log('[QuestionListPage] Component loaded');
