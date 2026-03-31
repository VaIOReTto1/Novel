import {
  bootstrapMyReservationPage,
  createMyReservationPageHandlers,
  getMyReservationContentState,
} from '../../src/page/ScrollBox/MyReservationPage/domain/myReservationPageModel';

describe('my reservation page model helpers', () => {
  test('bootstraps my reservation page once', async () => {
    const loadInitialData = jest.fn().mockResolvedValue(undefined);

    await bootstrapMyReservationPage({
      loadInitialData,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadInitialData).toHaveBeenCalledTimes(1);
  });

  test('delegates main/sub tab and item actions', () => {
    const setSelectedTab = jest.fn();
    const setSelectedSubTab = jest.fn();
    const handleReserveItem = jest.fn();
    const handleItemPress = jest.fn();
    const handlers = createMyReservationPageHandlers({
      navigateBack: jest.fn(),
      setSelectedTab,
      setSelectedSubTab,
      handleReserveItem,
      handleItemPress,
    });

    handlers.handleBackPress();
    handlers.handleMainTabChange('mine');
    handlers.handleSubTabChange('offline');
    handlers.handleReservePress('item-1');
    handlers.handleItemPress('item-2');

    expect(setSelectedTab).toHaveBeenCalledWith('mine');
    expect(setSelectedSubTab).toHaveBeenCalledWith('offline');
    expect(handleReserveItem).toHaveBeenCalledWith('item-1');
    expect(handleItemPress).toHaveBeenCalledWith('item-2');
  });

  test('computes content state for new and mine tabs', () => {
    expect(
      getMyReservationContentState({
        selectedTab: 'new',
        selectedSubTab: 'online',
        newReservations: [{ id: '1' }],
        myOnlineReservations: [],
        myOfflineReservations: [],
        emptyStates: {
          online: { icon: 'a', title: 'online' },
          offline: { icon: 'b', title: 'offline' },
        },
      }),
    ).toEqual({
      items: [{ id: '1' }],
      emptyData: null,
    });

    expect(
      getMyReservationContentState({
        selectedTab: 'mine',
        selectedSubTab: 'offline',
        newReservations: [],
        myOnlineReservations: [],
        myOfflineReservations: [],
        emptyStates: {
          online: { icon: 'a', title: 'online' },
          offline: { icon: 'b', title: 'offline' },
        },
      }),
    ).toEqual({
      items: [],
      emptyData: { icon: 'b', title: 'offline' },
    });
  });
});
