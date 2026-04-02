import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelChipBackground: '#EBEDF0',
    novelThemeMode: 'light',
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/styles/CommunityPageStyles', () => ({
  createCommunityPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/hooks/useCommunity', () => ({
  useCommunity: () => ({
    posts: [],
    circles: [],
    selectedCircle: 'all',
    loading: false,
    refreshing: false,
    hasMore: false,
    handleCircleChange: jest.fn(),
    handleRefresh: jest.fn(),
    handleLoadMore: jest.fn(),
    handleLike: jest.fn(),
    handleComment: jest.fn(),
    handleShare: jest.fn(),
    handleMore: jest.fn(),
    handleUserPress: jest.fn(),
    handleSubscribe: jest.fn(),
    handlePublish: jest.fn(),
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/components/PostList', () => ({
  PostList: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'CommunityPostList');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/components/FloatingButton', () => ({
  FloatingButton: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'CommunityFloatingButton');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/components/TopBar', () => ({
  TopBar: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'CommunityTopBar');
  },
}));

import CommunityPage from '../../src/page/BookshelfPage/pages/Community/CommunityPage';

describe('CommunityPage novelDesign layout', () => {
  it('renders page-level top chrome ahead of the feed shell', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<CommunityPage />);
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
        'CommunityTopBar',
        'CommunityPostList',
        'CommunityFloatingButton',
      ]),
    );
  });
});
