import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { RefreshControl } from 'react-native';

const mockMaterialIcons = jest.fn(() => null);

jest.mock('react-native-vector-icons/MaterialIcons', () => (props: any) => {
  mockMaterialIcons(props);
  return null;
});

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
  PostItem: () => null,
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/components/LoadingIndicator', () => ({
  LoadingIndicator: () => null,
}));

import { TabBar } from '../../src/page/BookshelfPage/pages/Community/components/TabBar';
import { EmptyState } from '../../src/page/BookshelfPage/pages/Community/components/EmptyState';
import { PostList } from '../../src/page/BookshelfPage/pages/Community/components/PostList';

describe('Community feed components novelDesign', () => {
  beforeEach(() => {
    mockMaterialIcons.mockClear();
  });

  it('uses vector icons for the all-circle entry and empty-state illustration', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <>
          <TabBar circles={[]} selectedCircle="all" onCircleChange={jest.fn()} />
          <EmptyState title="空" description="描述" />
        </>,
      );
    });

    const names = mockMaterialIcons.mock.calls.map(([props]) => props.name);
    expect(names).toEqual(
      expect.arrayContaining(['menu-book', 'forum']),
    );
  });

  it('uses brand refresh colors for the post feed list shell', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <PostList
          posts={[]}
          loading={false}
          refreshing={false}
          hasMore={false}
          onRefresh={jest.fn()}
          onLoadMore={jest.fn()}
          onLike={jest.fn()}
          onComment={jest.fn()}
          onShare={jest.fn()}
          onMore={jest.fn()}
          onUserPress={jest.fn()}
          onSubscribe={jest.fn()}
          circles={[]}
          selectedCircle="all"
          onCircleChange={jest.fn()}
        />,
      );
    });

    const refreshControl = renderer.root.findByType(RefreshControl);
    expect(refreshControl.props.colors).toEqual(['#C96A34']);
    expect(refreshControl.props.tintColor).toBe('#C96A34');
  });
});
