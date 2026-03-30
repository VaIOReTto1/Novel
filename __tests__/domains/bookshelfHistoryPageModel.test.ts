import {
  bootstrapBookshelfHistoryPage,
  createHistoryPageHandlers,
} from '../../src/page/BookshelfPage/pages/History/domain/historyPageModel';

const sampleHistoryItem = {
  id: 'history-1',
  title: 'Sample History',
};

describe('bookshelf history page domain helpers', () => {
  test('bootstraps history data once', async () => {
    const loadHistoryItems = jest.fn().mockResolvedValue(undefined);

    await bootstrapBookshelfHistoryPage({
      loadHistoryItems,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadHistoryItems).toHaveBeenCalledTimes(1);
  });

  test('switches tab without triggering duplicate reloads in the handler itself', () => {
    const setCurrentTab = jest.fn();
    const clearSelection = jest.fn();
    const handlers = createHistoryPageHandlers({
      setCurrentTab,
      clearSelection,
      setEditing: jest.fn(),
      removeSelectedItems: jest.fn(),
      toggleShelfStatus: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleTabPress('book');

    expect(setCurrentTab).toHaveBeenCalledWith('book');
    expect(clearSelection).toHaveBeenCalledTimes(1);
  });

  test('exiting edit mode clears selection', () => {
    const clearSelection = jest.fn();
    const setEditing = jest.fn();
    const handlers = createHistoryPageHandlers({
      setCurrentTab: jest.fn(),
      clearSelection,
      setEditing,
      removeSelectedItems: jest.fn(),
      toggleShelfStatus: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleEditToggle(true);

    expect(setEditing).toHaveBeenCalledWith(false);
    expect(clearSelection).toHaveBeenCalledTimes(1);
  });

  test('remove selected exits edit mode after deletion', async () => {
    const removeSelectedItems = jest.fn().mockResolvedValue(undefined);
    const setEditing = jest.fn();
    const handlers = createHistoryPageHandlers({
      setCurrentTab: jest.fn(),
      clearSelection: jest.fn(),
      setEditing,
      removeSelectedItems,
      toggleShelfStatus: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    await handlers.handleRemoveSelected();

    expect(removeSelectedItems).toHaveBeenCalledTimes(1);
    expect(setEditing).toHaveBeenCalledWith(false);
  });

  test('add to shelf delegates to toggleShelfStatus and swallows failures', async () => {
    const toggleShelfStatus = jest
      .fn()
      .mockRejectedValueOnce(new Error('failed'))
      .mockResolvedValueOnce(undefined);
    const logger = {
      log: jest.fn(),
      error: jest.fn(),
    };
    const handlers = createHistoryPageHandlers({
      setCurrentTab: jest.fn(),
      clearSelection: jest.fn(),
      setEditing: jest.fn(),
      removeSelectedItems: jest.fn(),
      toggleShelfStatus,
      logger,
    });

    await handlers.handleAddToShelf(sampleHistoryItem);
    await handlers.handleAddToShelf(sampleHistoryItem);

    expect(toggleShelfStatus).toHaveBeenCalledTimes(2);
    expect(logger.error).toHaveBeenCalledTimes(1);
  });
});
