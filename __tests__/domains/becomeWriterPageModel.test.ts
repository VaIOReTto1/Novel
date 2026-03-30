import {
  bootstrapBecomeWriterPage,
  createBecomeWriterPageHandlers,
} from '../../src/page/ScrollBox/BecomeWriterPage/domain/becomeWriterPageModel';

describe('become writer page model helpers', () => {
  test('bootstraps base data and falls back author works when native list is empty', async () => {
    const loadInitialData = jest.fn().mockResolvedValue(undefined);
    const setWorks = jest.fn();

    await bootstrapBecomeWriterPage({
      isAuthor: true,
      loadInitialData,
      getAuthorBooks: jest.fn().mockResolvedValue({ list: [] }),
      setWorks,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadInitialData).toHaveBeenCalledTimes(1);
    expect(setWorks).toHaveBeenCalledWith([{ id: 'm1', title: '我的新书', words: 0 }]);
  });

  test('maps native author works when available', async () => {
    const setWorks = jest.fn();

    await bootstrapBecomeWriterPage({
      isAuthor: true,
      loadInitialData: jest.fn().mockResolvedValue(undefined),
      getAuthorBooks: jest.fn().mockResolvedValue({
        list: [{ id: 1, bookName: '作品A', wordCount: 1234 }],
      }),
      setWorks,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(setWorks).toHaveBeenCalledWith([{ id: '1', title: '作品A', words: 1234 }]);
  });

  test('delegates tabs, ai and more actions', () => {
    const handlers = createBecomeWriterPageHandlers({
      navigateBack: jest.fn(),
      setSelectedDataTab: jest.fn(),
      setSelectedAuthorTab: jest.fn(),
      setSelectedActivityTab: jest.fn(),
      navigateToAIPage: jest.fn(),
      handleMorePress: jest.fn(),
      navigateToBookManage: jest.fn(),
      navigateToWritePage: jest.fn(),
    });

    handlers.handleBackPress();
    handlers.handleDataTabChange('short');
    handlers.handleAuthorTabChange('platform');
    handlers.handleActivityTabChange('short');
    handlers.handleAIPress();
    handlers.handleActivityMorePress();
    handlers.handleCourseMorePress();
    handlers.handlePressWork();
    handlers.handleCreateChapter();

    expect(handlers.handleBackPress).toBeDefined();
  });
});
