import {
  bootstrapReviewDetailPage,
  createReviewDetailPageHandlers,
} from '../../src/page/comment/ReviewDetailPage/domain/reviewDetailPageModel';

describe('review detail page domain helpers', () => {
  test('parses comment data, loads review detail and resets on cleanup', async () => {
    const setParsedCommentData = jest.fn();
    const loadReviewDetail = jest.fn().mockResolvedValue(undefined);
    const reset = jest.fn();

    const cleanup = await bootstrapReviewDetailPage({
      commentData: JSON.stringify({ id: 'review-1', title: 'review' }),
      setParsedCommentData,
      loadReviewDetail,
      reset,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(setParsedCommentData).toHaveBeenCalledWith({ id: 'review-1', title: 'review' });
    expect(loadReviewDetail).toHaveBeenCalledWith('review-1');

    cleanup();

    expect(reset).toHaveBeenCalledTimes(1);
  });

  test('ignores malformed comment data but still returns cleanup', async () => {
    const setParsedCommentData = jest.fn();
    const loadReviewDetail = jest.fn();
    const reset = jest.fn();
    const logger = {
      log: jest.fn(),
      error: jest.fn(),
    };

    const cleanup = await bootstrapReviewDetailPage({
      commentData: '{invalid',
      setParsedCommentData,
      loadReviewDetail,
      reset,
      logger,
    });

    expect(setParsedCommentData).not.toHaveBeenCalled();
    expect(loadReviewDetail).not.toHaveBeenCalled();
    expect(logger.error).toHaveBeenCalledTimes(1);

    cleanup();
    expect(reset).toHaveBeenCalledTimes(1);
  });

  test('delegates reply sheet and comment actions', () => {
    const setSelectedComment = jest.fn();
    const openRepliesSheet = jest.fn();
    const loadMoreComments = jest.fn();
    const toggleCommentLike = jest.fn();
    const toggleCommentDislike = jest.fn();
    const navigateBack = jest.fn();
    const handlers = createReviewDetailPageHandlers({
      navigateBack,
      loadMoreComments,
      toggleCommentLike,
      toggleCommentDislike,
      setSelectedComment,
      openRepliesSheet,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleBack();
    handlers.handleLoadMore();
    handlers.handleLike('comment-1');
    handlers.handleDislike('comment-2');
    handlers.handleViewMoreReplies({ id: 'reply-1' });

    expect(navigateBack).toHaveBeenCalledTimes(1);
    expect(loadMoreComments).toHaveBeenCalledTimes(1);
    expect(toggleCommentLike).toHaveBeenCalledWith('comment-1');
    expect(toggleCommentDislike).toHaveBeenCalledWith('comment-2');
    expect(setSelectedComment).toHaveBeenCalledWith({ id: 'reply-1' });
    expect(openRepliesSheet).toHaveBeenCalledTimes(1);
  });
});
