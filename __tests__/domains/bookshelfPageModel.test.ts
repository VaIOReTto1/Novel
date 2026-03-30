import {
  bootstrapBookshelfPage,
  createBookshelfPageHandlers,
} from '../../src/page/BookshelfPage/pages/Bookshelf/domain/bookshelfPageModel';

const sampleBook = {
  id: 'book-1',
  title: 'Sample Book',
};

const sampleRecommendation = {
  id: 'rec-1',
  title: 'Sample Recommendation',
};

describe('bookshelf page domain helpers', () => {
  test('bootstraps bookshelf and recommendation data together', async () => {
    const loadBookshelfItems = jest.fn().mockResolvedValue(undefined);
    const loadRecommendations = jest.fn().mockResolvedValue(undefined);

    await bootstrapBookshelfPage({
      loadBookshelfItems,
      loadRecommendations,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadBookshelfItems).toHaveBeenCalledTimes(1);
    expect(loadRecommendations).toHaveBeenCalledTimes(1);
  });

  test('book press toggles selection in edit mode', () => {
    const toggleItemSelection = jest.fn();
    const handlers = createBookshelfPageHandlers({
      isEditMode: true,
      enterEditMode: jest.fn(),
      exitEditMode: jest.fn(),
      toggleItemSelection,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleBookPress(sampleBook);

    expect(toggleItemSelection).toHaveBeenCalledWith('book-1');
  });

  test('book long press enters edit mode and selects the item when edit mode is off', () => {
    const enterEditMode = jest.fn();
    const toggleItemSelection = jest.fn();
    const handlers = createBookshelfPageHandlers({
      isEditMode: false,
      enterEditMode,
      exitEditMode: jest.fn(),
      toggleItemSelection,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleBookLongPress(sampleBook);

    expect(enterEditMode).toHaveBeenCalledTimes(1);
    expect(toggleItemSelection).toHaveBeenCalledWith('book-1');
  });

  test('edit press exits edit mode when already editing', () => {
    const exitEditMode = jest.fn();
    const handlers = createBookshelfPageHandlers({
      isEditMode: true,
      enterEditMode: jest.fn(),
      exitEditMode,
      toggleItemSelection: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleEditPress();

    expect(exitEditMode).toHaveBeenCalledTimes(1);
  });

  test('recommendation press falls back to logging for now', () => {
    const logger = {
      log: jest.fn(),
      error: jest.fn(),
    };
    const handlers = createBookshelfPageHandlers({
      isEditMode: false,
      enterEditMode: jest.fn(),
      exitEditMode: jest.fn(),
      toggleItemSelection: jest.fn(),
      logger,
    });

    handlers.handleRecommendationPress(sampleRecommendation);

    expect(logger.log).toHaveBeenCalledWith(
      '[BookshelfPage] Open recommendation',
      'Sample Recommendation',
    );
  });
});
