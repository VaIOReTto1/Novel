import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/MessagePage/styles/MessagePageStyles', () => ({
  createMessagePageStyles: () =>
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

jest.mock('../../src/page/ScrollBox/MessagePage/components', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return {
    TopBar: () => ReactMock.createElement(MockText, null, 'MessageTopBar'),
    MessageItem: () => ReactMock.createElement(MockText, null, 'MessageItem'),
    TabsArea: () => ReactMock.createElement(MockText, null, 'MessageTabs'),
    EmptyState: () => ReactMock.createElement(MockText, null, 'MessageEmptyState'),
    RefreshIndicator: () => ReactMock.createElement(MockText, null, 'MessageRefresh'),
    LoadMoreIndicator: () => ReactMock.createElement(MockText, null, 'MessageLoadMore'),
    MainMessagesSection: () => ReactMock.createElement(MockText, null, 'MessageMainSection'),
  };
});

jest.mock('../../src/page/ScrollBox/MessagePage/store/messageStore', () => ({
  useMessageStore: () => ({
    messages: [],
    loading: false,
    isRefreshing: false,
    hasMore: false,
    loadMessages: jest.fn(),
    refreshMessages: jest.fn(),
    loadMoreMessages: jest.fn(),
    markMessageAsRead: jest.fn(),
    markAllAsRead: jest.fn(),
    setMessages: jest.fn(),
  }),
}));

jest.mock('../../src/page/ScrollBox/MessagePage/hooks/useRefreshLogic', () => ({
  useRefreshLogic: () => ({
    isPullingDown: false,
    pullDistance: 0,
    PULL_THRESHOLD: 80,
    handleScroll: jest.fn(),
  }),
}));

jest.mock('../../src/page/ScrollBox/MessagePage/hooks/useMessageAnimations', () => ({
  useMessageAnimations: () => ({
    spinStyle: {},
  }),
}));

jest.mock('../../src/page/ScrollBox/MessagePage/domain/messagePageModel', () => ({
  bootstrapMessagePage: jest.fn(),
  createMessagePageHandlers: () => ({
    handleMarkAllReadPress: jest.fn(),
    handleMessagePress: jest.fn(),
    handleTabPress: jest.fn(),
  }),
  getSecondaryEmptyMessage: () => '暂无消息',
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateBack: jest.fn(),
}));

import MessagePage from '../../src/page/ScrollBox/MessagePage/MessagePage';

describe('MessagePage smoke', () => {
  it('renders message shell without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<MessagePage />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining([
        'MessageTopBar',
        'MessageRefresh',
        'MessageMainSection',
        'MessageTabs',
        'MessageEmptyState',
      ]),
    );
  });
});
