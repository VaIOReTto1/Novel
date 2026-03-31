import {
  bootstrapFeedbackHelpMainPage,
  createFeedbackHelpMainHandlers,
  createQuestionListPageHandlers,
  createQuestionDetailPageHandlers,
} from '../../src/page/ScrollBox/FeedbackHelpPage/domain/feedbackHelpPageModel';

describe('feedback help page model helpers', () => {
  test('resets main page state on bootstrap', () => {
    const resetToMain = jest.fn();

    bootstrapFeedbackHelpMainPage({
      resetToMain,
    });

    expect(resetToMain).toHaveBeenCalledTimes(1);
  });

  test('main page delegates category and question navigation', () => {
    const selectCategory = jest.fn();
    const selectQuestion = jest.fn();
    const navigateToQuestionList = jest.fn();
    const navigateToQuestionDetail = jest.fn();
    const handlers = createFeedbackHelpMainHandlers({
      selectCategory,
      selectQuestion,
      resetToMain: jest.fn(),
      navigateBack: jest.fn(),
      navigateToQuestionList,
      navigateToQuestionDetail,
    });

    handlers.handleCategoryPress('account');
    handlers.handleQuestionPress('q1');

    expect(selectCategory).toHaveBeenCalledWith('account');
    expect(selectQuestion).toHaveBeenCalledWith('q1');
    expect(navigateToQuestionList).toHaveBeenCalledTimes(1);
    expect(navigateToQuestionDetail).toHaveBeenCalledTimes(1);
  });

  test('question list delegates selection and back', () => {
    const selectQuestion = jest.fn();
    const navigateBack = jest.fn();
    const navigateToQuestionDetail = jest.fn();
    const handlers = createQuestionListPageHandlers({
      selectQuestion,
      navigateBack,
      navigateToQuestionDetail,
    });

    handlers.handleQuestionPress('q2');
    handlers.handleBack();

    expect(selectQuestion).toHaveBeenCalledWith('q2');
    expect(navigateToQuestionDetail).toHaveBeenCalledTimes(1);
    expect(navigateBack).toHaveBeenCalledTimes(1);
  });

  test('question detail delegates resolve and related question selection', () => {
    const markQuestionResolved = jest.fn();
    const selectQuestion = jest.fn();
    const navigateBack = jest.fn();
    const handlers = createQuestionDetailPageHandlers({
      navigateBack,
      markQuestionResolved,
      selectQuestion,
    });

    handlers.handleResolve('q3', true);
    handlers.handleRelatedQuestionPress('q4');
    handlers.handleBack();

    expect(markQuestionResolved).toHaveBeenCalledWith('q3', true);
    expect(selectQuestion).toHaveBeenCalledWith('q4');
    expect(navigateBack).toHaveBeenCalledTimes(1);
  });
});
