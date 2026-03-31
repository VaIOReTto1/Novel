import {
  bootstrapScrollBoxHistoryPage,
  createScrollBoxHistoryPageHandlers,
} from '../../src/page/ScrollBox/HistoryPage/domain/scrollboxHistoryPageModel';

describe('scrollbox history page model helpers', () => {
  test('bootstraps scrollbox history page once', async () => {
    const loadHistoryItems = jest.fn().mockResolvedValue(undefined);

    await bootstrapScrollBoxHistoryPage({
      loadHistoryItems,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadHistoryItems).toHaveBeenCalledTimes(1);
  });

  test('delegates back, tab and view type actions', () => {
    const navigateBack = jest.fn();
    const setSelectedTab = jest.fn();
    const setViewType = jest.fn();
    const handlers = createScrollBoxHistoryPageHandlers({
      navigateBack,
      setSelectedTab,
      setViewType,
    });

    handlers.handleBackPress();
    handlers.handleTabPress('book');
    handlers.handleViewTypeChange('list');

    expect(navigateBack).toHaveBeenCalledTimes(1);
    expect(setSelectedTab).toHaveBeenCalledWith('book');
    expect(setViewType).toHaveBeenCalledWith('list');
  });
});
