type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapCommentPageDeps = {
  bookId: string;
  loadComments: (bookId: string) => Promise<void>;
  reset: () => void;
  logger?: Logger;
};

type CreateCommentPageHandlersDeps = {
  bookId: string;
  navigateBack: () => void;
  navigateToWriteReview: (bookId: string) => void;
  loadMoreComments: () => void;
  setSearchQuery: (query: string) => void;
  setSelectedCategory: (category: string) => void;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapCommentPage = async ({
  bookId,
  loadComments,
  reset,
  logger = defaultLogger,
}: BootstrapCommentPageDeps): Promise<() => void> => {
  if (bookId) {
    try {
      await loadComments(bookId);
    } catch (error) {
      logger.error('[CommentPage] Failed to bootstrap comments', error);
    }
  }

  return () => {
    reset();
  };
};

export const createCommentPageHandlers = ({
  bookId,
  navigateBack,
  navigateToWriteReview,
  loadMoreComments,
  setSearchQuery,
  setSelectedCategory,
  logger = defaultLogger,
}: CreateCommentPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack();
  },

  handleLoadMore: () => {
    loadMoreComments();
  },

  handleSearch: (query: string) => {
    setSearchQuery(query);
    logger.log('[CommentPage] Search comments', query);
  },

  handleCategoryChange: (category: string) => {
    setSelectedCategory(category);
    logger.log('[CommentPage] Change category', category);
  },

  handleWriteReview: () => {
    navigateToWriteReview(bookId);
  },
});
