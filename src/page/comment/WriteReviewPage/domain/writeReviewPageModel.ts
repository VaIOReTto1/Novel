type Logger = Pick<Console, 'log' | 'warn'>;

type BootstrapWriteReviewPageDeps = {
  bookId?: string;
  source?: string;
  navigateBack: () => void;
  logger?: Logger;
};

type CreateWriteReviewPageHandlersDeps = {
  bookId?: string;
  submitReview: (bookId: string) => Promise<boolean>;
  clearErrors: () => void;
  dismissKeyboard: () => void;
  navigateBack: (componentName?: string) => void;
};

type CanSubmitWriteReviewArgs = {
  rating: number;
  content: string;
  isSubmitting: boolean;
};

const defaultLogger: Logger = console;

export const bootstrapWriteReviewPage = ({
  bookId,
  source,
  navigateBack,
  logger = defaultLogger,
}: BootstrapWriteReviewPageDeps): void => {
  if (source) {
    logger.log('[WriteReviewPage] Source', source);
  }

  if (!bookId) {
    logger.warn('[WriteReviewPage] bookId is required');
    navigateBack();
  }
};

export const createWriteReviewPageHandlers = ({
  bookId,
  submitReview,
  clearErrors,
  dismissKeyboard,
  navigateBack,
}: CreateWriteReviewPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack('WriteReviewPageComponent');
  },

  handleSubmit: async () => {
    if (!bookId) {
      return;
    }
    await submitReview(bookId);
  },

  handleDismissKeyboard: () => {
    dismissKeyboard();
  },

  handleInputFocus: () => {
    clearErrors();
  },
});

export const canSubmitWriteReview = ({
  rating,
  content,
  isSubmitting,
}: CanSubmitWriteReviewArgs): boolean =>
  rating > 0 && content.trim().length >= 10 && !isSubmitting;
