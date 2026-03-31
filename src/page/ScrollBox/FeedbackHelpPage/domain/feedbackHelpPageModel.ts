type Logger = Pick<Console, 'log' | 'error'>;

type CreateFeedbackHelpMainHandlersDeps = {
  selectCategory: (categoryId: string) => void;
  selectQuestion: (questionId: string) => void;
  resetToMain: () => void;
  navigateBack: () => void;
  navigateToQuestionList: () => void;
  navigateToQuestionDetail: () => void;
  logger?: Logger;
};

type CreateQuestionListHandlersDeps = {
  selectQuestion: (questionId: string) => void;
  navigateBack: () => void;
  navigateToQuestionDetail: () => void;
  logger?: Logger;
};

type CreateQuestionDetailHandlersDeps = {
  navigateBack: () => void;
  markQuestionResolved: (questionId: string, isResolved: boolean) => void;
  selectQuestion: (questionId: string) => void;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapFeedbackHelpMainPage = ({
  resetToMain,
}: {
  resetToMain: () => void;
}): void => {
  resetToMain();
};

export const createFeedbackHelpMainHandlers = ({
  selectCategory,
  selectQuestion,
  resetToMain,
  navigateBack,
  navigateToQuestionList,
  navigateToQuestionDetail,
}: CreateFeedbackHelpMainHandlersDeps) => ({
  handleCategoryPress: (categoryId: string) => {
    selectCategory(categoryId);
    navigateToQuestionList();
  },

  handleQuestionPress: (questionId: string) => {
    selectQuestion(questionId);
    navigateToQuestionDetail();
  },

  handleBack: () => {
    navigateBack();
  },

  handleSearch: () => {},

  handleContactPress: () => {},

  reset: () => {
    resetToMain();
  },
});

export const createQuestionListPageHandlers = ({
  selectQuestion,
  navigateBack,
  navigateToQuestionDetail,
}: CreateQuestionListHandlersDeps) => ({
  handleQuestionPress: (questionId: string) => {
    selectQuestion(questionId);
    navigateToQuestionDetail();
  },

  handleBack: () => {
    navigateBack();
  },

  handleSearch: () => {},
});

export const createQuestionDetailPageHandlers = ({
  navigateBack,
  markQuestionResolved,
  selectQuestion,
}: CreateQuestionDetailHandlersDeps) => ({
  handleBack: () => {
    navigateBack();
  },

  handleResolve: (selectedQuestion: string | null | undefined, isResolved: boolean) => {
    if (selectedQuestion) {
      markQuestionResolved(selectedQuestion, isResolved);
    }
  },

  handleRelatedQuestionPress: (questionId: string) => {
    selectQuestion(questionId);
  },
});
