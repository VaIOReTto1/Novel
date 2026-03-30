import {
  bootstrapRecommendBookPage,
  createRecommendBookPageHandlers,
} from '../../src/page/ScrollBox/RecommendBookPage/domain/recommendBookPageModel';

describe('recommend book page model helpers', () => {
  test('bootstraps recommend book page once', async () => {
    const loadInitialData = jest.fn().mockResolvedValue(undefined);

    await bootstrapRecommendBookPage({
      loadInitialData,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadInitialData).toHaveBeenCalledTimes(1);
  });

  test('delegates page handlers to store and alert callbacks', () => {
    const navigateBack = jest.fn();
    const setSelectedTaskTab = jest.fn();
    const handleViewAllTasks = jest.fn();
    const handleServicePress = jest.fn();
    const handleTaskPress = jest.fn();
    const showWithdrawAlert = jest.fn();
    const handlers = createRecommendBookPageHandlers({
      navigateBack,
      setSelectedTaskTab,
      handleViewAllTasks,
      handleServicePress,
      handleTaskPress,
      showWithdrawAlert,
    });

    handlers.handleBackPress();
    handlers.handleTaskTabChange('book');
    handlers.handleWithdrawPress();
    handlers.handleViewAllTasksPress();
    handlers.handleServiceItemPress('service-1');
    handlers.handleTaskItemPress('task-1');

    expect(navigateBack).toHaveBeenCalledTimes(1);
    expect(setSelectedTaskTab).toHaveBeenCalledWith('book');
    expect(showWithdrawAlert).toHaveBeenCalledTimes(1);
    expect(handleViewAllTasks).toHaveBeenCalledTimes(1);
    expect(handleServicePress).toHaveBeenCalledWith('service-1');
    expect(handleTaskPress).toHaveBeenCalledWith('task-1');
  });
});
