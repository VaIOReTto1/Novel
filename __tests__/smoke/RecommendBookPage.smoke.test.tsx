import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/RecommendBookPage/styles/RecommendBookPageStyles', () => ({
  createRecommendBookPageStyles: () =>
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

jest.mock('../../src/page/ScrollBox/RecommendBookPage/components', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return {
    TopBar: () => ReactMock.createElement(MockText, null, 'RecommendBookTopBar'),
    UserSection: () => ReactMock.createElement(MockText, null, 'RecommendBookUserSection'),
    DataStatsSection: () => ReactMock.createElement(MockText, null, 'RecommendBookDataStats'),
    CreativeServiceSection: () => ReactMock.createElement(MockText, null, 'RecommendBookServices'),
    CreativeTaskSection: () => ReactMock.createElement(MockText, null, 'RecommendBookTasks'),
  };
});

jest.mock('../../src/page/ScrollBox/RecommendBookPage/store/recommendBookStore', () => ({
  useRecommendBookStore: () => ({
    loading: false,
    error: null,
    userInfo: { name: '测试作者' },
    dataStats: {},
    services: [],
    selectedTaskTab: 'recommend',
    tasks: { recommend: [], book: [] },
    loadInitialData: jest.fn(),
    setSelectedTaskTab: jest.fn(),
    handleViewAllTasks: jest.fn(),
    handleServicePress: jest.fn(),
    handleTaskPress: jest.fn(),
  }),
}));

jest.mock('../../src/page/ScrollBox/RecommendBookPage/domain/recommendBookPageModel', () => ({
  bootstrapRecommendBookPage: jest.fn(),
  createRecommendBookPageHandlers: () => ({
    handleBackPress: jest.fn(),
    handleWithdrawPress: jest.fn(),
    handleServiceItemPress: jest.fn(),
    handleTaskTabChange: jest.fn(),
    handleTaskItemPress: jest.fn(),
    handleViewAllTasksPress: jest.fn(),
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateBack: jest.fn(),
}));

import RecommendBookPage from '../../src/page/ScrollBox/RecommendBookPage/RecommendBookPage';

describe('RecommendBookPage smoke', () => {
  it('renders core recommend-book sections without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<RecommendBookPage />);
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
        'RecommendBookTopBar',
        'RecommendBookUserSection',
        'RecommendBookDataStats',
        'RecommendBookServices',
        'RecommendBookTasks',
      ]),
    );
  });
});
