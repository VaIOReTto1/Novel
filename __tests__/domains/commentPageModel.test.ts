import {
  bootstrapCommentPage,
  createCommentPageHandlers,
} from '../../src/page/comment/CommentPage/domain/commentPageModel';

describe('comment page domain helpers', () => {
  test('loads comments when book id is present and resets on cleanup', async () => {
    const loadComments = jest.fn().mockResolvedValue(undefined);
    const reset = jest.fn();

    const cleanup = await bootstrapCommentPage({
      bookId: 'book-1',
      loadComments,
      reset,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadComments).toHaveBeenCalledWith('book-1');

    cleanup();

    expect(reset).toHaveBeenCalledTimes(1);
  });

  test('does not load comments when book id is missing', async () => {
    const loadComments = jest.fn().mockResolvedValue(undefined);

    await bootstrapCommentPage({
      bookId: '',
      loadComments,
      reset: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadComments).not.toHaveBeenCalled();
  });

  test('delegates write review navigation and category changes', () => {
    const navigateToWriteReview = jest.fn();
    const loadMoreComments = jest.fn();
    const setSearchQuery = jest.fn();
    const setSelectedCategory = jest.fn();
    const handlers = createCommentPageHandlers({
      bookId: 'book-1',
      navigateBack: jest.fn(),
      navigateToWriteReview,
      loadMoreComments,
      setSearchQuery,
      setSelectedCategory,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleWriteReview();
    handlers.handleLoadMore();
    handlers.handleSearch('keyword');
    handlers.handleCategoryChange('all');

    expect(navigateToWriteReview).toHaveBeenCalledWith('book-1');
    expect(loadMoreComments).toHaveBeenCalledTimes(1);
    expect(setSearchQuery).toHaveBeenCalledWith('keyword');
    expect(setSelectedCategory).toHaveBeenCalledWith('all');
  });
});
