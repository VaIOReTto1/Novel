import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/MyReservationPage/styles/MyReservationPageStyles', () => ({
  createMyReservationPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelBackground: '#fff',
  }),
}));

jest.mock('../../src/page/ScrollBox/MyReservationPage/components', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return {
    TopBar: () => ReactMock.createElement(MockText, null, 'MyReservationTopBar'),
    SubTabsSection: () => ReactMock.createElement(MockText, null, 'MyReservationSubTabs'),
    ReservationGrid: () => ReactMock.createElement(MockText, null, 'MyReservationGrid'),
    EmptyState: () => ReactMock.createElement(MockText, null, 'MyReservationEmpty'),
  };
});

jest.mock('../../src/page/ScrollBox/MyReservationPage/store/myReservationStore', () => ({
  useMyReservationStore: () => ({
    loading: false,
    error: null,
    selectedTab: 'mine',
    selectedSubTab: 'online',
    newReservations: [],
    myOnlineReservations: [{ id: 'r1', title: '未来事务所' }],
    myOfflineReservations: [],
    emptyStates: {
      online: {},
      offline: {},
    },
    loadInitialData: jest.fn(),
    setSelectedTab: jest.fn(),
    setSelectedSubTab: jest.fn(),
    handleReserveItem: jest.fn(),
    handleItemPress: jest.fn(),
  }),
}));

jest.mock('../../src/page/ScrollBox/MyReservationPage/domain/myReservationPageModel', () => ({
  bootstrapMyReservationPage: jest.fn(),
  createMyReservationPageHandlers: () => ({
    handleBackPress: jest.fn(),
    handleMainTabChange: jest.fn(),
    handleSubTabChange: jest.fn(),
    handleItemPress: jest.fn(),
    handleReservePress: jest.fn(),
  }),
  getMyReservationContentState: () => ({
    items: [{ id: 'r1', title: '未来事务所' }],
    emptyData: null,
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateBack: jest.fn(),
}));

import MyReservationPage from '../../src/page/ScrollBox/MyReservationPage/MyReservationPage';

describe('MyReservationPage smoke', () => {
  it('renders reservation shell without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<MyReservationPage />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['MyReservationTopBar', 'MyReservationSubTabs', 'MyReservationGrid']),
    );
  });
});
