import React, { useMemo, useCallback, useEffect } from 'react';
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
import { createQuestionListPageHandlers } from './domain/feedbackHelpPageModel';

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

  const handlers = useMemo(
    () =>
      createQuestionListPageHandlers({
        selectQuestion,
        navigateBack: () => NavigationBridge.navigateBack('QuestionListPageComponent'),
        navigateToQuestionDetail: () => NavigationBridge.navigateToQuestionDetail(),
      }),
    [selectQuestion],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBack();
      return true;
    });
  }, [handlers]);

  const getPageTitle = useCallback(() => {
    if (selectedCategory) {
      const category = consultCategories.find(c => c.id === selectedCategory);
      return category?.title || '问题列表';
    }
    return '问题列表';
  }, [selectedCategory, consultCategories]);

  if (isLoading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title={getPageTitle()}
          onBack={handlers.handleBack}
          pageType="list"
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
          title={getPageTitle()}
          onBack={handlers.handleBack}
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>错误</Text>
          <Text style={styles.emptyStateTitle}>加载失败</Text>
          <Text style={styles.emptyStateDescription}>{error}</Text>
        </View>
      </View>
    );
  }

  if (!selectedCategory) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题列表"
          onBack={handlers.handleBack}
          pageType="list"
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>提示</Text>
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
        onBack={handlers.handleBack}
        showSearch={true}
        onSearch={handlers.handleSearch}
        pageType="list"
      />
      <QuestionList
        styles={styles}
        questions={categoryQuestions}
        category={selectedCategory}
        onQuestionPress={handlers.handleQuestionPress}
        onBack={handlers.handleBack}
      />
    </View>
  );
});
