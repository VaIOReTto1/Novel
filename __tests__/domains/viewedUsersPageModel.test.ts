import {
  bootstrapViewedUsersPage,
  createViewedUsersPageHandlers,
  getViewedUsersCurrentUsers,
} from '../../src/page/ScrollBox/ViewedUsersPage/domain/viewedUsersPageModel';

describe('viewed users page model helpers', () => {
  test('bootstraps viewed users page once', async () => {
    const loadInitialData = jest.fn().mockResolvedValue(undefined);

    await bootstrapViewedUsersPage({
      loadInitialData,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadInitialData).toHaveBeenCalledTimes(1);
  });

  test('delegates tab, follow, user and view-more actions', () => {
    const setSelectedTab = jest.fn();
    const handleFollowUser = jest.fn();
    const handleViewMoreRecommend = jest.fn();
    const handleUserPress = jest.fn();
    const handlers = createViewedUsersPageHandlers({
      navigateBack: jest.fn(),
      setSelectedTab,
      handleFollowUser,
      handleViewMoreRecommend,
      handleUserPress,
    });

    handlers.handleBackPress();
    handlers.handleTabChange('fans');
    handlers.handleFollowPress('user-1');
    handlers.handleUserItemPress('user-2');
    handlers.handleViewMorePress();

    expect(setSelectedTab).toHaveBeenCalledWith('fans');
    expect(handleFollowUser).toHaveBeenCalledWith('user-1');
    expect(handleUserPress).toHaveBeenCalledWith('user-2');
    expect(handleViewMoreRecommend).toHaveBeenCalledTimes(1);
  });

  test('selects current users by tab', () => {
    expect(
      getViewedUsersCurrentUsers({
        selectedTab: 'following',
        viewedUsers: [],
        recommendUsers: [],
        followingUsers: [{ id: '1' }],
        fansUsers: [],
      }),
    ).toEqual([{ id: '1' }]);
  });
});
