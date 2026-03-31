import React, { useMemo, useEffect } from 'react';
import { View, Text } from 'react-native';
import { useNovelColors } from '../../../utils/theme';
import { createFeedbackHelpPageStyles } from './styles/FeedbackHelpPageStyles';
import { useFeedbackHelpStore } from './store/feedbackHelpStore';
import {
  TopBar,
  QuestionDetail,
} from './components';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import { createQuestionDetailPageHandlers } from './domain/feedbackHelpPageModel';

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

  const handlers = useMemo(
    () =>
      createQuestionDetailPageHandlers({
        navigateBack: () => NavigationBridge.navigateBack('QuestionDetailPageComponent'),
        markQuestionResolved,
        selectQuestion,
      }),
    [markQuestionResolved, selectQuestion],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBack();
      return true;
    });
  }, [handlers]);

  if (isLoading) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题详情"
          onBack={handlers.handleBack}
          pageType="detail"
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
          title="问题详情"
          onBack={handlers.handleBack}
          pageType="detail"
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>错误</Text>
          <Text style={styles.emptyStateTitle}>加载失败</Text>
          <Text style={styles.emptyStateDescription}>{error}</Text>
        </View>
      </View>
    );
  }

  if (!selectedQuestion) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题详情"
          onBack={handlers.handleBack}
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>提示</Text>
          <Text style={styles.emptyStateTitle}>请选择问题</Text>
          <Text style={styles.emptyStateDescription}>
            请先选择一个问题查看详细内容
          </Text>
        </View>
      </View>
    );
  }

  const questionDetail = questionDetails[selectedQuestion];

  if (!questionDetail) {
    return (
      <View style={styles.container}>
        <TopBar
          styles={styles}
          title="问题详情"
          onBack={handlers.handleBack}
        />
        <View style={styles.emptyState}>
          <Text style={styles.emptyStateIcon}>提示</Text>
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
        onBack={handlers.handleBack}
        pageType="detail"
      />
      <QuestionDetail
        styles={styles}
        detail={questionDetail}
        onResolve={(isResolved) => handlers.handleResolve(selectedQuestion, isResolved)}
        onRelatedQuestionPress={handlers.handleRelatedQuestionPress}
      />
    </View>
  );
});
