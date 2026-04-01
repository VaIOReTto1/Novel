import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { ActivityIndicator, RefreshControl } from 'react-native';

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
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/styles/BookshelfPageStyles', () => ({
  createBookshelfPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/GridView', () => ({
  GridView: () => null,
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/ListView', () => ({
  ListView: () => null,
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/WaterfallGrid', () => ({
  WaterfallGrid: () => null,
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/RecommendationFlow', () => ({
  RecommendationFlow: () => null,
}));

import { TopBar } from '../../src/page/BookshelfPage/pages/Bookshelf/components/TopBar';
import { ViewSwitcher } from '../../src/page/BookshelfPage/pages/Bookshelf/components/ViewSwitcher';
import { LoadMoreIndicator } from '../../src/page/BookshelfPage/pages/Bookshelf/components/LoadMoreIndicator';
import { EmptyState } from '../../src/page/BookshelfPage/pages/Bookshelf/components/EmptyState';
import { UnifiedScrollView } from '../../src/page/BookshelfPage/pages/Bookshelf/components/UnifiedScrollView';

describe('Bookshelf components novelDesign icons and loaders', () => {
  beforeEach(() => {
    mockMaterialIcons.mockClear();
  });

  it('uses vector icons for view switchers instead of text glyphs', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <>
          <TopBar currentView="grid" onViewChange={jest.fn()} onFilterPress={jest.fn()} onEditPress={jest.fn()} />
          <ViewSwitcher currentView="grid" onViewChange={jest.fn()} />
        </>,
      );
    });

    const names = mockMaterialIcons.mock.calls.map(([props]) => props.name);
    expect(names).toEqual(
      expect.arrayContaining(['grid-view', 'view-list', 'view-quilt']),
    );
  });

  it('uses brand loading color for infinite-load feedback', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<LoadMoreIndicator isLoading />);
    });

    const indicator = renderer.root.findByType(ActivityIndicator);
    expect(indicator.props.color).toBe('#C96A34');
  });

  it('uses vector icons for bookshelf and recommendation empty states', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <>
          <EmptyState type="bookshelf" />
          <EmptyState type="recommendations" />
        </>,
      );
    });

    const names = mockMaterialIcons.mock.calls.map(([props]) => props.name);
    expect(names).toEqual(
      expect.arrayContaining(['menu-book', 'auto-awesome']),
    );
  });

  it('uses brand refresh colors for the unified bookshelf scroll shell', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <UnifiedScrollView
          bookshelfData={[]}
          onBookPress={jest.fn()}
          onBookLongPress={jest.fn()}
          onLoadMore={jest.fn()}
          hasMore={false}
          isLoading={false}
          recommendations={[]}
          onRecommendationPress={jest.fn()}
          onLoadMoreRecommendations={jest.fn()}
          hasMoreRecommendations={false}
          isRecommendationLoading={false}
          currentView="grid"
          isEditMode={false}
          selectedItems={[]}
          isRefreshing={false}
          onRefresh={jest.fn()}
        />,
      );
    });

    const refreshControl = renderer.root.findByType(RefreshControl);
    expect(refreshControl.props.colors).toEqual(['#C96A34']);
    expect(refreshControl.props.tintColor).toBe('#C96A34');
  });
});
