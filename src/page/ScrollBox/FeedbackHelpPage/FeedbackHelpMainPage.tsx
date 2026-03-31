import React, { useEffect, useMemo } from 'react';
import { View, ScrollView } from 'react-native';
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
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  bootstrapFeedbackHelpMainPage,
  createFeedbackHelpMainHandlers,
} from './domain/feedbackHelpPageModel';

export const FeedbackHelpMainPage: React.FC = React.memo(() => {
  const colors = useNovelColors();
  const styles = createFeedbackHelpPageStyles(colors);

  const {
    consultCategories,
    frequentQuestions,
    selectCategory,
    selectQuestion,
    resetToMain,
  } = useFeedbackHelpStore();

  useEffect(() => {
    bootstrapFeedbackHelpMainPage({
      resetToMain,
    });
  }, [resetToMain]);

  const handlers = useMemo(
    () =>
      createFeedbackHelpMainHandlers({
        selectCategory,
        selectQuestion,
        resetToMain,
        navigateBack: () => NavigationBridge.navigateBack('FeedbackHelpMainPageComponent'),
        navigateToQuestionList: () => NavigationBridge.navigateToQuestionList(),
        navigateToQuestionDetail: () => NavigationBridge.navigateToQuestionDetail(),
      }),
    [resetToMain, selectCategory, selectQuestion],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      handlers.handleBack();
      return true;
    });
  }, [handlers]);

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        title="客服中心"
        onBack={handlers.handleBack}
        showSearch={true}
        onSearch={handlers.handleSearch}
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
          onCategoryPress={handlers.handleCategoryPress}
        />

        <FrequentQuestions
          styles={styles}
          questions={frequentQuestions}
          onQuestionPress={handlers.handleQuestionPress}
        />
      </ScrollView>

      <ContactSection
        styles={styles}
        onContactPress={handlers.handleContactPress}
      />
    </View>
  );
});
