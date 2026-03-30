type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapReviewDetailPageDeps = {
  commentData: string;
  setParsedCommentData: (value: unknown) => void;
  loadReviewDetail: (reviewId: string) => Promise<void> | void;
  reset: () => void;
  logger?: Logger;
};

type CreateReviewDetailPageHandlersDeps = {
  navigateBack: () => void;
  loadMoreComments: () => void;
  toggleCommentLike: (commentId: string) => void;
  toggleCommentDislike: (commentId: string) => void;
  setSelectedComment: (comment: unknown) => void;
  openRepliesSheet: () => void;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapReviewDetailPage = async ({
  commentData,
  setParsedCommentData,
  loadReviewDetail,
  reset,
  logger = defaultLogger,
}: BootstrapReviewDetailPageDeps): Promise<() => void> => {
  if (commentData) {
    try {
      const comment = JSON.parse(commentData);
      setParsedCommentData(comment);

      if (comment && typeof comment.id === 'string') {
        await loadReviewDetail(comment.id);
      }
    } catch (error) {
      logger.error('[ReviewDetailPage] Failed to parse comment data', error);
    }
  }

  return () => {
    reset();
  };
};

export const createReviewDetailPageHandlers = ({
  navigateBack,
  loadMoreComments,
  toggleCommentLike,
  toggleCommentDislike,
  setSelectedComment,
  openRepliesSheet,
  logger = defaultLogger,
}: CreateReviewDetailPageHandlersDeps) => ({
  handleBack: () => {
    navigateBack();
  },

  handleLoadMore: () => {
    loadMoreComments();
  },

  handleLike: (commentId: string) => {
    toggleCommentLike(commentId);
  },

  handleDislike: (commentId: string) => {
    toggleCommentDislike(commentId);
  },

  handleReply: (commentId: string) => {
    logger.log('[ReviewDetailPage] Reply comment', commentId);
  },

  handleViewMoreReplies: (comment: unknown) => {
    setSelectedComment(comment);
    openRepliesSheet();
  },
});
