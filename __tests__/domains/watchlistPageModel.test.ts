import {
  bootstrapWatchlistPage,
  createWatchlistPageHandlers,
} from '../../src/page/BookshelfPage/pages/Watchlist/domain/watchlistPageModel';

const sampleWatchlistItem = {
  id: 'watchlist-1',
  title: 'Sample Drama',
};

describe('watchlist page domain helpers', () => {
  test('bootstraps watchlist data once', async () => {
    const loadWatchlistItems = jest.fn().mockResolvedValue(undefined);

    await bootstrapWatchlistPage({
      loadWatchlistItems,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadWatchlistItems).toHaveBeenCalledTimes(1);
  });

  test('enters edit mode when edit is pressed outside edit mode', () => {
    const enterEditMode = jest.fn();
    const exitEditMode = jest.fn();
    const handlers = createWatchlistPageHandlers({
      isEditMode: false,
      enterEditMode,
      exitEditMode,
      selectAllItems: jest.fn(),
      deleteSelectedItems: jest.fn(),
      toggleItemSelection: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleEditPress();

    expect(enterEditMode).toHaveBeenCalledTimes(1);
    expect(exitEditMode).not.toHaveBeenCalled();
  });

  test('exits edit mode when edit is pressed inside edit mode', () => {
    const enterEditMode = jest.fn();
    const exitEditMode = jest.fn();
    const handlers = createWatchlistPageHandlers({
      isEditMode: true,
      enterEditMode,
      exitEditMode,
      selectAllItems: jest.fn(),
      deleteSelectedItems: jest.fn(),
      toggleItemSelection: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleEditPress();

    expect(exitEditMode).toHaveBeenCalledTimes(1);
    expect(enterEditMode).not.toHaveBeenCalled();
  });

  test('delete delegates to deleteSelectedItems', async () => {
    const deleteSelectedItems = jest.fn().mockResolvedValue(undefined);
    const handlers = createWatchlistPageHandlers({
      isEditMode: false,
      enterEditMode: jest.fn(),
      exitEditMode: jest.fn(),
      selectAllItems: jest.fn(),
      deleteSelectedItems,
      toggleItemSelection: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    await handlers.handleDelete();

    expect(deleteSelectedItems).toHaveBeenCalledTimes(1);
  });

  test('item select delegates to toggleItemSelection', () => {
    const toggleItemSelection = jest.fn();
    const handlers = createWatchlistPageHandlers({
      isEditMode: false,
      enterEditMode: jest.fn(),
      exitEditMode: jest.fn(),
      selectAllItems: jest.fn(),
      deleteSelectedItems: jest.fn(),
      toggleItemSelection,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleItemSelect(sampleWatchlistItem);

    expect(toggleItemSelection).toHaveBeenCalledWith('watchlist-1');
  });
});
