import {
  bootstrapBookManagePage,
  createBookManagePageHandlers,
} from '../../src/page/Writer/BookManage/domain/bookManagePageModel';

describe('book manage page domain helpers', () => {
  test('bootstraps book manage page by loading store data', async () => {
    const load = jest.fn().mockResolvedValue(undefined);

    await bootstrapBookManagePage({
      load,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(load).toHaveBeenCalledTimes(1);
  });

  test('delegates navigation handlers to the writer host routes', () => {
    const navigateBack = jest.fn();
    const navigateToWritePage = jest.fn();
    const navigateToBookManage = jest.fn();
    const handlers = createBookManagePageHandlers({
      navigateBack,
      navigateToWritePage,
      navigateToBookManage,
    });

    handlers.handleBack();
    handlers.handleContinueDraft();
    handlers.handleOpenBookManage();
    handlers.handleCreateChapter();

    expect(navigateBack).toHaveBeenCalledTimes(1);
    expect(navigateToWritePage).toHaveBeenCalledTimes(2);
    expect(navigateToBookManage).toHaveBeenCalledTimes(1);
  });
});
