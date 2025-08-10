import React, { useCallback, useEffect } from 'react';
import { View, Text, BackHandler } from 'react-native';
import { useNovelColors } from '../../../utils/theme';
import { createFeedbackHelpPageStyles } from './styles/FeedbackHelpPageStyles';
import { useFeedbackHelpStore } from './store/feedbackHelpStore';
import {
  TopBar,
  QuestionDetail,
} from './components';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';

export const QuestionDetailPage: React.FC = React.memo(() => {
  const colors = useNovelColors();
  const styles = createFeedbackHelpPageStyles(colors);

  const {
    selectedQuestion,
    questionDetails,
    markQuestionResolved,
    selectQuestion,
    isLoading,
    error,
  } = useFeedbackHelpStore();

  // 处理返回操作
  const handleBack = useCallback(() => {
    NavigationBridge.navigateBack('QuestionDetailPageComponent');
  }, []);

  // Android硬件返回按钮处理
  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      console.log('[QuestionDetailPage] Android硬件返回按钮被按下');
      NavigationBridge.navigateBack('QuestionDetailPageComponent');
      return true; // 阻止默认行为
    });

    return () => backHandler.remove();
  }, []);

  // 处理问题解决状态
  const handleResolve = useCallback((isResolved: boolean) => {
    if (selectedQuestion) {
      markQuestionResolved(selectedQuestion, isResolved);
    }
  }, [selectedQuestion, markQuestionResolved]);

  // 处理相关问题点击
  const handleRelatedQuestionPress = useCallback((questionId: string) => {
    console.log('[QuestionDetailPage] Related question selected:', questionId);
    selectQuestion(questionId);
    // 保持在当前页面，只更新内容
  }, [selectQuestion]);

  // 加载状态
  if (isLoading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题详情"
          onBack={handleBack}
          pageType="detail"
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
          title="问题详情"
          onBack={handleBack}
          pageType="detail"
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>❌</Text>
          <Text style={styles.emptyStateTitle}>加载失败</Text>
          <Text style={styles.emptyStateDescription}>{error}</Text>
        </View>
      </View>
    );
  }

  // 如果没有选中问题，显示空状态
  if (!selectedQuestion) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题详情"
          onBack={handleBack}
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>❓</Text>
          <Text style={styles.emptyStateTitle}>请选择问题</Text>
          <Text style={styles.emptyStateDescription}>
            请先选择一个问题查看详细内容
          </Text>
        </View>
      </View>
    );
  }

  const questionDetail = questionDetails[selectedQuestion];
  
  // 如果问题详情不存在
  if (!questionDetail) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题详情"
          onBack={handleBack}
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>🔍</Text>
          <Text style={styles.emptyStateTitle}>问题不存在</Text>
          <Text style={styles.emptyStateDescription}>
            所选问题的详细信息不存在或已被删除
          </Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        title="问题详情"
        onBack={handleBack}
        pageType="detail"
      />
      <QuestionDetail
        styles={styles}
        detail={questionDetail}
        onResolve={handleResolve}
        onRelatedQuestionPress={handleRelatedQuestionPress}
      />
    </View>
  );
});

console.log('[QuestionDetailPage] Component loaded');