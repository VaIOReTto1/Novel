import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { RefreshControl, Text } from 'react-native';

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelChipBackground: '#EBEDF0',
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

jest.mock('../../src/page/BookshelfPage/pages/Community/components/PostItem', () => ({
  PostItem: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'PostItem');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/components/LoadingIndicator', () => ({
  LoadingIndicator: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'LoadingIndicator');
  },
}));

import { PostList } from '../../src/page/BookshelfPage/pages/Community/components/PostList';

describe('Community PostList novelDesign behavior', () => {
  const baseProps = {
    posts: [] as any[],
    loading: false,
    refreshing: false,
    hasMore: false,
    onRefresh: jest.fn(),
    onLoadMore: jest.fn(),
    onLike: jest.fn(),
    onComment: jest.fn(),
    onShare: jest.fn(),
    onMore: jest.fn(),
    onUserPress: jest.fn(),
    onSubscribe: jest.fn(),
    circles: [],
    selectedCircle: 'all',
    onCircleChange: jest.fn(),
  };

  it('uses brand refresh colors and renders empty-state branch', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<PostList {...baseProps} />);
    });

    const refreshControl = renderer.root.findByType(RefreshControl);
    expect(refreshControl.props.colors).toEqual(['#C96A34']);
    expect(refreshControl.props.tintColor).toBe('#C96A34');

    const texts = renderer.root.findAllByType(Text).map((node) => node.props.children).flat();
    expect(texts).toEqual(expect.arrayContaining(['最热评论', '暂无帖子', '发布帖子']));
  });

  it('renders loading branch instead of empty state while fetching', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<PostList {...baseProps} loading />);
    });

    const texts = renderer.root.findAllByType(Text).map((node) => node.props.children).flat();
    expect(texts).toContain('LoadingIndicator');
    expect(texts).not.toContain('暂无帖子');
  });
});
