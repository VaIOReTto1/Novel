import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/BookshelfPage/styles/MainPageStyles', () => ({
  createMainPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelBackground: '#fff',
  }),
}));

jest.mock('../../src/page/BookshelfPage/store/mainStore', () => ({
  useMainBookshelfStore: () => ({
    currentTab: 'bookshelf',
    isLoading: false,
    error: null,
    setCurrentTab: jest.fn(),
  }),
  getMainTabs: () => [
    { id: 'bookshelf', name: '书架' },
    { id: 'history', name: '历史' },
    { id: 'watchlist', name: '追剧' },
    { id: 'community', name: '圈子' },
  ],
}));

jest.mock('../../src/page/BookshelfPage/pages/History/HistoryPage', () => () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');
  return ReactMock.createElement(MockText, null, 'HistoryPage');
});

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/BookshelfPage', () => ({
  BookshelfPage: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'BookshelfPage');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Watchlist/WatchlistPage', () => ({
  WatchlistPage: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'WatchlistPage');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/CommunityPage', () => () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');
  return ReactMock.createElement(MockText, null, 'CommunityPage');
});

import { MainPage } from '../../src/page/BookshelfPage/BookshelfPage';

describe('BookshelfPage smoke', () => {
  it('renders the four bookshelf shell tabs without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<MainPage />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['书架', '历史', '追剧', '圈子', 'BookshelfPage']),
    );
  });
});
