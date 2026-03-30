import {
  bootstrapWriteReviewPage,
  createWriteReviewPageHandlers,
  canSubmitWriteReview,
} from '../../src/page/comment/WriteReviewPage/domain/writeReviewPageModel';

describe('write review page domain helpers', () => {
  test('navigates back when book id is missing during bootstrap', () => {
    const navigateBack = jest.fn();

    bootstrapWriteReviewPage({
      bookId: '',
      source: 'comment',
      navigateBack,
      logger: {
        log: jest.fn(),
        warn: jest.fn(),
      },
    });

    expect(navigateBack).toHaveBeenCalledTimes(1);
  });

  test('submits review only when book id exists', async () => {
    const submitReview = jest.fn().mockResolvedValue(true);
    const handlers = createWriteReviewPageHandlers({
      bookId: 'book-1',
      submitReview,
      clearErrors: jest.fn(),
      dismissKeyboard: jest.fn(),
      navigateBack: jest.fn(),
    });

    await handlers.handleSubmit();

    expect(submitReview).toHaveBeenCalledWith('book-1');
  });

  test('clears errors on input focus and dismisses keyboard on background press', () => {
    const clearErrors = jest.fn();
    const dismissKeyboard = jest.fn();
    const handlers = createWriteReviewPageHandlers({
      bookId: 'book-1',
      submitReview: jest.fn(),
      clearErrors,
      dismissKeyboard,
      navigateBack: jest.fn(),
    });

    handlers.handleInputFocus();
    handlers.handleDismissKeyboard();

    expect(clearErrors).toHaveBeenCalledTimes(1);
    expect(dismissKeyboard).toHaveBeenCalledTimes(1);
  });

  test('computes can submit from rating, content length and submission state', () => {
    expect(canSubmitWriteReview({ rating: 5, content: '1234567890', isSubmitting: false })).toBe(true);
    expect(canSubmitWriteReview({ rating: 0, content: '1234567890', isSubmitting: false })).toBe(false);
    expect(canSubmitWriteReview({ rating: 5, content: 'short', isSubmitting: false })).toBe(false);
    expect(canSubmitWriteReview({ rating: 5, content: '1234567890', isSubmitting: true })).toBe(false);
  });
});
