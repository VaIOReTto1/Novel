import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/ViewedUsersPage/styles/ViewedUsersPageStyles', () => ({
  createViewedUsersPageStyles: () =>
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

jest.mock('../../src/page/ScrollBox/ViewedUsersPage/components', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return {
    TopBar: () => ReactMock.createElement(MockText, null, 'ViewedUsersTopBar'),
    TabsSection: () => ReactMock.createElement(MockText, null, 'ViewedUsersTabs'),
    EmptyState: () => ReactMock.createElement(MockText, null, 'ViewedUsersEmpty'),
    UsersList: () => ReactMock.createElement(MockText, null, 'ViewedUsersList'),
  };
});

jest.mock('../../src/page/ScrollBox/ViewedUsersPage/store/viewedUsersStore', () => ({
  useViewedUsersStore: () => ({
    loading: false,
    error: null,
    userInfo: { name: '测试作者' },
    selectedTab: 'recommend',
    viewedUsers: [],
    recommendUsers: [{ id: 'u1', name: '番茄小说团队' }],
    followingUsers: [],
    fansUsers: [],
    emptyStates: {
      viewed: {},
      recommend: {},
      following: {},
      fans: {},
    },
    loadInitialData: jest.fn(),
    setSelectedTab: jest.fn(),
    handleFollowUser: jest.fn(),
    handleViewMoreRecommend: jest.fn(),
    handleUserPress: jest.fn(),
  }),
}));

jest.mock('../../src/page/ScrollBox/ViewedUsersPage/domain/viewedUsersPageModel', () => ({
  bootstrapViewedUsersPage: jest.fn(),
  createViewedUsersPageHandlers: () => ({
    handleBackPress: jest.fn(),
    handleTabChange: jest.fn(),
    handleFollowPress: jest.fn(),
    handleViewMorePress: jest.fn(),
    handleUserItemPress: jest.fn(),
  }),
  getViewedUsersCurrentUsers: () => [{ id: 'u1', name: '番茄小说团队' }],
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateBack: jest.fn(),
}));

import ViewedUsersPage from '../../src/page/ScrollBox/ViewedUsersPage/ViewedUsersPage';

describe('ViewedUsersPage smoke', () => {
  it('renders visited-users shell without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<ViewedUsersPage />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['ViewedUsersTopBar', 'ViewedUsersTabs', 'ViewedUsersList']),
    );
  });
});
